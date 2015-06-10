
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
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 11, 2007
 */

public class QuotePriceTotalsPersister extends Persister {
    LogContext logger = LogContextFactory.singleton().getLogContext();
    private QuotePriceTotals_jdbc priceTotals = null;

    public  QuotePriceTotalsPersister(QuotePriceTotals_jdbc priceTotals){
        this.priceTotals = priceTotals;
    }
    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
        insertOrUpdate(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        // do nothing
        
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        // do nothing
        
    }
    private void insertOrUpdate(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        
        params.put("piWebQuoteNum", priceTotals.webQuoteNum);        
        params.put("piQuotePricType", priceTotals.priceType);
        params.put("piPricSumLvlCode", priceTotals.priceSumLevelCode);
        params.put("piRevnStreamCatCode", priceTotals.revnStrmCategoryCode);
        params.put("picurrncyCode", priceTotals.currencyCode);
        params.put("piSapDistribtnChnlCode", priceTotals.distChannelCode);
        params.put("piExtndAmt", new Double(priceTotals.extAmt));
        params.put("piUserID", priceTotals.getUserID());

        int retCode = -1;
        
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_PRICE_TOTALS, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_PRICE_TOTALS, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
        } catch (Exception e) {
            logger.error("Failed to log the quote line item to the database!", e);
            throw new TopazException(e);
        }
        
    }
    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        
        insertOrUpdate(connection);
    }

}
