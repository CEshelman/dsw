package com.ibm.dsw.quote.common.process;

import is.domainx.User;

import java.util.Map;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.exception.SPException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteCapabilityProcess</code> class is to determine if a certain
 * function in quote tool is available at the moment or not.
 *
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Apr 3, 2007
 */
public interface QuoteCapabilityProcess {
    String APPLIANCE_PARTS_POC_VALIDATED="APPLIANCE_PARTS_POC_VALIDATED";

	String OL_CALCULATE_NEEDED="OL_CALCULATE_NEEDED";
	
	String SOFTWARE_PART_WITHOUT_APPINC_ID ="SOFTWARE_PART_WITHOUT_APPINC_ID";
	
    String OL_UPDATE_NEEDED="OL_UPDATE_NEEDED";

	String EC_RECALCULATE_NEEDED="EC_RECALCULATE_NEEDED"; 

	String MTM_APPLIANCE_QTY_GREATER_THAN_ONE="MTM_APPLIANCE_QTY_GREATER_THAN_ONE";

	String MTM_APPLIANCE_QTY_GREATER_THAN_ONE_2="mtm_appliance_qty_greater_than_one";
	
	String APP_POC_IND_SELECTED="appliance_poc_indctr_must_be_selected";

	String APPLIANCE_QTY_HDR = "appliance_qty_hdr";
	
	String APPLIANCE_POC_IND = "appliance_poc_indctr";

	String TOTAL_QTY_EXCEED_LIMIT="TOTAL_QTY_EXCEED_LIMIT";

	String RELATED_ITEM_QTY_EXCEED_APPLIANCE="RELATED_ITEM_QTY_EXCEED_APPLIANCE";

    String CAN_VERIFY_AND_SUBMIT = "CAN_VERIFY_AND_SUBMIT";

    String EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS = "EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS";

    String EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS_BID_ITRTN = "EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS_BID_ITRTN";

    String INVALID_QUOTE_START_DATE = "INVALID_QUOTE_START_DATE";

    String QT_START_DATE_BEFORE_TODAY = "QT_START_DATE_BEFORE_TODAY";

    String QT_START_DATE_IN_FUTURE = "QT_START_DATE_IN_FUTURE";

    String CUSTOMER_NOT_SELECTED = "CUSTOMER_NOT_SELECTED";

    String CONTRACT_NOT_ACTIVE = "CONTRACT_NOT_ACTIVE";
    
    String EMAIL_LENGTH_EXCEED_MAX = "EMAIL_LENGTH_EXCEED_MAX";

    String FULFILLMENT_SRC_NOT_SET = "FULFILLMENT_SRC_NOT_SET";

    String QT_CLASSFCTN_NOT_SET = "QT_CLASSFCTN_NOT_SET";

    String QT_OEM_AGREEMENT_TYPE_NOT_SET = "QT_OEM_AGREEMENT_TYPE_NOT_SET";

    String RESELLER_NOT_SELECT = "RESELLER_NOT_SELECT";

    String RESELLER_NOT_SELECT_FOR_PA_PAE = "RESELLER_NOT_SELECT_FOR_PA_PAE";

    String DISTRIBUTOR_NOT_SELECT = "DISTRIBUTOR_NOT_SELECT";

    String DISTRIBUTOR_NOT_SELECT_FOR_PA_PAE = "DISTRIBUTOR_NOT_SELECT_FOR_PA_PAE";

    final String RESELLER_SHOULD_NOT_TIER1_FOR_SINGLE_TIER_MODEL = "RESELLER_SHOULD_NOT_TIER1_FOR_SINGLE_TIER_MODEL";

    final String DISTRIBUTOR_SHOULD_NOT_TBD_FOR_RESELLER = "DISTRIBUTOR_SHOULD_NOT_TBD_FOR_RESELLER";

    String CAN_VERIFY_AND_ORDER = "CAN_VERIFY_AND_ORDER";

    String SPECIAL_BID_INDICATOR_NOT_FALSE = "SPECIAL_BID_INDICATOR_NOT_FALSE";

    String PART_QTY_NOT_SET = "PART_QTY_NOT_SET";

    String PVU_PART_QTY_NOT_RIGHT = "PVU_PART_QTY_NOT_RIGHT";

