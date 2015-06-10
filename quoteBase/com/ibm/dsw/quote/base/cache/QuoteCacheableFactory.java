package com.ibm.dsw.quote.base.cache;

import java.sql.Connection;
import java.util.Map;

import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * <p>
 * Implementers of <code>QuoteCacheableFactory</code> are Factories which use
 * TOPAZ.JDBC for persistence and QUARTZ to cache the results of SQL queries.
 * The factory may need to initiate its domain with more than one result set.
 * </p>
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-7
 */
public interface QuoteCacheableFactory extends TopazCacheableFactory {
    Map initializeCache(Connection connection) throws TopazException;

}
