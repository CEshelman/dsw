package com.ibm.dsw.quote.draftquote.util.price;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.service.price.BaseLinePricingHelper;
import com.ibm.dsw.quote.common.service.price.DefaultPricingHelper;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.service.price.rule.ChannelPricingRule;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
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
 * Creation date: Apr 30, 2007
 */

public  class PriceCalculator {
	
	public static final String PAYER_ISSUE_PATTERN = "company master record is missing for company";
	public static final String PAYER_ISSUE_START = "For payer";
	public static final String ARITHMETIC_ISSUE_START="Overflow";
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;    
    
    private String docCat = QuoteConstants.QUOTE_DOC_CAT;
    
    private boolean offerPriceOk = true;
    //private boolean persistToDb = true;
    
    private String tranPriceLevelCode = null;
    
    private boolean payerDataIssue = false;
    
    private String payerDataIssusMsg = "";
    
    private boolean arithmeticOperationIssue = false; 
    
    private String arighmeticOperationIssueMsg = "";
    
    public boolean isArithmeticOperationIssue() {
		return arithmeticOperationIssue;
	}


	public String getArighmeticOperationIssueMsg() {
		return arighmeticOperationIssueMsg;
	}


	public PriceCalculator(Quote quote) {
        this.quote = quote;
        
    }    
    
    
    protected PricingRequest getPricingRequest() {
        PricingRequest pr = new PricingRequest(quote, docCat);

        if (QuoteConstants.FULFILLMENT_DIRECT.equals(quote.getQuoteHeader().getFulfillmentSrc())) {
            pr.setChannel(false);
        } else if (QuoteConstants.FULFILLMENT_CHANNEL.equals(quote.getQuoteHeader().getFulfillmentSrc())) {
            pr.setChannel(true);
        }
        return pr;
    }
    
    public boolean calculatePrice() throws TopazException {
    	return calculatePrice(false);
    }
    
    public  boolean calculatePrice(boolean forPriceIncrease) throws TopazException {
        PricingRequest pr = this.getPricingRequest();
        
        DefaultPricingHelper serviceHelper = null;
        try {
            serviceHelper = new DefaultPricingHelper(pr); 
            pr.setSendOvrrdExtndPrice(false);    
            tranPriceLevelCode = serviceHelper.getPrice();   
        
            if(!serviceHelper.isPriceOk()){
                logContext.info(this, "All line item don't have a price");
                return false;
            }
            
            boolean needRecalForGD = GDPartsUtil.needRecalculateForGrowthDelegation(quote);
            if(quote.getOverallYtyGrowth() != null || needRecalForGD){
     		   if(quote.getOverallYtyGrowth() != null){
     			   QuoteCommonUtil.clearOfferPrice(quote);
    			   OverallGrowthPriceCalculator calc = new OverallGrowthPriceCalculator(quote);
    			   calc.calculate(quote.getOverallYtyGrowth());
    		   }
            	
            	serviceHelper = new DefaultPricingHelper(pr); 
            	
                tranPriceLevelCode = serviceHelper.getPrice();   
                
                if(!serviceHelper.isPriceOk()){
                    logContext.info(this, "All line item don't have a price");
                    return false;
                }
            }
            
            if(quote.getQuoteHeader().getOfferPrice()!=null){
                processOfferPrice();
                
                if(QuoteCommonUtil.hasSaaSPartsApplicableForOfferPrice(quote) && !forPriceIncrease){
                	 pr.setSendOvrrdExtndPrice(true); 
                	 serviceHelper = new DefaultPricingHelper(pr);
                     tranPriceLevelCode = serviceHelper.getPrice();   
                     
                     if(!serviceHelper.isPriceOk()){
                         logContext.info(this, "All line item don't have a price");
                         return false;
                     }
                }
                // fix PL Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/B058B90BB5AD6A7D85257B0A0076F4A1 
                // always call Pricing Service again to get the correct price from SAP
                    logContext.debug(this,"always call Pricing Service again to get the correct price from SAP");                    
                    pr.setSendOvrrdExtndPrice(true);    
                    serviceHelper = new DefaultPricingHelper(pr);
                    serviceHelper.setPricingRule(new ChannelPricingRule(pr));
                    tranPriceLevelCode = serviceHelper.getPrice();
                    
                    if(!serviceHelper.isPriceOk()){
                        logContext.info(this, "All line item don't have a price");
                        return false;
                    }
            }
            
            
        } catch (Exception e) {
        	String msg = e.getMessage();
        	if(StringUtils.isNotEmpty(msg)){
        		int index = msg.lastIndexOf(PAYER_ISSUE_PATTERN);
        		if(index != -1){
	        		int start = msg.lastIndexOf(PAYER_ISSUE_START);
	        		if(start != -1){
	        			payerDataIssue = true;
	        			
	        			payerDataIssusMsg = msg.substring(start);
	        		}
	        	}else {
	        		int start = msg.lastIndexOf(ARITHMETIC_ISSUE_START);
	        		if (start != -1){
	        			arithmeticOperationIssue = true;
	        			arighmeticOperationIssueMsg = msg.substring(start);
	        		}
	        	}
        	}
        	
            logContext.info(this, "Failed in Price calculation:" + e.getMessage());
            return false;
        }
        
        
        CommonServiceUtil.setPriceChangeCode(quote);
        
        return true;
    }
    
    protected void processOfferPrice() throws TopazException{
        // if quote has offer price, calculate offer price
        
        logContext.debug(this,"Calculate the offer price again");
        OfferPriceCalculator offerPriceCalaulator = new OfferPriceCalculator(quote);
        this.offerPriceOk = offerPriceCalaulator.calculate();
    }
    
    public boolean isOfferPriceOk() {
        return offerPriceOk;
    }
    
    public String getTranPriceLevelCode(){
    	return tranPriceLevelCode;
    }
    
    public boolean isPayerDataIssue(){
    	return payerDataIssue;
    }
    
    public String getPayerDataIssueMsg(){
    	return payerDataIssusMsg;
    }
    
    public List calculateBLPrice() throws TopazException {
		QuoteHeader header = quote.getQuoteHeader();

		String lob = header.getLob().getCode();

		try {
			logContext.debug(this, "Begin to call Base Line price");

			PricingRequest pr = new PricingRequest(quote, QuoteConstants.QUOTE_DOC_CAT);
			pr.setChannel(false);
			pr.setUpdatePriceLevel(false);
			BaseLinePricingHelper serviceHelper = new BaseLinePricingHelper(pr);
			serviceHelper.getPrice();

			List baseLinePrices = serviceHelper.getBaseLinePrices();
			
			return baseLinePrices;
		} catch (Exception e) {
			logContext.error(this, "Call base line price error" + e.getMessage());
		}
		
        return null;
    }   

}
