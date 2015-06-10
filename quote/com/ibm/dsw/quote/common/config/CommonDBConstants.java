package com.ibm.dsw.quote.common.config;

import com.ibm.dsw.quote.base.config.DBConstants;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>CommonDBConstants</code> class contains db2 constants for common domain objects
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Feb 5, 2007
 */

public interface CommonDBConstants extends DBConstants {
	//sp names
	
	public static final String DB2_IU_QT_LINE_ITEM_YTY = "IU_QT_LINE_ITEM_YTY";
	public static final String DB2_D_QT_LINE_ITEM_YTY = "D_QT_LINE_ITEM_YTY";
	public static final String DB2_IU_QT_PYP = "IU_QT_PYP";
	public static final String DB2_S_QT_LINE_ITEM_YTY = "S_QT_LINE_ITEM_YTY";
	
    public static final String DB2_U_QT_LINE_ITEM = "U_QT_LINE_ITEM";
    public static final String DB2_I_QT_LINE_ITEM = "I_QT_LINE_ITEM";
    public static final String DB2_D_QT_LINE_ITEM = "D_QT_LINE_ITEM";
    public static final String DB2_IU_QT_LI_CONFIG = "IU_QT_LI_CONFIG";
    public static final String DB2_S_QT_GET_ITEMS = "S_QT_GET_ITEMS";
    public static final String DB2_S_QT_PART_BY_PID = "S_QT_PART_BY_PID";
	public static final String DB2_S_QT_PART_BY_PID_SCW = "S_QT_PART_BY_PID_SCW";
    public static final String DB2_S_QT_GET_PARTS_WITHOUT_VALDT = "S_QT_GET_PARTS_WITHOUT_VALDT";
    public static final String DB2_S_QT_GET_ITEM_CONFIGS = "S_QT_GET_ITEM_CONFIGS";
    public static final String DB2_S_QT_CUST_BY_ID = "S_QT_CUST_BY_ID";
    public static final String DB2_S_QT_CUST_BY_ATTR = "S_QT_CUST_BY_ATTR";
    public static final String DB2_S_QT_END_USER_BY_ID = "S_QT_END_USER_BY_ID";
    public static final String DB2_S_QT_END_USER_BY_ATTR = "S_QT_END_USER_BY_ATTR";
    public static final String DB2_S_QT_PAYER_BY_ID = "S_QT_PAYER_BY_ID";
    public static final String DB2_U_QT_CUST_CTRCT = "U_QT_CUST_CTRCT";
    public static final String DB2_U_QT_END_CUST = "U_QT_END_CUST";
    public static final String DB2_S_QT_SSNQT_INFO = "S_QT_SSNQT_INFO";
    public static final String DB2_S_QT_DUPL_CUSTOMR = "S_QT_DUPL_CUSTOMR";
    public static final String DB2_IU_QT_WEB_CUSTMER = "IU_QT_WEB_CUSTMER";
    public static final String DB2_I_QT_DLGTN = "I_QT_DLGTN";
    public static final String DB2_D_QT_DLGTN = "D_QT_DLGTN";
    public final static String DB2_IU_QT_MASS_DLG = "IU_QT_MASS_DLG";
    public static final String DB2_IU_QT_QUOTE_CNT = "IU_QT_QUOTE_CNT";
    public static final String DB2_U_QT_FULFLL_EXPDTE = "U_QT_FULFLL_EXPDTE";
    public static final String DB2_S_QT_GET_HDRINFO = "S_QT_GET_HDRINFO";
    public static final String DB2_U_QT_SALES_INFO = "U_QT_SALES_INFO";
    public static final String DB2_S_QT_GET_SBINFO = "S_QT_GET_SBINFO";
    public static final String DB2_S_QT_GET_SBHDR = "S_QT_GET_SBHDR";
    public static final String DB2_S_QT_GET_CUSTINFO = "S_QT_GET_CUSTINFO";
    public static final String DB2_S_QT_QTCNT = "S_QT_QTCNT";
    public static final String DB2_S_QT_GET_PRTNRINFO = "S_QT_GET_PRTNRINFO";
    public static final String DB2_S_QT_GET_DLGTS = "S_QT_GET_DLGTS";
    public static final String DB2_I_QT_ADD_DUP_PRT = "I_QT_ADD_DUP_PRT";
    public static final String DB2_S_QT_GET_USERDTL = "S_QT_GET_USERDTL";
    public static final String DB2_U_QT_SAVE_DRAFT = "U_QT_SAVE_DRAFT";
    public static final String DB2_I_QT_SESSN_RQ_PRTS = "I_QT_SESSN_RQ_PRTS";
    public static final String DB2_U_QT_HDR_PP = "U_QT_HDR_PP";
    public static final String DB2_U_QT_SB_CAT = "U_QT_SB_CAT";
    public static final String DB2_U_QT_SALES_PLAY_NUM = "U_QT_SALES_PLAY_NUM";
    public static final String DB2_U_QT_LI_PVU = "U_QT_LI_PVU";
    public static final String DB2_U_QT_LI_CRAD = "U_QT_LI_CRAD";
    public static final String DB2_S_QT_GET_STATUS = "S_QT_GET_STATUS";
    public static final String DB2_S_QT_CODE_DSCR = "S_QT_CODE_DSCR";
    public static final String DB2_S_QT_PART_PREREQS = "S_QT_PART_PREREQS";
    public static final String DB2_S_QT_PART_PRC_ALL = "S_QT_PART_PRC_ALL";
    public static final String DB2_IU_QT_SBINFO = "IU_QT_SBINFO";
    public static final String DB2_U_QT_SUBMISSION = "U_QT_SUBMISSION";
    public static final String DB2_U_QT_STAGE = "U_QT_STAGE";
    public static final String DB2_U_QT_EVAL_STAGE = "U_QT_EVAL_STAGE";
    public static final String DB2_IU_QT_PRICE_TOTALS = "IU_QT_PRICE_TOTALS";
    public static final String DB2_S_QT_PRICE_TOTALS = "S_QT_PRICE_TOTALS";
    public static final String DB2_D_QT_PRICE_TOTALS = "D_QT_PRICE_TOTALS";
    public static final String DB2_U_QT_COPY_SBMT_QT = "U_QT_COPY_SBMT_QT";
    public static final String DB2_U_QT_EXPRN_ICN_CRD = "U_QT_EXPRN_ICN_CRD";
    public static final String DB2_IU_QT_SB_APPR = "IU_QT_SB_APPR";
    public static final String DB2_I_QT_AUDIT_HIST = "I_QT_AUDIT_HIST";
    public static final String DB2_S_QT_ACCESS_USER = "S_QT_ACCESS_USER";
    public static final String DB2_IU_QT_SB_APPR_ACT = "IU_QT_SB_APPR_ACT";
    public static final String DB2_I_QT_SB_ACT_HIST = "I_QT_SB_ACT_HIST";
    public static final String DB2_U_QT_SPECL_BID = "U_QT_SPECL_BID";
    public static final String DB2_U_QT_OPPR_INFO = "U_QT_OPPR_INFO";
    public static final String DB2_S_QT_SB_ACT_HIST = "S_QT_SB_ACT_HIST";
    public static final String DB2_S_QT_SB_APPRV = "S_QT_SB_APPRV";
    public static final String DB2_IU_QT_ATTCHMT = "IU_QT_ATTCHMT";
    public static final String DB2_U_QT_CUST_CREATE = "U_QT_CUST_CREATE";
    public static final String DB2_IU_QT_RENWL_SB = "IU_QT_RENWL_SB";
    public static final String DB2_I_QT_SAVE_SAP_DATA = "I_QT_SAVE_SAP_DATA";
    public static final String DB2_I_QT_SUB_SESSN_QT = "I_QT_SUB_SESSN_QT";

