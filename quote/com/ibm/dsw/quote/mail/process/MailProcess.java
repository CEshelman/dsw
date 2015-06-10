package com.ibm.dsw.quote.mail.process;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ApproveComment;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.util.MultiPartEmail;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcess</code> class is business interface for quote
 * mass delegation function.
 * 
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Creation date: 2007-04-06
 */
public interface MailProcess {

    public void sendMail(MultiPartEmail mail) throws QuoteException;

    public void sendQuote(String from, String to, String cc, String subject, String text, File file, String desc)
            throws EmailException;
    
    public void sendAddtionalReview(String to, String cc, String quoteNum, String userId, String quoteTitle, String customerName,
            String comments, String bidCreator, String link, String mailTemplate) throws EmailException;

    public void sendCommentAdded(String approver, String creatorSubmitter, String quoteNum, String requestName, String customerName,
            String comments, String link, String lob, String mailTemplate) throws EmailException;
    
    public void sendNoApprovers(List userEmailList, String cc, String link, QuoteHeader quoteHeader) throws EmailException;
    
    public void sendMultiSameLevelGroups(List userEmailList, String cc, String quoteNum, String custName, String quoteTitle, String link) throws EmailException;
    
    public void sendOneApproverNotification(List toList, String cc, String quoteNum, String custName,
            String quoteTitle, String link) throws EmailException;
    
    public void sendCreateCustomer(String userId, QuoteHeader quoteHeader, Locale locale, Customer customer) throws EmailException;
    
    public void sendBidRequestApprovalForm(String toAddressList, String ccAddressList, String bidNum, String bidTitle, String customerName, String salesRepName, String salesRepEmail, String url, String mailTemplate, String extendedDate, String extendedReason) throws EmailException;
        //Data needed by Bid Request template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${salesRepName}
        // ${salesRepEmail}
        // ${url}
        // ${serverDetail}
    	// ${extendedDate}
    	// ${extendedReason}
        //*TO list (current level approver)
        //*CC list (submitter)
  
       
    
    public void sendBidAddlInfoProvidedForm(String toAddressList, String ccAddressList, String bidNum, String bidTitle, String customerName, String comments, String url, String mailTemplate) throws EmailException ;
        //Data needed by AdditionalInfoAdded email template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${comments}
        // ${url}
        // ${serverDetail}
        //*TO list <Approver email ID> (Approver that requested the comments.)
        //*CC list (submitter)       
    
