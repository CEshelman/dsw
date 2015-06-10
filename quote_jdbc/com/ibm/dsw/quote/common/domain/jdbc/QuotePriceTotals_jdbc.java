
package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.QuotePriceTotals_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * 
 *   <br>This <code>QuotePriceTotals_jdbc<code> class
 *
 *   @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 11, 2007
 */

public class QuotePriceTotals_jdbc extends QuotePriceTotals_Impl implements PersistentObject, Serializable{

    private transient QuotePriceTotalsPersister persister = null;
    private transient String userID;
    
    public QuotePriceTotals_jdbc(String webQuoteNumber){
        this.webQuoteNum = webQuoteNumber;
        persister  = new QuotePriceTotalsPersister(this);
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#setPriceType(java.lang.String)
     */
    public void setPriceType(String priceType) throws TopazException {
        this.priceType = priceType;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#setPriceSumLevelCode(java.lang.String)
     */
    public void setPriceSumLevelCode(String code)throws TopazException {
        
        this.priceSumLevelCode = code;
        persister.setDirty();
        

    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#setCurrencyCode(java.lang.String)
     */
    public void setCurrencyCode(String code) throws TopazException{
        this.currencyCode = code;
        persister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#setDistChannelCode(java.lang.String)
     */
    public void setDistChannelCode(String code) throws TopazException{
        
        this.distChannelCode = code;
        persister.setDirty();

    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#setExtAmount(double)
     */
    public void setExtAmount(double amt) throws TopazException{
        this.extAmt = amt;
        persister.setDirty();

    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        this.persister.hydrate(connection);
        
        
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        this.persister.persist(connection);
        
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        this.persister.isDeleted(deleteState);
        
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        this.persister.isNew(newState);
        
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
    
    public void setRevnStrmCategoryCode(String revnStrmCategoryCode) throws TopazException{
        this.revnStrmCategoryCode = revnStrmCategoryCode;
        persister.setDirty();
    }
}
