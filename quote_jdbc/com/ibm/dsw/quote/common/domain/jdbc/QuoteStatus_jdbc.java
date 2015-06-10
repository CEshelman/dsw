package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import com.ibm.dsw.quote.common.domain.QuoteStatus_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteStatus_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-6
 */

public class QuoteStatus_jdbc extends QuoteStatus_Impl implements PersistentObject, Serializable {
    private transient QuoteStatusPersister persister;

    /**
     *  
     */
    public QuoteStatus_jdbc() {
        persister = new QuoteStatusPersister(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {}

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

    public void setStatusCode(String statusCode) throws TopazException {
        this.statusCode = statusCode;
        persister.setDirty();
    }

    public void setStatusCodeDesc(String statusCodeDesc) throws TopazException {
        this.statusCodeDesc = statusCodeDesc;
        persister.setDirty();
    }

    public void setStatusType(String statusType) throws TopazException {
        this.statusType = statusType;
        persister.setDirty();
    }
    
    public void setQuoteNum(String quoteNum) throws TopazException {
    	this.quoteNum = quoteNum;
    }

    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        persister.setDirty();
    }
    
    public void setModifiedDate(Date modifiedDate) throws TopazException {
        this.modifiedDate = modifiedDate;
        persister.setDirty();
    }

}
