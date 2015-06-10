package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteStatusFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteDBConstants;
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
 * This <code>QuoteStatusFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-6
 */

public class QuoteStatusFactory_jdbc extends QuoteStatusFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     *  
     */
    public QuoteStatusFactory_jdbc() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteStatusFactory#getStatusByQuoteNum(java.lang.String)
     */
    public List getStatusByQuoteNum(String creatorId, String webQuoteNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorID", creatorId == null ? "" : creatorId);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        ArrayList statusList = new ArrayList();
        ResultSet rs = null;
        CallableStatement ps = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_STATUS, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_STATUS, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA)
                throw new NoDataException();
            else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS)
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);

            QuoteStatus_jdbc primary = new QuoteStatus_jdbc();
            primary.webQuoteNum = webQuoteNum;
            primary.statusType = QuoteConstants.QUOTE_STATUS_PRIMARY;
            primary.statusCode = StringUtils.trimToEmpty(ps.getString(4));
            primary.statusCodeDesc = StringUtils.trimToEmpty(ps.getString(5));
            statusList.add(primary);

            rs = ps.getResultSet();

            while (rs.next()) {
                QuoteStatus_jdbc secondary = new QuoteStatus_jdbc();
                secondary.webQuoteNum = webQuoteNum;
                secondary.statusType = StringUtils.trimToEmpty(rs.getString(1));
                secondary.statusCode = StringUtils.trimToEmpty(rs.getString(2));
                secondary.statusCodeDesc = StringUtils.trimToEmpty(rs.getString(3));
                statusList.add(secondary);
            }
        } catch (SQLException e) {
            logContext.debug(this, e.getMessage());
            throw new TopazException(e);
        }finally{
        	try {
				rs.close();
			} catch (SQLException e) {
				logContext.error(this, "Close resultSet rs error: "+ e.getMessage());
			}
			try {
				ps.close();
			} catch (SQLException e) {
				logContext.error(this, "Close CallableStatement error: "+ e.getMessage());
			}
        }
        return statusList;
    }
    
    
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteStatusFactory#getStatusByQuoteNum(java.lang.String)
     */
    public List getSapStatusByQuoteNum(String webQuoteNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        ArrayList statusList = new ArrayList();
        ResultSet prmRs = null;
        ResultSet sndRs = null;
        CallableStatement ps = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_SBMTQT_STAT, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_SBMTQT_STAT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA)
                throw new NoDataException();
            else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS)
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);

            
            prmRs = ps.getResultSet();

            while (prmRs.next()) {
                QuoteStatus_jdbc primary = new QuoteStatus_jdbc();
                primary.webQuoteNum = webQuoteNum;
                primary.statusType = QuoteConstants.QUOTE_STATUS_PRIMARY;
                primary.statusCodeDesc = StringUtils.trimToEmpty(prmRs.getString("status_dscr"));
                primary.statusCode = StringUtils.trimToEmpty(prmRs.getString("status_code"));
                statusList.add(primary);
                logContext.debug(this, "Add primary status: " + primary.statusCode);
            }
            prmRs.close();
            
            if (ps.getMoreResults()) {
                sndRs = ps.getResultSet();
	            while (sndRs.next()) {
	                QuoteStatus_jdbc secondary = new QuoteStatus_jdbc();
	                secondary.webQuoteNum = webQuoteNum;
	                secondary.statusType = QuoteConstants.QUOTE_STATUS_SECONDARY;
	                secondary.statusCodeDesc = StringUtils.trimToEmpty(sndRs.getString("status_dscr"));
	                secondary.statusCode = StringUtils.trimToEmpty(sndRs.getString("status_code"));
	                statusList.add(secondary);
	                logContext.debug(this, "Add secondary status: " + secondary.statusCode);
	            }
	            sndRs.close();
            }
            
        } catch (SQLException e) {
            logContext.debug(this, e.getMessage());
            throw new TopazException(e);
        }finally{
    		try {
				prmRs.close();
			} catch (SQLException e) {
				logContext.error(this, "Error when closing resultSet prmRs : "+ e.getMessage());
			}
        	try {
				sndRs.close();
			} catch (SQLException e) {
				logContext.error(this, "Error when closing resultSet sndRs: "+ e.getMessage());
			}
			try {
				ps.close();
			} catch (SQLException e) {
				logContext.error(this, "Error when closing CallableStatement: "+ e.getMessage());
			}
        }
        return statusList;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteStatusFactory#validateAndSaveRQStatus(java.lang.String,
     *      java.lang.String, int, boolean)
     */
    public void validateAndSaveRQStatus(String quoteNum, String statusCode, int statusType, boolean active)
            throws TopazException {

        final String STATUS_TYPE_PRIMARY = "P";
        final String STATUS_TYPE_SECONDARY = "S";

        String statusTypeStr = statusType == 1 ? STATUS_TYPE_PRIMARY : STATUS_TYPE_SECONDARY;

        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", quoteNum);
        parms.put("piQuoteStatCode", statusCode);
        parms.put("piStatPrirFlag", statusTypeStr);
        CallableStatement ps = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery;

            if (active) {
                sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.IU_QT_RENWL_STATUS, null);
                ps = TopazUtil.getConnection().prepareCall(sqlQuery);
                queryCtx.completeStatement(ps, DraftQuoteDBConstants.IU_QT_RENWL_STATUS, parms);
            } else {
                sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.D_QT_RENWL_STATUS, null);
                ps = TopazUtil.getConnection().prepareCall(sqlQuery);
                queryCtx.completeStatement(ps, DraftQuoteDBConstants.D_QT_RENWL_STATUS, parms);
            }
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

        } catch (SQLException e) {
            logContext.debug(this, e.getMessage());
            throw new TopazException(e);
        }finally{
        	try {
				ps.close();
			} catch (SQLException e) {
				logContext.error(this, e.getMessage());
			}
        }

    }
    
    public void UpdateWebQuoteStatus(String webQuoteNum, String activeStatusList, String inactStatusList)
            throws TopazException {

        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piActiveStatusList", activeStatusList);
        parms.put("piInactiveStatusList", inactStatusList);

        try {
            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_WEB_STATUS, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_WEB_STATUS, parms);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logContext.error(this, "Web status updating is failed for web quote: " + webQuoteNum
                        + ", the status is: " + poGenStatus);
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

        } catch (SQLException e) {
            logContext.error(this, "Web status updating is failed for web quote: " + webQuoteNum + e.getMessage());
            throw new TopazException(e);
        }
    }

}
