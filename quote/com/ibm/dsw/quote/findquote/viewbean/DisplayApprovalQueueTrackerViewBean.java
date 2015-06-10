package com.ibm.dsw.quote.findquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.findquote.config.FindQuoteActionKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.ApprovalQueueTrackerContract;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByPartnerViewBean</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-8-10
 */
public class DisplayApprovalQueueTrackerViewBean extends DisplayFindViewBean {
    ApprovalQueueTrackerContract approvalQueuecontract;

    private transient List sortByOptionList;

    private String queueTypeFilter;

    private String updateCriteriaURL;

    protected void collectResultsForFindQuote(Parameters param) {
        approvalQueuecontract = (ApprovalQueueTrackerContract) param
                .getParameter(FindQuoteParamKeys.APPROVAL_QUEUE_TRACKER_CONTRACT);
        findResult = (SearchResultList) param.getParameter(FindQuoteParamKeys.FIND_RESULTS);
        queueTypeFilter = approvalQueuecontract.getQueueType();
        sortByFilter = approvalQueuecontract.getSortFilter();
        countryListObj = (List) param.getParameter(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC);
        //fill nameContainer
        if (findResult != null && findResult.getRealSize() > 0) {
            for (Iterator iter = findResult.getResultList().iterator(); iter.hasNext();) {
                Quote quote = (Quote) iter.next();
                QuoteHeader qh = quote.getQuoteHeader();
                if (qh == null) {
                    continue;
                }

                fillNameContainer(qh.getCreatorId(), quote.getCreatorName());
                fillNameContainer(qh.getSubmitterId(), quote.getSubmitterName());
            }
        }
    }

    public Collection generateSortByOptions() {

        sortByOptionList = new ArrayList();
        //      --0:Approver name
        //		--1:Date submitted
        //		--2:Customer name
        //		--3:Reseller name
        //		--4:Total price
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.SELECT_FOLLOWING), "", this.getSortBy().equalsIgnoreCase("")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.APPROVAL_NAME), "0", this.getSortBy().equalsIgnoreCase("0")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.DATE_SUBMITTED), "1", this.getSortBy().equalsIgnoreCase("1")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.CUSTOMER_NAME), "2", this.getSortBy().equalsIgnoreCase("2")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.RESELLER_NAME), "3", this.getSortBy().equalsIgnoreCase("3")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.TOTAL_PRICE), "4", this.getSortBy().equalsIgnoreCase("4")));

        return sortByOptionList;
    }

    /**
     * @return Returns the queueTypeFilter.
     */
    public String getQueueTypeFilter() {
        return queueTypeFilter;
    }

    public String getSortBy() {
        return sortByFilter;
    }

    public String getViewBeanName() {
        return "DisplayApprovalQueueTrackerViewBean";
    }

    public String getDetailURL() {
        return "quote.wss?jadeAction=" + FindQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB + "&amp;quoteNum=";
    }
    
    public String getApprovalQueueTrackerActionUrl(){
        return HtmlUtil.getURLForAction(FindQuoteActionKeys.APPROVAL_QUEUE_TRACKER);
    }
    
    public int getReloadInterval(){
        int interval = 0;
        try{
           interval = CacheProcessFactory.singleton().create().getApprovalQueueTrackerRefreshInterval();
        } catch (QuoteException e) {
            LogContextFactory.singleton().getLogContext().error(this, e.getMessage());
        }
        return interval;
    }
    
    public String getDateStr(){
        return DateHelper.formatToLocalTime(new Date(), "dd MMM yyyy HH:mm:ss z", this.getDisplayTimeZone(), approvalQueuecontract.getLocale());
    }
}
