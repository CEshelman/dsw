package com.ibm.dsw.quote.draftquote.util.validation;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.EquityCurve;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PAAndPAERule</code> is the implementation for PA and PAE
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Mar 26, 2007
 */

class PAAndPAERule extends FCTRule {

    public PAAndPAERule(Quote quote, PostPartPriceTabContract ct) {
        super(quote, ct);
    }



    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.draftquote.util.validation.BaseRule#validate()
     */
    public void validate() throws TopazException {
    	
    	processEquityCurve(quote, ct);
    	processOmittedLine(quote, ct);
        validateGSA();

        boolean additionalYearChanged = validateAdditionalMaintYears();

        // now we need reload the line items, because lineitem maybe
        // inserted/deleted when validate maint year
        if(additionalYearChanged){
            logContext.debug(this,"One of Line item additional maint year changed, need reload the line items");
            reloadLineItems(quote);
            itemCountChanged = true;
        }

        // if user change the qty of the firwt maint part, the subsequence maint
        // part also should be adjusted
        setSubSequenceMaintPartQty();

        quote.getQuoteHeader().setCmprssCvrageFlag(ct.isEnableCmprssCvrage());

        List items = quote.getLineItemList();
        
        // Get the sequence numbers for the additional years
        List<Integer> additionalYearItemsSeqNums = new ArrayList();
        additionalYearItemsSeqNums.addAll(GrowthDelegationUtil.getRQAdditionalYearItemsSeqNums(quote));
        additionalYearItemsSeqNums.addAll(GDPartsUtil.getAdditionalYearItemsSeqNums(quote));
        
        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);
            String key = this.createKey(item);

            //no UI data for this line item, continue
            if(!isPartParamExist(key) && (item.getPartQty() == null || item.getPartQty() != null && item.getPartQty().intValue() != 0)){
            	continue;
            }

            logContext.debug(this, "Begin to valid " + key);
            // it's reasonable that the line item data can't be found in contract
            // because it's may be added by SP
            if (this.ct.getItems().get(key) == null && (item.getPartQty() != null && item.getPartQty().intValue() != 0)) {
                logContext.debug(this, "the lind item can't be found in contract :" + key);
                continue;
            }

            validateSortOrder(item);

            boolean isDeleted = validateQuantity(item);

            // if the quantity is 0, the line item is deleted, ignore subsequent
            // validation
            if (isDeleted) {
                continue;
            }


            if(shouldValidateOvrrdUnitPriceAndDiscount(item)){
            	String ytyRadio = ct.getYtyRadio(key);
            	
            	if(StringUtils.isNotBlank(ytyRadio)){
            		YTYGrowth yty = getLineItemYTY(item);
            		yty.setYtyGrwothRadio(ytyRadio);
            	}
            	
            	if(StringUtils.equals(DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE, ytyRadio)){
            		validateYtyGrowthPct(item, additionalYearItemsSeqNums);
            		
            	} else {
            		 validateOverrideUnitPriceAndDiscount(item);
            	}
            } else {
                logContext.debug(this, createKey(item)
                        + "is not eligible for override unit price or discount, don't check override unit price and discount");
            }

            if(!item.isSaasPart()){
            	validatePVUPartQtyStatus(item);
                validateOverrideDate(item);
                validateProrateAnniversary(item);
                setPrevDates(item);
            	validateStartDate(item);
            	validateEndDate(item);
            }

            validateBpOverrideDiscount(item);

            //appliance parts
            if(item.isApplncPart() || item.isDisplayModelAndSerialNum()) {
            	validateAppliancePart(item);
            }

          //15.1
            if(ct.isSQOEnv()){
            	QuoteHeader header = quote.getQuoteHeader();
            	// ramp up parts will be setted along with their related subscription part, skip set value here
            	if (!item.isRampupPart()){
            		if (item.isSaasSubscrptnPart()){
                		validateRenwlMgrtn(item,header.isSaasRenewalFlag(),header.isSaasMigrationFlag());
                	}
                	if ((item.isMonthlySoftwarePart()&&((MonthlySwLineItem) item).isMonthlySwSubscrptnPart())){
                		validateRenwlMgrtn(item,header.isMonthlyRenewalFlag(),header.isMonthlyMigrationFlag());
                	}
            	}	
            }
            	//if(!quote.getQuoteHeader().isPGSQuote())//refer to rtc#207982. For 10.6, only SQO needs this functionality.
                	//validateSaasRenwl(item);
            
