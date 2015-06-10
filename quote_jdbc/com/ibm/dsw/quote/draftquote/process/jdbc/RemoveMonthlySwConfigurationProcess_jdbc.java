package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.util.HashMap;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.draftquote.process.RemoveMonthlySwConfigurationProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>RemoveMonthlySwConfigurationProcess_jdbc</code> class is jdbc implementation of
 * RemoveMonthlySwConfigurationProcess.
 *
 *
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 *
 * Creation date: 2014-1-03
 */
public class RemoveMonthlySwConfigurationProcess_jdbc extends RemoveMonthlySwConfigurationProcess_Impl {

	@Override
	public void removeMonthlySwConfiguration(String webQuoteNum,
			String configurationId) throws QuoteException {
		long start = System.currentTimeMillis();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", webQuoteNum);
		params.put("piConfigrtnId",configurationId);

		int retCode = -1;
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_MONTHLY_SW_CONFIGRTN, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			context.completeStatement(ps, CommonDBConstants.DB2_D_QT_MONTHLY_SW_CONFIGRTN,params);
			ps.execute();
			retCode = ps.getInt(1);
			if (retCode != 0) {
				throw new TopazException("SP call returns error code: " + retCode);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.error("Failed to delete the monthly SW configuration from the database!!", e);
			throw new QuoteException(e);
		}
		long end = System.currentTimeMillis();

		logger.debug(this, "delete single line item = " + (end - start));
	
	}
	
    
}
