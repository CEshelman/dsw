package com.ibm.dsw.quote.findquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteActionKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayEvaluatorQueueContract;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;

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
public class DisplayEvaluatorQueueViewBean extends DisplayFindViewBean {
    DisplayEvaluatorQueueContract evalQuoteContract;
    transient List evalQuotesList;

    private transient List<SelectOptionImpl> sortByOptionList;

    private String queueTypeFilter;

    private String updateCriteriaURL;
    
    private String sWebQuoteNum ;

    private String sCustName;

    private int iNumOfParts = 0;

    private String sQuoteTypeCode;
    
    private String myDraftQuoteURL;
    
    private boolean isDisplayRighColumn = false;
    
    private String searchTypeFilter;
    
    private String searchInfo;
    
    protected void collectResultsForFindQuote(Parameters param) {
    	QuoteRightColumn quoteRightColumn = (QuoteRightColumn)param.getParameter(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN);
        if (quoteRightColumn != null){
            isDisplayRighColumn = true;
            sWebQuoteNum = quoteRightColumn.getSWebQuoteNum();
            sCustName = quoteRightColumn.getSCustName();
            iNumOfParts = quoteRightColumn.getINumOfParts();
            sQuoteTypeCode = quoteRightColumn.getSQuoteTypeCode();
            myDraftQuoteURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CURRENT_DRAFT_QUOTE);
        }
    	
    	evalQuoteContract = (DisplayEvaluatorQueueContract) param
                .getParameter(FindQuoteParamKeys.DISPLAY_EVALUATOR_QUEUE_CONTRACT);
        findResult = (SearchResultList) param.getParameter(FindQuoteParamKeys.FIND_RESULTS);
        evalQuotesList = findResult.getResultList();
        queueTypeFilter = evalQuoteContract.getQueueType();
        sortByFilter = evalQuoteContract.getSortFilter();
        searchTypeFilter = evalQuoteContract.getSearchType();
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

    public Collection<SelectOptionImpl> generateSortByOptions() {

        sortByOptionList = new ArrayList<SelectOptionImpl>();
        //      --0:Approver name
        //		--1:Date submitted
        //		--2:Customer name
        //		--3:Reseller name
        //		--4:Total price
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.SELECT_FOLLOWING), "", this.getSortBy().equalsIgnoreCase("")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.EVAL_CUST_NAME), "0", this.getSortBy().equalsIgnoreCase("0")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.EVAL_QUOTE_EVALOR), "1", this.getSortBy().equalsIgnoreCase("1")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.EVAL_QUOTE_TYPE), "2", this.getSortBy().equalsIgnoreCase("2")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.EVAL_CUST_SITE_NUM), "3", this.getSortBy().equalsIgnoreCase("3")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
        		FindQuoteParamKeys.EVAL_STATUS), "4", this.getSortBy().equalsIgnoreCase("4")));

        return sortByOptionList;
    }

    public String getUpdateCriteriaURL() {
        String criteriaURL = HtmlUtil.getURLForAction("LOAD_APPROVAL_QUEUE");
        criteriaURL += "&" + FindQuoteParamKeys.QUEUE_TYPE + "=" + evalQuoteContract.getQueueType();
        String markFilterDefault = evalQuoteContract.getMarkFilterDefault();
        if (markFilterDefault != null) {
            criteriaURL += "&" + FindQuoteParamKeys.MARK_FILTER_DEFAULT + "=";
            criteriaURL += markFilterDefault;
        }
        criteriaURL += "&" + FindQuoteParamKeys.SORT_FILTER + "=" + this.getSortBy();
        criteriaURL += "&" + FindQuoteParamKeys.SEARCH_BY_TYPE + "=" + this.getSearchTypeFilter();
        return criteriaURL;
    }

    public String getPrePageURL() {
        String prePageURL = "";
        prePageURL += getUpdateCriteriaURL();
        prePageURL += "&" + FindQuoteParamKeys.PARAM_PAGE_INDEX + "=" + this.getFindResult().getPre();
        return prePageURL;
    }

    public String getNextPageURL() {
        String nextPageURL = "";
        nextPageURL += getUpdateCriteriaURL();
        nextPageURL += "&" + FindQuoteParamKeys.PARAM_PAGE_INDEX + "=" + this.getFindResult().getNext();
        return nextPageURL;
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
        return "DisplayEvaluatorQueueViewBean";
    }

    public String getDetailURL() {
        return "quote.wss?jadeAction=" + FindQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB + "&amp;quoteNum=";
    }

	public List getEvalQuotesList() {
		return this.evalQuotesList;
	}

	public DisplayEvaluatorQueueContract getEvalQuoteContract() {
		return evalQuoteContract;
	}

	public String getSWebQuoteNum() {
		return sWebQuoteNum;
	}

	public String getSCustName() {
		return sCustName;
	}

	public int getINumOfParts() {
		return iNumOfParts;
	}

	public String getSQuoteTypeCode() {
		return sQuoteTypeCode;
	}

	public String getMyDraftQuoteURL() {
		return myDraftQuoteURL;
	}

	public boolean isDisplayRighColumn() {
		return isDisplayRighColumn;
	}

	public void setDisplayRighColumn(boolean isDisplayRighColumn) {
		this.isDisplayRighColumn = isDisplayRighColumn;
	}
	
	 /**
     * @return Returns the searchByTypeFilter.
     */
    public String getSearchTypeFilter() {
        return searchTypeFilter;
    }
    
    /**
     * @return Returns the searchInfo.
     */
    public String getSearchInfo() {
    	return ((DisplayEvaluatorQueueContract) evalQuoteContract).getSearchInfo();
    }

	public void setSearchInfo(String searchInfo) {
		this.searchInfo = searchInfo;
	}

}
