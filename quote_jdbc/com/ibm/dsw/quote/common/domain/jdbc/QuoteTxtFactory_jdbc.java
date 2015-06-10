package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteTxtFactory;
import com.ibm.dsw.quote.common.exception.SPException;
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
 * This <code>QuoteTxtFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 16, 2008
 */

public class QuoteTxtFactory_jdbc extends QuoteTxtFactory {
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteTxtFactory_jdbc() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteTxtFactory#getRenewalQuoteDetailComments(java.lang.String)
     */
    public QuoteTxt getRenewalQuoteDetailComments(String webQuoteNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        parms.put("piTypeCode", QuoteConstants.RQ_DETAIL_SALES_COMMENTS);
        parms.put("piGetTxt", new Integer(1));
        QuoteTxt_jdbc comment_jdbc = new QuoteTxt_jdbc();
        ResultSet rs = null;
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_TXT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_TXT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            comment_jdbc.webQuoteNum = webQuoteNum;
            rs = ps.getResultSet();
            if ( rs.next() )
            {
                comment_jdbc.quoteTextId = rs.getInt("QUOTE_TXT_ID");
                comment_jdbc.quoteText = rs.getString("QUOTE_TXT");
                comment_jdbc.addByUserName = rs.getString("ADD_BY_USER_NAME");
                comment_jdbc.modByUserName = rs.getString("MOD_BY_USER_NAME");
            }
            
        } catch (SQLException e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
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
        
        return comment_jdbc;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteTxtFactory#updateQuoteComment(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public QuoteTxt updateQuoteComment(String webQuoteNum, int qtTxtId, String qtTxtTypeCode, String qtTxt,
            String userEmail, String sectnId) throws TopazException {
        
        QuoteTxt_jdbc quoteComment_jdbc = new QuoteTxt_jdbc();
        quoteComment_jdbc.setWebQuoteNum(webQuoteNum);
        quoteComment_jdbc.setQuoteTextId(qtTxtId);
        quoteComment_jdbc.setQuoteTextTypeCode(qtTxtTypeCode);
        quoteComment_jdbc.setQuoteText(qtTxt == null ? "" : qtTxt);
        quoteComment_jdbc.setUserEmail(userEmail);
        quoteComment_jdbc.setJustificationSectionId(sectnId);
        quoteComment_jdbc.isNew(true);
        
        return quoteComment_jdbc;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteTxtFactory#createQuoteComment()
     */
    public QuoteTxt createQuoteComment() throws TopazException {
        QuoteTxt_jdbc cmt = new QuoteTxt_jdbc();
        cmt.isNew(true);
        return cmt;
    }
    
    private List getQuoteTxts(String webQuoteNum, String quoteTxtType, int getTxtFlag) throws TopazException, SQLException
	{
    	HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        parms.put("piTypeCode", quoteTxtType);
        parms.put("piGetTxt", new Integer(getTxtFlag));
        List list = new ArrayList();
        
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_TXT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_TXT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                this.logContext.debug(this, "return code: " + poGenStatus);
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            ResultSet rs = ps.getResultSet();
            while ( rs.next() )
            {
                QuoteTxt_jdbc quoteTxt = new QuoteTxt_jdbc();
                quoteTxt.firstName = rs.getString("FIRST_NAME");
                quoteTxt.lastName = rs.getString("LAST_NAME");
                quoteTxt.userEmail = rs.getString("USER_EMAIL_ADR");
                quoteTxt.quoteTextTypeCode = rs.getString("QUOTE_TXT_TYPE_CODE");
                quoteTxt.addDate = rs.getTimestamp("ADD_DATE");
                quoteTxt.modDate = rs.getTimestamp("MOD_DATE");
                quoteTxt.quoteTextId = rs.getInt("QUOTE_TXT_ID");
                quoteTxt.justificationSectionId = rs.getString("JSTFCTN_SECTN_ID");
                quoteTxt.addByUserName = rs.getString("ADD_BY_USER_NAME");
                quoteTxt.modByUserName = rs.getString("MOD_BY_USER_NAME");
                if ( getTxtFlag == 1 )
                {
                	quoteTxt.quoteText = rs.getString("QUOTE_TXT");
                }
                list.add(quoteTxt);
            }
            rs.close();
        return list;
	}

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteTxtFactory#getQuoteTxtHistory(java.lang.String, java.lang.String)
     */
    public List getQuoteTxtHistory(String webQuoteNum, String quoteTxtType, int txtFlag) throws TopazException {
        try
		{
        	return this.getQuoteTxts(webQuoteNum, quoteTxtType, txtFlag);
		}
        catch ( SQLException e )
		{
        	logContext.error(this, e.getMessage());
            throw new TopazException(e);
		}
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.QuoteTxtFactory#getUserDraftComments(java.lang.String, java.lang.String)
	 */
	public List getUserDraftComments(String webQuoteNum, String quoteTxtType) throws TopazException {
		try
		{
        	return this.getQuoteTxts(webQuoteNum, quoteTxtType, 1);
		}
        catch ( SQLException e )
		{
        	logContext.error(this, e.getMessage());
            throw new TopazException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.QuoteTxtFactory#deleteDraftComments(java.lang.String, java.lang.String, int)
	 */
	public void deleteDraftComments(String webQuoteNum, String userId, int deleteType) throws TopazException {
		try
		{
			HashMap parms = new HashMap();
	        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
	        parms.put("piUserEmailAdr", userId);
	        parms.put("piDeleteFlag", new Integer(deleteType));
	        List list = new ArrayList();
        
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_D_QT_TXT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_D_QT_TXT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if ( !( poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS || poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) ) {
                this.logContext.info(this, "return code: " + poGenStatus);
                //throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
		}
		catch ( SQLException e )
		{
			logContext.error(this, e);
			throw new TopazException(e);
		}
	}

}
