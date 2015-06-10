package com.ibm.dsw.quote.draftquote.config;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.config.QuoteParamKeys;

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
public interface DraftQuoteParamKeys extends ParamKeys, QuoteParamKeys {

    public static final String PARAM_QUOTE_OBJECT = "quoteObject";

    public static final String RQ_EDITABLE = "RQ_EDITABLE";

    public static final String PARAM_OPP_OWNER_EMAIL = "oppOwnerEmailAddr";

    public static final String QUOTE_DIS_PER_INPUT = "quoteDiscountPercent";

    public static final String PARTNER_DIS_PER_INPUT = "partnerDiscountPercent";
    
    public static final String OVERALL_YTY_GROWTH = "overallYtyGrowth";
    
    public static final String CLEAR_OFFER_BUTTON = "clearOfferButton";

    public static final String USE_GSA_PRICING = "use_gsa_pricing";

    public static final String USE_GSA_PRICING_ID1 = "use_gsa_pricing_id1";

    public static final String USE_GSA_PRICING_ID2 = "use_gsa_pricing_id2";

    public static final String GSA_PRICING_YES = "yes";

    public static final String GSA_PRICING_NO = "no";

    public static final String OVRD_DATE_FLAG = "true";

    public static final String UN_OVRD_DATE_FLAG = "false";

    public static final String partNumberSuffix = "_PART_NUM";

    public static final String specifySortOrderSuffix = "_MANUAL_SORT_SEQ";

    public static final String quantitySuffix = "_QTY";
    
    public static final String information = "_APPLIANCE_INFORMATION";

    public static final String overrideEntitledPriceSuffix = "_OVERRIDE_ENTITLED_PRC";

    public static final String BILLING_FREQUENCY_CHANGE_SELECT = "billingFrequencyChangeSelect";

    public static final String overridePriceSuffix = "_OVERRIDE_UNIT_PRC";
    public static final String overridePriceRadio = overridePriceSuffix + "_radio";
    public static final String originalOverridePriceSuffix = "_ORIGINAL_UNIT_PRC";
    
    public static final String PRICE_DISCOUNT_RADIO = "_PRICE_DISCOUNT_RADIO";
    
    

    public static final String YTY_GROWTH_SUFFIX = "_YTY_GROWTH";
    public static final String GRWTH_DLGTN_COND_APPLD_BUTTON = YTY_GROWTH_SUFFIX + "_radio";
    
    public static final String discountPriceSuffix = "_DISC_PCT";
    
    public static final String discountPriceRadio = discountPriceSuffix + "_radio";

    public static final String addiSeqNumSuffix = "_ADDI_SEQ_NUM";

    public static final String maintainAddtionalYearSuffix = "_MAINTAIN_ADDI_YEAR";

    public static final String prorateFirstYearToAnniSuffix = "_PRORATE_FLAG";

    public static final String prorateStartDateSuffix = "_START_DT";

    public static final String PARAM_APPROVAL_GROUPS = "_APPROVAL_GROUPS";

    public static final String prorateEndDateSuffix = "_END_DT";

    public static final String itemPointSuffix = "_UNIT_PTS";

    public static final String itemPriceSuffix = "_UNIT_PRC";

    public static final String totalPointsSuffix = "_EXT_PTS";

    public static final String totalPriceSuffix = "_EXT_PRC";

    public static final String ovrdDtStartFlagSuffix = "_OVRD_DT_START_FLAG";

    public static final String ovrdDtEndFlagSuffix = "_OVRD_DT_END_FLAG";

    public static final String proHidddenSuffix = "_ProHidden";

    public static final String nonproHiddenSuffix = "_NonProHidden";

    public static final String stdStartDateSuffix = "_STD_START_DATE";

    public static final String stdEndDateSuffix = "_STD_END_DATE";

    public static final String isFTLSuffix = "_IS_FTL";

    public static final String offerIncldFlagSuffix = "_OFFER_INCLD_FLAG";

    public static final String PVU_QTY_MANUALLY_ENTERED_SUFFIX = "_PVUQTY_MANUALLY_ENTERED";

    public static final String PREV_OFFER_PRICE = "PREV_OFFER_PRICE";

    public static final String PREV_OVRRD_PRICE_SUFFIX = "_PREV_OVRRD_PRICEPREV_SUFFIX";

