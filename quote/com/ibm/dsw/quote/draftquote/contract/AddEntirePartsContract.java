package com.ibm.dsw.quote.draftquote.contract;


/**
 * RemoveConfigurationContract.java
 *
 * <p>
 * Copyright 2011 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="wxiaoli@cn.ibm.com">Vivian</a> <br/>
 * Jul 4, 2011
 */
public class AddEntirePartsContract extends PostPartPriceTabContract {
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
