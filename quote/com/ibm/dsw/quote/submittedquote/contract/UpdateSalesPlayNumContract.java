package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UpdateSalesPlayNumContract</code> class is a contract
 * 
 * @author qinfengc@cn.ibm.com
 * 
 * Created on 2011-05-30
 */
public class UpdateSalesPlayNumContract extends SaveDraftCommentsBaseContract {
    
	private static final long serialVersionUID = 3233438826847981075L;
	private String quoteNum;
    private String salesPlayNum;
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        salesPlayNum = parameters.getParameterAsString(SpecialBidParamKeys.SALES_PLAY_NUM);
    }
    
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }
    
    public String getWebQuoteNum() {
        return quoteNum;
    }

    public String getSalesPlayNum() {
		return salesPlayNum;
	}

	public String getQuoteNum() {
        return quoteNum;
    }
 
}
