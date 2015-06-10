package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;


/**
 * RemoveMonthlySwConfigurationContract.java
 *
 * <p>
 * Copyright 2014 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="jiamengz@cn.ibm.com">Linda</a> <br/>
 * Jan 2, 2014
 */
public class RemoveMonthlySwConfigurationContract extends QuoteBaseCookieContract {
    private String webQuoteNum;
    private String configurationId;
    
    
    public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	public String getConfigurationId() {
		return configurationId;
	}
	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}
    

   
}
