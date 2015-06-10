
package com.ibm.dsw.quote.common.service.price;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.HeaderIn;
import DswSalesLibrary.HeaderOut;
import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 21, 2007
 */
public class QuoteRetrievalServiceHelper extends PricingServiceHelper {

    public QuoteRetrievalServiceHelper(PricingRequest pr) {
        super(pr);
        
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.PricingServiceHelper#mapQuoteHeader()
     */
    protected HeaderIn mapQuoteHeader() throws Exception {
        
        HeaderIn headerIn = this.mapQuoteHeaderBasicInfo();
        // in super class , the docCat is set in this way, if sales quote ,set Q, if renewal quote, set R
        // this works for all PricingServiceHelper subclass
        // but for this class used by QWRS, we need make sure 
        // whatever the consuming application is passing should be used when calling PWS 
        
        headerIn.setDocCat(pr.getDocCat());
        
        String distChannelCode = this.quote.getQuoteHeader().getSapDistribtnChnlCode();
        headerIn.setDistChnl(distChannelCode);

        if(pr.getPrcDate() != null){
            headerIn.setPrcDate(this.convertToYYYYMMDD(pr.getPrcDate()));
            logger.debug(this,"Set prcing date = "+pr.getPrcDate());
        }
        

        if(pr.getCustomerBandLevel() != null){
            headerIn.setPrcLvl(pr.getCustomerBandLevel());
        }
        
        if(QuoteCommonUtil.isCustWithOverrideSVP(quote)){
            headerIn.setPrcLvl(quote.getCustomer().getTransSVPLevel());
        }
        //fix ebiz ticket JKEY-79EVDA:BP eOrder using wrong currency on some Sales Quotes
        fillCountryAndCurrency(headerIn, quote.getQuoteHeader());
        
        return headerIn;

    }
    private void fillCountryAndCurrency(HeaderIn headerIn,QuoteHeader header){
        
        Partner payer =  quote.getPayer();
        Customer customer = quote.getCustomer();
        if((null==payer) || (null==customer)){
            return;
        }
        if(header.getSpeclBidFlag()==1){
            return;
        }
        
        String country = null;
        String currency = null;
        
        String custCountry = customer.getCountryCode();
        String custCurrency = customer.getCurrencyCode(); 
        
        String payerCountry = payer.getCountry();
        String payerCurrency = payer.getCurrencyCode();
        
       
        
        //check the payer currency and customer currency is match or not when a quote is external, channel not special bid
        if (this.pr.isChannel() && this.pr.isExternal() ) {
           // If they do not, reprice the quote using the payer's country and currency
            country = StringUtils.equals(custCountry,payerCountry)? custCountry : payerCountry;
            currency = StringUtils.equals(custCurrency,payerCurrency)? custCurrency : payerCurrency;
        } else {
        	// Andy 6/12/2008:  due to FCT cross boder, should use price country and quote currency code for fullfillment source Direct
            country = header.getPriceCountry().getCode3();
            currency = header.getCurrencyCode();

            
        } 
        logger.debug(this, "isChannel :" + this.pr.isChannel());
        logger.debug(this, "isExternal :" + this.pr.isExternal());
        logger.debug(this, "isSpeclBid :" + ( header.getSpeclBidFlag()==1 ));
        logger.debug(this, " |country: " + country +" |custCountry: "+custCountry + " |payerCountry: "+payerCountry);
        logger.debug(this, " |currency: " + currency +" |custCurrency: "+custCurrency + " |payerCurrency: "+payerCurrency);
        
        headerIn.setCountry(country);
        headerIn.setCurrency(currency);
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.PricingServiceHelper#processPrice(DswSalesLibrary.ItemOut[])
     */
    protected void processPrice(ItemOut[] itemOut) throws Exception {
        this.rule.execute(itemOut);

    }
    
    protected void setLatamUpliftPct(QuoteHeader header, HeaderOut headerOut){
        return;
    }

}