    public static final String DB2_S_QT_SBMTQT_STAT = "S_QT_SBMTQT_STAT";
    public static final String DB2_U_QT_CANCEL = "U_QT_CANCEL";
    public static final String DB2_S_QT_SBMT_ACCESS = "S_QT_SBMT_ACCESS";
    public static final String DB2_U_QT_IDOC = "U_QT_IDOC";

    public static final String DB2_S_QT_ADDTNL_STATUS = "S_QT_ADDTNL_STATUS";

    public static final String DB2_U_QT_PART_ADD_YRS = "U_QT_PART_ADD_YRS";

    public static final String DB2_D_QT_ATTCHMNT = "D_QT_ATTCHMNT";

    public static final String DB2_S_QT_CHK_EMP_DLGTN = "S_QT_CHK_EMP_DLGTN";

    public static final String DB2_S_QT_CUSTLIST_ID = "S_QT_CUSTLIST_ID";
    public static final String DB2_S_QT_CUSTLIST_ATTR = "S_QT_CUSTLIST_ATTR";

    public static final String DB2_S_QT_REAS_CMMT = "S_QT_REAS_CMMT";
    public static final String DB2_IU_QT_REAS_CMMT = "IU_QT_REAS_CMMT";

    public static final String DB2_S_QT_SB_REAS = "S_QT_SB_REAS";
    public static final String DB2_IU_QT_SB_REAS = "IU_QT_SB_REAS";

