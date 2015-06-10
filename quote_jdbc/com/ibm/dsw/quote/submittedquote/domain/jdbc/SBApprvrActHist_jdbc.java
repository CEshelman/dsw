package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.Connection;

import com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SBApprvrActHist_jdbc<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SBApprvrActHist_jdbc extends SBApprvrActHist_Impl implements PersistentObject {

    SBApprvrActHistPersister persister;

    /**
     *  
     */
    public SBApprvrActHist_jdbc() {
        super();
        persister = new SBApprvrActHistPersister(this);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist#setWebQuoteNum(java.lang.String)
     */
    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist#setUserEmail(java.lang.String)
     */
    public void setUserEmail(String userEmail) throws TopazException {
        this.userEmail = userEmail;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist#setUserRole(java.lang.String)
     */
    public void setUserRole(String userRole) throws TopazException {
        this.userRole = userRole;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist#setUserAction(java.lang.String)
     */
    public void setUserAction(String userAction) throws TopazException {
        this.userAction = userAction;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist#setQuoteTxt(java.lang.String)
     */
    public void setQuoteTxt(String quoteTxt) throws TopazException {
        this.quoteTxt = quoteTxt;
        persister.setDirty();
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist#setReturnReason(java.lang.String)
     */
    public void setReturnReason(String returnReason) throws TopazException {
        this.returnReason = returnReason;
        persister.setDirty();
    }
}
