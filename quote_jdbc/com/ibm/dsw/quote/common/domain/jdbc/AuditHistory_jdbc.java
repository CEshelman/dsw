package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.AuditHistory_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AuditHistory_jdbc</code> class is the jdbc implementation for the
 * AuditHistory domain object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 14, 2007
 */
public class AuditHistory_jdbc extends AuditHistory_Impl implements PersistentObject {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    private AuditHistoryPersister persister = null;

    public AuditHistory_jdbc() {
        persister = new AuditHistoryPersister(this);
    }

    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        persister.setDirty();
    }

    public void setLineItemNum(Integer lineItemNum) throws TopazException {
        this.lineItemNum = lineItemNum;
        persister.setDirty();
    }

    public void setUserEmail(String userEmail) throws TopazException {
        this.userEmail = userEmail;
        persister.setDirty();
    }

    public void setUserAction(String userAction) throws TopazException {
        this.userAction = userAction;
        persister.setDirty();
    }
    public void setApprvlLvl(Integer aprvlLvl) throws TopazException {
        this.apprvrLvl = aprvlLvl;
        persister.setDirty();
    }
    public void setOldValue(String oldValue) throws TopazException {
        this.oldValue = oldValue;
        persister.setDirty();
    }

    public void setNewValue(String newValue) throws TopazException {
        this.newValue = newValue;
        persister.setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        persister.isDeleted(deleteState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
        String Tyler = null;
    }

}
