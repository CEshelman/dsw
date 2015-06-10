package com.ibm.dsw.quote.findquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CountryFactory;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.Invoice_Impl;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.domain.jdbc.Customer_jdbc;
import com.ibm.dsw.quote.common.domain.jdbc.Order_jdbc;
import com.ibm.dsw.quote.common.domain.jdbc.Partner_jdbc;
import com.ibm.dsw.quote.common.domain.jdbc.QuoteHeader_jdbc;
import com.ibm.dsw.quote.common.domain.jdbc.QuoteStatus_jdbc;
import com.ibm.dsw.quote.common.domain.jdbc.Status_jdbc;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteStatusProcess_jdbc.java</code>
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Created on: Apr 28, 2007
 */

public class QuoteStatusProcess_jdbc extends QuoteStatusProcess_Impl {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    private final String poGenStatusKey = "poGenStatus";
    
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.findquote.process.QuoteStatusProcess#findByHDInfo(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SearchResultList findByHDInfo(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatuses, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName, String country,
            String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piUserID", userID);
        params.put("piUserCheckList", employees);
        params.put("piSalesQuoteFlagList", salesQuoteFlag);
        params.put("piQuoteTypeList", quoteType);
        params.put("piOverallStatusesList", overallStatuses);
        params.put("piStartEndDateRange", submittedDays);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);
        params.put("piCustSiteNumber", custSiteNumber);
        params.put("piCustAgreementNumber", custAgreementNumber);
        params.put("piCustName", custName);
        params.put("piCountry", country);
        params.put("piResellerSiteNumber", resellerSiteNumber);
        params.put("piPayerSiteNumber", payerSiteNumber);
        params.put("piResellerName", resellerName);
        params.put("piPayerName", payerName);
        params.put("piNameFlag", nameComparison);
        params.put("piClsfctnList", classification);
        params.put("piAcqrtnCode", actuation);
        params.put("piAudienceCode", audienceCode);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_HDINF;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            //1st cursor: quote_header
            int totalNum = ps.getInt(2);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, userID);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }
    }

    public SearchResultList findByCustomerNum(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatuses, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName, String country,
            String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException {

        return this.findByHDInfo(userID, employees, salesQuoteFlag, quoteType, overallStatuses, submittedDays, sortBy,
                pageIndex, offsetString, ecareFlag, custSiteNumber, custAgreementNumber, custName, country,
                resellerSiteNumber, payerSiteNumber, resellerName, payerName, nameComparison, classification, actuation,infoMap, audienceCode);
    }

    public SearchResultList findByCustomerName(String userID, String employees, String salesQuoteFlag,
            String quoteType, String overallStatuses, String submittedDays, String sortBy, String pageIndex,
            String offsetString, String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName,
            String country, String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException {

        return this.findByHDInfo(userID, employees, salesQuoteFlag, quoteType, overallStatuses, submittedDays, sortBy,
                pageIndex, offsetString, ecareFlag, custSiteNumber, custAgreementNumber, custName, country,
                resellerSiteNumber, payerSiteNumber, resellerName, payerName, nameComparison, classification, actuation,infoMap, audienceCode);

    }

    public SearchResultList findByPartNum(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatuses, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName, String country,
            String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException {

        return this.findByHDInfo(userID, employees, salesQuoteFlag, quoteType, overallStatuses, submittedDays, sortBy,
                pageIndex, offsetString, ecareFlag, custSiteNumber, custAgreementNumber, custName, country,
                resellerSiteNumber, payerSiteNumber, resellerName, payerName, nameComparison, classification, actuation, infoMap, audienceCode);
    }

    public SearchResultList findByPartnerName(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatuses, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName, String country,
            String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException {

        return this.findByHDInfo(userID, employees, salesQuoteFlag, quoteType, overallStatuses, submittedDays, sortBy,
                pageIndex, offsetString, ecareFlag, custSiteNumber, custAgreementNumber, custName, country,
                resellerSiteNumber, payerSiteNumber, resellerName, payerName, nameComparison, classification, actuation, infoMap, audienceCode);
    }

    public SearchResultList findByCountry(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatus, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String country, String state, String classification, String actuation, String subRgnCode, Map infoMap) throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();

        params.put("piUserID", userID);
        params.put("piUserCheckList", employees);
        params.put("piSalesQuoteFlagList", salesQuoteFlag);
        params.put("piQuoteTypeList", quoteType);
        params.put("piOverallStatusesList", overallStatus);
        params.put("piStartEndDateRange", submittedDays);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);
        params.put("piCountry", country);
        params.put("piState", state);
        params.put("piClsfctnList", classification);
        params.put("piAcqrtnCode", actuation);
        params.put("piSubRgnCode",subRgnCode);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_CNTRY;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            int totalNum = ps.getInt(2);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, userID);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }
    }

    public SearchResultList findByAppvlAttr(String userID, String userCheckList, String salesQuoteFlagList,
            String quoteTypeList, String overallStatusesList, String startEndDateRange, String sortBy,
            String pageIndex, String offsetString, String ecareFlag, String spbidReg, String spbidDis,
            String approverGroup, String groupPendingFlag, String groupApprovedDate, String approverType,
            String typePendingFlag, String typeApprovedDate, String clsfctnList, String acqrtnCode, Map infoMap)
            throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();

        params.put("piUserID", userID);
        params.put("piUserCheckList", userCheckList);
        params.put("piSalesQuoteFlagList", salesQuoteFlagList);
        params.put("piQuoteTypeList", quoteTypeList);
        params.put("piOverallStatusesList", overallStatusesList);
        params.put("piStartEndDateRange", startEndDateRange);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));

        params.put("piEcareFlag", ecareFlag);

        params.put("piSpbidReg", spbidReg);
        params.put("piSpbidDis", spbidDis);
        params.put("piApproverGroup", approverGroup);
        params.put("piGroupPendingFlag", groupPendingFlag);

        params.put("piGroupApprovedDate", groupApprovedDate);
        params.put("piApproverType",approverType);
        params.put("piTypePendingFlag", typePendingFlag);

        params.put("piTypeApprovedDate", typeApprovedDate);
        params.put("piClsfctnList", clsfctnList);
        params.put("piAcqrtnCode", acqrtnCode);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_APPVLATTR;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            int totalNum = ps.getInt(2);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, userID);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }
    }

    public SearchResultList findByIBMer(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatus, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String IBMerEmail, String firstName, String lastName, String role, String classification,
            String actuation, Map infoMap) throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piUserID", userID);
        params.put("piUserCheckList", employees);
        params.put("piSalesQuoteFlagList", salesQuoteFlag);
        params.put("piQuoteTypeList", quoteType);
        params.put("piOverallStatusesList", overallStatus);
        params.put("piStartEndDateRange", submittedDays);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);
        params.put("piIBMerEmail", IBMerEmail);
        params.put("piFirstName", firstName);
        params.put("piLastName", lastName);
        params.put("piRoleList", role);
        params.put("piClsfctnList", classification);
        params.put("piAcqrtnCode", actuation);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_IBMER;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            int totalNum = ps.getInt(18);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, userID);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }
    }

    public SearchResultList findByOrderNumber(String orderNumber, String creatorId, String employees, String ecareFlag,
            String pageIndex, String offsetString, Map<String, String> infoMap)
            throws QuoteException {

        int offset = alterOffset(offsetString);

        String sqlQuery = null;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("piNum", orderNumber);
        params.put("piUserID", creatorId);
        params.put("piUserCheckList", employees);
        params.put("piEcareFlag", ecareFlag);
     
        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_ORD_NUM;
        	this.beginTransaction();
        	
            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            //1st cursor: quote_header
            int totalNum = ps.getInt(6);

            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, creatorId);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }

    }
    
    public SearchResultList findBySiebelNumber(String siebelNumber, String creatorId, String employees, String ecareFlag,
            String salesQuoteFlag, String quoteType, String overallStatuses, String submittedDays,  String sortBy, String pageIndex, String offsetString,
            String classification, String actuation, String relatedQuoteFlag, String commonCriteriaFlag, Map<String, String> infoMap)
            throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("piSiebelNum", siebelNumber);
        params.put("piUserID", creatorId);
        params.put("piUserCheckList", employees);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);

        params.put("piSalesQuoteFlagList", salesQuoteFlag);
        params.put("piQuoteTypeList", quoteType);
        params.put("piOverallStatusesList", overallStatuses);
        params.put("piStartEndDateRange", submittedDays);
        params.put("piClsfctnList", classification);
        params.put("piAcqrtnCode", actuation);

        params.put("piRelatedQuoteFlag", relatedQuoteFlag);
        params.put("piCommonCriteriaFlag", commonCriteriaFlag);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_OPP_NUM;
        	this.beginTransaction();
        	
            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            //1st cursor: quote_header
            int totalNum = ps.getInt(9);

            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, creatorId);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }

    }
    
    public SearchResultList findByQuoteNumber(String number, String creatorId, String employees, String ecareFlag,
            String salesQuoteFlag, String quoteType, String overallStatuses, String submittedDays,  String sortBy, String pageIndex, String offsetString,
            String classification, String actuation, String relatedQuoteFlag, String commonCriteriaFlag, Map infoMap)
            throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piNum", number);
        params.put("piUserID", creatorId);
        params.put("piUserCheckList", employees);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);

        params.put("piSalesQuoteFlagList", salesQuoteFlag);
        params.put("piQuoteTypeList", quoteType);
        params.put("piOverallStatusesList", overallStatuses);
        params.put("piStartEndDateRange", submittedDays);
        params.put("piClsfctnList", classification);
        params.put("piAcqrtnCode", actuation);

        params.put("piRelatedQuoteFlag", relatedQuoteFlag);
        params.put("piCommonCriteriaFlag", commonCriteriaFlag);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_STAT_BY_NUM;
        	this.beginTransaction();
        	
            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            infoMap.put(poGenStatusKey,Integer.toString(poGenStatus));
            if (poGenStatus == CommonDBConstants.DB2_SP_OVER_LIMIT_TOTAL_ROWS){
                return new SearchResultList(0, offset, Integer.parseInt(pageIndex));
            }else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            //1st cursor: quote_header
            int totalNum = ps.getInt(9);

            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, creatorId);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }

    }

    /**
     * @param ps
     * @param rs
     * @param list
     * @throws Exception
     * @throws SQLException
     */
    private void assembleQuotes(CallableStatement ps, ResultSet rs, SearchResultList list, String creatorId)
            throws SQLException, Exception {

        //1st cursor: quote_header
        rs = ps.getResultSet();
        list = assembleQuoteHeader(rs, creatorId, list, false);
        for (int i = 5; i > 0; i--) {
            if (ps.getMoreResults()) {
                rs = ps.getResultSet();
                if (rs.next())
                    if (isOrderNumCursor(rs)) {
                        list = assembleOrderNum(rs, list);
                    } else if (isQuoteStatusCursor(rs)) {
                        list = assembleQuoteStatus(rs, list);
                    } else if (isOverallStatusCursor(rs)) {
                        list = assembleOverallStatus(rs, list);
                    } else if (isOrderStatusCursor(rs)) {
                        list = assembleOrderStatus(rs, list);
                    } else if (isPurchaseCursor(rs)){
                    	list = assemblePurchaseNum(rs, list);
                    }else {
                        logContext.error(this, "Result set not correct...");
                        throw new Exception();
                    }
            } else
                return;
        }

    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    private boolean isOrderStatusCursor(ResultSet rs) throws SQLException {
        if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("order_status"))
            return true;
        return false;
    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    private boolean isOverallStatusCursor(ResultSet rs) throws SQLException {
        if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("overall_status"))
            return true;
        return false;
    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    private boolean isQuoteStatusCursor(ResultSet rs) throws SQLException {
        if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("quote_status"))
            return true;
        return false;
    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     * @throws SQLException
     */
    private boolean isOrderNumCursor(ResultSet rs) throws SQLException {
        if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("order_number"))
            return true;

        return false;

    }
    /**
     * @param rs
     * @return
     * @throws SQLException
     * @throws SQLException
     */
    private boolean isPurchaseCursor(ResultSet rs) throws SQLException {
    	if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("purchase_number"))
    		return true;
    	return false;

    }

    /**
     * @param rs
     * @param list
     * @throws Exception
     * @throws SQLException
     */
    private SearchResultList assembleOrderStatus(ResultSet rs, SearchResultList list) throws SQLException, Exception {
        List orderStatusList = null;
        HashMap map = new HashMap();
        Status_jdbc orderStatus = null;

        String webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
        if (!map.containsKey(webQuoteNum)) {
            orderStatusList = new ArrayList();
        } else {
            orderStatusList = (List) map.get(webQuoteNum);
        }
        orderStatus = new Status_jdbc();
        orderStatus.orderNum = StringUtils.trimToEmpty(rs.getString("order_num"));
        orderStatus.statusCode = StringUtils.trimToEmpty(rs.getString("SAP_DOC_STAT"));
        orderStatus.statusCodeDesc = StringUtils.trimToEmpty(rs.getString("stat_dscr"));
        orderStatus.modifiedDate = rs.getTimestamp("mod_date");
        orderStatusList.add(orderStatus);
        map.put(webQuoteNum, orderStatusList);
        while (rs.next()) {
            webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
            if (!map.containsKey(webQuoteNum)) {
                orderStatusList = new ArrayList();
            } else {
                orderStatusList = (List) map.get(webQuoteNum);
            }
            orderStatus = new Status_jdbc();
            orderStatus.orderNum = StringUtils.trimToEmpty(rs.getString("order_num"));
            orderStatus.statusCode = StringUtils.trimToEmpty(rs.getString("SAP_DOC_STAT"));
            orderStatus.statusCodeDesc = StringUtils.trimToEmpty(rs.getString("stat_dscr"));
            orderStatus.modifiedDate = rs.getTimestamp("mod_date");
            orderStatusList.add(orderStatus);
            map.put(webQuoteNum, orderStatusList);
        }
        if (map.size() == 0)
            return list;

        Iterator iter1 = list.getResultList().iterator();
        SearchResultList list2 = list;
        int i = 0;
        while (iter1.hasNext()) {
            Quote quote = (Quote) iter1.next();
            if (quote.getOrders() != null && quote.getOrders().size() > 0) {
                List orderList = new ArrayList();
                Iterator iter2 = quote.getOrders().iterator();
                while (iter2.hasNext()) {
                    Order_jdbc order = (Order_jdbc) iter2.next();
                    List allOrderStatusList = (List) map.get(quote.getQuoteHeader().getWebQuoteNum());
                    if(allOrderStatusList!=null && allOrderStatusList.size()>0){
                    	Iterator iterStatus = allOrderStatusList.iterator();
                    	while (iterStatus.hasNext()) {
                    		Status_jdbc status = (Status_jdbc)iterStatus.next();
                    		if(order.getOrderNumber().equals(status.getOrderNum())){
                    			order.statusList.add(status);
                    		}
                    	}
                    }
                    orderList.add(order);
                }
                quote.setOrders(orderList);
                list2.getResultList().set(i, quote);
            }
            i++;
        }
        return list2;
    }

    /**
     * @param rs
     * @param list
     * @throws Exception
     * @throws SQLException
     */
    private SearchResultList assembleOverallStatus(ResultSet rs, SearchResultList list) throws SQLException, Exception {
        List overallStatusList = null;
        HashMap map = new HashMap();

        String webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
        if (!map.containsKey(webQuoteNum)) {
            overallStatusList = new ArrayList();
        } else {
            overallStatusList = (List) map.get(webQuoteNum);
        }
        overallStatusList.add(new CodeDescObj_jdbc(StringUtils.trimToEmpty(rs.getString("stat")), StringUtils
                .trimToEmpty(rs.getString("stat_dscr"))));
        map.put(webQuoteNum, overallStatusList);
        while (rs.next()) {
            webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
            if (!map.containsKey(webQuoteNum)) {
                overallStatusList = new ArrayList();
            } else {
                overallStatusList = (List) map.get(webQuoteNum);
            }
            overallStatusList.add(new CodeDescObj_jdbc(StringUtils.trimToEmpty(rs.getString("stat")), StringUtils
                    .trimToEmpty(rs.getString("stat_dscr"))));
            map.put(webQuoteNum, overallStatusList);
        }
        if (map.size() == 0)
            return list;

        Iterator iter = list.getResultList().iterator();
        SearchResultList list2 = list;
        int i = 0;
        while (iter.hasNext()) {
            Quote quote = (Quote) iter.next();
            ((QuoteHeader_jdbc) quote.getQuoteHeader()).quoteOverallStatuses = (List) map.get(quote.getQuoteHeader()
                    .getWebQuoteNum());
            list2.getResultList().set(i, quote);
            i++;
        }
        return list2;
    }

    /**
     * @param rs
     * @param list
     * @throws Exception
     * @throws SQLException
     */
    private SearchResultList assembleQuoteStatus(ResultSet rs, SearchResultList list) throws SQLException, Exception {

        QuoteStatus_jdbc status = null;
        HashMap map = new HashMap();
        List statusList = null;

        String webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
        if (!map.containsKey(webQuoteNum)) {
            statusList = new ArrayList();
        } else {
            statusList = (List) map.get(webQuoteNum);
        }
        status = new QuoteStatus_jdbc();
        status.statusCode = StringUtils.trimToEmpty(rs.getString("sap_doc_stat"));
        status.statusCodeDesc = StringUtils.trimToEmpty(rs.getString("stat_dscr"));
        status.modifiedDate = rs.getTimestamp("mod_date");

        if (StringUtils.trimToEmpty(rs.getString("stat_prir")).equalsIgnoreCase("1")) {
            status.statusType = QuoteConstants.QUOTE_STATUS_PRIMARY;
            statusList.add(status);
            map.put(webQuoteNum, statusList);
        } else {
            status.statusType = QuoteConstants.QUOTE_STATUS_SECONDARY;
            statusList.add(status);
            map.put(webQuoteNum, statusList);
        }
        while (rs.next()) {
            webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
            if (!map.containsKey(webQuoteNum)) {
                statusList = new ArrayList();
            } else {
                statusList = (List) map.get(webQuoteNum);
            }
            status = new QuoteStatus_jdbc();
            status.statusCode = StringUtils.trimToEmpty(rs.getString("sap_doc_stat"));
            status.statusCodeDesc = StringUtils.trimToEmpty(rs.getString("stat_dscr"));
            status.modifiedDate = rs.getTimestamp("mod_date");

            if (StringUtils.trimToEmpty(rs.getString("stat_prir")).equalsIgnoreCase("1")) {
                status.statusType = QuoteConstants.QUOTE_STATUS_PRIMARY;
                statusList.add(status);
                map.put(webQuoteNum, statusList);
            } else {
                status.statusType = QuoteConstants.QUOTE_STATUS_SECONDARY;
                statusList.add(status);
                map.put(webQuoteNum, statusList);
            }
        }
        if (map.size() == 0)
            return list;

        Iterator iter = list.getResultList().iterator();
        SearchResultList list2 = list;
        int i = 0;
        while (iter.hasNext()) {
            Quote quote = (Quote) iter.next();
            List statusList2 = (List) map.get(quote.getQuoteHeader().getWebQuoteNum());
            if (statusList2 != null && statusList2.size() >= 0) {
                Iterator statusIter = statusList2.iterator();
                while (statusIter.hasNext()) {
                    status = (QuoteStatus_jdbc) statusIter.next();
                    if (status.getStatusType().equalsIgnoreCase(QuoteConstants.QUOTE_STATUS_PRIMARY))
                        quote.addSapPrimaryStatus(status);
                    else
                        quote.addSapSecondaryStatus(status);
                }
            }
            list2.getResultList().set(i, quote);
            i++;
        }

        return list2;
    }

    /**
     * @param rs
     * @param list
     * @throws Exception
     * @throws SQLException
     */
    private SearchResultList assembleOrderNum(ResultSet rs, SearchResultList list) throws SQLException, Exception {
        Order_jdbc order = null;
        List orderList = null;
        HashMap map = new HashMap();

        String webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
        if (!map.containsKey(webQuoteNum)) {
            orderList = new ArrayList();
        } else {
            orderList = (List) map.get(webQuoteNum);
        }
        order = new Order_jdbc();
        order.orderNumber = StringUtils.trimToEmpty(rs.getString("order_num"));
        order.submittedDate = rs.getTimestamp("order_submitted");
        order.totalPrice = rs.getDouble("order_total");
        order.currencyCode = StringUtils.trimToEmpty(rs.getString("CURRNCY_CODE"));
        order.countryCode = StringUtils.trimToEmpty(rs.getString("CNTRY_CODE"));
        order.orderIdocNum  = StringUtils.trimToEmpty(rs.getString("order_idoc_num"));
        order.orderType  = StringUtils.trimToEmpty(rs.getString("order_type"));
        order.orderSubmitterName = StringUtils.trimToEmpty(rs.getString("ORDER_SUBMITTER_NAME"));
        order.orderSubmitterEmail = StringUtils.trimToEmpty(rs.getString("ORDER_SUBMITTER_EMAIL"));

        orderList.add(order);
        map.put(webQuoteNum, orderList);
        while (rs.next()) {
            webQuoteNum = StringUtils.trimToEmpty(rs.getString("web_quote_num"));
            if (!map.containsKey(webQuoteNum)) {
                orderList = new ArrayList();
            } else {
                orderList = (List) map.get(webQuoteNum);
            }
            order = new Order_jdbc();
            order.orderNumber = StringUtils.trimToEmpty(rs.getString("order_num"));
            order.submittedDate = rs.getTimestamp("order_submitted");
            order.totalPrice = rs.getDouble("order_total");
            order.currencyCode = StringUtils.trimToEmpty(rs.getString("CURRNCY_CODE"));
            order.countryCode = StringUtils.trimToEmpty(rs.getString("CNTRY_CODE"));
            order.orderIdocNum  = StringUtils.trimToEmpty(rs.getString("order_idoc_num"));
            order.orderType  = StringUtils.trimToEmpty(rs.getString("order_type"));
            order.orderSubmitterName = StringUtils.trimToEmpty(rs.getString("ORDER_SUBMITTER_NAME"));
            order.orderSubmitterEmail = StringUtils.trimToEmpty(rs.getString("ORDER_SUBMITTER_EMAIL"));

            orderList.add(order);
            map.put(webQuoteNum, orderList);
        }
        if (map.size() == 0)
            return list;
        Iterator iter = list.getResultList().iterator();
        SearchResultList list2 = list;
        int i = 0;
        while (iter.hasNext()) {
            Quote quote = ((Quote) iter.next());
            quote.setOrders(distinctOrderListByOrderIdocNum((List) map.get(quote.getQuoteHeader().getWebQuoteNum())));
            list2.getResultList().set(i, quote);
            i++;
        }
        return list2;
    }
    /**
     * @param rs
     * @param list
     * @throws Exception
     * @throws SQLException
     */
    private SearchResultList assemblePurchaseNum(ResultSet rs, SearchResultList list) throws SQLException, Exception {
    	Order_jdbc order = null;
    	List orderList = null;
    	List invoiceList = null;
    	HashMap map = new HashMap();

    	order = new Order_jdbc();
    	order.orderNumber = StringUtils.trimToEmpty(rs.getString("order_num"));
    	order.purchaseNum = StringUtils.trimToEmpty(rs.getString("purchase_num"));
    	Invoice_Impl invoice = new Invoice_Impl();
    	invoice.billingOrderNumber = StringUtils.trimToEmpty(rs.getString("billg_order_num"));
    	invoice.invoiceNumber = StringUtils.trimToEmpty(rs.getString("invoice_num"));
    	invoice.invoiceDate = rs.getDate("invoice_date");
    	invoiceList = new ArrayList();
    	invoiceList.add(invoice);
    	order.invoiceList = invoiceList;
    	map.put(order.orderNumber, order);
    	while (rs.next()) {
    		String orderNumber = StringUtils.trimToEmpty(rs.getString("order_num"));
    		if (!map.containsKey(orderNumber)) {
    			order = new Order_jdbc();
        		order.orderNumber = StringUtils.trimToEmpty(rs.getString("order_num"));
        		order.purchaseNum = StringUtils.trimToEmpty(rs.getString("purchase_num"));
        		invoice = new Invoice_Impl();
        		invoice.billingOrderNumber = StringUtils.trimToEmpty(rs.getString("billg_order_num"));
        		invoice.invoiceNumber = StringUtils.trimToEmpty(rs.getString("invoice_num"));
            	invoice.invoiceDate = rs.getDate("invoice_date");
    			invoiceList = new ArrayList();
    	    	invoiceList.add(invoice);
    	    	order.invoiceList = invoiceList;
    	    	map.put(order.orderNumber, order);
            } else {
            	order = (Order_jdbc)map.get(order.orderNumber);
            	invoice = new Invoice_Impl();
            	invoice.billingOrderNumber = StringUtils.trimToEmpty(rs.getString("billg_order_num"));
        		invoice.invoiceNumber = StringUtils.trimToEmpty(rs.getString("invoice_num"));
            	invoice.invoiceDate = rs.getDate("invoice_date");
            	order.invoiceList.add(invoice);
            }
    	}
    	if (map.size() == 0)
    		return list;
    	Iterator iter = list.getResultList().iterator();
    	SearchResultList list2 = list;
    	int i = 0;
    	while (iter.hasNext()) {
    		Quote quote = ((Quote) iter.next());
    		orderList = quote.getOrders();
    		if(orderList != null){
    			for(int j=0;j<orderList.size();j++){
    				String currentOrderNumber = ((Order_jdbc)orderList.get(j)).getOrderNumber();
    				if(currentOrderNumber != null && !currentOrderNumber.equals("")){
    					Object orderObj = map.get(currentOrderNumber);
    					if(null != orderObj && currentOrderNumber.equals(((Order_jdbc)orderObj).getOrderNumber())){
    						Order_jdbc currentOrder = (Order_jdbc)orderList.get(j);
    						currentOrder.purchaseNum = ((Order_jdbc)map.get(currentOrderNumber)).purchaseNum;
    						currentOrder.invoiceList = ((Order_jdbc)map.get(currentOrderNumber)).invoiceList;
    					}
    				}
    			}
    		}
    		list2.getResultList().set(i, quote);
    		i++;
    	}
    	return list2;
    }

    /**
     * @param rs
     * @param creatorId
     * @param resultCount
     * @param offset
     * @param startedIndex
     * @return
     * @throws Exception
     * @throws SQLException
     */
    private SearchResultList assembleQuoteHeader(ResultSet rs, String creatorId, SearchResultList list,
            boolean withApprover) throws SQLException, Exception {
        int i = 0;
        while (rs.next()) {
            if (!StringUtils.trimToEmpty(rs.getString("cur_name")).equals("quote_header")) {
                logContext.error(this, "Cursor of quote_header not correct...");
                throw new Exception();
            }

            Quote quote = null;
            QuoteHeader_jdbc quoteHeader = new QuoteHeader_jdbc(StringUtils.trimToEmpty(rs.getString("CREATOR_ID")));
            quoteHeader.webQuoteNum = StringUtils.trimToEmpty(rs.getString("WEB_QUOTE_NUM"));
            quoteHeader.quoteTypeCode = rs.getString("QUOTE_TYPE_CODE");
            quoteHeader.sapIntrmdiatDocNum = rs.getString("SAP_INTRMDIAT_DOC_NUM");
            quoteHeader.sapQuoteNum = rs.getString("QUOTE_NUM");
            quoteHeader.submittedDate = rs.getTimestamp("SUBMIT_DATE");
            quoteHeader.currencyCode = StringUtils.trimToEmpty(rs.getString("CURRNCY_CODE"));
            quoteHeader.lob = new CodeDescObj_jdbc("", StringUtils.trimToEmpty(rs.getString("LOB_DSCR")));
            quoteHeader.quoteTitle = rs.getString("QUOTE_TITLE");
            quoteHeader.submitterId = StringUtils.trimToEmpty(rs.getString("SUBMITTR_EMAIL"));
            quoteHeader.quotePriceTot = rs.getDouble("QUOTE_PRICE_TOT");
            quoteHeader.country = CountryFactory.singleton().findByCode3(
                    StringUtils.trimToEmpty(rs.getString("CNTRY_CODE")));
            quoteHeader.progMigrationCode = StringUtils.trimToEmpty(rs.getString("PROG_MIGRTN_CODE"));;
            if (withApprover) {
                quoteHeader.approverName = StringUtils.trimToEmpty(rs.getString("approver_name"));
                quoteHeader.approverAssignDate = rs.getTimestamp("ASSIGN_DATE");
                quoteHeader.sbApprovalGroupName = rs.getString("SPECL_BID_APPRVR_GRP_NAME");
            }
           	quoteHeader.contractNum = StringUtils.trimToEmpty(rs.getString("SAP_CTRCT_NUM"));
           	quoteHeader.setSaasFCTToPAQuote(rs.getInt("FCT_TO_PA_MIGRTN_FLAG")==1);
           	quoteHeader.setAgrmtTypeCode(StringUtils.trimToEmpty(rs.getString("AGRMT_TYPE_CODE")));
           	
           	try{
           		quoteHeader.setAudCode(StringUtils.trimToEmpty(rs.getString("AUD_CODE")));
           		quoteHeader.quoteStartDate = rs.getTimestamp("EFF_DATE");
           		quoteHeader.quoteExpDate = rs.getTimestamp("QUOTE_EXP_DATE");
           	}catch(Exception e){
           		
           	}
           	
            quote = new Quote(quoteHeader);
            quote.creatorName = StringUtils.trimToEmpty(rs.getString("CREATEOR_NAME"));
            quote.submitterName = StringUtils.trimToEmpty(rs.getString("SUBMITTER_NAME"));

            Customer_jdbc customer = new Customer_jdbc(StringUtils.trimToEmpty(rs.getString("SOLD_TO_CUST_NUM")));
            customer.custName = rs.getString("SLD_CUST_NAME");
            customer.city = rs.getString("SLD_CITY");
            customer.state = rs.getString("SLD_STATE");
            customer.countryCode = rs.getString("SLD_CNTRY_CODE");
            quote.setCustomer(customer);

            Partner_jdbc reseller = new Partner_jdbc(true);
            reseller.custNum = rs.getString("RSEL_CUST_NUM");
            reseller.custNameFull = rs.getString("RSL_CUST_NAME");
            reseller.city = rs.getString("RSL_CITY");
            reseller.state = rs.getString("RSL_STATE");
            reseller.country = rs.getString("RSL_CNTRY_CODE");
            quote.setReseller(reseller);

            Partner_jdbc distributor = new Partner_jdbc(true);
            distributor.custNum = rs.getString("PAYER_CUST_NUM");
            distributor.custNameFull = rs.getString("PAY_CUST_NAME");
            distributor.city = rs.getString("PAY_CITY");
            distributor.state = rs.getString("PAY_STATE");
            distributor.country = rs.getString("PAY_CNTRY_CODE");
            quote.setPayer(distributor);

            list.add(quote);
        }
        return list;
    }
    
    private SearchResultList assembleEvaluateQuoteList(ResultSet rs, SearchResultList list) 
    			throws SQLException, Exception {
        while (rs.next()) {

            Quote quote = null;
            QuoteHeader_jdbc quoteHeader = new QuoteHeader_jdbc(StringUtils.trimToEmpty(rs.getString("EVALTR_EMAIL_ADR")));
            quoteHeader.webQuoteNum = StringUtils.trimToEmpty(rs.getString("WEB_QUOTE_NUM"));
            quoteHeader.lob = new CodeDescObj_jdbc("", StringUtils.trimToEmpty(rs.getString("LOB_DSCR")));
            quoteHeader.setEvalFullName(rs.getString("FULL_NAME"));
            quoteHeader.setEvalEmailAdr(rs.getString("EVALTR_EMAIL_ADR"));
            quoteHeader.quoteStageCode = rs.getString("QUOTE_STAT");
            quoteHeader.quoteTitle = rs.getString("QUOTE_TITLE");
            quoteHeader.quoteDscr = rs.getString("QUOTE_DSCR");
            quoteHeader.setAgrmtTypeCode(rs.getString("AGRMT_TYPE_CODE")) ;
            
            quote = new Quote(quoteHeader);

            Customer_jdbc customer = new Customer_jdbc(StringUtils.trimToEmpty(rs.getString("CUSTOMER_NUM")));
            customer.custName = rs.getString("CUSTOMER_NAME");
            quote.setCustomer(customer);

            Partner_jdbc reseller = new Partner_jdbc(true);
            reseller.custNum = rs.getString("RSEL_CUST_NUM");
            reseller.custNameFull = rs.getString("RSL_CUST_NAME");
            quote.setReseller(reseller);

            Partner_jdbc distributor = new Partner_jdbc(true);
            distributor.custNum = rs.getString("PAYER_CUST_NUM");
            distributor.custNameFull = rs.getString("PAY_CUST_NAME");
            quote.setPayer(distributor);

            list.add(quote);
        }
        return list;
    }

    /**
     * @param pageIndex
     * @param offset
     * @return
     */
    private String alterStartIndex(String pageIndex, String offset) {
        return String.valueOf((Integer.parseInt(pageIndex) - 1) * Integer.parseInt(offset) + 1);
    }

    /**
     * @param offsetString
     * @return
     */
    private int alterOffset(String offsetString) {
        int offset = Integer.parseInt(offsetString);
        if (offset == 0)
            offset = FindQuoteDBConstants.MAX_FETCH_NUM;
        return offset;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.findquote.process.QuoteStatusProcess#findApprovalQueue(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public SearchResultList findApprovalQueue(String userId, String reportingUsers, String queueType,
            String sortFilter, String pageIndex, String offsetString, String ecareFlag) throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piUserID", userId);
        params.put("piUserCheckList", reportingUsers);
        params.put("piQueueType", queueType);
        params.put("piSortBy", sortFilter);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_APR_QUEUE;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            int totalNum = ps.getInt(9);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                list = assembleQuoteHeader(rs, userId, list, true);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }

    }
    
    public SearchResultList findEvaluatorQueue(String userId, String queueType, String sortFilter, String searchFilter, String searchInfo,
    		String pageIndex, String offsetString) throws QuoteException {
    	int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piUserID", userId);
        params.put("piQueueType", queueType);
        params.put("piSortBy", sortFilter);
        params.put("piSearchBy", searchFilter);
        params.put("piSearchInfo", searchInfo);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_EVAL_QUEUE;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

            int totalNum = ps.getInt(9);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                list = assembleEvaluateQuoteList(rs, list);
                rs.close();
            }

            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    private List distinctOrderListByOrderIdocNum(List orderList){
        List rList = new ArrayList();
        HashMap idocNumKeyMap = new HashMap();
        for(int i = 0; orderList != null && i < orderList.size(); i++){
            Order_jdbc order = (Order_jdbc) orderList.get(i);
            if(idocNumKeyMap.containsKey(order.getOrderIdocNum())) {
                if(StringUtils.isNotBlank(order.orderNumber)){
                    int replacedOrderIndex = Integer.parseInt(String.valueOf(idocNumKeyMap.get(order.getOrderIdocNum())));
                    rList.remove(replacedOrderIndex);
                    rList.add(replacedOrderIndex,order);
                }
            }else{
                idocNumKeyMap.put(order.getOrderIdocNum(), String.valueOf(rList.size()));
                rList.add(order);
            }
        }
        return rList;
    }

    public SearchResultList ibmerStatusTracker (String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatus, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String IBMerEmail, String role, String classification, String actuation) throws QuoteException {

        int offset = alterOffset(offsetString);
        String startIndex = alterStartIndex(pageIndex, offsetString);

        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piUserID", userID);
        params.put("piUserCheckList", employees);
        params.put("piSalesQuoteFlagList", salesQuoteFlag);
        params.put("piQuoteTypeList", quoteType);
        params.put("piOverallStatusesList", overallStatus);
        params.put("piSubmittedDays", submittedDays);
        params.put("piSortBy", sortBy);
        params.put("piStartIndex", startIndex);
        params.put("piOffset", String.valueOf(offset));
        params.put("piEcareFlag", ecareFlag);
        params.put("piIBMerEmail", IBMerEmail);
        params.put("piRoleList", role);
        params.put("piClsfctnList", classification);
        params.put("piAcqrtnCode", actuation);

        try {
        	String spName = FindQuoteDBConstants.SP_S_QT_IBMER_STATUS_TRACKER;
        	this.beginTransaction();

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, params);

            ps.execute();

            int totalNum = ps.getInt(15);
            SearchResultList list = new SearchResultList(totalNum, offset, Integer.parseInt(pageIndex));

            if (totalNum > 0) {
                ResultSet rs = ps.getResultSet();
                assembleQuotes(ps, rs, list, userID);
                rs.close();
            }
            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
        	this.rollbackTransaction();
        }
    }
}