    String PART_NOT_ACTIVE = "PART_NOT_ACTIVE";

    String PART_NOT_HAS_PRICE = "PART_NOT_HAS_PRICE";

    String ELA_HAS_NO_LINE_ITM_OR_SPRDSHT = "ELA_HAS_NO_LINE_ITM_OR_SPRDSHT";

    String FULFILLMENT_SRC_NOT_DIRECT = "FULFILLMENT_SRC_NOT_DIRECT";

    String BRIEF_TITLE_NOT_FILLED = "BRIEF_TITLE_NOT_FILLED";

    String OPP_NUM_OR_EXEMP_CODE_NOT_SET = "OPP_NUM_OR_EXEMP_CODE_NOT_SET";

    String BUS_ORG_NOT_SELECTED = "BUS_ORG_NOT_SELECTED";

    String PRICE_LEVEL_NOT_SET = "PRICE_LEVEL_NOT_SET";

    String IS_OVERRIDDEN_PRICE_LEVEL = "IS_OVERRIDDEN_PRICE_LEVEL";

    String AUD_CAN_NOT_SUBMIT_QUOTE = "AUD_CAN_NOT_SUBMIT_QUOTE";

    String PA_QUOTE_HAS_NON_PA_CUST = "PA_QUOTE_HAS_NON_PA_CUST";

    String EXP_DATE_NOT_WITHIN_SBMT_MONTH = "EXP_DATE_NOT_WITHIN_SBMT_MONTH";

    String FCT_RQ_STATUS_INVALID = "FCT_RQ_STATUS_INVALID";

    String RQ_NOT_SPECALL_BIDABLE = "RQ_NOT_SPECALL_BIDABLE";

    String EDIT_RQ = "EDIT_RQ";

    String EDIT_GPE_RQ = "EDIT_GPE_RQ";

    String EDIT_RQ_STATUS = "EDIT_RQ_STATUS";

    String EDIT_RQ_ST = "EDIT_RQ_ST";

    String ORDER_RQ = "ORDER_RQ";

    String CREATE_RQ_SPECL_BID = "CREATE_RQ_SPECL_BID";

    String ADD_RQ_TO_SQ = "ADD_RQ_TO_SQ";

    String IS_SALES_REP = "IS_SALES_REP";

    String IS_QUOTING_REP = "IS_QUOTING_REP";

    String IS_USER_VALID = "IS_USER_VALID";

    String RQ_MOD_DATE = "RQ_MOD_DATE";

    String SPECIAL_BID_EXP_TEXT_NOT_INPUT = "SPECIAL_BID_EXP_TEXT_NOT_INPUT";

    String RQ_STATUS_MOD_DATE = "RQ_STATUS_MOD_DATE";

    String SPECIAL_BID_REGION_NOT_INPUT = "SPECIAL_BID_REGION_NOT_INPUT";

    String RQ_IDOC_NUM = "RQ_IDOC_NUM";

    String SPECIAL_BID_DISTRICT_NOT_INPUT = "SPECIAL_BID_DISTRICT_NOT_INPUT";

    String EVAL_ACTION_NOT_INPUT = "EVAL_ACTION_NOT_INPUT";

    String EVAL_COMMENT_NOT_INPUT = "EVAL_COMMENT_NOT_INPUT";

    String SPECIAL_BID_CUST_IND_SEGMENT_NOT_INPUT = "SPECIAL_BID_CUST_IND_SEGMENT_NOT_INPUT";

    String SPECIAL_BID_TYPE_NOT_INPUT = "SPECIAL_BID_TYPE_NOT_INPUT";

    String SPECIAL_BIT_JUST_TEXT_NOT_INPUT = "SPECIAL_BIT_JUST_TEXT_NOT_INPUT";

    String QUOTE_STATUS_NOT_ALLOW_ORDER = "QUOTE_STATUS_NOT_ALLOW_ORDER";

    String REASON_FOR_CHANGE_REQUIRED = "REASON_FOR_CHANGE_REQUIRED";

    String REASON_FOR_INSERT_REQUIRED = "REASON_FOR_INSERT_REQUIRED";

    String REASON_FOR_DELETE_REQUIRED = "REASON_FOR_DELETE_REQUIRED";

