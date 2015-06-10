package com.ibm.dsw.quote.draftquote.config;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>ActionKeys<code> class .
 *
 * @author: wangxu@cn.ibm.com
 *
 */
public interface DraftQuoteActionKeys {
    public static final String UPDATE_OPP_OWNER = "UPDATE_OPP_OWNER";
    public static final String DISPLAY_SPECIAL_BID_TAB = "DISPLAY_SPECIAL_BID_TAB";
    public static final String SET_USER_COOKIE = "SET_USER_COOKIE";
    public static final String POST_SPECIAL_BID_TAB = "POST_SPECIAL_BID_TAB";
    public static final String DISPLAY_CUST_PRTNR_TAB = "DISPLAY_CUST_PRTNR_TAB";
    public static final String POST_SQ_CUST_PRTNR_TAB = "POST_SQ_CUST_PRTNR_TAB";
    public static final String SUBMIT_QUOTE_FOR_CONFIRMATION = "SUBMIT_QUOTE_SUBMISSION";
    public static final String SUBMIT_QUOTE_FOR_CONFIRMATION_TO_STH = "SUBMIT_QUOTE_SUBMISSION_TO_STH";
    public static final String POST_RQ_CUST_PRTNR_TAB = "POST_RQ_CUST_PRTNR_TAB";
    public static final String DISPLAY_PARTS_PRICE_TAB = "DISPLAY_PART_PRICE_TAB";
    public static final String CHANGE_ADDITIONAL_MAINT_ACTION ="CHANGE_ADDITIONAL_MAINT_ACTION";
    public static final String DISPLAY_SALES_INFO_TAB = "DISPLAY_SALES_INFO_TAB";
    public static final String POST_SALES_INFO_TAB = "POST_SALES_INFO_TAB";
    public static final String DISPLAY_EMPTY_DRAFT_QUOTE = "DISPLAY_EMPTY_DRAFT_QUOTE";
    public static final String POST_PART_PRICE_TAB = "POST_PART_PRICE_TAB";
    public static final String CONVERT_QUOTE = "CONVERT_QUOTE";
    public static final String ADD_DUP_LINE_ITEM = "ADD_DUP_LINE_ITEM";
    public static final String DELETE_LINE_ITEM = "DELETE_LINE_ITEM";
    public static final String REMOVE_CONFIGURATION = "REMOVE_CONFIGURATION";
    public static final String ADD_RQ_PARTS_TO_SQ = "ADD_RQ_PARTS_TO_SQ";
    public static final String DISPLAY_CURRENT_DRAFT_QUOTE = "DISPLAY_CURRENT_DRAFT_QUOTE";
    public static final String DISPLAY_RQ_STATUS_TAB = "DISPLAY_RQ_STATUS_TAB";
    public static final String POST_RQ_STATUS_TAB = "POST_RQ_STATUS_TAB";
    public static final String OVERRIDE_DATE = "OVERRIDE_DATE";
    public static final String SUBMIT_DRAFT_SQ_AS_FINAL = "SUBMIT_DRAFT_SQ_AS_FINAL";
    public static final String CONVERT_TO_SQ = "CONVERT_TO_SQ";
    public static final String CONVERT_RQ_TO_SPECL_BID = "CONVERT_RQ_TO_SPECL_BID";
    public static final String ORDER_DRAFT_QUOTE = "ORDER_DRAFT_QUOTE";
    public static final String SUBMIT_DRAFT_SQ_FOR_APPROVAL = "SUBMIT_DRAFT_SQ_FOR_APPROVAL";
    public static final String SUBMIT_DRAFT_RQ_AS_FINAL = "SUBMIT_DRAFT_RQ_AS_FINAL";
    public static final String SUBMIT_DRAFT_RQ_FOR_APPROVAL = "SUBMIT_DRAFT_RQ_FOR_APPROVAL";
    public static final String EXPORT_QUOTE = "EXPORT_QUOTE";
    public static final String EXPORT_QUOTE_NATIVE_EXCEL = "EXPORT_QUOTE_NATIVE_EXCEL";
    public static final String SUBMIT_SPECIALBID_JUSTIFICATION_DOCUMENTS = "SUBMIT_SPECIALBID_JUSTIFICATION_DOCUMENTS";
    public static final String RNW_QUOTE_SEARCH = "quoteitemssmryqtnum";
    public static final String SAVE_AS_NEW_DRAFT_QUOTE="SAVE_AS_NEW_DRAFT_QUOTE";
    public static final String DRAFT_QUOTE_RTF_DOWNLOAD = "DRAFT_QUOTE_RTF_DOWNLOAD";
    public static final String DISPLAY_ADD_SALESINFO_ATTACHMENTS_ACTION = "DISPLAY_ADD_SALESINFO_ATTACHMENTS_ACTION";
    public static final String CHECK_QUOTE_LOCK_ACTION = "CHECK_QUOTE_LOCK";
    public static final String UNLOCK_QUOTE_ACTION = "UNLOCK_QUOTE_ACTION";
    public static final String CREATE_QUOTE_FROM_ORDER = "CREATE_QUOTE_FROM_ORDER";

    // References to internal reporting's action keys
    public static final String DISPLAY_CUST_DTL_ENROLL_RPT = "enrollments";
    public static final String DISPLAY_CUST_DTL_CNTS_RPT = "customercontacts";
    public static final String DISPLAY_CUST_DTL_RNQT_RPT = "rnwlquot";
    public static final String DISPLAY_CUST_DTL_LCNS_RPT = "license";
    public static final String DISPLAY_CUST_DTL_HIST_RPT = "ordhist";
    public static final String DISPLAY_CUST_DTL_HTSV_RPT = "hostedServices";
    public static final String DISPLAY_CUST_DTL_HTSV_RPT_PGS = "HOSTED_SERVICES";

