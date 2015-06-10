package com.ibm.dsw.quote.base.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.ibm.dsw.quote.appcache.domain.QuoteConnectionWrapperFactory;
import com.ibm.dsw.quote.base.config.SPTimeTracerConfig;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>TopazUtil</code> class is to get connections from Topaz context
 * manager.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 9, 2007
 */
public class TopazUtil {
    /**
     * To get a connection in non-autoCommit mode.
     * 
     * @return The database connection
     * @throws TopazException
     * @throws QuoteException 
     * @throws SQLException
     */
    public static Connection getConnection() throws TopazException {
        Connection conn = TransactionContextManager.singleton().getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new TopazException("Exception when getting connection from Topax transaction manager.", e);
            }
        }
        
        if(SPTimeTracerConfig.isTimeTracerDisabled()){
        	return conn;
        } else {
        	return QuoteConnectionWrapperFactory.singleton().create(conn);
        }
    }
}
