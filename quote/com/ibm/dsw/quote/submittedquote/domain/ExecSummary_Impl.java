/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.domain;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 *
 * Created At: 2009-2-5
 */

public abstract class ExecSummary_Impl implements ExecSummary {

	public String webQuoteNum;
	
	public String userId;
	
	public Double periodBookableRevenue;
	
	public Double serviceRevenue;
	
	public Boolean recmdtFlag;
	
	public String recmdtText;
	
	public String execSupport;
	
	public String termCondText;
	
	public String briefOverviewText;
	
	public Double baselineTotalPrice;
	
	public Double entitledTotalPrice;
	
	public Double specialBidTotalPrice;
	
	public String totalDiscOffList;
	
	public String totalDiscOffEntitled;
	
	public String maxDiscPct;
	
	
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	
	public String getUserId(){
		return userId;
	}

	public Boolean getRecmdtFlag() {
		return recmdtFlag;
	}

	public String getRecmdtText() {
		return recmdtText;
	}

	public String getExecSupport() {
		return execSupport;
	}

	public String getTermCondText() {
		return termCondText;
	}

	public String getBriefOverviewText() {
		return briefOverviewText;
	}

	public Double getBaselineTotalPrice() {
		return baselineTotalPrice;
	}
	
	public Double getEntitledTotalPrice() {
		return entitledTotalPrice;
	}
	
	public String getTotalDiscOffList(){
		return totalDiscOffList;
	}
	
	public String getTotalDiscOffEntitled(){
		return totalDiscOffEntitled;
	}
	
	public String getMaxDiscPct() {
		return maxDiscPct;
	}
	
	public Double getPeriodBookableRevenue() {
		return periodBookableRevenue;
	}
	
	public Double getSpecialBidTotalPrice() {
		return specialBidTotalPrice;
	}
	/**
	 * @return Returns the services.
	 */
	public Double getServiceRevenue() {
		return serviceRevenue;
	}
}
