package com.ibm.dsw.quote.audit.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.audit.domain.QuoteAuditHistInfo;
import com.ibm.dsw.quote.audit.process.QuoteAuditProcess_Impl;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class QuoteAuditProcess_jdbc extends QuoteAuditProcess_Impl {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();
	public static final String piQueryTypeAction = "ACTION";

	public List getQuoteAuditHistoriesBySpecificActions(String webQuoteNum,String actionList)throws QuoteException {

        
        HashMap params = new HashMap();
        ArrayList audithistories = new ArrayList();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piQueryType", piQueryTypeAction);
        params.put("piQueryStr", actionList);
        
        ResultSet rs = null;
        try {

            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_AUDIT_HIST, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_AUDIT_HIST, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            
            rs = callStmt.getResultSet();
            while (rs.next()) {
                QuoteAuditHistInfo auditHistInfo = new QuoteAuditHistInfo();

                auditHistInfo.setUserAction(StringUtils.trimToEmpty(rs.getString(1)));
                auditHistInfo.setLineItemSeqNum(StringUtils.trimToEmpty(rs.getString(2)));
                auditHistInfo.setOldVal(StringUtils.trimToEmpty(rs.getString(3)));
                auditHistInfo.setNewVal(StringUtils.trimToEmpty(rs.getString(4)));
                auditHistInfo.setModDate(rs.getTimestamp(5));
                auditHistInfo.setModBy(StringUtils.trimToEmpty(rs.getString(6)));
                auditHistInfo.setSpeclBidApprvrLvl(StringUtils.trimToEmpty(rs.getString(7)));
            	
                audithistories.add(auditHistInfo);
            }

            this.commitTransaction();

            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        }finally{
        	if(rs!=null){
            	try {
            		rs.close();
    			} catch (Exception e) {
    				logContext.error(this, e);
    			}
        	}
	        rollbackTransaction();
        }
        String ws2 = null;
        String ws1 = null;
        String teddy = "teddy";
        return audithistories;
    
	}

}
