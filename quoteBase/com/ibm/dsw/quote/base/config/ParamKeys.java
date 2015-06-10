package com.ibm.dsw.quote.base.config;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ParamKeys<code> class .
 *    
 * @author: chenzhh@cn.ibm.com
 * Creation date: Apr 1, 2006
 */
public interface ParamKeys {
	//Configuration parameters
	public static final String ERROR_PAGE = "errorPage";

	public static final String ENCODING = "encoding";

	//parameter keys
	public static final String PARAM_LOCAL = "locale";
	
	public static final String PARAM_TIMEZONE = "timezone";
	
	public static final String PARAM_SESSION_QUOTE_USER = SessionKeys.SESSION_QUOTE_USER;

	public static final String PARAM_AUDIENCE_CODE = SessionKeys.SESSION_AUD_CODE;
	
	public static final String PARAM_TIMEZONEOFFSET = "timezoneOffset";
	
	public static final String PARAM_GMT_TIMEZONE = "GMT";
	
	public static final String PARAM_RPT_JADE_ACTION = "jadeAction";
	
	// search/select customer request parameters
	public static final String PARAM_SITE_NUM = "siteNumber";
	public static final String PARAM_AGREEMENT_NUM = "agreementNumber";
	public static final String PARAM_FIND_ALL_CNTRY_CUSTS = "findAllCntryCusts";
	//public static final String PARAM_IBM_CUST_NUM = "ibmCustomerNumber";
	public static final String PARAM_CUST_NAME = "customerName";
	public static final String PARAM_COUNTRY = "country";
	public static final String PARAM_COUNTRY_OBJECT = "countryObject";
	public static final String PARAM_LINE_OF_BUSINESS = "lob";
	public static final String PARAM_FIND_ACTIVE_CUSTS = "findActiveCusts";
	public static final String PARAM_FIND_ALL_CUSTS = "findAllCusts";
	public static final String PARAM_FIND_ACTIVE_PA = "findActivePA";
	public static final String PARAM_FIND_ACTIVE_PAE = "findActivePAE";
	public static final String PARAM_FIND_ACTIVE_FCT = "findActiveFCT";
	public static final String PARAM_FIND_ACTIVE_PPSS = "findActivePPSS";
	public static final String PARAM_FIND_ACTIVE_OEM = "findActiveOEM";
	public static final String PARAM_FIND_ACTIVE_SSP = "findActiveSSP";
	public static final String PARAM_AUDIENCE = "audience";
	public static final String PARAM_IS_NEW_CUST = "isNewCustomer";
	
	public static final String PARAM_CONTRACT_OPTION = "contractOption";
	public static final String PARAM_CONTRACT_OPTION_OBJECT = "contractOptionObject";
	public static final String PARAM_ANNIVERSARY = "anniversary";
	public static final String PARAM_CUSTOMER_SEARCH_CRITERIA = "customerSearchCriteria";
	public static final String PARAM_START_POSITION = "startPos";
	
