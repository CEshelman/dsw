package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;



/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 *
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 *
 * Creation date: Dec 17, 2013
 */

public class SubmittedQuotePartPricingMonthlySwViewBean extends SubmittedQuotePartPricingViewBean {
	
	public Map<MonthlySoftwareConfiguration, List<MonthlySwLineItem>> getMonthlySwConfigrtnsMap(){
		return quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();
	}
	public List<MonthlySwLineItem> getMasterMonthlySwLineItemsList(){
		return quote.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
	}
	public List<MonthlySoftwareConfiguration> getMonthlySwConfgrtns(){
		return quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
	}
	
}