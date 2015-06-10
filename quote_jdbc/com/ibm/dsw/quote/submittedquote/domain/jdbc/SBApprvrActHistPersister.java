package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
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
 * This <code>SBApprvrActHistPersister<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SBApprvrActHistPersister extends Persister {

    private SBApprvrActHist_jdbc sbApprvrActHist_jdbc;

    public SBApprvrActHistPersister(SBApprvrActHist_jdbc sbApprvrActHist_jdbc) {
        super();
        this.sbApprvrActHist_jdbc = sbApprvrActHist_jdbc;
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", sbApprvrActHist_jdbc.getWebQuoteNum());
        params.put("piUserID", sbApprvrActHist_jdbc.getUserEmail());
        params.put("piUserRole", sbApprvrActHist_jdbc.getUserRole());
        params.put("piUserAction", sbApprvrActHist_jdbc.getUserAction());
        params.put("piQuoteTxt", sbApprvrActHist_jdbc.getQuoteTxt());
        params.put("piReturnReason", StringUtils.trimToEmpty(sbApprvrActHist_jdbc.getReturnReason()));

        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_I_QT_SB_ACT_HIST, null);
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_I_QT_SB_ACT_HIST, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
        } catch (Exception e) {
            logger.error("Failed to insert/update the special bid approver to the database!", e);
            throw new TopazException(e);
        }
    }
}