    public static final String PREV_DISCOUNT_SUFFIX = "_PREV_DISCOUNTPREV_SUFFIX";

    public static final String PREV_YTY_SUFFIX = "_PREV_YTYPREV_SUFFIX";

    //Back dating param keys
    public static final String BACK_DATE_SUFFIX = "_backDateSuffix";

    public static final String BACK_DATE_REASON_SUFFIX = "_backDateReasonSuffix";

    public static final String BACK_DATE_TABLE_TBODY = "backTableTbody";

    public static final String BACK_DATE_RULE_DIV = "backDateRuleDiv";

    public static final String BACK_DATE_CHECK_IDS = "backDateCheckIDs";

    public static final String BACK_DATE_DISPLAY_STYLE = "backDateDisplayStyle";

    public static final String BACK_DATEING_COMMENT = "backDatingComment";

    public static final String bpOverrideDisSuffix = "_BP_OVERRIDE_DIS";

    public static final String bpOverridePriceSuffix = "_BP_OVERRIDE_PRICE";

    public static final String bidExtendedPriceSuffix = "_BID_EXTEDNDED_PRICE";

    public static final String REVN_STRM_CODE = "revnStrmCode";

    //field name for showing error message
    public static final String PARAM_QUOTE_DISCOUNT_PERCENT = "Quote discount price";

    public static final String PARAM_QUANTITY = "Quantity";

    public static final String PARAM_OVERRIDE_PRICE = "Override price";

    public static final String PARAM_DISCOUNT_PERCENT = "Discount percent";

    public static final String PARAM_ITEM_POINTS = "Item points";

    public static final String PARAM_ITEM_PRICE = "Item price";

    public static final String PARAM_TOTAL_POINTS = "Total points";

    public static final String RULE_ENGINE_NOT_AVAILABLE = "ruleEngineNotAvailable";

    public static final String PARAM_TOTAL_PRICE = "Total price";

    public static final String PARAM_WEB_QUOTE_NUM = "webQuoteNum";
    
    public static final String PARAM_WEB_QUOTE_YTY_SRC_CODE = "ytySrcCode";
    
    public static final String PARAM_WEB_QUOTE_CURRENCY_CODE = "quoteCurrencyCode";
    
    public static final String PARAM_LPP_CURRENCY_CODE = "lppCurrencyCode";
    
    public static final String PARAM_WEB_QUOTE_LINEITEM_OLD_LPP = "oldLpp";
    
    public static final String PARAM_WEB_QUOTE_YTY_FLAG = "flag";
    
    public static final String PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP= "errorMsgOrLpp";
    
    
    public static final String PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT = "ytyObject";

    // Sales draft quote - sales information tab request parameters
    public static final String PARAM_SI_BRIEF_TITLE = "briefTitle";

    public static final String PARAM_SI_FULL_DSCR = "quoteDesc";

    public static final String PARAM_SI_BUS_ORG = "busOrgCode";

    public static final String PARAM_SI_SET_DEFAULT = "markBOasDefault";

    public static final String PARAM_SI_OPP_NUM = "oppNumRadio";

    public static final String PARAM_SI_SIEBEL_RADIO = "sionSiebel";

    public static final String SELECT_SI_SIEBEL_RADIO = "sionSiebelSel";

    public static final String PARAM_SI_SIEBEL_INPUT = "opprtntyNum";

    public static final String PARAM_SI_SIEBEL_SELECT = "opprtntyNumSel";

    public static final String PARAM_SI_ON_CUSTINIT = "sionCustInit";

    public static final String PARAM_SI_EXEMPTION_CODE = "exemptnCode";

    public static final String PARAM_SI_UPSIDE_TRANS = "upsideTrendTowardsPurch";

    public static final String LINE_SEQ_NUM = "lineSeqNum";

    public static final String PARAM_SI_UT_YES = "upsideTransYes";

    public static final String PARAM_SI_UT_NO = "upsideTransNo";

    public static final String PARAM_SI_TACTIC_CODES = "tacticCodes";

    public static final String PARAM_SI_SALES_ODDS = "salesOdds";

    public static final String PARAM_SI_CUST_REAS_CODE = "custReasCode";

    public static final String PARAM_SI_SALES_COMMENTS = "salesComments";

