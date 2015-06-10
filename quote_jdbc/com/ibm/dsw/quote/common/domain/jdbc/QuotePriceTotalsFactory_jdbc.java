package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.domain.QuotePriceTotalsFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 11, 2007
 */

public class QuotePriceTotalsFactory_jdbc extends QuotePriceTotalsFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotalsFactory#getQuotePriceTotals(java.lang.String)
     */
    public QuotePriceTotals createQuotePriceTotals(String webQuoteNum, String distChannelCode, 
                                                         String prcType, String prcSumLevelCode,
                                                         String currencyCode, String userID, String revnStrmCategoryCode) throws TopazException {
        
        String key = webQuoteNum + distChannelCode + prcType + prcSumLevelCode+ currencyCode + revnStrmCategoryCode;
        QuotePriceTotals priceTotal = (QuotePriceTotals) this.getFromCache(key);
        if (priceTotal == null) {
            QuotePriceTotals_jdbc priceTotals_jdbc = new QuotePriceTotals_jdbc(webQuoteNum);
            priceTotals_jdbc.distChannelCode = distChannelCode;
            priceTotals_jdbc.priceType = prcType;
            priceTotals_jdbc.priceSumLevelCode = prcSumLevelCode;
            priceTotals_jdbc.revnStrmCategoryCode = revnStrmCategoryCode;
            priceTotals_jdbc.currencyCode = currencyCode;
            priceTotals_jdbc.isNew(true); 
            priceTotals_jdbc.setUserID(userID);
            priceTotal = priceTotals_jdbc;
            this.putInCache(key, priceTotals_jdbc);
        }
        return priceTotal;
    }
    public  void removePriceTotals(String webQuoteNum) throws TopazException{
        
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        
        QuotePriceTotals_jdbc priceTotal = null;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        try {
            
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_PRICE_TOTALS, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_D_QT_PRICE_TOTALS, params);

            ps.execute();
            
            
        } catch (Exception e) {
            logger.error("Failed to get the quote line item configs from the database!", e);
            throw new TopazException(e);
        }
       
    }
    

    public List getQuotePriceTotals(String webQuoteNum, String userID) throws TopazException {

        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);

        List priceTotals = new ArrayList();
        QuotePriceTotals_jdbc priceTotal = null;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rs = null;
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_PRICE_TOTALS, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_PRICE_TOTALS, params);

            ps.execute();
            
            rs = ps.getResultSet();

            while (rs.next()) {

                priceTotal = new QuotePriceTotals_jdbc(webQuoteNum);
                
                priceTotal.priceType = trim(rs.getString("QUOTE_PRIC_TYPE"));
                priceTotal.priceSumLevelCode = trim(rs.getString("PRIC_SUM_LVL_CODE"));
                priceTotal.revnStrmCategoryCode = trim(rs.getString("REVN_STREAM_CAT_CODE"));
                priceTotal.currencyCode = trim(rs.getString("CURRNCY_CODE"));
                priceTotal.distChannelCode = trim(rs.getString("SAP_DISTRIBTN_CHNL_CODE"));
                priceTotal.extAmt = rs.getDouble("EXTND_AMT");
                priceTotal.setUserID(userID);
                priceTotals.add(priceTotal);
            }

        } catch (Exception e) {
            logger.error("Failed to get the quote line item configs from the database!", e);
            throw new TopazException(e);
        }finally{
        	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        }
        return priceTotals;
    }
    private String trim (String value){
        if( null == value ){
            return null; 
        }
        else{
            return value.trim();
        }
    }
    public void putInCache(Object objectId, Object object) throws TopazException {

        TransactionContextManager.singleton().getTransactionContext().put(QuotePriceTotalsFactory.class, objectId,
                object);
    }

    public Object getFromCache(Object objectId) throws TopazException {
        return TransactionContextManager.singleton().getTransactionContext().get(QuotePriceTotalsFactory.class,
                objectId);
    }
}
