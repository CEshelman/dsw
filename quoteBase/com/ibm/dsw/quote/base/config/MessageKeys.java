package com.ibm.dsw.quote.base.config;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MessageKeys</code> class 
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public interface MessageKeys {
    //Site title
    public static final String SITE_TITLE = "site_title";
	
	// anniversary drop down display labels
	public static final String LABEL_ALL = "label_all";
	public static final String MONTH_JAN = "month_jan";
	public static final String MONTH_FEB = "month_feb";
	public static final String MONTH_MAR = "month_mar";
	public static final String MONTH_APR = "month_apr";
	public static final String MONTH_MAY = "month_may";
	public static final String MONTH_JUN = "month_jun";
	public static final String MONTH_JUL = "month_jul";
	public static final String MONTH_AUG = "month_aug";
	public static final String MONTH_SEP = "month_sep";
	public static final String MONTH_OCT = "month_oct";
	public static final String MONTH_NOV = "month_nov";
	public static final String MONTH_DEC = "month_dec";
	
	public static final String QUOTE_START_DAY = "quote_start_day";
	public static final String CALENDAR_QUOTE_START_DAY = "calendar_quote_start_day";
	public static final String QUOTE_EXP_DAY = "quote_exp_day";
	public static final String CALENDAR_QUOTE_EXP_DAY = "calendar_quote_exp_day";
	public static final String CUST_REQSTD_ARRIVL_DATE ="cust_reqstd_arrivl_date";
	public static final String FIELD_REQUIRE_TO_SUBMIT_QUOTE = "field_require_to_submit_quote";
	public static final String RELATIONSHIP_SVP_LEVEL = "relationship_svp_level";
	public static final String VARIANT_MONTH_LEVEL = "variant_month_level";
	public static final String ANNIVERSARY_DATE_SVP_LEVEL = "Anniversary_date_svp_level";
	public static final String QUOTE_VALUE = "quote_value";
	public static final String CURRENCY = "currency";
	public static final String SELECT_CUSTOMER_SITE_TITLE = "select_customer_site_title";
	public static final String CURRENT_DRAFT_QUOTE = "current_draft_quote";
	public static final String CURRENT_QUOTE_TYPE = "passport_advantage";
	public static final String DAY = "day";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
	public static final String ESTIMATED_ORDER_DATE = "estimated_order_date";
	public static final String CALENDAR_ESTIMATED_ORDER_DATE = "calendar_estimated_order_date";
	
	public static final String BUNDLE_APPL_I18N_QUOTE = "appl.i18n.quote";
	public static final String BUNDLE_BASE_I18N_VALIDATORMESSAGES = "base.i18n.validatorMessages";
	public static final String BUNDLE_APPL_I18N_DRAFT_QUOTE="appl.i18n.draftquote";
	public static final String BUNDLE_APPL_I18N_ERRORMESSAGES = "appl.i18n.errorMessage";
	public static final String BUNDLE_APPL_I18N_MASS_DLGTN = "appl.i18n.quoteMassDlgtn";
	public static final String BUNDLE_APPL_I18N_PART_SEARCH = "appl.i18n.partSearch";
	public static final String BUNDLE_APPL_I18N_PART_DETAILS = "appl.i18n.partDetails";
	public static final String BUNDLE_APPL_I18N_NEW_QUOTE = "appl.i18n.newquote";
	public static final String BUNDLE_APPL_I18N_PART_PRICE = "appl.i18n.partprice";
	public static final String BUNDLE_APPL_I18N_FIND_QUOTE = "appl.i18n.findquote";
	public static final String BUNDLE_APPL_I18N_CONFIGURATOR = "appl.i18n.configurator";

	
	public static final String MSG_ONE_OF_REQUIRED = "validation.text.oneofrequired";
	public static final String MSG_REQUIRED = "validation.text.required";
	public static final String MSG_EMAIL = "validation.text.email";
	public static final String MSG_ISOLATIN1_ONLY = "validation.invalid.charset";
	public static final String MSG_MAXLENGTH = "validation.text.maxlength";
	public static final String MSG_MINLENGTH_ALPHANUMERIC = "validation.minlength.alphanumeric";
    public static final String MSG_ICN_NUMERIC_ONLY = "ibm_cus_num.req.msg";
    public static final String MSG_POST_CODE_VAR_REQ = "postcode.var.req.msg";
    public static final String MSG_POST_CODE_BETWEEN_REQ = "postcode.between.req.msg";
    public static final String MSG_US_POSTCODE_FORMAT = "us.postcode.format.req.msg";
    public static final String MSG_CANADA_POSTCODE_FORMAT = "canada.postcode.format.req.msg";
    public static final String MSG_CZ_SV_POSTCODE_FORMAT = "cz.sv.postcode.format.req.msg";
    public static final String MSG_NL_POSTCODE_FORMAT = "nl.postcode.format.req.msg";
    public static final String MSG_POLAND_POSTCODE_FORMAT = "poland.postcode.format.req.msg";
    public static final String MSG_SKOREA_POSTCODE_FORMAT = "skorea.postcode.format.req.msg";
    public static final String MSG_SWEDEN_POSTCODE_FORMAT = "sweden.postcode.format.req.msg";
    public static final String MSG_PORTUGAL_POSTCODE_FORMAT = "portugal.postcode.format.req.msg";
    public static final String MSG_VAT_NUM_GAPS = "vat.num.gap.msg";
    public static final String MSG_VAT_NUM_CNTRY_CODE = "vat.num.cntry.code.msg";
    public static final String MSG_VAT_NUM_FIXED_LEN = "vat.num.fixed.len.msg";
    public static final String MSG_SPECIAL_CHARACTER = "validation.specialcharacter.invalid";
    public static final String MSG_NAME = "validation.text.name";
    
	
    public static final String EMAIL_ADDR_NOT_IN_BLUEPAGES = "msg_email_not_in_bluepages";
    public static final String EMAIL_ADDR_NOT_IN_REPORTING_LIST = "msg_email_not_in_reporting_list";

    public static final String OPP_OWNER_EMAIL_ADDR = "oppOwnerEmailAddr.displayname";

    public static final String DELEGATE_ID = "delegate_mail.displayname";

    public static final String SALES_USER_ID = "sales_repre_mail.displayname";
    public static final String SELECT_ONE="select_one";
    public static final String SELECT_QUOTE_TYPE="select_quote_type";
    public static final String SELECT_COUNTRY="select_country";
    public static final String SEELCT_ACQUISITION="select_acqstn";
    public static final String SEELCT_OEM_AGRMT_TYP="select_oem_agrmt_typ";
    
    public static final String LAYER_MSG_HEAD = "LAYER_MSG_HEAD";
    public static final String LAYER_MSG_ITEM = "LAYER_MSG_ITEM";
    
    public static final String IMPLIED_GROWTH = "implied_growth";
    public static final String OVERALL_GROWTH = "overall_growth";
    public static final String ADDITIONAL_YEAR_GROWTH = "additional_year_growth";
    
    public static final String MSG_CREATE_CUATOMER_SERVICE_ERROR = "msg_cust_create_usual_error";
    public static final String MSG_CREATE_CUATOMER_SERVICE_NUMBER_ERROR = "msg_cust_create_usual_number_error";   
    public static final String MSG_CREATE_QUOTE_SERVICE_ERROR = "msg_quote_create_ws_failed";
    public static final String MSG_MIGRATE_QUOTE_SERVICE_ERROR = "msg_FCT_to_PA_migrate_create_ws_failed";
    public static final String MSG_MODIFY_QUOTE_SERVICE_ERROR = "msg_quote_modify_ws_failed";
    public static final String MSG_QUOTE_TIMESTAMP_SERVICE_ERROR = "msg_quote_timestamp_usual_error";
    public static final String MSG_SERVICE_UNAVAILABLE_ERROR = "msg_ws_unavailable";
    public static final String MSG_SPECIAL_BID_WORKFLOW_FAILED = "msg_special_bid_workflow_failed";
    public static final String MSG_QUOTE_START_DATE_ERR = "msg_quote_start_date_err";
    public static final String MSG_QUOTE_EXP_DATE_ERR = "msg_quote_exp_date_err";
    public static final String MSG_CUST_REQSTD_ARRIVL_DATE = "msg_cust_reqstd_arrivl_date_err";
    
    public static final String MSG_UNAUTHORIZED_VIEW_QUOTE = "msg_unauthorized_view_quote";
    public static final String MSG_UNAUTHORIZED_DELEGATION = "msg_unauthorized_delegation";
    
    public static final String ATTACH_SUPPORTING_FILES = "attach_supporting_files";

    public static final String LOB_DEFAULT = "lob_default";
    public static final String LOB_ALL = "all_custs";
    public static final String LOB_CSA = "lob_csa";
    public static final String LOB_CSA_DESC = "lob_csa_desc";
    public static final String LOB_PA = "lob_pa";
    public static final String LOB_PA_DESC = "lob_pa_desc";
    public static final String LOB_PAE = "lob_pae";
    public static final String LOB_PAE_DESC = "lob_pae_desc";
    public static final String LOB_PAUN = "lob_paun";
    public static final String LOB_PAUN_DESC = "lob_paun_desc";
    public static final String LOB_FCT = "lob_fct";
    public static final String LOB_FCT_DESC = "lob_fct_desc";
    public static final String LOB_PPSS = "lob_ppss";
    public static final String LOB_PPSS_DESC = "lob_ppss_desc";
    public static final String LOB_FCT_TO_PA_DESC = "lob_fct_to_pa_desc";
    public static final String LOB_OEM = "lob_oem";
    public static final String LOB_OEM_DESC = "lob_oem_desc";
    public static final String LOB_SSP = "lob_ssp";
    public static final String LOB_SSP_DESC = "lob_ssp_desc";
    
    public static final String MSG_EXEC_SUMMARY_TAB_SAVE_SUCCEED = "msg_exec_summary_tab_save_succeed";
    
    public static final String PYMNT_TERMS_DAY = "pymnt_terms_day";
    
    public static final String PROMOTION_NUM = "promotion_Num";
    public static final String MSG_QUPTE_PROMOTION_NUM_ERR = "msg_quote_promotion_num_err";
    
    public static final String GOE_REVIEW="goe_review";
    
    public static final String APPLNC_MTM_VALIDATION_MSG="applnc_mtm_validate_msg";
    public static final String NEED_CONFIG_MONTHLY_SW_MSG="need_config_monthly_sw_msg";
    
    public static final String OVERRIDE_EXP_DATE ="override_exp_date";
    public static final String NEW_EXP_DATE="new_exp_date";
}
