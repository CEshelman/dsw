package com.ibm.dsw.quote.newquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>NewQuoteMessageKeys</code> class is the class to define
 * messages.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on: Mar 6, 2007
 */
public interface NewQuoteMessageKeys {
	//Saved draft sales quotes screen labels
    public static final String SAVED_DRAFT_QUOTE_TITLE="saved_draft_quote_title";//Retrieve a saved draft sales quote
    public static final String SAVED_DRAFT_QUOTE_SUBTITLE="saved_draft_quote_subtitle"; //Saved draft sales quotes.
    public static final String SELECTED_DRAFT_QUOTES="selected_draft_quotes";  //Selected draft quotes
    public static final String DELETE_SAVED_DRAFT_QUOTE_MESSAGE_1="delete_saved_draft_quote_message_1";
    public static final String DELETE_SAVED_DRAFT_QUOTE_MESSAGE_2="delete_saved_draft_quote_message_2";
    public static final String NO_QUOTES="no_saved_draft_quote";//There is no saved draft quote against your selection. 
    public static final String OWNER_OPTION ="display_quotes_that_they_are";//Display quotes that they are:
	public static final String TIME_OPTION ="modified_within_the_last";//Modified within the last:
	public static final String SORT_OPTION ="sorted_by";//Sorted by:
	public static final String MARK_DEFAULT_OPTION ="make_selections_my_default";//Make selections my default
	public static final String PARAM_REQUIRED_FIELD_TEXT= "required_field_text";
	public static final String PARAM_REQUIRED_FIELD_TEXT2= "required_field_tex2";
	public static final String PROMPT_DEFORE_DELETE = "prompt_before_delete";
	public static final String PROMPT_DEFORE_DELETE_SAVED_QUOTE = "prompt_before_delete_saved_quote";
	public static final String ERR_OWNER_FILTER_REQUIRED="error_owner_filter_required";

    public static final String MSG_URL_PARAM_NOT_VALID = "error_url_param_invalid";
    public static final String URL_PARAM = "";
    public static final String PARAM_OWNED_BY_ME ="onwned_by_me"; //Owned by me
    public static final String PARAM_DELEGATED_TO_ME ="delegated_to_me"; //Delegeted to me
    public static final String PARAM_ALL ="all"; //All
    public static final String PARAM_TIME_ALL ="all"; //All

    public static final String QUOTE_NUM ="web_quote_number";//Web quote number
    public static final String QUOTE_TYPE ="quote_type";//Quote type
    public static final String CUST_NUM ="cust_site_num";//Customer site number
    public static final String CUST_NAME ="cust_name";//Customer name
    public static final String LAST_MODIFIED ="last_mod";//Last modified date/time
    public static final String ACTIONS ="actions";//Actions
    public static final String QUOTE_TITLE ="quote_title"; //Quote title
    public static final String QUOTE_DESC ="quote_desc"; //Quote description
	public static final String QUOTE_TOTAL = "quote_total";//Quote total
    public static final String ORIGINAL_WEB_REFERENCE ="original_web_reference"; //Quote original web reference
    
    public static final String ASC_SORT_ICON = "acs_sort_icon";//
    public static final String DES_SORT_ICON = "dsc_sort_icon";//
    public static final String NO_SORT_ICON = "no_sort_icon";//
    
    public static final String QUOTE_EVALUATOR ="quote_evaluator";//Quote Evaluator
    public static final String QUOTE_STATUS ="quote_status";//Quote Status
    
    public static final String QUOTE_RESELLER ="quote_reseller"; //Quote description
    public static final String QUOTE_DISTRIBUTOR ="quote_distributor"; //Quote description
    
    public static final String DELETE = "delete";//delete
    public static final String OPENASNEW = "open_as_new_draft";//open
    public static final String OPEN= "open";//open
    public static final String ONE_WEEK = "one_week";//1 week
    public static final String ONE_MONTH = "one_month";//1 month
    public static final String THREE_MONTHS = "three_months";//3 months
    public static final String SIX_MONTHS = "six_months";//6 months
    
    
    public static final String QUOTE_TYPE_DRAFTQUOTE="quote_type";    
    public static final String COUNTRY = "country";
    public static final String COUNTRY_REGION = "cntry_region";
    public static final String PARAM_FORM_FLAG = "form_flag";   
    public static final String DRAFT_QUOTE_DESC = "draft_quote_desc";
    public static final String DISPLAY_NEW_DRAFTQUOTE = "display_new_draftquote";//display new draftquote
    public static final String HELP_QUOTE_TYPE_MESSAGE = "help_quote_type_message";
    
    public static final String UPLOAD_PAGE_TITLE = "upload_page_title";
    public static final String UPLOAD_LABEL_TEXT = "upload_label_text";
    public static final String UPLOAD_PAGE_TEXT_1 = "upload_page_text_1";
    public static final String UPLOAD_PAGE_TEXT_2 = "upload_page_text_2";
    public static final String UPLOAD_PAGE_TEXT_3 = "upload_page_text_3";
    public static final String UPLOAD_PAGE_VIRUS_WARNING = "upload_page_virus_warning";
    public static final String INVALID_XML_FORMAT = "invalid_xml_format";
    public static final String INVALID_NATIVE_EXCEL_FORMAT = "invalid_native_excel_format";
    public static final String INVALID_DATA_CONTENT = "invalid_data_content";
    public static final String INVALID_NATIVE_EXCEL_DATA_CONTENT = "invalid_native_excel_data_content";
    public static final String INVALID_LOB = "invalid_lob";
    public static final String UNSUPPORTED_LOB = "unsupported_lob";
    public static final String INVALID_PARTS = "invalid_both_sw_saas_parts";
    public static final String INVALID_TERM = "invalid_term";
    public static final String INVALID_CNTRY_CURRENCY = "invalid_cntry_currency";
    public static final String INVALID_CUST_CURRENCY = "invalid_cust_currency";
    public static final String INVALID_ACQUISITION = "invalid_acquisition";
    public static final String INVALID_CNTRY = "invalid_cntry";
    public static final String INVALID_START_END_DATE = "invalid_start_end_date";
    public static final String INVALID_STARTEND_DATE_DURATION = "invalid_startend_date_duration";
    public static final String INVALID_START_END_DATE_FORMAT = "invalid_start_end_date_format";
    public static final String QUOTE_HAS_BEEN_LOCKED = "quote_has_been_locked";
    public static final String QUOTE_HAS_BEEN_MODIFIED = "quote_has_been_modified";
    public static final String QUOTE_HAS_BEEN_MODIFIED_BY_YOURSELF = "quote_has_been_modified_by_yourself";
    public static final String UPLOAD_WARNING_NO_SPREADSHEET_SELECTED = "upload_warning_no_spreadsheet_selected";
    
    public static final String EVAL_QUOTE_STATUS_TITLE="eval_quote_status_title";
    public static final String UNDER_EVAL_QUOTES="under_eval_quotes"; 
    public static final String NO_EVAL_QUOTES="no_eval_quotes";
    public static final String EVAL_QUOTE_STATUS ="eval_quote_status";
    public static final String EVAL_QUOTE_EVALUATOR ="eval_quote_evaluator";
    public static final String EVAL_COPY_AS_NEW = "eval_copy_as_new";
    public static final String EVAL_EDIT = "eval_edit";
    public static final String S_TOBEEVAL = "s_tobeeval";
    public static final String S_ASGNEVAL = "s_asgneval";
    public static final String S_RESEVAL = "s_reseval";
    public static final String S_DELTEVAL = "s_delteval";
}
