package com.ibm.dsw.quote.loadtest.dao.impl;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.loadtest.dao.SqoLoadTestDao;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
 
/**
 * @author julia.liu
 *
 */
public class SqoLoadTestDaoJdbc implements SqoLoadTestDao {

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean getWebApplCodesByColName(String cnstntName)
			throws TopazException {
		boolean status = false; 
		TimeTracer tracer = TimeTracer.newInstance();
		
		HashMap params = new HashMap();
		params.put("piCnstntName", cnstntName);

		LogContext logContext = LogContextFactory.singleton().getLogContext();
		
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					CommonDBConstants.DB2_S_QT_GET_WEB_APP_CNSTNT, null);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps;

			ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			context.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_GET_WEB_APP_CNSTNT, params);

			tracer.stmtTraceStart("call S_QT_GET_WEB_APP_CNSTNT");
			ps.execute();
			int poGenStatus = ps.getInt(1);
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
			tracer.stmtTraceEnd("call S_QT_GET_WEB_APP_CNSTNT");
			status= true;

		} catch (SQLException e) {
			logContext.debug(this, LogThrowableUtil.getStackTraceContent(e));
			throw new TopazException(e);
		} finally {
			tracer.dump();
		}
		return status;
	}

}
