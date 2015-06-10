package com.ibm.dsw.quote.submittedquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteConstants<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-29
 */

public class SubmittedQuoteConstants {
    
    public static interface OverallStatus {
	    public static final String AWAITING_SPEC_BID_APPR = "QS001";
	    public static final String SPEC_BID_RETURN_FOR_ADDI_INFO = "QS002";
	    public static final String SPEC_BID_RETURN_FOR_CHG = "QS003";
	    public static final String SPEC_BID_APPROVED = "QS004";
	    public static final String SPEC_BID_REJECTED = "QS005";
	    public static final String QUOTE_ON_HOLD = "QS006";
	    public static final String READY_TO_ORDER = "QS007";
	    public static final String ORDER_ON_HOLD = "QS008";
	    public static final String ORDERED_NOT_BILLED = "QS009";
	    public static final String BILLED_ORDER = "QS010";
	    public static final String CANCEL_TERMINATED = "QS011";
	    public static final String EXPIRED_OR_OTHERS = "QS012";
	    public static final String ORDER_REJECTED = "QS013";
	    public static final String SOFTWARE_ORDER_ON_HOLD = "QS008SW";
	    public static final String SOFTWARE_ORDER_NOT_ON_HOLD = "QS009SW";
	    public static final String SOFTWARE_ORDER_BILLED = "QS010SW";
	    public static final String SOFTWARE_ORDER_REJECTED = "QS013SW";
    }

    public static final String RQ_SPECIAL_BID_REJECTED_STATUS = "E0048";
    public static final String RQ_SPECIAL_BID_REQUESTED_STATUS = "E0045";
    public static final String RQ_SPECIAL_BID_APPROVED_STATUS = "E0046";
    public static final String RQ_TS_CS_SPECIAL_BID_HOLD_STATUS = "E0052";
    public static final String RQ_PREAPPROVED_CONTRACT_PRICING_STATUS = "E0053";
    public static final String RQ_SPECIAL_BID_CANCELLED = "E0047";
    public static final String FCT_RQ_WITH_SALES_FOR_REVIEW_STATUS = "E0002";
    
    public static final String SQ_SPECIAL_BID_REJECTED_STATUS = "E0008";
    public static final String SQ_SPECIAL_BID_REQUESTED_STATUS = "E0013";
    public static final String SQ_SPECIAL_BID_APPROVED_STATUS = "E0006";
    public static final String SQ_SPECIAL_BID_TERMINATED_STATUS = "E0009";
    public static final String SQ_SPECIAL_BID_CANCELLED_STATUS = "E0007";
    public static final String SQ_TS_CS_SPECIAL_BID_HOLD_STATUS = "E0022";
    public static final String SQ_PREAPPROVED_CONTRACT_PRICING_STATUS = "E0023";
    public static final String APPRVR_ACTION_APPRVL_PENDG = "APPRVL_PENDG";
    public static final String APPRVR_ACTION_ADD_APRVR_COMMENT = "ADD_APRVR_COMMENT";
    public static final String APPRVR_ACTION_RETURN_FOR_ADD_INFO = "RETURN_FOR_ADD_INFO";
    public static final String APPRVR_ACTION_RETURN_FOR_CHANGES = "RETURN_FOR_CHANGES";
    public static final String APPRVR_ACTION_REJECT = "REJECT";
    public static final String APPRVR_ACTION_APPROVE = "APPROVE";
    public static final String APPRVR_ACTION_CANCEL_APPROVED_BID = "CANCEL";
    public static final String APPRVR_ACTION_SAVE_AND_NOTIFY = "SAVE_AND_NOTIFY";
    public static final String APPRVR_SAVE_DRAFT = "SAVE_DRAFT";
    public static final String SUBMITTED_QUOTE_USER_ROLE_APPROVER = "approver";
    public static final String SUBMITTED_QUOTE_USER_ROLE_SUBMITTER = "submitter";
    public static final String SUBMITTED_QUOTE_USER_ROLE_REVIEWER = "Reviewer";
    public static final String SUBMITTED_QUOTE_USER_ROLE_APPROVER_LEVEL = "approver-level-";
    public static final String REVIEWER_ACTION_ADD_COMMENT = "ADD_REVIEWER_COMMENT";
    
