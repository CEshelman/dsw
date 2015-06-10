package com.ibm.dsw.quote.findquote.contract;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class FindQuoteContract extends QuoteBaseCookieContract {

    String[] quoteTypeFilter;

    String[] LOBsFilter;

    String[] statusFilter;

    String timeFilter;

    String sortFilter;

    String markFilterDefault;

    String pageIndex;

    String getFlag;

    String reportingSalesReps = "";
    
    String[] classificationFilter;
    
    String actuationFilter;
    
    String[] timeFilterOptions;
    
    boolean PGSFlag;
    
    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        this.setGetFlag(parameters.getParameterAsString(FindQuoteParamKeys.PARAM_POST_FLAG));

        if (this.getFlag != null) {
            String quoteTypeFilterString = parameters.getParameterAsString(FindQuoteParamKeys.QUOTE_TYPE_FILTER);
            if (StringUtils.isNotBlank(quoteTypeFilterString)) {
                quoteTypeFilter = quoteTypeFilterString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN);
            }
            String LOBsFilterString = parameters.getParameterAsString(FindQuoteParamKeys.LOBS_FILTER);
            if (StringUtils.isNotBlank(LOBsFilterString)) {
                LOBsFilter = LOBsFilterString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN);
            }
            String statusFilterString = parameters.getParameterAsString(FindQuoteParamKeys.STATUS_FILTER);
            if (StringUtils.isNotBlank(statusFilterString)) {
                statusFilter = statusFilterString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN);
            }
            timeFilter = parameters.getParameterAsString(FindQuoteParamKeys.TIME_FILTER);

            String classificationFilterString = parameters.getParameterAsString(FindQuoteParamKeys.CLASSIFICATION_FILTER);
            if (StringUtils.isNotBlank(classificationFilterString)) {
                classificationFilter = classificationFilterString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN);
            }
            
            if (this.getTimeFilter() != null) {
                if (this.getTimeFilter().equals("Quarter")) {
                    String timeFilterOptionKey = FindQuoteParamKeys.TIME_FILTER + getTimeFilter() + "Options";
                    String timeFilterOptionsString = parameters.getParameterAsString(timeFilterOptionKey);
                    this.setTimeFilterOptions(timeFilterOptionsString.split(","));
                }
                else if (this.getTimeFilter().equals("Month")) {
                    String timeFilterOptionKey = FindQuoteParamKeys.TIME_FILTER + getTimeFilter() + "Options";
                    String timeFilterOptionsString = parameters.getParameterAsString(timeFilterOptionKey);
                    this.setTimeFilterOptions(timeFilterOptionsString.split(","));
                }
            }
            
        } else {
            this.setQuoteTypeFilter(parameters.getParameterWithMultiValues(FindQuoteParamKeys.QUOTE_TYPE_FILTER));
            this.setLOBsFilter(parameters.getParameterWithMultiValues(FindQuoteParamKeys.LOBS_FILTER));
            this.setStatusFilter(parameters.getParameterWithMultiValues(FindQuoteParamKeys.STATUS_FILTER));
            this.setMarkFilterDefault(parameters.getParameterAsString(FindQuoteParamKeys.MARK_FILTER_DEFAULT));
            this.setTimeFilter(parameters.getParameterAsString(FindQuoteParamKeys.TIME_FILTER));
            this.setClassificationFilter(parameters.getParameterWithMultiValues(FindQuoteParamKeys.CLASSIFICATION_FILTER));
            
            if (this.getTimeFilter() != null) {
                if ((this.getTimeFilter().equals("Quarter") || this.getTimeFilter().equals("Month"))) {
                    String timeFilterOptionKey = FindQuoteParamKeys.TIME_FILTER + getTimeFilter() + "Options";
                    String[] timeOptions = parameters.getParameterWithMultiValues(timeFilterOptionKey);
                    this.setTimeFilterOptions(timeOptions);
                }
            }
        }
        
        QuoteUserSession quoteUserSession = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
        if (null != quoteUserSession) {
            this.reportingSalesReps = quoteUserSession.getUp2ReportingUserIds();
        }
        this.setSortFilter(parameters.getParameterAsString(FindQuoteParamKeys.SORT_FILTER));
        this.setPageIndex(parameters.getParameterAsString(FindQuoteParamKeys.PARAM_PAGE_INDEX));
        this.setActuationFilter(parameters.getParameterAsString(FindQuoteParamKeys.ACTUATION_FILTER));
        
        PGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(getQuoteUserSession().getAudienceCode());

    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        List quteTypes = QuoteCookie.getRenewalOrSaleses(sqoCookie);
        quoteTypeFilter = new String[quteTypes.size()];
        for (int i = 0; i < quteTypes.size(); i++) {
            quoteTypeFilter[i] = (String) quteTypes.get(i);
        }

        List LOBsFolters = QuoteCookie.getQuoteTypes(sqoCookie);
        LOBsFilter = new String[LOBsFolters.size()];

        for (int i = 0; i < LOBsFolters.size(); i++) {
            LOBsFilter[i] = (String) LOBsFolters.get(i);
        }

        List statusFilters = QuoteCookie.getOverallStatuses(sqoCookie);
        statusFilter = new String[statusFilters.size()];

        for (int i = 0; i < statusFilters.size(); i++) {
            //QSxxx
            statusFilter[i] = (String) statusFilters.get(i);
        }

        timeFilter = QuoteCookie.getSubmittedTime(sqoCookie);

        this.setSortFilter(QuoteCookie.getStatusSortBy(sqoCookie));
        
        List classificationFoltersList = QuoteCookie.getSearchClsfctnCookieValue(sqoCookie);
        classificationFilter = new String[classificationFoltersList.size()];

        for (int i = 0; i < classificationFoltersList.size(); i++) {
            classificationFilter[i] = (String) classificationFoltersList.get(i);
        }

        this.setActuationFilter(QuoteCookie.getSearchAcqstnCookieValue(sqoCookie));

        String timeOptions = QuoteCookie.getSubmittedTimeOptions(sqoCookie);
        
        if(StringUtils.isNotBlank(timeOptions)){
            this.setTimeFilterOptions(timeOptions.split(" "));
        }
    }

    /**
     * @return Returns the lOBsFilter.
     */
    public String[] getLOBsFilter() {
        return LOBsFilter;
    }

    /**
     * @param bsFilter
     *            The lOBsFilter to set.
     */
    public void setLOBsFilter(String[] bsFilter) {
        LOBsFilter = bsFilter;
    }

    /**
     * @return Returns the markFilterDefault.
     */
    public String getMarkFilterDefault() {
        return markFilterDefault;
    }

    /**
     * @param markFilterDefault
     *            The markFilterDefault to set.
     */
    public void setMarkFilterDefault(String markFilterDefault) {
        this.markFilterDefault = markFilterDefault;
    }

    /**
     * @return Returns the quoteTypeFilter.
     */
    public String[] getQuoteTypeFilter() {
        return quoteTypeFilter;
    }

    /**
     * @param quoteTypeFilter
     *            The quoteTypeFilter to set.
     */
    public void setQuoteTypeFilter(String[] quoteTypeFilter) {
        this.quoteTypeFilter = quoteTypeFilter;
    }

    /**
     * @return Returns the sortFilter.
     */
    public String getSortFilter() {
        return notNullString(sortFilter);
    }

    /**
     * @param sortFilter
     *            The sortFilter to set.
     */
    public void setSortFilter(String sortFilter) {
        this.sortFilter = sortFilter;
    }

    /**
     * @return Returns the statusFilter.
     */
    public String[] getStatusFilter() {
        return statusFilter;
    }

    /**
     * @param statusFilter
     *            The statusFilter to set.
     */
    public void setStatusFilter(String[] statusFilter) {
        this.statusFilter = statusFilter;
    }

    /**
     * @return Returns the timeFilter.
     */
