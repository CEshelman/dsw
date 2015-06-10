package com.ibm.dsw.quote.draftquote.action.validator;

import java.sql.Date;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.action.PostPartPriceTabAction;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code></code> class is
 *
 *PartPriceUIValidator
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Jun 25, 2007
 */

public abstract class PartPriceUIValidator {
	PostPartPriceTabAction action;

    LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;

    protected NumberFormat numberFormatter = NumberFormat.getInstance();;

    public static PartPriceUIValidator create(PostPartPriceTabAction ppAction,PostPartPriceTabContract ct ){
        if(ct.isRenwalQuote()){
            return new  RenewlQuoteUIValidator(ppAction);
        }
        else{
            return new SalesQuoteUIValidator(ppAction);
        }
    }
    public PartPriceUIValidator(PostPartPriceTabAction ppAction) {
        action = ppAction;
    }

    public boolean validate(ProcessContract contract,boolean checkOfferPrice) throws QuoteException {
        loadQuote((PostPartPriceTabContract) contract);
        return true;
    }

    protected String createKey(QuoteLineItem item) {
        return item.getPartNum().trim() + "_" + item.getSeqNum();
    }

    protected void loadQuote(PostPartPriceTabContract ct) throws QuoteException {

    	long start = System.currentTimeMillis();

        String creatorId = ct.getUserId();

        QuoteProcess process = QuoteProcessFactory.singleton().create();

        try {
            quote = process.getDraftQuoteBaseInfoWithTransaction(creatorId);
            quote.setPgsAppl(ct.isPGSEnv());
            QuoteHeader header = quote.getQuoteHeader();
            if (StringUtils.isNotBlank(header.getRselCustNum())) {
                logContext.debug(this, "To retrieve Reseller by number: " + header.getRselCustNum());
                Partner reseller = PartnerFactory.singleton().findPartnerByNum(header.getRselCustNum(), header.getLob().getCode());
                quote.setReseller(reseller);
            }

            getLineItems(quote);
            List masterLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList(), true);
            List SaaSLineItems = CommonServiceUtil.getSaaSLineItemList(quote.getLineItemList());
            List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quote.getQuoteHeader().getWebQuoteNum());
            List monthlyConfgrtnList = MonthlySoftwareConfigurationFactory.singleton().findMonthlySwConfiguration(quote.getQuoteHeader().getWebQuoteNum());
            
            quote.setMasterSoftwareLineItems(masterLineItems);
            quote.setSaaSLineItems(SaaSLineItems);
            quote.setPartsPricingConfigrtnsList(confgrtnList);
            quote.getMonthlySwQuoteDomain().fillMonthlySwLineItems(quote.getLineItemList());
            quote.getMonthlySwQuoteDomain().fillMonthlySwConfigurationForDraft(monthlyConfgrtnList);
           
            
            QuoteCommonUtil.buildSaaSLineItemsWithRampUp(quote.getSaaSLineItems());
    		quote.setMasterSaaSLineItems(CommonServiceUtil.getMasterSaaSLineItemList(quote.getSaaSLineItems()));
            quote.setPartsPricingConfigrtnsMap(QuoteCommonUtil.getPartsPricingConfigurations(quote.getMasterSaaSLineItems(), confgrtnList));
            
            quote.getQuoteHeader().setHasRampUpPartFlag(CommonServiceUtil.getHasRampUpPartFlag(quote.getMasterSaaSLineItems()));
            
            //Appliance
            List applncMains = CommonServiceUtil.getApplncMainPart(quote.getLineItemList());
            List applncUpgradeParts = CommonServiceUtil.getApplncUpgradePart(quote.getLineItemList());
            quote.setApplncMains(applncMains);
            quote.setApplncUpgradeParts(applncUpgradeParts);
            quote.setApplncOwnerShipParts(CommonServiceUtil.getApplncOwnershipPart(quote.getLineItemList()));
            