    public static final String PARAM_SI_DETAIL_COMMENTS = "detailComments";

    public static final String PARAM_ATTACHMENT_LIST = "attachmentList";

    public static final String PARAM_OVR_START_DAY = "startDay";

    public static final String PARAM_OVR_START_MONTH = "startMonth";

    public static final String PARAM_OVR_START_YEAR = "startYear";

    public static final String PARAM_OVR_END_DAY = "endDay";

    public static final String PARAM_OVR_END_MONTH = "endMonth";

    public static final String PARAM_OVR_END_YEAR = "endYear";

    public static final String PARAM_EXP_DAY = "expirationDay";

    public static final String PARAM_EXP_MONTH = "expirationMonth";

    public static final String PARAM_EXP_YEAR = "expirationYear";

    public static final String PARAM_CUST_REQSTD_ARRIVL_DAY = "custReqstdArrivlDay";

    public static final String PARAM_CUST_REQSTD_ARRIVL_MONTH = "custReqstdArrivlMonth";

    public static final String PARAM_CUST_REQSTD_ARRIVL_YEAR = "custReqstdArrivlYear";
    
    public static final String PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY = "lineItem_custReqstdArrivlDay";

    public static final String PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH = "lineItem_custReqstdArrivlMonth";

    public static final String PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR = "lineItem_custReqstdArrivlYear";
    
    public static final String PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX = "LINEITEM_CUST_REQSTD_ARRIVL_DATE";
    
    public static final String PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX = "LINEITEM_CUST_REQSTD_ARRIVL_DATE_ORG";
    
    public static final String PARAM_LINEITEM_APP_SEND_MFG_FLG = "lineItem_app_send_MFG_flg";
    
    public static final String PARAM_ORIGINAL_HEADER_CRAD = "Original_header_CRAD";
    
    public static final String PARAM_ASSOCIATE_CRAD_AREA = "associate_CRAD_Area";

    public static final String PARAM_FUL_FILL_SRC = "fullfillmentSrc";

    public static final String PARAM_FUL_FILL_SRC_DIRECT = "fullfillmentSrcDirect";

    public static final String PARAM_FUL_FILL_SRC_CHANNEL = "fullfillmentSrcChannel";

    public static final String PARAM_MAX_EXP_DAYS = "maxExpirationDays";

    public static final String PARAM_DEFAULT_BUSORG = "PARAM_DEFAULT_BUSORG";

    public static final String PARAM_BTN_NAME = "buttonName";
    
    public static final String EXTENSION_DATE_TRUE = "true";

    public static final String EXTENSION_DATE_FALSE = "false";
    
    public static final String PARAM_EXTENSION_BUTTON = "extensionButton";

    public static final String PARAM_EXTENSION_TYPE = "extensionType";
    
    public static final String PARAM_EXTENSION_DAY = "extensionDay";

    public static final String PARAM_EXTENSION_MONTH = "extensionMonth";

    public static final String PARAM_EXTENSION_YEAR = "extensionYear";
    
    public static final String PARAM_EXTENSION_DATE="extensionDate";

    public static final String PARAM_START_DAY = "startDay";

    public static final String PARAM_START_MONTH = "startMonth";

    public static final String PARAM_START_YEAR = "startYear";
    
    public static final String PARAM_START_DATE="startDate";
    
    public static final String PARAM_EXPIRATION_DATE="expirastionDate";
    
    public static final String PARAM_CRAD_DATE="cradDate";
    
    public static final String PARAM_UNLOCK_QUOTE = "unlockQuote";

    public static final String PARAM_PAYMENT_TERMS_RADIO="pymntTermsRadio";

    public static final String PARAM_PAYMENT_TERMS_INPUT="pymntTermsInput";

    public static final String PARAM_PAYMENT_RADIO_STAND="pymntRadioStandar";

    public static final String PARAM_PAYMENT_RADIO_DAYS="pymntRadioDays";

    public static final String PARAM_ESTMTD_ORD_DAY = "estmtdOrdDay";

    public static final String PARAM_ESTMTD_ORD_MONTH = "estmtdOrdMonth";

    public static final String PARAM_ESTMTD_ORD_YEAR = "estmtdOrdYear";
    
    String PARAM_ESTMTD_ORD_DATE="estmtdOrdDate";