//    public String[] getTimeFilter() {
//        return timeFilter;
//    }
    public String getTimeFilter() {
        return timeFilter;
    }

    /**
     * @param timeFilter
     *            The timeFilter to set.
     */
    public void setTimeFilter(String timeFilter) {
        this.timeFilter = timeFilter;
    }

    /**
     * @return Returns the pageIndex.
     */
    public String getPageIndex() {
        if (pageIndex == null || pageIndex.equals(""))
            pageIndex = "1";
        return pageIndex;
    }

    /**
     * @param pageIndex
     *            The pageIndex to set.
     */
    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String notNullString(String str) {
        return null != str ? str.trim() : "";
    }

    /**
     * @return Returns the getFlag.
     */
    public String getGetFlag() {
        return getFlag;
    }

    /**
     * @param getFlag
     *            The getFlag to set.
     */
    public void setGetFlag(String getFlag) {
        this.getFlag = getFlag;
    }

    /**
     * @return Returns the reportingSalesReps.
     */
    public String getReportingSalesReps() {
        return reportingSalesReps;
    }

    /**
     * @param reportingSalesReps
     *            The reportingSalesReps to set.
     */
    public void setReportingSalesReps(String reportingSalesReps) {
        this.reportingSalesReps = reportingSalesReps;
    }
    /**
     * @return Returns the actuationFilter.
     */
    public String getActuationFilter() {
        return actuationFilter;
    }
    /**
     * @param actuationFilter The actuationFilter to set.
     */
    public void setActuationFilter(String actuationFilter) {
        this.actuationFilter = actuationFilter;
    }
    /**
     * @return Returns the classificationFilter.
     */
    public String[] getClassificationFilter() {
        return classificationFilter;
    }
    /**
     * @param classificationFilter The classificationFilter to set.
     */
    public void setClassificationFilter(String[] classificationFilter) {
        this.classificationFilter = classificationFilter;
    }
    
    
    /**
     * @return Returns the timeFilterOptions.
     */
    public String[] getTimeFilterOptions() {
        return timeFilterOptions;
    }
    /**
     * @param timeFilterOptions The timeFilterOptions to set.
     */
    public void setTimeFilterOptions(String[] timeFilterOptions) {
        this.timeFilterOptions = timeFilterOptions;
    }

	public boolean isPGSFlag() {
		return PGSFlag;
	}
    
}
