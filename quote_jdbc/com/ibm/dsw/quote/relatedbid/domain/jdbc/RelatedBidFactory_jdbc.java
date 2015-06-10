package com.ibm.dsw.quote.relatedbid.domain.jdbc;

//Import RelatedBid.java and RelatedBid.java
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.relatedbid.domain.RelatedBidFactory;
import com.ibm.dsw.quote.relatedbid.domain.RelatedBid_Impl;
import com.ibm.dsw.quote.relatedbid.process.RelatedBidProcess;
import com.ibm.dsw.quote.relatedbid.process.jdbc.RelatedBidProcess_jdbc;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
 
 /**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidFactory_jdbc<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 10, 2013
 */
 
 public class RelatedBidFactory_jdbc extends RelatedBidFactory {
 
	private LogContext logContext = LogContextFactory.singleton().getLogContext();

	public RelatedBidFactory_jdbc() {
			super();
		}

		protected List parseRDCNumList(String rdcNums) {

			ArrayList rdcNumList = new ArrayList();
			if (StringUtils.isBlank(rdcNums))
				return rdcNumList;

			StringTokenizer st = new StringTokenizer(rdcNums, ",");
			while (st.hasMoreTokens()) {
				String rdc = st.nextToken();
				if (StringUtils.isNotBlank(rdc))
					rdcNumList.add(rdc.trim());
			}
			return rdcNumList;
		}
		
	/**
     * Call SP to get Related Bids
     */
	 public List findByNum(String wenQuoteNum) throws TopazException {
		HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", wenQuoteNum == null ? "" : wenQuoteNum.trim());
        List resultList = new ArrayList();
        Map<String, RelatedBid_Impl> map = new HashMap<String, RelatedBid_Impl>();
        ResultSet rs = null;
        
		try {
			String sprocName = CommonDBConstants.DB2_S_QT_GET_RLTD_BID;
			
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            boolean retCode = ps.execute();
            if (!retCode)
                return null;
            
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                rs = ps.getResultSet();
               
                while (rs.next()) {
                	String webQuoteNum = rs.getString("WEB_QUOTE_NUM");
                	
                	RelatedBid_Impl related = map.get(webQuoteNum);
                	if(related == null){
                		related = new RelatedBid_Impl();
                		map.put(webQuoteNum, related);
                		related.sqoRef = webQuoteNum;
                		related.quoteNum = rs.getString("QUOTE_NUM");
                		related.currencyCode = rs.getString("CURRNCY_CODE");
                		related.renewalQuoteNum = rs.getString("RENWL_QUOTE_NUM");
                		if(rs.getObject("SUBMIT_DATE") != null){
                			related.submittedDate = rs.getDate("SUBMIT_DATE");
                		}
    					
    					related.overallStatus = rs.getString("OVERAL_STATS");
    					related.resellerNum = rs.getString("RSEL_CUST_NUM");
    					related.resellerName = rs.getString("RSEL_NAME_FULL");
    					related.distributorName = rs.getString("DIST_NAME_FULL");
    					related.distributorNum = rs.getString("PAYER_CUST_NUM");
    					if(rs.getObject("QUOTE_PRICE_TOT") != null){
    						related.quoteTotal = rs.getDouble("QUOTE_PRICE_TOT");
    					}
    					if(rs.getObject("YTY_GRWTH_PCT") != null){
    						related.overallGrowth = rs.getDouble("YTY_GRWTH_PCT");
    					}
    					if(rs.getObject("IMPLD_GRWTH_PCT") != null){
    						related.impliedGrowth = rs.getDouble("IMPLD_GRWTH_PCT");
    					}

    					related.addRenewalQuoteLineItemNum(rs.getString("RENWL_QUOTE_LINE_ITEM_SEQ_NUM"));
                	} else {
                		related.addRenewalQuoteLineItemNum(rs.getString("RENWL_QUOTE_LINE_ITEM_SEQ_NUM"));
                	}
                }
                
                resultList.addAll(map.values());
			}
                
    	    return resultList;
    	    
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }finally{
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
    }
	 
	 public RelatedBidProcess create() throws QuoteException {
        return new RelatedBidProcess_jdbc();
    }
}
						
