package com.ibm.dsw.quote.findquote.contract;

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
 * The <code>DisplayApprovalQueueContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-8-10
 */
public class DisplayApprovalQueueContract extends QuoteBaseCookieContract {
    String queueType;

    String sortFilter;

    String markFilterDefault;

    String pageIndex;

    String reportingSalesReps = "";

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        setQueueType(QuoteCookie.getAprQueueType(sqoCookie));
        if (queueType == null || queueType.equals(""))
            if (this.isUserApprover())
                this.queueType = "1";
            else
                this.queueType = "0";
        setSortFilter(QuoteCookie.getAprQueueSortFilter(sqoCookie));
        if (sortFilter == null || sortFilter.equals(""))
            if (this.isUserApprover())
                this.sortFilter = "1";
            else
                this.sortFilter = "0";
        QuoteUserSession quoteUserSession = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
        if (null != quoteUserSession) {
            this.reportingSalesReps = quoteUserSession.getUp2ReportingUserIds();
        }
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        setQueueType(parameters.getParameterAsString(FindQuoteParamKeys.QUEUE_TYPE));
        setSortFilter(parameters.getParameterAsString(FindQuoteParamKeys.SORT_FILTER));
        setMarkFilterDefault(parameters.getParameterAsString(FindQuoteParamKeys.MARK_FILTER_DEFAULT));
        setPageIndex(parameters.getParameterAsString(FindQuoteParamKeys.PARAM_PAGE_INDEX));

        QuoteUserSession quoteUserSession = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
        if (null != quoteUserSession) {
            this.reportingSalesReps = quoteUserSession.getUp2ReportingUserIds();
        }
    }

    /**
     * @return Returns the queueType.
     */
    public String getQueueType() {
        return queueType;
    }

    /**
     * @param queueType
     *            The queueType to set.
     */
    public void setQueueType(String queueType) {
        this.queueType = queueType;
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

    /**
     * @return Returns the sortFilter.
     */
    public String getSortFilter() {
        return sortFilter;
    }

    /**
     * @param sortFilter
     *            The sortFilter to set.
     */
    public void setSortFilter(String sortFilter) {
        this.sortFilter = sortFilter;
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

    public boolean isUserApprover() {
        return getUser().getAccessLevel("SQO") == 5;
    }

}
