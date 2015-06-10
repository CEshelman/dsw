package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
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
 * This <code>QuoteContactPersister<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */

public class QuoteContactPersister extends Persister {

    private LogContext logger = LogContextFactory.singleton().getLogContext();
    
    QuoteContact_jdbc quoteContact_jdbc;
    
    public QuoteContactPersister(QuoteContact_jdbc quoteContact_jdbc) {
        super();
        this.quoteContact_jdbc = quoteContact_jdbc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        HashMap parms = new HashMap();
        String quoteNum = quoteContact_jdbc.getWebQuoteNum();
        String cntPrtnrFuncCode = quoteContact_jdbc.getCntPrtnrFuncCode();
        parms.put("piWebQuoteNum", quoteNum);
        parms.put("piSapCntPrtnrFuncCode", cntPrtnrFuncCode);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_QTCNT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_QTCNT, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                quoteContact_jdbc.cntFirstName = ps.getString(4);
                quoteContact_jdbc.CntLastName = ps.getString(5);
                quoteContact_jdbc.cntPhoneNumFull = ps.getString(6);
                quoteContact_jdbc.cntFaxNumFull = ps.getString(7);
                quoteContact_jdbc.cntEmailAdr = ps.getString(8);
          
                this.isNew(false);
                this.isDeleted(false);
            }
        } catch (SQLException e) {
            logger.error("Failed to get the QuoteContact from DB!", e);
            throw new TopazException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        params.put("piCreatorId", quoteContact_jdbc.getCreatorId());
        params.put("piSapCntPrtnrFuncCode", quoteContact_jdbc.getCntPrtnrFuncCode());
        params.put("piCreatorFirstName", quoteContact_jdbc.getCntFirstName() == null ? "" : quoteContact_jdbc
                .getCntFirstName());
        params.put("piCreatorLastName", quoteContact_jdbc.getCntLastName() == null ? "" : quoteContact_jdbc
                .getCntLastName());
        params.put("piIntlPhoneNumFull", quoteContact_jdbc.getCntPhoneNumFull() == null ? "" : quoteContact_jdbc
                .getCntPhoneNumFull());
        params.put("piIntlFaxNumFull", quoteContact_jdbc.getCntFaxNumFull() == null ? "" : quoteContact_jdbc
                .getCntFaxNumFull());
        params.put("piCreatorEmailAdr", quoteContact_jdbc.getCntEmailAdr() == null ? "" : quoteContact_jdbc
                .getCntEmailAdr());

        int retCode = -1;
        
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_QUOTE_CNT, null);
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_QUOTE_CNT, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            retCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + retCode);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isNew(true);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to insert the QuoteContact to the database!", e);
            throw new TopazException(e);
        }
    }
}
