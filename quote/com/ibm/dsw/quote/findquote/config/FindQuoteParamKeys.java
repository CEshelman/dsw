package com.ibm.dsw.quote.findquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteParamKeys</code> class.
 * 
 * @author wangxu@cn.ibm.com
 *
 * Created on 2007-4-27
 */
public interface FindQuoteParamKeys {
    public static final String DISPLAY_FIND_CONTRACT = "DISPLAY_FIND_CONTRACT";
    public static final String DISPLAY_APPROVAL_QUEUE_CONTRACT = "DISPLAY_APPROVAL_QUEUE_CONTRACT";
    public static final String APPROVAL_QUEUE_TRACKER_CONTRACT = "APPROVAL_QUEUE_TRACKER_CONTRACT";
    public static final String DISPLAY_EVALUATOR_QUEUE_CONTRACT = "DISPLAY_EVALUATOR_QUEUE_CONTRACT";
    public static final String FIND_RESULTS = "FIND_RESULTS";
    
    public static final String QUOTE_TYPE_FILTER = "quoteTypeFilter";
    public static final String LOBS_FILTER = "lob_filter";
    public static final String LOBS_FILTER_REQUIRED = "lob_filter_required";
    public static final String STATUS_FILTER = "statusFilter";
    public static final String SORT_FILTER = "sortFilter";
    public static final String TIME_FILTER = "timeFilter";
    public static final String MARK_FILTER_DEFAULT = "markFilterDefault";  
    public static final String OWNER_TYPE = "ownerType";
    public static final String OWNER_NAME_OR_EMAIL = "ownerNameOrEmail";
    public static final String OWNER_ROLES = "ownerRoles";
    public static final String PARAM_POST_FLAG = "postFlag";
    public static final String PARTNER_SITE_NUM = "partnerSiteNum";
    public static final String STATE = "state";
    public static final String NUMBER = "number";
    public static final String QUEUE_TYPE = "queueType";
    public static final String SB_REGION = "sbRegion";
    public static final String SB_DISTRICT = "sbDistrict";
    public static final String PARAMS_SPLIT_SIGN = ":";
    public static final String APPROVER_GROUP = "approverGroup";
    public static final String APPROVER_TYPE = "approverType";
    public static final String LOB_LIST = "lobList";
    public static final String MARK_IBMER_DEFAULT = "markIBMerDefault";
    public static final String MARK_IBMER_DEFAULT_VALUE = "true";
    public static final String PARAM_PAGE_INDEX = "pageIndex";
    public static final String OVERALL_STATUS_LIST = "overallStatus";
    public static final String MARK_COUNTRY_REGION_DEFAULT = "markCountryRegionDefault";
    public static final String MARK_APPVL_ATTR_DEFAULT = "markAppvlAttrDefault";
    public static final String SITE_NUM = "siteNum";
    public static final String COUNTRY_NAME = "countryName";
    public static final String PARAM_SP_BID_DISTRICTS = "spDistricts";
    public static final String PARAM_SP_BID_REGIONS = "spRigions";
    public static final String PARAM_SP_BID_TYPES = "spTypes";
    public static final String PARAM_SP_BID_GROUPS = "spGroups";
    public static final String OWNER_TYPE_ME = "0";
    public static final String OWNER_TYPE_OTHER = "1";
    public static final String APPROVAL_NAME = "approval_name";
    public static final String FIND_BY_AGREEMENT_NUM_FLAG = "findByAgreementNumFlag";
    public static final String COUNTRY_LIST = "countryList";
    public static final String FIND_BY_CUST_NAME_FLAG = "findByCustNameFlag";
    public static final String FIND_BY_PART_NAME_FLAG = "findByPartNameFlag";
    public static final String COUNTRY_LIST_AS_CODE_DESC = "countryListObj";
    public static final String PARTNER_NAME = "partnerName";
    public static final String FIND_BY_PART_NUM_FLAG = "findByPartNumFlag";
    public static final String AGREEMENT_NUM = "agreement_num";
    public static final String NAME_COMPARISON_START_OF_NAME = "0";
    public static final String NAME_COMPARISON_MIDDLE_OF_NAME = "1";
    public static final String NAME_COMPARISON_EXACT_MATCH = "2";
    public static final String PARTNER_TYPE_FOR_NUM_RESELLER = "0";
    public static final String PARTNER_TYPE_FOR_NUM_DISTRIBUTER = "1";
    public static final String PARTNER_TYPE_FOR_NUM = "partnerTypeForNum";
    public static final String PARTNER_TYPE_FOR_NAME_RESELLER = "0";
    public static final String PARTNER_TYPE_FOR_NAME_DISTRIBUTER = "1";
    public static final String PARTNER_TYPE_FOR_NAME = "partnerTypeForName";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String NAME_COMPARISON = "nameComparison";
    public static final String MARK_COUNTRY_DEFAULT = "markCountryDefault";
    public static final String COUNTRY = "country";
    public static final String IMT = "imt";
    public static final String STATE_NAME = "stateName";
    public static final String SUB_REGION_NAME = "subRegionName";
    public static final String PENDING_APPROVAL = "pending_approval";
    public static final String CLASSIFICATION_FILTER  = "classification_filter";
    public static final String ACTUATION_FILTER = "actuation_filter";
    public static final String CLASSIFICATION_LIST  = "classification_list";
    public static final String ACTUATION_LIST = "actuation_list";
    public static final String COMMON_CRITERIA_FLAG = "commonCriteriaFlag";
    public static final String RELATED_QUOTE_FLAG = "relatedQuoteFlag";
    
