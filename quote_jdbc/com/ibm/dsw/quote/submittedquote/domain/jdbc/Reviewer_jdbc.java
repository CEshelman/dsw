package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.Connection;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.ibm.dsw.quote.submittedquote.domain.Reviewer_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Reviewer_jdbc</code> class is the jdbc implementation of Reviewer
 * domain object.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class Reviewer_jdbc extends Reviewer_Impl implements PersistentObject {

    private ReviewerPersister persister = null;

    public Reviewer_jdbc(String webQuoteNum, String userEmail, String reviewEmail, int quoteTxtId) {
        this.webQuoteNum = webQuoteNum;
        this.userEmail = userEmail;
        this.reviewEmail = reviewEmail;
        this.quoteTxtId = quoteTxtId;
        persister = new ReviewerPersister(this);
        
    }

    public Reviewer_jdbc(String webQuoteNum, String reviewEmail) {
        this.webQuoteNum = webQuoteNum;
        this.reviewEmail = reviewEmail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
//      TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);

    }
}
