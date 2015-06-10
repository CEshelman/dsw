package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>TermExtensionAction<code> class.
 * 
 * @author: yuepingl@cn.ibm.com 
 * 
 * Creation date: May 16, 2013
 */

public class TermExtensionContract extends QuoteBaseContract {

	private String chrgAgrmtNum = null;
	
	private String configId = null;
	
	private String pid = null;
	
	private String customerNum = null;
	
	private String isCreateNewQuote = null;

	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
	}

	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	
	
	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}

	public String getIsCreateNewQuote() {
		return isCreateNewQuote;
	}

	public void setIsCreateNewQuote(String isCreateNewQuote) {
		this.isCreateNewQuote = isCreateNewQuote;
	}
	
}
