package com.ibm.dsw.quote.submittedquote.contract;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AddReviewerContract</code> class is for add reviewers for
 * submitted quote
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class AddReviewerContract extends SubmittedQuoteBaseContract {

    private String reviewerEmail;

    /**
     * @return Returns the rvwrEmailAdr.
     */
    public String getReviewerEmail() {
        return reviewerEmail;
    }

    /**
     * @param reviewerEmail The reviewerEmail to set.
     */
    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }
}
