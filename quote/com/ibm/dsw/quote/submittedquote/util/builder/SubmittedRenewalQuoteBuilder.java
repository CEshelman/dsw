
package com.ibm.dsw.quote.submittedquote.util.builder;

import is.domainx.User;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 28, 2007
 */

public class SubmittedRenewalQuoteBuilder extends SubmittedQuoteBuilder {
    
    
    public SubmittedRenewalQuoteBuilder(Quote q,User user){
        super(q,user);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteBuilder#checkReCalculateFlag()
     */
    protected void checkReCalculateFlag() throws TopazException {
        // do nothing

    }
    
    protected void updateEOLPartPrices() throws TopazException{
        // do nothing
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteBuilder#adjustDate()
     */
    protected void adjustDate() throws TopazException {
        // do nothing

    }

}