    String SALES_ODDS_REQUIRED = "SALES_ODDS_REQUIRED";

    String PARTNER_ACCESS_NOT_SET = "PARTNER_ACCESS_NOT_SET";

    String SALES_ORG_NOT_SELECTED = "SALES_ORG_NOT_SELECTED";

    String UPSIDE_TRAN_NOT_SELECT = "UPSIDE_TRAN_NOT_SELECT";

    String QUOTE_STATUS_NOT_SET = "QUOTE_STATUS_NOT_SET";

    String SPEC_BID_NOT_PERMITTED = "SPEC_BID_NOT_PERMITTED";

    String CUST_PARTNER_CURRENCY_MISMATCH = "CUST_PARTNER_CURRENCY_MISMATCH";

    String CUST_PARTNER_REGION_MISMATCH = "CUST_PARTNER_REGION_MISMATCH";

    String CUST_PARTNER_COUNTRY_MISMATCH = "CUST_PARTNER_COUNTRY_MISMATCH";

    String CUST_PAYER_CURRENCY_MISMATCH = "CUST_PAYER_CURRENCY_MISMATCH";

    String OFFER_PRICE_MISMATCH = "OFFER_PRICE_MISMATCH";

    String RSEL_NOT_AUTH_TO_PORTFOLIO = "RSEL_NOT_AUTH_TO_PORTFOLIO";

    String RSEL_NOT_AUTH_TO_PORTFOLIO_TBD = "RSEL_NOT_AUTH_TO_PORTFOLIO_TBD";

    String DIST_NOT_ASSOC_TO_RESELLER = "DIST_NOT_ASSOC_TO_RESELLER";

    String RSEL_NOT_AUTH_TO_SELL = "RSEL_NOT_AUTH_TO_SELL";

    String INVALID_DISTRIBUTOR = "INVALID_DISTRIBUTOR";

    String WRNG_RSEL_DSTRBTR_CMBNTN = "WRNG_RSEL_DSTRBTR_CMBNTN";

    String DEL_ITEM_REF_BY_SQ = "DEL_ITEM_REF_BY_SQ";

    String HAS_INAVLID_OBSOLETE_PARTS = "HAS_INAVLID_OBSOLETE_PARTS";

    String PAE_HAS_RQ_PARTS = "PAE_HAS_RQ_PARTS";

    String ORIG_SALES_ORD_NUM_INVALID = "ORIG_SALES_ORD_NUM_INVALID";

    String QUOTE_PRORATE_PART_EXPIRATION_MESSAGE = "QUOTE_PRORATE_PART_EXPIRATION_MESSAGE";

    String QUOTE_EXP_DATE_BEFORE_TODAY = "QUOTE_EXP_DATE_BEFORE_TODAY";
    String QT_LINE_ITM_START_END_DATE_INVALID = "QT_LINE_ITM_START_END_DATE_INVALID";
    String TERM_NOT_EQL_DVSBL_BY_BILLG_OPTN_MSG = "TERM_NOT_EQL_DVSBL_BY_BILLG_OPTN_MSG";
    String TERM_OR_BILLG_OPTN_NOT_SPECIFY_MSG = "TERM_OR_BILLG_OPTN_NOT_SPECIFY_MSG";
    String NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG = "NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG";
    String INVALID_ESTMTD_ORD_DATE = "INVALID_ESTMTD_ORD_DATE";
    String INVALID_CUST_REQSTD_ARRIVL_DATE = "INVALID_CUST_REQSTD_ARRIVL_DATE";
    String ENTER_CUST_REQSTD_ARRIVL_DATE = "ENTER_CUST_REQSTD_ARRIVL_DATE";
    String ENTER_VALID_ESTMTD_ORD_DATE = "ENTER_VALID_ESTMTD_ORD_DATE";
    String QT_START_DATE_BEFORE_CALCD_START_DATE = "QT_START_DATE_BEFORE_CALCD_START_DATE";
    String EXPIRED_DATE_AFTER_CALCD_EXPIRED_DATE = "EXPIRED_DATE_AFTER_CALCD_EXPIRED_DATE";
    String QT_START_DATE_AFTER_CALCD_EXPIRED_DATE = "QT_START_DATE_AFTER_CALCD_EXPIRED_DATE";
    String ESTMTD_ORD_DATE_NOT_WITHIN_ACTIVIE_DAYS = "ESTMTD_ORD_DATE_NOT_WITHIN_ACTIVIE_DAYS";
    String CONFIGRTN_STALE_MSG = "CONFIGRTN_STALE_MSG";
    String SBSCRPTB_TCV_LOWER_THAN_ORGNL = "SBSCRPTB_TCV_LOWER_THAN_ORGNL";
    String CA_CUST_CURRNCY_NOT_MATCH = "CA_CUST_CURRNCY_NOT_MATCH";
    String TERMS_LESS_THAN_1_MONTH_MSG = "TERM_NOT_ALLOW_LESS_THAN_1_MONTH";
    String MSG_SQ_HAS_DIVESTED_PART = "MSG_SQ_HAS_DIVESTED_PART";
    String MSG_RQ_HAS_DIVESTED_PART = "MSG_RQ_HAS_DIVESTED_PART";