    public final static String PAGE_TITLE = "status";
    public final static String PAGE_SUB_TITLE_PRE_PREFIX = "pageSubTitlePrePrefix";
    public final static String PAGE_SUB_TITLE_PREFIX = "pageSubTitlePrefix";
    public final static String PAGE_SUB_TITLE_LINK = "pageSubTitleLink";
    public final static String PAGE_SUB_TITLE_POSTFIX = "pageSubTitlePostfix";
    public final static String REQUIRED_FIELDS = "requiredFields";
    public final static String FIND_QUOTE_BY_COUNTRY_TEXT = "findQuoteByCountryText";
    public final static String COUNTRY_REGION = "cntry_region";
    public final static String COUNTRY_TEXT = "countryText";
    public final static String STATE_REGION = "state";
    public final static String SAVE_SELECTIONS = "saveSelections";
    public final static String SAVE_SELECTIONS_AS_MY_DEFAULT = "save_selections";
    public final static String FIND_BY_SELECTING_TAB = "findBySelectingTab";
    
    public final static String FIND_QUOTE_BY_CUSTOMER_TEXT = "findQuoteByCustomerText";
    public final static String FIND_BY_SITE_NUM = "findBySiteNum";
    public final static String FIND_BY_SITE_NUM_TEXT = "findBySiteNumText";
    public final static String SAVE_AS_DEFAULT = "saveAsDefault";
    
    public final static String FIND_QUOTE_BY_IBMER_TEXT = "findQuoteByIBMerText";
    public final static String ASSIGNED_TEXT = "assignedText";
    public final static String ASSIGNED_TO_ME = "assignedToMe";
    public final static String ENTER_EMAIL = "enterEmail";
    public final static String ASSIGNED_TO_OTHERS = "assignedToOthers";
    public final static String ROLE_TEXT = "roleText";
    public final static String ROLE_CREATOR = "roleCreator";
    public final static String ROLE_EDITOR = "roleEditor";
    public final static String ROLE_APPROVER_PENDING = "roleApproverPending";
    public final static String ROLE_APPROVER_NAMED = "roleApproverNamed";
    
    public final static String ROLE_REVIEWER = "roleReviewer";
    public final static String SAVE_SELECTIONS_ON_TAB = "saveSelectionOnTab";
    
    public final static String FIND_QUOTE_BY_NUM_TEXT = "findQuoteByNumText";
    public final static String FIND_QUOTE_NUM = "findQuoteNum";
    public final static String FIND_QUOTE_BY_SIEBEL_NUM_TEXT = "findQuoteBySiebelNumText";
    public final static String FIND_QUOTE_SIEBEL_NUM = "findQuoteSiebelNum";
    public final static String FIND_QUOTE_BY_ORDER_NUM_TEXT = "findQuoteByOrderNumText";
    public final static String FIND_QUOTE_ORDER_NUM = "findQuoteOrderNum";
    