    public void sendBidRejectedForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String url, String mailTemplate,String returnReason) throws EmailException;
 	   //Data needed by Bid Rejection template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${approverComments}
        // ${url}
        // ${serverDetail}
        //*TO list (submitter)
        //*CC list (delegates, reviewers, all approvers)

    public void sendBidReturnedForChangesForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String url, String mailTemplate,String returnReasonDesc) throws EmailException;
 	   //Data needed by Bid Rejection template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${approverComments}
        // ${url}
        // ${serverDetail}
        //*TO list (submitter)
        //*CC list (delegates, reviewers)

    public void sendBidRequestAddlInfoForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String url, String mailTemplate,String returnReason) throws EmailException;
 	   //Data needed by AdditionalInfo template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${approverComments}
        // ${url}
        // ${serverDetail}
        //*TO list (submitter)
        //*CC list (delegates, reviewers)
    
    public void sendBidNotifyOfFinalApprovalForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String expirationDate, List<ApproveComment> allApprCmts, String mailTemplate) throws EmailException;
 	   //Data needed by Bid Final approval template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${approverComments}
        // ${expirationDate}
        // ${serverDetail}
        //*TO list (submitter)
        //*CC list (delegates, reviewers, all approvers)

    public void sendBidCancelledForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String salesRepName, String salesRepEmail, String url, String mailTemplate) throws EmailException;
 	   //Data needed by Bid Rejection template:
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${salesRepName}
        // ${salesRepEmail}
        // ${url}
        // ${serverDetail}
        //*TO list (all approvers)
        //*CC list (submitter?)
    
    public void sendBidEscalateForm(String toAddressList, String ccAddressList, String pendDays, String bidNumber, String approverComments, String bidTitle, String customerName, String salesRepName, String salesRepEmail, String url, String extendedDate, String extendedReason) throws EmailException;
 	   //Data needed by Bid Escalate template:
    	   // ${pendDays}
        // ${bidNum}
        // ${bidTitle}
    	   // ${customerName}
        // ${salesRepName}
        // ${salesRepEmail}
        // ${url}
        // ${serverDetail}
	    // ${extendedDate}
	    // ${extendedReason}
        //*TO list current approver id
        //*CC list (submitter)
        //From Distributed_software_online@ibm.com 
    
    public void sendEmailInvalidInputProvidedForm(String bidNumber, String originalToAddressList, String originalCcAddressList, Exception e) throws EmailException;
 	   //Data needed by Bid Escalate template:
        // ${bidNum}
        // ${originalToAddressList}
        // ${originalCcAddressList}
        // ${exceptionMessage}
        // ${exceptionTrace}
        // ${serverDetail}
        //From Distributed_software_online@ibm.com 
    
    public void sendEmailGeneralExceptionForm(String bidNumber, String originalToAddressList, String originalCcAddressList, Exception e) throws EmailException;
 	   //Data needed by Bid Escalate template:
        // ${bidNum}
        // ${originalToAddressList}
        // ${originalCcAddressList}
        // ${exceptionMessage}
        // ${exceptionTrace}
        // ${serverDetail}
        //From Distributed_software_online@ibm.com 
   
   public void sendSbidsProcessGeneralExceptionForm(String bidNumber, Exception e) throws EmailException;
 	   //Data needed by Bid Escalate template:
       // ${bidNum}
       // ${exceptionMessage}
       // ${exceptionTrace}
       // ${serverDetail}
       //From Distributed_software_online@ibm.com 

	/**
	 * @param toAddress
	 * @param ccAddress
	 * @param webQuoteNum
	 * @param bidTitle
	 * @param customerName
	 * @param url
	 * @param approverName
	 * @param userId
	 */
	public void sendApproverSaveJustAndNotify(String toAddress, String ccAddress, String webQuoteNum, String bidTitle, String customerName, String url, String approverName, String userId, String mailTemplate) throws EmailException;

    public void notifyPendingApproverForSupersedeApprove(String toAddress, String webQuoteNum, String apprvrAction, int apprvrLevel, String bidTitle, String customerName, String url, String approverName, Locale locale, String mailTemplate) throws EmailException;

    /**
     * @param toAddress
     * @param ccAddress
     * @param webQuoteNum
     * @param bidTitle
     * @param customerName
     * @param url
     * @param approverName
     * @param userId
     * @param apprvrComments
     */
    public void sendCancelApprovedBid(String toAddress, String ccAddress, String webQuoteNum, String bidTitle, String customerName, String url, String approverName, String userId, String apprvrComments, String mailTemplate) throws EmailException;

	public void sendEmailForm(String to, String cc, String bcc,  String subject, String template, Map paramMap)throws EmailException;
	
	/**
	 * When user upload a ToU document, send a mail out attached the uploaded ToU file.
	 * paramMap key set statement:
	 * <table border=1>
	 * <tr><td>customerName</td><td>&nbsp;</td></tr>
	 * <tr><td>customerNumber</td><td>&nbsp;</td></tr>
	 * <tr><td>salesRepName</td><td>Quote creator name, if not submitted, otherwise quote submitter name</td></tr> 
	 * <tr><td>salesRepEmail</td><td>Quote creator email, if not submitted, otherwise quote submitter email</td></tr>
	 * <tr><td>opportunityOwnerName</td><td>&nbsp;</td></tr>
	 * <tr><td>opportunityOwnerEmail</td><td>&nbsp;</td></tr>
	 * <tr><td>datetime</td><td>operation time. (Mandatory if remove operation)</td></tr>
	 * <tr><td>touName</td><td>&nbsp;</td></tr>
	 * <tr><td>sapQuoteNumber</td><td>&nbsp;</td></tr>
	 * <tr><td>sapOrderNumber</td><td>&nbsp;</td></tr>
	 * </table>
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param from
	 * @param paramMap
	 * @param attachment File path
	 * @throws EmailException
	 */
	public void sendTouAmendment(String to, String cc, String bcc,String from,
			Map<String, String> paramMap, String attachmentFilePath, String attachmentFileName) throws EmailException;
	
	/**
	 * When user remove a ToU document, send a mail out to notify focals.
	 * @see sendTouAmendment
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param from
	 * @param paramMap
	 * @throws EmailException
	 */
	public void sendTouRemove(String to, String cc, String bcc,String from,
			Map<String, String> paramMap) throws EmailException;
}
