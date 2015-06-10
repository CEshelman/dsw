package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteBaseContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-28
 */

public class SubmittedQuoteBaseContract extends SaveDraftCommentsBaseContract {
    private String quoteNum = null;
    
    private String redirectMsg = null;
    
    private Quote quote = null;
    
    private String saveSuccess;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        redirectMsg = parameters.getParameterAsString(SpecialBidParamKeys.PARAM_REDIRECT_MSG);
    }

    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }

    /**
     * @return Returns the redirectMsg.
     */
    public String getRedirectMsg() {
        return redirectMsg;
    }
	/**
	 * @param quoteNum The quoteNum to set.
	 */
	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}
    /**
     * @return Returns the quote.
     */
    public Quote getQuote() {
        return quote;
    }
    /**
     * @param quote The quote to set.
     */
    public void setQuote(Quote quote) {
        this.quote = quote;
    }
    /**
     * @return Returns the saveSuccess.
     */
    public String getSaveSuccess() {
        return saveSuccess;
    }
    /**
     * @param saveSuccess The saveSuccess to set.
     */
    public void setSaveSuccess(String saveSuccess) {
        this.saveSuccess = saveSuccess;
    }
}
