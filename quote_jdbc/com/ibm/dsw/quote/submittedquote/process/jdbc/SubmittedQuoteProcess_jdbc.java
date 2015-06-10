package com.ibm.dsw.quote.submittedquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.submittedquote.config.SubmittedDBConstants;
import com.ibm.dsw.quote.submittedquote.domain.OrderDetail;
import com.ibm.dsw.quote.submittedquote.domain.PriorComments;
import com.ibm.dsw.quote.submittedquote.domain.PriorComments_Impl;
import com.ibm.dsw.quote.submittedquote.domain.Reviewer;
import com.ibm.dsw.quote.submittedquote.domain.StatusExplanation;
import com.ibm.dsw.quote.submittedquote.domain.jdbc.Reviewer_jdbc;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ReviewerProcess_jdbc</code> class is the jdbc implementation of
 * Reviewer process.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on May 18, 2007
 */
public class SubmittedQuoteProcess_jdbc extends SubmittedQuoteProcess_Impl {

    public List findReviewersByWebQuoteNum(String webQuoteNum) throws QuoteException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        List result = new ArrayList();

        ResultSet rs = null;
        try {
            HashMap params = new HashMap();
            params.put("piWebQuoteNum", webQuoteNum);

            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.S_QT_SB_REVIEWER, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement statement = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(statement, SubmittedDBConstants.S_QT_SB_REVIEWER, params);
            rs = statement.executeQuery();
            int poGenStatus = statement.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            while (rs.next()) {
                String reviewerEmail = rs.getString("RVWR_EMAIL_ADR");
                Reviewer review = new Reviewer_jdbc(webQuoteNum, reviewerEmail);
                logContext.debug(logContext, "reviewer " + review);
                result.add(review);
            }
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException("Exception when finding reviews by web quote num [" + webQuoteNum + "]", e);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally{
	    	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this,"Failed to close the resultset!");
			}
        }
        return result;
    }
    
    public StatusExplanation getStatusDetailExplanation(String sapDocNum, String statusCode) throws QuoteException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        StatusExplanation result = null;

        try {
            this.beginTransaction();
            HashMap params = new HashMap();
            params.put("piSapDocNum", sapDocNum);
            params.put("piStatusCode", statusCode);

            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.S_QT_STAT_EXPLANT, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement statement = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(statement, SubmittedDBConstants.S_QT_STAT_EXPLANT, params);
            statement.execute();
            int poGenStatus = statement.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            result = new StatusExplanation();
            
            result.setStatusCode(statusCode);

            result.setStatusDescription(statement.getString(4));
            result.setExplanation(statement.getString(5));
            result.setResolutionAction(statement.getString(6));
            result.setActionOwner(statement.getString(7));
            result.setDurtnIndctr(statement.getString(8));
            
            this.commitTransaction();
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException("Exception when finding status detail explanation by sap doc num [" + sapDocNum + "] and status code [" + statusCode + "]", e);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        return result;
    }
    
    public boolean quoteHasCtrldParts(String webQuoteNum) throws QuoteException {
        boolean hasCtrldParts = false;

        try {
            HashMap params = new HashMap();
            params.put("piWebQuoteNum", webQuoteNum);
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.S_QT_HAS_CTRLD_PRT, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, SubmittedDBConstants.S_QT_HAS_CTRLD_PRT, params);
            
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            
            hasCtrldParts = (ps.getInt(3) == 1);
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException("Exception when finding reviews by web quote num [" + webQuoteNum + "]", e);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        }
        
        return hasCtrldParts;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#updatePARegstrnEmailFlag(java.lang.String, java.lang.String)
     */
    public void updatePARegstrnEmailFlag(String userId, String webQuoteNum) throws QuoteException {

        HashMap params = new HashMap();
        params.put("piUserID", userId == null ? "" : userId);
        params.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);

        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.U_QT_PA_REGSTRN, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, SubmittedDBConstants.U_QT_PA_REGSTRN, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logContext.error(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            //end topaz transaction
            this.commitTransaction();
        } catch (SQLException sqle) {
            logContext.debug(this, sqle.getMessage());
            throw new QuoteException(sqle);
        } catch (TopazException te) {
            logContext.debug(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    }
	
	public PriorComments getPriorComments(String webQuoteNum) throws QuoteException
    {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        PriorComments result = null;

        try {
            this.beginTransaction();
            HashMap params = new HashMap();
            params.put("piWebQuoteNum", webQuoteNum);

            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.S_QT_GET_PRIOR_CMTS, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement statement = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(statement, SubmittedDBConstants.S_QT_GET_PRIOR_CMTS, params);
            statement.execute();
            int poGenStatus = statement.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            
            ResultSet rs = statement.getResultSet();
            List commentsList = new ArrayList();
            while ( rs.next() )
            {
                PriorComments.Comments cmt = new PriorComments.Comments();
                cmt.setWebQuoteNum(rs.getString("WEB_QUOTE_NUM"));
                cmt.setCommentDate(rs.getTimestamp("MOD_DATE"));
                cmt.setCommentTxt(rs.getString("QUOTE_TXT"));
                cmt.setTextTypeCode(rs.getString("QUOTE_TXT_TYPE_CODE"));
                cmt.setUserId(rs.getString("user_email_adr"));
                cmt.setUserName(rs.getString("FULL_NAME"));
                commentsList.add(cmt);
            }
            statement.getMoreResults();
            ResultSet rs2 = statement.getResultSet();
            List attachments = new ArrayList();
            while ( rs2.next() )
            {
                QuoteAttachment attachment = new QuoteAttachment();
                attachment.setQuoteNumber(rs2.getString("WEB_QUOTE_NUM"));
                attachment.setId(rs2.getString("ATTCHMT_SEQ_NUM"));
                attachment.setFileName(rs2.getString("ATTCHMT_FILE_NAME"));
                attachment.setFileSize(rs2.getLong("ATTCHMT_FILE_SIZE"));
                attachment.setSecId(rs2.getString("JSTFCTN_SECTN_ID"));
                attachment.setStageCode(rs2.getString("ATTCHMT_STAGE_CODE"));
                //attachment.setUploaderEmail(rs2.getString("UPLOADER_EMAIL"));
                attachment.setAddDate(rs2.getTimestamp("ADD_DATE"));
                attachment.setAddByUserName(rs2.getString("ADD_BY_USER_NAME"));
                attachments.add(attachment);
            }
            result = new PriorComments_Impl(commentsList, attachments);
            this.commitTransaction();
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException("Exception when getting prior comments by num [" + webQuoteNum + "] ", e);
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        return result;
    }
	
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#updateSalesInfoTitle(java.lang.String, java.lang.String)
     */
    public void updateSalesInfoTitle(String userId, String title) throws QuoteException {
        HashMap params = new HashMap();
        params.put("piCreatorId", userId == null ? "" : userId);
        params.put("piQuoteTitle", title == null ? "" : title);

        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.DB2_U_QT_SALESINFO_TITLE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, SubmittedDBConstants.DB2_U_QT_SALESINFO_TITLE, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logContext.error(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            //end topaz transaction
            this.commitTransaction();
        } catch (SQLException sqle) {
            logContext.debug(this, sqle.getMessage());
            throw new QuoteException(sqle);
        } catch (TopazException te) {
            logContext.debug(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
        
    }

    public String getReturnReasonCode(String webQuoteNum, int apprvrLevel) throws QuoteException {
		// TODO Auto-generated method stub
	   	LogContext logContext = LogContextFactory.singleton().getLogContext();
	   	String oldReasonCode = "";
        try {
            String spName = CommonDBConstants.DB2_S_QT_GET_RETURN_REASON_CODE;
            HashMap parms = new HashMap();
            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            logContext.debug(this, "apprvrLevel is " + apprvrLevel);
            
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piApprvrLevel", apprvrLevel);
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();
                       
            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS == retStatus) {
            	oldReasonCode = StringUtils.trimToEmpty(ps.getString(4));
            }else{
            	throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            //end topaz transaction
            this.commitTransaction();
            return oldReasonCode;
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        finally{
        	 this.rollbackTransaction();
        }
	}
    
	public List<OrderDetail> getOrderDetails(String webQuoteNum, int destSeqNum)
			throws QuoteException {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		try {
			String spName = CommonDBConstants.DB2_S_QT_GET_ORD_DETAILS;
			HashMap parms = new HashMap();

			logContext.debug(this, "webquote number is " + webQuoteNum);
			logContext.debug(this, "destination sequence number is " + destSeqNum);

			parms.put("piWebQuoteNum", webQuoteNum);
			parms.put("piDestSeqNum", destSeqNum);
			// begin topaz transaction
			this.beginTransaction();
			QueryContext queryCtx = QueryContext.getInstance();

			String sqlQuery = queryCtx.getCompletedQuery(spName, null);

			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

			logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

			queryCtx.completeStatement(ps, spName, parms);

			ps.execute();

			int retStatus = ps.getInt(1);

			List<OrderDetail> orderList = new ArrayList<OrderDetail>();
			if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS == retStatus) {
				ResultSet rs2 = ps.getResultSet();
				Map<Integer, List<String>> sapDocUserStatusGroup = new TreeMap<Integer, List<String>>();
				while (rs2.next()) {
					Integer lineItemSeqNum = Integer.valueOf(rs2.getInt("LINE_ITEM_SEQ_NUM"));
					String codeDscr = rs2.getString("CODE_DSCR");

					List<String> sapDocUserStatusList = sapDocUserStatusGroup.get(lineItemSeqNum);
					if (sapDocUserStatusList == null) {
						sapDocUserStatusList = new ArrayList<String>();
						sapDocUserStatusList.add(codeDscr);
					} else {
						sapDocUserStatusList.add(codeDscr);
					}

					sapDocUserStatusGroup.put(lineItemSeqNum,sapDocUserStatusList);
				}
                if (ps.getMoreResults()) {
    				ResultSet rs = ps.getResultSet();
    				while (rs.next()) {
    					OrderDetail order = new OrderDetail();
    					order.setCcadDate(rs.getDate("CUST_CMMTTD_ARRIVL_DATE"));
    					order.setMachineType(rs.getString("HW_MACH_TYPE"));
    					order.setModel(rs.getString("HW_MACH_MODEL") != null ? rs.getString("HW_MACH_MODEL").trim() : "");
    					order.setOrderStatus(rs.getString("SAP_RJCTN_CODE"));
    					order.setSevialNum(rs.getString("HW_MACH_SERIAL_NUM") != null ? rs.getString("HW_MACH_SERIAL_NUM").trim() : "");
    					
    					Integer lineItemSeqNum = Integer.valueOf(rs.getInt("LINE_ITEM_SEQ_NUM"));
    					order.setOrderLineItemSeq(lineItemSeqNum.toString());
    					order.setSapDocUserStatusList(sapDocUserStatusGroup.get(lineItemSeqNum));
    					orderList.add(order);
    				}
                }
			} else {
				throw new TopazException("exeute sp failed, retStatus="
						+ retStatus + ",sql:" + sqlQuery);
			}
			// end topaz transaction
			this.commitTransaction();
			
			return orderList;
		} catch (Exception e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		}
		finally{
			this.rollbackTransaction();
		}
	}


}
