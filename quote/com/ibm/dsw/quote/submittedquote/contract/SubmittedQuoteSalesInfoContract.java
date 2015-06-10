package com.ibm.dsw.quote.submittedquote.contract; 

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteSalesInfoContract<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-7-16
 */
public class SubmittedQuoteSalesInfoContract extends SubmittedQuoteBaseContract {
    private String quoteTitle;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }

    /**
     * @return Returns the quoteTitle.
     */
    public String getQuoteTitle() {
        return quoteTitle;
    }
    /**
     * @param quoteTitle The quoteTitle to set.
     */
    public void setQuoteTitle(String quoteTitle) {
        this.quoteTitle = quoteTitle;
    }
}
 