    public static final String DB2_U_QT_WEB_STATUS = "U_QT_WEB_STATUS";

    public static final String DB2_S_QT_EXEC_SUMRY = "S_QT_EXEC_SUMRY";
    public static final String DB2_IU_QT_EXEC_SUMRY = "IU_QT_EXEC_SUMRY";

    public static final String DB2_S_QT_GET_ORD_DETAILS = "S_QT_GET_ORD_DETAILS";

    //sp return codes
    public static final int NO_LOB_COUNTRY = 36100;
    public static final int NO_QUOTE_TITLE = 36101;

    public static final String DB2_IU_QT_TXT = "IU_QT_TXT";
    public static final String DB2_S_QT_TXT = "S_QT_TXT";

    public static final String DB2_S_QT_ATTCHMT = "S_QT_ATTCHMT";
    public static final String DB2_S_QT_ATTCHMT_REF = "S_QT_ATTCHMT_REF";
	public static final String DB2_D_QT_TXT = "D_QT_TXT";

	public static final String DB2_S_QT_GET_EOL_PRC = "S_QT_GET_EOL_PRC";

	public static final String DB2_S_QT_AGRMNT_TYPES = "S_QT_AGRMNT_TYPES";
	public static final String DB2_S_QT_AGRMNT_OPTIONS = "S_QT_AGRMNT_OPTIONS";
	public static final String DB2_S_QT_PAUN_AGRMNT_OPTIONS = "S_QT_PAUN_AGRMNT_OPTIONS";

	public static final String DB2_S_QT_STAGE = "S_QT_STAGE";

	public static final String DB2_IU_QT_WEB_CTRCT = "IU_QT_WEB_CTRCT";
	public static final String DB2_U_QT_ASSGN_CTRCT = "U_QT_ASSGN_CTRCT";
	public static final String DB2_S_QT_GET_CTRCT = "S_QT_GET_CTRCT";

	public static final String DB2_S_QT_GET_DRVD_BID = "S_QT_GET_DRVD_BID";

	public static final String DB2_U_QT_UNLOCKQT = "U_QT_UNLOCKQT";

	public static final String DB2_I_QT_GET_LOCK_INFO = "I_QT_GET_LOCK_INFO";

	public static final String DB2_U_QT_LOAD_QT = "U_QT_LOAD_QT";

	public static final String DB2_S_QT_BELONGS_TO_USER = "S_QT_BELONGS_TO_USER";

	public static final String DB2_U_QT_CONV_TO_STD_COPY="U_QT_CONV_TO_STD_CP";

	public static final String DB2_I_QT_RNWL_CHG_REQ = "I_QT_RNWL_CHG_REQ";

	public static final String DB2_S_WEB_DSW_HEADLINE = "S_WEB_DSW_HEADLINE";

	public static final String DB2_S_QT_GET_ATTCHMT_INFO = "S_QT_GET_ATTCHMT_INFO";

	public static final String DB2_S_QT_BID_ITERATN_VERIFY = "S_QT_BID_ITERATN_VERIFY";

	public static final String DB2_S_QT_FILTER_STRMLND_APR = "S_QT_FILTER_STRMLND_APR";

	public static final String DB2_I_QT_CREATE_QT_FROM_ORDER = "I_QT_CREATE_QT_FROM_ORDER";

	public static final String DB2_S_QT_PRMTN ="S_QT_PRMTN";
	public static final String DB2_D_QT_PRMTN = "D_QT_PRMTN";

	public static final String DB2_S_QT_PRE_CREDIT_CHK = "S_QT_PRE_CREDIT_CHK";

