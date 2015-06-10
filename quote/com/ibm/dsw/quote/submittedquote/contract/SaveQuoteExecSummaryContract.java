/*
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author Xiao Guo Yi
 * 
 * Created on 2009-2-11
 */
package com.ibm.dsw.quote.submittedquote.contract;

import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;


public class SaveQuoteExecSummaryContract extends SubmittedQuoteBaseContract{
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	private Boolean recmdtFlag;
	private String apprRecmd;
	private Double periodBookableRevenue;
	private Double serviceRevenue;
	private String termConditions;
	private String execSupport;
	private String briefOverview;
	
	private boolean bookableRevenueValid = true;
	private boolean servicesValid = true;
	
	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
		
		String flag = parameters.getParameterAsString(SubmittedQuoteParamKeys.EXEC_RECMD_FLAG);
		logger.debug(this, "SaveQuoteExecSummaryContract.recmdtFlag = " + flag);
		if(!StringUtils.isEmpty(flag)){
			recmdtFlag = Boolean.valueOf(flag);
		}
		
		String bookableRevenue = parameters.getParameterAsString(SubmittedQuoteParamKeys.EXEC_PERIOD_BOOKABLE_REVENUE);
		logger.debug(this, "SaveQuoteExecSummaryContract.periodBookableRevenue = [" + bookableRevenue + "]");
		if (!StringUtils.isEmpty(bookableRevenue)) {
			try {
				periodBookableRevenue = getDoubleValue(bookableRevenue);
			} catch (Exception e) {
				bookableRevenueValid = false;
			}
		}
		
		String serv = parameters.getParameterAsString(SubmittedQuoteParamKeys.EXEC_SERVICE_REVENUE);
		logger.debug(this, "SaveQuoteExecSummaryContract.services = [" + serv + "]");
		if (!StringUtils.isEmpty(serv)) {
			try {
				serviceRevenue = getDoubleValue(serv);
			} catch (Exception e) {
				servicesValid = false;
			}
		}
	}
	
	private Double getDoubleValue(String strDouble) throws Exception{
		DecimalFormat format = new DecimalFormat("#,#");
		double tmp = format.parse(strDouble).doubleValue();
		return new Double(tmp);
	}
	/**
	 * @return Returns the apprRecmd.
	 */
	public String getApprRecmd() {
		return apprRecmd;
	}
	/**
	 * @param apprRecmd The apprRecmd to set.
	 */
	public void setApprRecmd(String apprRecmd) {
		this.apprRecmd = apprRecmd;
	}
	/**
	 * @return Returns the briefOverview.
	 */
	public String getBriefOverview() {
		return briefOverview;
	}
	/**
	 * @param briefOverview The briefOverview to set.
	 */
	public void setBriefOverview(String briefOverview) {
		this.briefOverview = briefOverview;
	}
	/**
	 * @return Returns the execSupport.
	 */
	public String getExecSupport() {
		return execSupport;
	}
	/**
	 * @param execSupport The execSupport to set.
	 */
	public void setExecSupport(String execSupport) {
		this.execSupport = execSupport;
	}
	/**
	 * @return Returns the periodBookableRevenue.
	 */
	public Double getPeriodBookableRevenue() {
		return periodBookableRevenue;
	}
	/**
	 * @param periodBookableRevenue The periodBookableRevenue to set.
	 */
	public void setPeriodBookableRevenue(Double periodBookableRevenue) {
		this.periodBookableRevenue = periodBookableRevenue;
	}
	/**
	 * @return Returns the recmdtFlag.
	 */
	public Boolean getRecmdtFlag() {
		return recmdtFlag;
	}
	/**
	 * @param recmdtFlag The recmdtFlag to set.
	 */
	public void setRecmdtFlag(Boolean recmdtFlag) {
		this.recmdtFlag = recmdtFlag;
	}
	/**
	 * @return Returns the termConditions.
	 */
	public String getTermConditions() {
		return termConditions;
	}
	/**
	 * @param termConditions The termConditions to set.
	 */
	public void setTermConditions(String termConditions) {
		this.termConditions = termConditions;
	}
	
	public boolean isBookableRevenueValid(){
		return this.bookableRevenueValid;
	}
	
	public boolean isServicesValid(){
		return this.servicesValid;
	}
	/**
	 * @return Returns the services.
	 */
	public Double getServiceRevenue() {
		return serviceRevenue;
	}
	/**
	 * @param services The services to set.
	 */
	public void setServiceRevenue(Double serviceRevenue) {
		this.serviceRevenue = serviceRevenue;
	}
}
