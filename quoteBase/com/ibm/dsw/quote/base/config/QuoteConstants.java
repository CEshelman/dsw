/*
 * Created on Mar 23, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.config;

import java.util.HashSet;

/**
 * @author minhuiy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface QuoteConstants {
	
	public static final String SSP_PROVIDER_TYPE_SINGLE = "S";
	public static final String SSP_PROVIDER_TYPE_MULTI = "M";
	
    //  Quote stage code value
	public static final String QUOTE_STAGE_CODE_SESSION = "SESSION";
	public static final String QUOTE_STAGE_CODE_DRAFT = "DRAFT";
	public static final String QUOTE_STAGE_CODE_SAVED = "SAVED";
	public static final String QUOTE_STAGE_CODE_SAPQUOTE = "SAPQUOTE";
	public static final String QUOTE_STAGE_CODE_SAPSBQT = "SAPSBQT";
	public static final String QUOTE_STAGE_CODE_CANCEL = "CANCEL";
	public static final String QUOTE_STAGE_CODE_CPPRCINC = "CPPRCINC";
	public static final String QUOTE_STAGE_CODE_BIDITRQT = "BIDITRQT";
	public static final String QUOTE_STAGE_CODE_RQCHGREQ = "RQCHGREQ";
	public static final String QUOTE_STAGE_CODE_CPMLTRSL = "CPMLTRSL";
	public static final String QUOTE_STAGE_CODE_CPCHGOUT = "CPCHGOUT";
	public static final String QUOTE_STAGE_CODE_CPEXDATE = "CPEXDATE";
	


	public static final String QUOTE_AUDIENCE_CODE_SQO = "INTERNAL";
	public static final String QUOTE_AUDIENCE_CODE_PGS = "PSPTRSEL";

	public static final String LOB_PA = "PA";
	public static final String LOB_PAE = "PAE";
	public static final String LOB_PPSS = "PPSS";
	public static final String LOB_FCT = "FCT";
	public static final String LOB_OEM = "OEM";
	public static final String LOB_CSA = "CSA";
	public static final String LOB_CSRA = "CSRA";
	public static final String LOB_CSTA = "CSTA";
	public static final String LOB_SSP = "SSP";
	
	public static final String QUOTE_AGREEMENT_OPTION_RELATIONAL="Relational";
	public static final String QUOTE_AGREEMENT_OPTION_TRANSACTIONAL="Transactional";

	public static final String FULFILLMENT_DIRECT = "DIRECT";
	public static final String FULFILLMENT_CHANNEL = "CHANNEL";
	public static final String FULFILLMENT_NOT_SPECIFIED = "NOT_SPEC";
    public static final String PARTNER_FUNC_CODE_ZW = "ZW";
    public static final String PARTNER_FUNC_CODE_ZG = "ZG";
    
	public static final int ACCESS_LEVEL_SUBMITTER = 1;
    public static final int ACCESS_LEVEL_READER = 2;
    public static final int ACCESS_LEVEL_NO_ACCESS = 3;
    public static final int ACCESS_LEVEL_ECARE = 4;
    public static final int ACCESS_LEVEL_APPROVER = 5;
    public static final int ACCESS_LEVEL_ADMINISTRATOR = 6;

    public static final String PRICE_LEVEL_B = "B";
    public static final String PRICE_LEVEL_D = "D";
    public static final String PRICE_LEVEL_E = "E";
    public static final String PRICE_LEVEL_F = "F";
    public static final String PRICE_LEVEL_G = "G";
    public static final String PRICE_LEVEL_H = "H";
    public static final String LOB_PAUN = "PAUN";

    public static final String APP_CODE_SQO = "SQO";
    public static final String APP_CODE_PGS = "PARPCBK";//this is for user access level check, webauth team provided
    public static final String APP_CODE_PGS_HEADLINE = "PGS";//this is for PGS head line message
    public static final double PA_MIN_POINTS = 500;
    public static final int ALLOWED_MAINT_YEARS = 2;
    public static final int EMAIL_ADDRESS_MAX_LENGTH = 80;

    //the value of checkbox when it's checked
	public static final String CHECKBOX_CHECKED= "on";
    public static final String PVU_PARAM = "APP_URL";


    public static final String QUOTE_TYPE_RENEWAL = "RNWLQUOTE";
    public static final String QUOTE_TYPE_SALES = "SLSQUOTE";

    public static final int RNWL_QUOTE_DURATION = 90;

    //Resultset Names in DB for QuoteStatus
    public static final String QUOTE_STATUS_PRIMARY = "PRIMARYSTATUS";
    public static final String QUOTE_STATUS_SECONDARY = "SECNDRYSTATUS";

    //reporting app param names
    public static final String QTERPT_SAP_CUST_NUM = "SAP_CUST_NUM";
    public static final String QTERPT_SAP_CTRCT_NUM = "SAP_CTRCT_NUM";

    // quote create sap call
    public static final String SAP_QUOTE_TYPE_PA = "ZTSA";
    public static final String SAP_QUOTE_TYPE_PAE = "ZTSX";
    public static final String SAP_QUOTE_TYPE_PPSS = "ZTSX";
    public static final String SAP_QUOTE_TYPE_FCT = "ZTSF";
    public static final String SAP_QUOTE_TYPE_OEM = "ZTSM";

    // currency
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";

    //GEO
    public static final String GEO_AP = "AP";
    public static final String GEO_AG = "AG";
    public static final String GEO_EMEA = "EMEA";
    public static final String GEO_LA = "LA";

    //Region
    public static final String REGION_APAC = "APAC";
    public static final String REGION_EMEA = "EMEA";
    public static final String REGION_AMERICAS = "AMERICAS";

    // distribution channel code
    public final static String DIST_CHNL_END_CUSTOMER = "00";
    public final static String DIST_CHNL_HOUSE_ACCOUNT = "H";
    public final static String DIST_CHNL_DISTRIBUTOR = "J";

    public final static String PRICE_SUM_LEVEL_TOTAL = "TOTAL";

    public static final String JADE_SECOND_ACTION_KEY= "jade.secondAction.key" ;
    public static final String LANDED_SALES_MODE_CODE = "L";
    public static final String APPROVAL_ACTION_PENDING = "APPRVL_PENDG";
    public static final String ROYALTY_PART_GROUP_NAME = "ROYALTY";


    public static final String BASELINE_PRICE = "BASELINE_PRICE";
    public static final String END_CUSTOMER_PRICE = "END_CUSTOMER_PRICE";
    public static final String CHANNEL_PRICE = "CHANNEL_PRICE";
    public static final String PVU_PARAM_PORTAL = "portal";
    public static final String PVU_PARAM_PORTAL_INTERNAL = "internal";
    public static final String PVU_PARAM_PORTAL_EXTERNAL = "external";
    public static final String RENEWALSTATUS_CODE_BALOPEN = "E0005";
    public static final String RENEWALSTATUS_CODE_OPEN = "E0003";

    public static final String QUOTE_DOC_CAT = "Q";
    public static final String RQUOTE_DOC_CAT = "R";
    public static final String ORDER_DOC_CAT = "O";
    public static final String APPROVAL_INFO_DELIMITOR = "%dlm%";
    public static final String COUNTRY_CODE_USA = "USA";

    public static final String MIGRTN_CODE_FCT_TO_PA = "FMP";

    public static final String RENEWAL_QUOTE_STATUS_READY_ON_HOLD = "E0001";
    public static final String RENEWAL_QUOTE_STATUS_WITH_SALES_FOR_REVIEW = "E0002";

    public static final String CHNL_MDL_CODE_SINGLTR = "SINGLTR";
    public static final String CHNL_MDL_CODE_TWOTR = "TWOTR";

    public static final int DISTRIBUTOR_TO_BE_DTRMND = 1;
    public static final int SINGLE_TIER_NO_DISTRIBUTOR = 2;

    public static final String RQ_DETAIL_SALES_COMMENTS = "RQ_SALES";

    public static final String QT_ATTCHMNT_SPEL_BID = "SPECL_BID";
    public static final String QT_ATTCHMNT_RQ_SLS_CMMNT = "RQ_SALES";
    public static final String QT_ATTCHMNT_FCT_NON_STD_TC = "FCT_NON_STD_TC";
    public static final String QT_ATTCHMNT_TOU_AMENDMENT = "TOU_AMENDMENT";
    
    //back dating constants
	public static final String BACK_DATING_REASON_OTHER = "OTHER";
	public static final String BACK_DATING_REASON_COMPLIANCE = "CMPL";
	public static final int BACK_DATING_SB_CONTINUOUS_COVERAGE_YEARS = 36;

	public static final int QT_COPY_TYPE_MLTRESL = 2; //multiple reseller
	public static final int QT_COPY_TYPE_PRICINC = 3; //price increase
	public static final int QT_COPY_TYPE_BIDITER = 4; //bid iteration
	public static final int QT_COPY_TYPE_OPTCHG = 5; // output option change
	public static final int QT_COPY_TYPE_EXPIRDATE = 6; // expiration date change
	

	public static final String QT_COPY_TYPE_MLTRESL_STR = "2"; //multiple reseller
	public static final String QT_COPY_TYPE_PRICINC_STR = "3"; //price increase
	public static final String QT_COPY_TYPE_BIDITER_STR = "4"; //bid iteration
	public static final String QT_COPY_TYPE_OPTCHG_STR = "5"; // output option change
	public static final String QT_COPY_TYPE_EXPIRDATE_STR = "6"; // expiration date change

	//Constants for CSRF Web guard
	public static final String CSRF_REQUEST_ACTION_2 = "action2";
	public static final String CSRF_REQUEST_ACTION_DOWNLOAD = "DOWNLOAD";
	public static final String CSRF_REQUEST_ACTION_EXPORT = "EXPORT";
	
	//suffix for term for import spreadsheet quote
	public static final String TERM_SUFFIX = " Months";
	
	//up to 1 quantity
	public static final String UP_TO = "Up To ";
	
	public static final String USA = "USA";
	public static final String IND = "IND";
	public static final String CAN = "CAN";
	public static final String BRA = "BRA";
	
	public static final int APPROVAL_TYPE_MAX_LEVLE = 22;
	
	
	
	//special bid constants
	public static interface SpecialBidReason{
		String SB_REAS_CODE = "SB_REAS_CODE";
		String SB_EMEA_DISCOUNT = "SB_EMEA_DISCOUNT";
		String SB_NO_APPROVAL = "SB_NO_APPROVAL";

		String MAINT_OVER_DEFAULT_PERIOD = "1001";
		String PART_RESTRICT = "1002";
		String PART_GROUP_REQUIRE_SPECIAL_BID = "1003";
		String LINE_ITEM_BACK_DATED = "1004";
		String RESELLER_NOT_AUTHORIZE_TO_PORTFOLIO = "1005";
		String CHANNEL_DISCOUNT_OVERRIDEN = "1006";
		String DISCOUNT_NO_DELEGATION = "1007";
		String DISCOUNT_OVER_DELEGATION = "1008";
		String EMEA_DISCOUNT_OVER_DEFAULT = "1009";
		String DISCOUNT_BELOW_DELEGATION = "1010";
		String HAS_EOL_PART = "1011";
		String RQ_STATUS_REQUIRE_SB = "1012";
		String COVERAGE_LESS_ONE_YEAR_SB = "1014";
		String CMPRSS_CVRAGE_QUOTE = "1015";
		String PAYMENT_TERMS_AND_VALIDITY_DAYS = "1016";
		String CPQ_EXCEP = "1017";
		String CONFGRTN_OVRRDN = "1018";
		String EXPORT_RESTRICTED  = "1019";
		String PARTIAL_RENEWAL = "1020";
		String PROVISNGDAYS_CHANGED = "1021";
		String PARTIAL_RENEWAL_ITEM_OMITTED = "1022";
		String PARTIAL_RENEWAL_QUANTITY_LOWERED = "1023";
		//this is only for display message for "quote is created from a multiple reseller bid" on submitted quote special bid tab
		String CREATE_FROM_MUL_RESELLER = "6001";
		//this is only for display message for "Initiate specal bid approval
		String INIT_SPECL_BID_APPR = "6002";
        // For displaying the message for special bid reason: The special bid is a copy of an approved quote.
  		// Added by Yue Ping Li on 2010-4-26
		String CREATE_FROM_APPROVED_QUOTE = "6003";
		String BID_ITERATION = "6004";
		String COPY_FOR_OUT_CHANGE = "6005";
		String STREAMLINED_APPROVAL_FLAG = "6006";
		
		//Terms and conditions change reason, 
		String TERMS_CONDITIONS_CHANGE = "1028";
		String GRID_OVER_DELEGATION="1029";
		//expiration date has been extended 
		String EXPIRATION_DATE_EXTENDED="1030";
		//coverage term more than 60 months 
		String TERM_GREATER_THAN_60_MONTHS="1031";
		String MANUAL_DISCOUNT_NO_DELEGATION = "1032";
		String YTY_DISCOUNT_NO_DELEGATION = "1033";		
	};


	public static final String NA_CODE_VALUE = "NA";

	public static final String IBM_PROG_CODE_SW_VN = "X";
	public static final String IBM_PROG_CODE_GTS = "G";
	public static final String IBM_PROG_CODE_CONV = "C";
	public static final String IBM_PROG_CODE_OPEN = "";

	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZMRP = "ZMRP"; //Renewal PDF
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZABN = "ZABN"; //Approved Bid Notification
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZABS = "ZABS"; //Approved Bid Notification - for SaaS channel
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZSQC = "ZSQC"; //None Approved Bid Notification - for SaaS channel
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZSQA = "ZSQA"; //Telesales Quote Confirmation/has saas items
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZTQ1 = "ZTQ1"; //Telesales Quote Confirmation
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZTQF = "ZTQF"; //FCT Telesales Quote Conf
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZSQF = "ZSQF"; //FCT Telesales Quote Conf/has saas items
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZRQA = "ZRQA"; //QuoteOutputType RATE, use ZRQA for PA
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZRQF = "ZRQF"; //QuoteOutputType RATE, use ZRQF for FCT
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZTQM = "ZTQM"; //OEM quote
	
	// 15.3 Saas Hybrid offering
	public static final String PART_SUB_TYPE_HYBRID = "HYBRID";

	/**
	 * Budgetary TCV Quotes PA / PAE
	 */
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZSBA = "ZSBA";
	
	/**
	 * Budgetary TCV Quotes FCT
	 */
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZSBF = "ZSBF";
	
	/**
	 * Budgetary Rate Quotes PA / PAE
	 */
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZRBA = "ZRBA";
	
	/**
	 * Budgetary Rate Quotes FCT
	 */
	public static final String QUOTE_OUTPUT_OUTPUT_TYPE_ZRBF = "ZRBF";

	public static final String BILLING_FREQ_ERROR_CODE = "YE076";
	public static final String MAPPING_ERROR_CODE = "YE088";
	
	public static interface CONTRACT_VARIANT {		
	    String ELA = "ELA";
	    String ELG = "ELG";
	}

	public static final String HTTP_METHOD_GET = "GET";

	public static interface FileExtension{
	    String PDF = ".pdf";
	    String RTF = ".rtf";
	}

	public static interface QUOTE_AUD_CODE{
	    String INTERNAL = "INTERNAL";
	    String SALES = "SALES";
	    String PSPTRSEL = "PSPTRSEL";
	}

	//added for added for controlled-distribution-details messages
	public static final String CONTROLLED_DISTRIBUTION_NONE = "";
	public static final String CONTROLLED_DISTRIBUTION_RSEL_NOT_AUTHORIZED = "controlled_distribution_rsel_not_authorized";
	public static final String CONTROLLED_DISTRIBUTION_RSEL_DIST_NOT_ASSOCIATED = "controlled_distribution_rsel_dist_not_associated";

	public static final String SLS_REP_COMPLT_REQ = "SLS_REP_COMPLT_REQ";
	public static final String CUST_DECLINED_REQ = "CUST_DECLINED";

	public static interface RNWLQT_VALIDATION_RESULT {

        public int SUCCESS = 0;

        public int NO_ACCESS = 1;

        public int QT_NOT_EDITABLE = 2;

        public int QT_NOT_MATCH_WITH_SAP = 3;

        public int QT_NOT_ORDERABLE = 4;

        public int QT_SALES_TRACKING_NOT_EDITABLE = 5;

        public int QT_NOT_GPE = 6;

        public int SB_NOT_CREATABLE = 7;

    }

	public static interface FCTNonStdTermsConds {
		public int YES = 1;
		public int NO = 0;
		public int NOT_APPLICABLE = -1;
	}

	public static interface SaaSBillFreq{
		public String MONTHLY = "M";
		public String ANNUAL = "A";
		public String QUARTERLY = "Q";
		public String UPFRONT ="U";
        public String EVENT = "E";
	}

	public static interface SaasBillgOptErrorCode{
		int VALID = 0;
		int BILLG_OPT_OR_CTRCT_TERM_NOT_SELECTED = 1;
		int SUBSCRPTN_NOT_DIVISIBLE = 4;
	}

	public static interface BPTierModel{
		String BP_TIER_MODEL_ONE = "1";
		String BP_TIER_MODEL_TWO = "2";

		String BP_TIER_MODEL_T1_RESELLER = "1";
		String BP_TIER_MODEL_T1_DISTRIBUTOR = "0";
	}
	
	public static interface CognosLob{
		String LOB_PA = "PA";
		String LOB_PX = "PX";
		String LOB_FC = "FC";
		String LOB_SP = "SP";
		String LOB_CR = "CR";
		String LOB_CT = "CT";
	}
	
	public static interface ContractEnrollmentFlag {
		public int YES = 1;
		public int NO = 0;
	}
	//bid iteration validation result
	public static interface BidIteratnValidationResult{
		int VALID_BID_ITERATN = 1; //passed all the rules, it's valid bid iteration
		int INVALID_BID_ITERATN = 0; //invalid bid bid iteration
		int NOT_APPLICABLE_BID_ITERATN = -1; //not applicable, no corresponding type part or no change
	}
	
	//bid iteration validation error message
	public static interface BidIterationErrorMsg{
		String TREM_REDUCE="term_reduce";	
		String BILLING_FREQUENCY_INCREASE="billing_frequency_increase";		
		String UNIT_PRICE_LARGER="unit_price_larger";	
		String PART_OFFERING_NOT_PRESENT="part_offering_not_present";	
		String PART_TYPE_NOT_PRESENT="part_type_not_present";	
		String DISCOUNT_LARGER="discount_larger";	
		String ROYALTY_BEARING="royalty_bearing";
		String EXP_DATE_EXTND_TOO_FAR="exp_date_extnd_too_far";
		String SOFTWARE_INVALID="software_invalid";
		String BID_ITERATION_NOT_APPLICABLE="bid_iteration_not_applicable";
		String PART_ADDED_NOT_VALID="part_added_not_valid";
		String PART_ADDED_LARGER_DISC="part_added_larger_disc";
		String CANNOT_CONTAIN_TERM_AND_CONDITION="cannot_contain_term_and_condition";
		String APPROVAL_TYPE_AND_LEVEL_HAVE_CHANGE = "approval_type_and_level_have_change";
	}
	
	/**
	 * bid iteration expiration date validation for ACV value
	 * when ACV<=50k,the approved quote's expiration date must be within the same year with the original one's;
	 * when 50K<ACV<=2M,the approved quote's expiration date must be within the same quarter with the original one's;
	 * when ACV>2M,the approved quote's expiration date must equal the original one's;
	 */
	public static interface BidIterationACVvalue{
		double ACV_LOW = 50000;
		double ACV_HIGH = 2000000;
	}
	public static final String PAYMENT_SCHEDULE_CHECKBOX_VALUE = "PAY SCHED";
	public static final String QUOTE_STAGE_CODE_SUBMITTED_FOR_EVAL="TOBEEVAL";
	public static final String QUOTE_STAGE_CODE_ACCEPTED_EVAL="ASGNEVAL";
	public static final String QUOTE_STAGE_CODE_RETURN_FORCHG_EVAL="RETDEVAL";
	public static final String QUOTE_STAGE_CODE_EDIT_FORRETCHG_EVAL="SESSEVAL";
	public static final String QUOTE_STAGE_CODE_SUBMIT_BY_EVAL = "EVALSBMT";
	public static final String QUOTE_STAGE_CODE_DELETE = "DELTEVAL";
	public static final String WEB_APP_CNSTNT_NAME="QT_EVAL_CNTRY_BLUE_GROUP";
	public static final String EVAL_SELECT_OPTION_SUBMIT = "Submit";
	public static final String EVAL_SELECT_OPTION_RETURN = "Return";
	
	public static final String EVAL_ACTION_HIS_SUBMIT = "Submit";
	public static final String EVAL_ACTION_HIS_RETURN = "Return";
	public static final String EVAL_ACTION_HIS_ACCEPTED = "Accepted";
	
	public static final String QUOTE_GRWTH_DLGTN_TYPE_FULL = "00";
	public static final String QUOTE_GRWTH_DLGTN_TYPE_NO = "02";
	public static final String QUOTE_GRWTH_DLGTN_TYPE_MIXED = "01";
	
	public static final String EVAL_USER_FLAG ="EVAL";
	
	// special bid attachment constants betin
	public static final HashSet<String> specialbidAttachUserrole = new HashSet<String>(){
		{
			add("creator");
			add("submitter");
			add("Reviewer");
			add("approver");
		}
	};
	
	public static final HashSet<String> specialbidAttachStage = new HashSet<String>(){
		{
			add("draft");
			add("Submitted");
		}
	};
	// special bid attachment constants end
	

	public static final HashSet<String> downloadActionsSet = new HashSet<String>(){{
			add("EXPORT_QUOTE");
			add("EXPORT_SUBMITTED_QUOTE");
			add("EXPORT_QUOTE_NATIVE_EXCEL");
			add("EXPORT_SUBMITTED_QUOTE_NATIVE_EXCEL");
			add("DRAFT_QUOTE_RTF_DOWNLOAD");
			add("SUBMIT_QUOTE_RTF_DOWNLOAD");
			add("SUBMIT_QUOTE_EXEC_SUMMARY_RTF_DOWNLOAD");
			add("SUBMIT_QUOTE_EXEC_SUMMARY_PDF_DOWNLOAD");
			add("DOWNLOAD_ATTACHMENT");
			add("HOSTED_SERVICES_DOWNLOAD");
			
			//RPT
			add("customercontactsdownloadsubmit");
			add("partnerinfodownloadsubmit");
			add("licensedownloadsubmit");
			add("maintenancedownloadsubmit");
			add("rnwlquotdownloadsubmit");
			add("enrollmentsdwnldsubmit");
			add("hostedServicesdwnldsubmit");
			add("ordhistsummarydwnldsubmit");
			add("ordhistdetaildwnldsubmit");
			add("quoteitemsdwnldsubmit");
			
			add("quotesmrycustattr");
			add("quotesmrycustsite");
			add("quotesmryibmcoord");
			add("quotesmryibmrep");
			add("quotesmryibmself");
			add("quotesmryptnrattr");
			add("quotesmryptnrsite");
			add("quotesmryqtnum");
			add("quotesmryregion");}};

    // 0: not prior customer purchase    
    public static final String EC_QT_PURCH_HIST_NO = "0";
    // 1: display the text 'prior customer purchase' to the right of 'top performer' label
    public static final String EC_QT_PURCH_HIST_TOP_PERFORMER = "1";
    // 2: display the text 'prior customer purchase' to the right of 'Market average' label
    public static final String EC_QT_PURCH_HIST_MARKET_AVERAGE = "2";
    //3: display the text 'prior customer purchase' to the right of 'top performer' and 'Market average' labels
    public static final String EC_QT_PURCH_HIST_ALL = "3";

    // 0: no need to recalculate the omit line price
    public static final int OMIT_RECALCULATE_N = 0;
    // 1: need to recalculate the omit line price
    public static final int OMIT_RECALCULATE_Y = 1;
    
    public static final String ToU_SUBTYPE_PARTA = "Part A";
    public static final String ToU_SUBTYPE_PARTB = "Part B";
    public static final String ToU_SUBTYPE_STANDALONE = "Standalone";
    public static final String ToU_SUBTYPE_PLACEHOLDER = "Placeholder";
    //for 14.2 CSA, start  added by Sunny
    
    public static final String ToU_TERMSTYPE_CSA = "SAASCSA";
