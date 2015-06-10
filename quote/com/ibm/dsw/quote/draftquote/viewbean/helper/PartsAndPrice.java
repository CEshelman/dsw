package com.ibm.dsw.quote.draftquote.viewbean.helper;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartsAndPriceViewBean</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class PartsAndPrice extends PartPriceCommon {
    
    
    public PartsAndPrice(Quote quote)
    {
        super(quote);
    }
    
    public PartsAndPrice(Quote quote, User user)
    {
        super(quote, user);
    }
    
    /**
     * @return true if any parts have been selected
     */
    public boolean showRecalculateQuoteLink() {
        return hasLineItems();
    }
    
    /**
     * return true if the quote contains any parts
     * @return
     */
    public boolean showSubmittedPartPricingSection() {
        if (quote != null && quote.getLineItemList() != null && quote.getLineItemList().size() != 0) {
            return true;
        }
        return false;
    }
    
    /**
     * return true if the editable version of this screen displays
     * @return
     */
    public boolean showSubmittedSaveDateChangedTextButton(boolean userPreference) {
        if(isSubmittedEditable() && userPreference){           
            List lineItems = quote.getLineItemList();
            PartDisplayAttr attr = null;

            for (int i = 0; i < lineItems.size(); i++) {
                QuoteLineItem lineItem = (QuoteLineItem)lineItems.get(i);
                
                if(isPA() || isPAE() || isOEM()){
                    //check wheather the current quote contains any contract parts, to be consitent 
                    //with method showSubmittedOverrideMaintenanceCoverage in SoftwareMaintenance.java
                    if (PartPriceConfigFactory.singleton().isDateEditAllowed(quote.getQuoteHeader(), lineItem)) {
                        return true;
                    }
                }
                else if (isFCT()){
                    attr = lineItem.getPartDispAttr();
                    //we alow user to override the date for license part
                    if (PartPriceConfigFactory.singleton().isDateEditAllowed(quote.getQuoteHeader(), lineItem)) {
                        return true;
                    }
                }
            }
        }
        
        return false;

    }
    
    public boolean showApplyOfferButton(){
        return QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())
               && quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag();
    }
    
    /**
     * return true if quote contains one or more PVU parts
     * @return
     */
    public boolean showSubmittedPVUCalculatorLink() {
        if (isPA() || isPAE() || isFCT() || isOEM()) {
            if (quote.getLineItemList() != null) {
                Iterator lineItemList = quote.getLineItemList().iterator();
                while (lineItemList.hasNext()) {
                    QuoteLineItem lineItem = (QuoteLineItem)lineItemList.next();
                    if (lineItem.isPvuPart()) {
                        return true;
                    }
                }
            }
        }

       return false; 
    }
    
    public boolean showCurrencyMsg() {
        
        QuoteHeader header = quote.getQuoteHeader();
        
        String pricingCurrency = header.getCurrencyCode();
        
        List currencyList = header.getPriceCountry().getCurrencyList();
      
        LogContextFactory.singleton().getLogContext().debug(this,"currList is null? " + (currencyList==null?"yes":"no"));
        if (currencyList != null && currencyList.size() > 0) {
            CodeDescObj countryCurrObj = (CodeDescObj)currencyList.get(0);
            String countryDefaultCurrency = countryCurrObj.getCode();
            LogContextFactory.singleton().getLogContext().debug(this,"Pricing Currency=" + pricingCurrency);
            LogContextFactory.singleton().getLogContext().debug(this,"Country default currency  "+ countryDefaultCurrency);
            if (pricingCurrency != null && countryDefaultCurrency != null && !pricingCurrency.trim().equalsIgnoreCase(countryDefaultCurrency.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    public String getDistinctRevnStrmCodeDesc(){
        List lineItems = new ArrayList();
        lineItems.addAll(quote.getMasterSoftwareLineItems());
        lineItems.addAll(quote.getSaaSLineItems());
        lineItems.addAll(quote.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems());
        List revnStrmCodes = new ArrayList();
		
        for(Iterator it = lineItems.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            
            if(qli.getRevnStrmCodeDesc() != null && !revnStrmCodes.contains(qli.getRevnStrmCodeDesc())){
                revnStrmCodes.add(qli.getRevnStrmCodeDesc());
            }
        }
        
        String value = StringUtils.join(revnStrmCodes.iterator(), PartPriceConstants.REVN_STRM_CODE_DSCR_SEPERATOR);
        
        try {
            return StringEncoder.textToHTML(value);
        } catch (Exception e) {
            LogContextFactory.singleton().getLogContext().debug(this,"getDistinctRevnStrmCodeDesc()" + e.getMessage());
            return "";
        }

    }
    
	/**
	 * judge whether the link 'calculate equity curve discount guidance' is showing
	 * @return true if the link is showed.
	 */
	public boolean showCalcutateEquityCurve(){
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		//add by fall, 2013/5/23, fcttopa quote can't display equitcurve calculate link
		if (null != quoteHeader && quoteHeader.isECEligible() && !quoteHeader.isBidIteratnQt() && !quoteHeader.isCopied4PrcIncrQuoteFlag()&& !quoteHeader.isFCTToPAQuote()){
			return true;
		}
		return false;
	}
	
	public boolean showLineItemAddresses() { return quote.getQuoteHeader().isDisShipInstAdrFlag(); }
	
	//move to parent class "PartPriceCommon" by lirui at release 14.1
	/*
	public boolean showChannelApJapan(){
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String fullfillment = (null == quoteHeader.getFulfillmentSrc())?"":quoteHeader.getFulfillmentSrc().trim();
		String  region = (null == quoteHeader.getCountry()|| null == quoteHeader.getCountry().getWWRegion()) ? "":quoteHeader.getCountry().getWWRegion().trim();
		return "CHANNEL".equalsIgnoreCase(fullfillment) && ("APAC".equalsIgnoreCase(region)||"JAPAN".equalsIgnoreCase(region));
	}*/
	
	public boolean showOmittedRenwalLine(){
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		if (null != quoteHeader && quoteHeader.isOmittedLine()){
			return true;
		}
		return false;
	}

}