	//Create customer request parameters
	public static final String PARAM_QUOTE_NUM = "quoteNum";
	public static final String PARAM_CURRENCY = "currency";
	public static final String PARAM_CURRENCY_OBJECT = "currencyObject";
	public static final String PARAM_COMPANY_NAME = "companyName";
	public static final String PARAM_COMPANY_NAME1 = "companyName1";
	public static final String PARAM_COMPANY_NAME2 = "companyName2";
	public static final String PARAM_ADDRESS1 = "address1";
	public static final String PARAM_ADDRESS2 = "address2";
	public static final String PARAM_CITY = "city";
	public static final String PARAM_STATE = "state";
	public static final String PARAM_POSTAL_CODE = "postalCode";
	public static final String PARAM_VAT_NUM = "vatNum";
	public static final String PARAM_INDUSTRY_IND = "industryIndicator";
	public static final String PARAM_COMPANY_SIZE = "companySize";	
	public static final String PARAM_CS_ONETOK = "oneToK";	
	public static final String PARAM_CS_KPLUS = "kPlus";	
	public static final String PARAM_CNT_FNAME = "cntFirstName";
	public static final String PARAM_CNT_LNAME = "cntLastName";
	public static final String PARAM_CNT_PHONE = "cntPhoneNumFull";
	public static final String PARAM_CNT_FAX = "cntFaxNumFull";
	public static final String PARAM_CNT_EMAIL = "cntEmailAdr";
	public static final String PARAM_COMM_LANGUAGE = "commLanguage";
	public static final String PARAM_MEDIA_LANGUAGE = "mediaLanguage";
	public static final String PARAM_WEBCUST_ID = "webCustId";
	public static final String PARAM_CUST_NUM = "customerNumber";
	public static final String PARAM_CUSTNUM = "custNum";
	public static final String PARAM_SAP_CONTRACT_NUM = "sapContractNum";
	public static final String PARAM_TEMP_ACCESS_NUM = "tempAccessNum";
	public static final String PARAM_SAP_CONTACT_ID = "sapContactId";
	public static final String PARAM_WEBCUST_TYPE_CODE = "webCustTypeCode";
	public static final String PARAM_SAP_INTL_PHONE = "sapIntlPhoneNum";
	public static final String PARAM_MKTG_EMAIL_FLAG = "mktgEmailFlag";
	public static final String PARAM_WEBCUST_STAT_CODE = "webCustStatCode";
	public static final String PARAM_SAP_CNT_PRTNR_FUNC_CODE = "sapCntPrtnrFuncCode";
	public static final String PARAM_SAP_DOC_ID = "sapDocId";
	
	public static final String PARAM_TOU_AMDT_CHANGE_FLAG = "isTouAmdtChanged";
	public static final String PARAM_AGREEMENT_TYPE = "agreementType";
	public static final String PARAM_AGREEMENT_OPTION = "agreementOption";
	public static final String PARAM_TRANS_SVP_LEVEL = "transSVPLevel";
	public static final String PARAM_GOV_SITE_TYPE = "govSiteType";
	public static final String PARAM_GST_STATE_LOCAL = "stateLocal";
	public static final String PARAM_GST_FEDERAL = "federal";
	
    public static final String PARAM_REDIRECT_MSG = "redirectMsg";

    // ship-to & install-at address
    public static final String PARAM_ADDRESS_TYPE = "addressType";
    
	// Quote parameters
	public static final String PARAM_QUOTE_HEADER_INFO = "quoteHeaderInfo";
	public static final String PARAM_QUOTE_OBJECT = "quoteObject";
	public static final String PARAM_USER_OBJECT = "userObject";
	public static final String PARAM_QUOTE_USER_SESSION = "quoteUserSession";
	public static final String PARAM_QUOTE_LOCK_INFO = "quoteLockInfo";
	public static final String PARAM_DRAFTQT_SAVE_SUCCUSS = "saveSuccess";
	
	public static final String PARAM_DEST = "dest";
	public static final String PARAM_IS_RQ = "isRq";
	public static final String PARAM_RNWL_QT_NUM = "renewalQuoteNum";
	public static final String PARAM_IS_SBMT_QT = "isSubmittedQuote";
	public static final String PARAM_IS_READ_ONLY = "isReadOnly";
	public static final String BIDITRTN_POST = "BIDITRTN_POST";
	public static final String PRE_CREDIT_CHECKED_QUOTE_NUM= "preCreditCheckedQuoteNum";
	
	//result parameters
	public static final String PARAM_SIMPLE_OBJECT = "eobject";
	public static final String PARAM_QUOTE_RIGHTCOLUMN = "QuoteRightColumn";
	
	public static final String VALIDATION_DATA_MAP = "VALIDATION_DATA_MAP";
	
	public static final String PARAM_QUOTE_COOKIE ="QUOTE_COOKIE";
	public static final String PARAM_STATUS_TRACKER_COOKIE ="STATUS_TRACKER_COOKIE";
	
	public static final String PARAM_HTTP_REQUEST_METHOD = "HTTP_REQUEST_METHOD";
	
