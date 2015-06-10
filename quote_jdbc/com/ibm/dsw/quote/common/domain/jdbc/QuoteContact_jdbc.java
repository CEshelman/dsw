package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.QuoteContact_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteContact_jdbc<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */

public class QuoteContact_jdbc extends QuoteContact_Impl implements PersistentObject, Serializable {
    
    transient QuoteContactPersister persister;
    
    public QuoteContact_jdbc(String creatorId){
        this.creatorId = creatorId;
        persister = new QuoteContactPersister(this);
    }
    
    public QuoteContact_jdbc(String webQuoteNum, String cntPrtnrFuncCode){
        this.webQuoteNum = webQuoteNum;
        this.cntPrtnrFuncCode = cntPrtnrFuncCode;
        persister = new QuoteContactPersister(this);
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

    /**
     * @param cntEmailAdr
     *            The cntEmailAdr to set.
     * @throws TopazException
     */
    public void setCntEmailAdr(String cntEmailAdr) throws TopazException {
        this.cntEmailAdr = cntEmailAdr;
        persister.setDirty();
    }

    /**
     * @param cntPrtnrFuncCode The cntPrtnrFuncCode to set.
     */
    public void setCntPrtnrFuncCode(String cntPrtnrFuncCode) {
        this.cntPrtnrFuncCode = cntPrtnrFuncCode;
    }
    /**
     * @param cntFaxNumFull
     *            The cntFaxNumFull to set.
     * @throws TopazException
     */
    public void setCntFaxNumFull(String cntFaxNumFull) throws TopazException {
        this.cntFaxNumFull = cntFaxNumFull;
        persister.setDirty();
    }

    /**
     * @param cntFirstName
     *            The cntFirstName to set.
     * @throws TopazException
     */
    public void setCntFirstName(String cntFirstName) throws TopazException {
        this.cntFirstName = cntFirstName;
        persister.setDirty();
    }

    /**
     * @param cntLastName
     *            The cntLastName to set.
     * @throws TopazException
     */
    public void setCntLastName(String cntLastName) throws TopazException {
        CntLastName = cntLastName;
        persister.setDirty();
    }

    /**
     * @param cntPhoneNumFull
     *            The cntPhoneNumFull to set.
     * @throws TopazException
     */
    public void setCntPhoneNumFull(String cntPhoneNumFull) throws TopazException {
        this.cntPhoneNumFull = cntPhoneNumFull;
        persister.setDirty();
    }

    /**
     * @param webQuoteNum
     *            The webQuoteNum to set.
     * @throws TopazException
     */
    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        persister.setDirty();
    }

}
