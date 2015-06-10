package com.ibm.dsw.quote.findquote.config;

import com.ibm.dsw.quote.base.config.ApplicationProperties;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteDBConstants</code> class.
 * 
 * @author wangxu@cn.ibm.com
 *
 * Created on 2007-4-30
 */
public interface FindQuoteDBConstants {

    public static final String SP_S_QT_STAT_BY_NUM = "S_QT_STAT_BY_NUM";
    public static final String SP_S_QT_STAT_BY_ORD_NUM = "S_QT_STAT_BY_ORD_NUM";
    public static final String SP_S_QT_STAT_BY_OPP_NUM = "S_QT_STAT_BY_OPP_NUM";
    public static final String SP_S_QT_STAT_BY_IBMER = "S_QT_STAT_BY_IBMER";
    public static final String SP_S_QT_STAT_BY_CNTRY = "S_QT_STAT_BY_CNTRY";
    public static final String SP_S_QT_STAT_BY_HDINF = "S_QT_STAT_BY_HDINF";
    public static final String SP_S_QT_APR_QUEUE = "S_QT_APR_QUEUE";
    public static final String SP_S_QT_EVAL_QUEUE = "S_QT_EVAL_QUEUE";
    public static final String SP_S_QT_STAT_BY_APPVLATTR = "S_QT_STAT_BY_APPVLATTR";
    public static final String SP_S_QT_IBMER_STATUS_TRACKER = "S_QT_IBMER_STATUS_TRACKER";
    
    public static final String SP_S_QT_STAT_BY_NUM_EBIZ2 = "S_QT_STAT_BY_NUM_EBIZ2";
    public static final String SP_S_QT_STAT_BY_ORD_NUM_EBIZ2 = "S_QT_STAT_BY_ORD_NUM_EBIZ2";
    public static final String SP_S_QT_STAT_BY_OPP_NUM_EBIZ2 = "S_QT_STAT_BY_OPP_NUM_EBIZ2";
    public static final String SP_S_QT_STAT_BY_IBMER_EBIZ2 = "S_QT_STAT_BY_IBMER_EBIZ2";
    public static final String SP_S_QT_STAT_BY_CNTRY_EBIZ2 = "S_QT_STAT_BY_CNTRY_EBIZ2";
    public static final String SP_S_QT_STAT_BY_HDINF_EBIZ2 = "S_QT_STAT_BY_HDINF_EBIZ2";
    public static final String SP_S_QT_APR_QUEUE_EBIZ2 = "S_QT_APR_QUEUE_EBIZ2";
    public static final String SP_S_QT_STAT_BY_APPVLATTR_EBIZ2 = "S_QT_STAT_BY_APPVLATTR_EBIZ2";
    public static final String SP_S_QT_IBMER_STATUS_TRACKER_EBIZ2 = "S_QT_IBMER_STATUS_TRACKER_EBIZ2";
    
    public static final int MAX_FETCH_NUM = ApplicationProperties.getInstance().getFindQuoteMaxFectchNum();
    public static final String FIND_QUOTE_PAGE_SIZE = ApplicationProperties.getInstance().getFindQuotePageSize();
    public static final String DB2_LIST_ITEM_DIVIDER = ",";
    public static final String DB2_UNKNOWN_SIGN = "%";
    
    public static final String SP_S_QTM_MY_QUOTES = "S_QTM_MY_QUOTES";
    public static final String SP_S_QTM_QUOTES_BY_NUM = "S_QTM_QUOTES_BY_NUM";
    public static final String S_QTM_QUOTE_BY_CUSTOMER = "S_QTM_QUOTE_BY_CUSTOMER";
    public static final String SP_S_QTM_RECENT_QUOTE_LIST = "S_QTM_RECENT_QUOTE_LIST";
    public static final String SP_S_QTM_APR_QUEUE = "S_QTM_APR_QUEUE";
    public static final String SP_S_QTM_QUOTE_DTL = "S_QTM_QUOTE_DTL";
    public static final String S_QT_GET_AMEND_INFO = "S_QT_GET_AMEND_INFO";
    
    public static final String D_QT_FAVORITE_QUOTE = "D_QT_FAVORITE_QUOTE";
    public static final String S_QT_GET_FAVORITE = "S_QT_GET_FAVORITE";
    public static final String I_QT_FAVORITE_QUOTE = "I_QT_FAVORITE_QUOTE";
    public static final String D_QTM_RECENT_QUOTE = "D_QTM_RECENT_QUOTE";
    
    public static final String S_QTM_MESSAGE_DTL = "S_QTM_MESSAGE_DTL";
    public static final String I_QTM_MESSAGE_DTL = "I_QTM_MESSAGE_DTL";
    public static final String I_QTM_SYSTEM_FEEDBACK_MESSAGE = "I_QTM_SYSTEM_FEEDBACK_MESSAGE";
    public static final String U_QTM_MESSAGE_DTL = "U_QTM_MESSAGE_DTL";
    public static final String D_QTM_MESSAGE_DTL = "D_QTM_MESSAGE_DTL";
    public static final String IU_QTM_FEEDBACK_DTL = "IU_QTM_FEEDBACK_DTL";
    public static final String S_QTM_FEEDBACK_DTL = "S_QTM_FEEDBACK_DTL";
    
    public static final String SQL_QT_FIND_PART_LINE_ITEM = "SQL_QT_FIND_PART_LINE_ITEM";
    public static final String SQL_QT_FIND_PART_LINE_ITEM_LIST = "SQL_QT_FIND_PART_LINE_ITEM_LIST";
    public static final String S_QT_GET_FAVORITE_DETAIL = "S_QT_GET_FAVORITE_DETAIL";
    public static final String S_QT_FIND_PART_LINE_ITEM = "S_QT_FIND_PART_LINE_ITEM";
    

}