    public static final String APPROVER_TO_REVIEWER_COMMENT = "AP2RWCMT";
    public static final String REVIEWER_TO_REDIRECT_COMMENT = "RV2RDCMT";
    
    public static final String UPDATE_SHIP_INSTALL_ADDRESS = "UPDT_ADDR_IDOC";
    public static final String USER_ACTION_MODIFY_CRAD = "MODIFY_CRAD_IDOC";
    public static final String USER_ACTION_MODIFY_CRAD_MTM_DEPL = "M_CRAD_MTM_DPL_IDOC";
    public static final String USER_ACTION_FIRST_APPRV = "FIRST_APPRV_IDOC";
    public static final String USER_ACTION_FINL_APPRV = "FINL_APPRV_IDOC";
    public static final String USER_ACTION_REJECT = "REJECT_IDOC";
    public static final String USER_ACTION_RET_FOR_CHG = "RET_FOR_CHG_IDOC";
    public static final String USER_ACTION_CHG_EXP_DATE = "CHG_QT_DATE_IDOC";
    public static final String USER_ACTION_CHG_LI = "CHG_LI_IDOC";
    public static final String USER_ACTION_CHG_OPP_NUM = "CHG_OPP_NUM_IDOC";
    public static final String USER_ACTION_UPDT_PRTNR_IDOC = "UPDT_PRTNR_IDOC";
    public static final String USER_ACTION_UNLOCK_PAO_ACCESS = "UNLOCK_PAO_ACCESS";
    
    public static final String USER_ACTION_CHG_LI_ST_DT = "CHG_LI_ST_DT";
    public static final String USER_ACTION_CHG_LI_END_DT = "CHG_LI_END_DT";
    public static final String USER_ACTION_UP_SALES_PLAY_NUM = "UP_SALES_PLAY_NUM";
    

    public static final String USER_ACTION_QT_AUDIT_HIST = "CANCEL,CHG_EXP_DATE,CHG_LI_END_DT," +
									"CHG_LI_ST_DT,CHG_START_DATE,REQST_ICN," +
									"REQST_PRE_CRED_CHK,SEND_PA_REG_EMAIL,UNLOCK_PAO_ACCESS," +
									"UPDATE_EXEMPTN_CODE,UPDATE_OPPR_NUM,UPDT_PRTNR_IDOC," +
									"CHG_APPRVR_QT,UP_SALES_PLAY_NUM,UPDATE_PRMTN_NUM," +
									"UPDT_MACH_TYPE,UPDT_MACH_MODEL,UPDT_MACH_SERIAL_NUM," +
									"UPDT_CRAD,UPDT_SHIP_TO_ADDR,UPDT_INSTALL_AT_ADDR,CHG_CRAD,UPDT_DEPOLYMT_ID";
    
    public static final int SAP_SUCCESS = 0;
    public static final int SAP_FAILED = 1;
    public static final int SAP_INVALID_DATA = 2;
    
    public static final String  USER_ACTION_SAVE_BID_RJCTN_CODE = "SAVE_BID_RJCTN_CODE";
    public static final String  USER_ACTION_UPDT_MTM_MACH_TYPE = "UPDT_MACH_TYPE";
    public static final String  USER_ACTION_UPDT_MTM_MACH_MODEL = "UPDT_MACH_MODEL";
    public static final String  USER_ACTION_UPDT_MTM_MACH_SERIAL_NUM = "UPDT_MACH_SERIAL_NUM";
    
    public static final String USER_ACTION_UPDT_CRAD = "UPDT_CRAD";
    public static final String USER_ACTION_UPDT_SHIPTO = "UPDT_SHIP_TO_ADDR";
    public static final String USER_ACTION_UPDT_SHIPTO_VALUE = "Ship to address changed";
    public static final String USER_ACTION_UPDT_INSTALLAT = "UPDT_INSTALL_AT_ADDR";
    public static final String USER_ACTION_UPDT_INSTALLAT_VALUE = "Install at address changed";
    public static final String USER_ACTION_UPDATE_DEPLOYMTID = "UPDT_DEPOLYMT_ID";
}