    String TERM_EXTENSIONS_MUST_INCLUDE_THE_ENTIRE_CONFIGURATION = "TERM_EXTENSIONS_MUST_INCLUDE_THE_ENTIRE_CONFIGURATION";

    String TERM_EXTENSIONS_EXTENSION_HAS_NOT_BEEN_SPECIFIED = "TERM_EXTENSIONS_EXTENSION_HAS_NOT_BEEN_SPECIFIED";

    String EXISTING_CA_AND_NEW_CA_RULE_MSG = "EXISTING_CA_AND_NEW_CA_RULE_MSG";
    
    // Renewal quote common action buttons display rule
    String DISPLAY_CNVRT_TO_SALES_QT_BTN = "DISPLAY_CNVRT_TO_SALES_QT_BTN";
    String DISPLAY_SUBMIT_FOR_APPR_BTN = "DISPLAY_SUBMIT_FOR_APPR_BTN";
    String DISPLAY_SUBMIT_AS_FINAL_BTN = "DISPLAY_SUBMIT_AS_FINAL_BTN";
    String DISPLAY_ORDER_BTN = "DISPLAY_ORDER_BTN";
    String DISPLAY_CTRCT_NOT_ACTIVE_MSG = "DISPLAY_CTRCT_NOT_ACTIVE_MSG";
    String DISPLAY_PART_NO_RPC_MSG = "DISPLAY_PART_NO_RPC_MSG";
    String DISPLAY_SB_NOT_PERMITTED_MSG = "DISPLAY_SB_NOT_PERMITTED_MSG";
    String DISPLAY_CNVT_TO_PAE_BTN = "DISPLAY_CNVT_TO_PAE_BTN";
    String DISPLAY_DOWNLOAD_RICH_TEXT_BTN = "DISPLAY_DOWNLOAD_RICH_TEXT_BTN";
    String DISPLAY_BLUE_PAGE_UNAVAILABLE_MSG = "DISPLAY_BLUE_PAGE_UNAVAILABLE_MSG";
    String DISPLAY_CREATE_SPECL_BID_BTN = "DISPLAY_CREATE_SPECL_BID_BTN";
    String DISPLAY_HAS_OBSLT_PART_MSG = "DISPLAY_HAS_OBSLT_PART_MSG";
    String DISPLAY_EXPRT_SPRDSHT_BTN = "DISPLAY_EXPRT_SPRDSHT_MSG";
    String DISPLAY_NOT_SPECL_BIDABLE_MSG = "DISPLAY_NOT_SPECL_BIDABLE_MSG";
    String DISPLAY_ACCEPT_QUOTE = "DISPLAY_ACCEPT_QUOTE";

