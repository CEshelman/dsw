package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.LineOfBusinessFactory;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteHeaderFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-8
 */

public class QuoteHeaderFactory_jdbc extends QuoteHeaderFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     *  
     */
    public QuoteHeaderFactory_jdbc() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteHeaderFactory#findByCreatorID(java.lang.String)
     */
    public QuoteHeader findByCreatorID(String creatorId) throws TopazException {
        QuoteHeader quoteHeader = (QuoteHeader) this.getFromCache(creatorId);
        if (quoteHeader == null) {
            QuoteHeader_jdbc quoteHeader_jdbc = new QuoteHeader_jdbc(creatorId);
            quoteHeader_jdbc.webQuoteNum = null;
            quoteHeader_jdbc.hydrate(TopazUtil.getConnection());
            quoteHeader = quoteHeader_jdbc;
            this.putInCache(creatorId, quoteHeader_jdbc);
        }
        return quoteHeader;
    }

    /**
     * user for create quote or delete quote
     */
    public QuoteHeader createQuote(String creatorID) throws TopazException {
        String key ="Quote_"+creatorID; 
        
        QuoteHeader quoteHeader = (QuoteHeader) this.getFromCache(key);
        // There's no quoteNum before the quote is created in DB2, use the creatorID as key
        // only one new quote can be created during one transaction.
        
        if (quoteHeader == null) {
            QuoteHeader_jdbc quoteHeader_jdbc = new QuoteHeader_jdbc(creatorID);
            quoteHeader_jdbc.quoteStageCode = QuoteConstants.QUOTE_STAGE_CODE_SESSION;
            quoteHeader_jdbc.isNew(true); 
            quoteHeader = quoteHeader_jdbc;
            this.putInCache(key, quoteHeader_jdbc);
        }
        return quoteHeader;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.domain.DraftSalesQuoteFactory#findQuotes(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public List findSavedQuotes(String userId, String ownerFilter, String timeFilter) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorID", userId);
        parms.put("piOwnerFilter", ownerFilter);
        parms.put("piTimeFilter", timeFilter);
        
        ResultSet rs = null;
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.SP_LOAD_QUOTES, null);

        try {
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, NewQuoteDBConstants.SP_LOAD_QUOTES, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = callStmt.execute();
            int poGenStatus = callStmt.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            rs = callStmt.getResultSet();
            
            List draftSalesQuoteList = new ArrayList();
            if (rs != null) {
                draftSalesQuoteList = loadDraftQuotesFromResultset(rs, userId);
            }
            
            rs.close();
            
            Iterator iter = draftSalesQuoteList.iterator();
            QuoteHeader_jdbc draftSalesQuote;
            while(iter.hasNext()) {
                draftSalesQuote = (QuoteHeader_jdbc)iter.next();
                draftSalesQuote.systemLOB =  LineOfBusinessFactory.singleton().findLOBByCode(draftSalesQuote.getLob().getCode());
            }

            return draftSalesQuoteList;
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall( NewQuoteDBConstants.SP_LOAD_QUOTES, parms));
            throw new TopazException("Exception when execute the SP " + NewQuoteDBConstants.SP_LOAD_QUOTES, e);
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
    }

    public String notNullString(String str) {
        return null != str ? str.trim() : "";
    }

    /**
     * @param rs
     * @param draftSalesQuoteList
     * @return
     */
    private List loadDraftQuotesFromResultset(ResultSet rs, String userId) throws SQLException, TopazException {
        List draftSalesQuoteList = new ArrayList();
        while (rs.next()) {
            String webQuoteNum = rs.getString("web_quote_num");
            String quoteTitle = rs.getString("quote_title");
            String progCode = rs.getString("prog_code");
            Timestamp modDate = rs.getTimestamp("mod_date");
            String soldToCustNum = rs.getString("sold_to_cust_num");
            String custNameFull = rs.getString("cust_name_full");
            String quoteDscr = rs.getString("quote_dscr");
            String progMigrtnCode = rs.getString("prog_migrtn_code");
            String modByFullName = rs.getString("MOD_BY_NAME");
            String agrmtTypeCode = rs.getString("AGRMT_TYPE_CODE");
            
            // add QUOTE_PRICE_TOT,PRIOR_QUOTE_NUM and CURRNCY_CODE
            double quotePriceTot = rs.getDouble("QUOTE_PRICE_TOT");
            String priorQuoteNum = rs.getString("PRIOR_QUOTE_NUM");
            String currencyCode = rs.getString("CURRNCY_CODE");
            
            QuoteHeader_jdbc draftSalesQuote = new QuoteHeader_jdbc(userId);
            draftSalesQuote.webQuoteNum = notNullString(webQuoteNum);
            draftSalesQuote.quoteTitle = notNullString(quoteTitle);
            //draftSalesQuote.lob = LineOfBusinessFactory.singleton().findLOBByCode(notNullString(progCode));
            draftSalesQuote.lob = new CodeDescObj_jdbc(notNullString(progCode), "");
            draftSalesQuote.modDate = modDate;
            draftSalesQuote.soldToCustNum = notNullString(soldToCustNum);
            draftSalesQuote.custName = notNullString(custNameFull);
            draftSalesQuote.quoteDscr = notNullString(quoteDscr);
            draftSalesQuote.progMigrationCode = progMigrtnCode;
            draftSalesQuote.setModByFullName(notNullString(modByFullName));
            draftSalesQuote.quotePriceTot = quotePriceTot;
            draftSalesQuote.priorQuoteNum = notNullString(priorQuoteNum);
            draftSalesQuote.currencyCode = notNullString(currencyCode);
            draftSalesQuote.setAgrmtTypeCode(agrmtTypeCode);
            
            
            draftSalesQuoteList.add(draftSalesQuote);
        }
        return draftSalesQuoteList;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteHeaderFactory#updateCustomer(java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateCustomer(String creatorId, String webQuoteNum, String customerNum, String contractNum, int webCustId, String currencyCode,String endUserFlag) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : StringHelper.fillString(webQuoteNum));
        parms.put("piSoldToCustNum", customerNum == null ? "" : StringHelper.fillString(customerNum));
        parms.put("piSapCtrctNum", contractNum == null ? "" : StringHelper.fillString(contractNum));
        parms.put("piWebCustId", new Integer(webCustId));
        parms.put("piCurrncyCode", currencyCode);
        parms.put("piEndUserFlag", endUserFlag == null ? "" : endUserFlag.trim());
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_CUST_CTRCT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_CUST_CTRCT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            } 
        } catch (SQLException e) {
            throw new TopazException(e);
        }
        
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteHeaderFactory#updateEndUser(java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateEndUser(String creatorId, String webQuoteNum, String endCustNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : StringHelper.fillString(webQuoteNum));
        parms.put("piEndCustNum", endCustNum == null ? "" : StringHelper.fillString(endCustNum));

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_END_CUST, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_END_CUST, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            } 
        } catch (SQLException e) {
            throw new TopazException(e);
        }
        
    }
    
    public void updateExprDateFullfill(String creatorId, Date expireDate, String fullfillmentSrc, String partnerAccess,
            int rselToBeDtrmndFlg, int distribtrToBeDtrmndFlg, String quoteClassfctnCode, Date startDate,
            String oemAgreementType, int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspProviderType) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        java.sql.Date qtExpireDate = expireDate == null ? null : new java.sql.Date(expireDate.getTime());
        parms.put("piQuoteMaxEprDate", qtExpireDate);
        parms.put("piFulfillmentSrc", fullfillmentSrc);
        parms.put("piRenwlPrtnrAccessFlag", partnerAccess);
        parms.put("piRselToBeDtrmndFlg", new Integer(rselToBeDtrmndFlg));
        parms.put("piDistribtrToBeDtrmndFlg", new Integer(distribtrToBeDtrmndFlg));
        parms.put("piQuoteClassfctnCode", quoteClassfctnCode);
        java.sql.Date qtStartDate = startDate == null ? null : new java.sql.Date(startDate.getTime());
        parms.put("piEffDate", qtStartDate);
        parms.put("piOrdgMethodCode", oemAgreementType);
        parms.put("piPymTermsDays", new Integer(pymTermsDays));
        parms.put("piOemQuoteType", new Integer(oemBidType));
        java.sql.Date qtEstmtdOrdDate = estmtdOrdDate == null ? null : new java.sql.Date(estmtdOrdDate.getTime());
        parms.put("piEstmtdOrdDate", qtEstmtdOrdDate);
        parms.put("pisspProviderType", sspProviderType);
        java.sql.Date qtCustReqstdArrivlDate = custReqstdArrivlDate == null ? null : new java.sql.Date(custReqstdArrivlDate.getTime());
        parms.put("piCustReqstdArrivlDate", qtCustReqstdArrivlDate);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_FULFLL_EXPDTE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_FULFLL_EXPDTE, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            } 
        } catch (SQLException sqle) {
            logContext.error(this, sqle.getMessage());
            throw new TopazException(sqle);
        }
        
    }
    
    public void updateSalesInfo(String creatorId, Date expireDate, String briefTitle, String quoteDesc,
            String busOrgCode, String opprtntyNum, String exemptnCode, String upsideTrendTowardsPurch,
            String salesOdds, String tacticCodes, String comments, String quoteClassfctnCode, String salesStageCode,
            String custReasCode, Date startDate, String oemAgreementType, int blockRenewalReminder, int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspProviderType) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piQuoteTitle", briefTitle);
        parms.put("piQuoteDscr", quoteDesc);
        parms.put("piBusOrgCode", busOrgCode);
        parms.put("piOpprtntyNum", opprtntyNum);
        parms.put("piExemptnCode", exemptnCode);
        parms.put("piQuoteMaxEprDate", expireDate == null ? null : new java.sql.Date(expireDate.getTime()));
        parms.put("piUpsideTrendTowardsPurch", upsideTrendTowardsPurch);
        parms.put("piRenwlQuoteSalesOddsCode", salesOdds);
        parms.put("piTaticCodesList", tacticCodes);
        parms.put("piComment", comments);
        parms.put("piQuoteClassfctnCode", quoteClassfctnCode);
        parms.put("piSalesStageCode", salesStageCode);
        parms.put("piCustReasCode", custReasCode);
        parms.put("piEffDate", startDate == null ? null : new java.sql.Date(startDate.getTime()));
        parms.put("piOrdgMethodCode", oemAgreementType);
        parms.put("piBlockRenewalReminder", new Integer(blockRenewalReminder));
        parms.put("piPymTermsDays", new Integer(pymTermsDays));
        parms.put("piOemQuoteType", new Integer(oemBidType));
        parms.put("pisspProviderType", sspProviderType);
        java.sql.Date qtEstmtdOrdDate = estmtdOrdDate == null ? null : new java.sql.Date(estmtdOrdDate.getTime());
        parms.put("piEstmtdOrdDate", qtEstmtdOrdDate);
        java.sql.Date qtCustReqstdArrivlDate = custReqstdArrivlDate == null ? null : new java.sql.Date(custReqstdArrivlDate.getTime());
        parms.put("piCustReqstdArrivlDate", qtCustReqstdArrivlDate);
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_SALES_INFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_SALES_INFO, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            } 
        } catch (SQLException e) {
            throw new TopazException(e);
        }
    }
    
    public void saveDraftQuote(String creatorId, boolean createNewCopy) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);
        parms.put("piNewCopy", createNewCopy ? "1" : "0");

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_SAVE_DRAFT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_SAVE_DRAFT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            
            if(returnCode == CommonDBConstants.NO_LOB_COUNTRY) {
                throw new SPException(CommonDBConstants.NO_LOB_COUNTRY);
            } else if(returnCode == CommonDBConstants.NO_QUOTE_TITLE) {
                throw new SPException(CommonDBConstants.NO_QUOTE_TITLE);
            } else if (returnCode != 0) {
                throw new TopazException("Failed to update sales information, return code is :" + returnCode);
            } 
        } catch (SQLException e) {
            throw new TopazException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteHeaderFactory#updateQuoteSubmission(java.lang.String, int, int, int, int, int, int, int, java.lang.String)
     */
    public void updateQuoteSubmission(String creatorId, String webQuoteNum, int reqstICNFlag,
            int reqstPreCreditChkFlag, int inclTaxFlag, int includeFOL,int sendToQTCntFlag, int sendToPrmryCntFlag,
            int sendToAddtnlCntFlag, String addtnlCntEmailAdr, String qtCoverEmail, int incldLineItmDtlQuoteFlg,
            int sendQuoteToAddtnlPrtnrFlg, String addtnlPrtnrEmailAdr, int PAOBlockFlag , int supprsPARegstrnEmailFlag,
            String preCreditCheckedQuoteNum, Integer fctNonStdTermsConds, String quoteOutputType, String softBidIteratnQtInd, String saasBidIteratnQtInd,Integer saaSStrmlndApprvlFlag,String quoteOutputOption,Integer budgetaryQuoteFlag) throws TopazException {

        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId == null ? "" : creatorId);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        parms.put("piReqstICNFlag", new Integer(reqstICNFlag));
        parms.put("piReqstPreCreditChkFlag", new Integer(reqstPreCreditChkFlag));
        parms.put("piInclTaxFlag", new Integer(inclTaxFlag));
        parms.put("piBudgetaryQuoteFlag", new Integer(budgetaryQuoteFlag));
        
		if ((includeFOL != 0) && (includeFOL!= 1) ) {
			parms.put("piInclFirmOrderLetter", 0);
		} else {
			parms.put("piInclFirmOrderLetter", new Integer(includeFOL));
		}
        parms.put("piSendToQTCntFlag", new Integer(sendToQTCntFlag));
        parms.put("piSendToPrmryCntFlag", new Integer(sendToPrmryCntFlag));
        parms.put("piSendToAddtnlCntFlag", new Integer(sendToAddtnlCntFlag));
        parms.put("piAddtnlCntEmailAdr", addtnlCntEmailAdr);
        parms.put("piQTCoverEmail", qtCoverEmail);
        parms.put("piIncldLineItmDtlQuoteFlg", new Integer(incldLineItmDtlQuoteFlg));
        parms.put("piSendQuoteToAddtnlPrtnrFlg", new Integer(sendQuoteToAddtnlPrtnrFlg));
        parms.put("piAddtnlPrtnrEmailAdr", addtnlPrtnrEmailAdr);
        parms.put("piPAOBlockFlag", new Integer(PAOBlockFlag));
        parms.put("piSupprsPARegstrnEmailFlag", new Integer(supprsPARegstrnEmailFlag));
        parms.put("piPreCreditCheckedQuoteNum", preCreditCheckedQuoteNum);
        parms.put("piFctNonStdTermsConds", fctNonStdTermsConds);
        parms.put("piQuoteOutputType", quoteOutputType);
        if(saaSStrmlndApprvlFlag != null){
        	parms.put("piSaaSStrmlndApprvlFlag", saaSStrmlndApprvlFlag);
        }
        if(StringUtils.isNotBlank(softBidIteratnQtInd)){
        	parms.put("piSoftBidIteratnQtInd", new Integer(softBidIteratnQtInd));
        }
        if(StringUtils.isNotBlank(saasBidIteratnQtInd)){
        	parms.put("piSaasBidIteratnQtInd", new Integer(saasBidIteratnQtInd));
        }
        parms.put("piQuoteOutputOption", quoteOutputOption);
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_SUBMISSION, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_SUBMISSION, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
        	logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_U_QT_SUBMISSION, parms));
            throw new TopazException(e);
        }
    }
    
    public void updateQuoteStage(String userId, String webQuoteNum, String quoteStageCode, String sapIDocNum) throws TopazException{
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piQuoteStage", quoteStageCode);
        parms.put("piSapIntrmdiatDocNum", sapIDocNum);
        parms.put("piSubmittrEmail", userId);
        
        logContext.info(this, "webQuoteNum:" + webQuoteNum + " quoteStage:" + quoteStageCode + " sapIdoc:"
                + sapIDocNum + " userId:" + userId);
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_STAGE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_STAGE, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.info(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            
            if (retCode) {
                rs = ps.getResultSet();
                while (rs.next()) {
                    int sqlCode = rs.getInt(1);
                    String sqlState = rs.getString(2);
                    logContext.info(this, "sqlCode:"+sqlCode+" sqlState:"+sqlState);
                }
                rs.close();
            }

            if (returnCode != 0) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            throw new TopazException(e);
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
    }
    
    public void updateQuoteStageToCancel(String userId, String webQuoteNum) throws TopazException {
        
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piUserEmailAdr", userId);
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_CANCEL, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_CANCEL, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            throw new TopazException(e);
        }
    }
    
    public String copyUpdateSbmtQuoteToSession(String quoteNum, String creatorId, int webRefFlag) throws TopazException {
        HashMap parms = new HashMap();
        String destQuoteNum  = null;
        parms.put("piWebQuoteNum", quoteNum);
        parms.put("piUserID", creatorId);
        parms.put("piWebRefFlag", new Integer(webRefFlag));
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_COPY_SBMT_QT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_COPY_SBMT_QT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(returnCode);
            }
            
            destQuoteNum = StringUtils.trimToNull(ps.getString(2));
            
        } catch (SQLException e) {
            throw new TopazException(e);
        }
        
        return destQuoteNum;
    }
    
    public void unlockQuote(String webQuoteNum, String creatorId) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piCreatorId", creatorId);
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_UNLOCKQT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_UNLOCKQT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            throw new TopazException(e);
        }
    }
    
    public void updateExpICNCRD(String quoteNum, Date expireDate, Integer reqstICNFlag, 
            Integer reqstPreCreditChkFlag, String userEmailAdr, Date startDate, Date cradDate) throws TopazException {
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", quoteNum);
        java.sql.Date qtExpireDate = expireDate == null ? null : new java.sql.Date(expireDate.getTime());
        params.put("piQuoteExpDate", qtExpireDate);
        params.put("piReqstIbmCustNumFlag", reqstICNFlag);
        params.put("piReqstPreCreditCheckFlag", reqstPreCreditChkFlag);
        params.put("piUserEmailAdr", userEmailAdr);
        java.sql.Date qtStartDate = startDate == null ? null : new java.sql.Date(startDate.getTime());
        params.put("piEffDate", qtStartDate);
        java.sql.Date qtCradDate = cradDate == null ? null : new java.sql.Date(cradDate.getTime());
        params.put("piQuoteCradDate", qtCradDate);
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_EXPRN_ICN_CRD, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_EXPRN_ICN_CRD, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            throw new TopazException(e);
        }
    }
    
    public void updateSapIDocNum(String webQuoteNum, String sapIDocNum, String userEmailAdr, String userAction) throws TopazException {
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piSapIntrmdiatDocNum", sapIDocNum);
        params.put("piUserEmailAdr", userEmailAdr);
        params.put("piUserAction", userAction);
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_IDOC, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_IDOC, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            }
        } catch (SQLException e) {
            throw new TopazException(e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(QuoteHeaderFactory.class, objectId, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        // get instance from cache
        return TransactionContextManager.singleton().getTransactionContext().get(QuoteHeaderFactory.class, objectId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteHeaderFactory#findByWebQuoteNum(java.lang.String)
     */
    public QuoteHeader findByWebQuoteNum(String webQuoteNum) throws TopazException {
        QuoteHeader quoteHeader = null;
        QuoteHeader_jdbc quoteHeader_jdbc = new QuoteHeader_jdbc(null);
        quoteHeader_jdbc.webQuoteNum = webQuoteNum;
        quoteHeader_jdbc.hydrate(TopazUtil.getConnection());
        quoteHeader = quoteHeader_jdbc;
        return quoteHeader;
    }
    
    
    public QuoteHeader findByWebQuoteNumAndUserId(String webQuoteNum, String userId) throws TopazException {
        QuoteHeader quoteHeader = null;
        QuoteHeader_jdbc quoteHeader_jdbc = new QuoteHeader_jdbc(null);
        quoteHeader_jdbc.webQuoteNum = webQuoteNum;
        quoteHeader_jdbc.creatorId = userId;
        quoteHeader_jdbc.hydrate(TopazUtil.getConnection());
        quoteHeader = quoteHeader_jdbc;
        return quoteHeader;
    }
    
    public QuoteHeader getQuoteStage(String webQuoteNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        
        QuoteHeader_jdbc quoteHeader_jdbc = new QuoteHeader_jdbc(null);
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_STAGE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_STAGE, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            boolean retCode = ps.execute();            
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            quoteHeader_jdbc.quoteStageCode = StringUtils.trimToEmpty(ps.getString(3));
            quoteHeader_jdbc.sapIntrmdiatDocNum = StringUtils.trimToEmpty(ps.getString(4));

        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_STAGE, e);
        }
        
        return quoteHeader_jdbc;
    }
    
    public List getDerivedApprvdBids(String webQuoteNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        
        ArrayList quoteHeaders = new ArrayList();
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_DRVD_BID, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_DRVD_BID, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            boolean retCode = ps.execute();            
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            if (retCode) {
                rs = ps.getResultSet();
                
                while (rs.next()) {
                    QuoteHeader_jdbc header = new QuoteHeader_jdbc(null);
                    header.webQuoteNum = StringUtils.trimToEmpty(rs.getString("WEB_QUOTE_NUM"));
                    header.sapIntrmdiatDocNum = StringUtils.trimToEmpty(rs.getString("SAP_INTRMDIAT_DOC_NUM"));
                    header.originalIdocNum = StringUtils.trimToEmpty(rs.getString("ORIG_SAP_IDOC"));
                    header.sapQuoteNum = StringUtils.trimToEmpty(rs.getString("QUOTE_NUM"));
                    header.quoteTypeCode = StringUtils.trimToEmpty(rs.getString("QUOTE_TYPE_CODE"));
                    header.copyFromApprvdBidFlag = (rs.getInt("CPY_FROM_APPRVD_BID_FLAG") == 1);
                    header.addOverallStatus(new CodeDescObj_jdbc(StringUtils.trimToEmpty(rs.getString("OV_STAT_COM")),
                            ""));
                    header.addOverallStatus(new CodeDescObj_jdbc(StringUtils.trimToEmpty(rs.getString("OV_STAT_SB")),
                            ""));
                    header.addOverallStatus(new CodeDescObj_jdbc(StringUtils.trimToEmpty(rs.getString("OV_STAT_SAAS")),
                    ""));                    
                    
                    quoteHeaders.add(header);
                }
                
                rs.close();
            }

        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_DRVD_BID, e);
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
        
        return quoteHeaders;
    }
    

    public void deleteQuoteDeeply(String quoteId)throws TopazException{
    	 HashMap params = new HashMap();
    	 params.put("piQuoteId", quoteId);
    	 params.put("piDeleteRecord", "1");

         QueryContext queryCtx = QueryContext.getInstance();
         String sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.SP_DELETE_QUOTE, null);
         CallableStatement callStmt;
         try {
             callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
             queryCtx.completeStatement(callStmt, NewQuoteDBConstants.SP_DELETE_QUOTE, params);
             logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

             callStmt.execute();
             
             int poGenStatus = callStmt.getInt(1);
             if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS)
                 throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
         } catch (SQLException e) {
             logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
             throw new TopazException("Exception when execute the SP " + NewQuoteDBConstants.SP_DELETE_QUOTE, e);
         }
    }

	public void updateTouFlag(String webQuoteNum, int saasTermCondCatFlag, String touURLs, String termsTypes, String termsSubTypes, 
			String radioFlags, int updateTouFlag, String agreementType) throws TopazException {
		HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        //params.put("stdloneSaasGenTermFlag", stdloneSaasGenTermFlag);
        params.put("touURLs", touURLs);
        params.put("termsTypes", termsTypes);
        params.put("termsSubTypes", termsSubTypes);
        params.put("radioFlags", radioFlags);
        params.put("saasTermCondCatFlag", saasTermCondCatFlag);
        params.put("updateTouFlag", updateTouFlag);
        params.put("agreementType", agreementType);
        
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_TERMS_CONDITIONS, null);
        CallableStatement callStmt;
        ResultSet rs = null;
        try {
            callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_U_QT_TERMS_CONDITIONS, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();
            int poGenStatus = callStmt.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            rs = callStmt.getResultSet();
        } catch (SQLException e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_U_QT_TERMS_CONDITIONS, e);
        } finally{
			try {
				if(null != rs && !rs.isClosed()){
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
	}
	
	public boolean isPlaceHolderTou(String touURL) throws TopazException{
		HashMap params = new HashMap();
        params.put("touURL", touURL);
        ResultSet rs = null;
        int isPlaceHolderFlag=0;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_IS_PLACEHOLDER, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_IS_PLACEHOLDER, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            boolean retCode = callStmt.execute();            
            int poGenStatus = callStmt.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
            	return false;
                //throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

        	isPlaceHolderFlag = callStmt.getInt(2);
        	rs = callStmt.getResultSet();
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_DRVD_BID, e);
        } finally{
			try {
				if(null != rs && !rs.isClosed()){
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
        
        return isPlaceHolderFlag==1;
	}

	@Override
	public void updateWarningTouFlag(String webQuoteNum, String userId, String yesFlags,
			String noFileTous) throws TopazException {
		HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piUserID", userId);
        params.put("yesFlag", yesFlags);
        params.put("touURLs", noFileTous);
       
        ResultSet rs = null;
        
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_BLANK_AMEMDMENT, null);
        CallableStatement callStmt;
        try {
            callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_IU_QT_BLANK_AMEMDMENT, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();
            int poGenStatus = callStmt.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            rs = callStmt.getResultSet();
        } catch (SQLException e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_IU_QT_BLANK_AMEMDMENT, e);
        } finally{
			try {
				if(null != rs && !rs.isClosed()){
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
		
	}

	@Override
	public boolean updateQuoteAgrmtTypeByTou(String webQuoteNum,
			String agrmtTypeCode, String agrmtNum) throws TopazException {
		HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piAgrmtNum", agrmtNum);
        params.put("piAgrmtTypeCode", agrmtTypeCode);
        
        ResultSet rs = null;
        int poGenStatus=0;
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_WEB_AGRMT_TOU, null);
        CallableStatement callStmt;
        try {
            callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_IU_QT_WEB_AGRMT_TOU, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();
             poGenStatus = callStmt.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            rs = callStmt.getResultSet();
        } catch (SQLException e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_IU_QT_WEB_AGRMT_TOU, e);
        } finally{
			try {
				if(null != rs && !rs.isClosed()){
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
       return poGenStatus==0;
	}
	
	public int getCountOfCsaTerms() throws TopazException {
		HashMap<String,Object> params = new HashMap<String,Object>();
        ResultSet rs = null;
        int count=-1;
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_CSA_TERMS_COUNT, null);
        CallableStatement callStmt;
        try {
            callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_GET_CSA_TERMS_COUNT, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();
            count = callStmt.getInt(2);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + count);

            rs = callStmt.getResultSet();
        } catch (SQLException e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_CSA_TERMS_COUNT, e);
        } finally{
			try {
				if(null != rs && !rs.isClosed()){
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
       return count;
	}
	 public void updateQuoteExpirationDateExtension(String webQuoteNum, String userId,Date expireDate,String justification,String updateSavedQuoteFlag) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piUserID", userId);
        java.sql.Date qtExpireDate = expireDate == null ? null : new java.sql.Date(expireDate.getTime());
        parms.put("piQuoteExpDate", qtExpireDate);
        parms.put("piJustificationTxt", justification);
        int savedQuoteFlag=0; 
        if(!StringUtils.isBlank(updateSavedQuoteFlag)){
        	savedQuoteFlag=Integer.valueOf(updateSavedQuoteFlag);
        } 
        parms.put("piUpdateSavedQuoteFlag", savedQuoteFlag);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_EXP_DATE_EXTENSION, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_EXP_DATE_EXTENSION, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(1);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);

            if (returnCode != 0) {
                throw new SPException(returnCode);
            } 
        } catch (SQLException sqle) {
            logContext.error(this, sqle.getMessage());
            throw new TopazException(sqle);
        }
        
    }
	public int getCountOfActivePartB(String webQuoteNum) throws TopazException { //PA/PAE - Check if active Part B exists when the quote have Part A
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("piWebQuoteNum", webQuoteNum);
        ResultSet rs = null;
        int count=-1;
        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_ACTIVE_PART_B_COUNT, null);
        CallableStatement callStmt;
        try {
            callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_ACTIVE_PART_B_COUNT, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();
            count = callStmt.getInt(3);
            logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + count);

            rs = callStmt.getResultSet();
        } catch (SQLException e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_ACTIVE_PART_B_COUNT, e);
        } finally{
			try {
				if(null != rs && !rs.isClosed()){
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
       return count;
	}
	
	
}
