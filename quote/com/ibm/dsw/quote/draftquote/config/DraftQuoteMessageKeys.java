package com.ibm.dsw.quote.draftquote.config;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>DraftQuoteMessageKeys</code>
 *
 *
 * @author: xiuliw@cn.ibm.com
 *
 * Creation date: 2007-3-23
 */
public interface DraftQuoteMessageKeys {


	public static final String QUOTE_DISCOUNT_PERCENT_MSG = "quote_discount_percent_msg";
	public static final String QUOTE_DISCOUNT_PERCENT_POSITIVE_MSG = "quote_discount_percent_positive_msg";
	public static final String QUOTE_DISCOUNT_PERCENT_NULL_MSG = "quote_discount_percent_null_msg";
	public static final String QUOTE_DISCOUNT_PERCENT_RANGE_MSG = "quote_discount_percent_range_msg";
	public static final String PARTNER_DISCOUNT_PERCENT_MSG = "partner_discount_percent_msg";
	public static final String PARTNER_DISCOUNT_PERCENT_POSITIVE_MSG = "partner_discount_percent_positive_msg";
	public static final String PARTNER_DISCOUNT_PERCENT_NULL_MSG = "partner_discount_percent_null_msg";
	public static final String PARTNER_DISCOUNT_PERCENT_RANGE_MSG = "partner_discount_percent_range_msg";
	public static final String QUANTITY_MSG = "quantity_msg";
	public static final String NO_FUTURE_MAINT_START_DATE = "no_future_maint_start_date";
	//back dating
	public static final String MSG_BACK_DTG_NO_REASON = "msg_back_dtg_no_reason";
	public static final String MSG_SELECT_BACK_DTG_REASON = "msg_select_back_dtg_reason";
	public static final String MSG_BACK_DTG_COMMENT_REQUIRED = "msg_back_dtg_comment_required";
	public static final String MSG_BACK_DTG_COMMENT_EXCEED_LIMIT = "msg_back_dtg_comment_exceed_limit";

	public static final String OVERRIDE_PRICE_MSG = "override_price_msg";
	public static final String OVERRIDE_PRICE_POSITIVE_MSG = "override_price_positive_msg";
	public static final String YTY_MISSING_MSG = "yty_missing_msg";
	public static final String DISCOUNT_PERCENT_MSG = "discount_percent_msg";
	public static final String DISCOUNT_PERCENT_POSITIVE_MSG = "discount_percent_positive_msg";
	public static final String DISCOUNT_PERCENT_POSITIVE_MSG_YTY = "discount_percent_positive_msg";
	public static final String DISCOUNT_PERCENT_RANGE_MSG = "discount_percent_range_msg";
	public static final String CLEAR_CURRENT_OFFER_MSG = "clear_current_offer_msg";
	public static final String PRICE_POSITIVE_MSG = "price_positive_msg";
	public static final String RQ_OVRD_DATE_DURATION_MSG = "rq_ovrrd_date_duration_msg";
	public static final String RQ_OVRD_INVALID_START_DATE_MSG = "rq_ovrrd_invalid_start_date";
	public static final String RQ_OVRD_INVALID_END_DATE_MSG = "rq_ovrrd_invalid_end_date";
	public static final String NO_OVRD_UNIT_PRICE_ALLOWED_MSG = "no_ovrd_unit_price_allowed";


