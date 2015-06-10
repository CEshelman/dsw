/*
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author Xiao Guo Yi
 * 
 * Created on 2009-2-5
 */
package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.OmitRenewalLine;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPricingTabURLs;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartsAndPrice;
import com.ibm.dsw.quote.export.ExportExecSummaryUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.BrandTotalPrice;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.BrandTotalPriceCollector;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedPartTable;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.topaz.exception.TopazException;


public class ExecSummaryPartPriceViewBean extends SubmittedQuoteBaseViewBean{
	public SubmittedPartTable partTable;
	
	public PartPricingTabURLs tabURLs;
	
	public PartTableTotal partTableTotal;
	
	public PartsAndPrice partAndPrice;
	
	private String swTotalEntitledExtendedPrice;
	private String saasTotalEntitledExtendedPrice;
	private String monthlyTotalEntitledExtendedPrice;
	private String swTotalBidExtendedPrice;
	private String saasTotalBidExtendedPrice;
	private String monthlyTotalBidExtendedPrice;
	private String HTML_BLANK_SPACE = "&nbsp;";
	
	
    public void collectResults(Parameters params) throws ViewBeanException {
    	super.collectResults(params);
    	
    	partTable = new SubmittedPartTable(quote, getLocale());
    	tabURLs = new PartPricingTabURLs(quote);
    	partTableTotal = new PartTableTotal(quote);
    	partAndPrice = new PartsAndPrice(quote, user);
    	
    	swTotalEntitledExtendedPrice = partTableTotal.getTotalEntitledExtendedPrice(getSoftwareLineItems(), false);
    	saasTotalEntitledExtendedPrice = partTableTotal.getTotalEntitledExtendedPrice(getSaaSLineItemList(), false);
    	monthlyTotalEntitledExtendedPrice =partTableTotal.getTotalEntitledExtendedPrice(getMonthlyLineItems(), false);
    	swTotalBidExtendedPrice = partTableTotal.getTotalBidExtendedPrice(getSoftwareLineItems(), false);
    	saasTotalBidExtendedPrice = partTableTotal.getTotalBidExtendedPrice(getSaaSLineItemList(), false);
    	monthlyTotalBidExtendedPrice = partTableTotal.getTotalBidExtendedPrice(getMonthlyLineItems(), false);
    }
	
    public List getLineItems() {
        return quote.getLineItemList();
    }
    
    public String getDisplayTabAction() {
    	return null;
    }
    
    public int getColSpan(){
    	if(partTable.showChannelMarginCol()){
    		return 7;
    	} else {
    		return 6;
    	}
    }
    
    public int getRowSpan(QuoteLineItem qli){
    	int spanRowsCount = 2;
    	if(partTable.showSubmittedStartDateText(qli)){
    		spanRowsCount++;
    	} 
    	if(partTable.showBillingFrequency(qli)){
    		spanRowsCount++;
    	} 
    	if(partTable.showTermSelection(qli)){
    		spanRowsCount++;
    	}
    	if(!qli.isSaasPart() && !qli.isMonthlySoftwarePart() && partTable.showItemTotContractVal(qli)){
    		spanRowsCount++;
    	} 
    	if(partTable.showRampUpPeriodForSubmitted(qli)){
    		spanRowsCount++;
    	}
    	if(partTable.isAddedFromRenewalQuote(qli) || partTable.showRenewalPricePYP(qli,quoteUserSession) || partTable.showRenewalPriceRSVP(qli,quoteUserSession)
    			  || partTable.showRenewalPriceMethod(qli,quoteUserSession)||GrowthDelegationUtil.isDisplayYTYStatusMessage(quote, qli)
    			  || partTable.showRenewalYTYGrowth(qli)){
    		spanRowsCount++;
    	}
    	if(showLineItemPriorSSEntitledUnitPrice(qli)){
    		spanRowsCount++;
    	}
    	return spanRowsCount;
    }
    
    public int getTotalsColSpan(){
    	if(partTable.showChannelMarginCol()){
    		return 2;
    	} else {
    		return 1;
    	}
    }
    
    public String getProrateMonths(QuoteLineItem qli){
    	int prorateMonths = qli.getProrateMonths();
    	if(prorateMonths > 0 && prorateMonths < 12){
    		return prorateMonths + ""; 
    	}
    	
    	return "-"; 	
    }
    
    public String getProrateWeeks(QuoteLineItem qli){
        int prorateWeeks = qli.getProrateWeeks();
        if(prorateWeeks > 0){
            return prorateWeeks + "";
        }
        
        return "";
    }
    
