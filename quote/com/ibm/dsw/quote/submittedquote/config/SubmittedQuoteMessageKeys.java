package com.ibm.dsw.quote.submittedquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SubmittedQuoteMessageKeys</code> class is to define the message
 * keys for submitted quote.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 16, 2007
 */
public interface SubmittedQuoteMessageKeys {
    public static final String MSG_UPDATE_OPPRINFO_SUCCESSFUL = "update_opp_info_successful";
    public static final String MSG_RQ_NOT_EDITABLE= "rq_not_editable";
    public static final String MSG_CANCEL_FAILED = "qt_cancel_failed";
    public static final String MSG_CANCEL_SUCCESS = "qt_cancel_success";
    public static final String MSG_CANCEL_SB_SUCCESS = "qt_cancel_sb_success";
    public static final String MSG_REQUEST_ICN_PRECREDIT_SUCCESS = "qt_request_icn_precredit_success";
    public static final String MSG_COMMENT_OR_ATTACHMENTS_REQUIRED = "comment_or_attachments_required";
    public static final String MSG_APPROVER_OF_LEVEL = "approver_of_level";
    public static final String MSG_APPROVER_NOTIN_BP = "msg_approver_notin_bp";
    public static final String MSG_REVIEW_REQUEST_SENT = "review_request_sent";
    public static final String MSG_APPROVE_REQUEST_SENT = "approval_request_sent";
    public static final String MSG_REJECT_REQUEST_SENT = "approval_reject_request_sent";
    public static final String MSG_RETURN_FOR_CHANGES_REQUEST_SENT = "return_for_change_request_sent";
    public static final String MSG_RETURN_FOR_ADD_INFO_REQUEST_SENT = "return_for_add_info_request_sent";
    public static final String MSG_SAVE_AND_NOTIFY_REQUEST_SENT = "save_and_notify_request_sent";
    public static final String MSG_SAVE_DRAFT_REQUEST_SENT = "save_draft_request_sent";
    public static final String MSG_EXISTED_REVIEWER = "existed_reviewer";
    public static final String MSG_MAIL_SEND_ERROR = "mail_send_error";
    public static final String MSG_FILE_UPLOAD_SUCCESS = "files_upload_success";
    public static final String MSG_SAP_NOT_AVAILABLE = "msg_sap_not_available";
    public static final String MSG_UPDATE_APPROVER_SELECTION_SUCCESSFULLY = "update_approver_selection_successfully";
    public static final String MSG_TXT_WAIT_PROMPT = "txt_wait_prompt";
    public static final String MSG_WORKFLOW_NOT_AVAILABLE = "msg_workflow_not_available";
    public static final String MSG_FILES_UPLOAD_FAILED = "msg_files_upload_failed";
    public static final String MSG_CM_NOT_AVAILABLE = "msg_cm_not_available";
    
    public static final String MSG_OVRRD_DATE_NOT_VALID = "submitted_ovrrd_date_not_valid_msg";
    public static final String MSG_OVRRD_DURATION_NOT_VALID = "submitted_ovrrd_duration_not_valid_msg";
    public static final String MSG_OVRRD_FTL_DURATION_NOT_VALID = "submitted_ovrrd_ftl_duration_not_valid_msg";
    public static final String MSG_QUOTE_UPDATE_FAILED = "submitted_quote_update_failed";
    public static final String MSG_SB_JUSTIFACTION_UPDATED_SUCCESSFULLY = "special_bid_justifacation_updated_successfully";
    public static final String MSG_UNKNOW_ERR = "msg_unknown_err";
    public static final String MSG_MIGRATE_RFC_ERROR1 = "migrate_create_RFC_error_message1";
    public static final String MSG_MIGRATE_RFC_ERROR2 = "migrate_create_RFC_error_message2";   
    public static final String MSG_BLUE_PAGE_UNAVAILABLE = "blue_page_unavailable_msg";
    public static final String MSG_RSEL_NOT_AUTH_TO_PORT = "rsel_not_auth_to_port_msg";
    public static final String MSG_DIST_NOT_ASSOC_TO_RSEL = "dist_not_assoc_to_rsel_msg";
    public static final String MSG_RSEL_NOT_AUTH_TO_SELL = "rsel_not_auth_to_sell_msg";
    public static final String MSG_WRNG_RSEL_DSTRBTR_BMBNTN = "wrng_rsel_dstrbtr_cmbntn_msg";
    public static final String MSG_PRTNR_UPDT_SUCC = "prtnr_updt_succ_msg";
    public static final String MSG_REQUIRE_WHEN_SAVE_AND_NOTIFY = "require_when_save_and_notify";
    public static final String MSG_OVRRD_START_DATE_NOT_VALID = "ovrrd_start_date_not_valid_msg";
    
    //added on 10/07/2008 for back dating
    public static final String MSG_NO_FUTURE_MAINT_START_DATE = "no_future_maint_start_date";
    public static final String MSG_BACK_DTG_EXCEEDS_RANGE = "back_dtg_exceeds_range";
    public static final String MSG_START_DATE_BACK_DTG_NOT_ALLOWED = "start_date_back_dtg_not_allowed";
    public static final String MSG_END_DATE_BACK_DTG_NOT_ALLOWED = "end_date_back_dtg_not_allowed";
    public static final String RESUBMIT_APPROVE_ACTION = "msg_resubmit_approve_action";
    public static final String NO_PRIVILEGE_PERFORM = "msg_no_privilege_perform";
    public static final String SUBMITER_SAME_APPRVR = "msg_submiter_same_apprvr";
    
