package com.ibm.dsw.quote.retrieval.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.service.price.QuoteRetrievalServiceHelper;
import com.ibm.dsw.quote.common.service.price.rule.QuoteRetrievalPricingRule;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.PricingServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.util.SubmittedDateComparator;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 29, 2007
 */
public class QuoteRetrievalPriceCalculator {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;

    protected String docCat;    

    protected PricingRequest pr;
    
    private boolean external = false;

    protected SubmittedDateComparator dateComparator = new SubmittedDateComparator();
    public QuoteRetrievalPriceCalculator(Quote quote, String docCat, boolean external) {

        this.quote = quote;
        this.docCat = docCat;
        this.external = external;
        

    }
    private void calculateDate() throws TopazException{        
        
        DateCalculator calculator = DateCalculator.create(quote);
        calculator.calculateDate();
        
        Iterator iter = quote.getLineItemList().iterator();
        while(iter.hasNext()){
            QuoteLineItem item = (QuoteLineItem)iter.next();
            // user override the date in draft quote
            if (item.getStartDtOvrrdFlg() || item.getEndDtOvrrdFlg()) {
                this.dateComparator.addDateRecord(item,
                        item.getMaintStartDate(),
                        item.getMaintEndDate(),
                        item.getMaintStartDate(),
                        item.getMaintEndDate());
            }
            else{
                this.dateComparator.addDateRecord(item,
                        item.getMaintStartDate(),
                        item.getMaintEndDate(),
                        item.getPartDispAttr().getStdStartDate(),
                        item.getPartDispAttr().getStdEndDate());
            }
            
        }
        logContext.debug(this,"Date Comparator Afer system adjusted date: \n" + dateComparator.toString());
        
        calculator.setLineItemDates();
        
        
    }
    public void calculatePrice() throws TopazException {

        calculateDate();        
        
        QuoteHeader header = quote.getQuoteHeader();

        if (QuoteConstants.FULFILLMENT_DIRECT.equals(header.getFulfillmentSrc())) {

            this.processDirectQuote();

        } else if (QuoteConstants.FULFILLMENT_CHANNEL.equals(header.getFulfillmentSrc())) {

            this.processChannelQuote();
        } else {
            logContext.info(this, "unsupported fullfillment source:" + header.getFulfillmentSrc());
        }
    }

    private boolean isSpecialBid() {
        return this.quote.getQuoteHeader().getSpeclBidFlag() == 1;
    }

    /**
     *  
     */
    private void processChannelQuote() throws TopazException {
     
        pr = new PricingRequest(this.quote, this.docCat);
        pr.setExternal(this.external);
        pr.setChannel(true);
        
        pr.setSaveToDb(false);
        
        // set prcDate = sumission date
        if (quote.getQuoteHeader().getSubmittedDate() == null) {

            logContext.info(this, "The submitted date is null :" + quote.getQuoteHeader().getWebQuoteNum());
            logContext.info(this, "Current Date will be used");
            pr.setPrcDate(DateUtil.getCurrentDate());

        } else {
            logContext.debug(this,"quote submitted date = "+ quote.getQuoteHeader().getSubmittedDate());
            pr.setPrcDate(new Date(quote.getQuoteHeader().getSubmittedDate().getTime()));
        }

        if (this.isSpecialBid()) {
            logContext.debug(this, "quote is Channel and special bid, not to Call PWS");
            return;
        }
        logContext.debug(this, "quote is Channel and not special bid, Call PWS twice");
        this.callPWSTwice(pr);
    }

    /**
     * @throws TopazException
     *  
     */
    private void processDirectQuote() throws TopazException {
        if (isSpecialBid()) {
            
            logContext.debug(this, "quote is direct and special bid, use the pricing saved in the quote tables");
            
            return;
            
        } else {
            
            logContext.debug(this, "quote is direct and not special bid, need best pricing");
            
            pr = new PricingRequest(this.quote, this.docCat);
            pr.setExternal(this.external);
            pr.setChannel(false);
            //pr.setSendOvrdUnitPrice(true);
            pr.setSaveToDb(false);
            pr.setCustomerBandLevel(CommonServiceUtil.getBestPricingBandLevel(quote));
            
            logContext.debug(this,"Pricing Reqeust has  been created, begin to callPWS");
            ItemOut[] lineItemPrcs = callPWS(pr);
            if(processForGrowthDelegationAsNeeded(lineItemPrcs)){
            	lineItemPrcs = callPWS(pr);
            }
            applyBestPricing(quote.getLineItemList(),lineItemPrcs);
        }
    }
    