         // ramp up parts will be setted along with their related subscription part, skip set value here
            if (!item.isRampupPart()){
            	validateSaasMgrtn(item);
            }
            
        }
        
        //appliance#99
        sharelineItemCRADWithApplncId();

        removeZeroQtyItems();

        checkIfLineItemEligibleForCmprssCvrage();

        calcuateDate();

        validateLineItemBackDating();

        //For cmprss cvrage
        validateCmprssCvrage();

        validateEOLPart();

        //validateSpecialBid();

        PartPriceHelper.calculateTotalPoint(quote);

        validateOfferPrice();

        validatePaymentTermDays();

        validateValidityDays();

        processPrvsningDays(quote, ct);
        
        processServiceDate(quote,ct);

        processCoTerms(quote, ct);

        processEstmtdOrdDate(quote, ct);
    }
    
    private YTYGrowth getLineItemYTY(QuoteLineItem item) throws TopazException{
    	YTYGrowth yty= item.getYtyGrowth();
		if(yty == null){
			yty = GrowthDelegationUtil.getDefaultYTY(quote, item, null);
			item.setYtyGrowth(yty);
		}
		
		return yty;
    }

    protected void validateYtyGrowthPct(QuoteLineItem item, List<Integer> additionalYearItemsSeqNums) throws TopazException {
        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);
        
        if (!additionalYearItemsSeqNums.contains(item.getSeqNum())) {
        	if(!GrowthDelegationUtil.isDisplayLineItemYTY(quote, item) && !GDPartsUtil.isEligibleRenewalPart(item)){
        		return;
        	}
        }
        
        YTYGrowth yty = getLineItemYTY(item);
    	Double ytyDiscPct = null;
    	String strYTYPct = ct.getYty(key);
    	
    	try{
    		ytyDiscPct = Double.valueOf(strYTYPct);
    	}catch(Exception e){
    		logContext.error(this, e);
    	}
    	
    	boolean needChange = false;
    	if(ytyDiscPct == null){
    		if(yty != null && yty.getYTYGrowthPct() != null){
    			needChange = true;
    		}
    	} else {
    		if(yty == null || yty.getYTYGrowthPct() == null){
    			needChange = true;
    		} else {
    			if(!DecimalUtil.isEqual(DecimalUtil.roundAsDouble(yty.getYTYGrowthPct(), 2), ytyDiscPct)){
    				needChange = true;
    			}
    		}
    	}
    	
    	if(needChange){
    		vr.unitPriceOrDiscountChanged = true;

   			item.setOverrideUnitPrc(null);

            if (quote.getQuoteHeader().getOfferPrice() != null){
            	item.setOvrrdExtPrice(null);
            	item.setOfferIncldFlag(Boolean.FALSE);
            } else {
            	item.setOvrrdExtPrice(null);
            	item.setOfferIncldFlag(null);
            }

    		yty.setYTYGrowthPct(ytyDiscPct);
			item.setLineDiscPct(0);
    	}
    }

    protected void validateProrateAnniversary(QuoteLineItem item) throws TopazException {

        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);

        boolean prorateAnni = false;
        try {
            prorateAnni = ct.getPartProrateAnni(key);
        } catch (Throwable e) {
            writeLog("Proration", item, e);
            return;
        }

        if (item.getProrateFlag() != prorateAnni) {
            item.setProrateFlag(prorateAnni);
            vr.prorateAnniversaryChanged = true;

        }
    }

    protected void validateGSA() throws TopazException {

        QuoteHeader header = quote.getQuoteHeader();
        String gsaFlag = ct.getUseGSAPricing();

        logContext.debug(this, "GSA Flag = " + gsaFlag);

        this.useGsaPricing = DraftQuoteParamKeys.GSA_PRICING_YES.equals(gsaFlag) ? 1 : 0;

        if (header.getGsaPricngFlg() != useGsaPricing) {

            this.needUpdateGSA = true;

        }
    }
    
    protected void processEquityCurve(Quote quote, PostPartPriceTabContract ct) throws TopazException {
    	if(quote == null || !quote.getQuoteHeader().isECEligible()){
    		return;
    	}
    	
    	String userId = ct.getUserId();
    	
    	List softwareList = quote.getLineItemList();
    	if(softwareList != null && softwareList.size() > 0){
    		
    		for (Iterator lineItemIt = softwareList.iterator(); lineItemIt.hasNext();) {

                QuoteLineItem lineItem = (QuoteLineItem) lineItemIt.next();
                EquityCurve ec = lineItem.getEquityCurve();
                if(ec == null || lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()){
                	continue;
                }
                if(ec.isEquityCurveFlag()){
	    			String key = createKey(lineItem);
	    			if (lineItem.getPartQty() != null && lineItem.getPartQty().intValue() != ct.getPartQtyInteger(key)){
	    				ct.getQuote().getQuoteHeader().setECRecalculateFlag(true);
	    				return;
	    			}
	    			
	    			String ctPartDiscountStr = ct.getPartDiscount(key);    			
	    			if (StringUtils.isNotBlank(ctPartDiscountStr)) {
	    				double ctPartDiscount = Double.valueOf(ctPartDiscountStr).doubleValue();
	    				if (!(DecimalUtil.formatTo5Number(ctPartDiscount)).equals(DecimalUtil.formatTo5Number(lineItem.getLineDiscPct()))) {
		    				ct.getQuote().getQuoteHeader().setECRecalculateFlag(true);
		    				return;
	    				}
	    			}

	    			
	    			String ctPartPriceStr = ct.getPartPrice(key);
	    			String ctOriginalPartPriceStr = ct.getOriginalPartPrice(key);
	    			if (null != ctPartPriceStr && null != ctOriginalPartPriceStr) {
	    				// the current override price is different with the original override price
	    				if (!ctPartPriceStr.equalsIgnoreCase(ctOriginalPartPriceStr)){
	    					// the current override price is bank or the original override price
	    					if (StringUtils.isBlank(ctPartPriceStr) || StringUtils.isBlank(ctOriginalPartPriceStr)){
	    						ct.getQuote().getQuoteHeader().setECRecalculateFlag(true);
			    				return;
	    					}
	    					// compare the current override price with the original override price
	    					double ctPartPrice = 0;
	    					double ctOriginalPartPrice = 0;
	    					NumberFormat format = NumberFormat.getInstance();
	    					try {
	    						ctPartPrice = format.parse(ctPartPriceStr).doubleValue();
	    						ctOriginalPartPrice = format.parse(ctOriginalPartPriceStr).doubleValue();
	    					} catch (ParseException e) {
	    						logContext.debug(this, e.getMessage());
	    					}
	    					if (Math.abs(ctPartPrice - ctOriginalPartPrice) > 0.001) {
	    						ct.getQuote().getQuoteHeader().setECRecalculateFlag(true);
	    						return;
	    					}
	    				}
	    				
	    			}
                }
    		}
    	}
    }
    
    protected void processOmittedLine(Quote quote, PostPartPriceTabContract ct) throws TopazException {
        if( null == quote || !quote.getQuoteHeader().isOmittedLine() ||
                (quote.getQuoteHeader().isOmittedLine() && QuoteConstants.OMIT_RECALCULATE_Y == quote.getQuoteHeader().getOmittedLineRecalcFlag())){
            return;
        }

        List softwareList = quote.getLineItemList();
        if(softwareList != null && softwareList.size() > 0){
            for (Iterator lineItemIt = softwareList.iterator(); lineItemIt.hasNext();) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItemIt.next();
                String key = createKey(lineItem);
                if (lineItem.getPartQty() != null && lineItem.getPartQty().intValue() != ct.getPartQtyInteger(key)){
                    ct.getQuote().getQuoteHeader().setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_Y);
                    return;
                }

                String ctPartPriceStr = ct.getPartPrice(key);
                if (StringUtils.isNotBlank(ctPartPriceStr)) {
                    ctPartPriceStr = ctPartPriceStr.trim();
                    NumberFormat format = NumberFormat.getInstance();
                    double ctPartPrice = 0;
                    try {
                        ctPartPrice = format.parse(ctPartPriceStr).doubleValue();
                    } catch (ParseException e) {
                        logContext.debug(this, e.getMessage());
                    }
                    Double dOverrideUnitPrice = lineItem.getLocalUnitProratedDiscPrc();
                    if ( null != dOverrideUnitPrice && dOverrideUnitPrice.doubleValue() != ctPartPrice) {
                        ct.getQuote().getQuoteHeader().setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_Y);
                        return;
                    }
                }

                String ctPartDiscountStr = ct.getPartDiscount(key);
                if (StringUtils.isNotBlank(ctPartDiscountStr)) {
                    double ctPartDiscount = Double.valueOf(ctPartDiscountStr).doubleValue();
                    if (DecimalUtil.roundAsDouble(ctPartDiscount,3) != DecimalUtil.roundAsDouble(lineItem.getLineDiscPct(),3)) {
                        ct.getQuote().getQuoteHeader().setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_Y);
                        return;
                    }
                }

                String ytyRadio = ct.getYtyRadio(key);
                String ctYtyGrowthStr = ct.getYty(key);
                if (StringUtils.isNotBlank(ytyRadio) && StringUtils.equals(DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE, ytyRadio)) {
                    double ctYtyGrowth = Double.valueOf(ctYtyGrowthStr).doubleValue();
                    if (DecimalUtil.roundAsDouble(ctYtyGrowth,2) != DecimalUtil.roundAsDouble(lineItem.getYtyGrowth().getYTYGrowthPct(),2)) {
                        ct.getQuote().getQuoteHeader().setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_Y);
                        return;
                    }
                }

            }
        }
    }

}
