package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
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
 * This <code>QuoteTxtPersister<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 16, 2008
 */

public class QuoteTxtPersister extends Persister {
    
    private QuoteTxt_jdbc quoteComment_jdbc = null;
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteTxtPersister(QuoteTxt_jdbc quoteComment_jdbc) {
        this.quoteComment_jdbc = quoteComment_jdbc;
    }

    public void update(Connection connection) throws TopazException {
        insertOrUpdte(connection);
    }

    public void delete(Connection connection) throws TopazException {
        
    }

    public void hydrate(Connection connection) throws TopazException {
        
    }

    public void insert(Connection connection) throws TopazException {
        insertOrUpdte(connection);
    }
    
    public QuoteTxt_jdbc getQuoteComment_jdbc() {
        return quoteComment_jdbc;
    }
    
    public void insertOrUpdte(Connection connection) throws TopazException {
        
        HashMap parms = new HashMap();
        Integer iTxtId = quoteComment_jdbc.getQuoteTextId() == -1 ? null : new Integer(quoteComment_jdbc.getQuoteTextId());
        parms.put("piWebQuoteNum", quoteComment_jdbc.getWebQuoteNum());
        parms.put("piQuoteTxtID", iTxtId);
        parms.put("piQuoteTxtTypeCode", quoteComment_jdbc.getQuoteTextTypeCode());
        parms.put("piQuoteTxt", quoteComment_jdbc.getQuoteText());
        parms.put("piUserEmailAdr", quoteComment_jdbc.getUserEmail());
        parms.put("piJstfctnSectnID", quoteComment_jdbc.getJustificationSectionId());
        parms.put("piLastUpdateTime", null);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_TXT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_TXT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);

            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), returnCode);
            }
            
            quoteComment_jdbc.quoteTextId = ps.getInt(9);

        } catch (SQLException sqle) {
            logContext.debug(this, sqle.getMessage());
            throw new TopazException(sqle);
        }
        
    }

}
