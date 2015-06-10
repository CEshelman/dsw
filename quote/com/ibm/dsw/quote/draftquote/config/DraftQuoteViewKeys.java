package com.ibm.dsw.quote.draftquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DraftQuoteViewKeys</code> class is to define the view constants
 * for draft quote.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 8, 2007
 */
public interface DraftQuoteViewKeys {

    public static final String UPDATE_OPP_OWNER_FORM = "updateOppOwner";
    
    public static final String POST_CUST_PARTNER_FORM = "postCustPartnerForm";

    public static final String ADD_LINE_ITEM_FORM = "addLineItemForm";
    
    public static final String DELETE_LINE_ITEM_FORM = "deleteLineItemForm";
    
    public static final String REMOVE_CONFIGURATION_FORM = "removeConfigurationForm";
    
    public static final String QUOTE_SUBMISSION_FORM = "quoteSubmission";
    
    //label for back dating reason codes and comments
    public static final String BACK_DTG_REAS_CODE_LBL = "back_dtg_reas_code_lbl";
    public static final String BACK_DTG_REAS_CODE_OTHER_LBL = "back_dtg_reas_code_other_lbl";
    public static final String BACK_DTG_CMMT_LBL = "back_dtg_cmmt_lbl";
    
	//Draft quote submit keys
	public static final String MSG_SUBMIT_TITLE = "quote_submit_title";
	public static final String MSG_SUBMIT_SUBTITLE = "quote_submit_subtitle";
	public static final String MSG_ERROR_SUBMIT_TITLE = "quote_submit_error_title";
	public static final String MSG_ERROR_SUBMIT_SUBTITLE = "quote_submit_error_subtitle";
	public static final String MSG_SUBMIT_FORM_HDR =  "quote_submit_form.header";
	public static final String MSG_SUBMIT_ROW_HDR1   = "quote_submit_row.header1";
	public static final String MSG_SUBMIT_CHK_HDR1= "quote_submit_cbox.header1";
	public static final String MSG_SUBMIT_CHK_HDR2  = "quote_submit_cbox.header2";
	public static final String MSG_SUBMIT_CHK_REQ_ICN =   "quote_submit_chk.req.icn";
	public static final String MSG_SUBMIT_CHK_REQ_CRED = "quote_submit_chk.req.cred";
	public static final String MSG_SUBMIT_CHK_CRED_ALRDY_PERFMD1 = "quote_submit_chk.req.cred.already.performed1";
	public static final String MSG_SUBMIT_CHK_CRED_ALRDY_PERFMD2 = "quote_submit_chk.req.cred.already.performed2";
	public static final String MSG_SUBMIT_FORM_HDR_NOT_SPBID = "quote_submit_form.header2.not.special.bid";
	public static final String MSG_SUBMIT_FORM_HDR_NOT_SPBID_CHANNEL = "quote_submit_form.header2.not.special.bid.channel";
	public static final String MSG_SUBMIT_FORM_HDR_SPBID_CHANNEL = "quote_submit_form.header2.special.bid.channel";
	public static final String MSG_SUBMIT_OUT_SUPPRESSED = "quote_submit_output.suppressed";
	public static final String MSG_SUBMIT_ROW_HDR2   = "quote_submit_row.header2";
	public static final String MSG_SUBMIT_FORM_HDR2 = "quote_submit_form.header2";
	public static final String MSG_SUBMIT_CHK_NO_TAX = "quote_submit_chk.no.tax";
	public static final String MSG_SUBMIT_CHK_MAIL_QT = "quote_submit_chk.mail.qt";
	public static final String MSG_SUBMIT_CHK_MAIL_QT1 = "quote_submit_chk.mail.qt1";
	public static final String MSG_SUBMIT_CHK_FIRM_ORDER_LETTER = "quote_submit_chk.firm.order.letter";
	public static final String MSG_SUBMIT_CHK_PAYMENT_SCHEDULE = "quote_submit_chk.payment.schedule";
	public static final String MSG_SUBMIT_CHK_MAIL_QT_PC = "quote_submit_chk.mail.qt.pc";
	public static final String MSG_SUBMIT_CHK_UPD_MAIL = "quote_submit_chk.upd.mail";
	public static final String MSG_SUBMIT_CUST_MSG_HDR = "quote_submit_cust.msg.hdr"; 
	public static final String MSG_SUBMIT_CHK_MAIL_RQ_PC = "quote_submit_chk.rq.pc";
	public static final String MSG_SUBMIT_OUTPUT_HDR="quote_submit_output.hdr";
	public static final String MSG_SUBMIT_OUTPUT_UQC="quote_submit_output.uqc";
	public static final String MSG_SUBMIT_FAIL_RQ="quote_submit_fail_rq";
	public static final String MSG_SUBMIT_FAIL_SQ="quote_submit_fail_sq";
	public static final String MSG_SUBMIT_CHK_NO_PRC_OR_POINTS = "quote_submit_chk.no.prc.or.points";
	public static final String MSG_SUBMIT_CHK_MAIL_PARTNER = "quote_submit_chk.mail.partner";
	public static final String MSG_SUBMIT_CHK_MAIL_PARTNER_ADDITIONAL = "quote_submit_chk.mail.partner.additional";
	public static final String MSG_SUBMIT_CHK_MAIL_CUST_TEXT = "quote_submit_chk.mail.cust.text";
	public static final String MSG_SUBMIT_CHK_NO_MAIL_PARTNER_ADDR = "quote_submit_chk.no.mail.partner.addr";
	public static final String MSG_SUBMIT_CHK_MAIL_PARTNER_SELECT1 = "quote_submit_chk.mail.select1";
	public static final String MSG_SUBMIT_PARTNER_NOTIFICATION = "quote_submit_partner_notification";
	public static final String MSG_SUBMIT_CHK_MAIL_PARTNER_YES = "quote_submit_chk.mail.yes";
	public static final String MSG_SUBMIT_CHK_MAIL_PARTNER_NO = "quote_submit_chk.mail.no";
	public static final String MSG_SUBMIT_CHK_PAO_BLOCK = "quote_submit_chk.pao.block";
	public static final String MSG_SUBMIT_CHK_MULTI_MAIL = "quote_submit_chk.multi.mail";
	public static final String MSG_PAO_NEW_CUST_REG_EMAIL = "pao_new_cust_reg_email";
	public static final String MSG_PAO_NEW_CUST_REG_EMAIL_MSG = "pao_new_cust_reg_email_msg";
	public static final String MSG_SUPPRS_NEW_CUST_REG_EMAIL_TXT = "supprs_new_cust_email_txt";
	public static final String MSG_SUBMIT_CHK_REQ_CRED_GCS_URL = "quote_submit_chk.req.cred.gcs.url";
	