	public static final String DB2_S_QT_HAS_OWNED_QUOTE = "S_QT_HAS_OWNED_QUOTE";

	public static final String DB2_S_QT_AUDIT_HIST = "S_QT_AUDIT_HIST";

	public static final String DB2_S_QT_PID_SUPP_BY_CPQ= "S_QT_PID_SUPP_BY_CPQ";

	public static final String DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG= "S_QT_HTSRV_LINE_ITMES_BY_CONFIG";

    public static final String DB2_IU_QT_CONFIGRTN = "IU_QT_CONFIGRTN";

    public static final String DB2_U_QT_ESTD_PROVISNG_DAYS = "U_QT_ESTD_PROVISNG_DAYS";

    public static final String DB2_S_QT_CPQ_EXCEP_CODE = "S_QT_CPQ_EXCEP_CODE";

    public static final String DB2_S_QT_GET_WEB_QUOTE_CONFIGRTN = "S_QT_GET_WEB_QUOTE_CONFIGRTN";

    public static final String DB2_IU_QT_WEB_CONFIGRTN = "IU_QT_WEB_CONFIGRTN";

    public static final String DB2_I_QT_CREATE_QT_FROM_ORDER_FOR_CONFIG = "I_QT_CREATE_QT_FROM_ORDER_FOR_CONFIG";

    public static final String DB2_U_QT_COPY_INFO_FROM_ORD_FOR_CONFIG = "U_QT_COPY_INFO_FROM_ORD_FOR_CONFIG";

    public static final String DB2_U_QT_CUST_FROM_ORDER = "U_QT_CUST_FROM_ORDER";

    public static final String DB2_D_QT_WEB_CONFIGRTN = "D_QT_WEB_CONFIGRTN";

    public static final String DB2_S_QT_GET_CA_INFO = "S_QT_GET_CA_INFO";

	public static final String DB2_S_QT_GET_CA_SUB_PARTS = "S_QT_GET_CA_SUB_PARTS";

	public static final String DB2_S_QT_OPPT_LIST_BY_QT_NUM = "S_QT_OPPT_LIST_BY_QT_NUM";

	public static final String DB2_S_QT_IS_OPPT_VLD = "S_QT_IS_OPPT_VLD";

	public static final String DB2_S_QT_GET_MIGRATE_PART = "S_QT_GET_MIGRATE_PART";

	public static final String DB2_S_QT_GET_MIGRATE_REQUEST = "S_QT_GET_MIGRATE_REQUEST";

	public static final String DB2_IU_QT_MIGRATE_PART = "IU_QT_MIGRATE_PART";

	public static final String DB2_U_QT_MIGRATE_REQSTD_SUBMISSION = "U_QT_MIGRATE_REQSTD_SUBMISSION";

	public static final String DB2_IU_QT_MIGRATE_REQUST_INF_UPT = "IU_QT_MIGRATE_REQUST_INF_UPT";

	public static final String DB2_U_QT_FCT_PA_MIGRATE_INFO = "U_QT_FCT_PA_MIGRATE_INFO";

	public static final String DB2_S_QT_GET_APPROVE_CMTS_WITH_TYPE = "S_QT_GET_APPROVE_CMTS_WITH_TYPE";

	public static final String DB2_S_QT_GET_RETURN_REASON_CODE = "S_QT_GET_RETURN_REASON_CODE";

	public static final String DB2_U_QT_PROVISNG_ID = "U_QT_PROVISNG_ID";// the sp for updating the provisioning id

	public static final String DB2_U_QT_CHANNEL_INFO = "U_QT_CHANNEL_INFO";

	public static final String DB2_S_QT_GET_PROVISNG_QT_INF = "S_QT_GET_PROVISNG_QT_INF";

	public static final String DB2_S_QT_GET_WEB_APP_CNSTNT = "S_QT_GET_WEB_APP_CNSTNT";
	public static final String DB2_S_GET_UNDER_EVALUATOR_QUOTE = "S_QT_GET_UNDER_EVAL_QUOTE"; // sp for search the quote, whose status is 'Under evaluation'