    public static final String DEAFULT_OPTION = "default_option";
	public static final String ITEM_POINTS_MSG = "item_points_msg";
	public static final String ITEM_PRICE_MSG = "item_price_msg";
	public static final String TOTAL_POINTS_MSG = "total_points_msg";
	public static final String TOTAL_PRICE_MSG = "total_price_msg";
	public static final String MSG_ENTER_VALID_EXP_DATE = "enter_valid_exp_date";
	public static final String MSG_ENTER_VALID_START_DATE = "enter_valid_start_date";
	public static final String MSG_ENTER_VALID_START_DATE_FOR_CRAD = "enter_valid_start_date_for_crad";
	public static final String MSG_ENTER_VALID_ESTMTD_ORD_DATE = "enter_valid_estmtd_ord_date";
	public static final String MSG_ENTER_VALID_CUST_REQSTD_ARRIVL_DATE = "enter_valid_cust_reqstd_arrivl_date";
	public static final String MSG_ENTER_VALID_LINEITEM_CUST_REQSTD_ARRIVL_DATE = "enter_valid_lineItem_cust_reqstd_arrivl_date";
	public static final String MSG_INVALID_CUST_REQSTD_ARRIVL_DATE = "invalid_line_item_crad";
	public static final String MSG_INVALID_CUST_REQSTD_ARRIVL_DATE_ = "invalid_cust_reqstd_arrivl_date";
	public static final String MSG_EXP_DATE_BEYOND_LIMIT = "exp_date_beyond_limit";
	public static final String MSG_ENTER_QT_TITLE = "enter_quote_title_msg";
	public static final String MSG_BRIEF_TITLE_TOO_LONG = "brief_title_too_long_msg";
	public static final String BRIEF_TITLE = "brief_title";
	public static final String MSG_QUOTE_DESC_TOO_LONG = "desc_too_long_msg";
	public static final String FULL_DESC = "full_desc";
	public static final String UPLOAD_WARNING_INVALID_CUST = "upload_warning_invalid_customer";
	public static final String UPLOAD_WARNING_INVALID_CTRCT = "upload_warning_invalid_contract";
	public static final String UPLOAD_WARNING_INVALID_PARTS = "upload_warning_invalid_parts";
	public static final String UPLOAD_WARNING_INVALID_OCS_PARTS = "upload_warning_invalid_ocs_parts";
	public static final String UPLOAD_WARNING_INVALID_FUTURE_DATE = "upload_warning_invalid_future_date";
	public static final String UPLOAD_WARNING_INVALID_SAAS_PARTS = "upload_warning_invalid_saas_parts";
	public static final String UPLOAD_WARNING_INVALID_SW_INCLUDING_SAAS_PARTS = "upload_warning_invalid_sw_including_saas_parts";
	public static final String UPLOAD_WARNING_INVALID_SAAS_INCLUDING_SW_PARTS = "upload_warning_invalid_saas_including_sw_parts";
	public static final String UPLOAD_WARNING_INVALID_FREQUENCY_PARTS = "upload_warning_invalid_frequency_parts";
	public static final String UPLOAD_WARNING_INCLUDING_SAAS_PARTS = "upload_warning_including_saas_parts";
	public static final String UPLOAD_WARNING_INCLUDING_SW_PARTS_PGS = "upload_warning_including_sw_parts_pgs";
	public static final String UPLOAD_WARNING_DUPLICATE_SAAS_PARTS = "upload_warning_duplicate_saas_parts";
	public static final String UPLOAD_WARNING_INACTIVE_SAAS_PARTS = "upload_warning_inactive_saas_parts";
	public static final String UPLOAD_WARNING_UNCERTIFIED_SAAS_PARTS = "upload_warning_uncertified_saas_parts";
	public static final String SELECT_PARTNER_ACCESS = "select_partner_access";
	public static final String SELECT_PARTNER_ACCESS_YES = "select_partner_access_yes";
	public static final String SELECT_PARTNER_ACCESS_NO = "select_partner_access_no";
	public static final String MSG_CAN_NOT_CONVERT = "msg_can_not_convert";
	public static final String MSG_CAN_NOT_CONVERT_SB = "msg_can_not_convert_sb";
    public static final String MSG_SALES_COMMENTS_TOO_LONG = "sales_comments_too_long";
    public static final String SALES_COMMENTS = "sales_comments";
	public static final String UPLOAD_WARNING_MAX_PART_QTY = "upload_warning_max_part_qty";
	public static final String MSG_LOCKED_QUOTE = "locked_quote_msg";
	public static final String MSG_LOCKED_QUOTE_HDR = "locked_quote_msg_hdr";
	public static final String MSG_OVERWRITTEN_QUOTE = "overwritten_quote_msg";
	public static final String SAVE_DRAFT_QUOTE_SUCCESS_MSG = "save_draft_quote_success_msg";
	public static final String MSG_ENTER_VALID_PYM_TERMS = "enter_valid_pym_terms";
	public static final String CREATE_QT_INVALID_PARTS_WARNING = "create_qt_invalid_parts_warning";
	public static final String OPP_NUM = "opp_num";
	public static final String MSG_ENTER_VALID_OPP_NUM = "enter_valid_opp_num";
	public static final String OPP_CANNOT_VALIDATE_FOR_EIW = "opp_cannot_validate_for_eiw";