	public static final String PARAM_REDIRECT_URL = "redirectURL";
	public static final String PARAM_FORWARD_FLAG = "forwardFlag";//flag for "need forward"
	public static final String PARAM_COME_FROM_FORWARD = "comeFromForward";//flag for "come from a forward action"
	public static final String PARAM_REDIRECT_PARAMS = "redirectParams";
	
	// validation message and argument parameters
	public static final String PARAM_VALIDATION = "vld_";
	public static final String PARAM_ARGUMENT = "arg_";
	
	public static final String PARAM_MSG_INFO = "messageInfo";
	public static final String PARAM_MSG_ERROR = "messageError";
	public static final String PARAM_TRAN_MSG = "tranMessage";
	
	//administrators of the bid?s geo object
	public static final String PARAM_ADMIN_LIST_OBJECT = "adminListObject";
	
	
	//param for FCT

    public static final String PARAM_ACQRTN_CODE = "acqrtnCode";
    
    public static final String PARAM_CLASSFCTN_CODE = "quoteClassfctnCode";
    
    //param for upload file
    public static final String PARAM_UPLOAD_UUID = "uploadUUID";
    
    public static final String PARAM_USER_ROLE = "userRole";
    
    public static final String PARAM_USER_ID="userId";
    
    public static final String PARAM_USER_NAME="userName";
    
    public static final String PARAM_STAGE = "stage";
    
    public static final String PARAM_OEM_AGREEMENT_TYPE = "oemAgrmntType";
    
    public static final String PARAM_COUNTRY_CODE2 = "countryCode2";
    
    public static final String PARAM_CUST_ACT_URL_PARAM = "searchCriteriaUrlParam";
    
    public static final String PARAM_CUST_ACT_REQUESTOR = "requestorEMail";
    
    public static final String PARAM_OEM_BID_TYPE = "oemBidType";
    
    public static final String PARAM_SQO_HEADLINE_MSG = "sqoHeadLineMsg";
    
    public static final String PARAM_PA_REG_EMAIL_SEND = "PARegstrnEmailSendFlag";
    
    public static final String PARAM_CREATE_QT_FROM_ORDER_URL = "createFromOrderURL";
    

	public static final String PARAM_QT_AUDIT_HIST_LIST = "quoteAuditHistList";
	
	public static final String COPIED_QUOTE_NUM ="copiedQuoteNum";
	
	public static final String ORIGINAL_QUOTE_NUM ="originalQuoteNum";
	
	public static final String PARAM_AJAX_OPER_STATUS = "ajaxOperStatus";
	
	public static final String PARAM_AJAX_OPER_MESS = "ajaxOperMess";

	public static final String PARAM_BP_TIER_MODEL = "bpTierModel";
	
	public static final String PARAM_APP_CODE = "applicationCode";
	
	//Downloaded pricing type for PGS
	public static final String PARAM_PRICING_TYPE = "pricingType";
	
	public static final String PARAM_MIGRATION_REQSTD_NUM = "migrationReqNum";
	
	public static final String PARPM_SAP_CONFIRM_NUM = "sapConfirmNum";
	
	public static final String PARPM_MIGRATION_FAILURE_LINE_ITEMS = "migrationFailureIineItems";

	public static final String PARAM_MIGRATION_REQUEST_OBJECT = "migrateResqtObject";
	
	public static final String PARAM_REDIRECT_CONGOS_URL = "redirectCongosUrl";

	public static final String PARAM_URLTYPE = "urlType";

	public static final String PARAM_REDIRECT_CONGOS_PARAM = "redCongosParam";

	public static final String PARAM_REDIRECT_CONGOS_CUSNUM = "p_Sold to cust";

	public static final String PARAM_REDIRECT_CONGOS_CUSNUM_JSP = "p_Sold%20to%20cust";

	public static final String PARAM_REDIRECT_CONGOS_AGREENUM = "p_SAP_CTRCT_NUM";

    public static final String PARAM_REDIRECT_CONGOS_LOB = "p_LINE_OF_BUS_CODE";

    public static final String PARAM_REDIRECT_CONGOS_FORMAT = "fmt";