	public static final String DB2_S_QT_GET_EVAL_HIS="S_QT_GET_EVAL_HIS"; // Returns the Result set of quote evaluator's action in the tab sheet of 'Sales information'
	public static final String DB2_S_QT_EVAL_BY_CNTRY="S_QT_EVAL_BY_CNTRY"; // get all evaluator for this country
	public static final String DB2_IU_WEB_QUOTE_RENWL_MDL="IU_QT_RENWL_MDL";
	public static final String DB2_D_QT_RENWL_MDL="D_QT_RENWL_MDL";
    public static final String DB2_S_QT_LINE_ITMS_NOT_ADDON_TRADEUP="S_QT_LINE_ITMS_NOT_ADDON_TRADEUP";
    public static final String DB2_S_QT_GET_RLTD_BID = "S_QT_GET_RLTD_BID";
    public static final String DB2_IU_QT_EQUITY_CURVE="IU_QT_EQUITY_CURVE";
    public static final String DB2_U_QT_HDR_EC_PP="U_QT_HDR_EC_PP";
    
    public static final String DB2_U_QT_EQUITY_CURVE="U_QT_EQUITY_CURVE";  // to erase EC data in the quote
    public static final String DB2_S_QT_CUST_DEFAULT_ADDRESS = "S_QT_CUST_DEFAULT_ADDRESS";		// get customer default address for ship to and install at
    public static final String DB2_S_QT_CUST_ADDRESS="S_QT_CUST_ADDRESS";  // query quote address
    public static final String DB2_S_SHIP_INSTALL_CUST_BY_ATTR = "S_SHIP_INSTALL_CUST_BY_ATTR";
//    public static final String DB2_S_QT_GET_SHIP_INSTALL_ITEMS = "S_QT_GET_SHIP_INSTALL_ITEMS";
    
    
    public static final String DB2_U_QT_CUST_INSTALL_AT_OPT = "U_QT_CUST_INSTALL_AT_OPT";		//Update the customer install at address option status
    
    public static final String DB2_I_QT_EXT_ENTIRE_CONFIGRTN = "I_QT_EXT_ENTIRE_CONFIGRTN";   //Add all available parts to quote 
    public static final String DB2_S_QT_GET_OMITTED_LINES = "S_QT_GET_OMITTED_LINES";   // display omitted line item
    
    public static final String DB2_S_QT_GET_OMITTED_YTYGROWTH = "S_QT_GET_OMITTED_YTYGROWTH";   // get new omitted growth
    
    public static final String DB2_IU_QT_CUST_ADDRESS = "IU_QT_CUST_ADDRESS";   //Add all available parts to quote 
    public static final String DB2_IU_QT_LINE_ITEM_ADDRESS = "IU_QT_LINE_ITEM_ADDRESS"; // Update line item address
    public static final String DB2_IU_QT_OMIT_LINES = "IU_QT_OMIT_LINES";
    public static final String DB2_IU_QT_OMITTED_PSSP = "IU_QT_OMITTED_PSSP"; // insert omitted prior S&S price
    public static final String S_QT_OMITTED_LINES_BY_QT_NUM = "S_QT_OMITTED_LINES_BY_QT_NUM";
    public static final String DB2_S_QT_GET_TOU = "S_QT_GET_TOU";
    public static final String DB2_S_QT_GET_OMIT_GROWTH_DELEGATION = "S_QT_GET_OMIT_GROWTH_DELEGATION";
	public static final String DB2_S_QT_GET_LOGIN_HIST = "S_QT_GET_LOGIN_HIST";
	public static final String DB2_I_QT_LOGIN_HIST = "I_QT_LOGIN_HIST";
    public static final String DB2_U_QT_TERMS_CONDITIONS = "U_QT_TERMS_CONDITIONS";
    public static final String DB2_D_QT_OMIT_LINES = "D_QT_OMIT_LINES";
	public static final String DB2_S_QT_IS_PLACEHOLDER = "S_QT_IS_PLACEHOLDER";
	public static final String DB2_IU_QT_BLANK_AMEMDMENT = "IU_QT_BLANK_AMEMDMENT";
	public static final String DB2_S_QT_GET_SPLIT_PCT = "S_QT_GET_SPLIT_PCT";
	public static final String S_QT_GET_VALIDATE_DEPL_ID = "S_QT_GET_VALIDATE_DEPL_ID";
    public static final String DB2_S_QT_GET_LINE_ITEM_DEPLOY_MODEL = "S_QT_GET_LINE_ITEM_DEPLOY_MODEL";
    public static final String DB2_S_QT_GET_DEPLOY_ID_BY_MTM = "S_QT_GET_DEPLOY_ID_BY_MTM";
    public static final String IU_QT_LINE_ITEM_APPLNC = "IU_QT_LINE_ITEM_APPLNC";
    public static final String DB2_S_QT_GET_WEB_QUOTE_TOTAL_PRICE = "S_QT_GET_WEB_QUOTE_TOTAL_PRICE";
    
