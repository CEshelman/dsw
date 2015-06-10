package com.ibm.dsw.quote.findquote.process;

import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteStatusProcess.java</code>
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on: Apr 27, 2007
 */

public interface QuoteStatusProcess {

    //List contains Quote object
    public SearchResultList findByQuoteNumber(String number, String creatorId, String reportingUsers, String ecareFlag,
            String salesQuoteFlag, String quoteType, String overallStatuses, String submittedDays,  String sortBy, String pageIndex, String offsetString,
            String classification, String actuation, String relatedQuoteFlag, String commonCriteriaFlag, Map infoMap)
            throws QuoteException;
    
    public SearchResultList findByOrderNumber(String number, String creatorId, String employees, String ecareFlag,
            String pageIndex, String offsetString, Map<String, String> infoMap)
            throws QuoteException;
    
    public SearchResultList findBySiebelNumber(String siebelNumber, String creatorId, String employees, String ecareFlag,
            String salesQuoteFlag, String quoteType, String overallStatuses, String submittedDays,  String sortBy, String pageIndex, String offsetString,
            String classification, String actuation, String relatedQuoteFlag, String commonCriteriaFlag, Map<String, String> infoMap)
            throws QuoteException;
    
    public SearchResultList findByHDInfo(String userID, String reportingUsers, String salesQuoteFlag, String quoteType,
            String overallStatuses, String submittedDays, String sortBy, String pageIndex, String offset,
            String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName, String country,
            String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException;

    /**
     * Please use findByHDInfo(String userID, String salesQuoteFlag, String
     * quoteType, String submittedDays, String sortBy, String pageIndex, String
     * offset, String ecareFlag, String custSiteNumber, String
     * custAgreementNumber, String custName, String country, String
     * resellerSiteNumber, String payerSiteNumber, String resellerName, String
     * payerName) instead.
     */
    public SearchResultList findByCustomerNum(String userID, String reportingUsers, String salesQuoteFlag,
            String quoteType, String overallStatuses, String submittedDays, String sortBy, String pageIndex,
            String offset, String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName,
            String country, String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException;

    /**
     * Please use findByHDInfo(String userID, String salesQuoteFlag, String
     * quoteType, String submittedDays, String sortBy, String pageIndex, String
     * offset, String ecareFlag, String custSiteNumber, String
     * custAgreementNumber, String custName, String country, String
     * resellerSiteNumber, String payerSiteNumber, String resellerName, String
     * payerName) instead.
     */
    public SearchResultList findByCustomerName(String userID, String reportingUsers, String salesQuoteFlag,
            String quoteType, String overallStatuses, String submittedDays, String sortBy, String pageIndex,
            String offset, String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName,
            String country, String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException;

    /**
     * Please use findByHDInfo(String userID, String salesQuoteFlag, String
     * quoteType, String submittedDays, String sortBy, String pageIndex, String
     * offset, String ecareFlag, String custSiteNumber, String
     * custAgreementNumber, String custName, String country, String
     * resellerSiteNumber, String payerSiteNumber, String resellerName, String
     * payerName) instead.
     */
    public SearchResultList findByPartNum(String userID, String reportingUsers, String salesQuoteFlag,
            String quoteType, String overallStatuses, String submittedDays, String sortBy, String pageIndex,
            String offset, String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName,
            String country, String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException;

    /**
     * Please use findByHDInfo(String userID, String salesQuoteFlag, String
     * quoteType, String submittedDays, String sortBy, String pageIndex, String
     * offset, String ecareFlag, String custSiteNumber, String
     * custAgreementNumber, String custName, String country, String
     * resellerSiteNumber, String payerSiteNumber, String resellerName, String
     * payerName) instead.
     */
    public SearchResultList findByPartnerName(String userID, String reportingUsers, String salesQuoteFlag,
            String quoteType, String overallStatuses, String submittedDays, String sortBy, String pageIndex,
            String offset, String ecareFlag, String custSiteNumber, String custAgreementNumber, String custName,
            String country, String resellerSiteNumber, String payerSiteNumber, String resellerName, String payerName,
            String nameComparison, String classification, String actuation, Map infoMap, String audienceCode) throws QuoteException;

    public SearchResultList findByCountry(String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatus, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String country, String state, String classification, String actuation, String subRgnCode, Map infoMap) throws QuoteException;
    
    public SearchResultList findByAppvlAttr(String userID, String userCheckList, String salesQuoteFlagList,
            String quoteTypeList, String overallStatusesList, String startEndDateRange, String sortBy,
            String pageIndex, String offsetString, String ecareFlag, String spbidReg, String spbidDis,
            String approverGroup, String groupPendingFlag, String groupApprovedDate, String approverType,
            String typePendingFlag, String typeApprovedDate, String clsfctnList, String acqrtnCode, Map infoMap)throws QuoteException;

    public SearchResultList findByIBMer(String userID, String reportingUsers, String salesQuoteFlag, String quoteType,
            String overallStatus, String submittedDays, String sortBy, String pageIndex, String offset,
            String ecareFlag, String IBMerEmail, String firstName, String lastName, String role, String classification, 
            String actuation, Map infoMap) throws QuoteException;


    /**
     * @param userId
     * @param sortFilter
     * @param queueType
     */
    public SearchResultList findApprovalQueue(String userId, String reportingUsers, String queueType,
            String sortFilter, String pageIndex, String offset, String ecareFlag) throws QuoteException;
    
    public SearchResultList findEvaluatorQueue(String userId, String queueType, String sortFilter, String searchFilter, String searchInfo,
    		String pageIndex, String offsetString) throws QuoteException;
    
    public SearchResultList ibmerStatusTracker (String userID, String employees, String salesQuoteFlag, String quoteType,
            String overallStatus, String submittedDays, String sortBy, String pageIndex, String offsetString,
            String ecareFlag, String IBMerEmail, String role, String classification, String actuation) throws QuoteException;
}
