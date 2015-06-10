package com.ibm.dsw.quote.findquote.viewbean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.Order;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteActionKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteContract;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindViewBean</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindViewBean extends BaseViewBean {

    FindQuoteContract findContract;

    SearchResultList findResult;

    transient List overallStatus;

    transient List lobList;

    transient List countryListObj;

    protected transient List spDistricts;

    protected transient List spRigions;

    protected transient List spGroups;

    protected transient List spTypes;
    
    protected transient List subRegions;

    transient List stateList;

    String countryname;
    
    String subRegionName;

    String statename;

    String sortByFilter;

    private String cf;

    private transient List sortByOptionList;

    private String page_previous;

    private String page_next;

    private String changeCriteriaURL;

    protected transient Map nameContainer = new HashMap();

    transient List classificationList;

    transient List acquisitionList;

    String commonCriteriaFlag;

    String relatedQuoteFlag;

    transient private List quarterOptionList;

    private transient List monthOptionList;
    
    private int quarterOptionRange = 16;
    
    private int monthOptionRange = 48;
    
    String poGenStatus;
    
    String codeDesc;

    
    public boolean isPGSFlag() {
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
	}

	protected transient CodeDescObj quoteStatuseEeqs = new CodeDescObj_jdbc(FindQuoteParamKeys.EEQS, getI18NString(
            I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.QUOTE_STATUSE_EEQS));
    
    protected transient static final LogContext logger = LogContextFactory.singleton().getLogContext();

    protected void fillNameContainer(String id, String name) {
        if (StringUtils.isNotBlank(id) && nameContainer.get(id) == null) {
            if (StringUtils.isBlank(name)) {
                BluePageUser bpu = getBluePageUser(id);
                if (bpu == null) {
                    nameContainer.put(id, " (" + id + ")");
                } else {
                    nameContainer.put(id, bpu.getFirstName() + " " + bpu.getLastName() + " (" + id + ")");
                }
            } else {
                nameContainer.put(id, name + " (" + id + ")");
            }
        }
    }

    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);
        collectResultsForFindQuote(param);
    }

    /**
     * @param param
     */
    protected void collectResultsForFindQuote(Parameters param) {
        findContract = (FindQuoteContract) param.getParameter(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT);
        findResult = (SearchResultList) param.getParameter(FindQuoteParamKeys.FIND_RESULTS);

        //fill nameContainer
        if (findResult != null) {
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
        overallStatus= (List) param.getParameter(FindQuoteParamKeys.OVERALL_STATUS_LIST);
        /*if (isPGSFlag()) {
        	int index = 0;
        	Iterator iterator = overallStatus.iterator();
        	
        	while(iterator.hasNext()){
            	CodeDescObj codeDescObj = (CodeDescObj)iterator.next();
        		if (codeDescObj.getCode().equalsIgnoreCase("QS010")) {
        			break;
        		}
            	index++;
            }
            overallStatus.remove(index);
        }*/
        
        subRegions = (List) param.getParameter(FindQuoteParamKeys.SUB_REGION_LIST);
        
        lobList = (List) param.getParameter(FindQuoteParamKeys.LOB_LIST);
        stateList = (List) param.getParameter(FindQuoteParamKeys.PARAM_STATE_LIST);
        countryname = param.getParameterAsString(FindQuoteParamKeys.COUNTRY_NAME);
        subRegionName = param.getParameterAsString(FindQuoteParamKeys.SUB_REGION_NAME);
        statename = param.getParameterAsString(FindQuoteParamKeys.STATE_NAME);
        spDistricts = (List) param.getParameter(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS);
        spRigions = (List) param.getParameter(FindQuoteParamKeys.PARAM_SP_BID_REGIONS);
        spGroups = (List) param.getParameter(FindQuoteParamKeys.PARAM_SP_BID_GROUPS);
        spTypes = (List) param.getParameter(FindQuoteParamKeys.PARAM_SP_BID_TYPES);
        countryListObj = (List) param.getParameter(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC);
        cf = param.getParameterAsString(FindQuoteParamKeys.COOKIE_FLAG);
        acquisitionList = (List) param.getParameter(FindQuoteParamKeys.ACTUATION_LIST);

        List cacheClassificationList = (List) param.getParameter(FindQuoteParamKeys.CLASSIFICATION_LIST);

        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String notClassified = context.getI18nValueAsString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteMessageKeys.NOT_CLASSIFIED);
        CodeDescObj notClassifiedObj = new CodeDescObj_jdbc(QuoteConstants.NA_CODE_VALUE, notClassified);
        classificationList = new ArrayList();
        if (cacheClassificationList != null) {
            classificationList.addAll(cacheClassificationList);
            classificationList.add(notClassifiedObj);
        }
        poGenStatus = (String)param.getParameter(FindQuoteParamKeys.PO_GEN_STATUS);
    }

    public String getViewBeanName() {
        return "";
    }

    public String getCountryName() {
        return countryname;
    }

    public String getSubRegionName(){
        return subRegionName;
    }
    
    /**
     * @return Returns the statename.
     */
    public String getStateName() {
        return statename;
    }

    public BluePageUser getBluePageUser(String internetID) {
        return BluePagesLookup.getBluePagesInfo(internetID.trim());
    }

    /**
     * @param codeDescOjb
     * @return
     */
    public boolean isSelectionDefault(String[] array, CodeDescObj codeDescOjb) {
        if (array == null)
            return false;

        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(codeDescOjb.getCode()))
                return true;
        }
        return false;
    }

    /**
     * @return Returns the findResult.
     */
    public SearchResultList getFindResult() {
        return findResult;
    }

    public String[] getQuoteTypeFilter() {
        return findContract.getQuoteTypeFilter();
    }

    public String[] getQuoteTypeFilterChecked() {
        String[] quoteTypeFilterChecked = new String[2];
        for (int i = 0; i < 2; i++) {
            quoteTypeFilterChecked[i] = "";
        }
        if (this.getQuoteTypeFilter() != null) {
            for (int i = 0; i < this.getQuoteTypeFilter().length; i++) {
                if (this.getQuoteTypeFilter()[i].equalsIgnoreCase("RNWLQUOTE"))
                    quoteTypeFilterChecked[0] = "1";
                else if (this.getQuoteTypeFilter()[i].equalsIgnoreCase("SLSQUOTE"))
                    quoteTypeFilterChecked[1] = "1";
            }
        }
        return quoteTypeFilterChecked;
    }

    public String[] getQuoteTypeFilterNames() {
        String[] quoteTypeFilterNames = null;
        if (this.getQuoteTypeFilter() != null) {
            quoteTypeFilterNames = new String[this.getQuoteTypeFilter().length];
            for (int i = 0; i < this.getQuoteTypeFilter().length; i++) {
                if (this.getQuoteTypeFilter()[i].equalsIgnoreCase("RNWLQUOTE"))
                    quoteTypeFilterNames[i] = FindQuoteParamKeys.RENEWAL_QUOTE_EDITS;
                else if (this.getQuoteTypeFilter()[i].equalsIgnoreCase("SLSQUOTE"))
                    quoteTypeFilterNames[i] = FindQuoteParamKeys.SALES_QUOTES;
            }
        }
        return quoteTypeFilterNames;
    }

    public String getQuoteTypeNameforResultList(String quoteType) {
        String quoteTypeName = "";
        if (quoteType.trim().equalsIgnoreCase("SLSQUOTE")) {
            quoteTypeName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.SALES);
        } else if (quoteType.trim().equalsIgnoreCase("RNWLQUOTE")) {
            quoteTypeName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.RENEWAL);
        }
        return quoteTypeName;
    }

    public String[] getLOBsFilter() {
        return findContract.getLOBsFilter();
    }

    public String[] getLOBsFilterNames() {
        String[] LOBsFilterNames = null;
        if (this.getLOBsFilter() != null) {
            LOBsFilterNames = new String[this.getLOBsFilter().length];
            List lobList = this.getLobList();
            for (int i = 0; i < this.getLOBsFilter().length; i++) {
                Iterator lobIt = lobList.iterator();
                while (lobIt.hasNext()) {
                    CodeDescObj lob = (CodeDescObj) lobIt.next();
                    if (this.getLOBsFilter()[i].equalsIgnoreCase(lob.getCode()))
                        LOBsFilterNames[i] = lob.getCodeDesc();
                }
            }
        }
        return LOBsFilterNames;
    }

    public String[] getLOBsFilterNameCodes() {
        String[] LOBsFilterNameCodes = null;
        if (this.getLOBsFilter() != null) {
        	LOBsFilterNameCodes = new String[this.getLOBsFilter().length];
            List lobList = this.getLobList();
            for (int i = 0; i < this.getLOBsFilter().length; i++) {
                Iterator lobIt = lobList.iterator();
                while (lobIt.hasNext()) {
                    CodeDescObj lob = (CodeDescObj) lobIt.next();
                    if (this.getLOBsFilter()[i].equalsIgnoreCase(lob.getCode()))
                    	
                		if (QuoteConstants.LOB_PA.equals(lob.getCode())) {
                			LOBsFilterNameCodes[i] = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, "lob_pa_csra_desc");
                		} else if (QuoteConstants.LOB_PAE.equals(lob.getCode())) {
                			LOBsFilterNameCodes[i] = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, "lob_pae_csta_desc");
                		} else {
                			LOBsFilterNameCodes[i] = lob.getCodeDesc();
                		}                    	
                }
            }
        }
        return LOBsFilterNameCodes;
    }
    
    public String[] getStatusFilter() {
        return findContract.getStatusFilter();
    }

    public String[] getStatusFilterNames() {
        String[] statusFilterNames = null;
        if (this.getStatusFilter() != null) {
            statusFilterNames = new String[this.getStatusFilter().length];
            List statusList = this.getOverallStatus();
            for (int i = 0; i < this.getStatusFilter().length; i++) {
                Iterator statusIt = statusList.iterator();
                while (statusIt.hasNext()) {
                    CodeDescObj status = (CodeDescObj) statusIt.next();
                    if (this.getStatusFilter()[i].equalsIgnoreCase(status.getCode())){
                        statusFilterNames[i] = status.getCodeDesc();
                    }else if(this.getStatusFilter()[i].equalsIgnoreCase(quoteStatuseEeqs.getCode())){
                        statusFilterNames[i] = quoteStatuseEeqs.getCodeDesc();
                    }
                }
            }
        }
        return statusFilterNames;
    }

    public String getStatusName(String statusCode) {
        String statusName = "";
        Iterator statusIt = getOverallStatus().iterator();
        while (statusIt.hasNext()) {
            CodeDescObj status = (CodeDescObj) statusIt.next();
            if (statusCode.trim().equalsIgnoreCase(status.getCode().trim())) {
                statusName = status.getCodeDesc().trim();
                break;
            }
        }
        return statusName;
    }

    public String getTimeFilter() {
        return findContract.getTimeFilter();
    }

    public String getTimeFilterNames() {
        String timeFilterNames = null;
        if (this.getTimeFilter() != null) {
            if (this.getTimeFilter().equalsIgnoreCase("7")) {
                timeFilterNames = "1 "
                        + getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.WEEK);
            } else if (this.getTimeFilter()
                    .equalsIgnoreCase(ApplicationProperties.getInstance().getQuoteAnyTimeValue())) {
                timeFilterNames = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.ANYTIME);
            } else if (this.getTimeFilter().equalsIgnoreCase("Month")) {
                timeFilterNames = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.MONTHTITLE);
            } else if (this.getTimeFilter().equalsIgnoreCase("Quarter")) {
                timeFilterNames = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.QUARTER);
            }
        }
        return timeFilterNames;
    }

    public List getOverallStatus() {
        return overallStatus;
    }
    
    //Get code description from message bundle 
    public String getOverallStatusCodeDesc(String code) {
    	if (isPGSFlag()) {
    		if (code.equalsIgnoreCase("QS010")) {
    			codeDesc = "";
    		} else {
    			codeDesc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, code);
    		}
    	} else {
    		codeDesc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, code);
    	}
    	return codeDesc;
    }

    /**
     * @return Returns the lobList.
     */
    public List getLobList() {
        return lobList;
    }

    public Collection generateSortByOptions() {

        sortByOptionList = new ArrayList();

        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.SELECT_FOLLOWING), "", this.getSortBy().equalsIgnoreCase("")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.DATE_SUBMITTED), "0", this.getSortBy().equalsIgnoreCase("0")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.CUSTOMER_NAME), "1", this.getSortBy().equalsIgnoreCase("1")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.RESELLER_NAME), "2", this.getSortBy().equalsIgnoreCase("2")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.OVERALL_STATUS), "3", this.getSortBy().equalsIgnoreCase("3")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.TOTAL_PRICE), "4", this.getSortBy().equalsIgnoreCase("4")));

        //		--5:First approval date (descending)
        //		--6:Final approval date (descending)
        if(!isPGSFlag()) {
        	sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                    FindQuoteParamKeys.First_Approval_Date), "5", this.getSortBy().equalsIgnoreCase("5")));
            sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                    FindQuoteParamKeys.Final_Approval_Date), "6", this.getSortBy().equalsIgnoreCase("6")));
        }

        return sortByOptionList;
    }

    public String getSortBy() {
        return StringEncoder.textToHTML(findContract.getSortFilter());
    }

    public String getSortByName() {
        String sortByName = "";
        if (this.getSortBy().equalsIgnoreCase("0"))
            sortByName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.DATE_SUBMITTED);
        else if (this.getSortBy().equalsIgnoreCase("1"))
            sortByName = getI18NString(I18NBundleNames.BASE_MESSAGES, locale, FindQuoteParamKeys.CUSTOMER_NAME);
        else if (this.getSortBy().equalsIgnoreCase("2"))
            sortByName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.RESELLER_NAME);
        else if (this.getSortBy().equalsIgnoreCase("3"))
            sortByName = getI18NString(I18NBundleNames.BASE_MESSAGES, locale, FindQuoteParamKeys.OVERALL_STATUS);
        else if (this.getSortBy().equalsIgnoreCase("4"))
            sortByName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.TOTAL_PRICE);
        
        else if (this.getSortBy().equalsIgnoreCase("5"))
            sortByName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.First_Approval_Date);
        else if (this.getSortBy().equalsIgnoreCase("6"))
            sortByName = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.Final_Approval_Date);
        
        
        return sortByName;
    }

    public String getCountryNameforResultList(String countryCode) {
        String countryDesc = "";
        if (this.countryListObj != null) {
            Iterator countryListIt = this.countryListObj.iterator();
            while (countryListIt.hasNext()) {
                CodeDescObj countryObj = (CodeDescObj) countryListIt.next();
                if (countryCode.equalsIgnoreCase(countryObj.getCode()))
                    countryDesc = countryObj.getCodeDesc();
            }
        } else
            return countryCode;
        return countryDesc;
    }

    /**
     * @return Returns the cf.
     */
    public String getCf() {
        return cf;
    }

    /**
     * @return Returns the countryListObj.
     */
    public List getCountryListObj() {
        return countryListObj;
    }

    public String formatDouble(String price) {
        DecimalFormat df = new DecimalFormat(".00");
        return df.format(Double.parseDouble(price));
    }

    public String getPrePageURL() {
        String prePageURL = "";
        prePageURL += getChangeCriteriaURLDetails();
        prePageURL += "&" + FindQuoteParamKeys.PARAM_PAGE_INDEX + "=" + this.getFindResult().getPre();
        return prePageURL;
    }

    public String getNextPageURL() {
        String nextPageURL = "";
        nextPageURL += getChangeCriteriaURLDetails();
        nextPageURL += "&" + FindQuoteParamKeys.PARAM_PAGE_INDEX + "=" + this.getFindResult().getNext();
        return nextPageURL;
    }

    public String getChangeCriteriaURLDetails() {
        String criteriaURL = "";
        String[] quoteType = this.getQuoteTypeFilter();
        if (quoteType != null) {
            criteriaURL += "&" + FindQuoteParamKeys.QUOTE_TYPE_FILTER + "=";
            for (int i = 0; i < quoteType.length; i++) {
                criteriaURL += quoteType[i] + ":";
            }
        }
        String[] lobsFilter = this.getLOBsFilter();
        if (lobsFilter != null) {
            criteriaURL += "&" + FindQuoteParamKeys.LOBS_FILTER + "=";
            for (int i = 0; i < lobsFilter.length; i++) {
                criteriaURL += lobsFilter[i] + ":";
            }
        }
        String[] statusFilter = this.getStatusFilter();
        if (statusFilter != null) {
            criteriaURL += "&" + FindQuoteParamKeys.STATUS_FILTER + "=";
            for (int i = 0; i < statusFilter.length; i++) {
                criteriaURL += statusFilter[i] + ":";
            }
        }
        String timeFilter = this.getTimeFilter();
        if (timeFilter != null) {
            criteriaURL += "&" + FindQuoteParamKeys.TIME_FILTER + "=" + timeFilter + ":";
        }
        
        String[] timeFilterOptions = findContract.getTimeFilterOptions();
        if(timeFilterOptions != null ){
            criteriaURL += "&" + FindQuoteParamKeys.TIME_FILTER.concat("Options") + "=";
            for (int i = 0; i < timeFilterOptions.length; i++) {
                criteriaURL += timeFilterOptions[i] + ",";
            }
        }
        
        criteriaURL += "&" + FindQuoteParamKeys.SORT_FILTER + "=" + this.getSortBy();
        criteriaURL += "&" + FindQuoteParamKeys.PARAM_POST_FLAG + "=true";
        return criteriaURL;
    }

    public String getI18NString(String basename, Locale locale, String key) {
        return ApplicationContextFactory.singleton().getApplicationContext()
                .getI18nValueAsString(basename, locale, key);
    }

    /**
     * @return Returns the page_next.
     */
    public String getPage_next() {
        return getI18NString(I18NBundleNames.BASE_MESSAGES, getLocale(), FindQuoteParamKeys.PAGE_NEXT);
    }

    /**
     * @param page_next
     *            The page_next to set.
     */
    public void setPage_next(String page_next) {
        this.page_next = page_next;
    }

    /**
     * @return Returns the page_previous.
     */
    public String getPage_previous() {
        return getI18NString(I18NBundleNames.BASE_MESSAGES, getLocale(), FindQuoteParamKeys.PAGE_PREVIOUS);
    }

    /**
     * @param page_previous
     *            The page_previous to set.
     */
    public void setPage_previous(String page_previous) {
        this.page_previous = page_previous;
    }

    /**
     * @param changeCriteriaURL
     *            The changeCriteriaURL to set.
     */
    public void setChangeCriteriaURL(String changeCriteriaURL) {
        this.changeCriteriaURL = changeCriteriaURL;
    }

    public boolean getDisplayFlag(QuoteHeader quoteHeader) {
        //Return true, always display the order info.
        return true;
    }

    public String getDisplayString(Quote quote) {
        List primaryList = quote.getSapPrimaryStatusList();
        List secondaryList = quote.getSapSecondaryStatusList();
        String displayString = "";
        if (primaryList != null && primaryList.size() > 0) {
            for (int j = 0; j < primaryList.size(); j++) {
                displayString = displayString + ((QuoteStatus) primaryList.get(j)).getStatusCodeDesc() + ", ";
            }
        }
        if (secondaryList != null && secondaryList.size() > 0) {
            for (int j = 0; j < secondaryList.size(); j++) {
                displayString = displayString + ((QuoteStatus) secondaryList.get(j)).getStatusCodeDesc() + ", ";
            }
        }
        if (displayString.endsWith(", "))
            displayString = displayString.substring(0, displayString.length() - 2);
        return displayString;
    }

    public boolean getOrderStatusExistFlag(Quote quote) {
        boolean existFlag = false;
        if (quote.getOrders() != null && quote.getOrders().size() > 0) {
            Iterator it = quote.getOrders().iterator();
            while (it.hasNext()) {
                Order order = (Order) it.next();
                if ((order != null) && order.getStatusList() != null && order.getStatusList().size() > 0) {
                    existFlag = true;
                    break;
                }
                if (order.getOrderNumber().equals("") && order.getOrderIdocNum() != null) {
                    existFlag = true;
                    break;
                }
            }
        }
        return existFlag;
    }

    public boolean getOrderNumSubmittedTotalDisplayFlag(Quote quote) {
        boolean displayFlag = false;
        if (quote.getOrders() != null && quote.getOrders().size() > 0) {
            Iterator it = quote.getOrders().iterator();
            while (it.hasNext()) {
                Order order = (Order) it.next();
                if (!order.getOrderNumber().equals("")) {
                    displayFlag = true;
                    break;
                }
            }
        }
        return displayFlag;
    }

    public int calculateItemNum(Quote quote) {
        int itemNum = 4;
        if (quote.getReseller() != null && quote.getReseller().getCustNum() != null
                && !quote.getReseller().getCustNum().trim().equals("")) {
            itemNum++;
        }
        if (quote.getPayer() != null && quote.getPayer().getCustNum() != null
                && !quote.getPayer().getCustNum().trim().equals("")) {
            if (quote.getReseller() == null || quote.getReseller().getCustNum() == null
                    || !quote.getPayer().getCustNum().trim().equals(quote.getReseller().getCustNum().trim())) {
                itemNum++;
            }
        }
        if (!getDisplayString(quote).equals("")) {
            itemNum++;
        }
        if ((getDisplayFlag(quote.getQuoteHeader())) && (getOrderStatusExistFlag(quote))) {
            itemNum++;
        }
        
        if(getOrderNumSubmittedTotalDisplayFlag(quote)) {
        	itemNum++;
        }

        return itemNum;
    }

    public Map getNameContainer() {
        return nameContainer;
    }

    public String getDetailURL() {
        return HtmlUtil.getURLForAction(FindQuoteActionKeys.DISPATCH_SUBMITTED_QUOTE_TABS) + "&amp;quoteNum=";
    }

    public Collection genQuoteAcquisitionOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String defaultOption = context.getI18nValueAsString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteMessageKeys.SELECT_ACQUISITION);

        String qtClassCode = findContract.getActuationFilter();
        boolean isDefault = StringUtils.isBlank(qtClassCode);
        collection.add(new SelectOptionImpl(defaultOption, "", isDefault));

        if (acquisitionList != null) {
            for (int i = 0; i < acquisitionList.size(); i++) {
                CodeDescObj classCode = (CodeDescObj) acquisitionList.get(i);
                boolean isSelected = false;
                if (StringUtils.isNotBlank(qtClassCode)) {
                    isSelected = qtClassCode.equalsIgnoreCase(classCode.getCode());
                }
                collection.add(new SelectOptionImpl(classCode.getCodeDesc(), classCode.getCode(), isSelected));
            }
        }

        return collection;
    }

    /**
     * @return Returns the classificationList.
     */
    public List getClassificationList() {
        return classificationList;
    }

    public String[] getClassificationFilter() {
        return findContract.getClassificationFilter();
    }

    public String getActuationFilter() {
        return findContract.getActuationFilter();
    }

    public String[] getClassificationFilterNames() {
        String[] classificationFilterNames = null;
        if (this.getClassificationFilter() != null) {
            classificationFilterNames = new String[this.getClassificationFilter().length];
            List classificationList = this.getClassificationList();
            for (int i = 0; i < this.getClassificationFilter().length; i++) {
                Iterator clIt = classificationList.iterator();
                while (clIt.hasNext()) {
                    CodeDescObj lob = (CodeDescObj) clIt.next();
                    if (this.getClassificationFilter()[i].equalsIgnoreCase(lob.getCode()))
                        classificationFilterNames[i] = lob.getCodeDesc();
                }
            }
        }
        return classificationFilterNames;
    }

    public String getActuationFilterName() {

        if (acquisitionList != null && findContract.getActuationFilter() != null) {
            for (int i = 0; i < acquisitionList.size(); i++) {
                CodeDescObj classCode = (CodeDescObj) acquisitionList.get(i);
                if (classCode.getCode().equalsIgnoreCase(findContract.getActuationFilter())) {
                    return classCode.getCodeDesc();
                }
            }
        }
        return null;
    }

    public Collection generateQuarterOptions() {
        quarterOptionList = new ArrayList();

        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.DATE,1);
        
        String[] timeFilterOptions = null;
        
        if(findContract.getTimeFilter() != null && findContract.getTimeFilter().equals("Quarter")){
            timeFilterOptions = findContract.getTimeFilterOptions();
        }else{
            timeFilterOptions = new String[0];
        }
        List optionSelectedList = new ArrayList();
        if(timeFilterOptions != null){
	        for(int i = 0; i < timeFilterOptions.length; i++){
	            optionSelectedList.add(timeFilterOptions[i]);
	        }
        }
        boolean isSelected = false;
        String optionLable = "";
        String optionValue = "";
        for (int i = 0; i < quarterOptionRange; i++) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            switch (month) {
            case 0:
	        case 1:
	        case 2:
	            optionLable = "Q1 " + year;
	            optionValue = year + "-01-01:" + year + "-03-31";
	            break;
	        case 3:
	        case 4:
	        case 5:
	            optionLable = "Q2 " + year;
	            optionValue = year + "-04-01:" + year + "-06-30";
	            break;
	        case 6:
	        case 7:
	        case 8:
	            optionLable = "Q3 " + year;
	            optionValue = year + "-07-01:" + year + "-09-30";
	            break;
	        case 9:
	        case 10:
	        case 11:
	            optionLable = "Q4 " + year;
	            optionValue = year + "-10-01:" + year + "-12-31";
	            break;
	        }
            
            isSelected = optionSelectedList.contains(optionValue);
            quarterOptionList.add(new SelectOptionImpl(optionLable, optionValue, isSelected));

            calendar.add(Calendar.MONTH, -3);
        }

        return quarterOptionList;
    }

    public Collection generateTimeFilterMonthOptions() {
        monthOptionList = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        
        String[] timeFilterOptions = null;
        
        if(findContract.getTimeFilter() != null && findContract.getTimeFilter().equals("Month")){
            timeFilterOptions = findContract.getTimeFilterOptions();
        }else{
            timeFilterOptions = new String[0];
        }
        
        List optionSelectedList = new ArrayList();
        if(timeFilterOptions != null){
	        for(int i = 0; i < timeFilterOptions.length; i++){
	            optionSelectedList.add(timeFilterOptions[i]);
	        }
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        String[] labels = { MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR, MessageKeys.MONTH_APR,
                MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC };
        String labelValue = "";
        String labelStr = "";
        boolean isSelected = false;
        
        for (int i = 0; i < monthOptionRange; i++) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            labelStr = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[month]) + " " + year;
            labelValue = df.format(calendar.getTime()) + "-01:";
            labelValue += df.format(calendar.getTime()) + "-" + calendar.getActualMaximum(Calendar.DATE);
            
            isSelected = optionSelectedList.contains(labelValue);
            monthOptionList.add(new SelectOptionImpl(labelStr,labelValue, isSelected));
            
            calendar.add(Calendar.MONTH, -1);
        }
        
        return monthOptionList;
    }
    
    public String getTimeFilterOptionsNames(){
        
        String timeFilter = findContract.getTimeFilter();
        if (StringUtils.isNotBlank(timeFilter)) {
            if (timeFilter.equals("7")) {
                return "1 week";
            } else if (timeFilter.equals("36500")) {
                return "Anytime";
            } else if (timeFilter.equals("Quarter") || timeFilter.equals("Month")) {
                ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
                String[] timeRange = findContract.getTimeFilterOptions();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                StringBuffer result = new StringBuffer("  ");
                Calendar startCal = Calendar.getInstance();
                Calendar endCal = Calendar.getInstance();

                String[] labels = { MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                        MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL,
                        MessageKeys.MONTH_AUG, MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV,
                        MessageKeys.MONTH_DEC };

                try {
                    for (int i = 0; i < timeRange.length; i++) {
                        String str = timeRange[i];
                        String[] strArr = str.split(":");
                        startCal.setTime(df.parse(strArr[0]));
                        endCal.setTime(df.parse(strArr[1]));

                        int year = startCal.get(Calendar.YEAR);
                        int month = startCal.get(Calendar.MONTH);
                        if (month == endCal.get(Calendar.MONTH)) {
                            result.append(context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                                    labels[month])
                                    + " " + year);
                        } else {
                            startCal.add(Calendar.MONTH, 3);
                            startCal.add(Calendar.DATE, -1);

                            if (startCal.getTime().equals(endCal.getTime())) {
                                if (month == 0) {
                                    result.append("Q1 " + year);
                                } else if (month == 3) {
                                    result.append("Q2 " + year);
                                } else if (month == 6) {
                                    result.append("Q3 " + year);
                                } else if (month == 9) {
                                    result.append("Q4 " + year);
                                }

                            }
                        }
                        result.append(", ");
                    }
                    String temp = result.toString();
            		return temp.substring(0,temp.length()-2);
                } catch (Exception e) {
                    logger.error(this, e);
                }
            }
        }
        return "";
		
	}
    
    public String[] getTimeFilterOptions(){
        return findContract.getTimeFilterOptions();
    }
    
    public String getPoGenStatus(){
        return poGenStatus;
    }
      
    /**
     * @return Returns the quoteStatuseEeqs.
     */
    public CodeDescObj getQuoteStatuseEeqs() {
        return quoteStatuseEeqs;
    }
    
    public String getOrderHistoryLink(){
    	return ApplicationProperties.getInstance().getOrderHistoryDetailURL();
    }
    
    public String getViewCustRptHostedServicesURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT);
        StringBuffer sb = new StringBuffer(url);
        sb.append("&amp;" + DraftQuoteParamKeys.RPT_SAP_CUST_NUM + "=");
        return sb.toString();
    }
    
    public boolean isDisplayStatusPage(){
    	if(isPGSFlag()){
    		return isTier1Reseller() || isDistributor();
    	}else{
    		return true;
    	}
    }
    
    public String displayOrderType(String orderType){    	
    	if(!isPGSFlag()){
    		if(orderType != null && orderType.equals("Services order")){
    			orderType = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,FindQuoteParamKeys.SERIVCE_ORDER);
    		}
    	}   	
    	return orderType;
    }
}
