/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummary;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummaryFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 *
 * Created At: 2009-2-5
 */

public class ExecSummaryFactory_jdbc extends ExecSummaryFactory {

	private LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	public ExecSummary findExecSummaryByQuoteNum(String webQuoteNumber) throws TopazException {
		
		HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNumber==null?"":webQuoteNumber);
        ExecSummary_jdbc execSummary = null;
        try {
	        QueryContext queryCtx = QueryContext.getInstance();
	        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_EXEC_SUMRY, null);
	        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	        queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_EXEC_SUMRY, parms);
	        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
	        
            ps.execute();
            
            int resultCode = ps.getInt(1);
            if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            
          
            
        	execSummary = new ExecSummary_jdbc();
        	execSummary.webQuoteNum = webQuoteNumber;
        	Integer recmdtFlag = (Integer)ps.getObject(3);
        	execSummary.recmdtFlag= recmdtFlag == null? null: (recmdtFlag.intValue() == 0? Boolean.FALSE : Boolean.TRUE);
        	execSummary.periodBookableRevenue= ps.getObject(4)== null ? null: new Double(ps.getDouble(4));
        	execSummary.recmdtText= ps.getString(5);
        	execSummary.execSupport= ps.getString(6);
        	execSummary.termCondText= ps.getString(7);
        	execSummary.briefOverviewText= ps.getString(8);
        	execSummary.entitledTotalPrice= new Double(DecimalUtil.roundAsDouble(ps.getDouble(9),3));
        	execSummary.specialBidTotalPrice= new Double(DecimalUtil.roundAsDouble(ps.getDouble(10),3));
        	execSummary.baselineTotalPrice= new Double(DecimalUtil.roundAsDouble(ps.getDouble(11),3));
        	execSummary.maxDiscPct= DecimalUtil.formatTo5Number(ps.getDouble(12));
        	execSummary.serviceRevenue= ps.getObject(13)== null ? null: new Double(ps.getDouble(13));
    		
    		execSummary.totalDiscOffEntitled= DecimalUtil.calculateDiscount(execSummary.specialBidTotalPrice.doubleValue(), execSummary.entitledTotalPrice.doubleValue());
    		execSummary.totalDiscOffList= DecimalUtil.calculateDiscount(execSummary.specialBidTotalPrice.doubleValue(), execSummary.baselineTotalPrice.doubleValue());
        		
        } catch (SQLException sqle) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(sqle));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_SB_ACT_HIST);
        }
		return execSummary;
	}
	
	public ExecSummary create(){
		return new ExecSummary_jdbc();
	}

}
