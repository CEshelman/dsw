package com.ibm.dsw.quote.submittedquote.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Reviewer_Impl</code> class is the abstract implementation for
 * the Reviewer domain object.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public abstract class Reviewer_Impl implements Reviewer {

    public String webQuoteNum;

    public String reviewEmail;
    
    public String userEmail;
    
    public int quoteTxtId;
    
    /**
     * @return Returns the reviewEmail.
     */
    public String getReviewEmail() {
        return reviewEmail;
    }

    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    /**
     * @return Returns the userEmail.
     */
    public String getUserEmail() {
        return userEmail;
    }
    /**
     * @return Returns the quoteTxtId.
     */
    public int getQuoteTxtId() {
        return quoteTxtId;
    }
    /**
     * @param quoteTxtId The quoteTxtId to set.
     */
    public void setQuoteTxtId(int quoteTxtId) {
        this.quoteTxtId = quoteTxtId;
    }
}