    // Sales quote common action buttons display rule
    String DISPLAY_SAVE_QT_AS_DRAFT_BTN = "DISPLAY_SAVE_QT_AS_DRAFT_BTN";
    String DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN = "DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN";
    String DISPLAY_EMAIL_DRAFT_QUOTE_BTN = "DISPLAY_EMAIL_DRAFT_QUOTE_BTN";
    String DISPLAY_NO_CUST_OR_PART_MSG = "DISPLAY_NO_CUST_OR_PART_MSG";
    String DISPLAY_NEW_COPY_BTN = "DISPLAY_NEW_COPY_BTN";
    String DISPLAY_NON_PA_CUST_MSG = "DISPLAY_NON_PA_CUST_MSG";
    String DISPLAY_EFF_DATE_IN_FTR_MSG = "DISPLAY_EFF_DATE_IN_FTR_MSG";
    String DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG = "DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG";
    String DISPLAY_OEM_NOT_ALLOW_ORDER_MSG = "DISPLAY_OEM_NOT_ALLOW_ORDER_MSG";
    String DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG = "DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG";
    String DISPLAY_NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG = "DISPLAY_NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG";

    // Submitted sales quote common action buttons display rule
    String DISPLAY_OVERRIDE_EXP_DATE = "DISPLAY_OVERRIDE_EXP_DATE";
    String DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST = "DISPLAY_APPR_ENTER_ADDI_SB_JUST";
    String DISPLAY_SUBMITTER_SUBMIT_REQUEST = "DISPLAY_SUBMITTER_SUBMIT_REQUEST";
    String DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG = "DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG";
    String DISPLAY_QT_PROCESS_IN_SAP_MSG = "DISPLAY_QT_PROCESS_IN_SAP_MSG";
    String DISPLAY_NO_CUST_ENROLL_MSG = "DISPLAY_NO_CUST_ENROLL_MSG";
    String DISPLAY_HAS_ORDERED_MSG = "DISPLAY_HAS_ORDERED_MSG";
    String DISPLAY_CANCELLED_QT_MSG = "DISPLAY_CANCELLED_QT_MSG";
    String DISPLAY_SB_QT_MSG = "DISPLAY_SB_QT_MSG";
    String DISPLAY_SBMT_ACL_REQ_MSG = "DISPLAY_SBMT_ACL_REQ_MSG";
    String DISPLAY_CHANNEL_QT_ORDER_MSG = "DISPLAY_CHANNEL_QT_ORDER_MSG";
    String DISPLAY_EXPIRED_SB_QT_CANNOT_ORDERED_MSG = "DISPLAY_EXPIRED_SB_QT_CANNOT_ORDERED_MSG";
    String DISPLAY_EXPIRATION_DATE_PASSED_MSG = "DISPLAY_EXPIRATION_DATE_PASSED_MSG";
    String DISPLAY_SB_NOT_ACTIVE_MSG = "DISPLAY_SB_NOT_ACTIVE_MSG";
    String DISPLAY_EXEC_SMMRY_MSG = "DISPLAY_EXEC_SMMRY_MSG";
    String DISPLAY_ELA_QT_ORDER_MSG = "DISPLAY_ELA_QT_ORDER_MSG";
    String DISPLAY_SVP_LVL_NOT_MATCH_MSG = "DISPLAY_SVP_LVL_NOT_MATCH_MSG";
    String DISPLAY_CTRCT_NUM_NOT_MATCH_MSG = "DISPLAY_CTRCT_NUM_NOT_MATCH_MSG";
    String DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG = "DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG";//control the message for hidden order button

    String QUOTE_SPBID_FULFILLMENT_INVALID = "QUOTE_SPBID_FULFILLMENT_INVALID";
    String QUOTE_FULFILLMENT_INVALID = "QUOTE_FULFILLMENT_INVALID";
    String DISPLAY_ICN_REQUIRED = "DISPLAY_ICN_REQUIRED";
    String DISPLAY_PRE_CREDIT_CHK_REQUIRED = "DISPLAY_PRE_CREDIT_CHK_REQUIRED";
    String DISPLAY_UPDATE_QUOTE_BTN = "DISPLAY_UPDATE_QUOTE_BTN";
    String DISPLAY_CREATE_COPY_BTN = "DISPLAY_CREATE_COPY_BTN";
    String DISPLAY_CREATE_COPY_CHECKBOX_MSG = "DISPLAY_CREATE_COPY_CHECKBOX_MSG";
    String DISPLAY_CANCEL_QUOTE_BTN = "DISPLAY_CANCEL_QUOTE_BTN";
    String DISPLAY_APPROVER_ACTION = "DISPLAY_APPROVER_ACTION";
    String DISPLAY_APPROVER_ADDI_COMMENTS = "DISPLAY_APPROVER_ADDI_COMMENTS";
    String DISPLAY_REVIEWER_SAVE_COMMENTS = "DISPLAY_REVIEWER_SAVE_COMMENTS";

