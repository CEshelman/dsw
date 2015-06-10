package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartInfoViewBean</code>
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class PartInfo extends PartPriceCommon {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5193680714601483494L;

	public PartInfo(Quote quote) {
        super(quote);
    }

    /**
     * @return true if any parts have been selected
     */
    public boolean showPartInfoSection() {
        if (hasLineItems()) {
            return true;
        }

        return false;
    }

    /**
     * Shows the quote header discount percentage
     * 
     * @return true if If quote contains at least one contract part
     */
    public boolean showQuoteHeaderDiscountPct() {
    	if(CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())
    		&& (quote.getSoftwareLineItems() == null || quote.getSoftwareLineItems().size() == 0 )){
            return false;
        }
    	
    	//if the quote is bid iterator part, don't show discount;
//    	if( quote.getQuoteHeader() != null && quote.getQuoteHeader().isBidIteratnQt())
    	//	return false;
    	
    	List lineItemList = quote.getLineItemList();
        
        //return false if quote has no line items
        if(lineItemList == null || lineItemList.size() == 0){
            return false;
        }
        
        int len = lineItemList.size();
        
        if (isPA() || isPAE() || isOEM() || isSSP()) {
            for (int i = 0; i < len; i++) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItemList.get(i);
                if (isContractPart(lineItem) && !lineItem.isObsoletePart() && (!lineItem.hasValidCmprssCvrageMonth())) {
                    return true;
                }
            }
        }

        if (isFCT()) {
            for (int i = 0; i < len; i++) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItemList.get(i);
                if (!lineItem.isObsoletePart() && (!lineItem.hasValidCmprssCvrageMonth())) {
                    return true;
                }
            }
        }

        return false;
    }
    
    public boolean showCompressedCovrgCheckBox(){
        if(!PartPriceConfigFactory.singleton().allowCmprssCvrage(quote.getQuoteHeader().getLob().getCode())){
            return false;
        }
        
        List masterLineItems = quote.getMasterSoftwareLineItems();
        
        //return false if quote has no line items
        if(masterLineItems == null || masterLineItems.size() == 0){
            return false;
        }
        
        //Back-dating and compressed coverage are mutually exclusive.
        if(quote.getQuoteHeader().getBackDatingFlag()){
            return false;
        }
        
        for(Iterator it = masterLineItems.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            
            if(qli.isEligibleForCmprssCvrage()){
                return true;
            }
        }
        
        return false;
    }

    /**
     * Shows the apply offer
     * 
     * @return
     */
    public boolean showApplyOffer() {
        List lineItemList = quote.getLineItemList();
        
        //return false if quote has no line items
        if(lineItemList == null || lineItemList.size() == 0){
            return false;
        }
        
        int len = lineItemList.size();
        
        if (isPA() || isPAE() || isOEM() || isSSP()) {
            for (int i = 0; i < len; i++) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItemList.get(i);
                if(lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()){
                	if(showSaaSBidExtPrice(lineItem) && !lineItem.isReplacedPart()){
                		return true;
                	}
                } else {
                    if (isContractPart(lineItem) && !lineItem.isObsoletePart() && (!lineItem.hasValidCmprssCvrageMonth())) {
                        return true;
                    }
                }
            }
        }

        if (isFCT()) {
            for (int i = 0; i < len; i++) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItemList.get(i);
                
                if(lineItem.isSaasPart()){
                	if(showSaaSBidExtPrice(lineItem) && !lineItem.isReplacedPart()){
                		return true;
                	}
                } else {
                    if (!lineItem.isObsoletePart() && (!lineItem.hasValidCmprssCvrageMonth())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Shows the clear current offer link
     * 
     * @return
     */
    public boolean showClearOffer() {
        if (isPA() || isPAE() || isFCT() ||isOEM() || isSSP()) {
            //if contract part exists and offer price is not null
            if (quote.getQuoteHeader().getOfferPrice() != null) {
                return true;
            }
        }

        return false;
    }

    public boolean showOfferPrice() {
        if (isPA() || isPAE() || isFCT() || isOEM()) {
            //if offer price is not null
            if (quote.getQuoteHeader().getOfferPrice() != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get current offer value
     * 
     * @return
     */
    public String getCurrentOffer() {
    	// the "null" is not necessary!
        if (quote.getQuoteHeader().getOfferPrice() == null){
            return "";
        }else{
            return StringUtils.isBlank(quote.getQuoteHeader().getOfferPrice().toString())? "": formatter.formatEndCustomerPrice(quote.getQuoteHeader().getOfferPrice().doubleValue());
        }
    }
    
    public String getYtyGrowthDelegation() {

//        if (quote.getQuoteHeader().getYtyGrwthPct() == null){
//            return "";
//        }else{
//            return StringUtils.isBlank(quote.getQuoteHeader().getYtyGrwthPct().toString())? "": formatter.formatEndCustomerPrice(quote.getQuoteHeader().getYtyGrwthPct().doubleValue());
//        }
    	return "";
    }
    
    
    
    public boolean showPrcBandOvrrd(){
        boolean tranLvlOvrrden = (quote.getQuoteHeader().isSalesQuote() && (isPA() || isOEM())
                         && StringUtils.isNotBlank(quote.getQuoteHeader().getOvrrdTranLevelCode()));
        if(!tranLvlOvrrden){
            return false;
        }
        
        List items = quote.getLineItemList();
        if(items != null && items.size() > 0){
            for(Iterator it = items.iterator(); it.hasNext(); ){
                QuoteLineItem qli = (QuoteLineItem)it.next();
                //Exclude cmprss cvrage applied line item
                if(qli.isContractPart() && (!qli.hasValidCmprssCvrageMonth())){
                    return true;
                }
            }
        }
        
        return false;
    }
}