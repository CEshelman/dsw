package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AuditHistoryPersister</code> class is the persister for the
 * AuditHistory domain object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 14, 2007
 */
public class AuditHistoryPersister extends Persister {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    private AuditHistory_jdbc auditHistory = null;

    public AuditHistoryPersister(AuditHistory_jdbc auditHistory) {
        super();
        this.auditHistory = auditHistory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", auditHistory.getWebQuoteNum());
        params.put("piLineItemSeqNum", auditHistory.getLineItemNum()==null?new Integer(0):auditHistory.getLineItemNum());
        params.put("piUserEmailAdr", auditHistory.getUserEmail()==null?"":auditHistory.getUserEmail());
        params.put("piUserAction", auditHistory.getUserAction()==null?"":auditHistory.getUserAction());
        params.put("piSpeclBidApprvrLvl",auditHistory.getApprvrLvl());
        params.put("piOldVal", auditHistory.getOldValue()==null?"":auditHistory.getOldValue());
        params.put("piNewVal", auditHistory.getNewValue()==null?"":auditHistory.getNewValue());

        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_AUDIT_HIST, null);
        CallableStatement callStmt;
        try {
            callStmt = connection.prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_I_QT_AUDIT_HIST, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logContext.error(this, "SP execution result: opGenStatus = " + poGenStatus);
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_I_QT_AUDIT_HIST, e);
        }
    }

}
