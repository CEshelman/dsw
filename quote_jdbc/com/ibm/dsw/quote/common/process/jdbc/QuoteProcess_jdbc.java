package com.ibm.dsw.quote.common.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.AgreementTypeConfigFactory;
import com.ibm.dsw.quote.common.domain.ApproverRuleValidation;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.dsw.quote.common.domain.ChargeAgreement;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.common.domain.MigratePart_Impl;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo_Impl;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.jdbc.QuoteStatus_jdbc;
import com.ibm.dsw.quote.common.exception.CtrctInactiveException;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.QuoteProcess_Impl;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteDBConstants;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * @author <a href="mailto:doris_yuen@us.ibm.com">Doris Yuen </a> <br/>
 *  
 */
public class QuoteProcess_jdbc extends QuoteProcess_Impl {

    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

    /**
     * Constructor
     */
    public QuoteProcess_jdbc() {
        super();
    }

    @Override
	public void loadDraftQuoteToSession(String webQuoteNum, String creatorId, boolean openAsNewFlag) throws QuoteException {

        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piCreatorId", creatorId);
        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String spName = openAsNewFlag==true?NewQuoteDBConstants.SP_LOAD_SAVE_QUOTE:CommonDBConstants.DB2_U_QT_LOAD_QT;
            
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, spName, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);

            //end topaz transaction
            this.commitTransaction();

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#updateQuotePartnerInfro(java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    @Override
	public boolean updateQuotePartnerInfo(String webQuoteNum, String lob, String partnerNum, String partnerType, String userID)
            throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piLOB", lob);
        parms.put("piPartnerNum", partnerNum == null ? "" : partnerNum);
        parms.put("piPartnerType", new Integer(partnerType));
        parms.put("piUserID", userID);