    public Double getChnlDiscPct(QuoteLineItem qli){
    	if(qli.getChnlOvrrdDiscPct() != null){
    		return qli.getChnlOvrrdDiscPct();
    	}
    	
    	if(qli.getChnlStdDiscPct() != null){
    		return qli.getChnlStdDiscPct();
    	}
    	
    	return new Double(0);
    }
    
    public String getProrateAlign(QuoteLineItem qli){
        if(showProration(qli)){
            return "right";
        }
        
        return "center";
    }
    
    public boolean showProration(QuoteLineItem qli){
        return (partTable.showSubmittedProrateMonth(qli) || partTable.showSubmittedProrateWeek(qli));
    }
    
    public String getTotalDiscFromEntitled(){
    	BrandTotalPriceCollector collector = new BrandTotalPriceCollector(quote);
    	Map sbPriceMap = collector.getSpecialBidPrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,null,PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);
    	Map entitledPriceMap = collector.getEntitledPrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,sbPriceMap,PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);
    	List entitledPrices = collector.convertMap2List(entitledPriceMap);
    	List specialBidPrices = collector.convertMap2List(sbPriceMap);
    	
        double entitledTotal = 0.0;
        double specialTotal = 0.0;
        for (int i = 0; i < entitledPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) entitledPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                    entitledTotal = btp.localCurrencyPrice;
                break;
            }
        }
        if (entitledTotal == 0.0) {
            return "";
        }
        for (int i = 0; i < specialBidPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) specialBidPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                    specialTotal = btp.localCurrencyPrice;
                break;
            }
        }
        
        
        return DecimalUtil.calculateDiscount(specialTotal,entitledTotal);
    }
    
    private String getTotalDisc(String discPrice, String originalPrice){
    	return ExportExecSummaryUtil.calculateDiscount(discPrice, originalPrice);
    }

	public String getSwTotalEntitledExtendedPrice() {
		return swTotalEntitledExtendedPrice;
	}

	public String getSaasTotalEntitledExtendedPrice() {
		return saasTotalEntitledExtendedPrice;
	}

	public String getSwTotalBidExtendedPrice() {
		return swTotalBidExtendedPrice;
	}

	public String getSaasTotalBidExtendedPrice() {
		return saasTotalBidExtendedPrice;
	}
    
    public String getMonthlyTotalEntitledExtendedPrice() {
		return monthlyTotalEntitledExtendedPrice;
	}

	public String getMonthlyTotalBidExtendedPrice() {
		return monthlyTotalBidExtendedPrice;
	}

	public String getSwTotalDiscFromEntitled(){
    	return ExportExecSummaryUtil.calculateDiscount(swTotalBidExtendedPrice, swTotalEntitledExtendedPrice);
    }
    
    public String getSaasTotalDiscFromEntitled(){
    	return ExportExecSummaryUtil.calculateDiscount(saasTotalBidExtendedPrice, saasTotalEntitledExtendedPrice);
    }
    
    public String getMonthlyTotalDiscFromEntitled(){
    	return ExportExecSummaryUtil.calculateDiscount(monthlyTotalBidExtendedPrice, monthlyTotalEntitledExtendedPrice);
    }
    
    public String getLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
	    StringBuffer priorSSEntitledUnitPriceSB = new StringBuffer("");
	    ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
	    /* S&S price is available*/
	    Double lppPrice = GrowthDelegationUtil.getUnitLppPrice(lineItem, getQuote().getQuoteHeader());
	    if (lppPrice != null){
	    	/* Prior S&S price
	    	 * If the prior S&S price currency is different 
	    	 * from the the renewal quote currency, display currency indicator*/
			priorSSEntitledUnitPriceSB
					.append("<span  style=\"color:#cc0000;\">")
					.append(partTable.getFormattedPriceByPartType(lineItem, lppPrice))
					.append("</span>")
					.append(HTML_BLANK_SPACE)
					.append(partTable.getPriorSSCurrncyCode(lineItem))
					.append(HTML_BLANK_SPACE);
	    	
		    /* Evolved part - If evolved part flag is available in SWG ID table, display "(evolved part)" */
		    if(lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isEvolved()){
				priorSSEntitledUnitPriceSB
						.append(context
								.getI18nValueAsString(
										I18NBundleNames.PART_PRICE_MESSAGES,
										locale,
										SubmittedQuoteMessageKeys.AVAILABLE_PRIOR_PRICE_FROM_EVOLVED_PART));
		    }
		   
		}else{
			/*  If no value available, display "(Prior price could not be calculated)" */
			priorSSEntitledUnitPriceSB
					.append(context
							.getI18nValueAsString(
									I18NBundleNames.PART_PRICE_MESSAGES,
									locale,
									SubmittedQuoteMessageKeys.PRIOR_PRICE_COULD_NOT_BE_CALCULATED));
		}
	    return priorSSEntitledUnitPriceSB.toString();
    }
    
    private boolean isApproved(){
    	return this.quote.getQuoteHeader().containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED);
    }
    
    public boolean showLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
    	return StringUtils.isNotBlank(lineItem.getRenewalQuoteNum()) && !isApproved();
    }
    
    public String getRenewalRsvpPrice(QuoteLineItem lineItem, QuoteUserSession userSession){
    	ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
    	StringBuffer rsvpPriceSB = new StringBuffer("");
    	if(partTable.showRenewalPriceRSVP(lineItem,userSession)){
		    String rsvpPrice = partTable.getFormattedPriceByPartType(lineItem,lineItem.getRenewalRsvpPrice());
		    if(StringUtils.isNotBlank(rsvpPrice)){
		    	if(quote.getQuoteHeader().isPAQuote()){
					rsvpPriceSB
							.append("<strong>")
							.append(context.getI18nValueAsString(
									I18NBundleNames.PART_PRICE_MESSAGES,
									locale, SubmittedQuoteViewKeys.RSVP_PRICE))
							.append("</strong>").append(HTML_BLANK_SPACE);
					
		    	}else if(quote.getQuoteHeader().isPAEQuote()){
					rsvpPriceSB
							.append("<strong>")
							.append(context.getI18nValueAsString(
									I18NBundleNames.PART_PRICE_MESSAGES,
									locale, SubmittedQuoteViewKeys.SRP_PRICE))
							.append("</strong>").append(HTML_BLANK_SPACE);
		    	}
		    	rsvpPriceSB.append(rsvpPrice);
		    }
	    }
    	return rsvpPriceSB.toString();
    }
    
    public String getToTalExtendedPriorPrice() {
       List lineItemList = quote.getMasterSoftwareLineItems();
  	   double  totalExtendedPriorPrice = 0;
  	   Double extendedPriorPrice = null;
  	   if(lineItemList != null && lineItemList.size() > 0){
  		   for(int i = 0; i < lineItemList.size(); i++){
  			   QuoteLineItem lineItem =(QuoteLineItem) lineItemList.get(i);
  			   
  			  if(lineItem.getRenewalQuoteNum() != null && !"".equals(lineItem.getRenewalQuoteNum().trim())){
	  				 if(!lineItem.isApplncPart()){
		  				   extendedPriorPrice = GDPartsUtil.getExtndLppPrice(lineItem, quote.getQuoteHeader());
		  				   if(extendedPriorPrice != null ){
		    				   totalExtendedPriorPrice = totalExtendedPriorPrice + extendedPriorPrice;
		    			   }
	  				 }
  			   }
  			   
  		   }
  	   }
  	  double omittedTotalExtendPriorPrice = 0;
 	   OmitRenewalLine omitRenewalLine = null;
 	   Double omitRenewalLinePrice = null;
 	   if(quote.getQuoteHeader() != null && quote.getQuoteHeader().isOmittedLine()){
 		   try {
 			omitRenewalLine = QuoteLineItemFactory.singleton().getOmittedRenewalLine(quote.getQuoteHeader().getWebQuoteNum());
 		} catch (TopazException e) {
 		
 			e.printStackTrace();
 		}
 	   }
 	   if(omitRenewalLine != null){
 		   omitRenewalLinePrice = omitRenewalLine.getOmittedLinePrice();
 	   }
 	   if(omitRenewalLinePrice != null){
 		   omittedTotalExtendPriorPrice = omitRenewalLinePrice.doubleValue();
 	   }
 	   if(quote.getQuoteHeader() != null && quote.getQuoteHeader().isOmittedLine()){
 		   if(totalExtendedPriorPrice > 0 || omittedTotalExtendPriorPrice > 0 ){
 			   return DecimalUtil.format(totalExtendedPriorPrice + omittedTotalExtendPriorPrice,2);
 		   }
 		   
 	   }else if(quote.getQuoteHeader() != null && quote.getQuoteHeader().isOmittedLine() == false){
 		   if(totalExtendedPriorPrice > 0 ){
 			   return DecimalUtil.format(totalExtendedPriorPrice ,2);  
 		   }
 		  
 	   }else{
 		   return "";
 	   }
 		return "";
  			 
  		
     }
}
