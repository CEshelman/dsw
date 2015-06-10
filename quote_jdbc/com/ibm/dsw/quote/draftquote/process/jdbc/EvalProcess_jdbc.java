package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.EvalQuote;
import com.ibm.dsw.quote.common.domain.EvalQuote_Impl;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.process.EvalProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class EvalProcess_jdbc extends EvalProcess_Impl {
	
	private LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	 /**
     * Get the quote/bid whose status is 'Under evaluation'
     */
	@Override
    public List<EvalQuote> getEvalQuotes(String creator_id, String isTeleSales)throws TopazException{
    	List<EvalQuote> result = new ArrayList<EvalQuote>();
    	HashMap<String, String> parms = new HashMap<String, String>();
        parms.put("piCreatorId", creator_id);
        parms.put("piIsTeleSales", isTeleSales);
        
        CallableStatement cstmt = null;
        ResultSet rs = null;
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_GET_UNDER_EVALUATOR_QUOTE, null);
        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
        try {
            cstmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(cstmt, CommonDBConstants.DB2_S_GET_UNDER_EVALUATOR_QUOTE, parms);
            
            boolean retCode = cstmt.execute();            
            int poGenStatus = cstmt.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            if (retCode) {
                rs = cstmt.getResultSet();
                this.setEvalQuote(result, rs);
            }
        }   catch (SQLException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_GET_UNDER_EVALUATOR_QUOTE, e);
		} finally{
			this.close(cstmt, rs);
		}
    	return result;
    }

    /**
     * Get the object of the Evaluation quote
     * @param result
     * @param rs
     * @throws SQLException
     */
	private void setEvalQuote(List<EvalQuote> result, ResultSet rs) throws SQLException {
		while (rs.next()) {
			EvalQuote evalQuote= new EvalQuote_Impl();
			evalQuote.setWebQuoteNum(StringUtils.trimToEmpty(rs.getString("WEB_QUOTE_NUM")));
			evalQuote.setQuoteTypeCode(StringUtils.trimToEmpty(rs.getString("QUOTE_TYPE_CODE")));
		    evalQuote.setSoldToCustNum(StringUtils.trimToEmpty(rs.getString("SOLD_TO_CUST_NUM")));
		    evalQuote.setCustName(StringUtils.trimToEmpty(rs.getString("CUSTOMER_NAME")));
		    evalQuote.setEvalEmailAdr(StringUtils.trimToEmpty(rs.getString("EVALTR_EMAIL_ADR")));
		    evalQuote.setQuoteTitle(StringUtils.trimToEmpty(rs.getString("QUOTE_TITLE")));
		    evalQuote.setQuoteDscr(StringUtils.trimToEmpty(rs.getString("QUOTE_DSCR")));
		    evalQuote.setQuoteStageCode(StringUtils.trimToEmpty(rs.getString("QUOTE_STAGE_CODE")));
		    evalQuote.setFullName(StringUtils.trimToEmpty(rs.getString("FULL_NAME")));
		    evalQuote.setProgCode(StringUtils.trimToEmpty(rs.getString("PROG_CODE")));
		    evalQuote.setAgrmtTypeCode(StringUtils.trimToEmpty(rs.getString("AGRMT_TYPE_CODE")));
		    result.add(evalQuote);
		}
	}
    
    /**
     * close db resource
     * @param cstmt
     * @param rs
     */
    private void close(CallableStatement cstmt, ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
		}

		if (null != cstmt) {
			try {
				cstmt.close();
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
		}
	}

	public List<String> getEvalsList(String cntry_code)
			throws  TopazException {
		List<String> evalsByCountryResult = getEvalsByCountry(cntry_code);
    	return evalsByCountryResult;
	}
	
	public List<String> getEvalsByCountry(String cntry_code)
			throws  TopazException {
		List<String> result = new ArrayList<String>();
    	HashMap parms = new HashMap();
        parms.put("piCntryCode", cntry_code);
        
        CallableStatement cstmt = null;
        ResultSet rs = null;
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_EVAL_BY_CNTRY, null);
        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
        try {
            cstmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(cstmt, CommonDBConstants.DB2_S_QT_EVAL_BY_CNTRY, parms);
            
            boolean retCode = cstmt.execute();            
            int poGenStatus = cstmt.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            if (retCode) {
                rs = cstmt.getResultSet();
                while (rs.next()) {
        		    result.add(StringUtils.trimToEmpty(rs.getString("EVALTR_EMAIL_ADR")));
        		}
            }
        }   catch (SQLException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_EVAL_BY_CNTRY, e);
		} finally{
			this.close(cstmt, rs);
		}
    	return result;
	}
	
}
