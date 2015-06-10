package com.ibm.dsw.quote.draftquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DraftRQParamKeys</code> class is to define parameter keys for
 * draft renewal quote.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 16, 2007
 */
public interface DraftRQParamKeys {
    public static final String PARAM_RQ_P_STATUS = "primaryStatus";

    public static final String PARAM_RQ_S_STATUS = "secondaryStatus";

    public static final String PARAM_RQ_TERM_REASON = "termReason";

    public static final String PARAM_RQ_TERM_COMMENT = "termComment";

    public static final String PARAM_RQ_TERM_EMAIL = "termEmail";

    public static final String PARAM_DISPLAY_RQ_STATUS = "displayRQStatus";

    public static final String PARAM_DISPLAY_CHANGE_RQ_STATUS = "displayChangeRQStatus";

    public static final String PARAM_DISPLAY_TERM_TRACKING = "displayTermTracking";

    public static final String PARAM_RQ_QUOTE_NUM = "quoteNum";

    //  These parameters are for internal reporting only
    public static final String PARAM_RPT_QUOTE_NUM = "QUOTE_NUM";

    public static final String PARAM_RPT_P1 = "P1";

    public static final String PARAM_RPT_LIST_ACTION_NAME = "LIST_ACTION_NAME";

    public static final String PARAM_RPT_SEARCH_ACTION_NAME = "SEARCH_ACTION_NAME";

    public static final String PARAM_RPT_SEARCH_CRITERIA_URL_PARAM = "SEARCH_CRITERIA_URL_PARAM";

    public static final String PARAM_RPT_SORT_BY = "SORT_BY";

    public static final String PARAM_RPT_ERROR_FLAG = "EF";
    
    // PP Tab params
    
    public static final String RQ_EDITABLE = "RQ_EDITABLE";
    
    public static final String ovrdDtStartDaySuffix = "_OVRD_DT_START_DAY";
    
    public static final String ovrdDtStartMonthSuffix = "_OVRD_DT_START_MONTH";

    public static final String ovrdDtStartYearSuffix = "_OVRD_DT_START_YEAR";

    public static final String ovrdDtEndDaySuffix = "_OVRD_DT_END_DAY";

    public static final String ovrdDtEndMonthSuffix = "_OVRD_DT_END_MONTH";

    public static final String ovrdDtEndYearSuffix = "_OVRD_DT_END_YEAR";
    
    public static final String ovrdDtStartButtonSuffix = "_OVRD_DT_START_BUTTON";

    public static final String ovrdDtEndButtonSuffix = "_OVRD_DT_END_BUTTON";

    public static final String dtStartDaySuffix = "_DT_START_DAY";
    
    public static final String dtStartMonthSuffix = "_DT_START_MONTH";

    public static final String dtStartYearSuffix = "_DT_START_YEAR";
    
    public static final String dtEndDaySuffix = "_DT_END_DAY";

    public static final String dtEndMonthSuffix = "_DT_END_MONTH";
    
    public static final String dtEndYearSuffix = "_DT_END_YEAR";

    public static final String dtStartButtonSuffix = "_DT_START_BUTTON";

    public static final String dtEndButtonSuffix = "_DT_END_BUTTON";

    public static final String RQ_REASON_FOR_CHG_SUFFIX = "_RQ_RSN_FOR_CHG";

    public static final String RQ_REASON_FOR_DEL_SUFFIX = "_RQ_RSN_FOR_DEL";

    public static final String RQ_REASON_FOR_ADD_SUFFIX = "_RQ_RSN_FOR_ADD";
    
    public static final String RQ_PREV_QTY_SUFFIX = "_RQ_PREV_QTYPREV_SUFFIX";
    
    public static final String RQ_PREV_START_DATE_SUFFIX = "_RQ_PREV_STARTDATEPREV_SUFFIX";
    
    public static final String RQ_PREV_END_DATE_SUFFIX = "_RQ_PREV_ENDDATEPREV_SUFFIX";
        
}
