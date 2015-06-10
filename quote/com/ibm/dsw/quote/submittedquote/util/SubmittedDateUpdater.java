
package com.ibm.dsw.quote.submittedquote.util;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.exception.InvalidWSInputException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuotePriceTotalsFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPartPriceContract;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPartPriceContract.LineItemParameter;
import com.ibm.dsw.quote.submittedquote.util.SubmittedDateComparator.DateRecord;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Aug 30, 2007
 */
public class SubmittedDateUpdater {
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    SubmittedQuoteRequest request;
    SubmittedQuoteResponse response;
    
    public SubmittedDateUpdater(SubmittedQuoteRequest request, SubmittedQuoteResponse response){
        this.request = request;
        this.response = response;
    }
    public void execute() throws QuoteException{ 
        
        Quote quote = request.getQuote();        
        
        SubmittedPartPriceContract ct = request.getContract();

        HashMap dateMap = ct.getItems();

        try {
            
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    quote.getQuoteHeader().getWebQuoteNum());
            quote.setLineItemList(lineItemList);
            
            boolean hasDateChanged = false;
            boolean hasBackDating = false;
            boolean hasDurationChange = false;
            Iterator iter = dateMap.keySet().iterator();
            
            String dateChangeCode = "__"+PartPriceConstants.PartChangeType.PART_DATE_CHANGED + "_";
            SubmittedDateComparator dateComparator = new SubmittedDateComparator();
            
            DateCalculator dateCalculator = DateCalculator.create(quote);
            
            while (iter.hasNext()) {
                
                String key = (String) iter.next();

                QuoteLineItem item = getLineItem(lineItemList, key);
                
                if (null == item) {
                    logContext.info(this, "Can't find line item for " + key);
                    continue;
                }
                
                //item.setUserEmail(ct.getUserId());

                item.setChgType(null);

                LineItemParameter parameter = (LineItemParameter) dateMap.get(key);
                
                if(!PartPriceConfigFactory.singleton().isAllowEditStartDate(quote.getQuoteHeader(), item)){
                    //For now, except cmprss cvrage parts, if start date edit is not allowed then end date edit is not allowed
                    //as well, skip blow processing
                    if(!item.hasValidCmprssCvrageMonth()){
                        continue;
                    }
                }
                Date ovrdStartDate = DateUtil.parseDate(parameter.getOvrdStartDate(), DateUtil.PATTERN);
                //For cmprss cvrage applied parts, start date edit is not allowed, use the default(first day of quote submit month)
                if(item.hasValidCmprssCvrageMonth() ){
                    ovrdStartDate = item.getMaintStartDate();
                }
                
                Date ovrdEndDate = null;
                
                //if the end date should be system calculated, then calculate the end date
                if(item.getPartDispAttr().isSysCalEndDate()){                	
                	ovrdEndDate = dateCalculator.calLicencePartEndDate(item.getPartDispAttr().isFtlPart(), ovrdStartDate);
                } else {
                	ovrdEndDate = DateUtil.parseDate(parameter.getOvrdEndDate(), DateUtil.PATTERN);
                }
                
                //Skip back dating check for cmprss cvrage applied parts
                if(parameter.startDateBackDated && (!item.hasValidCmprssCvrageMonth()) && !QuoteCommonUtil.isSkipLineItemDateValidate(item)){
                    hasBackDating = true;
                }
                
                DateRecord dr = dateComparator.addDateRecord(item,item.getMaintStartDate(),item.getMaintEndDate(),ovrdStartDate,ovrdEndDate);
                
                if(dr.isDurationChanged()){                 
                	hasDurationChange = true;
                }
                
                
                //if any date changed
                if(dr.isStartDateChanged() || dr.isEndDateChanged()){
                    hasDateChanged = true;
                    //set previsous date
                    item.setPrevsStartDate(item.getMaintStartDate());
                    item.setPrevsEndDate(item.getMaintEndDate());
                    //set back dating flag 
                    item.setBackDatingFlag(parameter.startDateBackDated);
                    
                    if (dr.isStartDateChanged()) {
                        item.setMaintStartDate(ovrdStartDate);
                        item.setChgType(dateChangeCode);
                        item.setStartDtOvrrdFlg(true);
                        logContext.debug(this, "ovrdStartDate should be updated to db:" + ovrdStartDate);
                    }
                    if (dr.isEndDateChanged()) {
                        item.setMaintEndDate(ovrdEndDate);
                        item.setChgType(dateChangeCode);
                        item.setEndDtOvrrdFlg(true);
                        logContext.debug(this, "ovrdEndDate should be updated to db:" + ovrdEndDate);
                    }
                    
                    item.setSModByUserID(ct.getUserId());
                    
                }
                
            }
            logContext.debug(this,"Has Date changed:"+hasDateChanged);
            logContext.debug(this, "Has back dating line items: " + hasBackDating);
            logContext.debug(this, "Has duration change : " + hasDurationChange);
            logContext.debug(this,"Date Comparator: \n" + dateComparator.toString());
            // Call pricing service
            boolean callPriceFailed = false;
            
            if(hasDateChanged){
                if(hasDurationChange){
                    // clear offer price
                    if (quote.getQuoteHeader().getOfferPrice() != null) {
	                    quote.getQuoteHeader().setOfferPrice(null);
	                    for (int i = 0; i < lineItemList.size(); i++) {
	                        QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
	                        item.setOvrrdExtPrice(null);
	                        item.setOfferIncldFlag(null);
	                        if(!dateChangeCode.equals(item.getChgType())){
	                            item.setChgType(PartPriceConstants.PartChangeType.PART_OFFER_PRICE_CLEARED);
	                        }
	                    }
                    }
                }
                
                SubmittedPriceCalculator calculator = new SubmittedPriceCalculator(quote,dateComparator);
                callPriceFailed = !calculator.calculateDefaultPrice();            
                
                if (callPriceFailed) {
                    response.setUpdateLineItemFailed(true);
                    logContext.debug(this,"Call Price engine failed, return directly");
                    return;
                }
                else{
                    response.setUpdateLineItemFailed(false);
                    logContext.debug(this,"Call Price engine successfully");
                }  
                
                // Call quote modify service
                if (quote.getQuoteHeader().isRenewalQuote() && ct.getQuoteUserSession() == null) {
                    response.setBluePageUnavailable(true);
                    logContext.debug(this,"Blue page is unavailable now.");
                    return;
                }
                else {
                    response.setBluePageUnavailable(false);
                    logContext.debug(this,"Blue page is available now.");
                }
                
                try {
                    QuoteModifyServiceHelper helper = new QuoteModifyServiceHelper();
                    helper.updateLineItemDate(quote, ct.getQuoteUserSession());

                } catch (InvalidWSInputException e) {
                    logContext.error(this, "Call Quote Modify Service Error : " + e.getMessage());
                    response.setWebServiceInputInvalid(true);
                    return;
                } catch (WebServiceException e) {
                    logContext.error(this, "Call Quote Modify Service Error : " + e.getMessage());
                    response.setUpdateLineItemFailed(true);
                    return;
                }
                
                // up to now, the quote has been modifed in SAP,  clear the change type, so next time the data will be submitted
                clearChangeTypeCode(quote);
                
                QuoteHeader header = quote.getQuoteHeader();
                // clear the recalcuate flag, since approver has manually changed the date and price already calculated
                header.setRecalcPrcFlag(0);
                header.setDateOvrrdByApproverFlag(true);
                //set back dating flag
                header.setBackDatingFlag(hasBackDating);
                
                // get approver level
                int apprvlLvl = 0;
                if (quote.getQuoteUserAccess() != null) {
                    apprvlLvl = quote.getQuoteUserAccess().getAppLevel();
                }                
                writeChangeAuditHistory(header,dateComparator,ct.getUserId(),apprvlLvl);              
                try {
                    CommonServiceUtil.updateRelatedItemSeqNum(quote);
                } catch (TopazException e1) {
                    throw new QuoteException(e1);
                }
        		
                PartPriceHelper.calculateTotalPoint(quote);
                PartPriceHelper.calculateQuoteTotalPrice(quote);
                PartPriceProcess process = PartPriceProcessFactory.singleton().create();
                process.updateQuoteHeader(quote.getQuoteHeader(), quote.getQuoteHeader().getCreatorId());
                
                //if start date or end date is changed, remove date from ebiz1.web_quote_price_totals table
                removePriceTotals(quote);
            }
            
        } catch (TopazException e) {

            logContext.debug(this, "error getting submitted quote's part & pricing info");
            throw new QuoteException(e);
        }
    }
   
    private QuoteLineItem getLineItem(List lineItems, String theKey) {        
        
         for (int i = 0; i < lineItems.size(); i++) {
             QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
             
             String key = item.getPartNum() + "_" + item.getSeqNum() ;//+ "_" + item.getDestSeqNum();
             if(key.equals(theKey)){                
                 return item;
             }
             
         }
         return null;
     }
    private void writeChangeAuditHistory(QuoteHeader header, SubmittedDateComparator dc,String userId,int apprvlLvl) throws TopazException{
        
        // write the idoc change history
        QuoteHeaderFactory.singleton().updateSapIDocNum(
                header.getWebQuoteNum(), 
                header.getSapIntrmdiatDocNum(), 
                userId, 
                SubmittedQuoteConstants.USER_ACTION_CHG_LI);
        
        // write date change hihstory
        Iterator iter = dc.getDateRecords();
        while(iter.hasNext()){
            DateRecord dr = (DateRecord)iter.next();
            if(dr.isStartDateChanged()){
                AuditHistoryFactory.singleton().createAuditHistory(header.getWebQuoteNum(),
                        new Integer(dr.item.getSeqNum()),  // line item seq numbr
                        userId,
                        SubmittedQuoteConstants.USER_ACTION_CHG_LI_ST_DT, // user action
                        apprvlLvl, // approver level
                        DateUtil.formatDate(dr.prevStartDate), // old value
                        DateUtil.formatDate(dr.curStartDate) // new value
                        );
            }
            if(dr.isEndDateChanged()){
                AuditHistoryFactory.singleton().createAuditHistory(header.getWebQuoteNum(),
                        new Integer(dr.item.getSeqNum()),  // line item seq numbr
                        userId,
                        SubmittedQuoteConstants.USER_ACTION_CHG_LI_END_DT, // user action
                        apprvlLvl, // approver level
                        DateUtil.formatDate(dr.prevEndDate), // old value
                        DateUtil.formatDate(dr.curEndDate) // new value
                        );
            }
        }
        
    }
    private void clearChangeTypeCode(Quote quote) throws TopazException{
        List lineItems = quote.getLineItemList();
        for(int i=0;i<lineItems.size();i++){
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            item.setChgType(null);
        }
        /*List sapItems = quote.getSapLineItemList();
        for(int i=0;i<sapItems.size(); i++){
            SapLineItem item = (SapLineItem)sapItems.get(i);
            // clear change type in SAP ,need update the sp U_QT_SAP_LI
            item.setChgType(PartPriceConstants.PartChangeType.SAP_PART_NO_CHANGES);
        }*/
    }
    
    private void removePriceTotals(Quote quote) throws TopazException{
        QuotePriceTotalsFactory.singleton().removePriceTotals(quote.getQuoteHeader().getWebQuoteNum());
        quote.setPriceTotals(null);
    }
}
