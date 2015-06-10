package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteTotalPrice;
import com.ibm.dsw.quote.common.domain.QuoteTotalPriceFactory;
import com.ibm.dsw.quote.common.domain.QuoteTotalPriceGenerator;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 * 
 * Creation date: Dec 09, 2013
 */

public class QuoteTotalPriceFactory_jdbc extends QuoteTotalPriceFactory {

    private String trim (String value){
        if( null == value ){
            return null; 
        }
        else{
            return value.trim();
        }
    }

	@Override
	public Map<String, QuoteTotalPrice> getQuoteTotalPriceByWebQuoteNum(
			String webQuoteNum) throws TopazException {
		HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);

        Map<String,QuoteTotalPrice> totalPriceMap = new HashMap();
        QuoteTotalPrice qtTotPrice = null;
        QuoteTotalPriceGenerator generator = QuoteTotalPriceGenerator.singleton();
        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rs = null;
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_WEB_QUOTE_TOTAL_PRICE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_WEB_QUOTE_TOTAL_PRICE, params);

            ps.execute();
            
            rs = ps.getResultSet();
            String totalPriceType = null;
            while (rs.next()) {
            	totalPriceType = trim(rs.getString("TOTAL_PRC_TYPE"));
            	qtTotPrice = generator.create(totalPriceType);
            	qtTotPrice.setTotalPriceType(totalPriceType);
            	qtTotPrice.setTotalPoints(rs.getDouble("TOTAL_POINTS"));
            	qtTotPrice.setTotalEntitledPrice(rs.getDouble("TOTAL_ENTITLED_PRICE"));
            	qtTotPrice.setTotalBidPrice(rs.getDouble("TOTAL_BID_PRICE"));
            	qtTotPrice.setTotalBpPrice(rs.getDouble("TOTAL_BP_PRICE"));
                
            	totalPriceMap.put(totalPriceType,qtTotPrice);
            }

        } catch (Exception e) {
            logger.error("Failed to get the quote total price from the database!", e);
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
        return totalPriceMap;
	}


}