            //14.2 GD associate maintenance parts to license parts
            GDPartsUtil.checkLicAndMaitAssociation(quote);
            ct.setQuote(quote);

        } catch (NoDataException e) {
            logContext.error(this, "Get draft qutoe base info error: " + e.getMessage());
        } catch (TopazException e) {
            logContext.error(this, "Get draft qutoe base info error: " + e.getMessage());
        }

        logContext.debug(this, "PartPriceUIValidator.loadQuote time dump : "
        		                + (System.currentTimeMillis() - start));
    }

    private static boolean checkLineItemInputChange(LineItemParameter lip) {
        boolean lineItemChange = true;
        String ovrrdPrice = lip.overridePrice;
        String preOvrrdPrice = lip.prevOvrrdPrice;

        if (StringUtils.isBlank(ovrrdPrice)) ovrrdPrice = "";
        if (StringUtils.isBlank(preOvrrdPrice)) preOvrrdPrice = "";

        //user input value should not be formatted
        //ovrrdPrice = DecimalUtil.format(Double.valueOf(ovrrdPrice).doubleValue(), DecimalUtil.DEFAULT_SCALE);
        //preOvrrdPrice = DecimalUtil.format(Double.valueOf(preOvrrdPrice).doubleValue(),DecimalUtil.DEFAULT_SCALE);
        
        if (!checkLineItemYtyGrowthChange(lip) && !checkLineItemDiscountChange(lip) && ovrrdPrice.equals(preOvrrdPrice)) {
            lineItemChange = false;
        }

        return lineItemChange;
    }

    public static boolean checkLineItemDiscountChange(LineItemParameter lip) {
        String discount = lip.discountPercent;
        String preDiscount = lip.prevDiscount;

        if (StringUtils.isBlank(discount)) discount = "";
        if (StringUtils.isBlank(preDiscount)) preDiscount = "";

        //user input value should not be formatted
        //discount = DecimalUtil.formatTo5Number(Double.valueOf(discount).doubleValue());
        //preDiscount = DecimalUtil.formatTo5Number(Double.valueOf(preDiscount).doubleValue());

        return !discount.equals(preDiscount);
    }
    public static boolean checkLineItemYtyGrowthChange(LineItemParameter lip) {
    	if(lip.ytyRadio !=null && DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE.equals(lip.ytyRadio)){
    		String yty = lip.yty;
        	String preYty = lip.prevYty;
        	if (StringUtils.isBlank(yty)) yty = "";
        	if (StringUtils.isBlank(preYty)) preYty = "";
        	return !yty.equals(preYty);
    	}else{
    		return false;
    	}
    }
    public static boolean checkLineItemDiscountChange(LineItemParameter lip,String discount) {
    	String preDiscount = lip.prevDiscount;
    	if (StringUtils.isBlank(discount)) discount = "";
    	if (StringUtils.isBlank(preDiscount)) preDiscount = "";
    	
    	//user input value should not be formatted
    	//discount = DecimalUtil.formatTo5Number(Double.valueOf(discount).doubleValue());
    	//preDiscount = DecimalUtil.formatTo5Number(Double.valueOf(preDiscount).doubleValue());
    	double prevDisc = 0;
    	double disc = 0;
    	try{
    		prevDisc = Double.parseDouble(preDiscount);
    		disc = Double.parseDouble(discount);
    	}catch(Exception e){
    		return (DecimalUtil.isNotEqual(prevDisc, disc));
    	}
    	return (DecimalUtil.isNotEqual(prevDisc, disc));
    }

    protected boolean validatePartnerDiscount(String discountStr, boolean allowNull, String inputKey,
    		                                   HashMap vMap, ProcessContract ct){
        try {
        	//line item bp discount allow empty value
        	if(allowNull && StringUtils.isBlank(discountStr)){
        		return true;
        	}

			double discount = Double.parseDouble(discountStr);
			if (discount < 0) {
				FieldResult fieldResult = new FieldResult();
				fieldResult
						.setMsg(
								MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
								DraftQuoteMessageKeys.PARTNER_DISCOUNT_PERCENT_POSITIVE_MSG);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.PARTNER_DISC_PCT);
				vMap.put(inputKey, fieldResult);

				addToValidationDataMap(ct, vMap);

				return false;

			} else if (discount > 100) {
				FieldResult fieldResult = new FieldResult();
				fieldResult
						.setMsg(
								MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
								DraftQuoteMessageKeys.PARTNER_DISCOUNT_PERCENT_RANGE_MSG);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.PARTNER_DISC_PCT);
				vMap.put(inputKey, fieldResult);

				addToValidationDataMap(ct, vMap);

				return false;
			}
		} catch (Exception e) {
			FieldResult fieldResult = new FieldResult();
			fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
					DraftQuoteMessageKeys.PARTNER_DISCOUNT_PERCENT_MSG);
			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
					PartPriceViewKeys.PARTNER_DISC_PCT);
			vMap.put(inputKey, fieldResult);

			addToValidationDataMap(ct, vMap);

			return false;
		}

		return true;
	}

    public boolean validateLineItemPartnerDiscount(LineItemParameter lip, HashMap vMap, ProcessContract ct){
    	String inputKey = PartPriceViewKeys.PREFIX + lip.key + DraftQuoteParamKeys.bpOverrideDisSuffix;

    	return validatePartnerDiscount(lip.bpOverrideDiscount, true, inputKey, vMap, ct);
    }

    private void checkOfferPriceIncludeFlag(PostPartPriceTabContract ct,HashMap lineItems) {
        List lineItemsList;

        //read db to get master/subsidiary relationship
        boolean ytyEnteredForGD = false;
        lineItemsList = ct.getQuote().getLineItemList();
        for (int i=0;i<lineItemsList.size();i++) {
            QuoteLineItem qli = (QuoteLineItem)lineItemsList.get(i);
            LineItemParameter lip = (LineItemParameter)lineItems.get(createKey(qli));
            if(lip == null){
            	continue;
            }
            // 14.2 GD, check if yty is manually entered
            ytyEnteredForGD = ytyEnteredForGD || (GDPartsUtil.isEligibleRenewalPart(qli) && !StringUtils.isBlank(lip.ytyRadio) && GrowthDelegationUtil.isManualEnterYTY(lip.ytyRadio));
            
            if(!QuoteCommonUtil.shouldIncludeInOfferPrice(ct.getQuote(), qli)){
            	lip.isOfferIncldFlag = false;
            	continue;
            }
            boolean excludeFlag = checkLineItemInputChange(lip);
            if (excludeFlag) {
                lip.isOfferIncldFlag = false;
            }
            if(qli.getAddtnlMaintCvrageQty() > 0){
	            //if master part is excluded,all subsidiary parts will be excluded
	            List subLineItems = qli.getAddtnlYearCvrageLineItems();

	            for (int j=0;j<subLineItems.size();j++) {
	                QuoteLineItem addiQli = (QuoteLineItem)subLineItems.get(j);
	                LineItemParameter addiLip = (LineItemParameter)lineItems.get(createKey(addiQli));
	                if(addiLip == null){
	                	continue;
	                }
	                if (!excludeFlag) {
	                    excludeFlag = checkLineItemInputChange(addiLip);
	                }

	                if (excludeFlag){
	                    addiLip.isOfferIncldFlag = false;
	                }

	                logContext.debug(this, "addiLip.key... " + addiLip.key + " exclude? " + !addiLip.isOfferIncldFlag);
	            }
            }
        }
        
        // 14.2 GD, offer price should be cleared if there is any yty entered
        if(ytyEnteredForGD){
        	for (int i=0;i<lineItemsList.size();i++) {
        		QuoteLineItem qli = (QuoteLineItem)lineItemsList.get(i);
        		LineItemParameter lip = (LineItemParameter)lineItems.get(createKey(qli));
        		if(lip == null){
        			continue;
        		}
            	lip.isOfferIncldFlag = false;

            	if(qli.getAddtnlMaintCvrageQty() > 0){
            		//if master part is excluded,all subsidiary parts will be excluded
            		List subLineItems = qli.getAddtnlYearCvrageLineItems();

            		for (int j=0;j<subLineItems.size();j++) {
            			QuoteLineItem addiQli = (QuoteLineItem)subLineItems.get(j);
            			LineItemParameter addiLip = (LineItemParameter)lineItems.get(createKey(addiQli));
            			if(addiLip == null){
            				continue;
            			}
	                    addiLip.isOfferIncldFlag = false;
	                    logContext.debug(this, "addiLip.key... " + addiLip.key + " exclude? " + !addiLip.isOfferIncldFlag);
            		}
            	}
        	}
        }
    }

    protected boolean checkOfferPrice(HashMap vMap,PostPartPriceTabContract ct) {
        //If the quote has an offer, and all discount percent or override unit price
        //has been manually set by the user on every line item, return false
        if (ct.getPrevOfferPrice() != null) {
            HashMap lineItems = ct.getItems();

            if (lineItems != null) {
                checkOfferPriceIncludeFlag(ct,lineItems);

                Iterator it = lineItems.values().iterator();
                boolean isAllLineItemExcluded = true;

                while (it.hasNext()) {
                    LineItemParameter lip = (LineItemParameter)it.next();
                    isAllLineItemExcluded = isAllLineItemExcluded && !lip.isOfferIncldFlag;
                }

                if (isAllLineItemExcluded) {
                    FieldResult fieldResult = new FieldResult();
                    fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.CLEAR_CURRENT_OFFER_MSG);
                    fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.CLEAR_OFFER_TXT);
                    vMap.put(DraftQuoteParamKeys.CLEAR_OFFER_BUTTON, fieldResult);

                    addToValidationDataMap(ct, vMap);

                    return false;
                }
            }
        }

        return true;
    }
    
    protected boolean validateDiscount(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract ct) {
        try {
            if (!StringUtils.isBlank(lineItem.discountPercent)) {
                double tempDiscountPercent = Double.parseDouble(lineItem.discountPercent);
                logContext.debug(this, "validateDiscount... " + lineItem.key + " line item exclude flg =" + !lineItem.isOfferIncldFlag + " discount percent =" + lineItem.discountPercent);

                //if discount is not calculated(by offer price or by override unit price or by yty)
                //then validate manually entered discount percent
                if ((StringUtils.isBlank(ct.getPrevOfferPrice()) || !lineItem.isOfferIncldFlag)
                        && StringUtils.isBlank(lineItem.overridePrice)
                        && !GrowthDelegationUtil.isManualEnterYTY(lineItem.ytyRadio)) {
                    if (tempDiscountPercent < 0) {
                            FieldResult fieldResult = new FieldResult();
                            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.DISCOUNT_PERCENT_POSITIVE_MSG);
                            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.DIS_PER_HDR);
                            vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.discountPriceSuffix, fieldResult);

                            addToValidationDataMap(ct, vMap);

                            return false;
                    }
                    else if (tempDiscountPercent > 100) {
                        FieldResult fieldResult = new FieldResult();
                        fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.DISCOUNT_PERCENT_RANGE_MSG);
                        fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.DIS_PER_HDR);
                        vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.discountPriceSuffix, fieldResult);

                        addToValidationDataMap(ct, vMap);

                        return false;
                    }
                }
            }
        }
        catch (Exception e) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.DISCOUNT_PERCENT_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.DIS_PER_HDR);
            vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.discountPriceSuffix, fieldResult);

            addToValidationDataMap(ct, vMap);

            return false;
        }

        return true;
    }

    protected boolean validateYty(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract ct) {
        try {
        	if(!StringUtils.isBlank(lineItem.ytyRadio) && GrowthDelegationUtil.isManualEnterYTY(lineItem.ytyRadio)){
        		 if (StringUtils.isBlank(lineItem.yty)) {
        			 FieldResult fieldResult = new FieldResult();
                     fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.YTY_MISSING_MSG);
                     fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.YTY_GROWTH_HDR);
                     vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.YTY_GROWTH_SUFFIX, fieldResult);
                     addToValidationDataMap(ct, vMap);
                     return false; 
                }
        	}
           
        }
        catch (Exception e) {
        	 FieldResult fieldResult = new FieldResult();
             fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.YTY_MISSING_MSG);
             fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.YTY_GROWTH_HDR);
             vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.YTY_GROWTH_SUFFIX, fieldResult);
             addToValidationDataMap(ct, vMap);
             return false;
        }
        return true;
    }
    
    protected boolean validateOverridePrice(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract ct) {
        //make sure if the override is not null, it should be  a positive number or zero
        try {
            logContext.debug(this, "Override price =" + lineItem.overridePrice);
            if (lineItem.overridePrice != null && (!"".equals(lineItem.overridePrice))
                    && lineItem.overridePrice.trim().length() != 0) {
                double tempOverridePrice = numberFormatter.parse(lineItem.overridePrice).doubleValue();

                QuoteLineItem qli = findLineFromQuote(lineItem.key);

                boolean valid = true;

                double minValue = 0.01;
                int scale = 2;
                if(qli != null && (qli.isSaasPart() || qli.isMonthlySoftwarePart())){
                	scale = QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), qli);
                	minValue = 1 / Math.pow(10, scale);
                	if (tempOverridePrice <minValue) {
                        valid = false;
                    }
                } else {
                    if (tempOverridePrice < minValue) {
                    	valid = false;
                    }
                }

                if(!valid){
                    FieldResult fieldResult = new FieldResult();
                    fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.OVERRIDE_PRICE_POSITIVE_MSG);
                    fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.OVERRIDE_PRICE_HDR);
                    fieldResult.addArg(DecimalUtil.format(minValue, scale), false);
                    vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.overridePriceSuffix, fieldResult);
                    addToValidationDataMap(ct, vMap);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.OVERRIDE_PRICE_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.OVERRIDE_PRICE_HDR);
            vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.overridePriceSuffix, fieldResult);
            addToValidationDataMap(ct, vMap);
            return false;
        }
    }

    protected boolean validateEOLPartInfo(HashMap vMap, PostPartPriceTabContract ct) {
    	List masterItems = quote.getMasterSoftwareLineItems();
    	String key = "";
    	QuoteLineItem masterItem = null;
    	LineItemParameter lineItemParam = null;

    	if(masterItems == null || masterItems.size() == 0){
    		return true;
    	}
    	for(Iterator masterIt = masterItems.iterator(); masterIt.hasNext(); ){
    		masterItem = (QuoteLineItem)masterIt.next();
    		if(!masterItem.isObsoletePart()){
    			continue;
    		}

    		key = masterItem.getPartNum() + "_" + masterItem.getSeqNum();
    		lineItemParam = (LineItemParameter)ct.getItems().get(key);

    		if(QuoteCommonUtil.isSkipLineItemUIValidate(lineItemParam, ct)){
            	continue;
            }
    		//if part is part is not re-activable or quote is PPSS, user should delete below line item from quote
    		if(!masterItem.canPartBeReactivated() || quote.getQuoteHeader().isPPSSQuote()){
    			addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    					              DraftQuoteMessageKeys.REMOVE_EOL_PART,
    					              PartPriceViewKeys.QTY_HDR,
    					              PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.quantitySuffix,
									  vMap, ct);
                return false;
            }

    		String ovrdEntitledPrice = StringUtils.trim(lineItemParam.overrideEntitledPrice);
    		logContext.debug(this, key + ": Override entitled price = " + ovrdEntitledPrice);
    		//if no history price is available and user has not entered entitled price
    		//then entitled price is required
    		if(!masterItem.isHasEolPrice()){
    			if(StringUtils.isEmpty(ovrdEntitledPrice)){
    				addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    						DraftQuoteMessageKeys.ENTITLED_PRICE_REQUIRED,
    						PartPriceViewKeys.OVERRIDE_ENTITLED_UNIT_PRICE,
    						PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.overrideEntitledPriceSuffix,
    						vMap, ct);
                    return false;
    			}

            	try {
                    double tmp = numberFormatter.parse(ovrdEntitledPrice).doubleValue();
    				if (tmp < 0) {
    					addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    											DraftQuoteMessageKeys.ENTITLED_PRICE_POSITIVE_MSG,
    											PartPriceViewKeys.OVERRIDE_ENTITLED_UNIT_PRICE,
    											PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.overrideEntitledPriceSuffix,
    											vMap, ct);
    					return false;
    				}
                } catch (Exception e) {
    				addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    						DraftQuoteMessageKeys.ENTITLED_PRICE_MSG,
    						PartPriceViewKeys.OVERRIDE_ENTITLED_UNIT_PRICE,
    						PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.overrideEntitledPriceSuffix,
    						vMap, ct);
                    return false;
                }
    		}

    		if(!checkEOLPartOUP(lineItemParam, vMap, ct)){
    			return false;
    		}

    		//check additional years of coverage
    		//since we have Z8 status at master line item level, no need to check that again for sub item
    		List subItems = masterItem.getAddtnlYearCvrageLineItems();
    		if(subItems != null && subItems.size() > 0){
    			for(Iterator subIt = subItems.iterator(); subIt.hasNext(); ){
    				QuoteLineItem subItem = (QuoteLineItem)subIt.next();
    	    		String subKey = subItem.getPartNum() + "_" + subItem.getSeqNum();
    	    		LineItemParameter subLineItemParam = (LineItemParameter)ct.getItems().get(subKey);
    	    		String subOvrdUnitPrice = StringUtils.trim(subLineItemParam.overridePrice);

    	    		if(subLineItemParam == null){
    	    			continue;
    	    		}

    	    		if(!checkEOLPartOUP(subLineItemParam, vMap, ct)){
    	    			return false;
    	    		}
    			}
    		}
    	}

    	return true;
    }

    protected boolean checkEOLPartOUP(LineItemParameter lineItemParam, HashMap vMap, PostPartPriceTabContract ct){
		String ovrdUnitPrice = StringUtils.trim(lineItemParam.overridePrice);
		logContext.debug(this, lineItemParam.key + ": override unit price is " + ovrdUnitPrice);

		if(StringUtils.isEmpty(ovrdUnitPrice)){
			//up to here, ovrdUnitPrice is already validated to be empty or a positive numeric
			//override unit price is required for eol part
			addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
					DraftQuoteMessageKeys.OVERRIDE_PRICE_REQUIRED,
					PartPriceViewKeys.OVERRIDE_PRICE_HDR,
					PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.overridePriceSuffix,
					vMap, ct);
            return false;
		}

		return true;
    }

    private void addValidationErrorMsg(String resource, String msg, String arg, String key,HashMap vMap, PostPartPriceTabContract ct){
        FieldResult fieldResult = new FieldResult();
        fieldResult.setMsg(resource,msg);
        fieldResult.addArg(resource, arg);
        vMap.put(key, fieldResult);
        addToValidationDataMap(ct, vMap);
    }

    protected boolean validateQuantity(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract ct) {
        //validate quantity to be integer
        try {
            logContext.debug(this, "Quantity =" + lineItem.quantity);

            if (lineItem.quantity != null && lineItem.quantity.trim().length() != 0) {
                if (!lineItem.quantity.equals("")) {
                    Integer.parseInt(lineItem.quantity);
                }
            }

        } catch (Exception e) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUANTITY_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.QTY_HDR);
            vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.quantitySuffix, fieldResult);

            addToValidationDataMap(ct, vMap);
            return false;
        }

        QuoteLineItem qli = findLineFromQuote(lineItem.key);
		if (qli != null && qli.isDisplayModelAndSerialNum()) {
			 if(!StringUtils.isBlank(lineItem.quantity) && !"1".equals(lineItem.quantity.trim())) {
				   addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.LEGACY_PART_QUANTITY,
							PartPriceViewKeys.QTY_HDR, PartPriceViewKeys.PREFIX + lineItem.key + DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER, vMap, ct);
					return false;
			   }
		}
		return true;
    }

    private void getLineItems(Quote quote) throws TopazException {
        try {

            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    quote.getQuoteHeader().getWebQuoteNum());

            quote.setLineItemList(lineItemList);

        } catch (TopazException te) {
            throw te;
        } catch (Exception ex) {
            throw new TopazException("An unexpected error occurred while getting line items. Cause: " + ex);
        }
    }

    protected boolean checkBackDating(HashMap vMap, PostPartPriceTabContract contract){
    	if (quote == null){
    		return false;
    	}
    	List masterLineItems = quote.getMasterSoftwareLineItems();
    	String key = "";
    	QuoteLineItem lineItem = null;
    	LineItemParameter lineItemParam = null;
    	Date date = null;

    	boolean cmprssCvrageEnabled = contract.isEnableCmprssCvrage() || quote.getQuoteHeader().getCmprssCvrageFlag();
    	for(Iterator it = masterLineItems.iterator(); it.hasNext(); ){
    		lineItem = (QuoteLineItem)it.next();
    		if(QuoteCommonUtil.isSkipLineItemDateValidate(lineItem)){
                continue;
            }
    		key = lineItem.getPartNum() + "_" + lineItem.getSeqNum();

    		lineItemParam = (LineItemParameter)contract.getItems().get(key);

    		if(QuoteCommonUtil.isSkipLineItemUIValidate(lineItemParam, contract)){
            	continue;
            }

    		date = lineItemParam.maintStartDate;
    		if(date == null){
    			continue;
    		}

    		//Skip back dating check for cmprss cvrage items
    		if(cmprssCvrageEnabled && lineItem.hasValidCmprssCvrageMonth() ){
    		    continue;
    		}

    		if(DateUtil.isDateBeforeToday(date) && !lineItem.getPartDispAttr().isFromRQ()){
    			if(contract.getReasonCodes() == null || contract.getReasonCodes().size() == 0){
   	       			 FieldResult fieldResult = new FieldResult();
   	       	         fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.MSG_BACK_DTG_NO_REASON);
   	       	         fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.BACK_DATE_MSG_HDR);
   	       	         vMap.put(DraftQuoteParamKeys.BACK_DATE_TABLE_TBODY, fieldResult);

   	       	         addToValidationDataMap(contract, vMap);

   	   				 return false;
       			} else if(contract.getReasonCodes().contains(QuoteConstants.BACK_DATING_REASON_OTHER)
       					    && StringUtils.isBlank(contract.getBackDatingComment())){
  	       			 FieldResult fieldResult = new FieldResult();
   	       	         fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.MSG_BACK_DTG_COMMENT_REQUIRED);
   	       	         fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.BACK_DATE_MSG_HDR);
   	       	         vMap.put(DraftQuoteParamKeys.BACK_DATEING_COMMENT, fieldResult);

   	       	         addToValidationDataMap(contract, vMap);

   	   				 return false;
       			}

                if(StringUtils.isNotBlank(contract.getBackDatingComment()) && contract.getBackDatingComment().length() > 700){
	       			 FieldResult fieldResult = new FieldResult();
	       	         fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.MSG_BACK_DTG_COMMENT_EXCEED_LIMIT);
	       	         fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.BACK_DATE_MSG_HDR);
	       	         vMap.put(DraftQuoteParamKeys.BACK_DATEING_COMMENT, fieldResult);

	       	         addToValidationDataMap(contract, vMap);

	   				 return false;
  			    }
    		}
    	}

    	return true;
    }

    private QuoteLineItem findLineFromQuote(String key){
    	int index = key.indexOf("_");

    	if (index < 0) return null;

    	int iSeqNum ;

    	 try {
    		 iSeqNum = Integer.parseInt(key.substring(index + 1));
		} catch (NumberFormatException e) {
			return null;
		}

    	return quote.getLineItem(key.substring(0, index), iSeqNum);
    }

    /**
     * @param contract
     * @param map
     */
    protected void addToValidationDataMap(ProcessContract contract, HashMap map) {
        action.addToValidationDataMap(contract, map);

    }

    /**
     * @return Returns the quote.
     */
    public Quote getQuote() {
        return quote;
    }
    
    

	protected boolean validationApplncId(LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct) {
		logContext.debug(this, "applianceId =" + lineItemParam.applianceId);
		String applianceId = lineItemParam.applianceId;
		if (StringUtils.isNotEmpty(applianceId)) {
			if (PartPriceConstants.APPLNC_SELECT_DEFAULT.equals(applianceId.trim())) {
				addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.APPLANCE_ID_MSG,
						PartPriceViewKeys.APPLIANCE_ID_HDR,
						PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.APPLNC_ID, vMap, ct);

				return false;
			}
		}
		return true;
	}

	protected boolean validationMachType(LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct) {
		logContext.debug(this, "machineType =" + lineItemParam.machineType);
		return validateMTM(lineItemParam.machineType,4,
				DraftQuoteMessageKeys.MACHINE_TYPE_ALPHANUMERIC_MSG,
				DraftQuoteMessageKeys.MACHINE_TYPE_EXCEED_LIMIT,
				PartPriceViewKeys.MACHINE_TYPE_HDR,
				DraftQuoteParamKeys.APPLNC_MTM_TYPE
				,lineItemParam,vMap,ct);
	}

	protected boolean validationMachModel(LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct) {	
		int validateLength = 3;
		String lengthMsg = DraftQuoteMessageKeys.MACHINE_MODEL_EXCEED_LIMIT;
		
		logContext.debug(this, "machineModel =" + lineItemParam.machineModel);
		return validateMTM(lineItemParam.machineModel,validateLength,
				DraftQuoteMessageKeys.MACHINE_MODEL_ALPHANUMERIC_MSG,
				lengthMsg,
				PartPriceViewKeys.MACHINE_MODEL_HDR,
				DraftQuoteParamKeys.APPLNC_MTM_MODEL
				,lineItemParam,vMap,ct);
	}
	
	

	protected boolean validationSerialNum(LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct) {		
		int validateLength = 7;
		String lengthMsg = DraftQuoteMessageKeys.MACHINE_SERIAL_NUBMER_EXCEED_LIMIT;
		QuoteLineItem qli = findLineFromQuote(lineItemParam.key);
		boolean flag = true;
		
		if (qli != null && qli.isDisplayModelAndSerialNum()) {
			logContext.debug(this, "nonIBMmachineSerialNumber =" + lineItemParam.nonIBMSerialNumber);
			
			if(qli.isMandatorySerialNum()) {
				if (StringUtils.isBlank(lineItemParam.nonIBMSerialNumber)) {
					addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,DraftQuoteMessageKeys.MACHINE_SERIAL_NUMBER_NOT_NULL,
							PartPriceViewKeys.MACHINE_SERIAL_NUMBER_HDR, PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER, vMap, ct);
					return false;
				}
			}
		} else {
			flag = validateMTM(lineItemParam.machineSerialNumber,validateLength,
					DraftQuoteMessageKeys.MACHINE_SERIAL_NUBMER_MSG,
					lengthMsg,
					PartPriceViewKeys.MACHINE_SERIAL_NUMBER_HDR,
					DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER
					,lineItemParam,vMap,ct);
			
		}
		
		return flag;
	}
	
	
	private boolean validateMTM(String validateProperty,int validateLength,
			String alphanumericMsg,String lengthMsg, String arg, String key,
			LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct){
		if (StringUtils.isNotBlank(validateProperty) 
				&& (lineItemParam.applncPocInd == null || DraftQuoteParamKeys.PARAM_YES.equalsIgnoreCase(lineItemParam.applncPocInd) )) {
			validateProperty = validateProperty.trim();
			if (!StringHelper.isAlphanumeric(validateProperty)) {
				addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						alphanumericMsg,arg,
						PartPriceViewKeys.PREFIX + lineItemParam.key + key, vMap,ct);
				return false;
			}
			
			
			if (validateProperty.length() != validateLength) {
				addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,lengthMsg,arg,PartPriceViewKeys.PREFIX + lineItemParam.key + key, vMap,ct);
				return false;
			}
		}
		return true;
	}
	
	private int getInt(String str){
		try{
			return Integer.parseInt(StringUtils.trim(str));
			
		} catch(Exception e){
			return 0;
		}
	}

	protected boolean validateApplncQty(LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct){
		boolean validate = true;
		
		String key = lineItemParam.key;
		if(StringUtils.isBlank(key)){
			return true;
		}
		
		int idx = key.indexOf("_");
		if(idx == -1){
			return true;
		}
		
		String partNum = key.substring(0, idx);
		int seqNum = getInt(key.substring(idx + 1));
		
		QuoteLineItem qli = quote.getLineItem(partNum, seqNum);
		
		//skip validation for parts that don't have quantity restriction
		if(qli == null || !qli.isApplncPart() || !qli.isApplncQtyRestrctn()){
			return true;
		}
		
		//skip validation for any parts that don't have quantities assigned
		if(StringUtils.isBlank(lineItemParam.quantity)){
			return true;
		}
		
		int qty = getInt(lineItemParam.quantity);
		if(!(qty > 1)){
			return true;
		}
		
		if(StringUtils.isNotBlank(lineItemParam.machineModel)
				|| StringUtils.isNotBlank(lineItemParam.machineType)
				|| StringUtils.isNotBlank(lineItemParam.machineSerialNumber)){
			validate = false;
		}
		
		String applncPocInd = lineItemParam.applncPocInd;
		if(StringUtils.isNotBlank(applncPocInd)
				&& "Y".equals(StringUtils.trim(applncPocInd))){
			validate = false;
		}
		
		if(!validate){
			addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					QuoteCapabilityProcess.MTM_APPLIANCE_QTY_GREATER_THAN_ONE_2,
					QuoteCapabilityProcess.APPLIANCE_QTY_HDR,
					PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.quantitySuffix, vMap, ct);
			return false;
		}
		
		return true;
	}
	
	protected boolean validateApplncPoc(LineItemParameter lineItemParam,HashMap vMap, PostPartPriceTabContract ct){
		boolean validate = true;
		
		String key = lineItemParam.key;
		if(StringUtils.isBlank(key)){
			return true;
		}
		
		int idx = key.indexOf("_");
		if(idx == -1){
			return true;
		}
		
		String partNum = key.substring(0, idx);
		int seqNum = getInt(key.substring(idx + 1));
		
		QuoteLineItem qli = quote.getLineItem(partNum, seqNum);
		
		//skip validation for parts that don't have quantity restriction or that is owerShip transfer part
		if(qli == null || qli.isApplncUpgrade()|| qli.isOwerTransferPart()){
			return true;
		}
			
		PartPriceCommon common = new PartPriceCommon(quote);
		if(!common.showApplianceInformation(findLineFromQuote(lineItemParam.key), quote.getQuoteHeader().isSubmittedQuote(), quote.getQuoteHeader().isSalesQuote())){
			return true;
		}
		
//		String applncPocInd = lineItemParam.applncPocInd;
//		if(StringUtils.isBlank(applncPocInd)){
//			validate = false;
//		}
//		
//		if(!validate){
//			addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
//					QuoteCapabilityProcess.APP_POC_IND_SELECTED,
//					QuoteCapabilityProcess.APPLIANCE_POC_IND,
//					PartPriceViewKeys.PREFIX + lineItemParam.key + DraftQuoteParamKeys.information, vMap, ct);
//			return false;
//		}
		
		return true;
	}
	
}