    public static final String ORDER_FAILED_MSG = "order_failed_msg";
    public static final String SUBMIT_FAILED_MSG = "submit_failed_msg";
    public static final String MIGRATION_FAILED_MSG = "migration_failed_msg";
    public static final String SPEC_BID_INVALID_MSG = "spec_bid_invalid_msg";
    public static final String MSG_PRICE_ENGINE_UNAVAILABLE_WHEN_SUBMIT = "msg_price_engine_unavailable_when_submit";
    public static final String RULE_ENGINE_NOT_AVAILABLE = "msg_rule_engine_not_available";
    public static final String EXP_DATE_INVALID_MSG = "exp_date_invalid_msg";
    public static final String CUST_NOT_SELECT_MSG = "cust_not_selected_msg";
    public static final String CTRCT_INACTIVE_MSG = "ctrct_inactive_msg";
    public static final String ENTER_CTRCT_MSG = "enter_contract_msg";
    public static final String FULFILL_SRC_INVALID_MSG = "fulfill_src_invalid_msg";
    public static final String PART_QTY_INVALID_MSG = "part_qty_invalid_msg";
    public static final String PVU_PART_QTY_INVALID_MSG = "pvu_part_qty_invalid_msg";
    public static final String PART_NOT_ACTIVE_MSG = "part_not_active_msg";
    public static final String PART_NO_PRICE_MSG = "part_no_price_msg";
    public static final String ELA_HAS_LINE_ITEMS_MSG = "ela_has_line_items_msg";
    public static final String ENTER_BRF_TITLE_MSG = "enter_brf_title_msg";
    public static final String ENTER_OPP_EXMP_CODE_MSG = "enter_opp_exmp_code_msg";
    public static final String SELECT_BUZ_ORG_MSG = "select_buz_org_msg";
    public static final String SELECT_FULFILL_SRC_MSG = "select_fulfill_src_msg";
    public static final String SELECT_RESELLER_MSG = "select_reseller_msg";
    public static final String SELECT_RESELLER_FOR_PA_PAE_MSG = "select_reseller_for_pa_pae_msg";
    public static final String SELECT_DISTRIBUTOR_MSG = "select_distributor_msg";
    public static final String SELECT_DISTRIBUTOR_FOR_PA_PAE_MSG = "select_distributor_for_pa_pae_msg";
    //public static final String MSG_ENTER_FINATE_RATE = "msg_enter_finance_rate";
    public static final String MSG_ENTER_PROG_RBD = "msg_enter_prog_rbd";
    public static final String MSG_ENTER_INCR_RBD = "msg_enter_incr_rbd";
    public static final String MSG_ENTER_QUOTE_NUM = "msg_enter_quote_num";
    public static final String MSG_ENTER_SALES_PLAY_NUM = "msg_enter_sales_play_num";
    public static final String RESELLER_SHOULD_NOT_TIRE1_FOR_SINGLE_TIRE_MODEL_MSG = "reseller_should_not_tire1_for_single_tire_model_msg";
    public static final String PRC_LVL_NOT_SET_MSG = "prc_lvl_not_set_msg";
    public static final String ACL_INVALID_MSG = "acl_invlid_msg";
    public static final String SELECT_QT_CLASSFCTN_CODE_MSG = "select_qt_classfctn_code_msg";
    public static final String SELECT_QT_OEM_AGREEMENT_TYPE_MSG = "select_qt_oem_agreement_type_msg";
    public static final String RQ_NOT_SPECL_BIDABLE_MSG = "rq_not_specl_bidable_msg";
    public static final String DEL_ITEM_REF_BY_SQ_MSG = "del_item_ref_by_sq_msg";
    public static final String SELECT_OEM_BID_TYPE_MSG = "select_oem_bid_type_msg";