	public static final String DRFT_QUOTE_ITERATION = "draftquote_iteration_partprice";
	public static final String DRFT_QUOTE_ITERATION_CUSTPARTNER = "draftquote_iteration_custpartner";
	
	public static final String MSG_ATTCHMT_DEL_SUCC ="att_del_succ";
	public static final String MSG_ATTCHMT_DEL_FAIL = "att_del_fail";
	public static final String MSG_SUBMIT_QUOTE_OUTPUT_TYPE = "quote_submit_quote_output_type";
	
	public static final String MSG_BUDGETARY_QUOTE = "quote_submit_budgetary_quote_output";
	public static final String MSG_BUDGETARY_QUOTE_OUTPUT_HDR = "quote_submit_budgetary_quote_output_hdr";
	public static final String MSG_SUBMIT_BUDGETARY_QUOTE_OUTPUT_NOTE_1 = "quote_submit_budgetary_quote_output_note1";
	public static final String MSG_SUBMIT_BUDGETARY_QUOTE_OUTPUT_NOTE_2 = "quote_submit_budgetary_quote_output_note2";
	
	public static final String MSG_SUBMIT_SSP_MSG_DEF = "quote_submit_ssp.msg.def"; 
	public static final String MSG_MONTHLY_SOFTWARE_PAO_BLOCK = "monthly_software_pao_block_msg";
	
	public static final String DRFT_QUOTE_EXP_DATE_EXTENSION_CUSTPARTNER = "draftquote_exp_date_extension_custpartner";
}
