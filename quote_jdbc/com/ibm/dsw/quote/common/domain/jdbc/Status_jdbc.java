package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import com.ibm.dsw.quote.common.domain.Status_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Status_jdbc</code> class is the jdbc implementation of the Status
 * domain object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 21, 2007
 */
public class Status_jdbc extends Status_Impl implements PersistentObject, Serializable {
    transient StatusPersister persister = null;

    public Status_jdbc() {
        persister = new StatusPersister(this);
    }

    protected Persister getPersister() {
        return persister;
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
        persister.persist(connection);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Status#setWebQuoteNum(java.lang.String)
     */
    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        getPersister().setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Status#setStatusCode(java.lang.String)
     */
    public void setStatusCode(String statusCode) throws TopazException {
        this.statusCode = statusCode;
        getPersister().setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Status#setStatusCodeDesc(java.lang.String)
     */
    public void setStatusCodeDesc(String statusCodeDesc) throws TopazException {
        this.statusCodeDesc = statusCodeDesc;
        getPersister().setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Status#setModifiedDate(java.util.Date)
     */
    public void setModifiedDate(Date modifiedDate) throws TopazException {
        this.modifiedDate = modifiedDate;
        getPersister().setDirty();
    }

}