    public static final String UPDATE_SALES_INFO = "update_sales_info";
    public static final String SPECIAL_BID_CUST_IND_SEGMENT_NOT_INPUT = "special_bid_cust_ind_not_input";
    public static final String SPECIAL_BID_DISTRICT_NOT_INPUT = "special_bid_district_not_input";
    public static final String SPECIAL_BID_EXP_TEXT_NOT_INPUT = "special_bid_exp_text_not_input";
    public static final String SPECIAL_BID_REGION_NOT_INPUT = "special_bid_region_not_input";
    public static final String SPECIAL_BID_TYPE_NOT_INPUT = "special_bid_type_not_input";
    public static final String SPECIAL_BIT_JUST_TEXT_NOT_INPUT = "special_bid_just_text_not_input";
    public static final String REASON_FOR_CHANGE_REQUIRED = "reason_for_change_required";
    public static final String REASON_FOR_DELETE_REQUIRED = "reason_for_delete_required";
    public static final String REASON_FOR_INSERT_REQUIRED = "reason_for_insert_required";
    public static final String PARTNER_ACCESS_REQUIRED = "partner_access_required";
    public static final String QUOTE_STATUS_REQUIRED = "quote_status_required";
    public static final String SALES_ODDS_REQUIRED = "sales_odds_required";

    //  Draft quote finalize keys
    public static final String BTN_BROWSE = "btn_browse";
	public static final String TITLE_FINALIZE_QUOTE = "title_finalize_quote";
	public static final String SUBTITLE_SPECIFY_INFO = "subtitle_specify_info";
	public static final String SUBHEAD_APPR_JUST = "subhead_appr_just";
	public static final String HEAD_SPEC_BID_APPRS = "head_spec_bid_apprs";
	public static final String SELT_APPR_FOR_LEVEL = "selt_appr_for_level";
	public static final String SELT_APPR_FOR_DATE_EXTENDED = "selt_appr_for_date_extended";
	public static final String SELECT_AN_APPROVER = "select_an_approver";
	public static final String ONLY_ONE_APPR_FOR_LEVEL = "only_one_appr_for_level";
	public static final String HEAD_SPEC_BID_JUST = "head_spec_bid_just";
	public static final String NOTE_SPEC_BID_ATTACHMENTS = "note_spec_bid_attachments";
	public static final String NOTE_FCT_NON_STD_TERMS_CONDS_ATTACHMENTS = "note_fct_non_std_terms_conds_attachments";
	public static final String HEAD_QUOTE_ATTACHMENTS = "head_quote_attachments";
	public static final String NOTE_QUOTE_ATTACHMENTS = "note_quote_attachments";
	public static final String IBM_INSTALLER = "ibm_installer";
	public static final String TITLE_PLS_WAIT = "title_pls_wait";
	public static final String TXT_WAIT_FOR_STORE = "txt_wait_for_store";
	public static final String TITLE_QUOTE_SUBMIT = "title_quote_submit";
	public static final String TITLE_QUOTE_SUBMIT_FOR_EVALUATION = "title_quote_submit_for_evaluation";
	public static final String SUBHEAD_SPEC_BID_CONFIRM = "subhead_spec_bid_confirm";
	public static final String TXT_HAVE_SUBMIT = "txt_have_submit";
	public static final String TXT_HAVE_SUBMIT_FOR_EVALUATION = "txt_have_submit_for_evaluation";
	public static final String TXT_APPROVER_NOTIFY = "txt_approver_notify";
	public static final String TXT_REVIEWER_NOTIFY = "txt_reviewer_notify";
	public static final String TXT_PROCESS_ONCE_APPROVED = "txt_process_once_approved";
	public static final String TXT_CHECK_STATUS = "txt_check_status";
	public static final String SUBHEAD_RESUBMIT = "subhead_resubmit";
	public static final String TXT_PROCESS_RESUBMIT_DOCS = "txt_process_resubmit_docs";
	public static final String TXT_ADD_DOC = "txt_add_doc";
	public static final String BTN_STORE_DOC = "btn_store_doc";
	public static final String MSG_UNABLE_STORE_DOC = "msg_unable_store_doc";
	public static final String MSG_SELT_APPR = "msg_selt_appr";
	public static final String MSG_FILE_UNABLE_READ = "msg_file_unable_read";
	public static final String MSG_FILE_TOO_LARGE = "msg_file_too_large";
	public static final String MSG_FILE_SIZE_ZERO = "msg_file_size_zero";
	public static final String MSG_FILE_CANNOT_BE_STORED = "msg_file_cannot_be_stored";
	public static final String MSG_FILE_UPLOAD_SUCCESS = "files_upload_success";
    public static final String MSG_FILES_UPLOAD_FAILED = "msg_files_upload_failed";
    public static final String MSG_COMMENT_OR_ATTACHMENTS_REQUIRED = "comment_or_attachments_required";
    public static final String MSG_SUBMIT_FORM_HDR = "quote_submit_form.header";
    public static final String UPSIDE_TRAN_NOT_SELECT = "upside_tran_msg";
    public static final String MSG_APPROVER_NOT_SELECTED = "approver_not_selected_msg";