    // Submitted renewal quote common action buttons display rule
    String DISPLAY_UPDATE_SPEC_BID_BTN = "DISPLAY_UPDATE_SPEC_BID_BTN";
    String DISPLAY_CANCEL_SPEC_BID_BTN = "DISPLAY_CANCEL_SPEC_BID_BTN";

    String CONTACT_EMAIL_ADR_BLANK = "CONTACT_EMAIL_ADR_BLANK";

    String CONTACT_PHONE_NUM_BLANK = "CONTACT_PHONE_NUM_BLANK";

    String CONTACT_FIRST_NAME_BLANK = "CONTACT_FIRST_NAME_BLANK";

    String CONTACT_LAST_NAME_BLANK = "CONTACT_LAST_NAME_BLANK";

    //Back dating
    String SELECT_BACK_DTG_REASON = "SELECT_BACK_DTG_REASON";

    String DISPLAY_CANCEL_APPROVED_BID = "DISPLAY_CANCEL_APPROVED_BID";

    String DISPLAY_COPY_SB_FOR_PRICE_INCREASE = "DISPLAY_COPY_SB_FOR_PRICE_INCREASE";
    
    String DISPLAY_COPY_FOR_EXPRIY_DATE = "DISPLAY_COPY_FOR_EXPRIY_DATE";

    String OFFER_PRICE_NOT_VALID = "OFFER_PRICE_NOT_VALID";

    String DISPLAY_BID_ITERATION_BTN = "DISPLAY_BID_ITERATION_BTN";

    String NOT_MEET_BID_ITERATION = "NOT_MEET_BID_ITERATION";

    String DISPLAY_CONV_TO_STD_COPY_BTN = "CONV_TO_STD_COPY_BTN";

    String OEM_BID_TYPE_NOT_SET_MSG = "OEM_BID_TYPE_NOT_SET_MSG";

    String DISPLAY_COPY_SB_FOR_CHNAGE_OUTPUT = "DISPLAY_COPY_SB_FOR_CHNAGE_OUTPUT";

    String INVALID_SB_REASON_CODE_PGS = "INVALID_SB_REASON_CODE_PGS";

    String NO_APPROVER_RETURNED = "NO_APPROVER_RETURNED";

    String NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG = "NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG";

	String DISPLAY_PGS_SUBMITTER = "DISPLAY_PGS_SUBMITTER";

	String DISPLAY_ORD_WITHOUT_OPPTNUM_MSG = "DISPLAY_ORD_WITHOUT_OPPTNUM_MSG";

	String DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG = "DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG";

	String MACHINE_TYPE_NOT_NULL = "MACHINE_TYPE_NOT_NULL";

	String MACHINE_MODEL_NOT_NULL = "MACHINE_MODEL_NOT_NULL";

	String MACHINE_SERIAL_NUMBER_NOT_NULL = "MACHINE_SERIAL_NUMBER_NOT_NULL";

	String MAGRATION_CUSTOMER_NOT_SELECTED = "MAGRATION_CUSTOMER_NOT_SELECTED";

	String MAGRATION_RESELLER_NOT_SELECT = "MAGRATION_RESELLER_NOT_SELECT";

	String MAGRATION_DISTRIBUTOR_NOT_SELECT = "MAGRATION_DISTRIBUTOR_NOT_SELECT";

	String MAGRATION_LINE_ITEM_NOT_SELECT = "MAGRATION_LINE_ITEM_NOT_SELECT";

	String MIGRATION_FULFILLMENT_SRC_NOT_SET = "MIGRATION_FULFILLMENT_SRC_NOT_SET";

	String FEDERAL_CUSTOMERS_MUST_PURCHASE_NEW_SYSTEM_APPLIANCE = "FEDERAL_CUSTOMERS_MUST_PURCHASE_NEW_SYSTEM_APPLIANCE";

	String APPLIANCE_SERIAL_NUMBER_NOT_SAME = "APPLIANCE_SERIAL_NUMBER_NOT_SAME";
	
