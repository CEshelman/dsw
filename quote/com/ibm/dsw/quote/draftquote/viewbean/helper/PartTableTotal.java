package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.EquityCurve;
import com.ibm.dsw.quote.common.domain.EquityCurveTotal;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.retrieval.config.RetrieveQuoteConstant;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartTableTotalViewBean</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class PartTableTotal extends PartPriceCommon {
    protected UIFormatter formatter ;

    public PartTableTotal(Quote quote)
    {
        super(quote);
        formatter  = new UIFormatter(quote);
    }
    /**
     * Added by lee,March 30,2007
     * 
     * @return
     */
    public boolean showPartTableTotalsSection() {
        if (isPA() || isPAE() || isFCT() || isPPSS() || isOEM()|| isSSP()) {
            return hasLineItems();
        }
        return false;
    }
    
    public boolean showSubmittedPartTableTotalsSection() {
        if (hasSubmittedLineItems()) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @return
     */
    public boolean showTotalPoints() {
        return (isPA() || isPAE() || isSSP());
    }

    /**
     * @return
     */
    public boolean showTotalPrice() {
        if (isPA() || isPAE() || isFCT() || isPPSS() || isOEM() || isSSP()) {
            return showPartTableTotalsSection();
        }
        return false;
    }
    
    /**
     * @return
     */
    public boolean showSubmittedTotalPrice() {
        if (isPA() || isPAE() || isFCT() || isPPSS() || isOEM() || isSSP()) {
            return showSubmittedPartTableTotalsSection();
        }
        return false;
    }

    /**
     * @return true if any parts have been selected
     */
    public boolean showRecalculateQuoteLink() {
        if (isPA() || isPAE() || isFCT() || isPPSS() || isOEM() || isSSP()) {
            return showPartTableTotalsSection();
        }
        return false;
    }
    
    /**
     * display if the quote contains any parts
     * @return
     */
    public boolean showSubmittedTotalSection() {
        if (isPA() || isPAE() || isPPSS() || isFCT() || isOEM() || isSSP()) {
            return hasSubmittedLineItems();    
        }
        
        return false;
    }
    
    /**
     * @return
     */
    public String getRegionBaseCurrency() {
        String geo = quote.getQuoteHeader().getPriceCountry().getSpecialBidAreaCode();
        String localCurrency= quote.getQuoteHeader().getCurrencyCode();
        if (QuoteConstants.GEO_EMEA.equals(geo))   {
            return PartPriceConstants.RGNBASECUR_EUR;
        }
        else return PartPriceConstants.RGNBASECUR_USD;
    }
    
    /**
     * @return
     */
    public boolean showChannelPriceSummary() {
        String fulfillSrc = quote.getQuoteHeader().getFulfillmentSrc();
        
        if (fulfillSrc != null && fulfillSrc.equalsIgnoreCase(RetrieveQuoteConstant.FULFILLMENT_CHANNEL)) {
            return true;
        }
        return false;
    }
    
    
    /**
     * @param liList (quotelineitem list), can be lineItemList, saasLineItemList, nonSaasLineItemList
     * @param isPriceUnAvaialbe
     * @return
     * String
     */
    public String getTotalEntitledExtendedPrice(List liList, boolean isPriceUnAvaialbe) {
        double totalEntitledExtendedPrice = 0;
        boolean allUnavailable = true;
        
        if (liList !=  null && liList.size() != 0) {
            for (int i=0;i<liList.size();i++) {
                QuoteLineItem lineItem = (QuoteLineItem)liList.get(i);
                if(skipCalcltPointAndPrice(lineItem)){
                	continue;
                }
                if (showLineitemPrice(lineItem,isPriceUnAvaialbe)){
                	allUnavailable = false;
                	if(lineItem.isSaasTcvAcv()){
                		if(lineItem.getSaasEntitledTCV() != null){
                			totalEntitledExtendedPrice = totalEntitledExtendedPrice + lineItem.getSaasEntitledTCV().doubleValue();
                		}
                	}else{
                		if(lineItem.getLocalExtProratedPrc() != null){
                			totalEntitledExtendedPrice = totalEntitledExtendedPrice + lineItem.getLocalExtProratedPrc().doubleValue();
                		}
                	}
                }
            }
        }
        
        if (allUnavailable || totalEntitledExtendedPrice == 0) {
            return DraftQuoteConstants.BLANK;
        }
        else {
            return getFormattedPrice(totalEntitledExtendedPrice);
        }
    }
    
    /**
     * @param liList (quotelineitem list), can be lineItemList, saasLineItemList, nonSaasLineItemList
     * @param isPriceUnAvaialbe
     * @return
     * String
     */
    public String getTotalBidExtendedPrice(List liList, boolean isPriceUnAvaialbe) {
        double totalBidExtendedPrice = 0;
        boolean allUnavailable = true;
        
        if (liList !=  null && liList.size() != 0) {
            for (int i=0;i<liList.size();i++) {
                QuoteLineItem lineItem = (QuoteLineItem)liList.get(i);
                if(skipCalcltPointAndPrice(lineItem)){
                	continue;
                }
                if (showLineitemPrice(lineItem,isPriceUnAvaialbe)){
                	allUnavailable = false;
                	if(lineItem.isSaasTcvAcv()){
                		if(lineItem.getSaasBidTCV() != null){
                			totalBidExtendedPrice = totalBidExtendedPrice + lineItem.getSaasBidTCV().doubleValue();
                		}
                	}else{
                		if(lineItem.getLocalExtProratedDiscPrc() != null){
                			totalBidExtendedPrice = totalBidExtendedPrice + lineItem.getLocalExtProratedDiscPrc().doubleValue();
                		}
                	}
                }
            }
        }
        
        if (allUnavailable || totalBidExtendedPrice == 0) {
            return DraftQuoteConstants.BLANK;
        }
        else {
            return getFormattedPrice(totalBidExtendedPrice);
        }
    }
    
    /**
     * @param liList (quotelineitem list), can be lineItemList, saasLineItemList, nonSaasLineItemList
     * @param isPriceUnAvaialbe
     * @return
     * String
     */
    public String getTotalBpExtendedPrice(List liList, boolean isPriceUnAvaialbe) {
        double totalBpExtendedPrice = 0;
        boolean allUnavailable = true;
        
        if (liList !=  null && liList.size() != 0) {
            for (int i=0;i<liList.size();i++) {
                QuoteLineItem lineItem = (QuoteLineItem)liList.get(i);
                if(skipCalcltPointAndPrice(lineItem)){
                	continue;
                }
                if (showLineitemPrice(lineItem,isPriceUnAvaialbe) && (lineItem.getChannelExtndPrice()!=null)) {
                    totalBpExtendedPrice = totalBpExtendedPrice + lineItem.getChannelExtndPrice().doubleValue();
                    allUnavailable = false;
                }
            }
        }
        
        if (allUnavailable || totalBpExtendedPrice == 0) {
            return DraftQuoteConstants.BLANK;
        }
        else {
            return getFormattedPrice(totalBpExtendedPrice);
        }
    }
    
    
    /**
     * @param lineItemList
     * @return
     * String, if param is nonSaaS line item list, return nonSaaS total point
     *         if param is SaaS line item list, return SaaS total point
     * get the total points
     */
    public String calculateTotalPoint(List lineItemList){

        double quoteTotalPoints = 0;
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
            if(skipCalcltPointAndPrice(item)){
            	continue;
            }
            int qty = 1;
            if (item.getPartQty() != null) {
                qty = item.getPartQty().intValue();
            }
            double lineItemTotalPoint = item.getContributionUnitPts() * qty;            
            quoteTotalPoints += lineItemTotalPoint;
        }
        return formatter.formatPoint(quoteTotalPoints);

    }
    
    public boolean skipCalcltPointAndPrice(QuoteLineItem qli){
    	return ((qli.isSaasPart() || qli.isMonthlySoftwarePart()) && qli.isReplacedPart());
    }
    
    /**
     * @param liList (quotelineitem list), can be lineItemList, saasLineItemList, nonSaasLineItemList
     * @param isPriceUnAvaialbe
     * @return
     * String
     */
    public String getTotalBpTCV(List liList, boolean isPriceUnAvaialbe) {
        double totalBpTCV = 0;
        boolean allUnavailable = true;
        
        if (liList !=  null && liList.size() != 0) {
            for (int i=0;i<liList.size();i++) {
                QuoteLineItem lineItem = (QuoteLineItem)liList.get(i);
                if(skipCalcltPointAndPrice(lineItem) || CommonServiceUtil.checkIsUsagePart(lineItem)){
                	continue;
                }
                if(lineItem.isSaasTcvAcv()){
	                if (showLineitemPrice(lineItem,isPriceUnAvaialbe) && (lineItem.getSaasBpTCV()!=null)) {
	                	totalBpTCV = totalBpTCV + lineItem.getSaasBpTCV().doubleValue();
	                    allUnavailable = false;
	                }
                }else{
                	if (showLineitemPrice(lineItem,isPriceUnAvaialbe) && (lineItem.getChannelExtndPrice()!=null)) {
	                	totalBpTCV = totalBpTCV + lineItem.getChannelExtndPrice().doubleValue();
	                    allUnavailable = false;
	                }
                }
            }
        }
        
        if (allUnavailable || totalBpTCV == 0) {
            return DraftQuoteConstants.BLANK;
        }
        else {
            return getFormattedPrice(totalBpTCV);
        }
    }
    
    
	public String getOverallGrowth() {
		if (quote.getQuoteHeader() != null && quote.getQuoteHeader().getYtyGrwthPct() != null){
			return DecimalUtil.formatToTwoDecimals(quote.getQuoteHeader().getYtyGrwthPct());
		}else{
			return DraftQuoteConstants.BLANK;
		}
	}

	public String getImpliedGrowth() {
		if (quote.getQuoteHeader() != null && quote.getQuoteHeader().getImpldGrwthPct() != null){
			return DecimalUtil.formatToTwoDecimals( quote.getQuoteHeader().getImpldGrwthPct());
		}else{
			return DraftQuoteConstants.BLANK;
		}
	}

	public boolean showOverallGrowth() {
		return GrowthDelegationUtil.isDisplayOverallYTYTotal(quote);
	}

	public boolean showImpliedGrowth() {
		return GrowthDelegationUtil.isDisplayImpliedYTYTotal(quote);
	}
	
	/**
	 * judge whether the total recommended discount is showed
	 * @return true if 
	 */
	public boolean showTotalRecommendDiscount()
	{
		List lineItems = quote.getMasterSoftwareLineItems();
		List saasLineItems = quote.getSaaSLineItems();
		if (null == lineItems || lineItems.isEmpty() ||( null != saasLineItems && !saasLineItems.isEmpty())){
			return false;
		}
		boolean equityCurveCtrolFlag = false;
		 for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
	            QuoteLineItem item = (QuoteLineItem) itemIt.next();
                EquityCurve equityCurve = item.getEquityCurve();
	            if (null == equityCurve || !equityCurve.isEquityCurveFlag()){
	            	return false;
	            }
	            if (equityCurve.isEquityCurveCtrolFlag()){
	            	equityCurveCtrolFlag = true;
	            }
		 }
	            
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		if (null == quoteHeader){
			return false;
		}
		return equityCurveCtrolFlag && !quoteHeader.isBidIteratnQt() && !quoteHeader.isCopied4PrcIncrQuoteFlag();
	}
	
	public boolean isShowECInfo(){
		List lineItems = quote.getMasterSoftwareLineItems();
		for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();){
			QuoteLineItem item = (QuoteLineItem) itemIt.next();
			if (null == item || null == item.getLocalUnitProratedPrc()) return false;
			else{
				EquityCurve equityCurve = item.getEquityCurve();
				if(null == equityCurve ||  null == equityCurve.getMinDiscount() || null == equityCurve.getMaxDiscount())
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * judge whether the purchase history is showing
	 * @param lineItem
	 * @return
	 */
	public boolean showPurchaseHistAppliedFlag(){
		List lineItems = quote.getMasterSoftwareLineItems();
		List saasLineItems = quote.getSaaSLineItems();
		if (null == lineItems || lineItems.isEmpty()){
			return false;
		}
		 for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
	            QuoteLineItem item = (QuoteLineItem) itemIt.next();
                EquityCurve equityCurve = item.getEquityCurve();
                if (null != equityCurve && !QuoteConstants.EC_QT_PURCH_HIST_NO.equalsIgnoreCase(equityCurve.getPurchaseHistAppliedFlag())){
	            	return true;
	            }
		 }
		 return false;
	}
	
	/**
	 * format the minimum discount of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getTotalMinDiscount(){
		if (null == quote.getQuoteHeader()){
			return DraftQuoteConstants.BLANK;
		}
		
		EquityCurveTotal equityCurveTotal = quote.getQuoteHeader().getEquityCurveTotal();
		if(null == equityCurveTotal ||null == equityCurveTotal.getWeightAverageMin()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(equityCurveTotal.getWeightAverageMin(),2) + " %";
	}
	
	/**
	 * format the maximum discount of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getTotalMaxDiscount(){
		if (null == quote.getQuoteHeader()){
			return DraftQuoteConstants.BLANK;
		}
		
		EquityCurveTotal equityCurveTotal = quote.getQuoteHeader().getEquityCurveTotal();
		if(null == equityCurveTotal ||null == equityCurveTotal.getWeightAverageMax()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(equityCurveTotal.getWeightAverageMax(),2) + " %";
	}
	
	/**
	 * format the minimum bid unit price of the equity curve
	 * @return
	 */
	public String getTotalMinUnitPrice(){
		if (null == quote.getLineItemList()|| quote.getLineItemList().isEmpty() ){
			return DraftQuoteConstants.BLANK;
		}
		
		EquityCurveTotal equityCurveTotal = quote.getQuoteHeader().getEquityCurveTotal();
		equityCurveTotal.setTotalBidUnitPriceMin(quote.getLineItemList());
		if(null == equityCurveTotal ||null == equityCurveTotal.getTotalBidUnitPriceMin()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(equityCurveTotal.getTotalBidUnitPriceMin(),2);
	}
	
	/**
	 * format the maximum bid unit price of the equity curve
	 * @return
	 */
	public String getTotalMaxUnitPrice(){
		if (null == quote.getLineItemList()|| quote.getLineItemList().isEmpty() ){
			return DraftQuoteConstants.BLANK;
		}
		
		EquityCurveTotal equityCurveTotal = quote.getQuoteHeader().getEquityCurveTotal();
		equityCurveTotal.setTotalBidUnitPriceMax(quote.getLineItemList());
		if(null == equityCurveTotal ||null == equityCurveTotal.getTotalBidUnitPriceMax()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(equityCurveTotal.getTotalBidUnitPriceMax(),2);
	}
	/**
	 * format the minimum bid extended price of the equity curve
	 * @return
	 */
	public String getTotalMinExtendedPrice(){
		if (null == quote.getLineItemList()|| quote.getLineItemList().isEmpty() ){
			return DraftQuoteConstants.BLANK;
		}
		
		EquityCurveTotal equityCurveTotal = quote.getQuoteHeader().getEquityCurveTotal();
		if (null == equityCurveTotal ){
			return DraftQuoteConstants.BLANK;
		}
		equityCurveTotal.setTotalBidExtendedPriceMin(quote.getLineItemList());
		if(null == equityCurveTotal.getTotalBidExtendedPriceMin()){
			return DraftQuoteConstants.BLANK;
		}
		String totalMinExtendedPrice =  DecimalUtil.format(equityCurveTotal.getTotalBidExtendedPriceMin(),2);
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String  region = (null == quoteHeader.getCountry()|| null == quoteHeader.getCountry().getWWRegion()) ? "":quoteHeader.getCountry().getWWRegion().trim();
		if ("JAPAN".equalsIgnoreCase(region) && totalMinExtendedPrice.endsWith(".00")){
			return totalMinExtendedPrice.substring(0, totalMinExtendedPrice.length()-3);
		}
		return totalMinExtendedPrice;
	}
	
	/**
	 * format the maximum bid extended price of the equity curve
	 * @return
	 */
	public String getTotalMaxExtendedPrice(){
		if (null == quote.getLineItemList()|| quote.getLineItemList().isEmpty() ){
			return DraftQuoteConstants.BLANK;
		}
		
		EquityCurveTotal equityCurveTotal = quote.getQuoteHeader().getEquityCurveTotal();
		if (null == equityCurveTotal ){
			return DraftQuoteConstants.BLANK;
		}
		equityCurveTotal.setTotalBidExtendedPriceMax(quote.getLineItemList());
		if(null == equityCurveTotal.getTotalBidExtendedPriceMax()){
			return DraftQuoteConstants.BLANK;
		}
		String totalMaxExtendedPrice  = DecimalUtil.format(equityCurveTotal.getTotalBidExtendedPriceMax(),2);
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String  region = (null == quoteHeader.getCountry()|| null == quoteHeader.getCountry().getWWRegion()) ? "":quoteHeader.getCountry().getWWRegion().trim();
		if ("JAPAN".equalsIgnoreCase(region) && totalMaxExtendedPrice.endsWith(".00")){
			return totalMaxExtendedPrice.substring(0, totalMaxExtendedPrice.length()-3);
		}
		return totalMaxExtendedPrice;
	}
	
	public boolean showTotalMaxExtendedPrice(){
		List quoteLineItemList = quote.getLineItemList();
		if(quoteLineItemList != null && quoteLineItemList.size() >0){
			for(int i = 0; i <quoteLineItemList.size(); i++ ){
				QuoteLineItem qli = (QuoteLineItem)quoteLineItemList.get(i);
				if(qli.getRenewalQuoteNum() != null && !"".equals(qli.getRenewalQuoteNum().trim())){
					return true;
				}
				
			}
			
		}
		return false;
	}
	
	public boolean isContainRenewalPartForGD(){
		
		return GDPartsUtil.isQuoteEligibleForRenewalGrowthDelegation(quote);
	}
}