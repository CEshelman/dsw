package com.ibm.dsw.quote.mail.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>MailConstants<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: May 21, 2007
 */

public interface MailConstants {
    //template name
    public static final String MAIL_TEMPLATE_CUSTOMER_CREATE = "createCustomer.vm";
    public static final String MAIL_TEMPLATE_CUSTOMER_CREATE_PAE = "createCustomer_pae.vm";
    public static final String MAIL_TEMPLATE_CUSTOMER_CREATE_PPSS = "createCustomer_ppss.vm";
    public static final String MAIL_TEMPLATE_CUSTOMER_CREATE_OEM = "createCustomer_oem.vm";
    
    //template name for special bids
    public static final String MAIL_TEMPLATE_BID_INFO_PROVIDED = "bidAddlInfoProvided.vm";
    public static final String MAIL_TEMPLATE_BID_INFO_PROVIDED_PGS = "pgs/bidAddlInfoProvided.vm";
    public static final String MAIL_TEMPLATE_BID_INFO_PROVIDED_INTRL_PGS = "pgs/bidAddlInfoProvidedIntrl.vm";
    public static final String MAIL_TEMPLATE_BID_CANCELLED = "bidCancelled.vm";
    public static final String MAIL_TEMPLATE_BID_CANCELLED_PGS = "pgs/bidCancelled.vm";
    public static final String MAIL_TEMPLATE_BID_CANCELLED_TOBPANDEDITOR = "bidCancelledToBPandEditor.vm";
    public static final String MAIL_TEMPLATE_BID_CANCELLED_PGS_TOBPANDEDITOR = "pgs/bidCancelledToBPandEditor.vm";
    public static final String MAIL_TEMPLATE_BID_CANCELLED_INTRL_PGS = "pgs/bidCancelledIntrl.vm";
    public static final String MAIL_TEMPLATE_BID_REMINDER = "bidEscalate.vm";
    public static final String MAIL_TEMPLATE_BID_FINAL_APPROVAL = "bidFinalApproval.vm";
    public static final String MAIL_TEMPLATE_BID_FINAL_APPROVAL_PGS = "pgs/bidFinalApproval.vm";
    public static final String MAIL_TEMPLATE_BID_REJECTED = "bidRejected.vm";
    public static final String MAIL_TEMPLATE_BID_REJECTED_PGS = "pgs/bidRejected.vm";
    public static final String MAIL_TEMPLATE_BID_REQUEST_INFO = "bidRequestAddlInfo.vm";
    public static final String MAIL_TEMPLATE_BID_REQUEST_INFO_PGS = "pgs/bidRequestAddlInfo.vm";
    public static final String MAIL_TEMPLATE_BID_REQUEST_APPROVAL = "bidRequestApproval.vm";
    public static final String MAIL_TEMPLATE_BID_REQUEST_APPROVAL_PGS = "pgs/bidRequestApproval.vm";
    public static final String MAIL_TEMPLATE_BID_REQUEST_APPROVAL_INTRL_PGS = "pgs/bidRequestApprovalIntrl.vm";
    public static final String MAIL_TEMPLATE_BID_RETURN_CHANGE = "bidReturnedForChanges.vm";
    public static final String MAIL_TEMPLATE_BID_RETURN_CHANGE_PGS = "pgs/bidReturnedForChanges.vm";
    public static final String MAIL_TEMPLATE_BID_SAVE_AND_NOTIFY = "bidSaveAndNotify.vm";
    public static final String MAIL_TEMPLATE_BID_SAVE_AND_NOTIFY_PGS = "pgs/bidSaveAndNotify.vm";
    public static final String MAIL_TEMPLATE_BID_ADD_REVIEWER = "addReviewer.vm";
    public static final String MAIL_TEMPLATE_BID_ADD_REVIEWER_PGS = "pgs/addReviewer.vm";
    public static final String MAIL_TEMPLATE_BID_COMMENT_ADDED = "addComments.vm";
    public static final String MAIL_TEMPLATE_BID_COMMENT_ADDED_PGS = "pgs/addComments.vm";
    public static final String MAIL_TEMPLATE_SUPERSEDE_APPROVE = "bidSupersedeApproval.vm";
    public static final String MAIL_TEMPLATE_SUPERSEDE_APPROVE_PGS = "pgs/bidSupersedeApproval.vm";
    public static final String MAINT_TEMPLATE_ONE_LEVEL_APPROVER = "oneLevelApprover.vm";
    public static final String MAIL_TEMPLATE_CANCEL_APPROVED_BID = "cancelApprovedBid.vm";
    public static final String MAIL_TEMPLATE_CANCEL_APPROVED_BID_PGS = "pgs/cancelApprovedBid.vm";
    
    public static final String MAIL_TEMPLATE_SUBMIT_FOR_STH = "submitForSTH.vm";
    public static final String MAIL_TEMPLATE_RESUBMIT_FOR_STH = "resubmitForSTH.vm";
    public static final String MAIL_TEMPLATE_SUBMIT_FOR_STH_PGS = "pgs/submitForSTH.vm";
    
    public static final String MAIL_TEMPLATE_APPROVED_TO_CREATER_PGS = "pgs/approvedToCreater.vm";
    
    
    public static final String MAIL_APP = "mail.app";
    public static final String MAIL_ENV = "mail.env";
    public static final String MAIL_BCC_ADDRESS = "mail.bcc.address";
    
    public static final String MAIL_TEMPLATE_BID_EVAL_RETURN = "pgs/bidEvalReturn.vm";

    public static final String MAIL_TEMPLATE_TOUADMENT_FORCUSTOMERS = "toUAmendmentForCustmer.vm";
    public static final String MAIL_TOU_AMEENDMENT_RECIPIENT = "mail.tou.amendment.recipient";
}