    // added for partprice.jsp
    public static final String  QUANTITY_NUMBER_WITHOUT_DECIMALS = "quantity_number_without_decimals";
    public static final String  START_DATE_EARLIER_THAN_END_DATE = "start_date_earlier_than_end_date";
    public static final String  DURATION_SHORTER_ONE_WEEK_OR_LONGER_THAN_ONE_YEAR = "duration_shorter_one_week_or_longer_than_one_year";
    public static final String  DURATION_SHORTER_ONE_MONTH_OR_LONGER_THAN_ONE_YEAR = "duration_shorter_one_month_or_longer_than_one_year";
    public static final String  DATE_INVALID_MSG = "date_invalid_msg";
    public static final String  BOTH_DATES_INVALID_MSG = "both_dates_invalid_msg";
    //For cmprss cvrage
    public static final String  CMPRSS_CVRAGE_SHORTER_THAN_ONE_YEAR = "cmprss_cvrage_shorter_than_one_year";
    
    //added for exec summary
    public static final String EXEC_SUMMARY_RECMD_FLAG_REQUIRED = "exec_summary_recmd_flag_required";
    public static final String VALUE_MUST_BE_NUMERIC = "value_must_be_numeric";
    
    public static final String NOT_CLASSIFIED = "not_classfd";
    public static final String TERMINATED_BY = "terminated_by";
    
    //added for status explanation popup
    public static final String CREATION_IN_PROGRESS = "creation_in_progress";
    public static final String CREATION_ON_HOLD = "creation_on_hold";
    
    public static final String EOL_PARTS_MESSAGE = "eol_parts_message";
    public static final String INVLD_QT_DATA_MSG = "invld_qt_data_msg";
    public static final String STATUS_TAB_NO_CUST_ENROLL_MSG = "status_tab_no_cust_enroll_msg";
    public static final String STATUS_TAB_NO_CUST_ENROLL_MSG_2 = "status_tab_no_cust_enroll_msg2";
    public static final String STATUS_TAB_NO_CUST_ENROLL_MSG_3 = "status_tab_no_cust_enroll_msg3";
    public static final String STATUS_TAB_NO_CUST_ENROLL_MSG_4 = "status_tab_no_cust_enroll_msg4";
    public static final String STATUS_TAB_NO_CUST_ENROLL_MSG_5 = "status_tab_no_cust_enroll_msg5";

	//Access to SAP outputs from SQO
	public static final String QUOTE_OUTPUT_OUTPUT_NAME_CQPDF = "quote_output_docname_custqt_pdf"; 
	public static final String QUOTE_OUTPUT_OUTPUT_NAME_CSBNPDF = "quote_output_docname_spbnoti_pdf";
	public static final String QUOTE_OUTPUT_OUTPUT_NAME_BQPDF = "quote_output_docname_budgetary_pdf";
	
	//Get customer related documents info
	public static final String QUOTE_CUST_RELATED_DOC_NEW_CUST_MAIL = "quote_cust_related_doc_new_cust_mail"; 
	public static final String QUOTE_CUST_RELATED_DOC_DISTRIBUTED_TO = "quote_cust_related_doc_distributed_to"; 
	
	//added for increase price after s.b approved
	public static final String OFFER_PRICE_INVALID = "offer_price_invalid";
	public static final String OFFER_PRICE_INCREASE_FAILED = "offer_price_increase_failed";
	public static final String MATCH_OFFER_PRICE_FAILED = "match_offer_price_failed";
	
	//added for part becomes obsolete after quote submitted
	public static final String PART_OBSOLETE_AFTER_SUBMIT = "part_obsolete_after_submit";
	
	public static final String APPRVL_COMMENTS_NULL = "msg_apprvl_comments_null";
	
	public static final String AVAILABLE_PRIOR_PRICE_FROM_EVOLVED_PART="available_prior_price_from_evolved_part";
	public static final String PRIOR_PRICE_COULD_NOT_BE_CALCULATED="prior_price_could_not_be_calculated";
	public static final String PRICE_FROM_A_CHANNEL_ORDER="price_from_a_channel_order";
	
	public static final String MSG_MAIL_SEND_FAIL = "msg_mail_send_fail";
	
	public static final String ORDERED_ITEM_DETAILS_PAGE_TITLE = "ordered_item_details_page_title";
	public static final String ORDERED_ITEM_DETAILS_FOR = "ordered_item_details_for";
	public static final String ORDERED_ITEM_WEBQUOTENUM = "ordered_item_webQuoteNum";
	public static final String ORDERED_ITEM_DESTSEQNUM = "ordered_item_destSeqNum";
	public static final String ORDERED_ITEM_ORDER_LINE_ITEM = "ordered_item_order_line_item";
	public static final String ORDERED_ITEM_MACHINE_TYPE = "ordered_item_machine_type";
	public static final String ORDERED_ITEM_MODEL = "ordered_item_model";
	public static final String ORDERED_ITEM_SERIAL = "ordered_item_serial";
	public static final String ORDERED_ITEM_CCAD_DATE = "ordered_item_ccad_date";
	public static final String ORDERED_ITEM_ORDER_STATUS = "ordered_item_order_status";
	//Message indicating CRAD updates took place
    public static final String QT_UP_LINE_ITEM_CHG_SUCCESS_MSG = "qt_up_line_item_chg_success_msg";
    public static final String QT_CRAD_SUCCESS_MSG = "qt_crad_success_msg";
    public static final String APPLNC_MTM_VALIDATE_SUCCESS = "applnc_mtm_validate_success";

}
