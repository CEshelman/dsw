package com.ibm.dsw.quote.relatedbid.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidSearchContract<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 22, 2013 
 */
 
 public class RelatedBidsContract extends QuoteBaseContract{
	 
	 private String webQuoteNum;
	 
	 
	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}




	public void load(Parameters parameters, JadeSession session) {
	        super.load(parameters, session);
    }
 }	