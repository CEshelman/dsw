package com.ibm.dsw.quote.draftquote.process;


import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.ArithmeticOperationException;
import com.ibm.dsw.quote.base.exception.PayerDataException;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants.PartRelNumAndTypeDefault;
import com.ibm.dsw.quote.common.domain.DeployModel;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.spbid.SpecialBidRule;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.util.builder.DraftQuoteBuilder;
import com.ibm.dsw.quote.draftquote.util.validation.ValidationRule;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.submittedquote.contract.UpdateLineItemCRADContract;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PartPriceProcess_Impl</code> class is abstract implementation of
 * PartPriceProcess.
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: 2007-3-27
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class PartPriceProcess_Impl extends TopazTransactionalProcess implements PartPriceProcess {

    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    @Override
	public void getPartPriceInfoNoTransation(Quote quote, QuoteUserSession user) throws QuoteException, PriceEngineUnAvailableException {
    	TimeTracer tracer = TimeTracer.newInstance();
        DraftQuoteBuilder builder = null;
        try {
        	QuoteReasonFactory.singleton().getBackDatingReason(quote.getQuoteHeader());
        	//SQO userId logic is not same as PGS userId logic. Refer to rtc #176266 - 8
			String userId = user.getUserId();
            builder = DraftQuoteBuilder.create(quote, userId);
            builder.buildForDisplay();                
            if (builder.isSapCallFailed()) {
            	if(builder.isSapCallFailedBecausePayerDataIssue()){
            		throw new PayerDataException(true, builder.getPayerDataIssueMsg());
            	} else if (builder.isSapCallFailedBeacuseArithmeticIssue()) {
            		throw new ArithmeticOperationException(true, builder.getArithmeticOperationMsg());
            	} else {
            		throw new PriceEngineUnAvailableException();
            	}
            }
            if (!builder.isOfferPriceOk()){
            }
        } catch (TopazException e) {
            logContext.debug(this, "error getting part & pricing info");
            throw new QuoteException(e);
        } finally {
            tracer.dump();
        }
        // update PartPriceFlags and update DeployModel
        try {
            builder.updatePartPriceFlagsByBuilder();
            builder.updateDeployModelByBuilder();
        } catch (TopazException e) {
            logContext.debug(this, "error getting part & pricing info");
            throw new QuoteException(e);
        }
    }
    @Override
	public void getPartPriceInfo(Quote quote, QuoteUserSession user) throws QuoteException, PriceEngineUnAvailableException {

    	TimeTracer tracer = TimeTracer.newInstance();
    	this.beginTransaction();
        DraftQuoteBuilder builder = null;
        try {
        	QuoteReasonFactory.singleton().getBackDatingReason(quote.getQuoteHeader());
        	//SQO userId logic is not same as PGS userId logic. Refer to rtc #176266 - 8
			String userId = user.getUserId();

            builder = DraftQuoteBuilder.create(quote, userId);

            builder.buildForDisplay();

            //updateQuoteHeader(quote.getQuoteHeader());

            tracer.stmtTraceStart("commit transaction");
            commitTransaction();
            tracer.stmtTraceEnd("commit transaction");
            
            if (builder.isSapCallFailed()) {
            	if(builder.isSapCallFailedBecausePayerDataIssue()){
            		throw new PayerDataException(true, builder.getPayerDataIssueMsg());
            	} else if (builder.isSapCallFailedBeacuseArithmeticIssue()) {
            		throw new ArithmeticOperationException(true, builder.getArithmeticOperationMsg());
            	} else {
            		throw new PriceEngineUnAvailableException();
            	}
            }
            if (!builder.isOfferPriceOk()){
            }
            // update PartPriceFlags and update DeployModel
            builder.updatePartPriceFlagsByBuilder();
            builder.updateDeployModelByBuilder();

        } catch (TopazException e) {
            logContext.debug(this, "error getting part & pricing info");
            throw new QuoteException(e);

        } finally {
            this.rollbackTransaction();
            tracer.dump();
        }


    }

    @Override
	public void getCurrentPriceForSubmit(Quote quote, String userID) throws QuoteException, PriceEngineUnAvailableException {

        this.beginTransaction();

        try {

            DraftQuoteBuilder builder = DraftQuoteBuilder.create(quote, userID);

            builder.buildForSubmit();
          //add this function to compute GRID
          //should not check special bid rules for renewal quote
            if(quote.getQuoteHeader().isSalesQuote()){
            	validateSpecialBidRule(quote, userID);
            }

            //updateQuoteHeader(quote.getQuoteHeader());

            commitTransaction();

            if (builder.isSapCallFailed()) {
                throw new PriceEngineUnAvailableException();
            }
            if (!builder.isOfferPriceOk()){
            }
        } catch (TopazException e) {
            throw new QuoteException(e);

        } finally {
            this.rollbackTransaction();
        }

    }

    @Override
	public void getCurrentPriceForOrder(Quote quote, String userID) throws QuoteException, PriceEngineUnAvailableException {


        try {
            this.beginTransaction();

            DraftQuoteBuilder builder = DraftQuoteBuilder.create(quote, userID);
            builder.buildForOrder();
            //add this function to compute GRID
            //should not check special bid rules for renewal quote
            if(quote.getQuoteHeader().isSalesQuote()){
              	validateSpecialBidRule(quote, userID);
              }
            this.commitTransaction();

            if (builder.isSapCallFailed()) {
                throw new PriceEngineUnAvailableException();
            }
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    @Override
	public void postPartPriceInfo( ProcessContract contract) throws QuoteException {
        long start = System.currentTimeMillis();
        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;
        String userId = ct.getUserId();

        try {
            Quote quote = ((PostPartPriceTabContract)contract).getQuote();
            quote.setPgsAppl(ct.isPGSEnv());
            innerPostPartPriceInfo((PostPartPriceTabContract) contract);
            
            // check special bid
            validateSpecialBidRule(quote, userId);

            updateQuoteHeader(quote.getQuoteHeader(), ct.getUserId());

        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } 
        long end = System.currentTimeMillis();
        logContext.debug(this, "post pp tab :" + (end - start));
    }

    @Override
	public void clearOffer(PostPartPriceTabContract contract) throws QuoteException {
        this.beginTransaction();
        String userId = contract.getUserId();

        try {
            contract.setOfferAction(PartPriceConstants.OfferPriceAction.CLEAR_OFFER_PRICE_ACTION);
            Quote quote = innerPostPartPriceInfo(contract);

            // clears all override extended prices from all line items
        	// set include_offer_flag  to null for all contract parts
            QuoteCommonUtil.clearOfferPrice(quote);
            //set offer price to null
            quote.getQuoteHeader().setOfferPrice(null);

            validateSpecialBidRule(quote, userId);

            //always set re-calculate flag to true
            quote.getQuoteHeader().setRecalcPrcFlag(1);

            //update quote header
            updateQuoteHeader(quote.getQuoteHeader(), userId);

            commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        long end = System.currentTimeMillis();
        logContext.debug(this, "clear offer");
    }

    @Override
	public void applyOffer(PostPartPriceTabContract contract) throws QuoteException {
        this.beginTransaction();
        String userId = contract.getUserId();

        try {
            contract.setOfferAction(PartPriceConstants.OfferPriceAction.APPLY_OFFER_PRICE_ACTION);
            Quote quote = innerPostPartPriceInfo(contract);

            // clears all discounts, override unit prices, override extended prices from all line items
            for( Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
            	QuoteLineItem qli = (QuoteLineItem)iter.next();

            	if(!QuoteCommonUtil.shouldIncludeInOfferPrice(quote, qli)){
            		continue;
            	}

            	qli.setLineDiscPct(0d);
            	qli.setOverrideUnitPrc(null);
				qli.setOvrrdExtPrice(null);

				processYTYRadio(quote, qli, true);
				clearSetLineToRsvp(quote);
				if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
					qli.setOfferIncldFlag(null);
				} else {
					if (PartPriceConstants.PartTypeCode.PACTRCT.equals(qli.getPartTypeCode())){
						qli.setOfferIncldFlag(null);
					}
				}
            }

            // set offer price to quote header
            Double offerPrice = Double.valueOf(contract.getOfferPrice());
            // set the EC flag
            setECRecalculateFlagForApplyOffer(quote.getQuoteHeader(), offerPrice.doubleValue());
            setOLRecalculateFlagForApplyOffer(quote.getQuoteHeader(), offerPrice.doubleValue());
            quote.getQuoteHeader().setOfferPrice(offerPrice);
            // re-set special bid flag
            validateSpecialBidRule(quote, userId);

            // always set re-calculate flag to true
            quote.getQuoteHeader().setRecalcPrcFlag(1);
            

            // update quote header
            updateQuoteHeader(quote.getQuoteHeader(), userId);

            commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        long end = System.currentTimeMillis();
        logContext.debug(this, "apply offer");
    }

    private void processYTYRadio(Quote quote, QuoteLineItem qli, boolean isForOfferPrice)
                                   throws QuoteException, TopazException{
     	if(GrowthDelegationUtil.isDisplayLineItemYTY(quote, qli)){
    	 	YTYGrowth yty = qli.getYtyGrowth();
    	 	if(yty == null){
    	 		yty = GrowthDelegationUtil.getDefaultYTY(quote, qli, null);
    	 	}
        	//remove YTY growth information
        	if(yty != null){
        		yty.setYTYGrowthPct(null);
        		if(isForOfferPrice){
        			yty.setYtyGrwothRadio(null);
        		} else {
        			yty.setYtyGrwothRadio(DraftQuoteParamKeys.YTY_RADIO_DISCOUNT_VALUE);
        		}

        		//this.doCalculateYtyGrowth(qli, quote.getQuoteHeader());
        	}
    	}
     	// 14.2 GD
     	if(GDPartsUtil.isEligibleRenewalPart(qli)){
     		GDPartsUtil.initYtyGrowth(quote, qli);
    	 	YTYGrowth yty = qli.getYtyGrowth();
        	// remove YTY growth information
        	if(yty != null){
        		yty.setYTYGrowthPct(null);
        		yty.setManualLPP(null);
        		if(isForOfferPrice){
        			yty.setYtyGrwothRadio(null);
        		} else {
        			yty.setYtyGrwothRadio(DraftQuoteParamKeys.YTY_RADIO_DISCOUNT_VALUE);
        		}

        		//this.doCalculateYtyGrowth(qli, quote.getQuoteHeader());
        	}
    	}
    }

    @Override
	public void applyDiscount(PostPartPriceTabContract contract) throws QuoteException {
        this.beginTransaction();
        String userId = contract.getUserId();

        try {
            Quote quote = innerPostPartPriceInfo(contract);
            boolean isBidIteration = CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader());
            quote.getQuoteHeader().setOfferPrice(null);
            logContext.debug(this, "Current offer price"+quote.getQuoteHeader().getOfferPrice());
            for( Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
            	QuoteLineItem qli = (QuoteLineItem)iter.next();
            	//skip the SaaS part when quote is bid iteration
            	if(isBidIteration && (qli.isSaasPart() || qli.isMonthlySoftwarePart())){
            		continue;
            	}

            	//skip apply discount for EOL part
            	if(qli.isObsoletePart()){
            		continue;
            	}

            	//Skip apply discount for cmprss cvrage applied parts
            	if(qli.hasValidCmprssCvrageMonth() ){
            	    continue;
            	}

            	//exclude the SaaS parts that forbidden to input discount or it's replace part
            	if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
            		if(!PartPriceSaaSPartConfigFactory.singleton().canInputDiscount(qli)
            			|| qli.isReplacedPart()){
            			continue;
            		}
            	}
            	// set the EC flag
            	setECRecalculateFlagForApplyDisc(quote.getQuoteHeader(), qli, Double.valueOf(contract.getQuoteDiscountPercent()).doubleValue());
            	setOLRecalculateFlagForApplyDisc(quote.getQuoteHeader(), qli, Double.valueOf(contract.getQuoteDiscountPercent()).doubleValue());
            	//clear all override unit price and discounts on each line items
            	qli.setOverrideUnitPrc(null);
            	qli.setLineDiscPct(0d);

            	processYTYRadio(quote, qli, false);

				//If an offer has been applied, clears the offer price and the override extended price from each line item,
            	//set include_offer_flag to NULL
            	qli.setOvrrdExtPrice(null);
             	qli.setOfferIncldFlag(null);
             	//if an offer has been applied ,clrars the setLineTORsvpflag to false
             	qli.setSetLineToRsvpSrpFlag(false);
            	//System sets the line item discount percent to the quote discount percent value entered by the user on all contract part line items.
            	//for FCT quote, apply discount to all parts
            	if (quote.getQuoteHeader().isPAQuote() ||
            	        quote.getQuoteHeader().isPAEQuote()
            	        || quote.getQuoteHeader().isOEMQuote()
            	        || quote.getQuoteHeader().isSSPQuote()) {
            	    if (PartPriceConstants.PartTypeCode.PACTRCT.equalsIgnoreCase(qli.getPartTypeCode())) {
            	        if (StringUtils.isNotBlank(contract.getQuoteDiscountPercent())) {
                        	qli.setLineDiscPct(Double.valueOf(contract.getQuoteDiscountPercent()).doubleValue());
                        	if(quote.getQuoteHeader().isRenewalQuote()){
                        	    qli.setChgType("_"+PartPriceConstants.PartChangeType.PART_DISCOUNT_UNIT_PRICE_CHANAGED+"__");
                        	}
            	        }
            	    }
            	}
            	else if (quote.getQuoteHeader().isFCTQuote()) {
        	        if (StringUtils.isNotBlank(contract.getQuoteDiscountPercent())) {
                    	qli.setLineDiscPct(Double.valueOf(contract.getQuoteDiscountPercent()).doubleValue());
        	        }
            	}

            	//if is Saas part, and Can user input unit over-ride price,  but SQO must calculate override unit price and round per currency's decimal places
            	if(StringUtils.isNotBlank(contract.getQuoteDiscountPercent()) && (qli.isSaasPart() || qli.isMonthlySoftwarePart())
	                	&& PartPriceSaaSPartConfigFactory.singleton().shldClcltOvrrdUnitPricPerDisc(qli)){
            		qli.setOverrideUnitPrc(
	                	QuoteCommonUtil.getFormatDecimalPrice(quote, qli, QuoteCommonUtil.calculatePriceByDiscount(Double.valueOf(contract.getQuoteDiscountPercent()).doubleValue(), qli.getLocalUnitProratedPrc()))
	                	);
	           }
            }

            // re-set special bid flag
            validateSpecialBidRule(quote, userId);

            // always set re-calculate flag to true
            quote.getQuoteHeader().setRecalcPrcFlag(1);
            
            

            // update quote header
            updateQuoteHeader(quote.getQuoteHeader(), userId);

            commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        long end = System.currentTimeMillis();
        logContext.debug(this, "apply discount");
    }

    @Override
	public void applyPrcBandOvrrd(PostPartPriceTabContract contract) throws QuoteException {
		this.beginTransaction();
		String userId = contract.getUserId();

		try {
			Quote quote = innerPostPartPriceInfo(contract);
			for (Iterator iter = quote.getLineItemList().iterator(); iter.hasNext();) {
				QuoteLineItem qli = (QuoteLineItem) iter.next();

				if (qli.getOverrideUnitPrc() != null) {
					continue;
				}

				if (qli.getLocalUnitProratedDiscPrc() == null) {
					continue;
				}

				// For PA quote, OUP/Discount is not allowed for non-contract parts
				if (!qli.isContractPart()) {
					continue;
				}

				// Exclude cmprss cvrage applied parts
				if (qli.hasValidCmprssCvrageMonth()) {
					continue;
				}

				// exclude the SaaS parts and monthly software parts
				if (qli.isSaasPart() || qli.isMonthlySoftwarePart()) {
					continue;
				}

				qli.setOvrrdExtPrice(null);
				qli.setOfferIncldFlag(Boolean.FALSE);

				qli.setOverrideUnitPrc(null);
				qli.setLineDiscPct(0d);

				double entitledUnitPrice = qli.getLocalUnitProratedDiscPrc().doubleValue();

				if (DecimalUtil.isEqual(qli.getLineDiscPct(), 0)) {
					qli.setOverrideUnitPrc(new Double(entitledUnitPrice));
				} else {
					qli.setOverrideUnitPrc(new Double(entitledUnitPrice * (1 - qli.getLineDiscPct() / 100)));
				}

			}

			quote.getQuoteHeader().setOvrrdTranLevelCode(null);
			// re-set special bid flag
			validateSpecialBidRule(quote, userId);

			// always set re-calculate flag to true
			quote.getQuoteHeader().setRecalcPrcFlag(1);

			// update quote header
			updateQuoteHeader(quote.getQuoteHeader(), userId);

			commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			this.rollbackTransaction();
		}
		long end = System.currentTimeMillis();
		logContext.debug(this, "apply discount");
	}

    /**
     * @param contract
     * @throws QuoteException
     * @throws NoDataException
     * @throws TopazException
     */
    private Quote innerPostPartPriceInfo(PostPartPriceTabContract contract) throws QuoteException, NoDataException, TopazException {


        if (contract.getItems().values().size() == 0) {
            logContext.debug(this, "no parts in the quote, no need to perform the post");
            return null;

        }

        Quote quote = contract.getQuote();
        // add for compute the term extension info logic for new and addon
        processTermExtension(quote,contract);
        
     // set migration flag and renewal flag for saas and monthly // not apply for PGS
        QuoteHeader header = quote.getQuoteHeader();
        if (!quote.isPgsAppl()){
        	header.setSaasRenewalFlag(contract.getIsSaasRenewal());
            header.setSaasMigrationFlag(contract.getIsSaasMigration());
            header.setMonthlyRenewalFlag(contract.getIsMonthlyRenewal());
            header.setMonthlyMigrationFlag(contract.getIsMonthlyMigration());
        }

        ValidationRule rule = ValidationRule.createRule(quote, contract);
        rule.execute();
        int recalculateFlag = rule.needRecalculateQuote() ? 1 : 0;
        //int specialBid = rule.isSpecialBid() ? 1 : 0;
        int needGSAPricing = rule.getUseGsaPricing();
        
        if (header.getRecalcPrcFlag() == 0) {
            header.setRecalcPrcFlag(recalculateFlag);
        }
        //header.setSpeclBidSystemInitFlag(specialBid);
        if(!CommonServiceUtil.quoteIsDraftBidItrtn(header)){
	        header.setGsaPricngFlg(needGSAPricing);
	        header.setQuoteStartDate(contract.getStartDate());
	        header.setRefDocNum(contract.getChargeAgreementNum());
	        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
    		quoteProcess.updateQuoteHeaderCustPrtnrTab(contract.getUserId(), contract.getExpireDate(),
    				header.getFulfillmentSrc(), header.getRnwlPrtnrAccessFlag() == -1 ? null : String.valueOf(header.getRnwlPrtnrAccessFlag()),
                    header.isResellerToBeDtrmndFlag() ? 1 : 0, header.isDistribtrToBeDtrmndFlag() ? 1 : 0,
                    contract.getQuoteClassfctnCode(), contract.getStartDate(),
                    contract.getOemAgrmntType(), contract.getPymntTermsDays(), contract.getOemBidType(),
                    contract.getEstmtdOrdDate(), contract.getCustReqstdArrivlDate(), contract.getSspType());
        }
        
            
        QuoteReasonFactory.singleton().updateBackDatingReason(header, contract.getUserId());

        //process Renewal model : insert or update renewal model
        processRenewalModel(contract,header.getWebQuoteNum());

        this.updateQuoteHeader(header, contract.getUserId());

        logContext.debug(this, "complete posting part & pricing tab");

        return quote;
    }

	private void processTermExtension(Quote quote,
			PostPartPriceTabContract contract) throws TopazException, QuoteException {
    	// get termExtensionMap(configid,boolean) from contract
    	Map termExtensionMap = contract.getTermExtensionMap();
    	if(termExtensionMap.size() == 0){
    		return;
    	}

    	for(Iterator it = termExtensionMap.keySet().iterator();it.hasNext();){
    		String configId = (String) it.next();
    		boolean termExtensionFlag = ((Boolean) termExtensionMap.get(configId)).booleanValue();
    		List partsPricingConfigrtnsList = quote.getPartsPricingConfigrtnsList();

    		// get partsPricingConfigrtns with the same confid of the contract termExtensionMap
    		for(int i=0;i<partsPricingConfigrtnsList.size();i++){
    			PartsPricingConfiguration ppc = (PartsPricingConfiguration) partsPricingConfigrtnsList.get(i);

    			if(!configId.equals(ppc.getConfigrtnId())){
    				continue;
    			}

    			// if configuration action code is not add-ons/trade-ups or FctPAFnl do nothing.
    			if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())
    					&& !PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())){
    				continue;
    			}

    			// TermExtensionFlag is true: 1.If the lineItem has a replaced lineItem just to recalculate term
    			//							  2.If the lineItem has not a replaced lineItem,need to create a new line
    			//                            item and mark the orig line item is replaced and add both old and new line
    			//							  item to the quote line item.
    			// TermExtensionFlag is false: 1. ppc.isTermExtension() is false do noting
    			// 							   2. ppc.isTermExtension() is true rollback line item to CA line item.
    			List saasLineItems = quote.getSaaSLineItems();
    			List origLineItems = quote.getLineItemList();
    			List<QuoteLineItem> lineItems = new ArrayList<QuoteLineItem>();
    			if(termExtensionFlag){
    	    		// get replaced line items from saas line items
    	    		List<QuoteLineItem> replacedLineItems = findReplacedLineItems(saasLineItems);
    	    		// convert list to map with item part number is key and item is the value
    	    		Map<String,QuoteLineItem> replacedLineItemsMap = toMap(replacedLineItems);
    	    		// store new LineItem
    	    		List<QuoteLineItem> newLineItems = new ArrayList<QuoteLineItem>();
    	    		// store replaced lineItem
    	    		//List<QuoteLineItem> removeLineItems = new ArrayList<QuoteLineItem>();
    	    		Integer desNum = getMaxDesNumber(saasLineItems);
    	    		//logContext.debug(this, "max desseqnum := " + desNum);
    	    		Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm = new HashMap<String, ConfiguratorPart>();
					if(saasLineItems.size() > 0 && StringUtils.isNotBlank(contract.getChargeAgreementNum())
							&& StringUtils.isNotBlank(ppc.getConfigrtnId())){
						findMainPartsFromChrgAgrm = findMainPartsFromChrgAgrm(contract.getChargeAgreementNum(),ppc.getConfigrtnId());
					}
    	   			for(int j=0;j<saasLineItems.size();j++){
        				QuoteLineItem origItem = (QuoteLineItem) saasLineItems.get(j);
        				QuoteLineItem tempReplacedLineItem = replacedLineItemsMap.get(origItem.getPartNum());
        				QuoteLineItem newItem = null;

        				if(!origItem.isReplacedPart()){
        					// (trade up) there are no replaced line item with the same part number
//        					if(tempReplacedLineItem == null && origItem.isSaasSubscrptnPart()){
////                                try {
////                                    Object cloned = origItem.clone();
////                                    if (cloned instanceof QuoteLineItem) {
////                                        QuoteLineItem quoteLineItem = (QuoteLineItem) cloned;
////                                        newItem = QuoteLineItemFactory.singleton().createNewLineItemFromExistItem(quoteLineItem);
////                                    }
////
////                                } catch (CloneNotSupportedException e) {
////                                    logContext.error(this, e.getMessage());
////                                    throw new QuoteException(e);
////                                }
////        						desNum++;
////        						newItem.setDestSeqNum(desNum);
////         						tempReplacedLineItem = origItem;
////        						tempReplacedLineItem.setReplacedPart(true);
////        						tempReplacedLineItem.setIRelatedLineItmNum(newItem.getDestSeqNum());
////        						// only not (replaced & subcription) or setup part set related coterm line item number.
////        						tempReplacedLineItem.setRelatedCotermLineItmNum(null);
//
//        					}else{
//        						newItem = origItem;
//        					}
        					newItem = origItem;
        					if(newItem.isSaasSubscrptnPart() || newItem.isSaasSubsumedSubscrptnPart()){
        						ConfiguratorPart part = findMainPartsFromChrgAgrm.get(newItem.getPartNum());
        						if(part != null){
        							newItem.setRelatedCotermLineItmNum(part.getRefDocLineNum());
        						}
        					}else{
        						// add-on / trade-up old logic only (part.isSubscrptn() && !part.isPartReplaced()) || part.isSetUp()
        						// set coterm-line item,but for 10.6 term extension set up part not need to set coterm line item.
        						// can see Notes://CAMDB10/85256B890058CBA6/CD76522BA873968E85256D33004FBB0B/A4C30E802EC29E2B85257B2E0063F26E
        						newItem.setRelatedCotermLineItmNum(null);
        					}
        					lineItems.add(newItem);
        					if(tempReplacedLineItem != null){
        						lineItems.add(tempReplacedLineItem);
        					}
        					newLineItems.add(newItem);
        				}

        				// If the lineItem is not replaced,clone a new LineItem form it and add the new line item to return line items.
        				// if replacedLineItems does't have the same  part number need to add it if not add the old one.
        					// set the new lineItem isNew to persist to db


        					// saas daily part not to add replace part
        					//newLineItems.add(newItem);
        					//origItem.setReplacedPart(true);
        					// add replaced part to return line item.

        			}
    	   			//flatLineItems.addAll(newLineItems);
    	   			logContext.debug(this, "lineItems := " + lineItems);

    	   			// If termExtensionFlag is true,update co-term end date.
    	   			// processForCoterm(ppc,contract);
    	   			//
    	   			//processForExtensionEligibilityDate(lineItems,ppc,contract);

    	   			quote.setLineItemList(lineItems);
    	   			quote.setSaaSLineItems(lineItems);

    			// if the termExtensionFlag is false and ppc.isTermExtension() is true need to rollback the lineitem to ca lineItem
    			}else{
					if (ppc.isTermExtension() && StringUtils.isNotBlank(contract.getChargeAgreementNum())) {
    					List<ConfiguratorPart> configuratorParts = getSubPartsFromChrgAgrm(contract.getChargeAgreementNum(),ppc.getConfigrtnId());
    					Map<String,ConfiguratorPart> caPartsMap = toConfiguratorPartMap(configuratorParts);
    					// hold lineItem seq number which need to delete
    					List inValidLineItemPartSeqNumList = new ArrayList();
    					for(int j=0;j<origLineItems.size();j++){
    						QuoteLineItem origItem = (QuoteLineItem) origLineItems.get(j);
    						ConfiguratorPart caPart = caPartsMap.get(origItem.getPartNum());

    						// the saas part's qulity is the same with the same part number in Ca part
    						// need to delete the line item and related line items
    						if(caPart != null){
    							if(origItem.isSaasSubscrptnPart() && !origItem.isReplacedPart()){
    								int origQty = (origItem.getPartQty() == null ? 0 : origItem.getPartQty().intValue());
    								int caQty = (caPart.getPartQty() == null ? 0:  caPart.getPartQty().intValue());
    								if(origQty == caQty){
    									inValidLineItemPartSeqNumList.add(origItem.getSeqNum());
    									processSameQtyLineItem(origItem,origLineItems,inValidLineItemPartSeqNumList);
    								}
    							}
        					// there are no the same part number in CA part,If the origItem is replaced
        					// user must extension the trade up part's term,should delete the line item
    						}else{
    							if(origItem.isReplacedPart()){
    								inValidLineItemPartSeqNumList.add(origItem.getSeqNum());
    								origLineItems.remove(origItem);
    							}
    						}


    					}
    					// delete the line item from the original line items.
    					if(inValidLineItemPartSeqNumList.size() > 0){
							QuoteLineItemFactory.singleton().delInvalidLineItemsByWebQuoteNum(ppc.getWebQuoteNum(), inValidLineItemPartSeqNumList);
    					}
    				}
    			}


    		}
    	}
    		// process every partsPricingConfigrtns if the term extension flag is true

    		//CotermParameter processForCoterm = processForCoterm(saasLineItems,removeLineItems);

	}



    private void processSameQtyLineItem(QuoteLineItem origItem,List<QuoteLineItem> origLineItems,
			List inValidLineItemPartSeqNumList) {
    	QuoteLineItem overageLineItem = null;
		for(int i=0;i<origLineItems.size();i++){
			QuoteLineItem item = origLineItems.get(i);
			if(origItem.getDestSeqNum() == item.getIRelatedLineItmNum()){
				inValidLineItemPartSeqNumList.add(item.getSeqNum());
				origLineItems.remove(item);
				if(item.isSaasSubscrptnOvragePart()){
					overageLineItem = item;
				}
			}
		}

		if(overageLineItem != null){
			for(int i=0;i<origLineItems.size();i++){
				QuoteLineItem item = origLineItems.get(i);
				if(overageLineItem.getIRelatedLineItmNum() == item.getDestSeqNum() && item.isReplacedPart()){
					inValidLineItemPartSeqNumList.add(item.getSeqNum());
					origLineItems.remove(item);
				}
			}
		}

	}

	private Integer getMaxDesNumber(List saasLineItems) {
    	Integer returnValue = 0;
    	for(int i =0; i<saasLineItems.size();i++){
    		QuoteLineItem item = (QuoteLineItem) saasLineItems.get(i);
    		if(item.getDestSeqNum() >= returnValue){
    			returnValue = item.getDestSeqNum();
    		}
    	}
		return returnValue;
	}

	private List<QuoteLineItem> findReplacedLineItems(List saasLineItems) {
    	List<QuoteLineItem> list = new ArrayList<QuoteLineItem>();
    	for(int i =0; i<saasLineItems.size();i++){
    		QuoteLineItem item = (QuoteLineItem) saasLineItems.get(i);
    		if(item.isReplacedPart()){
    			list.add(item);
    		}
    	}
		return list;
	}

	private Map<String, QuoteLineItem> toMap(List<QuoteLineItem> list){
		Map<String, QuoteLineItem> map = new HashMap<String, QuoteLineItem>();

		for(QuoteLineItem item : list){
			map.put(item.getPartNum(), item);
		}
		return map;
	}

	private Map<String, ConfiguratorPart> toConfiguratorPartMap(List<ConfiguratorPart> configuratorParts){
		Map<String, ConfiguratorPart> map = new HashMap<String, ConfiguratorPart>();

		for(ConfiguratorPart part : configuratorParts){
			map.put(part.getPartNum(), part);
		}
		return map;
	}

	public List<ConfiguratorPart> getSubPartsFromChrgAgrm(String chrgAgrmtNum,
			String configId) throws QuoteException {
		List<ConfiguratorPart> configrtrPartList = new ArrayList<ConfiguratorPart>();
		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", chrgAgrmtNum);

		if (StringUtils.isNotBlank(configId)) {
			params.put("piConfigId", configId);
		}
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(
					CommonDBConstants.DB2_S_QT_GET_CA_SUB_PARTS, null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_GET_CA_SUB_PARTS, params);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(1);

			logContext.info(this, "the return code of calling " + sqlQuery
					+ " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			rs = ps.getResultSet();
			while (rs.next()) {
				ConfiguratorPart part = new ConfiguratorPart();
				configrtrPartList.add(part);
				part.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));
				part.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
				part.setEndDate(rs.getDate("END_DATE"));
				part.setRenewalEndDate(rs.getDate("RENWL_END_DATE"));
				part.setSapMatlTypeCode(StringUtils.trim(rs
						.getString("SAP_MATL_TYPE_CODE")));
				part.setConfigrtnId(StringUtils.trim(rs
						.getString("CONFIGRTN_ID")));
				part.setCotermEndDate(rs.getDate("COTERM_END_DATE"));
				part.setSapBillgFrqncyOptCode(StringUtils.trim(rs
						.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
				part.setNextRenwlDate(rs.getDate("NEXT_RENWL_DATE"));
				part.setRenwlTermMths(rs.getInt("RENWL_TERM_MTHS"));
				part.setRenwlMdlCode(StringUtils.trim(rs
						.getString("RENWL_MDL_CODE")));
				//13.4kenexa
				part.setSubsumedSubscrptn("1".equals(StringUtils.trim(rs
						.getString("SUB_SUMED_SCRIPTION_FLAG"))));
			}
		} catch (SQLException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		}finally{
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
		}

		return configrtrPartList;
	}

	private QuoteLineItem createQli(String configrtnId,
			String webQuoteNum, String userId, ConfiguratorPart part,
			List SaaSLineItems) throws TopazException {
		logContext.log(this,"part is null ? " + (part == null));
		QuoteHeader quoteHeader = QuoteHeaderFactory.singleton()
				.findByWebQuoteNum(webQuoteNum);

		QuoteLineItem qli = QuoteLineItemFactory.singleton()
				.createQuoteLineItem(webQuoteNum, part.getPartNum(), userId);

		int destObjSeqNum = 0;
		for (Object obj : SaaSLineItems) {
			QuoteLineItem lineItem = (QuoteLineItem) obj;
			if (lineItem.isReplacedPart()) {
				continue;
			}
			if (lineItem.getPartNum().equals(part.getPartNum())) {
				qli.setOverrideUnitPrc(lineItem.getOverrideUnitPrc());
				qli.setOvrrdExtPrice(lineItem.getOvrrdExtPrice());
				qli.setLocalExtProratedDiscPrc(lineItem.getOvrrdExtPrice());
				qli.setLineDiscPct(lineItem.getLineDiscPct());
				qli.setManualSortSeqNum(lineItem.getManualSortSeqNum());
				qli.setSwSubId(lineItem.getSwSubId());
				qli.setRevnStrmCodeDesc(lineItem.getRevnStrmCodeDesc());
				qli.setSeqNum(lineItem.getSeqNum());
				qli.setChnlOvrrdDiscPct(lineItem.getChnlOvrrdDiscPct());
				break;
			}
		}
		//todo
		qli.setRevnStrmCode("SSSMA");

		destObjSeqNum++;
		qli.setSwProdBrandCode(part.getSwProdBrandCode());
		qli.setProrateFlag(false);
		qli.setAssocdLicPartFlag(false);
		qli.setConfigrtnId(configrtnId);
		qli.setPartDispAttr(new PartDisplayAttr(qli));

		// Every part will get a dest seq num
		qli.setDestSeqNum(destObjSeqNum);
		//qli.setIRelatedLineItmNum(getRelatedSeqNum(part.getRelatedSeqNum(),
		//		baseDestObjNum));
		qli.setPartQty(part.getPartQty());
		qli.setBillgFrqncyCode(part.getBillingFrequencyCode());
		qli.setCumCvrageTerm(part.getTotalTerm());
		qli.setICvrageTerm(part.getTerm());
		qli.setRefDocLineNum(part.getRefDocLineNum());
		qli.setRelatedCotermLineItmNum(part.getRelatedCotermLineItmNum());
		qli.setRampUp(part.isRampUp());
		qli.setReplacedPart(part.isPartReplaced());

		// FCT TO PA Finalization
		qli.setOrignlSalesOrdRefNum(part.getOrignlSalesOrdRefNum());
		qli.setOrignlConfigrtnId(part.getOrignlConfigrtnId());

		if (part.isPartReplaced()) {
			if (quoteHeader.isChannelQuote()) {
				if (part.isOvrage() || part.isAddiSetUp() || part.isOnDemand()) {
					qli.setLocalUnitProratedPrc(null);
					qli.setLocalUnitProratedDiscPrc(part
							.getLocalSaasOvrageAmt());
					qli.setChannelUnitPrice(part.getLocalSaasOvrageAmt());
				}
				if (part.isSubscrptn()) {
					qli.setLocalExtPrc(null);
					qli.setLocalExtProratedPrc(null);
					qli.setLocalExtProratedDiscPrc(part.getLocalExtndPrice());
					qli.setSaasBidTCV(part.getSaasTotCmmtmtVal());
					qli.setChannelExtndPrice(part.getLocalExtndPrice());
					qli.setSaasBpTCV(part.getSaasTotCmmtmtVal());
				}
			} else {
				if (part.isOvrage() || part.isAddiSetUp() || part.isOnDemand()) {
					qli.setLocalUnitProratedPrc(null);
					qli.setLocalUnitProratedDiscPrc(part
							.getLocalSaasOvrageAmt());
				}
				if (part.isSubscrptn()) {
					qli.setLocalExtPrc(null);
					qli.setLocalExtProratedPrc(null);
					qli.setLocalExtProratedDiscPrc(part.getLocalExtndPrice());
					qli.setSaasBidTCV(part.getSaasTotCmmtmtVal());
					// PL: 421951,fixed the total price missed. The detail
					// infomation
					// Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/FBF6BC9B291B7A9485257AD2005C4E3A
					qli.setChannelExtndPrice(part.getLocalExtndPrice());
					qli.setSaasBpTCV(part.getSaasTotCmmtmtVal());
				}
			}
		}
		return qli;
	}

	private Integer getRelatedSeqNum(Integer base, int increase){
		if(base == null){
			return PartRelNumAndTypeDefault.RELATED_LINE_ITM_NUM_DEFAULT;
		}

		return base + increase;
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

/*    public void calculateDate(Quote quote) throws QuoteException {

        try {

            DateCalculator calculator = DateCalculator.create(quote);
            calculator.calculateDate();
            calculator.setLineItemDates();

        } catch (TopazException e) {

            logContext.error(this, "Calculate Date error,err=" + e.getMessage());
            throw new QuoteException(e);
        }

    }*/

    /*public List calculateDateWithNoUpdate(Quote quote) throws QuoteException {

        DateCalculator calculator = DateCalculator.create(quote);
        calculator.calculateDate();
        return calculator.getDateResult();

    }*/

    /*public boolean calculatePrices(Quote quote) throws QuoteException {

        try {
            PriceCalculator calculator = new PriceCalculator(quote);
            return calculator.calculateDefaultPrice();

        } catch (TopazException e) {
            logContext.error(this, "Calculate Price error,err=" + e.getMessage());
            throw new QuoteException(e);
        }

    }*/

    public QuoteLineItem findMasterLineItem(Quote quote, String partNum, int seqNum) throws QuoteException {
        List masterLineItems = null;
        try {
            masterLineItems = QuoteLineItemFactory.singleton().findMasterLineItemsByWebQuoteNum(
                    quote.getQuoteHeader().getWebQuoteNum());
        } catch (TopazException e) {
            throw new QuoteException(e);
        }
        QuoteLineItem masterLineItem = null;
        for (int i = 0; i < masterLineItems.size(); i++) {
            masterLineItem = (QuoteLineItem) masterLineItems.get(i);
            if (partNum.equals(masterLineItem.getPartNum()) && masterLineItem.getSeqNum() == seqNum) {
                return masterLineItem;
            }
        }
        return null;
    }

    @Override
	public void changeAdditionalMaint(String webQuoteNum, String partNum, int seqNum, int additionalYears,
            Integer partQty, int manualSortSeqNum, Double dOverrideUnitprice, double discPct, boolean prorationFlag, String userID)
            throws QuoteException {
        try {
            String spName = CommonDBConstants.DB2_U_QT_PART_ADD_YRS;
            HashMap params = new HashMap();
            Double iDiscPct = null;

            iDiscPct = new Double(discPct);

            params.put("piWebQuoteNum", webQuoteNum);
            params.put("piPartNum", partNum);
            params.put("piPartSeqNum", new Integer(seqNum));
            params.put("piAddYears", new Integer(additionalYears));
            params.put("piQty", partQty);
            params.put("piManualSeqNum", new Integer(manualSortSeqNum));
            params.put("piOverrideUnitprice", dOverrideUnitprice);
            params.put("piDiscPct", iDiscPct);
            params.put("piProrate", prorationFlag ? new Integer(1) : new Integer(0));
            params.put("piUserID", userID);

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            queryCtx.completeStatement(ps, spName, params);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
            	if(CommonDBConstants.DB2_SP_RETURN_PART_INPUT_INVALID == retStatus){
            		logContext.info(this, "retStatus = " + retStatus+ ", Invalid part number:"+partNum);
            	}else{
            		logContext.error(this, "retStatus = " + retStatus);
            		throw new TopazException("exeute sp failed:" + sqlQuery);
            	}
            }
        } catch (Exception e) {

            logContext.error(this, e.getMessage());
            throw new QuoteException(e);

        }

    }

    @Override
	public void applyPartnerDiscount(PostPartPriceTabContract contract) throws QuoteException {
        this.beginTransaction();
        String userId = contract.getUserId();
        try {
            Quote quote = innerPostPartPriceInfo(contract);
            boolean isChannel = quote.getQuoteHeader().isChannelQuote();

            for( Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
            	QuoteLineItem qli = (QuoteLineItem)iter.next();
            	//clear all business partner override discounts on each line items

            	if(StringUtils.isBlank(contract.getPartnerDiscountPercent())){
            	    qli.setChnlOvrrdDiscPct(null);
            	}
    	        else {
    	        	if(qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwDailyPart()){
    	        		// do nothing
    	        	}
    	        	//exclude the saas daily part and replace part from apply BP discount
    	        	else if(!qli.isSaasDaily() && !qli.isReplacedPart()){
    	        		qli.setChnlOvrrdDiscPct(Double.valueOf(contract.getPartnerDiscountPercent()));
    	        	}
    	        }
            }

            // re-set special bid flag
            validateSpecialBidRule(quote, userId);

            // always set re-calculate flag to true
            quote.getQuoteHeader().setRecalcPrcFlag(1);

            // update quote header
            updateQuoteHeader(quote.getQuoteHeader(), userId);

            commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        long end = System.currentTimeMillis();
        logContext.debug(this, "apply discount");
    }

    private void validateSpecialBidRule(Quote quote, String userId) throws TopazException, QuoteException{
        boolean prevShouldAutoSetChnlDisc = QuoteCommonUtil.shouldSetELAAutoChnlDisc(quote);

        QuoteHeader header = quote.getQuoteHeader();
        SpecialBidRule rule = SpecialBidRule.create(quote);
        header.setSpeclBidSystemInitFlag(rule.validate(userId)?1:0);
//      Add new field: approvalRouteFlag on 5/15/2008
        quote.getQuoteHeader().setApprovalRouteFlag(rule.getApprovalRouteFlagAsInt());

        //07/16/2009 persist special bid reasons to DB
        QuoteReasonFactory.singleton().updateSpecialBidReason(header.getWebQuoteNum(), rule.getSpecialBidReason(), userId);

        //the auto set BP discounts are always cleared during ValidationRule.validateBpOverrideDiscount
        //restore them if should so that doesn't result in too many unnecessary pricing calls
        List items = quote.getLineItemList();
        if(items == null || items.size() == 0){
            return;
        }

        if(QuoteCommonUtil.shouldSetELAAutoChnlDisc(quote)){
            //before now, there should be not set auto channel discount
            if(!prevShouldAutoSetChnlDisc){
                header.setRecalcPrcFlag(1);
            }

            Double chnlDisc = PartPriceConfigFactory.singleton().getAutoChnlDiscOvrd(
                                               header.getCountry().getSpecialBidAreaCode());
            for(Iterator it = items.iterator(); it.hasNext(); ){
                QuoteLineItem qli = (QuoteLineItem)it.next();
                qli.setChnlOvrrdDiscPct(chnlDisc);
            }
        } else if(!PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote)){

            if(prevShouldAutoSetChnlDisc){
                header.setRecalcPrcFlag(1);
            }
            for(Iterator it = items.iterator(); it.hasNext(); ){
                QuoteLineItem qli = (QuoteLineItem)it.next();
                qli.setChnlOvrrdDiscPct(null);
            }
        }
    }


    /**
     *
     * @param contract
     * @param webQuoteNumber
     * @throws QuoteException
     */
    private void  processRenewalModel(PostPartPriceTabContract contract ,String webQuoteNumber)throws QuoteException{
    	Quote quote = contract.getQuote();
    	StringBuffer renwlModelStr = new StringBuffer("");
    	StringBuffer configLevelRenwlModStr = new StringBuffer("");
    	Map<String,String> brandCodeMap = quote.getSaasBrandCodeMap();
    	Map<String,List<PartsPricingConfiguration>> brandMap =  quote.getSaasBrandMap();
    	Map configrtnsMap =quote.getPartsPricingConfigrtnsMap();
    	for (Object element : brandCodeMap.keySet()) {
             String brandCode = (String) element;
             List configrtnsList = brandMap.get(brandCode);
             Iterator configrtnsIt = configrtnsList.iterator();
             while(configrtnsIt.hasNext()){
            	PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
            	//join configuration level info
            	if (ppc.isAddOnTradeUp()){
            		configLevelRenwlModStr.append(joinConfigLevelRewalModStr(contract,ppc.getConfigrtnId()));
            	}

            	List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
				if (subSaaSlineItemList != null && subSaaSlineItemList.size()>0){
					// join renewal model info
					renwlModelStr.append(joinPartsRenwlModelStr(subSaaSlineItemList,contract,ppc.getConfigrtnId()));
				}
             }
    	 }
    	
    	// add for monthly sw part
    	List monthlySWconfigrtnsList = quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
    	Map monthlySWconfigrtnMap = quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();
    	Iterator configrtnsIt = monthlySWconfigrtnsList.iterator();
    	while(configrtnsIt.hasNext()){
    		MonthlySoftwareConfiguration mppc = (MonthlySoftwareConfiguration) configrtnsIt.next();
    		if (mppc.isAddOnTradeUp()){
    			configLevelRenwlModStr.append(joinConfigLevelRewalModStr(contract,mppc.getConfigrtnId()));
    		}
    		
    		List monthlyLineItemList = (List)monthlySWconfigrtnMap.get(mppc);
    		if (monthlyLineItemList !=null && monthlyLineItemList.size()>0){
    			// join renewal model info
				renwlModelStr.append(joinPartsRenwlModelStr(monthlyLineItemList,contract,mppc.getConfigrtnId()));
    		}
    	}
    	
        if (StringUtils.isNotBlank(renwlModelStr.toString())){
        	 addRenwlModel(contract.getUserId(),renwlModelStr.toString(),webQuoteNumber,configLevelRenwlModStr.toString(), QuoteConstants.SourceOfRenewalModel.FROM_WEB);
        }
      }


    /**
     * join renewal model info  to into table
     * LINE_ITEM_SEQ_NUM:RENWL_MDL_CODE:RENWL_MDL_CODE_LVL;
     * example  10:T:CONFIG;20:T:LINE;
     * @param subSaasLineItemList  All  part in configuration
     * @param ct
     * @param configurtnId
     * @return
     */
	private String joinPartsRenwlModelStr(List subSaasLineItemList,
			PostPartPriceTabContract ct, String configurtnId) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : subSaasLineItemList) {
			QuoteLineItem qli = (QuoteLineItem) obj;
			String key = qli.getPartNum() + "_" + qli.getSeqNum();

			if ((qli.isSaasSubscrptnPart()||(qli.isMonthlySoftwarePart()&& ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()))
					&& !qli.isReplacedPart() && qli.isSupportRenwlMdl()) {
				String subscriptnRewalModCode = StringUtils.trimToEmpty(ct
						.getRenwlModeSubscrptn(key));
				String configurtnRewalModCode = StringUtils.trimToEmpty(ct
						.getRenwlModeConfigutn(configurtnId));
				String rewalModCodeLevel = PartPriceConstants.RenewalModelCode.LINE_RENWL_MDL_CODE_LEVEL;

				// subscript renewal model "use configuration renewal model"
				if (StringUtils.isBlank(subscriptnRewalModCode)) {
					subscriptnRewalModCode = configurtnRewalModCode;
				}

				// use configuration level
				if (subscriptnRewalModCode.equals(configurtnRewalModCode)) {
					rewalModCodeLevel = PartPriceConstants.RenewalModelCode.CONFIG_RENWL_MDL_CODE_LEVEL;
				}

				// configuration renewal model is "select one" and subscription
				// renewal model is "use configuration renewal model"
//				if (StringUtils.isBlank(configurtnRewalModCode)
//						&& StringUtils.isBlank(subscriptnRewalModCode)) {
//					rewalModCodeLevel = "";
//				}

				// fixed model process
				if (qli.isFixedRenwlMdl()) {
					subscriptnRewalModCode = qli.getRenwlMdlCode();
					rewalModCodeLevel = "";
				}

				sb.append(qli.getSeqNum());
				sb.append(":");
				sb.append(subscriptnRewalModCode);
				sb.append(":");
				sb.append(rewalModCodeLevel);
				sb.append(";");
			}
		}
		return sb.toString();
	}

    /**
     * join configuration level renewal model info to into table (use AddOn/tradeUp)
     *  5725F6720130120:C;5725F6520130120:C;
     *  purpose: when update configuration level renewal model,
     *  All parts in this configuration also need to be updated.
     *  Including parts did not show on the web (AddOn/tradeUp)
     * @param ct
     * @param configurtnId
     * @return
     */
    private String joinConfigLevelRewalModStr(PostPartPriceTabContract ct,String configurtnId){
    	StringBuffer sb = new StringBuffer("");
    	sb.append(configurtnId);
    	sb.append(":");
    	sb.append(StringUtils.trimToEmpty(ct.getRenwlModeConfigutn(configurtnId)));
    	sb.append(";");
    	return sb.toString();
    }

	@Override
	public void applyYtyGrowthDelegation(PostPartPriceTabContract contract) throws QuoteException {
		this.beginTransaction();
		String userId = contract.getUserId();

        try {
            Quote quote = innerPostPartPriceInfo(contract);

//            for( Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
//            	QuoteLineItem qli = (QuoteLineItem)iter.next();
//
//            	if(GrowthDelegationUtil.isDisplayLineItemYTY(quote,qli)){
//        			doCalculateYtyGrowth(qli, contract.getQuoteYty(), "3", quote.getQuoteHeader());
//
//        			GrowthDelegationUtil.calculateDiscFromYtyGrwthPct(qli, quote.getQuoteHeader());
//            	} else {
//            		qli.setYtyGrowth(null);
//            	}
//            }

            // re-set special bid flag
            validateSpecialBidRule(quote, userId);

            // always set re-calculate flag to true
            quote.getQuoteHeader().setRecalcPrcFlag(1);

            // update quote header
            updateQuoteHeader(quote.getQuoteHeader(), userId);
            // clear setlinersvp flag
            clearSetLineToRsvp(quote);
            commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        long end = System.currentTimeMillis();
        logContext.debug(this, "apply yty growth delegation");
	}
	
	private void clearSetLineToRsvp(Quote quote){
		if(quote != null ){
			if(quote.getMasterSoftwareLineItems() != null && quote.getMasterSoftwareLineItems().size() > 0){
				for( Iterator iter= quote.getMasterSoftwareLineItems().iterator(); iter.hasNext();){
		        	QuoteLineItem qli = (QuoteLineItem)iter.next();
		        		qli.setSetLineToRsvpSrpFlag(false);
		        }
			}
		}
		
	}
	
	@Override
	public void changeBillingFrequency(PostPartPriceTabContract contract)
			throws QuoteException {
		String billgFrqncyValue = contract.getBillingFrequencyChangeSelect();
		try {
			this.beginTransaction();
            Quote quote = contract.getQuote();
            List<QuoteLineItem> changedItems = QuoteCommonUtil.findChangeAbleBillingFrequencyIineItems(quote);
            for (QuoteLineItem item : changedItems) {
    			item.setBillgFrqncyCode(billgFrqncyValue);
    			List<QuoteLineItem> rampLineItems = item.getRampUpLineItems();
    			if(rampLineItems !=null)
	    			for(QuoteLineItem rampItem : rampLineItems){
	    				rampItem.setBillgFrqncyCode(billgFrqncyValue);
	    			}
    		}
            quote.getQuoteHeader().setRecalcPrcFlag(1);
    		updateQuoteHeader(quote.getQuoteHeader(), contract.getUserId());
    		commitTransaction();
		} catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        logContext.debug(this, "change billing frequency");
	}
	
	//#appliance#99
	
	public void submitLineItemCRAD(Quote quote,UpdateLineItemCRADContract ct) throws QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		try{
			this.beginTransaction();
			tracer.stmtTraceStart("commit transaction");
	        commitTransaction();
	        tracer.stmtTraceEnd("commit transaction");
		}catch (TopazException e){
			throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        logContext.debug(this, "submitLineItemCRAD");
	}

	public Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm(String chrgAgrmtNum, String configId)  throws TopazException {
		Map<String, ConfiguratorPart> configrtrPartMap = new HashMap<String, ConfiguratorPart>();

		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", chrgAgrmtNum);
		params.put("piConfigId", configId);
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx
					.getCompletedQuery(
							CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG,
							null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG,
					params);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(4);

			logContext.info(this, "the return code of calling " + sqlQuery
					+ " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			int lineItemCount = ps.getInt(3);
			if (lineItemCount > 0) {

				rs = ps.getResultSet();
				String qtyStr = null;
				while (rs.next()) {
					String activeFlag = StringUtils.trim(rs.getString("ACTIVE_FLAG"));
					if(activeFlag.equals("0")){
						continue;
					}
					String rampUpFlag = StringUtils.trim(rs.getString("RAMP_UP_FLAG"));//rtc #224650
					if("1".equals(rampUpFlag)){
						continue;
					}
					ConfiguratorPart part = new ConfiguratorPart();

					part.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));

					qtyStr = rs.getString("PART_QTY");
					if (StringUtils.isNotBlank(qtyStr)) {
						// if qtyStr is '1.000', convert it to '1'
						qtyStr = qtyStr.substring(0,
								qtyStr.indexOf(".") < 0 ? qtyStr.length()
										: qtyStr.indexOf("."));
						part.setPartQty(Integer.parseInt(qtyStr));
					}

					part.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
					part.setTerm(rs.getObject("CVRAGE_TERM") == null ? null : rs.getInt("CVRAGE_TERM"));
					part.setBillingFrequencyCode(StringUtils.trim(rs.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
					part.setLocalSaasOvrageAmt(rs.getObject("LOCAL_SAAS_OVRAGE_AMT") == null ? null : rs.getDouble("LOCAL_SAAS_OVRAGE_AMT"));
					part.setSaasTotCmmtmtVal(rs.getObject("SAAS_TOT_CMMTMT_VAL") == null ? null : rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
					part.setLocalExtndPrice(rs.getObject("LOCAL_EXTND_PRICE") == null ? null : rs.getDouble("LOCAL_EXTND_PRICE"));
					part.setCotermEndDate(rs.getObject("COTERM_END_DATE") == null ? null : rs.getDate("COTERM_END_DATE"));
					part.setNextRenwlDate(rs.getObject("NEXT_RENWL_DATE") == null ? null : rs.getDate("NEXT_RENWL_DATE"));
					part.setOrignlSalesOrdRefNum(StringUtils.trim(rs.getString("ORIGNL_SALES_ORD_REF_NUM")));
					part.setOrignlConfigrtnId(StringUtils.trim(rs.getString("ORIGNL_CONFIGRTN_ID")));
					part.setRenwlCounter(rs.getInt("RENWL_COUNTER"));
					part.setRenwlMdlCode(StringUtils.trim(rs.getString("RENWL_MDL_CODE")));
					configrtrPartMap.put(part.getPartNum(), part);
				}
			}

		} catch (SQLException e) {
			logContext.error(this, e.getMessage());
			throw new TopazException(e);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new TopazException(e);
		}finally{
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
		}

		return configrtrPartMap;
	}
	
	private void setECRecalculateFlagForApplyOffer(QuoteHeader quoteHeader, double contractOfferPrice){
		if(quoteHeader.isECEligible() && !quoteHeader.isECRecalculateFlag()){
	    	if(quoteHeader.getOfferPrice() == null 
	    		|| contractOfferPrice != quoteHeader.getOfferPrice().doubleValue()
	    	){
	    		quoteHeader.setECRecalculateFlag(true);
	    	}
	    }
	}
	
	private void setECRecalculateFlagForApplyDisc(QuoteHeader quoteHeader, QuoteLineItem qli, double contractApplyDisc){
		if(quoteHeader.isECEligible() && !quoteHeader.isECRecalculateFlag()){
	    	if(qli.getLineDiscPct() != contractApplyDisc){
	    		quoteHeader.setECRecalculateFlag(true);
	    	}
	    	
		}
	}

	private void setOLRecalculateFlagForApplyOffer(QuoteHeader quoteHeader, double contractOfferPrice){
		if(quoteHeader.isOmittedLine() && QuoteConstants.OMIT_RECALCULATE_Y != quoteHeader.getOmittedLineRecalcFlag()){
	    	if(quoteHeader.getOfferPrice() == null 
	    		|| contractOfferPrice != quoteHeader.getOfferPrice().doubleValue()
	    	){
	    		quoteHeader.setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_Y);
	    	}
	    }
	}
	
	private void setOLRecalculateFlagForApplyDisc(QuoteHeader quoteHeader, QuoteLineItem qli, double contractApplyDisc){
		if(quoteHeader.isOmittedLine() && QuoteConstants.OMIT_RECALCULATE_Y != quoteHeader.getOmittedLineRecalcFlag()){
	    	if(qli.getLineDiscPct() != contractApplyDisc){
	    		quoteHeader.setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_Y);
	    	}
	    	
		}
	}
	
	@Override
	public void resetToRsvpSrp(PostPartPriceTabContract ct) throws QuoteException{
		this.beginTransaction();
        String userId = ct.getUserId();
        try {
            Quote quote = innerPostPartPriceInfo(ct);

            for( Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
            	QuoteLineItem qli = (QuoteLineItem)iter.next();
            	
	        	String sign = ct.getWebQuoteNum()+"_"+qli.getSeqNum()+"_"+qli.getPartNum();
	        	if( ct.getSetLineToRsvpSrpId().equals(sign)){
	        		qli.setSetLineToRsvpSrpFlag(true);
	        		qli.setOverrideUnitPrc(GrowthDelegationUtil.getProratedRSVPPrice(qli));
	        		break;
	        	}
            }

            // re-set special bid flag
            validateSpecialBidRule(quote, userId);

            // always set re-calculate flag to true
            quote.getQuoteHeader().setRecalcPrcFlag(1);

            // update quote header
            updateQuoteHeader(quote.getQuoteHeader(), userId);

            commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        long end = System.currentTimeMillis();
        logContext.debug(this, "apply reset lint to rsvp");
		
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.draftquote.process.PartPriceProcess#doCalculateDeployModel(com.ibm.dsw.quote.common.domain.
     * QuoteLineItem, com.ibm.dsw.quote.common.domain.QuoteHeader)
     */
    @Override
    public void doCalculateDeployModel(Quote quote) throws QuoteException {
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", quote.getQuoteHeader().getWebQuoteNum());
        try {

            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_LINE_ITEM_DEPLOY_MODEL, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_LINE_ITEM_DEPLOY_MODEL, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }

            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                DeployModel deployModel = new DeployModel();
                Integer quoteLineItemSeqNum = rs.getObject("QUOTE_LINE_ITEM_SEQ_NUM") == null ? null : rs
                        .getInt("QUOTE_LINE_ITEM_SEQ_NUM");
                Integer serialNumWarningFlag = rs.getObject("APPLNC_SERIAL_NUM_WARNG_FLAG") == null ? null : rs
                        .getInt("APPLNC_SERIAL_NUM_WARNG_FLAG");
                deployModel.setSerialNumWarningFlag(serialNumWarningFlag);
                updateQuoteLineItemDeployModel(quote, quoteLineItemSeqNum, deployModel);
            }


        } catch (SQLException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } 

    }

    /**
     * DOC Comment method "updateQuoteLineItemDeployModel".
     * 
     * @param quote
     * @param quoteLineItemSeqNum
     * @param deployModel
     */
    private void updateQuoteLineItemDeployModel(Quote quote, Integer quoteLineItemSeqNum, DeployModel deployModel) {
        QuoteLineItem quoteLineItem = quote.getLineItemBySeqNum(quoteLineItemSeqNum);
        if (quoteLineItem != null) {
            DeployModel oldDeployModel = quoteLineItem.getDeployModel();
            oldDeployModel.setSerialNumWarningFlag(deployModel.getSerialNumWarningFlag());
        }
    }
	@Override
	public void updateTCVs(String webQuoteNum, String configrtnIds,
			String increaseBidTCVs, String unUsedTCVs) throws QuoteException {		
		try {
			this.beginTransaction();
			updateIncreaseBidTCV(webQuoteNum,configrtnIds,increaseBidTCVs,unUsedTCVs);
			this.commitTransaction();
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
            throw new QuoteException(e);
		}
		finally{
			this.rollbackTransaction();
		}		
		
	}
    
    
	
}
