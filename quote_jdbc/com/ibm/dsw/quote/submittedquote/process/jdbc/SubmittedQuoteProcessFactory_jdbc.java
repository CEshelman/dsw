package com.ibm.dsw.quote.submittedquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ReviewerProcessFactory_jdbc</code> class is the jdbc
 * implementation of Reviewer process factory.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on May 18, 2007
 */
public class SubmittedQuoteProcessFactory_jdbc extends SubmittedQuoteProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory#create()
     */
    public SubmittedQuoteProcess create() throws QuoteException {
        return new SubmittedQuoteProcess_jdbc();
    }

}