    // price engine unaviable exceptiion indicator

    public static final String Price_Engine_UnAvailable = "Price_Engine_UnAvailable";

    public static final String PART_NUM = "partNum";

    public static final String SEQ_NUM = "seqNum";

    public static final String IS_FTL = "isFTL";

    public static final String START_DATE = "startDate";

    public static final String END_DATE = "endDate";

    public static final String PARAM_QUOTE_TYPE = "quoteType";

    public static final String PARAM_PARTNER_ACCESS = "partnerAccess";

    //for approver and attechment keys

    public static final String PARAM_AMENDMENT_FILE = "chooseFile";
    
    public static final String PARAM_JUSTIFICATION_DOCUMENT = "justificationDocument";

    public static final String PARAM_UPLOAD_MANIFEST = "uploadManifest";

    public static final String PARAM_APPROVER_LEVEL = "apprvrLevel";
    
    public static final String PARAM_APPROVER_READY_TO_ORDER_FLAG = "apprvrRdyToOrder";

    public static final String PARAM_APPROVER_GROUP = "approverGroup";

    public static final String PARAM_PRIOR_APPROVER = "priorApprover";

//  public static final String PARAM_SB_ATTACHMENTS = "PARAM_SB_ATTACHMENTS";

    public static final String PARAM_ATTACHMENTS = "attachments";

    public static final String PARAM_SEC_ID = "secId";

    public static final String PARAM_ATT_CODE = "attCode";
    
    // References to internal reporting's parameter keys
    public static final String RPT_SAP_CUST_NUM = "SAP_CUST_NUM";
    public static final String RPT_SAP_CTRCT_NUM = "SAP_CTRCT_NUM";

    public static final String RPT_SAP_CUST_NUM_PGS = "sapCustNum";
    public static final String RPT_SAP_CTRCT_NUM_PGS = "sapCtrctNum";

    public static final String ADDITIONAL_YEARS = "additionalYears";

    public static final String RNW_SEARCH_PARAM1 = "SEARCH_ACTION_NAME";

    public static final String RNW_SEARCH_PARAM2 = "SEARCH_CRITERIA_URL_PARAM";

    public static final String RNW_SEARCH_PARAM_VALUE1 = "quotesrchqtnum";

    public static final String RNW_SEARCH_PARAM_VALUE2 = "QUOTE_NUM";

    public static final String SVP_LEVEL_ID = "ovrrdTranLevelCode";

    public static final String CHANNEL_MODEL_CODE = "channelModelCode";

    public static final String PARAM_ATTCH_SEQ_NUM = "attchmtSeqNum";

    //GPE params
    public static final String IS_SPECIAL_BID_RNWL_PART = "isSpecialBidRnwlPart";


    public static final String ALLOW_EDIT_END_DATE = "allowEditEndDate";

    public static final String ALLOW_EDIT_START_DATE = "allowEditStartDate";

    public static final String SESSION_USER_ID = "session_user_id";


    public static final String PARAM_CHK_EMAIL_Y9_PARTNER_ADDR_LIST = "chkEmailY9PartnerAddrList";
    public static final String PARAM_EMAIL_ADDI_PARTNER_ADDR = "custEmailAddiPartnerAddr";
    public static final String PARAM_VALIDATE_PARTNER_EMAIL_FIELDS = "validatePartnerEmailFields";

    public static final String PARAM_PROG_MIGRTN_CODE = "progMigrtnCode";

    // Special bid email notification paramter keys
    public static final String PARAM_SEND_NO_APPRVR_NOTIF = "sendNoApprvrNotif";

    public static final String PARAM_SEND_MULTI_GRPS_NOTIF = "sendMultiGrpsNotif";

    public static final String PARAM_SEND_ONE_LVL_APPRVR_NOTIF = "sendOneLvlApprvrNotif";

    //price band level override
    public static final String PARAM_PRC_BAND_OVRRD = "prcBandOvrrd";

    public static final String PARAM_NEED_SEND_ADD_MAIL_TO_CREATOR = "needSendAddiMailToCreator";

    public static final String PARAM_CREATOR_EMAIL = "creatorEmail";

    public static final String PARAM_CUST_ADDI_EMAIL_ADDR = "custEmailAddr";