    public final static String FIND_QUOTE_BY_PARTNER_TEXT = "findQuoteByPartnerText";
    public final static String FIND_BY_PARTNER_SITE_NUM = "findByPartnerSiteNum";
    public final static String PARTNER_TYPE = "partnerType";
    public final static String RESELLER = "reseller";
    public final static String DISTRIBUTOR_PAYER = "distributorPayer";
    public final static String FIND_BY_PARTNER_NAME = "findByPartnerName";

    public static final String FIND_TYPE_PARTNER_NAME = "partname";
    public static final String FIND_TYPE_CUSTOMER_NAME = "custname";
    public static final String FIND_TYPE_PARTNER_NUMBER = "partnum";
    public static final String FIND_TYPE_SITE_NUMBER = "sitenum";
    
    public final static String SQO_REFERENCE = "sqoReference";
    public final static String SALES_RENEWAL = "salesRevewal";
    public final static String QUOTE_CONFIRM_NUM = "quoteConfirmNum";
    public final static String QUOTE_NUM = "quoteNum";
    public final static String QUOTE_SUBMITTED = "quoteSubmitted";
    public final static String QUOTE_TOTAL = "quoteTotal";
    public final static String ORDER_NUM = "orderNum";
    public final static String ORDER_SUBMITTED = "orderSubmitted";
    public final static String ORDER_TOTAL = "orderTotal";
    
    public final static String QUOTE_TYPE_TITLE = "quoteTypeTitle";
    public final static String CREATOR_SUBMITTER = "creatorSubmitter";
    public final static String ORDER_SUBMITTER_INFO = "orderSubmitterInfo";
    public final static String CUSTOMER = "customer";
    public final static String QUOTE_STATUS = "quoteStatus";
    public final static String ORDER_STATUS = "orderStatus";
    
    public final static String RESULT_PAGE_TITLE = "selectedQuotes";
    public final static String RESULT_PAGE_SUB_TITLE = "resultPageSubTitle";
    public final static String SELECTION_CRITERIA = "selectionCriteria";
    public final static String QUOTES = "quotes";
    public final static String QUOTE_TYPES = "quoteTypes";
    public final static String QUOTE_TYPES_REQUIRED = "quoteTypesRequired";
    public final static String STATUSES = "statuses";
    public final static String SUBMITTED_WITHIN = "submittedWithin";
    public final static String SORT_BY = "sortBy";
    public final static String SELECTED_QUOTES = "selectedQuotes";
    public final static String SELECTED_QUOTES_TEXT = "selectedQuotesText";
    public final static String SELECTED_QUOTES_NOTE_TEXT = "selectedQuotesNoteText";
    public final static String VIEW_STATUS_DETAIL = "viewStatusDetail";
    
    public final static String COMMON_SELECTION_CRITERIA = "commonSelectionCriteria";
    public final static String FIND_FOLLOWING_SUBMITTED_QUOTES= "findFollowingSubmittedQuotes";
    
    public static final String MARK_COUNTRY_DEFAULT_VALUE = "true";
    public static final String MARK_COUNTRY_REGION_DEFAULT_VALUE = "true";
    public static final String RENEWAL_QUOTE_EDITS = "renewalQuoteEdits";
    public static final String SALES_QUOTES = "sales_quotes";
    public static final String FIND_FOLLOWING_QUOTE_TYPES= "findFollowingQuoteTypes";
    public static final String TIME_WITHIN= "timeWithin";
    public static final String SELECT_ALL_STATUSES = "selectAllStatuses";
    public static final String DESELECT_ALL_STATUSES = "deselectAllStatuses";
    public static final String OVERALL_STATUS_TEXT = "overallStatusText";
    public static final String WEEK = "week";
    public static final String MONTH = "month";
    public static final String MONTHS = "months";
    public static final String MONTHTITLE = "monthTitle";
    public static final String QUARTER = "quarter";
    public static final String ANYTIME = "anytime";
    public static final String START_OF_NAME = "startOfName";
    public static final String ASSIGNED_TO = "assignedTo";
    public static final String ROLES = "roles";
    public static final String PAGE_PREVIOUS = "page_previous";
    public static final String PAGE_NEXT = "page_next";
    public static final String VIEW_DETAILS = "viewDetails";
    public static final String ME = "me";
    public static final String COOKIE_FLAG = "cf";
    