    private boolean processForGrowthDelegationAsNeeded(ItemOut[] lineItemPrcs) throws TopazException{
    	if(lineItemPrcs == null){
    		return false;
    	}
    	
    	boolean needRecalculate = false;
        for (int i = 0; i < lineItemPrcs.length; i++) {
            ItemOut item = lineItemPrcs[i];

            QuoteLineItem lineItem = this.getLineItem(item.getPartNum(), item.getItmNum().intValue());
            YTYGrowth yty = lineItem.getYtyGrowth();
            if(yty != null && yty.isPctManuallyEntered() && yty.getYTYGrowthPct() != null){
            	Double newEntitledPrice = PricingServiceUtil.getLocalExtProratedPrc(item, lineItem, quote);
            	Double oldEntitledPrice = lineItem.getLocalExtProratedPrc();
            	
            	if(newEntitledPrice == null
            			|| oldEntitledPrice == null){
            		continue;
            	}
            	if(DecimalUtil.isNotEqual(newEntitledPrice, oldEntitledPrice)){
            		//Entitled price changed, need to recalculate the disc pct
            		double discPct = GDPartsUtil.calculateDisc(yty.getYTYGrowthPct(), lineItem, quote.getQuoteHeader());
            		
            		if(DecimalUtil.isNotEqual(discPct, lineItem.getLineDiscPct())){
            			needRecalculate = true;
            			lineItem.setLineDiscPct(discPct);
            		}
            	}
            }
        }
        
        return needRecalculate;
    }

    private void callPWSTwice(PricingRequest pr) throws TopazException {

        QuoteRetrievalServiceHelper serviceHelper = new QuoteRetrievalServiceHelper(pr);
        QuoteRetrievalPricingRule rule = new QuoteRetrievalPricingRule(pr);
        serviceHelper.setPricingRule(rule);

        try {
            serviceHelper.getPrice();

            ItemOut[] lastTimePrcs = rule.getLineItemPrices();
            logContext.debug(this,"First call finished");
            
            if(processForGrowthDelegationAsNeeded(lastTimePrcs)){
            	serviceHelper.getPrice();
                lastTimePrcs = rule.getLineItemPrices();
            }
            
            pr.setPrcDate(DateUtil.getCurrentDate());
            serviceHelper.getPrice();
            ItemOut[] currentPrcs = rule.getLineItemPrices();
            
            if(processForGrowthDelegationAsNeeded(currentPrcs)){
            	serviceHelper.getPrice();
                currentPrcs = rule.getLineItemPrices();
            }
            
            logContext.debug(this,"Second Call finished");
            ItemOut[] lowerPrcItems = this.getLowerPrice(lastTimePrcs,currentPrcs);
            
            logContext.debug(this,"Get lower price finished:"+lowerPrcItems.length);
            
            for (int i = 0; i < lowerPrcItems.length; i++) {

                ItemOut item = lowerPrcItems[i];
                
                QuoteLineItem sapLineItem = this.getLineItem(item.getPartNum(), item.getItmNum().intValue());

                if (null == sapLineItem) {
                    logContext.info(this, "Can't find sap line item for (" + item.getPartNum() + "," + item.getItmNum()
                            + ")");
                    continue;
                }
                String errCode = item.getErrCode();
                if(!QuoteCommonUtil.acceptPrice(item, sapLineItem)){
                    logContext.info(this,"Sap return error for part " + item.getPartNum()+",Price will be cleared");
                    logContext.info(this,"Error Code =" + errCode);
                    logContext.info(this,"Error Message =" + item.getErrMsg());
                    sapLineItem.clearPrices();  
                    continue;
                }
                else{
                    this.setNewPrice(sapLineItem,item);
                }
            }

        } catch (Exception e) {
            logContext.error(this, "Call Pricing Service Error:" + e.getMessage());
            return;
        }
        
    }
    private ItemOut[] getLowerPrice(ItemOut[] lastTimePrcs,ItemOut[] currentPrcs){
        List lowerPriceItems = new ArrayList();
        for(int i=0;i<lastTimePrcs.length;i++){
            ItemOut lastTimeItem = lastTimePrcs[i];
            ItemOut currentItem = this.findLineItemPrice(currentPrcs,lastTimeItem.getPartNum(),lastTimeItem.getItmNum());
            if(currentItem == null){
                logContext.info(this,"Can't get the item in current prc " + lastTimeItem.getPartNum()+","+lastTimeItem.getItmNum());
                lowerPriceItems.add(lastTimeItem);
            }            
            else{
            	QuoteLineItem qli = (QuoteLineItem)getLineItem(lastTimeItem.getPartNum(),
            			                                         lastTimeItem.getItmNum().intValue());
            	
                if(!qli.isObsoletePart()){
	                if(StringUtils.isNotBlank(lastTimeItem.getErrCode())){
	                    lowerPriceItems.add(currentItem);
	                }
	                else if (StringUtils.isNotBlank(currentItem.getErrCode())){
	                    lowerPriceItems.add(lastTimeItem);
	                }
	                else if(lastTimeItem.getLclUnitPrc().doubleValue()< currentItem.getLclUnitPrc().doubleValue()){
	                    lowerPriceItems.add(lastTimeItem);
	                    logContext.debug(this,"Last time price is lower");
	                } else {
	                	lowerPriceItems.add(currentItem);
	                }
	                
                } else {
                	//for eol parts, PWS will always return error code
                	if(lastTimeItem.getLclUnitPrc().doubleValue()< currentItem.getLclUnitPrc().doubleValue()){
                		lowerPriceItems.add(lastTimeItem);
                		logContext.info(this,"Last time price is lower");
                	} else {
                		lowerPriceItems.add(currentItem);
                	}
                } 
            }
        }
        ItemOut [] result = new ItemOut[lowerPriceItems.size()];
        lowerPriceItems.toArray(result);
        return result;
        
    }
    private ItemOut findLineItemPrice(ItemOut[] items,String partNum,Integer seqNum){
        
        for(int i=0;i< items.length; i++){
            ItemOut item = items[i];
            if(item.getPartNum().equals(partNum) && item.getItmNum().equals(seqNum)){
                return item;
            }
        }
        return null;
    }
    
    
    private ItemOut[] callPWS(PricingRequest pr) throws TopazException {
        
        QuoteRetrievalServiceHelper serviceHelper = new QuoteRetrievalServiceHelper(pr);
        QuoteRetrievalPricingRule rule = new QuoteRetrievalPricingRule(pr);
        serviceHelper.setPricingRule(rule);

        try {
            logContext.debug(this,"Begin to call price");
            serviceHelper.getPrice();

            ItemOut[] lineItemPrcs = rule.getLineItemPrices();

            return lineItemPrcs;

        } catch (Exception e) {
            logContext.error(this, "Call Pricing Service Error:" + e.getMessage());
            return new ItemOut[0];
        }

    }