    public static final String PARAM_WEB_CTRCT_ID = "webCtrctId";

    public static final String PARAM_ADDI_SITE_GOV_TYPE_DISPLAY = "isAddiSiteGovTypeDisplay";

   //Parameter for compressed coverage
    public static final String PARAM_CMPRSS_CVRAGE = "cmprssCvrage";

    public static final String PARAM_CMPRSS_CVRAGE_MONTH_SUFFIX = "_cmprssCovrageMonth";
    public static final String PARAM_CMPRSS_CVRAGE_DISC_SUFFIX = "_cmprssCovrageDisc";

    //Cmprss cvrage parameter for draft sales quote date overriding
    public static final String CMPRSS_CVRAGE_APPLIED = "cmprssCvrageApplied";

    //For maximum allowed part number limit
    public static final String PART_LIMIT_EXCEED_CODE = "partLimitExceedCode";

    public static final String PARAM_BLOCK_RENEWAL_REMINDER = "blockReminder";

    public static final String PARAM_BLOCK_RENEWAL_REMINDER_YES = "blockReminderYes";

    public static final String PARAM_BLOCK_RENEWAL_REMINDER_NO = "blockReminderNo";

    public static final String COPY_FROM_SUBMITTED_QUOTE_NUM = "copyFromSubmittedQuoteNum";

    public static final String PARAM_PRE_CREDIT_CHECKED_QUOTE_NUM = "preCreditCheckedQuoteNum";

    // add SaaS param keys
    public static final String CVRAGE_TERM = "_CVRAGE_TERM";
    public static final String BILLING_FREQUENCY = "_BILLING_FREQUENCY";
    public static final String overrideDropdownSuffix = "_OVERRIDE_DROPDOWN";
    public static final String PARAM_OVERRIDE_TYPE = "_OVERRIDE_TYPE";
    public static final String PROVISIONING_DAYS = "_PROVISIONING_DAYS";
    public static final String SERVICE_DATE_MODTYPE = "_SERVICE_DATE_MODTYPE";
    public static final String SERVICE_DATE = "_SERVICE_DATE";
    public static final String TERM_EXTENSION = "_SERVICE_TERM_EXTENSION";
    public static final String CONFGRTN_PREFIX = "CONFGRTN_";
    public static final String RAMPUP_FLAG_SUFFIX = "_RAMPUP_FLAG";
    public static final String RAMPUP_SEQ_NUM_SUFFIX = "_RAMPUP_SEQ_NUM";
    public static final String REPLACED_FLAG_SUFFIX = "_REPLACED_FLAG";
    public static final String END_DATE_SUFFIX = "_END_DATE";
    public static final String SERVICE_AGREEMENT = "serviceAgreement";
    public static final String CO_TERM_SUFFIX = "_CO_TERM";
    public static final String SAAS_RENWL_SUFFIX = "_SAAS_RENWL";	//refer to rtc #207982
    public static final String SAAS_MGRTN_SUFFIX = "_SAAS_MGRTN";
	public static final String BID_ITERATION_SAAS_RENWL_SUFFIX = "_BID_ITERATION_SAAS_RENWL";
	public static final String MIGRATION_FLAG = "MIGRATION_FLAG";
	public static final String MONTHLY_MIGRATION_FLAG = "MONTHLY_MIGRATION_FLAG";
	public static final String RENEWAL_FLAG = "RENEWAL_FLAG";
	public static final String MONTHLY_RENEWAL_FLAG = "MONTHLY_RENEWAL_FLAG";
	public static final String LINE_NEW_SERVICE_SUFFIX = "_LINE_NEW_SERVICE";
	public static final String LINE_DIV_HIDDEN="_LINE_DIV_HIDDEN";
	public static final String LINE_DIV_HIDDEN_SAAS="_LINE_DIV_HIDDEN_SAAS";
	public static final String LINE_DIV_HIDDEN_MONTHLY="_LINE_DIV_HIDDEN_MONTHLY";

    public static final String PARAM_SUPPRS_PA_REGSTRN_EMAIL = "supprsPARegstrnEmailFlag";
    public static final String PARAM_CHK_NO_TAX = "chkNoTax";
    