    public static final String RNWL_QUOTE_INCONSISTENT_MSG = "rnwl_quote_inconsistent_msg";
    public static final String CONTACT_EMAIL_ADR_BLANK_MSG = "contact_email_adr_blank_msg";
    public static final String CONTACT_PHONE_NUM_BLANK_MSG = "contact_phone_num_blank_msg";
    public static final String CONTACT_FIRST_NAME_BLANK_MSG = "contact_first_name_blank_msg";
    public static final String CONTACT_LAST_NAME_BLANK_MSG = "contact_last_name_blank_msg";
    public static final String QUOTE_FULFILLMENT_INVALID_MSG = "quote_fulfillment_invalid_msg";
    public static final String QUOTE_SPBID_FULFILLMENT_INVALID_MSG = "quote_spbid_fulfillment_invalid_msg";
	public static final String IS_OVERRIDDEN_PRICE_LEVEL_MSG = "quote_is_overridden_price_level";
    public static final String SPEC_BID_NOT_PERMITTED_MSG = "spec_bid_not_permitted_msg";
    public static final String CUST_PTNR_CURRENCY_MISMATCH_MSG = "cust_ptnr_currency_mismatch_msg";
    public static final String CUST_PAYER_CURRENCY_MISMATCH_MSG = "cust_payer_currency_mismatch_msg";
    public static final String OFFER_PRICE_MISMATCH_MSG = "offer_price_mismatch_msg";
    public static final String CUST_PTNR_REGION_MISMATCH_MSG = "cust_ptnr_region_mismatch_msg" ;
    public static final String CUST_PTNR_COUNTRY_MISMATCH_MSG = "cust_ptnr_country_mismatch_msg";
    public static final String EXP_DATE_NOT_WITHIN_SBMT_MONTH = "exp_date_not_within_sbmt_month_msg";
    public static final String FCT_RQ_STATUS_INVALID_MSG = "fct_rq_status_invalid_msg";
    public static final String CHANNEL_MODEL = "chnl_mdl";
    public static final String CHNL_MDL_APPL_TBD_ONLY_MSG = "chnl_mdl_appl_tbd_only_msg";
    public static final String CHNL_MDL_APPL_FULFILL_CHNL_ONLY_MSG = "chnl_mdl_appl_fulfill_chnl_only_msg";

    //EOL
    public static final String ENTITLED_PRICE_MSG = "entitled_price_msg";
    public static final String ENTITLED_PRICE_POSITIVE_MSG = "entitled_price_positive_msg";
    public static final String ENTITLED_PRICE_REQUIRED = "entitled_price_required";
    public static final String OVERRIDE_PRICE_REQUIRED = "override_price_required";
    public static final String REMOVE_EOL_PART = "remove_eol_part";
    public static final String EOL_PART_BP_DISC_REQURIED = "eol_part_bp_disc_required";

