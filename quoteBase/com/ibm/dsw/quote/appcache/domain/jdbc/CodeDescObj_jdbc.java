package com.ibm.dsw.quote.appcache.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.KeyedPersistentObject;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CodeDesc_jdbc</code> class is the jdbc implementation of
 * CodeDesc.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public class CodeDescObj_jdbc extends CodeDescObj_Impl implements KeyedPersistentObject, Serializable {

    /**
     * @param code
     * @param codeDesc
     */
    public CodeDescObj_jdbc(String code, String codeDesc) {
        this.code = code;
        this.codeDesc = codeDesc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.KeyedPersistentObject#getKey()
     */
    public Object getKey() {
        return getCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

    }

    private static final long serialVersionUID = 4033573641555225709L;

}
