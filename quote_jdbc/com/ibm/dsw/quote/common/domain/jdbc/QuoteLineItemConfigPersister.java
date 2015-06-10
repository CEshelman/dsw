package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteLineItemConfigPersister</code> class is persister to do
 * Quote Line item config related SQL.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class QuoteLineItemConfigPersister extends Persister {

    private QuoteLineItemConfig_jdbc config;

    /**
     * @param config_jdbc
     */
    public QuoteLineItemConfigPersister(QuoteLineItemConfig_jdbc config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
        insertOrUpdate(connection);
    }

    /**
     * @param connection
     * @throws TopazException
     */
    private void insertOrUpdate(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", config.getWebQuoteNum());
        params.put("piQuoteLineItemSeqNum", new Integer(config.getQuoteLineItemSecNum()));
        params.put("piProcrCode", config.getProcrCode());
        params.put("piProcrTypeQty", new Integer(config.getProcrTypeQty()));
        params.put("piUnitDvu", new Integer(config.getUnitDVU()));
        params.put("piExtndDvu", new Integer(config.getExtndDVU()));
        params.put("piUserID", config.getUserID());
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_LI_CONFIG, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_LI_CONFIG, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isNew(true);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to log the quote line item config to the database!", e);
            throw new TopazException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        // not be used currently
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        insertOrUpdate(connection);
    }

}