    public static final String BUS_PARTNER_NOTIFICATION = "bus_partner_notification";
    public static final String MSG_Y9_PARTNER_EMAIL_NOT_SELECTED = "y9_partner_email_not_selected_msg";
    public static final String MSG_ADDI_PARTNER_EMAIL_EMPTY = "addi_partner_email_empty_msg";
    public static final String MSG_CUST_ADDI_EMAIL_INVLD = "cust_addi_email_invld";

    public static final String MSG_NOT_APPLICABLE = "not_applicable";

    //Compressed coverage
    public static final String CMPRSS_CVRAGE_DISC_POSITIVE_MSG = "cmprss_cvrage_disc_positive_msg";
    public static final String CMPRSS_CVRAGE_DISC_RANGE_MSG = "cmprss_cvrage_disc_range_msg";
    public static final String CMPRSS_CVRAGE_DISC_MSG = "cmprss_cvrage_disc_msg";

    //For maximum allowed parts limit
    public static final String PART_EXCEED_LIMIT_MSG = "part_exceed_limit";

    //For fct non standard terms and conditions
	public static final String FCT_NON_STD_TERMS_CONDS_NOT_SELECTED = "fct_non_std_terms_conds_not_selected";
	public static final String FCT_NON_STD_TERMS_CONDS_MUST_ATTACH_FILE = "fct_non_std_terms_conds_must_attach_file";

	//For saas billing options
	public static final String BILLING_OPTION_NOT_VALID = "billing_option_not_valid";
	public static final String QUANTITY_MSG_MULTIPLE = "quantity_msg_multiple";

	//For provisioning days
	public static final String PROVSNING_DAYS_MUST_MSG = "provsning_days_must_msg";
	public static final String PROVSNING_DAYS_INVLD_MSG = "provsning_days_invld_msg";


	//For appliance msg
	public static final String MACHINE_TYPE_ALPHANUMERIC_MSG = "machine_type_alphanumeric_msg";
	public static final String MACHINE_MODEL_ALPHANUMERIC_MSG = "machine_model_alphanumeric_msg";
	public static final String MACHINE_SERIAL_NUBMER_MSG = "serial_number_alphanumeric_msg";
	public static final String MACHINE_TYPE_EXCEED_LIMIT = "machine_type_exceed_limit";
	public static final String MACHINE_MODEL_EXCEED_LIMIT = "machine_model_exceed_limit";
	public static final String MACHINE_SERIAL_NUBMER_EXCEED_LIMIT = "machine_serial_number_exceed_limit";
	public static final String MACHINE_TYPE_NOT_NULL="machine_type_not_null";
	public static final String MACHINE_MODEL_NOT_NULL="machine_model_not_null";
	public static final String MACHINE_SERIAL_NUMBER_NOT_NULL="machine_serial_number_not_null";
	public static final String APPLANCE_ID_MSG = "appliance_id_not_null";
	public static final String APPLANCE_SERIAL_NUMBER_NOT_SAME = "appliance_serial_number_not_same";
	public static final String LINE_ITEM_CRAD_MSG = "lineItem_crad_not_null";
	public static final String LEGACY_PART_QUANTITY = "legacy_Part_Quantity";
	
	
	//For FCT to PA migration
    public static final String MSG_BILLING_FREQUENCE_NOT_VALID1= "billing_frequence_not_valid1";
    public static final String MSG_BILLING_FREQUENCE_NOT_VALID2= "billing_frequence_not_valid2";
    public static final String MSG_BILLING_FREQUENCE ="billing_frequence";
    
