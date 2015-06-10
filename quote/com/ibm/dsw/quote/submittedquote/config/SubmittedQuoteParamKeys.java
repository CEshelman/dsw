package com.ibm.dsw.quote.submittedquote.config;

import com.ibm.dsw.quote.base.config.ParamKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteParamKeys<code> class .
 *    
 * @author: doris_yuen@us.ibm.com
 * Creation date: May 2, 2007
 */
public interface SubmittedQuoteParamKeys extends ParamKeys {
    
    // Submitted quote common header and action buttons 
    public static final String PARAM_ADDI_SPEC_BID_JUST = "addiSpecBidJust";
    public static final String PARAM_SPEC_BID_JUST_FILE_0 = "sbSupportFile_0";
    public static final String PARAM_SPEC_BID_JUST_FILE_1 = "sbSupportFile_1";
    public static final String PARAM_SPEC_BID_JUST_FILE_2 = "sbSupportFile_2";
    public static final String PARAM_SPEC_BID_JUST_FILE_3 = "sbSupportFile_3";
    public static final String PARAM_SPEC_BID_JUST_FILE_4 = "sbSupportFile_4";
    public static final String PARAM_SPEC_BID_JUST_FILE_5 = "sbSupportFile_5";
    public static final String PARAM_SPEC_BID_JUST_FILE_6 = "sbSupportFile_6";
    public static final String PARAM_SPEC_BID_JUST_FILE_7 = "sbSupportFile_7";
    public static final String PARAM_SPEC_BID_JUST_FILE_8 = "sbSupportFile_8";
    public static final String PARAM_SPEC_BID_JUST_FILE_9 = "sbSupportFile_9";
    public static final String PARAM_SPEC_BID_REVIEW_COMMT = "reviewComments";
    public static final String PARAM_SB_REV_COM_FILE_0 = "reviewCommentSupFile_0";
    public static final String PARAM_SB_REV_COM_FILE_1 = "reviewCommentSupFile_1";
    public static final String PARAM_SB_REV_COM_FILE_2 = "reviewCommentSupFile_2";
    public static final String PARAM_SB_AA_APPROVE = "apprActAppr";
    public static final String PARAM_SB_AA_RET_ADDI_INFO = "apprActRetForAddiInfo";
    public static final String PARAM_SB_AA_RETURN_CHANGE = "apprActRetForChg";
    public static final String PARAM_SB_AA_REJECT = "apprActReject";
    
    public static final String PARAM_SB_APPROVER_CMMT = "approverComments";
    public static final String PARAM_APPRVR_ACTION = "apprvrAction";
    public static final String PARAM_SB_CMMT = "specialBidComment";
    public static final String PARAM_APPRVR_RETURN_REASON = "returnReason";
    
    public static final String LINE_ITEM_DETAIL_FALG = "lineItemDetailFlag";
    
    //copy/update submitted quote params
	public static final String PARAM_COPY_UPDATE_FLAG = "copyUpdateFlag";
	public static final String PARAM_WEB_REF_FLAG = "includeRef";
    public static final String quantitySuffix = "_QTY";

    public static final String ovrdDtStartDaySuffix = "_OVRD_DT_START_DAY";
    
    public static final String itemPointSuffix = "_UNIT_PTS";

    public static final String overridePriceSuffix = "_OVERRIDE_UNIT_PRC";

    public static final String discountPriceSuffix = "_DISC_PCT";

    public static final String partNumberSuffix = "_PART_NUM";

    public static final String ovrdDtStartMonthSuffix = "_OVRD_DT_START_MONTH";
    public static final String PARAM_ACTION_HISTORIES = "action_histories";
    public static final String PARAM_APPROVERS = "approvers";

    public static final String totalPointsSuffix = "_EXT_PTS";

    public static final String ovrdDtStartYearSuffix = "_OVRD_DT_START_YEAR";

    public static final String ovrdDtEndDaySuffix = "_OVRD_DT_END_DAY";

    public static final String ovrdDtEndMonthSuffix = "_OVRD_DT_END_MONTH";

    public static final String ovrdDtEndYearSuffix = "_OVRD_DT_END_YEAR";
    
    public static final String ovrdDtStartButtonSuffix = "_OVRD_DT_START_BUTTON";

    public static final String ovrdDtEndButtonSuffix = "_OVRD_DT_END_BUTTON";
    
