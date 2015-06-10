/*
 * Created on 2008-10-28
 * @author Xiao Guo Yi
 */
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
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidReason;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class QuoteReasonFactory_jdbc extends QuoteReasonFactory {
	
    public void getBackDatingReason(QuoteHeader header) throws TopazException{
    	ResultSet set = null;
    	try {
    		QueryContext queryCtx = QueryContext.getInstance();
            String spName = CommonDBConstants.DB2_S_QT_REAS_CMMT;
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", header.getWebQuoteNum());
            
            logContext.debug(this,"webquote number is " + header.getWebQuoteNum());
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, spName, parms);            
            if(ps.execute()){
            	
            	header.setBackDatingComment(ps.getString(3));
            	
            	set = ps.getResultSet();
            	List list = new ArrayList();
            	while(set.next()){
            		list.add(set.getString("CODE").trim());
            	}
            	
            	header.setReasonCodes(list);
            }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }finally{
        	try {
				if (null != set && !set.isClosed())
				{
					set.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
    }
    
    public void updateBackDatingReason(QuoteHeader header, String userID) throws TopazException{
    	try {
    		QueryContext queryCtx = QueryContext.getInstance();
            String spName = CommonDBConstants.DB2_IU_QT_REAS_CMMT;
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", header.getWebQuoteNum());
            parms.put("piReasCodeList", joinReasonCodes(header.getReasonCodes()));
            parms.put("piBackDtgCmmt", header.getBackDatingComment());
            parms.put("piBackDtgCmmt", header.getBackDatingComment());
            parms.put("piUserID", userID);
            
            logContext.debug(this,"webquote number is " + header.getWebQuoteNum());
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, spName, parms);            
            ps.execute();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }
    }
    
    private String joinReasonCodes(List list){
    	if(list == null || list.size() == 0){
    		return null;
    	}
    	StringBuffer sb = new StringBuffer();
    	
    	for(int i = 0; i < list.size(); i++){
    		sb.append((String)list.get(i)).append(",");
    	}
    	sb.deleteCharAt(sb.length() - 1);
    	
    	return sb.toString();
    }
    
    public void updateSpecialBidReason(String webQuoteNum, SpecialBidReason sbReason, String userID)
                                                                throws TopazException{
    	try {
    		QueryContext queryCtx = QueryContext.getInstance();
            String spName = CommonDBConstants.DB2_IU_QT_SB_REAS;
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piSBReasCodeList", joinReasonCodes(sbReason.getSpecialBidReasonList()));
            parms.put("piNoApprovalReasList", joinReasonCodes(sbReason.getNoApprovalReasonList()));
            parms.put("piEMEADisc", sbReason.getEMEADiscountReason());
            parms.put("piUserID", userID);
            
            logContext.debug(this,"webquote number is " + webQuoteNum);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, spName, parms);            
            ps.execute();
            
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }
    }
    
    public SpecialBidReason loadSpecialBidReason(String webQuoteNum) throws TopazException{
		SpecialBidReason sbReason = null;
		ResultSet set = null;
    	try {  		
    		QueryContext queryCtx = QueryContext.getInstance();
            String spName = CommonDBConstants.DB2_S_QT_SB_REAS;
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", webQuoteNum);
            
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, spName, parms);   
            
            if(ps.execute()){
            	if(ps.getInt(3) == 0){
            		logContext.debug(this, "no special bid reasons in DB for quote:" + webQuoteNum);
            		return null;
            	}
            	
            	sbReason = new SpecialBidReason();
            	set = ps.getResultSet();
            	String colName = "";
            	String reasCode = "";
            	
            	while(set.next()){
            		colName = set.getString("COL_NAME").trim();
            		reasCode = set.getString("CODE").trim();

            	    sbReason.addReasCode(colName, reasCode);
            	}
           }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }finally{
        	try {
				if (null != set && !set.isClosed())
				{
					set.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
        
        if(logContext.isDebug()){
        	logContext.debug(this, sbReason.toString());
        }
        
        return sbReason;
    }
}