    //For warning messages for retrieve parts from Cognos
    public static final String COGNOS_WARNING_INVALID_PARTS= "cognos_warning_invalid_parts";
    public static final String COGNOS_WARNING_EXCEED_PARTS= "cognos_warning_exceed_parts";
    public static final String COGNOS_WARNING_PARAMS_NOT_MATCH= "cognos_warning_params_not_match";
    public static final String COGNOS_WARNING_NO_PARTS_RETRIEVED= "cognos_warning_no_parts_retrieved";
    public static final String COGNOS_WARNING_WEBSERVICE_ERROR="cognos_warning_webservice_error";
    
    //For YTY growth delegation
    public static final String QUOTE_YTY_PERCENT_NULL_MSG = "quote_yty_percent_null_msg";
    public static final String QUOTE_YTY_PERCENT_POSITIVE_MSG = "quote_yty_percent_positive_msg";
    public static final String QUOTE_YTY_PERCENT_RANGE_MSG = "quote_yty_percent_range_msg";
    public static final String QUOTE_YTY_PERCENT_MSG = "quote_yty_percent_msg";
    public static final String PRIOR_SS_CHANGED_MSG = "prior_ss_changed_msg";
    
    
    public static final String NO_EVALUATOR_SET_UP_FOR_THIS_COUNTRY_IN_PGS="no_evaluator_set_up_for_this_country_in_PGS";
    
    public static final String MSG_EXCEED_RSVP_PRICE = "msg_exceed_rsvp_price";
    public static final String MSG_EVALUATOR_RETURN_QUOTE_TO_BP = "msg_eval_return_quote_to_bp";
    public static final String TERM_RENEWAL_MODEL_INVLD_MSG = "term_renewal_model_invld_msg";
    public static final String TERM_EXTENSION_DATE_INVLD_MSG = "term_extension_date_invld_msg";
    public static final String EXTENSION_TYPE_VALID_MESSAGE = "extension_type_valid_message";
    public static final String RAMPUP_SERVICE_END_DATE_INVLD_MSG = "rampup_service_end_date_invld_msg";
    public static final String END_DATE_LATER_THAN_EXISTING_MSG="end_date_later_than_existing_msg";

    public static final String IMPLIED_GROWTH = "implied_growth";
    
    public static final String PRIOR_SS_SITE_NUMBERS_BE_SEVEN_TO_TEN_CHARACTERS = "prior_ss_site_numbers_be_seven_to_ten_characters";
    public static final String PRIOR_SS_SITE_NUMBERS_CONTAIN_SEVEN_TO_TEN_CHARACTERS = "prior_ss_site_numbers_contain_seven_to_ten_characters";
    public static final String PRIOR_SS_SITE_NUMBERS_EXCEEDS_LENGTH = "prior_ss_site_numbers_exceeds_length";
    public static final String PRIOR_SS_PART_NUMBERS_CONTAIN_SEVEN_CHARACTERS = "prior_ss_part_numbers_contain_seven_characters";
    public static final String PRIOR_SS_PART_NUMBERS_BE_SEVEN_CHARACTERS = "prior_ss_part_numbers_be_seven_characters";
    public static final String PRIOR_SS_PART_NUMBERS_EXCEEDS_LENGTH = "prior_ss_part_numbers_exceeds_length";    
    public static final String PRIOR_SS_PART_QUANTITY_POSITIVE = "prior_ss_part_quantity_positive";    
    public static final String PRIOR_SS_PART_QUANTITY_VALID_INTEGER = "prior_ss_part_quantity_valid_integer";
    public static final String PRIOR_SS_PART_QUANTITY_EXCEEDS_LENGTH = "prior_ss_part_quantity_exceeds_length";       
    public static final String PRIOR_SS_PRICE_MISS_REASON_REQUIRED = "prior_ss_price_miss_reason_required";
    public static final String PRIOR_SS_PRICE_MISS_REASON_EXCEEDS_LENGTH = "prior_ss_price_miss_reason_exceeds_length";
    public static final String PRIOR_SS_ORDER_NUMBER_REQUIRED = "prior_ss_order_number_required";
    public static final String PRIOR_SS_ORDER_NUMBER_BE_TEN_CHARACTERS = "prior_ss_order_number_be_ten_characters";
    public static final String PRIOR_SS_QUOTE_NUMBER_REQUIRED = "prior_ss_quote_number_required";
    public static final String PRIOR_SS_QUOTE_NUMBER_BE_TEN_CHARACTERS = "prior_ss_quote_number_be_ten_characters";    
    public static final String PRIOR_SS_PRICE_JUST_REQUIRED = "prior_ss_price_just_required";
    public static final String SALES_REP_COMPUTED_PRIOR_SS_PRICE_REQUIRED = "sales_rep_computed_prior_ss_price_required";
    public static final String SALES_REP_COMPUTED_PRIOR_SS_PRICE_BE_NUMBER = "sales_rep_computed_prior_ss_price_be_number";
    public static final String SALES_REP_COMPUTED_PRIOR_SS_PRICE_MUST_BE_THAN_ZERO = "sales_rep_computed_prior_ss_price_must_be_than_zero";
    public static final String GD_SB_NEED_RECAUCULATE = "gd_sb_need_recalculate";
    
