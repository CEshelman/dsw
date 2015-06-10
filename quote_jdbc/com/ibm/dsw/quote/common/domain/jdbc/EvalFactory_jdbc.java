package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.EvalAction;
import com.ibm.dsw.quote.common.domain.EvalAction_Impl;
import com.ibm.dsw.quote.common.domain.EvalFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.CacheableFactory;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * 
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * @author <a href="mailto:kunzhwh@cn.ibm.com">Jason Zhang</a><br/>
 *  
 */
public class EvalFactory_jdbc extends EvalFactory implements CacheableFactory {

    public List getEvalActionHis(String quoteNum) throws TopazException {
        LogContext log = LogContextFactory.singleton().getLogContext();

        List appHisList = new ArrayList();
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", quoteNum); // quoteNum 
        CallableStatement ps = null;
        ResultSet rs = null;
        try {

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_EVAL_HIS, null);

            ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_EVAL_HIS, parms);
            log.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();

            rs = ps.getResultSet();

            while (rs.next()) {
            	EvalAction evalAction = new EvalAction_Impl();
            	evalAction.setFullName(rs.getString("FULL_NAME"));
            	evalAction.setEmailAdr(rs.getString("EMAIL_ADR"));
            	evalAction.setUserAction(rs.getString("USER_ACTION"));
            	java.sql.Date sqlDate= rs.getDate("ADD_DATE"); // his.ADD_DATE
            	long time = sqlDate.getTime();
            	java.util.Date date=new java.util.Date (time);
            	evalAction.setAddDate(date);
            	if(evalAction.getUserAction()!=null && evalAction.getUserAction().trim().equalsIgnoreCase(com.ibm.dsw.quote.base.config.QuoteConstants.EVAL_ACTION_HIS_RETURN))
            		evalAction.setComments(rs.getString("COMMENTS"));
            	appHisList.add(evalAction);
            }
            rs.close();
        } catch (Exception e) {
            log.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        }
        return appHisList;
    }

    /**
     * 
     * @param objectId
     * @param object
     * @throws TopazException
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(EvalFactory.class, objectId, object);
    }

    /**
     * 
     * @param objectId
     * @return
     * @throws TopazException
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        return TransactionContextManager.singleton().getTransactionContext().get(EvalFactory.class, objectId);
    }
    
    /**
     * close JDBC resource
     * @param cstmt
     * @param rs
     */
    private void close(CallableStatement cstmt, ResultSet rs) {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				logContext.error(TopazUtil.class, LogThrowableUtil.getStackTraceContent(e));
			}
		}

		if (null != cstmt) {
			try {
				cstmt.close();
			} catch (SQLException e) {
				logContext.error(TopazUtil.class, LogThrowableUtil.getStackTraceContent(e));
			}
		}
	}
    
}