    protected QuoteLineItem getLineItem(String partNum, int seqNum) {

        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (item.getPartNum().endsWith(partNum) && (item.getDestSeqNum() == seqNum)) {
                return item;
            }
        }
        return null;

    }

    private void applyBestPricing(List sapItems, ItemOut[] lineItemPrcs) throws TopazException {

        for (int i = 0; i < lineItemPrcs.length; i++) {

            ItemOut item = lineItemPrcs[i];

            QuoteLineItem lineItem = this.getLineItem(item.getPartNum(), item.getItmNum().intValue());

            if (null == lineItem) {
                logContext.info(this, "Can't find sap line item for (" + item.getPartNum() + "," + item.getItmNum()
                        + ")");
                continue;
            }
            String errCode = item.getErrCode();
            if(!QuoteCommonUtil.acceptPrice(item, lineItem)){
                logContext.info(this,"Sap return error for part " + item.getPartNum()+",Price will be cleared");
                logContext.info(this,"Error Code =" + errCode);
                logContext.info(this,"Error Message =" + item.getErrMsg());
                lineItem.clearPrices();  
                continue;
            }
            if (lineItem.getOverrideUnitPrc() != null) {
                logContext.debug(this, createKey(lineItem) + " Has OUP:" + lineItem.getOverrideUnitPrc()
                        + ", don't apply best pricing ");
                setNewPrice(lineItem, item);
            } else {
                logContext.debug(this, "Begin to apply best pricing for QRWS");
                if (isProrationMonthChanged(lineItem)) {
                    logContext.debug(this, "Proration Month changed, need use price from PWS");
                    setNewPrice(lineItem, item);
                } else if (lineItem.getLocalUnitPrc()==null || item.getLclUnitPrc().doubleValue() < lineItem.getLocalUnitPrc().doubleValue()) {
                    logContext.debug(this, "Price Decreased, Use price from PWS");
                    setNewPrice(lineItem, item);
                } else {
                    logContext.debug(this, "Price No change or Price increased,Keep Original Price");
                }
            }
        }

    }

    private String createKey(QuoteLineItem item) {
        return "[" + item.getPartNum() + "," + item.getSeqNum() + "]";
    }

    protected boolean isProrationMonthChanged(QuoteLineItem item) {
        // Andy : we have to get the qutoe line item , because the dateComparator is using it
        return dateComparator.isDurationChanged(item);
    }

    protected void setNewPrice(QuoteLineItem lineItem, ItemOut item) throws TopazException {
    	QuoteCommonUtil.setLineItemPrice(item, lineItem, quote);
    }
}