	String OWNERSHIP_PARTS_SERIAL_NUMBER_NOT_SAME = "OWNERSHIP_PARTS_SERIAL_NUMBER_NOT_SAME";

	String APPLIANCE_PARTS_DEPLOYMENTID_NOTBE_VALIDATED = "APPLIANCE_PARTS_DEPLOYMENTID_NOTBE_VALIDATED";

	String FCT_TO_PA_MGT_TERM_NOT_SAME = "FCT_TO_PA_MGT_TERM_NOT_SAME";

    String CANNOT_CALC_SAAS_CONF_DUE_END_DATE_MESSAGE = "CANNOT_CALC_SAAS_CONF_DUE_END_DATE_MESSAGE";

    String REMAINING_SUBSCRPT_TERM_CANNOT_LESS_THAN_ZERO="REMAINING_SUBSCRPT_TERM_CANNOT_LESS_THAN_ZERO";

	String DISPLAY_CANNOT_SUBMIT_WITH_SOFTPART_IN_PGS_MSG = "DISPLAY_CANNOT_SUBMIT_WITH_SOFTPART_IN_PGS_MSG";

	String DISPLAY_EVALUATOR_OPTIONS_SECTION = "DISPLAY_EVALUATOR_OPTIONS_SECTION";

	String SSP_FILL_IN_END_USER = "SSP_FILL_IN_END_USER";

	String SSP_CA_FILL_IN_END_USER = "SSP_CA_FILL_IN_END_USER";

	String SSP_CLEAR_OUT_END_USER = "SSP_CLEAR_OUT_END_USER";

	String SSP_CA_CLEAR_OUT_END_USER = "SSP_CA_CLEAR_OUT_END_USER";

	String SSP_TYPE_NOT_SELECT = "SSP_TYPE_NOT_SELECT";

	String GRWTH_DLGTN_MISSING_YTY = "GRWTH_DLGTN_MISSING_YTY";

	String MSG_EXCEED_RSVP_PRICE = "MSG_EXCEED_RSVP_PRICE";

	String EXTENSION_END_DATE_VALID_MESSAGE="EXTENSION_END_DATE_VALID_MESSAGE";

	String EXTENSION_START_DATE_VALID_MESSAGE="EXTENSION_START_DATE_VALID_MESSAGE";

	String EXTENSION_TYPE_VALID_MESSAGE="EXTENSION_TYPE_VALID_MESSAGE";

	String QUOTE_START_DATE_NOT_AFTER_EARLIEST_PROVISIONING = "QUOTE_START_DATE_NOT_AFTER_EARLIEST_PROVISIONING";

	String QUOTE_EXPIR_DATE_NOT_AFTER_LASTEST_PROVISIONING = "QUOTE_EXPIR_DATE_NOT_AFTER_LASTEST_PROVISIONING";

	String SUBMIT_PART_LOST_TOU_MSG = "SUBMIT_PART_LOST_TOU_MSG";
	
	String ORDER_PART_LOST_TOU_MSG = "ORDER_PART_LOST_TOU_MSG";

	String SPECIFY_TERMS_CONDITIONS_COVERING = "SPECIFY_TERMS_CONDITIONS_COVERING";
	
	String SUBMIT_PART_SAAS_ONLY_CSA_TOU_MSG = "SUBMIT_PART_SAAS_ONLY_CSA_TOU_MSG";
	
	String SUBMIT_CSA_CHANNEL_TOU_MSG = "SUBMIT_CSA_CHANNEL_TOU_MSG";
	
	String MISSING_CSA_TERMS_ORDER = "MISSING_CSA_TERMS_ORDER";
	
	String MISSING_CSA_TERMS_SUBMIT ="MISSING_CSA_TERMS_SUBMIT";
	
	String MISSING_ACTIVE_PART_B ="MISSING_ACTIVE_PART_B";
	
	String PLACEHOLDER_ORDER_MSG = "PLACEHOLDER_ORDER_MSG";
	
	String TOU_AMENDMENT_BLANK_MSG = "TOU_AMENDMENT_BLANK_MSG";
	
	String CSA_TOU_AMENDMENT_BLANK_MSG = "CSA_TOU_AMENDMENT_BLANK_MSG";
	
