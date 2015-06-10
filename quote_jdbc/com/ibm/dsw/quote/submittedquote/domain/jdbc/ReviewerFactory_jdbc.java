package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import com.ibm.dsw.quote.submittedquote.domain.Reviewer;
import com.ibm.dsw.quote.submittedquote.domain.ReviewerFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ReviewerFactory_jdbc</code> class is the jdbc implementation of
 * Reviewer factory.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class ReviewerFactory_jdbc extends ReviewerFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.ReviewerFactory#addReviewer(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Reviewer addReviewer(String webQuoteNum, String userEmail, String reviewEmail, int txtId) throws TopazException {

        Reviewer_jdbc reviewer_jdbc = new Reviewer_jdbc(webQuoteNum, userEmail, reviewEmail,txtId);

        reviewer_jdbc.webQuoteNum = webQuoteNum;
        reviewer_jdbc.userEmail = userEmail;
        reviewer_jdbc.reviewEmail = reviewEmail;
        reviewer_jdbc.quoteTxtId = txtId;
        
       // System.err.println("txtId in reviewerFactory_jdbc :" + txtId);

        reviewer_jdbc.isNew(true);

        return reviewer_jdbc;
    }

}
