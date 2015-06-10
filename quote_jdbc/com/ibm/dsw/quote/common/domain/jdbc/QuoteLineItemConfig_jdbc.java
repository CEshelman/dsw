package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.QuoteLineItemConfig_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteLineItemConfig_jdbc</code> class is jdbc implementation of
 * Quote line item config.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class QuoteLineItemConfig_jdbc extends QuoteLineItemConfig_Impl implements PersistentObject, Serializable{

    transient QuoteLineItemConfigPersister persister = new QuoteLineItemConfigPersister(this);
    private transient String userID;
    
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
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#delete()
     */
    public void delete() throws TopazException {
        persister.isDeleted(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#setWebQuoteNum(java.lang.String)
     */
    public void setWebQuoteNum(String quoteNum) throws TopazException {
        this.webQuoteNum = quoteNum;
        persister.setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#setProcrCode(java.lang.String)
     */
    public void setProcrCode(String code) throws TopazException {
        this.sProcrCode = code;
        persister.setDirty();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#setQuoteLineItemSecNum(int)
     */
    public void setQuoteLineItemSecNum(int seqNum) throws TopazException {
        this.iQuoteLineItemSecNum = seqNum;
        persister.setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#setProcrTypeQty(int)
     */
    public void setProcrTypeQty(int qty) throws TopazException {
        this.iProcrTypeQty = qty;
        persister.setDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#setExtndDVU(int)
     */
    public void setExtndDVU(int dvu) throws TopazException {
        this.iExtndDVU = dvu;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfig#setUnitDVU()
     */
    public void setUnitDVU(int dvu) throws TopazException{
        this.iUnitDVU = dvu;
        persister.setDirty();
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return userID;
    }
    /**
     * @param userID The userID to set.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
