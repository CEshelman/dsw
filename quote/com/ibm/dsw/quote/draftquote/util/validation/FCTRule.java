package com.ibm.dsw.quote.draftquote.util.validation;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.exception.OfferPriceException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>FCTRule</code> is the implementation for FCT
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Mar 26, 2007
 */

class FCTRule extends SalesQuoteRule {

    FCTRule(Quote quote, PostPartPriceTabContract ct) {
        super(quote, ct);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.draftquote.util.validation.BaseRule#validate()
     */
    public void validate() throws TopazException {

        boolean additionalYearChanged = validateAdditionalMaintYears();

        // now we need reload the line items, because lineitem maybe
        // inserted/deleted when validate maint year
        if (additionalYearChanged) {
            logContext.debug(this, "One of Line item additional maint year changed, need reload the line items");
            reloadLineItems(quote);
            itemCountChanged = true;
        }

        // if user change the qty of the firt maint part, the subsequence maint
        // part also should be adjusted
        setSubSequenceMaintPartQty();

        quote.getQuoteHeader().setCmprssCvrageFlag(ct.isEnableCmprssCvrage());

        List items = quote.getLineItemList();

        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);
            String key = this.createKey(item);

            //no UI data for this line item, continue
            if(!isPartParamExist(key)){
            	continue;
            }

            logContext.debug(this, "Begin to valid " + key);
            // it's reasonable that the line item data can't be found in
            // contract
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
                validateOverrideUnitPriceAndDiscount(item);
            }


            if(!item.isSaasPart() && !item.isMonthlySoftwarePart()){
            	validatePVUPartQtyStatus(item);
                validateOverrideDate(item);
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

        validateOfferPrice();

        validatePaymentTermDays();

        validateValidityDays();

        processPrvsningDays(quote, ct);
        
        processServiceDate(quote,ct);

        processCoTerms(quote, ct);

        processEstmtdOrdDate(quote, ct);
        
        processRenewalFlagMigrationFlag(quote,ct);

    }
    
    protected void processRenewalFlagMigrationFlag(Quote quote,PostPartPriceTabContract ct){
    	List items = quote.getLineItemList();
        for (int i = 0; i < items.size(); i++) {
        	 QuoteLineItem item = (QuoteLineItem) items.get(i);
        	 String itemKey =  this.createKey(item);
        	 LineItemParameter li = (LineItemParameter) ct.getItems().get(itemKey);
//        	 if( is a new service=null or is a new service='Y'){
//        		    Line_item_migration=0;
//        			Line_item_renewal=0;
//        		}else{
//        		//not a new service 
//        		    if(Header_is this a legacy= Y ){
//        			     Line_item_migration=1;
//        			}
//        		    if(Header_is_this contract_expireing= Y){
//        			     Line_item_renewal=1;
//        			}
//        		}
        	 
        	 if (item.isSaasPart()){
 
        	 }
        }
    }

