package com.ibm.dsw.quote.appcache.domain.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.appcache.config.CacheDBConstants;
import com.ibm.dsw.quote.appcache.domain.PrintSPTimeTraceFactory;
import com.ibm.dsw.quote.base.cache.QuoteTopazCacheableFactoryHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.persistence.jdbc.KeyedPersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazReadOnlyFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PrintSPTimeTraceFactory_jdbc</code> class is jdbc implementation of
 * PrintSPTimeTraceFactory.
 * 
 * 
 * @author <a href="machaomc@cn.ibm.com"> Ma Chao </a> <br/>
 * 
 * Creation date: Oct.21,2013
 */

public class PrintSPTimeTraceFactory_jdbc extends PrintSPTimeTraceFactory
		implements TopazReadOnlyFactory {
	
	public String getSelectSqlQueryStringKey() {
		return CacheDBConstants.DB2_S_QT_PRINT_SP_TIMETRACER;
	}


	public HashMap getSelectSqlParms() {
		return null;
	}

	/**
	 * 
	 * @param rs
	 * @param checkCache
	 */
	public KeyedPersistentObject hydrate(ResultSet rs, boolean checkCache) throws TopazException {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
		PrintSPTimeTrace_jdbc printSPTimeTrace = null;
		try{
			String spName = rs.getString(1).trim();
            if (checkCache) {
            	printSPTimeTrace = (PrintSPTimeTrace_jdbc) this.getFromCache(spName);
            }
            if(printSPTimeTrace == null){
            	int timeThreshold = rs.getInt(2);
            	printSPTimeTrace = new PrintSPTimeTrace_jdbc( spName, timeThreshold );
            	logCtx.debug(this, "Loading SP from DB2, SP_NAME=" + spName + ": TIME_THRESHOLD=" + timeThreshold);
            }
		} catch (SQLException e) {
            throw new TopazException(e);
        }
		return printSPTimeTrace;
	}

	/**
	 * 
	 * @param objectId
	 * @param object
	 */
	public void putInCache(Object objectId, Object object) throws TopazException {
		TransactionContextManager.singleton().getTransactionContext().put(PrintSPTimeTraceFactory.class, objectId, object);
	}

	/**
	 * 
	 * @param objectId
	 */
	public Object getFromCache(Object objectId) throws TopazException {
		Object object = TransactionContextManager.singleton().getTransactionContext().get(PrintSPTimeTraceFactory.class, objectId);
		if (null == object) {
			object = QuoteTopazCacheableFactoryHelper.singleton().getFromGlobalCache(this, objectId);
		}
        return object;
	}
	
    /**
     * This method return all SP that need to print time trace if it's execution time more than the time threshold.  
     * 
     * @return Map<String, Integer>
     * @throws TopazException
     */
	public Map<String, Integer> findAllSPinCache() throws TopazException {
		return QuoteTopazCacheableFactoryHelper.singleton().findAllSPinCache(this);
	}
	
}