    public static final String DB2_IU_QT_WEB_AGRMT_TOU = "IU_QT_WEB_AGRMT_TOU";//CSA - change quote agreement type if user chose different in TOU overlay
    
    public static final String DB2_S_QT_GET_MONTHLY_SW_CONFGRTN = "S_QT_GET_MONTHLY_SW_CONFGRTN";
    
    public static final String DB2_IU_QT_MONTHLY_SW_LINE_ITEM = "IU_QT_MONTHLY_SW_LINE_ITEM";
    public static final String DB2_D_QT_MONTHLY_SW_LINE_ITEM = "D_QT_MONTHLY_SW_LINE_ITEM";
    public static final String DB2_S_QT_GET_CUSTINFO_EXISTCTRCT = "S_QT_GET_CUSTINFO_EXISTCTRCT";//CSA - provide an interface to Overlay to search customer existing contracts
    public static final String DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_NEW_MOD = "S_QT_GET_MONTHLY_SW_CONFGRTN_NEW_MOD";
    public static final String DB2_S_QT_GET_INSTALL_AT_BY_MTM = "S_QT_GET_INSTALL_AT_BY_MTM";
    public static final String DB2_IU_QT_MONTHLY_SW_CONFGRTN="IU_QT_MONTHLY_SW_CONFGRTN";
    
    public static final String DB2_D_QT_MONTHLY_SW_CONFIGRTN = "D_QT_MONTHLY_SW_CONFIGRTN";

    public static final String DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_FRM_CA = "S_QT_GET_MONTHLY_SW_CONFGRTN_FRM_CA";
    
    public static final String DB2_S_QT_GET_NEWLY_INSERTED_MONTHLY_RAMPUP_SEQNUM = "S_QT_GET_NEWLY_INSERTED_MONTHLY_RAMPUP_SEQNUM";
    
    public static final String DB2_S_QT_GET_CSA_TERMS_COUNT = "S_QT_GET_CSA_TERMS_COUNT"; //CSA - query if CSA T&C doc  exists
    
    public static final String DB2_S_QT_ACTIVE_PART_B_COUNT = "S_QT_ACTIVE_PART_B_COUNT"; //PA/PAE - Check if Part B exists when the quote have Part A
    
    public static final String DB_S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR = "S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR";

    public static final String DB2_S_QT_VALIDATE_SERIAL_NUM = "S_QT_VALIDATE_SERIAL_NUM";
    
    public static final String DB2_S_QT_GET_PARTNER_CHECK_CEID = "S_QT_GET_PARTNER_CHECK_CEID";
    
    public static final String DB2_U_QT_SPECL_BID_GRID_FLAG="U_QT_SPECL_BID_GRID_FLAG";
    
    public static final String DB2_U_QT_WEB_CUST_CNTRCT_NUM = "U_QT_WEB_CUST_CNTRCT_NUM";
    
    public static final String DB2_U_QT_WEB_ENRLLMT_NUM = "U_QT_WEB_ENRLLMT_NUM";
    
    public static final String DB2_U_QT_TCV_NET_INC ="U_QT_TCV_NET_INC";
    
    public static final String DB2_S_QT_SCODS_MNG_CONFIG = "S_QT_SCODS_MNG_CONFIG";
    
    public static final String DB2_U_QT_EXP_DATE_EXTENSION = "U_QT_EXP_DATE_EXTENSION";
    
    public static final String DB2_S_QT_SEARCH_MONTHLY_RTRCT_PARTS = "S_QT_SEARCH_MONTHLY_RTRCT_PARTS";
    
    public static final String DB2_U_QT_DSJ_INFO = "U_QT_DSJ_INFO";
    
    public static final String DB2_S_QT_GET_IS_DSJ = "S_QT_GET_IS_DSJ";
    
    public static final String DB2_IU_QT_WEB_QUOTE_EXT_PRPTY = "IU_QT_WEB_QUOTE_EXT_PRPTY";
    
}
