
package com.ibm.dsw.quote.common.util;

import java.sql.Connection;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EmptyPersister.java</code>
 * 
 * @author: liuxinlx@cn.ibm.com
 * 
 * Created on: 2007-05-08
 */
public class EmptyPersister extends Persister {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();
    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
       logContext.debug(this,"Update is called ,no db operation");

    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        logContext.debug(this,"Delete is called ,no db operation");

    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        logContext.debug(this,"Hydrate is called ,no db operation");

    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        logContext.debug(this,"Insert is called ,no db operation");
    }

}
