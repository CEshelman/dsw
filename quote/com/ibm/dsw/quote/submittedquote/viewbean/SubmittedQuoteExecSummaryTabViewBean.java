/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.viewbean;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummary;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 *
 * Created At: 2009-2-5
 */
public class SubmittedQuoteExecSummaryTabViewBean extends
		SubmittedQuoteBaseViewBean {
	
	private UIFormatter uiFormater;
	
	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		
		uiFormater = new UIFormatter(quote);
	}
	
	
	public boolean isCanEditExecutiveSummary(){		
		return quote.getQuoteUserAccess().isCanEditExecSummary(user.getAccessLevel(QuoteConstants.APP_CODE_SQO));
	}
	
	public boolean isCanViewExecutiveSummary(){
		return quote.getQuoteUserAccess().isCanViewExecSummry(user.getAccessLevel(QuoteConstants.APP_CODE_SQO));
	}
	
	public ExecSummary getExecSummary(){
		return quote.getExecSummary();
	}
	
	public String getSpecialBidRegion(){
		return quote.getSpecialBidInfo().getSpBidRgn();
	}
	
	public String getSpecialBidDistrict(){
		return quote.getSpecialBidInfo().getSpBidDist();
	}
	
	public SalesRep getQuoteCreator(){
		return quote.getCreator();
	}
	
	public SalesRep getOppOwner(){
		return quote.getOppOwner();
	}
	
	public String getFulfillmentSource(){
		return quote.getQuoteHeader().getFulfillmentSrc();
	}
	
	public String getDisplayTabAction() {
		return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_EXEC_SUMMARY_TAB;
	}
	
	public String getRecmdtText(){
		return StringUtils.trimToEmpty(getExecSummary().getRecmdtText());
	}
	
	public String getTermCondText(){
		if (StringUtils.isEmpty(getExecSummary().getTermCondText())
				&& (!quote.getSpecialBidInfo().isTermsAndCondsChg())) {
			return getI18NString(I18NBundleNames.BASE_MESSAGES,
					SubmittedQuoteViewKeys.EXEC_TERM_COND_NA);
		}
		return getExecSummary().getTermCondText();
	}
	
	public String getExecSupport(){
		return StringUtils.trimToEmpty(getExecSummary().getExecSupport());
	}
	
	public String getBriefOverviewText(){
		return StringUtils.trimToEmpty(getExecSummary().getBriefOverviewText());
	}
	
	public boolean isDisplaySVPLevel(){
		return (quote.getQuoteHeader().isPAEQuote() || quote.getQuoteHeader().isPAQuote());
	}
	
	public String getSVPLevel(String resource){
		QuoteHeader header = quote.getQuoteHeader();
		if(header.isPAQuote()){
		    return header.getTranPriceLevelCode();
		}
		
		if(header.isPAEQuote()){
			return getI18NString(resource, SubmittedQuoteViewKeys.PAE_SVP_LEVEL_VALUE);
		}
		
		return "";
	}
	
	public String getEntitledTotalPrice(){
		return getFormattedPrice(getExecSummary().getEntitledTotalPrice())+" "+getLocalCurrencyCode();
	}
	
	public String getSpecialBidTotalPrice(){
		return getFormattedPrice(getExecSummary().getSpecialBidTotalPrice())+" "+getLocalCurrencyCode();
	}
	
	public String getLocalCurrencyCode(){
		return quote.getQuoteHeader().getCurrencyCode();
	}
	
	public String getPeriodBookableRevenue(){
		if(getExecSummary().getPeriodBookableRevenue() == null){
			return "";
		}
		return uiFormater.formatEndCustomerPrice(getExecSummary().getPeriodBookableRevenue().doubleValue());
	}
	
	public String getServiceRevenue(){
		if(getExecSummary().getServiceRevenue() == null){
			return "";
		}
		return uiFormater.formatEndCustomerPrice(getExecSummary().getServiceRevenue().doubleValue());
	}
	
	public boolean hasBLPrice(){

		return (quote.getQuoteHeader().getSpeclBidFlag() == 1
				&& quote.getQuoteHeader().isPAQuote()
				&& !DecimalUtil.isEqual(getExecSummary().getBaselineTotalPrice().doubleValue(), 0));
	}
	
	public String getQuoteCreatorBluePageURL() {
        return getFormattedBluePageURL(getQuoteCreator().getEmailAddress());
    }

    public String getOppOwnerBluePageURL() {
        return getFormattedBluePageURL(getOppOwner().getEmailAddress());
    }

    private String getFormattedBluePageURL(String email) {
        return ApplicationProperties.getInstance().getBluePageSimpleSearchURL() + email;
    }
	
	private String getFormattedPrice(Double price){
		if(price == null){
			price = new Double(0);
		}
		return uiFormater.formatEndCustomerPrice(price.doubleValue());
	}
	
	public String getSaveExecSummaryURL(){
		return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SAVE_QUOTE_EXEC_SUMMARY_TAB, SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_EXEC_SUMMARY_TAB);
	}
	
	public String getDownloadExecSummaryAsRTFURL(){
        if(this.isCanEditExecutiveSummary()){
        	return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SAVE_QUOTE_EXEC_SUMMARY_TAB, 
				                            SubmittedQuoteActionKeys.SUBMIT_QUOTE_EXEC_SUMMARY_RTF_DOWNLOAD);
        } else {
        	return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMIT_QUOTE_EXEC_SUMMARY_RTF_DOWNLOAD, null);
        }
		               
	}
	
	public String getDownloadExecSummaryAsPDFURL(){
        if(this.isCanEditExecutiveSummary()){
        	return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SAVE_QUOTE_EXEC_SUMMARY_TAB, 
				                            SubmittedQuoteActionKeys.SUBMIT_QUOTE_EXEC_SUMMARY_PDF_DOWNLOAD);
        } else {
        	return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMIT_QUOTE_EXEC_SUMMARY_PDF_DOWNLOAD, null);
        }
		               
	}
	
	public String getViewOmitLineURL(){
       return HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_SUBMITTED_OMITTED_LINEITEM, null,"redirectMsg=execSummaryTab");
	}
}