    public static final String PARAM_STATE_LIST = "stateList";
    
    public static final String CHANGE_SELECT_CRITERIA = "changeSelectionCriteria";
    public static final String MAKE_DEFAULT = "make_default";
    public static final String N0_RECORD = "no_record";
    
    public static final String SELECT_FOLLOWING = "select_one_of_following";
    public static final String DATE_SUBMITTED = "dateSubmitted";
    public static final String RESELLER_NAME = "resellerName";
    public static final String OVERALL_STATUS = "overall_status";
    public static final String TOTAL_PRICE = "TotalPrice";
    public static final String First_Approval_Date = "firstApprovalDate";
    public static final String Final_Approval_Date = "finalApprovalDate";
    
    public static final String SALES = "Sales";
    public static final String RENEWAL = "Renewal";
    
    public static final String FIND_BY_CUSTOMER_NAME = "find_by_customer_name";
    public static final String MATCH_PATTERN_FOR_CUSTOMER_NAME = "match_pattern_for_customer_name";
    
    public static final String ORDER_BEING_PROCESSED = "order_being_processed";
    
    public static final String QUOTE_PENDING_APPROVER_FOR_GROUP="quote_pending_approver_for_group";
    public static final String QUOTE_PENDING_APPROVER_FOR_TYPE="quote_pending_approver_for_type";
    
    public static final String QUOTE_APPROVED_BY_GROUP_ON_DAY="quote_approved_by_group_on_day";
    public static final String QUOTE_APPROVED_BY_TYPE_ON_DAY="quote_approved_by_type_on_day";
    
    public static final String APPROVER_GROUP_DATE="approverGroupDate";
    public static final String APPROVER_TYPE_DATE="approverTypeDate";
    
    public static final String APPROVER_GROUP_FILTER="approverGroupFilter";
    public static final String APPROVER_TYPE_FILTER="approverTypeFilter";
    
    public static final String QUOTE_STATUSE_EEQS="quote_statuse_eeqs";
    public static final String EEQS="EEQS";
    
    public static final String SUB_REGION_LIST="subRegionList";
    public static final String SUB_REGION="subRegion";
    public final static String ROLE_APPROVER = "roleApprover";
    
    public static final String COUNTRY_OR_SUBREGION_REQUIRED ="country_or_subregion_required";
    
    public static final String PO_GEN_STATUS = "poGenStatus";
    public static final String OVER_LIMIT_TOTAL_ROWS = "overLimitTotalRows";
    
    public static final String NO_ACCESS_RIGHTS_TO_VIEW_STATUS = "no_access_rights_to_view_status";
    public static final String NOT_ALLOWED_VIEW_STATUS = "not_allowed_view_status";
    public static final String FIND_ALL_MATCHING_QUOTES_TEXT = "findAllMatchingQuotesText";
    
	public static final String EVAL_WEB_QUOTE_NUM = "eval_web_quote_num";
	public static final String EVAL_QUOTE_TYPE = "eval_quote_type";
	public static final String EVAL_CUST_SITE_NUM = "eval_cust_site_num";
	public static final String EVAL_CUST_NAME = "eval_cust_name";
	public static final String EVAL_QUOTE_EVALOR = "eval_quote_evalor";
	public static final String EVAL_STATUS = "eval_status";
	
	public static final String FIND_TOU_RESULTS = "find_tou_results";
	public static final String DISPLAY_DRAFT_CONTRACT = "display_draft_contract";
	public static final String SERIVCE_ORDER = "service_order";
	
	public static final String SEARCH_INFO = "searchInfo";
	public static final String SEARCH_BY_TYPE = "searchType";
}