    //for EC validation
    public static final String EC_CANNOT_BE_CALCULATED = "ec_cannot_be_calculated";
    
    //MTM
	public static final String LEGACY_MACHINE_MODEL_EXCEED_LIMIT = "legacy_machine_model_exceed_limit";
	public static final String LEGACY_MACHINE_SERIAL_NUBMER_EXCEED_LIMIT = "legacy_machine_serial_number_exceed_limit";
	
    public static final String OL_CANNOT_BE_RECALCULATED = "ol_update_needed";
    public static final String PRICE_METHOD_CANNOT_BE_UPDATED = "price_method_cannot_be_updated";
    
    //for VAT ID
    public static final String MSG_CS_VAT_REGISTRATION_NUMBER = "cs_vat";
    public static final String MSG_CS_VAT_NUM_ALREADY_EXISTS = "cs_vat_num_already_exists";
    public static final String MSG_CS_VAT_NUM_FIRST_3_NOT_ALPHA = "cs_vat_num_first_3_not_alpha";
    public static final String MSG_CS_VAT_NUM_FIRST_4_NOT_ALPHA = "cs_vat_num_first_4_not_alpha";
    public static final String MSG_CS_VAT_NUM_INVALID_LENGTH = "cs_vat_num_invalid_length";
    public static final String MSG_CS_VAT_NUM_DATA_INCORRECT = "cs_vat_num_date_incorrect";
   
	public static final String MSG_ENTER_VALID_EXTENSION_EXP_DATE = "enter_valid_extension_exp_date";
	public static final String MSG_EXTENSION_EXP_DATE_MUST_BE_ON_OR_AFTER_ORIGINAL_EXP_DATE = "extension_exp_date_must_be_on_or_after_original_exp_date";
	public static final String MSG_EXTENSION_EXP_DATE_MUST_BE_ON_OR_BEFORE_ORIGINAL_EXP_DATE_QUARTER_LAST_DAY = "extension_exp_date_must_be_on_or_before_original_exp_date_quarter_last_day";
	public static final String MSG_REQUIRED_FIELDS_MUST_BE_FILLED = "required_fields_must_be_filled";
	public static final String MSG_EXP_DATE_EXTENSION_JUSTIFICATION_MUST_BE_FILLED = "exp_date_extension_justification_must_be_filled";
	public static final String EXP_DATE_EXTENSION_JUSTIFICATION = "exp_date_extension_justification";
	public static final String MSG_EXP_DATE_MUST_BE_FILLED = "exp_date_extension_must_be_filled";
	public static final String EXTEND_EXP_DATE = "extend_exp_date";
	public static final String EXP_DATE_EXTENSION_PROMPT = "exp_date_extension_prompt";
	
	//for DSJ Project
	public static final String NO_MATCH_DSJ_CUSTOMER="dsj_no_result";
	public static final String Multiple_MATCH_DSJ_CUSTOMER="dsj_multiple_reusult";
	public static final String NO_CUSTOMER_MATCH_FOUND_BY_ID = "dsj_no_customer_match_by_id";
}