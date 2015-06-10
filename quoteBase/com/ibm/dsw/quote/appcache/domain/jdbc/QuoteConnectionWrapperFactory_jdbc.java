package com.ibm.dsw.quote.appcache.domain.jdbc;

import java.sql.Connection;

import com.ibm.dsw.quote.appcache.domain.QuoteConnectionWrapper;
import com.ibm.dsw.quote.appcache.domain.QuoteConnectionWrapperFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteConnectionWrapperFactory_jdbc<code> class is  Create new QuoteReadOnlyConnection.
 *    
 * @author: <a href="jiewbj@cn.ibm.com">Crespo </a>
 * 
 * Creation date: October 1, 2013
 */
public class QuoteConnectionWrapperFactory_jdbc extends  QuoteConnectionWrapperFactory {
	private LogContext logContext = LogContextFactory.singleton().getLogContext();
	/*
	 * (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.printsptime.process.QuoteReadOnlyConnectionFactory#create(java.sql.Connection)
	 */
	public QuoteConnectionWrapper create(Connection conn) throws TopazException {
		logContext.debug(this, "Create new QuoteReadOnlyConnection");
		return new QuoteConnectionWrapper(conn);
    }

}