    protected boolean shouldValidateOvrrdUnitPriceAndDiscount(QuoteLineItem qli){
        if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
        	PartPriceSaaSPartConfigFactory conf = PartPriceSaaSPartConfigFactory.singleton();

        	return (conf.canInputExtndOvrrdPrice(qli)
        			|| conf.canInputUnitOvrrdPrice(qli)
        			|| conf.canInputDiscount(qli));
        } else {
        	if(qli.isObsoletePart()){
                return true;
            }

            String lob = quote.getQuoteHeader().getLob().getCode();
            String partTypeCode = qli.getPartTypeCode();
            return PartPriceConfigFactory.singleton().allowOvrdUnitPriceOrDisc(lob,partTypeCode);
        }
    }

    private boolean shouldCheckOfferPrice(){
		if((StringUtils.isBlank(ct.getOfferPrice())
		        && quote.getQuoteHeader().getOfferPrice() == null)
		        || ct.getOfferAction() == PartPriceConstants.OfferPriceAction.CLEAR_OFFER_PRICE_ACTION){
		    return false;
		}

		return true;
    }

    /**
     * validate user entered offer price acceptable
     */
    protected void validateOfferPrice() {

        //calculate part total quanity
        if(!shouldCheckOfferPrice()){
            return;
        }
        List items = quote.getLineItemList();
        int totalQuanity = 0;
        double totalExclusion = 0d;
        for (int i = 0; i < items.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) items.get(i);

    		if((item.isSaasPart() || item.isMonthlySoftwarePart()) && !PartPriceSaaSPartConfigFactory.singleton()
    				.showBidExtndPrice(item)){
    			continue;
    		}

            if(!QuoteCommonUtil.shouldIncludeInOfferPrice(quote, item)){
                if(item.getLocalExtProratedDiscPrc() != null){
                    totalExclusion += item.getLocalExtProratedDiscPrc().doubleValue();
                }
                continue;
            }

            if(item.getPartQty() != null){
                totalQuanity+=item.getPartQty().intValue();
            }
        }

        double miniumOfferPrice = PartPriceConstants.MINIUM_UNIT_PRICE * totalQuanity;
        double offerPrice=0d;

        if(ct.getOfferAction() == PartPriceConstants.OfferPriceAction.APPLY_OFFER_PRICE_ACTION){
            offerPrice = Double.parseDouble(ct.getOfferPrice());
        }else if(quote.getQuoteHeader().getOfferPrice() != null){
            //if offer price from contract is null, try to check the existing value from db
            //if not null then means user ever applied offer price, that value may not apply with this request
            //user may adjust part quantity/additional year etc which may cause the applied offer price doesn't work agagin
            //for this case we also need to check if it matches with minium
            offerPrice = quote.getQuoteHeader().getOfferPrice().doubleValue();
        }else{
            return;
        }

        offerPrice=offerPrice-totalExclusion;
        if(offerPrice < miniumOfferPrice){
            throw new OfferPriceException(ErrorKeys.MSG_OFFER_PRICE_ERR);
        }
    }

    protected void reloadLineItems(Quote quote) throws TopazException {
        try {
        	List sLineItems = quote.getLineItemList();
            List lineItems = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    quote.getQuoteHeader().getWebQuoteNum());
            if(sLineItems.size() > lineItems.size()){            	
            	for(int i=0;i<sLineItems.size();i++){
            		boolean isFound = false;
            		QuoteLineItem slineItem = (QuoteLineItem) sLineItems.get(i);
            		if(slineItem.getYtyGrowth() != null){
            			for(int j=0;j<lineItems.size();j++){
            				QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(j);
            				if(lineItem.getSeqNum() == slineItem.getSeqNum()){
            					isFound = true;
            					break;
            				}
            			}
                		if(!isFound){
                			slineItem.getYtyGrowth().delete();
                		}
            		}
            	}
            }

            quote.setLineItemList(lineItems);
            quote.setMasterSoftwareLineItems(null);
            for (int i = 0; i < lineItems.size(); i++) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
                String key = this.createKey(lineItem);
                //only add default validation result for new line item
                if (validationResults.get(key) == null) {
                    validationResults.put(key, new ValidationResult());
                }
            }

        } catch (TopazException te) {
            throw te;
        } catch (Exception ex) {
            throw new TopazException("An unexpected error occurred while getting line items. Cause: " + ex);
        }
    }

    /**
     * @param item
     */
    protected boolean validateAdditionalMaintYears() throws TopazException {
        boolean additionalYearChanged = false;

        List masterLineItems = quote.getMasterSoftwareLineItems();

        if (masterLineItems == null) {
            masterLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList());

        }

        PartPriceProcess process = null;
        try {
            process = PartPriceProcessFactory.singleton().create();
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());

        }

        for (int i = 0; i < masterLineItems.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
            String key = createKey(item);

            if(!isPartParamExist(key)){
            	continue;
            }

            int additionalMaintYears = ct.getMaintainAdditionalYearInteger(key);
            if (-1 == additionalMaintYears) {
                // should be a error
                continue;
            }
            if (item.getAddtnlMaintCvrageQty() == additionalMaintYears) {
                continue;
            } else {
                ValidationResult vr = this.getValidationResult(key);
                vr.additionalMaintYearsChanged = true;
                additionalYearChanged = true;

                LineItemParameter lineItem = (LineItemParameter) ct.getItems().get(key);

                String partNum = this.parsePartNum(lineItem);

                int seqNum = this.parseSeqNum(lineItem);

                Integer qty = null;
                try {

                    qty = Integer.valueOf(lineItem.quantity);

                } catch (Throwable e) {
                	logContext.error(this, e.getMessage());
                }

                int sortOrder = -1;
                try {
                    sortOrder = Integer.valueOf(lineItem.manualSortOrder).intValue();
                } catch (Throwable e) {
                	logContext.debug(this, e.getMessage());
                }

                Double oup = null;
                try {
                    oup = Double.valueOf(lineItem.overridePrice);
                } catch (Throwable e) {
                	logContext.debug(this, e.getMessage());
                }

                double discount = 0.0d;
                try {
                    discount = Double.valueOf(lineItem.discountPercent).doubleValue();
                } catch (Throwable e) {
                	logContext.debug(this, e.getMessage());
                }

                try {
                    process.changeAdditionalMaint(quote.getQuoteHeader().getWebQuoteNum(), partNum, seqNum,
                            additionalMaintYears, qty, sortOrder, oup, discount, lineItem.prorateFirstYearToAnni, ct.getUserId());
                } catch (QuoteException e1) {
                    logContext.fatal(this, "Can't change Additional Maint for " + lineItem.key);
                }

            }
        }
        return additionalYearChanged;

    }

    protected int parseSeqNum(LineItemParameter lineItem) {
        int pos = lineItem.key.indexOf("_");
        return Integer.valueOf(lineItem.key.substring(pos + 1)).intValue();
    }

    /*
     * protected void processAdditionalMaintYears() {
     *
     * QuoteHeader header = quote.getQuoteHeader();
     *
     * //This is only for PA and PAE draft quote if (header.isSalesQuote() &&
     * (header.isPAQuote() || header.isPAEQuote())) {
     *
     * PartPriceProcess process = null; try { process =
     * PartPriceProcessFactory.singleton().create(); } catch (QuoteException e) {
     * logContext.fatal(this, e.getMessage()); }
     *
     * Iterator iter = ct.getItems().values().iterator();
     *
     * while (iter.hasNext()) {
     *
     * LineItemParameter lineItem = (LineItemParameter) iter.next();
     *
     * int additionalMaintYears = -1; try {
     *
     * additionalMaintYears =
     * Integer.valueOf(lineItem.maintainAddtionalYear).intValue(); } catch
     * (Throwable e) { //Do nothing }
     *
     * if (additionalMaintYears != -1) { } } } }
     */
    protected String parsePartNum(LineItemParameter lineItem) {
        int pos = lineItem.key.indexOf("_");
        return lineItem.key.substring(0, pos);

    }

    protected void validateLineItemBackDating() throws TopazException {
    	QuoteHeader header = quote.getQuoteHeader();

        List masterItems = CommonServiceUtil.getSoftwareLineItemList(quote.getLineItemList());
        List subItems = null;

        if (masterItems == null || masterItems.size() == 0) {
        	//no line item is on the quote, clear every thing
        	header.setBackDatingFlag(false);
        	header.setReasonCodes(null);
        	header.setBackDatingComment(null);

            logContext.debug(this, "Master line item is empty, clear back dating flag, reason code and comment");
            return;
        }


        boolean itemBackDtgFlg = false;
        boolean quoteBackDtgFlg = false;

        boolean cmprssCvrageEnabled = quote.getQuoteHeader().getCmprssCvrageFlag();
        for (Iterator masterIt = masterItems.iterator(); masterIt.hasNext();) {

            QuoteLineItem masterItem = (QuoteLineItem) masterIt.next();

            //Skip back dating validation from cmprss cvrage line item
            if(cmprssCvrageEnabled && masterItem.isEligibleForCmprssCvrage()){
            	logDebug("Line item " + masterItem.getPartNum()
                            + "_" + masterItem.getSeqNum() + " is eligiable for cmprss cvrage, skip back dating validation");
                continue;
            }

        	if(masterItem.getPartDispAttr().isFromRQ()){
            	logDebug("Line item " + masterItem.getPartNum()
                        + "_" + masterItem.getSeqNum() + " is renewal, skip back dating validation");
        	    continue;
        	}

        	itemBackDtgFlg = masterItem.isItemBackDated();

            logContext.debug(this, "Back dating flag for asd item " + masterItem.getPartNum()
            		                   + "_" + masterItem.getSeqNum() + ": " + itemBackDtgFlg);
            masterItem.setBackDatingFlag(itemBackDtgFlg);

            quoteBackDtgFlg = (quoteBackDtgFlg || itemBackDtgFlg);

            subItems = masterItem.getAddtnlYearCvrageLineItems();
            if(subItems == null || subItems.size() == 0){
            	continue;
            }

            logContext.debug(this, "set back dating flag of additional maint coverage for line item "
            		               + masterItem.getPartNum() + "_" + masterItem.getSeqNum() + " to " + itemBackDtgFlg);

            for(Iterator subIt = subItems.iterator(); subIt.hasNext(); ){
            	QuoteLineItem subItem = (QuoteLineItem)subIt.next();
            	subItem.setBackDatingFlag(itemBackDtgFlg);
            }
        }

        logContext.debug(this, "quote[" + header.getWebQuoteNum()
        		                    + "] header back dating flag set to " + quoteBackDtgFlg);
        header.setBackDatingFlag(quoteBackDtgFlg);

        if (quoteBackDtgFlg) {
        	logContext.debug(this, "quote[" + header.getWebQuoteNum()
        			                + "] back dating reason codes[" + ct.getReasonCodes()
									+ "], back dating comment[" + ct.getBackDatingComment() + "]");
        	header.setReasonCodes(ct.getReasonCodes());
        	header.setBackDatingComment(ct.getBackDatingComment());
        } else {
        	logContext.debug(this, "No back dating items, clear back dating reason code and comment");
        	header.setReasonCodes(null);
        	header.setBackDatingComment(null);
        }
    }

    protected void setSubSequenceMaintPartQty() {

        List masterLineItems = quote.getMasterSoftwareLineItems();

        if (masterLineItems == null || masterLineItems.size()==0) {
            masterLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList());
            quote.setMasterSoftwareLineItems(masterLineItems);
        }

        for (int i = 0; i < masterLineItems.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
            String key = createKey(item);
            if(!isPartParamExist(key)){
            	continue;
            }

            String masterQty = ct.getPartQty(key);
            int additionalMaintYearSeqNum = ct.getAdditionalMaintSeqNum(key);

            logContext.debug(this, "Master line item ,link to " + additionalMaintYearSeqNum);

            if (item.getAddtnlMaintCvrageQty() == 0) {
                logContext.debug(this, "This line item has no additional maint " + key);
                continue;
            }

            List subLineItems = item.getAddtnlYearCvrageLineItems();
            for (int j = 0; j < subLineItems.size(); j++) {

                QuoteLineItem subItem = (QuoteLineItem) subLineItems.get(j);

                String subKey = this.createKey(subItem);

                if (!isPartParamExist(subKey)) {
                    logContext.debug(this, "The line item is not in Contract, it maybe added from store procedure");
                    continue;
                }
                ct.setPartQty(subKey, masterQty);
            }
        }

    }

    /*
     * protected void validateAdditionalMaintYears(QuoteLineItem item) throws
     * TopazException { String key = createKey(item); ValidationResult vr =
     * getValidationResult(key); int years = -1; try { years =
     * ct.getMaintainAdditionalYearInteger(key); } catch (Throwable e) {
     * writeLog("Additional Maint Years", item, e); return; } if
     * (item.getAddtnlMaintCvrageQty() != years) {
     * item.setAddtnlMaintCvrageQty(years); vr.additionalMaintYearsChanged =
     * true; } }
     */
    protected void validateStartDate(QuoteLineItem item) throws TopazException {
        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);
        Date startDate = ct.getPartStartDate(key);

        //for some case , the revenue stream code is not in the list
        if ((null == startDate) || (null == item.getMaintStartDate())) {
            return;
        }
        Date prevStartDate = item.getMaintStartDate();

        if (!item.getMaintStartDate().equals(startDate)) {

            item.setMaintStartDate(startDate);
            if (!DateUtil.isYMDEqual(startDate, item.getPartDispAttr().getStdStartDate())) {
                item.setStartDtOvrrdFlg(true);
            }
            vr.dateChanged = true;

        }

    }

    protected void validateEndDate(QuoteLineItem item) throws TopazException {
        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);
        Date endDate = ct.getPartEndDate(key);

        if ((null == endDate) || (null == item.getMaintEndDate())) {
            return;
        }
        if (!item.getMaintEndDate().equals(endDate)) {
            item.setPrevsEndDate(item.getMaintEndDate());
            item.setMaintEndDate(endDate);
            if (!DateUtil.isYMDEqual(endDate, item.getPartDispAttr().getStdEndDate())) {
                item.setEndDtOvrrdFlg(true);
            }
            vr.dateChanged = true;
        }
    }

    private void logDebug(String debugMsg){
    	if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
				logContext.debug(this, debugMsg);
			}
		}
	 }
    
    
 	//Appliance#99
    protected void sharelineItemCRADWithApplncId()  throws TopazException{
 	   List items = quote.getLineItemList();

        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);
            String key = this.createKey(item);
            if(item.isApplncPart()) {
            	if (QuoteCommonUtil.isMainApplncOrUpgradeApplnc(item)){
    			} else if (QuoteCommonUtil.isAssociated(item)){
    				item.setLineItemCRAD(getAssociatedLineItemCRAD(item.getConfigrtnId()));
    			} 
        	}
        }
            
    }

 	// get associated main part's lineItemCRAD for associated appliance
 	private java.sql.Date getAssociatedLineItemCRAD(String configId){
 		for(Object obj: quote.getLineItemList()){
 			QuoteLineItem quoteLineItem = (QuoteLineItem)obj;
 			if (quoteLineItem.isApplncMain() && quoteLineItem.getConfigrtnId().equals(configId)){
 				
 				return quoteLineItem.getLineItemCRAD();
 			}
 		}
 		return null;
 	}
}
