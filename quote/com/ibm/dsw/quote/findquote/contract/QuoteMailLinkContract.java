package com.ibm.dsw.quote.findquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). 
 * 
 * The <code>QuoteMailLinkContract</code> class is 
 * 
 * @author wangship@cn.ibm.com
 *
 * Created on Jan. 24, 2013
 */
public class QuoteMailLinkContract extends QuoteBaseContract {
    private String quoteNum;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }

	public String getQuoteNum() {
		return quoteNum;
	}

	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}
}