	String DISPLAY_SSP_NO_CONTACT = "DISPLAY_SSP_NO_CONTACT";
	
	String NOT_ALLOW_ORDER_FOR_DIVESTED_PART="NOT_ALLOW_ORDER_FOR_DIVESTED_PART";
	
	String NOT_COPY_CHNAGE_OUTPUT_FOR_DIVESTED_PART="NOT_COPY_CHNAGE_OUTPUT_FOR_DIVESTED_PART";
	
	String NOT_COPY_PRICE_INCREASE_FOR_DIVESTED_PART="NOT_COPY_PRICE_INCREASE_FOR_DIVESTED_PART";
	
	String NOT_COPY_BID_ITERATION_FOR_DIVESTED_PART="NOT_COPY_BID_ITERATION_FOR_DIVESTED_PART";
	
	String GOE_QUOTE_WITH_TBD_PARTNER = "GOE_QUOTE_WITH_TBD_PARTNER";
	
	String GOE_QUOTE_WITH_CEID_PARTNER = "GOE_QUOTE_WITH_CEID_PARTNER";
	
	String ANSWER_SAAS_MONTHLY_QUESTION="ANSWER_SAAS_MONTHLY_QUESTION";
	
	String SPECIFY_CHANNEL_DISCOUNT_OVERRIDE_REASON = "SPECIFY_CHANNEL_DISCOUNT_OVERRIDE_REASON";

	String ENTER_VALID_EXTENSION_EXP_DATE="ENTER_VALID_EXTENSION_EXP_DATE";
	
	String EXP_DATE_EXTENSION_MUST_BE_FILLED="EXP_DATE_EXTENSION_MUST_BE_FILLED";
	
	String EXP_DATE_EXTENSION_JUSTIFICATION_MUST_BE_FILLED="EXP_DATE_EXTENSION_JUSTIFICATION_MUST_BE_FILLED";
	
	String EXTENSION_EXP_DATE_MUST_BE_ON_OR_AFTER_ORIGINAL_EXP_DATE="EXTENSION_EXP_DATE_MUST_BE_ON_OR_AFTER_ORIGINAL_EXP_DATE";
	
	String EXTENSION_EXP_DATE_MUST_BE_ON_OR_BEFORE_ORIGINAL_EXP_DATE_QUARTER_LAST_DAY="EXTENSION_EXP_DATE_MUST_BE_ON_OR_BEFORE_ORIGINAL_EXP_DATE_QUARTER_LAST_DAY";
	
    public Map validateForSubmissionAsFinal(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException;

    /**
     * @param user
     * @param quote
     * @param cookie
     * @return
     */
    public Map validateSubmissionForApproval(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException;

    public Map validateForOrder(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException;
    
    public Map validateTouForOrder(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException;
    
    public boolean isSystemInitiatedSpBid(Quote quote, String userId) throws QuoteException;

    public Map getRnwlQuoteStatus(String quoteNum, String userIdList, int qaNeeded, int steNeeded, int duration,
            boolean checkSalesRep) throws SPException, QuoteException;

    public int validateEditRQ(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)throws QuoteException;

    public int validateEditGPExprdRQ(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException;

    public int validateEditRQStatus(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)throws QuoteException;

    public int validateEditRQSalesTracking(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)throws QuoteException;

    public int validateOrderRQ(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)throws QuoteException;

    public int validateCreateRQSpeclBid(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException;

    public Map getDraftQuoteActionButtonsRule(QuoteUserSession user, Quote quote, QuoteUserSession salesRep);

    public Map getSubmittedQuoteActionButtonsRule(QuoteUserSession user, Quote quote);

    //public boolean validateLineItemRevnStrm(Quote quote) throws QuoteException;

    public boolean validateLineItemNOTReferRQ(Quote quote) throws QuoteException;

    public QuoteAccess getRnwlQuoteAccess(String userId, String renwlQuoteNum) throws QuoteException;

    public Map validateFCTToPAMigrationRequest(QuoteUserSession user, MigrateRequest migrateRequest) throws QuoteException ;

    public boolean isSubmitPGSLevel0SPBid(QuoteUserSession user, Quote quote, Cookie cookie)throws QuoteException;


}