        try {
            //begin topaz transaction
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.U_QT_PARTNER, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.U_QT_PARTNER, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            //end topaz transaction
            return true;

        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#updateOppOwner(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
	public boolean updateOppOwner(String userId, SalesRep oppOwner) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piUserId", userId);
        parms.put("piOppOwnerEmailAdr", oppOwner.getEmailAddress().toLowerCase());
        parms.put("piCntryCode", oppOwner.getCountryCode());
        parms.put("piNotesId", oppOwner.getNotesId());
        parms.put("piFullName", oppOwner.getFullName());
        parms.put("piFirstName", oppOwner.getFirstName());
        parms.put("piLastName", oppOwner.getLastName());
        parms.put("piPhoneNum", oppOwner.getPhoneNumber());
        parms.put("piFaxNum", oppOwner.getFaxNumber());

        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.U_QT_OPPOWNER, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.U_QT_OPPOWNER, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);
            if (0 != returnCode) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new QuoteException("calling sp return not zero");
            }
            //end topaz transaction
            this.commitTransaction();
            if (returnCode == 0) {
                return true;
            } else {
				return false;
			}

        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#convertPaToPae(java.lang.String)
     */
    @Override
	public void convertPaToPae(String creatorId) throws QuoteException {
        convert(creatorId, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#convertPaeToPa(java.lang.String)
     */
    @Override
	public void convertPaeToPa(String creatorId) throws QuoteException {
        convert(creatorId, true);

    }

    private void convert(String creatorId, boolean toPa) throws QuoteException {
        int type = (toPa ? 2 : 1);
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piConvertType", new Integer(type));

        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.U_QT_PA_CONVERT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.U_QT_PA_CONVERT, parms);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            this.commitTransaction();
        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    @Override
	public void populateRenewalQuote(String creatorId, String rnwlQuoteNum, boolean nonExpLineItems, String orignFromCustChgReqFlag) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piCreatorID", creatorId == null ? "" : creatorId);
        parms.put("piQuoteNum", rnwlQuoteNum == null ? "" : rnwlQuoteNum);
        parms.put("piNonExpLineItemFlag", nonExpLineItems ? new Integer(1) : new Integer(0));
        parms.put("piOrignFromCustChgReqFlag",  "true".equalsIgnoreCase(orignFromCustChgReqFlag) ? new Integer(1) : new Integer(0));
        
        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.I_QT_RNWL_QUOTE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.I_QT_RNWL_QUOTE, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);

            if (0 != returnCode) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new QuoteException("calling sp return not zero");
            }
            //end topaz transaction
            this.commitTransaction();
        } catch (SQLException sqle) {
            logger.debug(this, sqle.getMessage());
            throw new QuoteException(sqle);
        } catch (TopazException te) {
            logger.debug(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#convertToSalsQuote(java.lang.String)
     */
    @Override
	public void convertToSalsQuote(String creatorId, String webQuoteNum, boolean createSpeclBidFlag)
            throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piCreateSpeclBidFlag", new Integer(createSpeclBidFlag ? 1 : 0));

        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.U_QT_CONV_TO_SALES, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.U_QT_CONV_TO_SALES, parms);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            this.commitTransaction();
        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#getAvailablePrimaryStatus(java.lang.String)
     */
    @Override
	public List getAvailablePrimaryStatus(String quoteNum, int rqAccess) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", quoteNum);
        parms.put("piRQAccess", new Integer(rqAccess));

        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.S_QT_GET_AVLB_STAT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.S_QT_GET_AVLB_STAT, parms);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            List result = new ArrayList();
            ResultSet rs = ps.getResultSet();
            
            if (rs != null) {
                while (rs.next()) {
                    CodeDescObj_jdbc obj = new CodeDescObj_jdbc(StringUtils.trim(rs.getString("CODE")), StringUtils
                            .trim(rs.getString("COMMENT")));
                    result.add(obj);
                }
                rs.close();
            }
            this.commitTransaction();

            if (result.size() == 0) {
                throw new NoDataException();
            }
            return result;

        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    @Override
	public List getTerminationReasons() throws QuoteException {

        HashMap parms = new HashMap();

        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.S_QT_GET_TRM_REASN, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.S_QT_GET_TRM_REASN, parms);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            List result = new ArrayList();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                CodeDescObj_jdbc obj = new CodeDescObj_jdbc(StringUtils.trimToEmpty(rs.getString("CODE")), StringUtils.trimToEmpty(rs.getString("CODE_DSCR")));
                result.add(obj);
            }
            rs.close();
            this.commitTransaction();

            if (result.size() == 0) {
                throw new NoDataException();
            }
            return result;

        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#updateTerminationTracking(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
	public void updateTerminationTracking(String quoteNum, String reasonCode, String comments , String userID) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", quoteNum);
        parms.put("piRenwlTermntnReasCode", reasonCode);
        parms.put("piComment", comments);
        parms.put("piUserID", userID);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery;
            CallableStatement ps;

            sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.IU_QT_TERM_TRKING, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.IU_QT_TERM_TRKING, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
    
    @Override
	public void updateSavedQuoteWithSessionData(String creatorId) throws QuoteException{
        HashMap parms = new HashMap();
        parms.put("piCreatorID", creatorId);
        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_SAVE_SAP_DATA, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_I_QT_SAVE_SAP_DATA, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            this.commitTransaction();
           
            if (returnCode != 0) {
                throw new TopazException("Failed to update sales information, return code is :" + returnCode);
            } 
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    @Override
	public void substituteSessionQuote(String creatorId, String webQuoteNum) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piCreatorID", creatorId);
        parms.put("piWebQuoteNum", webQuoteNum);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_SUB_SESSN_QT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_I_QT_SUB_SESSN_QT, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            
            if (returnCode != 0) {
                throw new TopazException("Failed to update sales information, return code is :" + returnCode);
            } 
            

            this.commitTransaction();
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    };
    
    @Override
	public QuoteUserAccess getQuoteUserAccess(String webQuoteNum, String userId, String up2ReportingUserIds) throws QuoteException{
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        parms.put("piUserID", userId == null ? "" : userId);
        parms.put("piUserIDList", up2ReportingUserIds == null ? "" : up2ReportingUserIds);
        QuoteUserAccess quoteUserAccess = null;
        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_ACCESS_USER, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_ACCESS_USER, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            quoteUserAccess = new QuoteUserAccess();
            quoteUserAccess.setIsEditor(ps.getInt(5) == 1 ? true : false);
            quoteUserAccess.setIsFirstAppTypMember(ps.getInt(6) == 1 ? true : false);
            quoteUserAccess.setIsAnyAppTypMember(ps.getInt(7) == 1 ? true : false);
            quoteUserAccess.setIsPendingAppTypMember(ps.getInt(8) == 1 ? true : false);
            quoteUserAccess.setIsReviewer(ps.getInt(9) == 1 ? true : false);
            quoteUserAccess.setNoneApproval(ps.getInt(10) == 1 ? true : false);
            quoteUserAccess.setPendingAppLevel(ps.getInt(11));
            quoteUserAccess.setAppLevel(ps.getInt(12));
            quoteUserAccess.setCanViewQuote(ps.getInt(13) == 1 ? true : false);
            quoteUserAccess.setCanUpdtPrtnr(ps.getInt(14) == 1 ? true : false);
            quoteUserAccess.setCanChangeBidExpDate(ps.getInt(15) == 1 ? true : false);
            quoteUserAccess.setCanChangeBidLineItemDate(ps.getInt(16) == 1 ? true : false);
            quoteUserAccess.setCanSupersedeAppr(ps.getInt(17) == 1 ? true : false);
            quoteUserAccess.setCanEditExecSummary(ps.getInt(18) == 1 ? true : false);
            quoteUserAccess.setExecSummryCreatd(ps.getInt(19) == 1 ? true : false);
            quoteUserAccess.setCanCancelApprovedBid(ps.getInt(20) == 1);
            
            this.commitTransaction();
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return quoteUserAccess;
    }
    
    @Override
	public SubmittedQuoteAccess getSubmittedQuoteAccess(String webQuoteNum) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        SubmittedQuoteAccess sbmtQuoteAccess = null;
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_SBMT_ACCESS, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_SBMT_ACCESS, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            sbmtQuoteAccess = new SubmittedQuoteAccess();
            sbmtQuoteAccess.setNoStatusInOneHour(ps.getInt(3) == 1 ? true : false);
            sbmtQuoteAccess.setNoStatusOverOneHour(ps.getInt(3) == 2 ? true : false);
            sbmtQuoteAccess.setNoCustEnroll(ps.getInt(4) == 1 ? true : false);
            sbmtQuoteAccess.setHasOrdered(ps.getInt(5) == 1 ? true : false);
            sbmtQuoteAccess.setSbApprovedDate(ps.getDate(6));
            sbmtQuoteAccess.setAccessBlockStatus(ps.getInt(7) == 1 ? true : false);
            sbmtQuoteAccess.setCancelledBy(ps.getString(8));
            
            this.commitTransaction();
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return sbmtQuoteAccess;
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#getPrecheckStatus(java.lang.String)
     */
    @Override
	public List getPrecheckStatus(String webQuoteNum) throws QuoteException {
    	HashMap parms = new HashMap();
    	parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
    	List resultList = null;
    	try {
    		this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_ADDTNL_STATUS, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_ADDTNL_STATUS, parms);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();
            int poGenStatus = ps.getInt(1);
    		if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
    			throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
    		}
    		resultList = new ArrayList();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
            	QuoteStatus_jdbc qs = new QuoteStatus_jdbc();
            	qs.webQuoteNum = StringUtils.trimToEmpty(rs.getString(1));
            	qs.statusCodeDesc = StringUtils.trimToEmpty(rs.getString(2));
            	qs.statusCode = StringUtils.trimToEmpty(rs.getString(3));
            	qs.modifiedDate = rs.getDate(4);
            	qs.quoteNum = StringUtils.trimToEmpty(rs.getString(5));
            	resultList.add(qs);
            }
            rs.close();
            this.commitTransaction();
    		
    	} catch (SQLException e) {
    		logger.info(this, e.getMessage());
    		throw new QuoteException(e);
    	} catch (TopazException e){
    		logger.error(this, e.getMessage());
    		throw new QuoteException(e);
    	} finally {
    		this.rollbackTransaction();
    	}
    	
    	return resultList;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#updateOpprInfo(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public void updateOpprInfo(String webQuoteNum, String oldOpprNum, String opprNum, String oldExemptionCode,
            String exemptionCode, String userEmail) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piOpprtntyNum", opprNum==null?"":opprNum);
        parms.put("piExemptnCode", exemptionCode==null?"":exemptionCode);
        parms.put("piUserID", userEmail);
        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_OPPR_INFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_OPPR_INFO, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();

            int returnCode = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS == returnCode) {
                AuditHistoryFactory.singleton().createAuditHistory(webQuoteNum, null, userEmail, "UPDATE_OPPR_NUM",
                        oldOpprNum, opprNum);
                AuditHistoryFactory.singleton().createAuditHistory(webQuoteNum, null, userEmail, "UPDATE_EXEMPTN_CODE",
                        oldExemptionCode, exemptionCode);
            }

            this.commitTransaction();

            if (returnCode != 0) {
                throw new TopazException("Failed to update opportunity information, return code is :" + returnCode);
            }
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    @Override
	public void updateCustomerCreate(String webQuoteNum, Customer customer , String UserID) throws QuoteException{
        HashMap parms = new HashMap();
        String agrmntType = customer.getAgreementType();
        Integer procdFlag = AgreementTypeConfigFactory.singleton().getProcdFlagByAgrmntType(agrmntType);
        
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piWebCustId", new Integer(customer.getWebCustId()));
        parms.put("piCustNum", customer.getCustNum());
        parms.put("piSapCntId", new Integer(customer.getSapCntId()));
        parms.put("piTempAccessNum", customer.getTempAccessNum());
        parms.put("piWebAuthTempId", StringHelper.generateWebAuthTempId());
        parms.put("piProcdFlag", procdFlag);
        parms.put("piUserID", UserID);
        
        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_CUST_CREATE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_CUST_CREATE, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();

            int returnCode = ps.getInt(1);

            this.commitTransaction();

            if (returnCode != 0) {
                throw new TopazException("Failed to update customer info in db, return code is: " + returnCode);
            }
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    @Override
	public void updateRenewalQuoteSBInfo(String userId, String webQuoteNum) throws QuoteException{
        HashMap parms = new HashMap();
        parms.put("piUserID", userId == null ? "" : userId);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);

        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_RENWL_SB, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_RENWL_SB, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);

            if (0 != returnCode) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new QuoteException("calling sp return not zero");
            }
            //end topaz transaction
            this.commitTransaction();
        } catch (SQLException sqle) {
            logger.debug(this, sqle.getMessage());
            throw new QuoteException(sqle);
        } catch (TopazException te) {
            logger.debug(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    @Override
	public void assignNewAgreement(String creatorId, String sapCtrctNum, Integer webCtrctId) throws QuoteException, CtrctInactiveException {
        
        String vldCtrctNum = StringUtils.isBlank(sapCtrctNum) ? "" : StringHelper.fillString(sapCtrctNum);
        
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piSapCtrctNum", vldCtrctNum);
        parms.put("piWebCtrctId", webCtrctId);

        try {
            //begin topaz transaction
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_ASSGN_CTRCT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_ASSGN_CTRCT, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_CONTRACT_INACTIVE == poGenStatus) {
                throw new CtrctInactiveException("The contract " + vldCtrctNum + " is inactive.");
            }
            else if (0 != poGenStatus) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);
                throw new QuoteException("calling sp return not zero");
            }
            
            //end topaz transaction
            this.commitTransaction();
            
        } catch (CtrctInactiveException cie) {
            logger.debug(this, cie.getMessage());
            throw cie;
        } catch (SQLException sqle) {
            logger.debug(this, sqle.getMessage());
            throw new QuoteException(sqle);
        } catch (TopazException te) {
            logger.debug(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }

    }
    @Override
	public QuoteLockInfo getQuoteLockInfo(String webQuoteNum, String creatorId) throws QuoteException {
        QuoteLockInfo quoteLockInfo = null;
        HashMap params = new HashMap();
        params.put("piUserID", creatorId == null ? "" : creatorId);
        params.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_GET_LOCK_INFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_I_QT_GET_LOCK_INFO, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            quoteLockInfo = new QuoteLockInfo_Impl();
            quoteLockInfo.setWebQuoteNum(webQuoteNum);
            quoteLockInfo.setLockedFlag(ps.getInt(2) == 1 ? true : false);
            quoteLockInfo.setLockedBy(StringUtils.trimToEmpty(ps.getString(3)));
            
            // end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
        return quoteLockInfo;
    }

    @Override
	public boolean isQuoteBelongsToUser(String creatorId, String quoteNum) throws QuoteException {
        boolean isQuoteBelongsToUser = false;
        HashMap parms = new HashMap();
        parms.put("piCreatorID", creatorId);
        parms.put("piWebQuoteNum", quoteNum);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_BELONGS_TO_USER, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_BELONGS_TO_USER, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            
            isQuoteBelongsToUser = ps.getInt(2) == 1 ? true : false;
            
            this.commitTransaction();
           
            if (returnCode != 0) {
                throw new TopazException("Failed to update sales information, return code is :" + returnCode);
            } 
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return isQuoteBelongsToUser;
    }

    @Override
	public void conv2StdCopy(String userId, String webQuoteNum) throws QuoteException{
        
        HashMap params = new HashMap();
        params.put("piUserId", userId);
        params.put("piWebQuoteNum", webQuoteNum);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_CONV_TO_STD_COPY, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_CONV_TO_STD_COPY, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            this.commitTransaction();
            
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    @Override
	public void completeCustChgReqest(String userID, String quoteNum) throws QuoteException{
        HashMap params = new HashMap();
        params.put("piUserId", userID);
        params.put("piQuoteNum", quoteNum);
        params.put("piChgReqStage", QuoteConstants.SLS_REP_COMPLT_REQ);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_RNWL_CHG_REQ, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_I_QT_RNWL_CHG_REQ, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            
            this.commitTransaction();
            
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }    

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#isBidItraionSubmitable(java.lang.String, java.lang.String)
     */
    @Override
	public ApproverRuleValidation filterBidIterQtApr(String orignQuoteNum, String approvalInfo, int softBidIter, int saasBidIter) throws QuoteException {

        HashMap params = new HashMap();
        params.put("piOrignQuoteNum", orignQuoteNum);
        params.put("piTypeLevels", approvalInfo);
        params.put("piSoftIter", softBidIter);
        params.put("piSaasIter", saasBidIter);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_BID_ITERATN_VERIFY, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_BID_ITERATN_VERIFY, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                           
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            ApproverRuleValidation ruleVal = new ApproverRuleValidation();
            int bidIteratnSubmitable = ps.getInt(2);
            ruleVal.setBidIterFlag(bidIteratnSubmitable == 1);
            
            logger.info(this, "the flag of bid iteration submittable is: " + bidIteratnSubmitable);
            
            if ( ruleVal.isBidIterFlag() )
            {
            	ResultSet rs = ps.getResultSet();
            	List<String> apprList = new ArrayList<String>();
            	while ( rs.next() )
            	{
            		apprList.add(rs.getString("SPECL_BID_APPRVR_TYPE"));
            	}
            	ruleVal.setApproveTypes(apprList);
            }
            
            this.commitTransaction();
            return ruleVal;
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    @Override
	public ApproverRuleValidation filterStrmlndQtApr(String geo, String progCode, String piApprovers) throws QuoteException {

        HashMap params = new HashMap();
        params.put("piApprovers", piApprovers);
        params.put("piGeo", geo);
        params.put("piProgCode", progCode);
        
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_FILTER_STRMLND_APR, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_FILTER_STRMLND_APR, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                           
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            ApproverRuleValidation ruleVal = new ApproverRuleValidation();
            ruleVal.setStreamLineFlag(true);
            List<String> approveTypes = new ArrayList<String>();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
            	approveTypes.add(StringUtils.trimToEmpty(rs.getString(1)));
            }
            ruleVal.setApproveTypes(approveTypes);
            rs.close();
            
            this.commitTransaction();
            return ruleVal;
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    /**
     * For create a sales quote from an order requirement
     * if quote create successfully,newly session quote num return
     * if failed errorCode return:
     * 5003 indicate that can't decide quote lob
     * 5004 indicate that acquisition is empty for FCT quote
     */
    @Override
	public Map createQuoteFromOrder(String userId, String orderNum) throws QuoteException {
        
        String webQuoteNum = "";
        Map results = new HashMap();
        HashMap params = new HashMap();
        params.put("piUserID", userId);
        params.put("piOrderNum", orderNum);
        try {
            //begin topaz transaction
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_CREATE_QT_FROM_ORDER, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_I_QT_CREATE_QT_FROM_ORDER, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);
            webQuoteNum = StringUtils.trimToEmpty(callStmt.getString(2));
            //end topaz transaction
            this.commitTransaction();

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logger.info(this, "Can't create sales quote from this order,error code is : " + poGenStatus);
            }
            results.put("webQuoteNum", webQuoteNum);
            results.put("errorCode", poGenStatus);
            
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
        return results;
    }    
    
    @Override
	public String getPreCreditCheckedQuoteNum(String currentQuoteNum, int validMonths) throws QuoteException {
        String preCreditCheckedQuoteNum = "";
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", currentQuoteNum);
        params.put("piValidMonths", validMonths);
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_PRE_CREDIT_CHK, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_PRE_CREDIT_CHK, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                           
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS
                    && returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new SPException(returnCode);
            }
            
            if (returnCode == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                preCreditCheckedQuoteNum = ps.getString(4);
            }
            logger.debug(this, "pre-Credit Checked Quote Number is: " + preCreditCheckedQuoteNum);
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return preCreditCheckedQuoteNum;
    }

    /**
     * check if specific product is supported by CPQ
     * true means support, false means not
     * return code: -1 not Support for restriction, 0 not Support, 1 support
     */
	@Override
	public int isProdSuppbyCPQ(String prodID, String webQuoteNum, String audCode) throws QuoteException {
		
		int isProdSuppFlag = 0;		
		HashMap params = new HashMap();
        params.put("piProdID", prodID);
        params.put("piAudCode", audCode);
        params.put("piWebQuoteNum", webQuoteNum);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_PID_SUPP_BY_CPQ, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_PID_SUPP_BY_CPQ, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);
            int result = ps.getInt(3);
            
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            
            this.commitTransaction();
            
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            return result;
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
	}
    @Override
	public List retrieveLineItemsFromOrder(String chargeAgreementNum, String configId) throws QuoteException{

		List itemList = null;		
		HashMap params = new HashMap();
        params.put("piChrgAgrmtNum", chargeAgreementNum);
        params.put("piConfigId", configId);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(4);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            int lineItemCount = ps.getInt(3);
            if(lineItemCount>0){

                itemList = new ArrayList();
                ResultSet rs = ps.getResultSet();
                String qtyStr = null;
                while (rs.next()) {
                	ActiveService as = new ActiveService();
                	if(rs.getObject(1)!=null){
                    	as.setPartNumber(rs.getString(1).trim());
                	}
                	if(rs.getObject(2)!=null){
                    	as.setBillingFrequency(rs.getString(2).trim());
                	}
                	if(rs.getObject(3)!=null){
                		qtyStr = rs.getString(3).trim();
                		// if qtyStr is '1.000', convert it to '1'
                    	as.setQuantity(qtyStr.substring(0,qtyStr.indexOf(".")<0?qtyStr.length():qtyStr.indexOf(".")));
                	}
                	if(rs.getObject(4)!=null){
                    	as.setTerm(rs.getString(4).trim());
                	}
                	if(rs.getObject(5)!=null){
                    	as.setRampupFlag(rs.getString(5).trim());
                	}
                	if(rs.getObject(6)!=null){
                    	as.setRampupSeqNum(rs.getString(6).trim());
                	}
                	if(rs.getObject(7)!=null){
                		as.setEndDate(DateUtil.formatDate(rs.getDate(7)));
                	}
                	if(rs.getObject(9)!=null && rs.getString(9).trim().equals("1")){
                		as.setSetupFlag(true);
                	}else{
                		as.setSetupFlag(false);
                	}

                	if(rs.getObject(10)!=null && rs.getString(10).trim().equals("1")){
                		as.setSbscrptnFlag(true);
                	}else{
                		as.setSbscrptnFlag(false);
                	}

                	if(rs.getObject(11)!=null && rs.getString(11).trim().equals("1")){
                		as.setActiveFlag(true);
                	}else{
                		as.setActiveFlag(false);
                	}
                	
                	if(rs.getObject(15)!=null){
                		as.setCotermEndDate(rs.getDate(15));
                	}
                	
                	if(rs.getObject(16)!=null){
                		as.setRenwlTermMths(new Integer(rs.getInt(16)));
                	}
                	
                	if(rs.getObject(17)!=null){
                		as.setOrdAddDate(rs.getDate(17));
                	}
                	
                	if(rs.getObject(20) != null){
                		as.setNewxtRenwlDate(rs.getDate(20));
                	}
                	
                	if(rs.getObject(22) != null){
                		as.setRenwlModelCode(rs.getString(22));
                	}
                	
                	if(rs.getObject(23)!=null){
                		as.setStartDate(DateUtil.formatDate(rs.getDate(23)));
                	}
                	itemList.add(as);
                }
                rs.close();
            }
            //Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/CA0C6DC3B816FE1485257A860060B684 
            ps.close();
            
            this.commitTransaction();
            
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return itemList;
    }
    
    @Override
	public List retrieveLineItemsFromOrderNoTx(String chargeAgreementNum, String configId) throws QuoteException{

		List itemList = null;		
		HashMap params = new HashMap();
        params.put("piChrgAgrmtNum", chargeAgreementNum);
        params.put("piConfigId", configId);
        
        try {
//            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(4);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            int lineItemCount = ps.getInt(3);
            if(lineItemCount>0){

                itemList = new ArrayList();
                ResultSet rs = ps.getResultSet();
                String qtyStr = null;
                while (rs.next()) {
                	ActiveService as = new ActiveService();
                	if(rs.getObject(1)!=null){
                    	as.setPartNumber(rs.getString(1).trim());
                	}
                	if(rs.getObject(2)!=null){
                    	as.setBillingFrequency(rs.getString(2).trim());
                	}
                	if(rs.getObject(3)!=null){
                		qtyStr = rs.getString(3).trim();
                		// if qtyStr is '1.000', convert it to '1'
                    	as.setQuantity(qtyStr.substring(0,qtyStr.indexOf(".")<0?qtyStr.length():qtyStr.indexOf(".")));
                	}
                	if(rs.getObject(4)!=null){
                    	as.setTerm(rs.getString(4).trim());
                	}
                	if(rs.getObject(5)!=null){
                    	as.setRampupFlag(rs.getString(5).trim());
                	}
                	if(rs.getObject(6)!=null){
                    	as.setRampupSeqNum(rs.getString(6).trim());
                	}
                	if(rs.getObject(7)!=null){
                		as.setEndDate(DateUtil.formatDate(rs.getDate(7)));
                	}
                	if(rs.getObject(8) != null){
                		as.setLineItemSeqNumber(new Integer(rs.getInt(8)));
                	}
                	if(rs.getObject(9)!=null && rs.getString(9).trim().equals("1")){
                		as.setSetupFlag(true);
                	}else{
                		as.setSetupFlag(false);
                	}

                	if(rs.getObject(10)!=null && rs.getString(10).trim().equals("1")){
                		as.setSbscrptnFlag(true);
                	}else{
                		as.setSbscrptnFlag(false);
                	}

                	if(rs.getObject(11)!=null && rs.getString(11).trim().equals("1")){
                		as.setActiveFlag(true);
                	}else{
                		as.setActiveFlag(false);
                	}
                	
                	if(rs.getObject(15)!=null){
                		as.setCotermEndDate(rs.getDate(15));
                	}
                	
                	if(rs.getObject(16)!=null){
                		as.setRenwlTermMths(new Integer(rs.getInt(16)));
                	}
                	
                	if(rs.getObject(17)!=null){
                		as.setOrdAddDate(rs.getDate(17));
                	}
                	
                	if(rs.getObject(20) != null){
                		as.setNewxtRenwlDate(rs.getDate(20));
                	}
                	
                	if(rs.getObject(22) != null){
                		as.setRenwlModelCode(rs.getString(22));
                	}
                	
                	if(rs.getObject(23)!=null){
                		as.setStartDate(DateUtil.formatDate(rs.getDate(23)));
                	}
                	
                	if(rs.getObject(24)!=null){
                		as.setRenewalEndDate(rs.getDate(24));
                	}
                	
                	itemList.add(as);
                }
                rs.close();
            }
            //Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/CA0C6DC3B816FE1485257A860060B684 
            ps.close();
            
//            this.commitTransaction();
            
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
//            this.rollbackTransaction();
        }
        
        return itemList;
    }
    @Override
	public String createQuoteFromOrderForConfigurator(String orderNum, String userId, String audCode) throws QuoteException{
    	String webQuoteNum = null;
		HashMap params = new HashMap();
        params.put("piOrderNum", orderNum);
        params.put("piUserID", userId);
        params.put("piAudCode", audCode);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_I_QT_CREATE_QT_FROM_ORDER_FOR_CONFIG, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_I_QT_CREATE_QT_FROM_ORDER_FOR_CONFIG, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            webQuoteNum = ps.getString(2);
            
            this.commitTransaction();
            
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return webQuoteNum;
    }
    

    @Override
	public void copyQuoteInfoFromOrderForConfigurator(String orderNum, String webQuoteNum, String userId, String audCode) throws QuoteException{
    	
		HashMap params = new HashMap();
		params.put("piSessionQuoteNum", webQuoteNum);
        params.put("piOrderNum", orderNum);
        params.put("piUserID", userId);
        params.put("piAudCode", audCode);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_COPY_INFO_FROM_ORD_FOR_CONFIG, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_COPY_INFO_FROM_ORD_FOR_CONFIG, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            this.commitTransaction();
            
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
    }
    

    @Override
	public void copyCustFromOrderToQuote(String webQuoteNum, String orderNum, String userId) throws QuoteException{
    	
		HashMap params = new HashMap();
        params.put("piSessionQuoteNum", webQuoteNum);
        params.put("piOrderNum", orderNum);
        params.put("piUserID", userId);
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_CUST_FROM_ORDER, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_CUST_FROM_ORDER, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            
            this.commitTransaction();
            
            
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

        @Override
		public ChargeAgreement getChargeAgreementInfo(String chargeAgreementNum, String configurationId) throws QuoteException{
    	
		
        ChargeAgreement ca = null;
        try {
			this.beginTransaction();
            
			ca = getChargeAgreementInfoWithoutTransaction(chargeAgreementNum, configurationId);
            
			this.commitTransaction();
            
            
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return ca;
    }
        
	public ChargeAgreement getChargeAgreementInfoWithoutTransaction(String chargeAgreementNum, String configurationId) throws QuoteException {

		HashMap params = new HashMap();
		params.put("piCaNum", chargeAgreementNum);
		params.put("piConfigId", configurationId);
		ChargeAgreement ca = null;
		try {

			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_CA_INFO, null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_CA_INFO, params);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(1);

			logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			ca = new ChargeAgreement();

			ca.setEndDate(ps.getTimestamp(4));

		} catch (SQLException e) {
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		} catch (TopazException e) {
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		}

		return ca;
	}
    @Override
	public List getMigrateParts(String caNum) throws QuoteException
    {
    	HashMap params = new HashMap();
        params.put("caNum", caNum);
        List list = new ArrayList();
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_MIGRATE_PART, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_MIGRATE_PART, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            ResultSet rs = ps.getResultSet();
            while ( rs.next() )
            {
            	MigratePart part = new MigratePart_Impl();
            	part.setConfId(rs.getString("CONFIGRTN_ID"));
            	part.setPartNum(rs.getString("PART_NUM"));
            	part.setPartDesc(rs.getString("PART_DSCR"));
            	part.setSeqNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
            	part.setOnDemandPart(StringUtils.equals("1", rs.getString("SAAS_ON_DMND_FLAG")));
            	part.setMigration(true);
            	part.setPastDateFlag(StringUtils.equals("1", rs.getString("PAST_DATE_FLAG")));
            	list.add(part);
            }
           
            
            
            this.commitTransaction();
            
          
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);           
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return list;	
    }
    
    @Override
	public String addMigrateParts(String caNum,String migrtnReqstNum,String partNums,String lineNums,
			String billingFreq,String coverageTerm,String userId) throws QuoteException{
    	HashMap params = new HashMap();
        params.put("piCaNum", caNum);
        params.put("piRequestNum", migrtnReqstNum);
        params.put("piPartNums", partNums);
        params.put("piLineNums", lineNums);
        params.put("piBillingFreq", billingFreq);
        params.put("piCoverageTerm", coverageTerm);
        params.put("piUserId", userId);
        
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MIGRATE_PART, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MIGRATE_PART, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            String poRequestNum = ps.getString(2);
            
            this.commitTransaction();
            
            return poRequestNum;
          
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);           
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    @Override
	public Map getWebApplCodesByColName(String cnstntName)throws QuoteException{
    	Map map = new HashMap();
    	HashMap parms = new HashMap();
        parms.put("piCnstntName", cnstntName);
       

        try {
            this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_WEB_APP_CNSTNT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_WEB_APP_CNSTNT, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            List result = new ArrayList();
            ResultSet rs = ps.getResultSet();
            String code = null;
            String colName = null;
            if (rs != null) {
                while (rs.next()) {
                	code = StringUtils.trimToEmpty(rs.getString("CODE"));
                	colName = StringUtils.trimToEmpty(rs.getString("col_name"));
                	map.put(code, colName);
                }
                rs.close();
            }
            this.commitTransaction();
            return map;

        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }
    @Override
	public void updateBPQuoteStage(String userId, String webQuoteNum,String quoteStageCode,Integer delSessFlag,Integer delLockFlag,String comments)throws QuoteException{
    	HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piQuoteStage", quoteStageCode);
        parms.put("piSubmittrEmail", userId);
        parms.put("piDelSess", delSessFlag);
        parms.put("piDelLock", delLockFlag);
        parms.put("piComments", comments == null ? "" : comments);
        
        
        logger.info(this, "webQuoteNum:" + webQuoteNum + " quoteStage:" + quoteStageCode  + " userId:" + userId + "comments :"+comments);
        this.beginTransaction();
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_EVAL_STAGE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_EVAL_STAGE, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            logger.info(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);
            this.commitTransaction();
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    }

	@Override
	public boolean eraseECData(String creatorId) throws QuoteException  {
		// TODO Auto-generated method stub
		String webQuoteNum = "";
		try{
			webQuoteNum = super.getQuoteHdrInfo(creatorId).getWebQuoteNum();
		}catch (NoDataException nde) {
            throw new QuoteException("Quote header is not found for the login user " + creatorId);
        }
		
		this.updateECData(webQuoteNum);
		
		return true;
	}
	
	/**
	 * Clean the EC data
	 * @param webQuoteNum
	 */
	private void updateECData(String webQuoteNum)throws QuoteException{
		HashMap parms = new HashMap();
		parms.put("piWebQuoteNum", webQuoteNum);
		
		this.beginTransaction();
		try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_EQUITY_CURVE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_EQUITY_CURVE, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            this.commitTransaction();
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
	}

    @Override
	public void updateSapCtrctNum(String webQuoteNum, String sapCtrctNum)
			throws QuoteException{
    	HashMap parms = new HashMap();
		parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piSapCtrctNum", sapCtrctNum);
        
		logger.info(this, "webQuoteNum:" + webQuoteNum + " sapCtrctNum:" + sapCtrctNum);
        this.beginTransaction();
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_WEB_CUST_CNTRCT_NUM, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_WEB_CUST_CNTRCT_NUM, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            logger.info(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);
            this.commitTransaction();
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    public String getWebEnrollmentNum()throws QuoteException{
    	HashMap parms = new HashMap();
    	  try {
              this.beginTransaction();
              QueryContext queryCtx = QueryContext.getInstance();
              String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_WEB_ENRLLMT_NUM, null);
              CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
              queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_WEB_ENRLLMT_NUM,parms);
              ps.execute();
              int poGenStatus = ps.getInt(1);

              if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                  throw new NoDataException();
              } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                  throw new SPException(LogHelper.logSPCall(sqlQuery,parms), poGenStatus);
              }

              String enrollmentNum = ps.getString(2);
              
              this.commitTransaction();
              return enrollmentNum;

          } catch (Exception e) {
              logger.debug(this, e.getMessage());
              throw new QuoteException(e);
          } finally {
              this.rollbackTransaction();
          }	
    }
    public String getWebEnrllmtNum()throws QuoteException{
        try {
            //begin the transaction
            this.beginTransaction();
            String webEnrllmtNum = this.getWebEnrollmentNum();
            
            //commit the transaction
            this.commitTransaction();
            return webEnrllmtNum;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

	@Override
	public Map<String,String> refreshSCODSConnPoolConfig() throws QuoteException {
    	HashMap<String,String> returnValues = new HashMap<String,String>();
  	  try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_SCODS_MNG_CONFIG, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_SCODS_MNG_CONFIG,returnValues);
            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery,returnValues), poGenStatus);
            }

            returnValues.put("PORETRYINTERVAL", ps.getString(2));
            returnValues.put("POLOCKPOOLMAX", ps.getString(3));
            returnValues.put("POWAITINGMAX", ps.getString(4));
            returnValues.put("POWAITTIMEOUT", ps.getString(5));
            returnValues.put("POQUIETDECTTIME", ps.getString(6));
            returnValues.put("POCONFIGRELOADINTER", ps.getString(7));
            
            this.commitTransaction();
            return returnValues;

        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }	
  };	        
  
  public void dsjUpdateQuoteInfo(String webQuoteNum,String briefTitle,  String opprNum, String userEmail) throws QuoteException {
      HashMap parms = new HashMap();
      parms.put("piWebQuoteNum", webQuoteNum);
      parms.put("piQuoteTitle", briefTitle==null?"":briefTitle);
      parms.put("piOpprtntyNum", opprNum==null?"":opprNum);
      parms.put("piUserID", userEmail);
      try {
          this.beginTransaction();
          QueryContext queryCtx = QueryContext.getInstance();
          String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_DSJ_INFO, null);
          CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
          queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_DSJ_INFO, parms);
          logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

          ps.execute();

          int returnCode = ps.getInt(1);

          this.commitTransaction();

          if (returnCode != 0) {
              throw new TopazException("Failed to update opportunity information, return code is :" + returnCode);
          }
      } catch (SQLException e) {
          logger.error(this, e.getMessage());
          throw new QuoteException(e);
      } catch (TopazException e) {
          logger.error(this, e.getMessage());
          throw new QuoteException(e);
      } finally {
          this.rollbackTransaction();
      }
  }
  public void dsjInsertQuoteFlag(String webQuoteNum,String channel,  String trailId, String userEmail) throws QuoteException {
      HashMap parms = new HashMap();
      parms.put("piWebQuoteNum", webQuoteNum);
      parms.put("piChannel", channel==null?"":channel);
      parms.put("piTrialId", trailId==null?"":trailId);
      parms.put("piCreatorId", userEmail==null?"":userEmail);
      try {
          this.beginTransaction();
          QueryContext queryCtx = QueryContext.getInstance();
          String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_WEB_QUOTE_EXT_PRPTY, null);
          CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
          queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_WEB_QUOTE_EXT_PRPTY, parms);
          logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

          ps.execute();

          int returnCode = ps.getInt(1);

          this.commitTransaction();

          if (returnCode != 0) {
              throw new TopazException("Failed to update dsj quote flag, return code is :" + returnCode);
          }
      } catch (SQLException e) {
          logger.error(this, e.getMessage());
          throw new QuoteException(e);
      } catch (TopazException e) {
          logger.error(this, e.getMessage());
          throw new QuoteException(e);
      } finally {
          this.rollbackTransaction();
      }
  }  
  public boolean isDsjQuote(String webQuoteNum) throws QuoteException {
      HashMap parms = new HashMap();
      parms.put("piWebQuoteNum", webQuoteNum);
    
      try {
          this.beginTransaction();
          QueryContext queryCtx = QueryContext.getInstance();
          String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_IS_DSJ, null);
          CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
          queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_IS_DSJ, parms);
          logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

          ps.execute();

          int returnCode = ps.getInt(1);
          
          int isDsjQuote = ps.getInt(3);
          this.commitTransaction();

          if (returnCode != 0) {
              throw new TopazException("Failed to find dsj quote , return code is :" + returnCode);
          }
          
          if(isDsjQuote==1)
        	  return true;
          else 
        	  return false;
      } catch (SQLException e) {
          logger.error(this, e.getMessage());
          throw new QuoteException(e);
      } catch (TopazException e) {
          logger.error(this, e.getMessage());
          throw new QuoteException(e);
      } finally {
          this.rollbackTransaction();
      }
  }
}