    public static final String EXTENSION_TYPE_SUFFIX = "_SERVICE_DATE_MODTYPE"; //refer to rtc #424820
    public static final String IS_EXTENSION_DATE_SUFFIX = "_SERVICE_TERM_EXTENSION"; //refer to rtc #424820
    public static final String EXTENSION_DATE_SUFFIX = "_SERVICE_DATE"; //refer to rtc #424820
	public static final String SAAS_LINE_ITEM_NEW_SERVICE_DIV = "SAAS_LINE_ITEM_NEW_SERVICE_DIV";
	public static final String MONTHLY_LINE_ITEM_NEW_SERVICE_DIV = "MONTHLY_LINE_ITEM_NEW_SERVICE_DIV";




    //Appliance param variable.
    public static final String PARAM_YES = "Y";
    public static final String PARAM_NO = "N";


    public static final String APPLNC_MTM_AREA="applnceArea";
	public static final String APPLNC_MTM_TYPE="applncMachineType";
	public static final String APPLNC_MTM_MODEL="applncMachineModel";
	public static final String APPLNC_MTM_SERIAL_NUMBER="applncSerialNumber";
	public static final String APPLNC_POC_IND="applncPocInd";
	public static final String APPLNC_POC_IND_YES="applncPocIndYes";
	public static final String APPLNC_POC_IND_NO = "applncPocIndNo";
	public static final String APPLNC_PRIOR_POC="applncPriorPoc";
	public static final String APPLNC_PRIOR_POC_YES="applncPriorPocYes";
	public static final String APPLNC_PRIOR_POC_NO="applncPriorPocNo";
	public static final String APPLNC_ID="applncId";	
	public static final String NON_IBM_MACHINE_SERIAL_NUM = "nonIBMSerialNumber";
	public static final String NON_IBM_MODEL = "nonIBMMachineModel";
	
	//Saas to FCT migration
	public static final String CA_NUM = "caNum";
	public static final String SAP_CA_NUM = "SAP_CA_NUM";
	public static final String SAP_CUST_NUM = "SAP_CUST_NUM";
	public static final String SAP_CTRCT_NUM = "SAP_CTRCT_NUM";
	public static final String HIGHLIGHT_ID = "HIGHLIGHT_ID";
	public static final String INACT_FLAG = "INACT_FLAG";
	public static final String MIGRATE_PART_LIST = "migratePartList";
	public static final String BILLING_OPTIION_MAP = "billingOptionMap";
	public static final String COVERAGE_TERM = "coverageTerm";
	public static final String DISPLAY_TERM = "displayTerm";
	public static final String BILLING_FREQ = "billingFreq";
	public static final String LINE_NUMS = "lineNums";
	public static final String PART_TOTAL_NUM = "partTotalNum";
	public static final String PAGE_FROM_FCT2PA_CUST_PARTNER = "pageFromFCT2PACustPartner";
	public static final String PAGE_FROM = "pageFrom";
	public static final String FCT2PA_MIGRTN_FLG = "fct2PAMigrtnFlag";
	
	
	//for  Cognos 
	public static final String COGNOS_CUST_NUMBER = "p_Sold to cust";
	public static final String COGNOS_AGREEMENT_NUMBER = "p_SAP_CTRCT_NUM";
	public static final String COGNOS_BUS_CODE = "p_LINE_OF_BUS_CODE";
	
	public static final String NO_APPROVAL_REQUIRE = "NO_APPROVAL_REQUIRE";
	public static final String SAAS_STRMLND_APPRVL_FLAG="saaSStrmlndApprvlFlag";
	public static final String QUOTE_EXTENSION_DAY = "quote_extension_day";

    public static final String YTY_SOURCE_DEFAULT = "0";
	public static final String YTY_RADIO_OVERRIDE_PRICE_VALUE = "1";
	public static final String YTY_RADIO_DISCOUNT_VALUE = "2";
    public static final String YTY_RADIO_YTY_GROWTH_VALUE = "3";

  //auto renew model
	public static final String PARAM_RENEWAL_MODEL_CONFIGURTN="_RENEWAL_MODEL_CONFIGURTN";
	public static final String PARAM_RENEWAL_MODEL_SUBSCRPTN="_RENEWAL_MODEL_SUBSCRPTN";
	public static final String DISPLAY_LEVEL_SUBSCRPTN="DISPLAY_LEVEL_SUBSCRPTN";
	public static final String DISPLAY_LEVEL_CONFIGURTN="DISPLAY_LEVEL_CONFIGURTN";
	
