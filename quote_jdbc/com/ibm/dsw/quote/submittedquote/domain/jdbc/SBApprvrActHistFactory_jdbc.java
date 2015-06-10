package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist;
import com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHistFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SBApprvrActHistFactory_jdbc<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SBApprvrActHistFactory_jdbc extends SBApprvrActHistFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * 
     */
    public SBApprvrActHistFactory_jdbc() {
        super();
    }
    
    /*
     *  (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHistFactory#createSBApprvrActHist()
     */
    public SBApprvrActHist createSBApprvrActHist() throws TopazException {
        SBApprvrActHist_jdbc sbApprvrActHist_jdbc = new SBApprvrActHist_jdbc();
        sbApprvrActHist_jdbc.isNew(true); //persist to DB2
        return sbApprvrActHist_jdbc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHistFactory#findActHistsByQuoteNum(java.lang.String)
     */
    public List findActHistsByQuoteNum(String webQuoteNum) throws TopazException {
        List actHistList = new ArrayList();

        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum==null?"":webQuoteNum);
        ResultSet rs = null;
        try {
	        QueryContext queryCtx = QueryContext.getInstance();
	        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_SB_ACT_HIST, null);
	        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	        queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_SB_ACT_HIST, parms);
	        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
	        
            ps.execute();
            
            int resultCode = ps.getInt(1);
            if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            
            rs = ps.getResultSet();
            while (rs.next()) {
                SBApprvrActHist_jdbc actHist = new SBApprvrActHist_jdbc();
                actHist.webQuoteNum = webQuoteNum;
                actHist.userRole = rs.getString("USER_ROLE");
                actHist.userAction = rs.getString("USER_ACTION");
                actHist.userEmail = rs.getString("USER_EMAIL_ADR");
                actHist.quoteTxt = rs.getString("QUOTE_TXT");
                actHistList.add(actHist);
            }
        } catch (SQLException sqle) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(sqle));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_SB_ACT_HIST);
        }finally{
        	try {
				if (null != rs)
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
        
        return actHistList;
    }
}
