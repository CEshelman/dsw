package com.ibm.dsw.quote.submittedquote.config;

import com.ibm.dsw.quote.base.config.ActionKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteActionKeys<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 28, 2007
 */

public interface SubmittedQuoteActionKeys extends ActionKeys {
    public static final String DISPLAY_SUBMITTEDQT_SALES_INFO_TAB = "DISPLAY_SUBMITTEDQT_SALES_INFO_TAB";
    public static final String DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB = "DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB";
    public static final String POST_DRAFTQT_SPECIAL_BID_TAB = "POST_DRAFTQT_SPECIAL_BID_TAB";
    public static final String DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB = "DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB";
    public static final String SUBMITTEDQT_ADD_REVIEWER = "SUBMITTEDQT_ADD_REVIEWER";
    
    public static final String DISPLAY_SUBMITTEDQT_STATUS_TAB = "DISPLAY_SUBMITTEDQT_STATUS_TAB";
    
    public static final String DISPLAY_SUBMITTEDQT_PART_PRICE_TAB = "DISPLAY_SUBMITTEDQT_PART_PRICE_TAB";
    public static final String DISPLAY_DRAFT_PART_PRICE_TAB = "DISPLAY_PART_PRICE_TAB";
    public static final String DISPLAY_SUBMITTEDQT_EXEC_SUMMARY_TAB = "DISPLAY_SUBMITTEDQT_EXEC_SUMMARY_TAB";
    public static final String SAVE_QUOTE_EXEC_SUMMARY_TAB = "SAVE_QUOTE_EXEC_SUMMARY_TAB";
    public static final String DISPLAY_EXEC_SUMMARY_PP = "DISPLAY_EXEC_SUMMARY_PP";
    //Appliance#99
    public static final String UPDATE_LINE_ITEM_CRAD_DATE="UPDATE_LINE_ITEM_CRAD_DATE";
    
    public static final String UPDATE_OPPR_INFO = "UPDATE_OPPR_INFO";
    
    public static final String COPY_UPDATE_SUBMITTED_QUOTE = "COPY_UPDATE_SUBMITTED_QUOTE";
    public static final String UPDATE_QUOTE_DATE = "UPDATE_QUOTE_DATE";
    public static final String EXTEND_QUOTE_EXP_DATE = "EXTEND_QUOTE_EXP_DATE";
    public static final String SUBMIT_EXTEND_QUOTE_EXP_DATE = "SUBMIT_EXTEND_QUOTE_EXP_DATE";
    public static final String SAVE_EXTEND_QUOTE_EXP_DATE = "SAVE_EXTEND_QUOTE_EXP_DATE";
    public static final String GO_EXTEND_QUOTE_EXP_DATE = "GO_EXTEND_QUOTE_EXP_DATE";
    public static final String SUBMIT_APPROVER_ACTION = "SUBMIT_APPROVER_ACTION";
    public static final String UPDATE_CATEGORY = "UPDATE_CATEGORY";
    public static final String UPDATE_SALES_PLAY_NUM = "UPDATE_SALES_PLAY_NUM";
    public static final String UPDATE_RQ_SBINFO = "UPDATE_RQ_SBINFO";
    public static final String CANCEL_SUBMITTED_QUOTE = "CANCEL_SUBMITTED_QUOTE";
    public static final String UPDATE_ICN_PRECREDIT = "UPDATE_ICN_PRECREDIT";
    
    public static final String SUBMITTEDQT_UPDATE_APPROVER_SELECTION = "UPDATE_APPROVER_SELECTION";
    public static final String ADD_SB_COMMENT_ATTACHMENTS = "ADD_SB_COMMENT_ATTACHMENTS";
    public static final String SUBMIT_QUOTE_RTF_DOWNLOAD = "SUBMIT_QUOTE_RTF_DOWNLOAD";
    public static final String SUBMIT_QUOTE_EXEC_SUMMARY_RTF_DOWNLOAD = "SUBMIT_QUOTE_EXEC_SUMMARY_RTF_DOWNLOAD";
    public static final String SUBMIT_QUOTE_EXEC_SUMMARY_PDF_DOWNLOAD = "SUBMIT_QUOTE_EXEC_SUMMARY_PDF_DOWNLOAD";
    public static final String UPDATE_QUOTE_PARTNER = "UPDATE_QUOTE_PARTNER";
    public static final String REMOVE_SUBMITTED_QUOTE_ATTACHMENT = "REMOVE_SUBMITTED_QUOTE_ATTACHMENT";
    public static final String COPY_APPROVED_BID = "COPY_APPROVED_BID";

    public static final String DISPLAY_STATUS_DETEAIL_EXPLANATION = "DISPLAY_STATUS_DETEAIL_EXPLANATION";
    
    public static final String EXPORT_SUBMITTED_QUOTE = "EXPORT_SUBMITTED_QUOTE";
    
    public static final String EXPORT_EVAL_QUOTE = "EXPORT_EVAL_QUOTE";
    
    public static final String EXPORT_EVAL_QUOTE_NATIVE_EXCEL = "EXPORT_EVAL_QUOTE_NATIVE_EXCEL";
    
    public static final String EXPORT_SUBMITTED_QUOTE_NATIVE_EXCEL = "EXPORT_SUBMITTED_QUOTE_NATIVE_EXCEL";
    
    public static final String REMOVED_QUOTE_STATUS = "REMOVED_QUOTE_STATUS";
    public static final String CANCEL_APPROVD_BID_ACTION = "CANCEL_APPROVED_BID";
    
    public static final String SEND_NEW_CUSTOMER_EMAIL = "SEND_NEW_CUSTOMER_EMAIL";
    
    public static final String SUBMIT_DRAFT_SQ_AS_FINAL = "SUBMIT_DRAFT_SQ_AS_FINAL";
    
    public static final String SAVE_BID_ITERATION = "SAVE_BID_ITERATION";
    public static final String CONVERT_TO_STD_COPY="CONVERT_TO_STD_COPY";
    public static final String POST_SUBMITTED_SALES_INFO_TAB="POST_SUBMITTED_SALES_INFO_TAB";
    public static final String GET_ORDER_HISTORY_DETAIL="GET_ORDER_HISTORY_DETAIL";
    public static final String SUBMIT_DRAFT_SQ_AS_FINAL_FOR_OUT_CHG="SUBMIT_DRAFT_SQ_AS_FINAL_FOR_OUT_CHG";
    public static final String VIEW_TXT_HISTORY = "VIEW_TXT_HISTORY";
    public static final String DISPLAY_ORDERED_ITEM_DETAIL = "DISPLAY_ORDERED_ITEM_DETAIL";
    
    public static final String QUOTE_MAIL_LINK_DISPATCH = "QUOTE_MAIL_LINK_DISPATCH";
    
    //Added by Andy@20140226 
    //when order submitted quote in PGS, need to validate if there is missing TOU.
    public static final String ORDER_SUBMITTED_QUOTE="ORDER_SUBMITTED_QUOTE";
}
