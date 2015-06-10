package com.ibm.dsw.quote.login.history.process.jdbc;


import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.login.history.process.QuoteLoginHistoryProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class QuoteLoginHistoryProcess_jdbc extends QuoteLoginHistoryProcess_Impl  {
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
	@Override
	public String getCSRFID(String userid, Timestamp time) throws QuoteException {
		
        HashMap params = new HashMap();
        params.put("piUserId", userid);
        params.put("piLoginTime", time);
        String CSRFID = null;
        try {
        	    this.beginTransaction();
                QueryContext queryCtx = QueryContext.getInstance();
                String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_LOGIN_HIST, null);
                CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
                queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_LOGIN_HIST, params);
                logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

                ps.execute();
                int poGenStatus = ps.getInt(1);
                if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                	if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS ) {
                		throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
                	} else {
                        CSRFID = ps.getString(4);                           		
                	}
                } else {
                    logger.debug(this, "No previous data exists for this "+userid);
                }
                this.commitTransaction();
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
        return CSRFID;
    }
	

	@Override
	public void persistCSRFID(String userid, String CSRFID, Timestamp time) throws QuoteException {
        HashMap params = new HashMap();
        params.put("piUserId", userid);
        params.put("piLoginTime", time);
        params.put("piCSRFID", CSRFID);
        try {
        	this.beginTransaction();
                QueryContext queryCtx = QueryContext.getInstance();
                String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_LOGIN_HIST, null);
                CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
                queryCtx.completeStatement(ps, CommonDBConstants.DB2_I_QT_LOGIN_HIST, params);
                logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

                ps.execute();
                int poGenStatus = ps.getInt(1);
                if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                    throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
                }
            	this.commitTransaction();
            
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
        	this.rollbackTransaction();
        }
	}

}
