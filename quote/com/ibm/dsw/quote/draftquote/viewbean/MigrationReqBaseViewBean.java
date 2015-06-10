package com.ibm.dsw.quote.draftquote.viewbean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.ibm.dsw.common.base.util.TimeZoneUtils;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>MigrationReqBaseViewBean<code> class.
 *    
 * @author: xiongxj@cn.ibm.com
 * 
 * Creation date: 2012-5-15
 */
public class MigrationReqBaseViewBean extends BaseViewBean {

	private static final long serialVersionUID = 1L;
	protected  String migrationReqNum = "";
	protected  String siteNumber = "";
	protected  String agreementNum = "";
	protected String sapCustNum = "";
	protected String sapCtrctNum = "";
	protected String highlightId = "";
	protected String billingFreq = "";
	protected int coverageTerm;
	protected String caNum = "";
	
	public String getCaNum() {
		return caNum;
	}

	public void setCaNum(String caNum) {
		this.caNum = caNum;
	}

	
	public String getBillingFreq() {
		return billingFreq;
	}

	public void setBillingFreq(String billingFreq) {
		this.billingFreq = billingFreq;
	}

	public int getCoverageTerm() {
		return coverageTerm;
	}

	public void setCoverageTerm(int coverageTerm) {
		this.coverageTerm = coverageTerm;
	}

	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        siteNumber =  params.getParameterAsString(ParamKeys.PARAM_SITE_NUM);
        agreementNum =  params.getParameterAsString(ParamKeys.PARAM_AGREEMENT_NUM);
        migrationReqNum =  params.getParameterAsString(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
        sapCustNum = params.getParameterAsString(DraftQuoteParamKeys.SAP_CUST_NUM);
        sapCtrctNum = params.getParameterAsString(DraftQuoteParamKeys.SAP_CTRCT_NUM);
        highlightId = params.getParameterAsString(DraftQuoteParamKeys.HIGHLIGHT_ID);
        billingFreq = params.getParameterAsString(DraftQuoteParamKeys.BILLING_FREQUENCY);
        coverageTerm = params.getParameterAsString(DraftQuoteParamKeys.COVERAGE_TERM)==null?0:Integer.parseInt(params.getParameterAsString(DraftQuoteParamKeys.COVERAGE_TERM));
        caNum = params.getParameterAsString(DraftQuoteParamKeys.CA_NUM);
    }

	public String getCancelLinkUrl(){
		StringBuffer resultUrl = new StringBuffer();
		String baseUrl = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT);
		resultUrl.append(baseUrl);
		resultUrl.append("&").append(DraftQuoteParamKeys.SAP_CA_NUM).append("=").append(caNum);
		resultUrl.append("&").append(DraftQuoteParamKeys.HIGHLIGHT_ID).append("=").append("current_draft_quote");
		return resultUrl.toString();
	}
	
	public String getSelectParts2BeMigratedUrl() {
		StringBuffer resultUrl = new StringBuffer();
		String baseUrl = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_MIGRATE_PART_LIST);
		resultUrl.append(baseUrl);
		resultUrl.append("&").append(DraftQuoteParamKeys.PARAM_MIGRATION_REQSTD_NUM).append("=").append(migrationReqNum);	
		return resultUrl.toString();
	}
	
    public String getCurrTime()
    {
    	Locale locale = this.getLocale();     
        TimeZone tz = TimeZoneUtils.getTimeZone();
        SimpleDateFormat sdformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz",locale);
        sdformat.setTimeZone(tz);
        String sDate = sdformat.format(new Date()).toString();
        return sDate;
    }
	
	public String getMigrationReqNum() {
		return migrationReqNum == null ? "" : migrationReqNum;
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getSiteNumber() {
		return siteNumber == null ? "" : siteNumber;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public String getAgreementNum() {
		return agreementNum == null ? "" : agreementNum;
	}

	public String getSapCustNum() {
		return sapCustNum == null ? "" : sapCustNum;
	}

	public void setSapCustNum(String sapCustNum) {
		this.sapCustNum = sapCustNum;
	}

	public String getSapCtrctNum() {
		return sapCtrctNum == null ? "" : sapCtrctNum;
	}

	public void setSapCtrctNum(String sapCtrctNum) {
		this.sapCtrctNum = sapCtrctNum;
	}

	public String getHighlightId() {
		return highlightId == null ? "" : highlightId;
	}

	public void setHighlightId(String highlightId) {
		this.highlightId = highlightId;
	}

	public void setAgreementNum(String agreementNum) {
		this.agreementNum = agreementNum;
	}
	
}
