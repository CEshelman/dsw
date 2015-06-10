package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteAttachmentFactory;
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
 * This <code>QuoteAttachmentFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 17, 2008
 */

public class QuoteAttachmentFactory_jdbc extends QuoteAttachmentFactory {
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteAttachmentFactory_jdbc() {
        super();
    }
    
    protected List getQuoteAttachments(String webQuoteNum, String classfctnCode) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        parms.put("piAttchmtClassfctnCode", classfctnCode);
        ArrayList attachments = new ArrayList();
        ResultSet rs = null;
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_ATTCHMT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_ATTCHMT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            rs = ps.getResultSet();
            while (rs.next()) {
                QuoteAttachment attachment = new QuoteAttachment();
                attachment.setQuoteNumber(rs.getString("WEB_QUOTE_NUM"));
                attachment.setId(rs.getString("ATTCHMT_SEQ_NUM"));
                attachment.setFileName(rs.getString("ATTCHMT_FILE_NAME"));
                attachment.setFileSize(rs.getLong("ATTCHMT_FILE_SIZE"));
                attachment.setSecId(rs.getString("JSTFCTN_SECTN_ID"));
                attachment.setStageCode(rs.getString("ATTCHMT_STAGE_CODE"));
                attachment.setUploaderEmail(rs.getString("UPLOADER_EMAIL"));
                Timestamp addTime = rs.getTimestamp("ADD_DATE");
                Date addDate = new Date();
                if (addTime != null) {
                    addDate = new Date(addTime.getTime());
                }
                attachment.setAddDate(addDate);
                
                attachment.setAddByUserName(rs.getString("ADD_BY_USER_NAME"));
                
                attachments.add(attachment);
            }
            rs.close();
            
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
        
        return attachments;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteAttachmentFactory#getRQSalesCommentAttachments(java.lang.String)
     */
    public List getRQSalesCommentAttachments(String webQuoteNum) throws TopazException {
        return getQuoteAttachments(webQuoteNum, QuoteConstants.QT_ATTCHMNT_RQ_SLS_CMMNT);
    }
    
    public List getSpecialBidAttachments(String webQuoteNum) throws TopazException {
        return getQuoteAttachments(webQuoteNum, QuoteConstants.QT_ATTCHMNT_SPEL_BID);
    }
    public List getFctNonStdTcAttachments(String webQuoteNum) throws TopazException {
        return getQuoteAttachments(webQuoteNum, QuoteConstants.QT_ATTCHMNT_FCT_NON_STD_TC);
    }
    
    public void removeQuoteAttachment(String webQuoteNum, String attchSeqNum, String userId, int stageCode) throws TopazException {
        
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piAttchmtSeqNum", attchSeqNum);
        userId = null == userId ? "" : userId;
        parms.put("piUserId", userId);
        parms.put("piStageCode", new Integer(stageCode));
        
        this.logContext.debug(this, "delete attach: webQuoteNum=" + webQuoteNum + ", attchSeqNum=" + attchSeqNum + ", userId=" + userId + ", stageCode=" + stageCode);
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_D_QT_ATTCHMNT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_D_QT_ATTCHMNT, parms);

            ps.execute();
            
            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS == retStatus || retStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA ) {
                if ( retStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA  )
                {
                	logContext.info(this, webQuoteNum + " attachment " + attchSeqNum + " not in db");
                }
            }
            else
            {
            	throw new TopazException("Execute SP D_QT_ATTCHMNT error. SP returns " + retStatus
                        + ". WebQuoteNum: " + webQuoteNum + "; AttchmtSeqNum: " + attchSeqNum);
            }
        } catch(SQLException e){
            logContext.error(this, e.getMessage());
            throw new TopazException("Exception when execute SP D_QT_ATTCHMNT. WebQuoteNum: " + webQuoteNum
                    + "; AttchmtSeqNum: " + attchSeqNum);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteAttachmentFactory#getAttchmtRefNum(java.lang.String)
     */
    public int getAttchmtRefNum(String attchSeqNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piAttchmtSeqNum", attchSeqNum);
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_ATTCHMT_REF, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_ATTCHMT_REF, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS || poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA ) {
                int refNum = 0;
                if ( poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS )
                {
                	refNum = ps.getInt(2);
                }
                else
                {
                	logContext.info(this, "return code is: " + poGenStatus + ", the attachment not exists in db");
                }
                logContext.debug(this, "attchmt ref num " + refNum);
                return refNum;
            }
            else
            {
            	throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
        } catch (SQLException e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteAttachmentFactory#getQuoteAttachmentInfo(java.lang.String)
     */
    public QuoteAttachment getQuoteAttachmentInfo(String attchSeqNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piAttchSeqNum", attchSeqNum);
        QuoteAttachment attachment = null;
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_ATTCHMT_INFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_ATTCHMT_INFO, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            rs = ps.getResultSet();
            if (rs.next()) {
                attachment = new QuoteAttachment();
                attachment.setQuoteNumber(rs.getString("WEB_QUOTE_NUM"));
                attachment.setId(rs.getString("ATTCHMT_SEQ_NUM"));
                attachment.setFileName(rs.getString("ATTCHMT_FILE_NAME"));
                attachment.setFileSize(rs.getLong("ATTCHMT_FILE_SIZE"));
                attachment.setSecId(rs.getString("JSTFCTN_SECTN_ID"));
                attachment.setStageCode(rs.getString("ATTCHMT_STAGE_CODE"));
                attachment.setUploaderEmail(rs.getString("UPLOADER_EMAIL"));
                attachment.setAddDate(rs.getTimestamp("ADD_DATE"));
                attachment.setCmprssdFileFlag(rs.getInt("CMPRSSD_FILE_FLAG") == 1);
                
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
        
        return attachment;
    }

}
