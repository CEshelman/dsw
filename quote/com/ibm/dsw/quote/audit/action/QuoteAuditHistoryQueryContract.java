package com.ibm.dsw.quote.audit.action;

import java.util.Locale;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditHistoryQueryContract<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public class QuoteAuditHistoryQueryContract extends QuoteBaseCookieContract {
	private String webQuoteNum = null;
	protected Locale locale = null;

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	
	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
        locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
	}

	
	
	
}
