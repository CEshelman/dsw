package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DisplayDraftToUQuoteContract<code> class.
 * 
 * @author: whliul@cn.ibm.com 
 * 
 * Creation date: May 31, 2013
 */
public class DisplayToUQuoteContract extends QuoteBaseContract{

	private String webQuoteNum;
	private String lob;
	private boolean isOnlySaaSParts;
	private boolean isChannelQuote;
	private boolean isSubmittedQuote;
	private String quoteStatus;
	private int saasTermCondCatFlag;
	private String actionCode;
	private String agrmtTypeCode;
	
	public void load(Parameters parameters, JadeSession session) {
		 super.load(parameters, session);
		 lob = parameters.getParameterAsString("lob");
		 isOnlySaaSParts = parameters.getParameterAsBoolean("isOnlySaaSParts");
		 isChannelQuote = parameters.getParameterAsBoolean("isChannelQuote");
		 isSubmittedQuote = parameters.getParameterAsBoolean("isSubmittedQuote");
		 webQuoteNum = parameters.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
		 quoteStatus = parameters.getParameterAsString("quoteStatus");
		 actionCode = parameters.getParameterAsString("actionCode");
		 agrmtTypeCode = parameters.getParameterAsString("agrmtTypeCode");
		 
		 if(this.isSQOEnv()){
			 saasTermCondCatFlag = parameters.getParameterAsInt(ParamKeys.SAAS_TERM_COND_CAT_FLAG);
		 }
		
	}

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public boolean isOnlySaaSParts() {
		return isOnlySaaSParts;
	}

	public void setOnlySaaSParts(boolean isOnlySaaSParts) {
		this.isOnlySaaSParts = isOnlySaaSParts;
	}

	public boolean isChannelQuote() {
		return isChannelQuote;
	}

	public void setChannelQuote(boolean isChannelQuote) {
		this.isChannelQuote = isChannelQuote;
	}

	public boolean isSubmittedQuote() {
		return isSubmittedQuote;
	}

	public void setSubmittedQuote(boolean isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}

	public String getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public int getSaasTermCondCatFlag() {
		return saasTermCondCatFlag;
	}

	public void setSaasTermCondCatFlag(int saasTermCondCatFlag) {
		this.saasTermCondCatFlag = saasTermCondCatFlag;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getAgrmtTypeCode() {
		return agrmtTypeCode;
	}

	public void setAgrmtTypeCode(String agrmtTypeCode) {
		this.agrmtTypeCode = agrmtTypeCode;
	}
	
	
}
