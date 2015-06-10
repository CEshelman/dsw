package com.ibm.dsw.quote.findquote.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.LOBListUtil;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.StatusTrackerContract;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcess;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>StatusTrackerAction</code> class.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class StatusTrackerAction extends FindQuoteAction  {
    private static final String UPDATE_COOKIE = "x";

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        StatusTrackerContract findByIBMerContract = (StatusTrackerContract) contract;
        findByIBMerContract.setPageIndex("1");

        handler.addObject(FindQuoteParamKeys.COOKIE_FLAG, findByIBMerContract.getCf());

        Cookie cookie = findByIBMerContract.getSqoCookie();
        if (StringUtils.equalsIgnoreCase(findByIBMerContract.getCf(), UPDATE_COOKIE)) {
            String[] roles = findByIBMerContract.getOwnerRoles();
            List rolesList = new ArrayList();
            for (int i = roles.length; i > 0; i--) {
                rolesList.add(roles[i - 1]);
            }
            QuoteCookie.setSubmittedOwnerRole(cookie, rolesList);

            String[] quoteTypeFilter = findByIBMerContract.getQuoteTypeFilter();
            List quoteTypeFilterList = new ArrayList();
            for (int i = quoteTypeFilter.length; i > 0; i--) {
                quoteTypeFilterList.add(quoteTypeFilter[i - 1]);
            }
            QuoteCookie.setRenewalOrSales(cookie, quoteTypeFilterList);

            String[] LOBsFilter = findByIBMerContract.getLOBsFilter();
            List LOBsFilterList = new ArrayList();
            for (int i = LOBsFilter.length; i > 0; i--) {
                LOBsFilterList.add(LOBsFilter[i - 1]);
            }
            QuoteCookie.setQuoteType(cookie, LOBsFilterList);

            String[] classificationFilter = findByIBMerContract.getClassificationFilter();
            List classificationFilterList = new ArrayList();
            if(classificationFilter != null){
	            for (int i = classificationFilter.length; i > 0; i--) {
	                classificationFilterList.add(classificationFilter[i - 1]);
	            }
            }
            QuoteCookie.setSearchClsfctnCookieValues(cookie, classificationFilterList);

            QuoteCookie.setSearchAcqstnCookieValue(cookie, findByIBMerContract.getActuationFilter());

            List statusFilterList = new ArrayList();
            for (int i = 0; i < findByIBMerContract.getStatusFilter().length; i++) {
                statusFilterList.add(findByIBMerContract.getStatusFilter()[i]);
            }
            QuoteCookie.setOverallStatus(cookie, statusFilterList);

            QuoteCookie.setSubmittedTime(cookie, (findByIBMerContract.getTimeFilter() != null ? findByIBMerContract
                    .getTimeFilter() : ""));
            QuoteCookie.setStatusSortBy(cookie, findByIBMerContract.getSortFilter());
        }
//----------------
        

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results = null;

        String salesQuoteFlag = getSalesQuoteFlag(findByIBMerContract);

        String quoteType = getQuoteType(findByIBMerContract);

        String overallStatus = getOverallStatus(findByIBMerContract);

        String submittedDays = getSubmittedDays(findByIBMerContract);

        String ecareFlag = getEcareFlag(findByIBMerContract);

        String classification = getClassification(findByIBMerContract);

        String ownerRoles = "";
        
        for (int i = 0; i < findByIBMerContract.getOwnerRoles().length; i++) {
            ownerRoles = ownerRoles + findByIBMerContract.getOwnerRoles()[i]
                    + FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
        }

      
        results = quoteStatusProcess.ibmerStatusTracker(findByIBMerContract.getUserId(), findByIBMerContract
                    .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByIBMerContract.getSortFilter(), findByIBMerContract.getPageIndex(), getPageSize(), ecareFlag,
                    findByIBMerContract.getUserId(),  ownerRoles, classification, findByIBMerContract.getActuationFilter());
        
        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByIBMerContract);
        return handler.getResultBean();

    }

    protected String getPageSize() {
        return "0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_STATUS_TRACKER;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        return "statusTrackerFrm";
    }

    protected ResultBean getPreResultBean(ProcessContract contract, ResultHandler handler) throws ResultBeanException {
        //since this function is in multi-window situation, the deault
        // implementation "getUndoResultBean" can't work well.
        try {
        	QuoteBaseContract quoteBaseContract = (QuoteBaseContract)contract;
            handler.addObject(FindQuoteParamKeys.OVERALL_STATUS_LIST, CacheProcessFactory.singleton().create()
                    .findOverallStatus(quoteBaseContract.getLocale()));
            handler.addObject(FindQuoteParamKeys.LOB_LIST, 
            		LOBListUtil.getLobs(quoteBaseContract.getLocale(),
            				quoteBaseContract.getQuoteUserSession().getAudienceCode()));
        } catch (QuoteException e) {
            throw new RuntimeException(e);
        }

        handler.addObject(ParamKeys.PARAM_LOCAL, ((QuoteBaseContract) contract).getLocale());
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, contract);
        handler.setState(FindQuoteStateKeys.STATE_DISPLAY_STATUS_TRACKER_SETTINGS);
        return handler.getResultBean();
    }
    
    /**
     * @param contract
     * @return
     */
    public String getSubmittedDays(StatusTrackerContract contract) {
        return contract.getTimeFilter();
    }
}
