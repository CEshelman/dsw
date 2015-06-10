package com.ibm.dsw.quote.submittedquote.util;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.service.price.DefaultPricingHelper;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.service.price.PricingServiceHelper;
import com.ibm.dsw.quote.common.service.price.rule.ChannelPricingRule;
import com.ibm.dsw.quote.common.service.price.rule.SSQPricingRule;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;

import com.ibm.dsw.quote.draftquote.util.price.OfferPriceCalculator;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
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
 * Creation date: May 29, 2007
 */

public class SubmittedPriceCalculator {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;
    private SubmittedDateComparator dateComparator ;
    public SubmittedPriceCalculator(Quote quote,SubmittedDateComparator dateComparator) {
        this.quote = quote;
        this.dateComparator = dateComparator;
    }
    
    public boolean calculateDefaultPrice() throws TopazException{
        
        PricingRequest pr = new PricingRequest(quote,QuoteConstants.QUOTE_DOC_CAT);
        
        if (QuoteConstants.FULFILLMENT_CHANNEL.equals(quote.getQuoteHeader().getFulfillmentSrc())) {
            pr.setChannel(true);        
        } else {
            pr.setChannel(false);
            
        }        
        
        pr.setUpdatePriceLevel(false);
        return calculatePrice(pr);
    }
    
    private boolean callPWS(PricingRequest pr) throws TopazException, Exception{
        boolean needBestPricing = this.needBestPricing();            
        if(needBestPricing){
            pr.setCustomerBandLevel(CommonServiceUtil.getBestPricingBandLevel(quote));
            logContext.debug(this,"For best pricing, we need get customer's band level");
        }
        SSQPricingRule rule = new SSQPricingRule(pr,needBestPricing,dateComparator);            
        
        DefaultPricingHelper serviceHelper = new DefaultPricingHelper(pr);
        serviceHelper.setPricingRule(rule);
        serviceHelper.getPrice();
        
        if(!serviceHelper.isPriceOk()){
            logContext.info(this, "All line item don't have a price");
            return false;
        }
        
        return true;
    }
    
    private boolean calculatePrice(PricingRequest pr) throws TopazException{
        try {
        	
        	if(!callPWS(pr)){
        		return false;
        	}
        	
        	if(GDPartsUtil.needRecalculateForGrowthDelegation(quote)){
        		if(!callPWS(pr)){
            		return false;
            	}
        	}
            
            if( quote.getQuoteHeader().getOfferPrice()!=null){
                OfferPriceCalculator offerCalculator = new OfferPriceCalculator(quote);
                offerCalculator.calculate();             
                
                if(QuoteCommonUtil.hasSaaSPartsApplicableForOfferPrice(quote)){
                	pr.setSendOvrrdExtndPrice(true);
                	
                	if(!callPWS(pr)){
                		return false;
                	}
                }
                
             // fix PL Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/B058B90BB5AD6A7D85257B0A0076F4A1 
                // always call Pricing Service again to get the correct price from SAP
                    logContext.debug(this,"always call Pricing Service again to get the correct price from SAP");                    
                    // only send Ovrrd extend price
                    pr.setSendOvrrdExtndPrice(true);
                    //if the quote has offer price, it must be a special bid , no need to apply best pricing
                    
                    PricingServiceHelper serviceHelper = new DefaultPricingHelper(pr);                    
                    serviceHelper.setPricingRule(new ChannelPricingRule(pr));
                    serviceHelper.getPrice();
                    
                    if(!serviceHelper.isPriceOk()){
                        logContext.info(this, "All line item don't have a price");
                        return false;
                    }
            }
            

        } catch (Exception e) {
            logContext.error(this, "Failed in Price calculation:" + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean needBestPricing() {

        QuoteHeader header = quote.getQuoteHeader();

        if (!isSpecialBid()) {

            boolean quoteOnHold = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD);
            boolean readyToOrder = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER);
            if (quoteOnHold || readyToOrder) {
                return true;
            }
        }

        return false;

    }

    protected boolean isSpecialBid() {

        QuoteHeader header = quote.getQuoteHeader();

        return (header.getSpeclBidManualInitFlg() == 1) || (header.getSpeclBidSystemInitFlg() == 1);

    }

}