//    public static final String DSW_AGRMT_TYPE_CSRA = "CSRA";
//    public static final String DSW_AGRMT_TYPE_CSTA = "CSTA";

    //for 14.2 CSA, end 
    public static final String CONTRACT_VAR_NONE = "NONE";
    
  
	//when update renewal model, the source to indicate where the call from
	public static interface SourceOfRenewalModel{
		String FROM_WEB="WEB";	
		String FROM_SAAS_REPORTING="RPT";		
		String FROM_BASIC_CONFIGURATOR="BASIC";	
	}
	
	// message type
	public static final String QUOTE_NOTIFICATION = "QUOTE NOTIFICATION";
	public static final String SYSTEM_MESSAGE = "SYSTEM MESSAGE";
	public static final String FEEDBACK = "FEEDBACK";
	public static final String SPECIAL_SBID_EXCL_GRID_DELE_YES = "1";
    public static final String SPECIAL_SBID_EXCL_GRID_DELE_NO = "0";
    public static final String GRID_DELEGATION_FLAG_YES="1";
    public static final String GRID_DELEGATION_FLAG_NO="0";
    public static final String REVN_STREAM_CAT_CODE_NEWLICN="NEWLICNS";
    public static final  String SW_PROD_BRAND_CODE_RATIONAL="RATIONAL";
}
