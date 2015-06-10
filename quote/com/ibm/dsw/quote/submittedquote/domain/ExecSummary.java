/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 * 
 * Created At: 2009-2-5
 */

public interface ExecSummary {

	public String getWebQuoteNum();
	public void setWebQuoteNum(String webQuoteNum) throws TopazException;
	
	public String getUserId();
	public void setUserId(String userId) throws TopazException;

	public Boolean getRecmdtFlag();
	public void setRecmdtFlag(Boolean recmdtFlag) throws TopazException ;

	public String getRecmdtText();
	public void setRecmdtText(String recmdtText) throws TopazException;

	public String getExecSupport();
	public void setExecSupport(String execSupport) throws TopazException;

	public String getTermCondText();
	public void setTermCondText(String termCondText) throws TopazException;

	public String getBriefOverviewText();
	public void setBriefOverviewText(String briefOverviewText) throws TopazException;

	public Double getBaselineTotalPrice();

	public Double getEntitledTotalPrice();
	
	public String getTotalDiscOffList();
	
	public String getTotalDiscOffEntitled();

	public String getMaxDiscPct();

	public void setPeriodBookableRevenue(Double periodBookableRevenue) throws TopazException;
	
	public Double getPeriodBookableRevenue();

	public Double getSpecialBidTotalPrice();
	
	public void setServiceRevenue(Double services) throws TopazException;
	
	public Double getServiceRevenue();
}