	//For ship to / install at section
	public static final String PARAM_SHIP_TO_ADDRESS="shipToOption";
	public static final String PARAM_SHIP_TO_ADDRESS_SOLD_TO_ALL_S="shipToAddressSoldToAllS";
	public static final String PARAM_SHIP_TO_ADDRESS_NEW_SHIP_TO="shipToAddressNewShipTo";
	public static final String PARAM_INSTALL_AT_ADDRESS = "installAtOption";
	public static final String PARAM_SHIP_TO_ADDRESS_SOLD_TO_ALL_I="shipToAddressSoldToAllI";
	public static final String PARAM_USE_SHIP_TO_ADDRESS = "useShipToAddress";
	public static final String PARAM_INSTALL_AT_ADDRESS_NEW_INSTALL = "installAtAddressNewInstall";
	public static final String PARAM_CNT_FIRST_NAME = "cntFirstName";
	public static final String PARAM_CNT_LASE_NAME = "cntLastName";
	public static final String PARAM_SAP_INTL_PHONE_NUM_FULL= "sapIntlPhoneNumFull";
	public static final String PARAM_QUOTE_LINE_ITEM_SEQ_NUM= "quoteLineItemSeqNum";
	public static final String PARAM_CUST_NUM = "custNum";
	public static final String PARAM_CNT_ID = "cntId";
	public static final String PARAM_SAP_CNT_ID = "sapCntId";
	public static final String PARAM_WEB_CUST_ID = "webCustId";
	public static final String PARAM_RENEWAL_NUM = "renewalNum";
	public static final String PARAM_PRIOR_PRICE = "priorPrice";
	public static final String PARAM_SYSTEM_PRIOR_PRICE = "systemPriorPrice";
	public static final String PARAM_GROWTH_YTY_PART_FLAG = "gdPartFlag";
	public static final String PARAM_RETURN_CODE = "returnCode";
	public static final String PARAM_RETURN_CODE_SUCCESS = "1";
	public static final String PARAM_RETURN_CODE_FAILURE = "0";
	public static final String PARAM_YTY_GROWTH = "ytyGrowth";
	

    public static final String PARAM_TOU_URL = "touURL";
    public static final String PARAM_TOU_NAME = "touName";
    public static final String TERMS_TYPE = "termsType";
    public static final String TERMS_SUBTYPE = "termsSubType";
    public static final String SAAS_TERM_CONDCAT_FLAG = "saasTermCondCatFlag";
    
    public static final String APPLNC_DEPLOY_ASSOCIATION = "applncDeployAssociation";
    public static final String APPLNC_DEPLOY_ID = "applncDeployId";
    public static final String APPLNC_DEPLOY_ID_SYSTEM = "applncDeployId_hidden";
    public static final String APPLNC_DEPLOY_ID_VALID = "applncDeployId_valid";
    public static final String APPLNC_DEPLOY_ID_FOR_UNDO = "deploymentIdForUndo";
	
    public static final String ACTION_CODE = "actionCode";
    public static final String PARAM_PA_CONTRACT_NUM = "PAContractNum";
	public static final String PARAM_CSRA_CONTRACT_NUM = "CSRAContractNum";
	public static final String PARAM_CURRENT_CONTRACT_NUM = "contractNum";

	public static final String PARAM_CSA_TERMS_COUNT = "CSATermsCount";
	
	public static final String EXP_DATE_EXTENSION_INPUT = "expDateExtensionDay";
	 
	public static final String EXP_DATE_EXTENSION_JUSTIFICATION_INPUT = "expDateExtensionJustification";
	
	public static final String PARAM_EXP_DATE_EXTENSION_DAY = "expDateExtensionDay";

    public static final String PARAM_EXP_DATE_EXTENSION_MONTH = "expDateExtensionMonth";

    public static final String PARAM_EXP_DATE_EXTENSION_YEAR = "expDateExtensionYear";
    
    public static final String PARAM_EXP_DATE_EXTENSION_DATE="expDateExtensionDate"; 
    
    public static final String PARAM_EXP_DATE_EXTENSION_JUSTIFICATION="expDateExtensionJustification";
}