    //October#8 What if Analysis
    public static final String OVRRD_TRAN_LEVEL_CODE_ACTION ="OVRRD_TRAN_LEVEL_CODE_ACTION";
    public static final String APPLY_DISCOUNT_ACTION = "APPLY_DISCOUNT";
    public static final String APPLY_OFFER_ACTION = "APPLY_OFFER";
    public static final String CLEAR_OFFER_ACTION = "CLEAR_OFFER";
    public static final String YTY_GROWTH_DELEGATION_ACTION = "YTY_GROWTH_DELEGATION";

    //Supporting files
    public static final String REMOVE_SB_ATTACHMENT_ACTION = "REMOVE_SB_ATTACHMENT_ACTION";

    public static final String REMOVE_ATTACHMENT_ACTION = "REMOVE_ATTACHMENT_ACTION";
    public static final String ADD_SALESINFO_ATTACHMENT_ACTION = "ADD_SALESINFO_ATTACHMENT_ACTION";

    public static final String APPLY_PARTNER_DISCOUNT_ACTION = "APPLY_PARTNER_DISCOUNT";

    //price band override
    public static final String APPLY_PRC_BAND_OVRRD = "APPLY_PRC_BAND_OVRRD";

    public static final String DISPLAY_ASSGN_CRT_AGRMNT_ACTION = "DISPLAY_ASSGN_CRT_AGRMNT_ACTION";
    public static final String ASSGN_CRT_AGRMNT_ACTION = "ASSGN_CRT_AGRMNT_ACTION";
    public static final String SUBMIT_QUOTE_SUBMISSION_FOR_OUT_CHG = "SUBMIT_QUOTE_SUBMISSION_FOR_OUT_CHG";
    public static final String ADD_OR_UPDATE_CONFIGRTN = "ADD_OR_UPDATE_CONFIGRTN";
    public static final String CONFIG_HOSTED_SERVICE = "CONFIG_HOSTED_SERVICE";

    public static final String DISPLAY_MIGRATE_PART_LIST = "DISPLAY_MIGRATE_PART_LIST";
    public static final String ADD_MIGRATE_PART = "ADD_MIGRATE_PART";

    public static final String DISPLAY_FCT2PA_CUST_PARTNER = "DISPLAY_FCT2PA_CUST_PARTNER";
    public static final String POST_DISPLAY_FCT2PA_CUST_PARTNER = "POST_DISPLAY_FCT2PA_CUST_PARTNER";
    public static final String SUBMIT_FCTTOPA_MIGRATION_REQUEST = "SUBMIT_FCTTOPA_MIGRATION_REQUEST";

    public static final String RETRIEVE_PART_FROM_COGNOS = "RETRIEVE_PART_FROM_COGNOS";

    public static final String UPDATE_QT_EXP_DATE = "UPDATE_QT_EXP_DATE";

    public static final String CHANGE_BILLING_FREQUENCY_ACTION = "CHANGE_BILLING_FREQUENCY_ACTION";

    //13.4 10.6B extended entire configuration end date
    public static final String ADD_ETR_PARTS_TO_SQ="ADD_ETR_PARTS_TO_SQ";
    
    public static final String DISPLAY_OMITTED_LINEITEM="DISPLAY_OMITTED_LINEITEM";
    
    public static final String DISPLAY_SUBMITTED_OMITTED_LINEITEM = "DISPLAY_SUBMITTED_OMITTED_LINEITEM";
    
    public static final String CREATE_OMIT_RENEWAL_LINE="CREATE_OMIT_RENEWAL_LINE";
    
    public static final String SET_LINE_TO_RSVP_SRP="SET_LINE_TO_RSVP_SRP";
    
    public static final String LINE_ITEM_ADDRESS_DETAILS = "LINE_ITEM_ADDRESS_DETAILS";
    
    public static final String RECALCULATE_GROWTH_DELEGATION = "RECALCULATE_GROWTH_DELEGATION";
    
    public static final String ADD_TOU_AMENDMENT_ACTION = "ADD_TOU_AMENDMENT_ACTION";
    
    public static final String DOWNLOAD_ATTACHMENT = "DOWNLOAD_ATTACHMENT";
    
    public static final String REMOVE_TOU_AMENDMENT_ACTION = "REMOVE_TOU_AMENDMENT_ACTION";
    
    public static final String VALIDATE_CREATE_DEPLOYMENT_ID_AJAX = "VALIDATE_CREATE_DEPLOYMENT_ID_AJAX";
    
    public static final String GET_APPLIANCE_DEPLOYMENT_ID_AJAX = "GET_APPLIANCE_DEPLOYMENT_ID_AJAX";

    //monthly
    public static final String REMOVE_ALL_PARTS="REMOVE_MONTHLY_SW_CONFIGURATION";

    public static final String BUILD_MONTHLY_SW_CONFIGUATOR = "BUILD_MONTHLY_SW_CONFIGUATOR";
    
    public static final String SUBMIT_MONTHLY_SW_CONFIGUATOR= "SUBMIT_MONTHLY_SW_CONFIGUATOR";
    
    public static final String SEARCH_RESTRICTED_PARTS_FOR_CONFIGURATOR="SEARCH_RESTRICTED_PARTS_FOR_CONFIGURATOR";

}