    public static final String PARAM_MASS_DLG_LIST = "massDlgtnList";
    
    public static final String PARAM_QUOTE_NUM = "quoteNum";
    
    public static final String PARAM_RNWL_QT_NUM = "renewalQuoteNum";
    
    public static final String HAS_ACTIVE_DRAFT_QUOTE = "hasActiveDraftQuote";
    
    public static final String PARAM_ATTACHMENT = "attachment";
    public static final String PARAM_MIME_TYPE = "mimeType";
    public static final String PARAM_FILE_NAME = "fileName";
    public static final String WORKFLOW_DETAIL_ORDER = "WORKFLOW_DETAIL_ORDER";
    public static final String WORKFLOW_DETAIL_QUOTE = "WORKFLOW_DETAIL_QUOTE";
    public static final String SPECIAL_BID_COMMON = "SPECIAL_BID_COMMON";
   
    public static final String PARAM_REVIEWER_ID = "reviewerID";
    
    public static final String REQUESTED_ACTION="requestedAction";
    public static final String PARAM_USER_ROLE = "userRole";
    
    public static final String UPDATE_LINE_ITEM_DATE_FLAG = "UPDATE_LINE_ITEM_DATE_FLAG";
    
    public static final String PARAM_WEB_QUOTE_NUM = "webQuoteNum";
    
    public static final String LI_DETAIL_FLAG = "lineItemDetailFlag";
    
    public static final String BR_DETAIL_FLAG = "brandDetailFlag";
    
    public static final String PARAM_RQ_DETAIL_SALES_COMMENTS = "RQDetailComments";
    public static final String PARAM_ATTACHMENT_LIST = "attachmentList";
    
    //exec summary
    public static final String EXEC_RECMD_FLAG = "recmdtFlag";
    public static final String EXEC_RECMD_TXT = "apprRecmd";
    public static final String EXEC_PERIOD_BOOKABLE_REVENUE = "periodBookableRevenue";
    public static final String EXEC_SERVICE_REVENUE = "serviceRevenue";
    public static final String EXEC_TERM_CONDITIONS = "termConditions";
    public static final String EXEC_SUPPORT = "execSupport";
    public static final String EXEC_BRIEF_OVERVIEW = "briefOverview";
    
    //status detail explanation
    public static final String STATUS_DETAIL_EXPLANATION = "statusDetailExplanation";
    
    //Access to SAP outputs from SQO
    public static final String QUOTE_OUTPUT = "quoteOutput";
    public static final String DOWNLOAD_TYPE = "downloadType";
    public static final String SAP_DOC_ID = "sapDocId";
    public static final String UNSIGNED_TNC_ITEM = "UNSIGNED_TNC_ITEM";
    
    //Get the Customer related docements info
    public static final String CUSTOMER_RELATED_DOCUMENTS = "customerRelatedDocuments";
    
    public static final String QUOTE_PRECK_STATUS = "quotePreCheckStatus";
    
    public static final String PARAM_HAS_CTRLD_PART = "hasCtrldPart";

    public static final String REVN_STRM_CODE_SUFFIX = "_revnStrmCode";
    
    public static final String APPLY_OFFER_PRICE_FLAG = "applyOfferPriceFlag";
    
    public static final String PARAM_DISPLAY_ACTION = "displayAction";
    
    public static final String PARAM_SI_BRIEF_TITLE = "quoteTitle";
    
    public static final String ORDER_NUM = "orderNum";
    
    public static final String PARAM_PO = "P0";
    
    public static final String PARAM_ISORDERNOW = "isOrderNow";
    public static final String PARAM_HASSAAS = "hasSaas";
    
    public static final String PARAM_SAP_QUOTE_NUM = "quote";
    
    public static final String PARAM_DEST = "dest";
    
    public static final String PARAM_ORDERED_ITEM_DETAIL = "PARAM_ORDERED_ITEM_DETAIL";
    public static final String PARAM_DEST_SEQ_NUM = "destSeqNum";
    public static final String PARAM_DISPLAY_TAB_URL = "displayTabUrl";
    
    //15.2 expiration date extension,add a parameter to indicate if update ebiz.WEB_QUOTE saved_quote_flag = 1
    public static final String PARAM_UPDATE_SAVED_QUOTE_FLAG = "updateSavedQuoteFlag";
    
    
    public static final String PARAM_REDIRECT_URL="redirectURL";
}