    public static final String PARAM_REDIRECT_CONGOS_FORMAT_DATASET = "DataSet";

    public static final String PARAM_PROG_MIGRATION_CODE ="progMigrationCode";

    public static final String PARAM_QUOTE_FLAG="quoteFlag";

    public static final String PARMA_BID_ITERATN_REASONS = "bidIteratnReasons";
    
    public static final String PARAM_RETURN_REASONS = "returnReasons";

  //email error

    public static final String PARAM_SHOW_EMAIL_MSG_FLAG = "isShowErrorEmailMsgFlag";

    //order pgs param
    public static final String PARAM_DRAFT_PGS_ORDER_P0="P0";
    public static final String PARAM_DRAFT_PGS_ORDER_P0_VALUE="PGS";
    public static final String PARAM_DRAFT_PGS_ORDER_ISORDERNOW="isOrderNow";
    public static final String PARAM_DRAFT_PGS_ORDER_ISORDERNOW_VALUE="true";
    public static final String PARAM_DRAFT_PGS_ORDER_WEBQUTENUMBER="quote";

	//quote flag lineItem
	public static final String PARAM_QUOTE_FLAG_EMPTY="empty";
	public static final String PARAM_QUOTE_FLAG_SAAS="saas";
	public static final String PARAM_QUOTE_FLAG_SOFTWARE="software";
	
	public static final String PARAM_PROVISIONING_QUOTE="provisioningQuoteInfo";
	
	public static final String PARAM_CURRENT_TAB_DISPLAY_ACTION = "curTabDisAct";
	
	public static final String PARAM_UNDO_FLAG = "paramUndoFlag";
	
	public static final String PARAM_SSP_PROVIDER_TYPE ="sspType";
	
	//Add a new parameter key for the customer country to avoid overrider 
	public static final String CUSTOMER_COUNTRY = "cusCountry";
	
	public static final String RELATED_BIDS_LIST = "RELATED_BIDS_LIST";
	
	//Add a new parameter key for the flag of EC lines in the quote
	public static final String ECELIGIBLE_FLAG = "isECEligible";

	public static final String PARAM_SEARCH_FOR = "searchFor";
	
	public static final String OMITTED_LINEITEM_LIST = "OMITTED_LINEITEM_LIST";
	
	public static final String LINE_ITEM_ADDRESSES = "lineItemsAddresses";
	
	public static final String PARAM_QUOTE_CURRENCY_CODE = "currencyCode";
	
	public static final String PARAM_END_USER_FLAG_NAME = "endUserFlag";
	
	public static final String SAAS_TERM_COND_CAT_FLAG = "saasTermCondCatFlag";

	public static final String IS_ONLY_SAAS_PARTS = "isOnlySaaSParts";
	
	public static final String IS_CHANNEL_QUOTE = "isChannelQuote";
	
	public static final String IS_SUBMITTED_QUOTE = "isSubmittedQuote";

	public static final String QUOTE_STATUS = "quoteStatus";
	
	public static final String IS_SPECIAL_BID = "isSpecialBid";
	
	public static final String PARAM_SEARCH_METHOD = "searchMethod";
	
	
	//Monthly parts param
	public static final String PARAM_CONFIGURATOR_FORM="configuratoForm";
	public static final String PARAM_ADD_NEW_MONTHLY_SW="isAddNewMonthlySWFlag";
	public static final String PARAM_CHRG_AGRMT_NUM="chrgAgrmtNum";
	public static final String PARAM_CONFIGRTN_ID="configrtnId";
	public static final String PARAM_CONFIGRTN_ACTION_CODE="configrtnActionCode";
	public static final String PARAM_ORG_CONFIG_ID="orgConfigId";
	
	//Mobile
	public static final String PARAM_SPECIAL_BID_REASON_TEXT = "specialBidReasonText";	
	
	public static final String PARAM_FWD_MSG = "forwardedMessages";
	public static final String PARAM_HTTP_REQUEST = "httpRequest";
	public static final String PARAM_SAVE_MSG_2_HTTP_REQ_FLAG = "saveMsg2HttpReqFlag";
	
}
