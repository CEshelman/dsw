package com.ibm.dsw.quote.findquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteMessageKeys</code> class.
 * 
 * @author wangxu@cn.ibm.com
 *
 * Created on 2007-5-8
 */
public interface FindQuoteMessageKeys {
    public static final String ERR_ONE_OF_FOLLOWING_REQUIRED = "one_of_following_required";
    public static final String SELECT_ONE_OF_FOLLOWING = "select_one_of_following";
    public static final String SELECT_COUNTRY_FIRST = "select_country_first";
    public static final String SELECT_SUB_REGION_FIRST = "select_sub_region_first";
    public static final String ALL_COUNTRIES = "all_countries";
    public static final String START_OF_NAME = "start_of_name";
    public static final String WITHIN_NAME = "within_name";
    public static final String EXACT_MATCH = "exact_match";
    public static final String MSG_QUOTE_TYPES_REQUIRED = "quoteTypesRequired";
    public static final String MSG_LOBS_FILTER_REQUIRED = "lob_filter_required";
    public static final String MSG_FOR_WORD = "for_word";
    public static final String APPROVAL_QUEUE_TITLE = "approval_queue_title";
    public static final String APPROVAL_QUEUE_SUBTITLE = "approval_queue_subtitle";
    public static final String SPECIAL_BID_PENDING_QUOTES_TITLE = "sb_pending_quotes";
    
    public static final String BIDS_BY_ME = "bids_by_me";
    public static final String BIDS_BY_SAME_GROUP = "bids_by_same_group";
    public static final String BIDS_BY_SAME_APPROVAL_TYPE =  "bids_by_same_approval_type";
    public static final String ALL_BIDS_VISIBLE_TO_ME = "all_bids_visible_to_me";
    public static final String UPDATE_SELECT_CRITERIA = "update_select_criteria";
    public static final String SPECIFY_DISPLAY = "specify_display";

    public static final String SELECT_ACQUISITION = "select_acquisition";
    public static final String NOT_CLASSIFIED = "not_classified";
    public static final String ACTUATION = "actuation";
    public static final String QUOTE_CLASSIFICATION = "quote_classification";
    public static final String QUOTE_CLASSIFICATION_DESC = "quote_classification_desc";
    public static final String SELECT_ALL_CLASSIFICATIONS = "select_all_classifications";
    public static final String DESELECT_ALL_CLASSIFICATIONS = "deselect_all_classifications";
    
    public static final String QUOTE_SUBMISSION_SELECTED = "quoteSubmissionSelected";
    
    public static final String SELECT_SB_REGION_FIRST = "select_special_bid_region_first";
    public static final String ONE_OF_COUNTRY_AND_REGION_REQUIRES = "one_of_country_and_region_requires";
    
    public static final String FIND_BY_APPVLATTR="find_by_appvlAttr";
    public static final String APPROVER_GROUP_DATE_INVALID = "approver_group_date_invalid";
    public static final String APPROVER_TYPE_DATE_INVALID = "approver_type_date_invalid";
    
    public static final String APPROVER_GROUP_REQUIRED="approver_group_required";
    public static final String APPROVER_TYPE_REQUIRED="approver_type_required";
    
    public static final String EVALUATOR_QUEUE_TITLE = "evaluator_queue_title";
    public static final String EVALUATOR_QUEUE_SUBTITLE = "evaluator_queue_subtitle";
    public static final String EVAL_BIDS_BY_ME = "eval_bids_by_me";
    public static final String EVAL_BIDS_BY_SAME_GROUP_ACCEPTANCE = "eval_bids_by_same_group_acceptance";
    public static final String EVAL_BIDS_BY_SAME_GROUP_SUBMITTAL=  "eval_bids_by_same_group_submittal";
    
    public static final String SEARCH_BY = "search_by";
    public static final String SEARCH_BY_SQO_REFERENCE = "search_by_sqo_reference";
    public static final String SEARCH_BY_CUSTOMER_SITE_NUM = "search_by_customer_site_num";
    public static final String SEARCH_BY_CUSTOMER_NAME = "search_by_customer_name";
    public static final String SEARCH_BY_OPTIONAL = "search_by_optional";
    
}
