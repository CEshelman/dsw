package com.ibm.dsw.quote.mail.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.util.ServerDetailsUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.dsw.quote.common.domain.ApproveComment;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidEmailHelper.java</code> class is to
 * 
 * @author: <a href="mailto:lijiatao@cn.ibm.com">Joe Li </a>
 * 
 * Creation date: 2007-5-11
 */
public class SpecialBidEmailHelper {

    protected LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * @param assignedApproverEmail
     * @param specialBidApprvr
     * @param webQuoteNum
     * @return
     * @throws QuoteException
     */
    private Approvers[] constructApprovers(String assignedApproverEmail, SpecialBidApprvrOjbect specialBidApprvr,
            String webQuoteNum) throws QuoteException {
        logContext.debug(this, "Begin to construct approver list according to the quote # " + webQuoteNum);
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        List approverList = qProcess.findApprvrsByQuoteNum(webQuoteNum);
        Approvers[] approvers = null;
        for (int i = 0; i < approverList.size(); i++) {
            Object apprvr = approverList.get(i);
            if (apprvr instanceof SpecialBidApprvr_Impl) {
                SpecialBidApprvr_Impl approver = (SpecialBidApprvr_Impl) apprvr;
                if (approvers == null) {
                    approvers = new Approvers[approverList.size()];
                }
                Approvers apprs = new Approvers();
                apprs.setEmail(approver.getApprvrEmail());
                apprs.setGroupName(approver.getSpecialBidApprGrp());
                apprs.setLevel(String.valueOf(approver.getSpecialBidApprLvl()));
                apprs.setFirstName(approver.getFirstName());
                apprs.setLastName(approver.getLastName());
                apprs.setRdyToOrder(approver.getRdyToOrder());
                approvers[i] = apprs;
                logContext.debug(this, approvers[i].getEmail());
            }
        }
        // if specialBidApprvr is null that means this is for a submitter to
        // change the status, not an approval action
        // if specialBidApprvr is not null, need to check if it's different to
        // the assigned approver
        if (null == specialBidApprvr || assignedApproverEmail.equalsIgnoreCase(specialBidApprvr.getApprvrEmail())) {
            logContext
                    .debug(
                            this,
                            "The approver is the assigned approver or this is a change made by submitter, end construct approver list according to the quote # "
                                    + webQuoteNum);
            return approvers;
        } else {
            for (int i = 0; i < approvers.length; i++) {
                if (Integer.valueOf(approvers[i].getLevel()).intValue() == specialBidApprvr.getApprvrLevel()) {
                    approvers[i].setFirstName(specialBidApprvr.getApprvrFirstName());
                    approvers[i].setLastName(specialBidApprvr.getApprvrlastName());
                    approvers[i].setEmail(specialBidApprvr.getApprvrEmail());
                    logContext.debug(this, approvers[i].getEmail());
                }
            }
            logContext.debug(this,
                    "The approver is NOT the assigned approver, end construct approver list according to the quote # "
                            + webQuoteNum);
            return approvers;
        }
    }
    
    private SpecialBidApprvr_Impl getPendingApprover(String webQuoteNum) throws QuoteException {
        logContext.debug(this, "Begin to construct approver list according to the quote # " + webQuoteNum);
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        List approverList = qProcess.findApprvrsByQuoteNum(webQuoteNum);
        
        SpecialBidApprvr_Impl pendAppr = null;
        for (int i = 0; i < approverList.size(); i++) {
            Object apprvr = approverList.get(i);
            if (apprvr instanceof SpecialBidApprvr_Impl) {
                SpecialBidApprvr_Impl approver = (SpecialBidApprvr_Impl) apprvr;
                logContext.debug(this, "Approver: "+approver.getApprvrEmail()+" Level: "+approver.getSpecialBidApprLvl()+" Action: "+approver.getApprvrAction());
                if(SubmittedQuoteConstants.APPRVR_ACTION_APPRVL_PENDG.equalsIgnoreCase(approver.getApprvrAction())){
                    if(pendAppr!=null){
                        if(pendAppr.getSpecialBidApprLvl() > approver.getSpecialBidApprLvl())
                            pendAppr = approver;
                    }else{
                        pendAppr = approver;
                    }
                }
            }
        }
        if(pendAppr!=null)
            logContext.info(this, "Pending approver is: "+pendAppr.getApprvrEmail()+" Level: "+pendAppr.getSpecialBidApprLvl());
        else
            logContext.info(this, "No pending approver found.");
        return pendAppr;
    }
    
    private List<ApproveComment> getAllComments(String webQuoteNum, int level, String approver, String lastComment) throws QuoteException
    {
    	List<ApproveComment> list = SpecialBidProcessFactory.singleton().create().getAllApproverCommentsWithType(webQuoteNum, approver, level);
    	if ( list != null && list.size() > 0 )
    	{
    		ApproveComment cmt = list.get(list.size() - 1);
    		cmt.getCmts().add(lastComment);
    	}
    	Iterator<ApproveComment> iter = list.iterator();
    	while ( iter.hasNext() )
    	{
    		ApproveComment cmt = iter.next();
    		if ( cmt.getCmts().size() == 0 )
    		{
    			iter.remove();
    		}
    	}
    	return list;
    }

    private Quote getQuoteInfoByWebQuoteNum(String webQuoteNum) throws QuoteException {
        logContext.debug(this, "Begin to get quote detailed info according to the quote # " + webQuoteNum);
        Quote quote = null;
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        try {
            quote = qProcess.getSpecialBidQuoteInfoForEmail(webQuoteNum);
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException("The input submitted quote number is invalid.", e);
        } catch (QuoteException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        }
        
        logContext.debug(this, "End of get quote detailed info according to the quote # " + webQuoteNum);
        return quote;
    }

    public boolean createApprovalProcess(SpecialBidWorkflow specialBidWorkflow) throws QuoteException {
        //send email to the first approver
        logContext.debug(this, "Create approval process for " + specialBidWorkflow.getSpecialBid().getQuoteNumber()
                + "started...");

        //Default is Create failed
        boolean isCreated = false;
        
        boolean isPGSQuote = specialBidWorkflow.getSpecialBid().isPGSQuote();
        boolean isSQOEnv = specialBidWorkflow.getSpecialBid().isSQOEnv();

        String toAddress = ((Approvers) specialBidWorkflow.getApprovers(0)).getEmail();
        String ccAddress = specialBidWorkflow.getSpecialBid().getSubmitterEmail();
        String bidNum = specialBidWorkflow.getSpecialBid().getQuoteNumber();
        ccAddress = getEditorEmailAddress(bidNum, ccAddress, isSQOEnv);
        String bidTitle = specialBidWorkflow.getSpecialBid().getQuoteTitle();
        String customerName = specialBidWorkflow.getSpecialBid().getCustomerName();
        String salesRepName = specialBidWorkflow.getSpecialBid().getQuoteCreatorFirstName();
        String salesRepEmail = specialBidWorkflow.getSpecialBid().getSubmitterEmail();
        String url = specialBidWorkflow.getSpecialBid().getUrl();
        String partnerUrl = specialBidWorkflow.getSpecialBid().getPartnerURL();
        String extendedDate = specialBidWorkflow.getSpecialBid().getBidExpirationDate();
        String extendedReason = specialBidWorkflow.getSpecialBid().getExpDateExtensionJustification();
        
        logContext.debug(this, "Begin to call mail process: sendBidRequestApprovalForm(toAddressList, ccAddressList, bidNum, bidTitle, customerName, salesRepName, salesRepEmail, url)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + bidNum);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------salesRepName:  " + salesRepName);
        logContext.debug(this, "------salesRepEmail: " + salesRepEmail);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------partner url: " + partnerUrl);
        logContext.debug(this, "------isPGSQuote: " + isPGSQuote);
        logContext.debug(this, "------isSQOEnv: " + isSQOEnv);
        
        try {
            if ( !isPGSQuote )
            {
	            MailProcessFactory.singleton().create().sendBidRequestApprovalForm(toAddress, ccAddress, bidNum, bidTitle,
	                    customerName, salesRepName, salesRepEmail, url, MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL, extendedDate, extendedReason);
	            this.logContext.info(this, "sended mail success after quote submit: " + bidNum + "; " + toAddress + ";" + ccAddress);
            }
            else
            {
            	//For pgs quote, send mail to approver using pgs template for approver
            	//if sqo env, send to ibm sales in the same mail with approver
            	MailProcessFactory.singleton().create().sendBidRequestApprovalForm(toAddress, isSQOEnv ? ccAddress : null, bidNum, bidTitle,
	                    customerName, salesRepName, salesRepEmail, url, MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL_INTRL_PGS, extendedDate, extendedReason);
	            this.logContext.info(this, "sended mail success after quote submit for pgs quote to approver: " + bidNum + "; " + toAddress + ";" + ccAddress);
            	//send mail to sales using pgs template for sales
//            	toAddress = specialBidWorkflow.getSpecialBid().getSubmitterEmail();
	            //if not sqo env, we should send mail to partner in seperate mail
	            if ( !isSQOEnv )
	            {
	            	MailProcessFactory.singleton().create().sendBidRequestApprovalForm(ccAddress, null, bidNum, bidTitle,
	                        customerName, salesRepName, salesRepEmail, partnerUrl, MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL_PGS, extendedDate, extendedReason);
	                this.logContext.info(this, "sended mail success after pgs quote submit for pgs quote to submitter: " + bidNum + "; " + toAddress + ";" + ccAddress);
	            }
            }
            isCreated = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidRequestApprovalForm()");
        logContext.debug(this, "Create approval process completed...");

        return isCreated;
    }

    public boolean cancelApprovalProcessToBPandEditor(Quote quote) throws QuoteException {
    	logContext.debug(this, "Cancel approval process for " + quote.getQuoteHeader().getWebQuoteNum() + "started...");

        //Default is Cancel failed
        boolean isCanceled = false;
        String bidNum = quote.getQuoteHeader().getWebQuoteNum();

        String toCreaterAddress = quote.getQuoteHeader().getCreatorEmail();
        String ccVADAddress = this.getCCToVADY9ForT2(quote);
        
        StringBuffer toEditorAdds = new StringBuffer();
        String submtrAddress = quote.getQuoteHeader().getSubmitterId();
        String ooAddress = quote.getQuoteHeader().getOpprtntyOwnrEmailAdr();
        
        toEditorAdds.append(submtrAddress);
        if(ooAddress != null && !submtrAddress.equalsIgnoreCase(ooAddress)) {
        	toEditorAdds.append(",").append(ooAddress);
        }
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
        String toEditorAddress = this.getEditorEmailAddress(bidNum, toEditorAdds.toString(), isSubmittedOnSQO);
        
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        
        String sqoUrl = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + bidNum;
        
        String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
    			+ "&quoteNum=" + bidNum;

        String subject = "Quote Request, "+bidNum+" has been cancelled.";
        
        logContext.debug(this, "Begin to call mail process: sendEmailForm( to,  cc,  bcc,  DSW_ONLINE_EMAIL,  subject,  template,  paramMap)");
        logContext.debug(this, "------toCreaterAddress: " + toCreaterAddress.toString());
        logContext.debug(this, "------ccVADAddress: " + ccVADAddress);
        logContext.debug(this, "------toEditorAddress: " + toEditorAddress);
        logContext.debug(this, "------bidNum:        " + bidNum);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------sqoUrl:           " + sqoUrl);
        logContext.debug(this, "------partnerUrl:           " + partnerUrl);
        try {
        	Map paramMap = new HashMap();
        	paramMap.put("quoteNum", bidNum);
        	paramMap.put("requestName", StringEscapeUtils.escapeHtml(bidTitle));
        	paramMap.put("customerName", com.ibm.dsw.quote.base.util.StringHelper.handleSpecialChar(customerName));
        	paramMap.put("sqoUrl", StringEscapeUtils.escapeHtml(sqoUrl));
        	paramMap.put("partnerUrl", StringEscapeUtils.escapeHtml(partnerUrl));
        	
        	MailProcessFactory.singleton().create().sendEmailForm(toCreaterAddress,ccVADAddress,null,subject,MailConstants.MAIL_TEMPLATE_BID_CANCELLED_PGS_TOBPANDEDITOR,paramMap);
        	
        	if(isSubmittedOnSQO && StringUtils.isNotEmpty(toEditorAddress))
        		MailProcessFactory.singleton().create().sendEmailForm(toEditorAddress,null,null,subject,MailConstants.MAIL_TEMPLATE_BID_CANCELLED_TOBPANDEDITOR, paramMap);
    
            isCanceled = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidCancelledForm()");
        logContext.debug(this, "Cancel approval process completed...");

        return isCanceled;
    }
    
    public boolean cancelApprovalProcess(String webQuoteNum) throws QuoteException {
        logContext.debug(this, "Cancel approval process for " + webQuoteNum + "started...");

        //Default is Cancel failed
        boolean isCanceled = false;
        Quote quote = this.getQuoteInfoByWebQuoteNum(webQuoteNum);
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
        String bidNum = quote.getQuoteHeader().getWebQuoteNum();

        String toAddress = "";
        SpecialBidApprvr_Impl pendAppr = getPendingApprover(bidNum);
        if(pendAppr!=null){
            toAddress = pendAppr.getApprvrEmail();
        }
        
        StringBuffer ccAddress = new StringBuffer();
        String submtrAddress = quote.getQuoteHeader().getSubmitterId();
        String ooAddress = quote.getQuoteHeader().getOpprtntyOwnrEmailAdr();
        
        ccAddress.append(submtrAddress);
        if(ooAddress != null && !submtrAddress.equalsIgnoreCase(ooAddress)) {
            ccAddress.append(",").append(ooAddress);
        }
        
        String ccAdds = this.getEditorEmailAddress(webQuoteNum, ccAddress.toString(), isSubmittedOnSQO);
        
        //String comments = quote.getSpecialBidInfo().getSpBidJustText();
        String comments = "";
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        //SalesRep sp = SubmittedQuoteProcessFactory.singleton().create().getSalesRepById(quote.getQuoteHeader().getSubmitterId());
        String salesRepName = quote.getQuoteHeader().getSubmitterName();
        String salesRepEmail = quote.getQuoteHeader().getSubmitterId();

        String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + bidNum;

        logContext.debug(this, "Begin to call mail process: sendBidCancelledForm(toAddressList, ccAddressList, bidNum, approverComments, bidTitle, customerName, salesRepName, salesRepEmail, url)");
        logContext.debug(this, "------toAddressList: " + toAddress.toString());
        logContext.debug(this, "------ccAddressList: " + ccAdds);
        logContext.debug(this, "------bidNum:        " + bidNum);
        logContext.debug(this, "------comments:      " + comments);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------salesRepName:  " + salesRepName);
        logContext.debug(this, "------salesRepEmail: " + salesRepEmail);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------isPGSQuote: " + isPGSQuote);
        try {
        	if ( !isPGSQuote )
        	{
        		MailProcessFactory.singleton().create().sendBidCancelledForm(toAddress, ccAdds, bidNum, comments,
                    bidTitle, customerName, salesRepName, salesRepEmail, url, MailConstants.MAIL_TEMPLATE_BID_CANCELLED);
        	}
        	else
        	{
        		//send mail to approver
        		if ( isSubmittedOnSQO )
	        	{
	        		MailProcessFactory.singleton().create().sendBidCancelledForm(toAddress, ccAdds, bidNum, comments,
	                        bidTitle, customerName, salesRepName, salesRepEmail, url, MailConstants.MAIL_TEMPLATE_BID_CANCELLED_INTRL_PGS);
	        	}
	        	else
	        	{
	        		//the submiter id for pgs quote submitted in pgs is convert to email in S_QT_GET_HDRINFO
	        		MailProcessFactory.singleton().create().sendBidCancelledForm(toAddress, null, bidNum, comments,
	                        bidTitle, customerName, salesRepName, salesRepEmail, url, MailConstants.MAIL_TEMPLATE_BID_CANCELLED_INTRL_PGS);
	        	}
        		//435375 add mail to bp and vad 
        		//send mail to BP, get Payer email
        		String toBP = this.getToBPMail(quote);
        		String ccVAD = this.getCCToVADY9ForT2(quote);
		        String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
		                	+ "&quoteNum=" + bidNum;
		        MailProcessFactory.singleton().create().sendBidCancelledForm(toBP, ccVAD, bidNum, comments,
		                        bidTitle, customerName, salesRepName, salesRepEmail, partnerUrl, MailConstants.MAIL_TEMPLATE_BID_CANCELLED_PGS);
        	}
            isCanceled = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidCancelledForm()");
        logContext.debug(this, "Cancel approval process completed...");

        return isCanceled;
    }
    
    protected String getPartnerY9Mails(Partner p)
    {
    	if ( p == null )
    	{
    		return "";
    	}
    	return this.getMailFormList(p.getY9EmailList());
    }
    
    protected String getMailFormList(List list)
    {
    	if ( list == null || list.size() == 0 )
    	{
    		return "";
    	}
    	StringBuffer buff = new StringBuffer();
    	for ( int i = 0; i < list.size(); i++ )
    	{
    		String temp = (String)list.get(i);
    		buff.append(temp);
    		if ( i != list.size() - 1 )
    		{
    			buff.append(",");
    		}
    	}
    	return buff.toString();
    }
    
    //If the quote creator site is not the same with the Payer's site num, the quote was T2, if this not work, should call F_PGS_BP_TIERMODEL to get the tier model
    protected boolean isTier2(Quote quote)
    {
    	QuoteHeader header = quote.getQuoteHeader();
    	Partner payer = quote.getPayer();
    	if ( !header.isPGSQuote() || payer == null )
    	{
    		return false;
    	}
    	String creatorId = header.getCreatorId();
    	if ( !StringUtils.contains(creatorId, "-") )
    	{
    		return false;
    	}
    	String siteNum = StringUtils.split(creatorId, "-")[1];
    	if ( StringUtils.equals(siteNum, payer.getCustNum()) )
    	{
    		return false;
    	}
    	return true;
    }
    
    //this check only suited for submitted quote, because the issubmittedOnSQO flag from quote header is for submitted quote, for draft quote, submitter is null
    protected String getToBPMail(Quote quote)
    {
    	String toBP = "";
		if ( quote.getQuoteHeader().isSubmittedOnSQO() )
		{
			toBP = quote.getCreator().getEmailAddress();
		}
		else
		{
			toBP = quote.getQuoteHeader().getSubmitterId();
		}
		return toBP;
    }
    
    protected String getCCToVADY9ForT2(Quote quote)
    {
    	String toBP = "";
    	if ( this.isTier2(quote) )
		{
			toBP = this.getPartnerY9Mails(quote.getPayer());
		}
    	return toBP;
    }

    public boolean approveSpecialBid(String assignedApproverEmail, SpecialBidApprvrOjbect specialBidApprvr,
            String webQuoteNum, String comments, String bidExpireDate) throws QuoteException {
        boolean isApproved = false;

        Quote quote = this.getQuoteInfoByWebQuoteNum(webQuoteNum);
        Approvers[] approvers = this.constructApprovers(assignedApproverEmail, specialBidApprvr, webQuoteNum);

        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();

        int nextAppr = 0;
        for (int i = 0; i < approvers.length; i++) {
            if (Integer.valueOf(approvers[i].getLevel()).intValue() >= specialBidApprvr.getApprvrLevel()) {
            	 nextAppr = i + 1;
            	 
             	//find the first non-ready to order approver after the current approver
            	 if(nextAppr < approvers.length){
                 	if(approvers[nextAppr].getRdyToOrder() == 0){
                        break;
                	}
            	 } else {
            		 break; // this is already the last approver
            	 }
            }
        }
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();

    	if (nextAppr == approvers.length || (approvers.length > 0 && Integer.parseInt(approvers[approvers.length - 1].getLevel()) < specialBidApprvr.getApprvrLevel() ) ) {
            logContext.debug(this, "This is final approval. Send final approval notification to quote creator.");
            String toAddress = quote.getQuoteHeader().getSubmitterId();
            String ccAddress = quote.getQuoteHeader().getOpprtntyOwnrEmailAdr();
            ccAddress = this.getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
            
            String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_FINAL_APPROVAL_PGS : MailConstants.MAIL_TEMPLATE_BID_FINAL_APPROVAL;
            logContext.debug(this, "Begin to call mail process: sendBidNotifyOfFinalApprovalForm(toAddressList, ccAddressList, bidNum, approverComments, bidTitle, customerName, expirationDate)");
            logContext.debug(this, "------toAddressList: " + toAddress);
            logContext.debug(this, "------ccAddressList: " + ccAddress);
            logContext.debug(this, "------bidNum:        " + webQuoteNum);
            logContext.debug(this, "------comments:      " + comments);
            logContext.debug(this, "------bidTitle:      " + bidTitle);
            logContext.debug(this, "------customerName:  " + customerName);
            logContext.debug(this, "------bidExpireDate: " + bidExpireDate);
            logContext.debug(this, "------isPGSQuote:    " + isPGSQuote);
            logContext.debug(this, "------isSubmittedONSQO:" + isSubmittedOnSQO);
            List<ApproveComment> allApproveCmts = this.getAllComments(webQuoteNum, specialBidApprvr.getApprvrLevel(), specialBidApprvr.getApprvrEmail(), comments);
            try {
            	if ( !isPGSQuote || isSubmittedOnSQO )
            	{
	                MailProcessFactory.singleton().create().sendBidNotifyOfFinalApprovalForm(toAddress, ccAddress,
	                        webQuoteNum, comments, bidTitle, customerName, bidExpireDate, allApproveCmts, mailTemplate);
            	}
            	
            	// send mail to BP and VAD
                if ( isPGSQuote )
                {
                	//send mail to creator quote.getCreator().get email
                	//cc to VAD if quote is created by T2
                	String toBP = this.getToBPMail(quote);
                	String ccVAD = this.getCCToVADY9ForT2(quote);
                	String subject = "Your Quote Request, "+webQuoteNum+" has been Approved";
                	Map paramMap = new HashMap();
                	paramMap.put("quoteNum", webQuoteNum);
                	paramMap.put("comments", comments);
                	paramMap.put("requestName", StringEscapeUtils.escapeHtml(bidTitle));
                	paramMap.put("customerName", com.ibm.dsw.quote.base.util.StringHelper.handleSpecialChar(customerName));
                	paramMap.put("bidExpireDate", StringEscapeUtils.escapeHtml(bidExpireDate));
                	MailProcessFactory.singleton().create().sendEmailForm(toBP, ccVAD, null,subject,MailConstants.MAIL_TEMPLATE_APPROVED_TO_CREATER_PGS,paramMap);
                	
                	logContext.info(this,"Send mail to quote creator and VAD for BP quote final approved.  to: "+ toBP +"; cc: " + ccVAD +"; subject: "+ subject +"; quote number: "+webQuoteNum);
                }
                
                isApproved = true;
            } catch (EmailException e) {
            	logContext.error(this, e);
                throw e;
            }
            logContext.debug(this, "End of calling mail process: sendBidNotifyOfFinalApprovalForm()");
        } else {
            logContext.debug(this, "Send email to the next level of approver.");
            //Send email to the next approver to request his approval
            Approvers approver = approvers[nextAppr];
            String toAddress = approver.getEmail();
            //No need to cc to partner for pgs quote request next level approval
            String ccAddress = isPGSQuote ? "" : quote.getQuoteHeader().getSubmitterId();
//            SalesRep sp = SubmittedQuoteProcessFactory.singleton().create().getSalesRepById(quote.getQuoteHeader().getSubmitterId());
            String salesRepName = quote.getQuoteHeader().getSubmitterName();
            String salesRepEmail = quote.getQuoteHeader().getSubmitterId();
            
            String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB) + "&quoteNum=" + webQuoteNum;
            
            String extendedDate = "";
            String extendedReason = "";
            //Add editor to cc list
            ccAddress = this.getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
            
            logContext.debug(this, "Begin to call mail process: sendBidRequestApprovalForm(toAddressList, ccAddressList, bidNum, bidTitle, customerName, salesRepName, salesRepEmail, url)");
            logContext.debug(this, "------toAddressList: " + toAddress);
            logContext.debug(this, "------ccAddressList: " + ccAddress);
            logContext.debug(this, "------bidNum:        " + webQuoteNum);            
            logContext.debug(this, "------bidTitle:      " + bidTitle);
            logContext.debug(this, "------customerName:  " + customerName);
            logContext.debug(this, "------salesRepName:  " + salesRepName);
            logContext.debug(this, "------salesRepEmail: " + salesRepEmail);
            logContext.debug(this, "------url:           " + url);
            logContext.debug(this, "------isPGSQuote:    " + isPGSQuote);
            try {
            	String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL_PGS : MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL;
                MailProcessFactory.singleton().create().sendBidRequestApprovalForm(toAddress, ccAddress, webQuoteNum,
                        bidTitle, customerName, salesRepName, salesRepEmail, url, mailTemplate, extendedDate, extendedReason);
                if ( isPGSQuote )
                {
                	//Approve, quote routing to next level approver, no need send mail to submitter for PGS quote.
//                    toAddress = sp.getEmailAddress();
//                    MailProcessFactory.singleton().create().sendBidRequestApprovalForm(toAddress, null, webQuoteNum,
//                            bidTitle, customerName, salesRepName, salesRepEmail, url, MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL_PGS);
                }
                this.logContext.info(this, "sended mail success for approve and send request to next approver: " + webQuoteNum + ";" 
                		+ toAddress + ";" + ccAddress);
                isApproved = true;
            } catch (EmailException e) {
            	logContext.error(this, e);
                throw e;
            }
            logContext.debug(this, "End of calling mail process: sendBidRequestApprovalForm()");
        }

        return isApproved;
    }
 
//    public String getEditorEmailAddress(String webQuoteNum, String ccAddress)
//    {
//    	return getEditorEmailAddress(webQuoteNum, ccAddress, true);
//    }
    
    //Add to get editor mail address
    //If no editor return "", else return ",editor,editor,editor"
    //isSubmittedONSQO is true will get editor mail, else not
    public String getEditorEmailAddress(String webQuoteNum, String ccAddress, boolean isSubmittedONSQO)
    {
    	List list = null;
		try {
			if ( isSubmittedONSQO )
			{
				list = SalesRepFactory.singleton().findDelegatesByQuote(webQuoteNum);
			}
		} catch (TopazException e) {
			logContext.error(this, "GetEditorEmailAddress fail: " + e.getStackTraceAsString());
		}
    	if ( list == null || list.size() == 0 )
    	{
    		return ccAddress;
    	}
    	StringBuffer buff = new StringBuffer();
    	if ( StringUtils.isNotBlank(ccAddress) )
    	{
    		buff.append(ccAddress).append(",");
    	}
    	for ( int i = 0; i < list.size(); i++ )
    	{
    		SalesRep rep = (SalesRep)list.get(i);
    		buff.append(rep.getEmailAddress());
    		if ( i != list.size() - 1 )
    		{
    			buff.append(",");
    		}
    	}
    	return buff.toString();
    }

    public boolean rejectSpecialBid(String assignedApproverEmail, SpecialBidApprvrOjbect specialBidApprvr,
            String webQuoteNum, String comments,String returnReason) throws QuoteException {
        boolean isRejected = false;

        Quote quote = this.getQuoteInfoByWebQuoteNum(webQuoteNum);
//        Approvers[] approvers = this.constructApprovers(assignedApproverEmail, specialBidApprvr, webQuoteNum);
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        String toAddress = quote.getQuoteHeader().getSubmitterId();
        
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
        	+ "&quoteNum=" + webQuoteNum;
        
        if ( isPGSQuote && !isSubmittedOnSQO)
        {
        	String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
            	+ "&quoteNum=" + webQuoteNum;
        	url = partnerUrl;
        }
        
        String ccAddress = quote.getQuoteHeader().getOpprtntyOwnrEmailAdr();
        ccAddress = this.getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
        
        logContext.debug(this, "Begin to call mail process: sendBidRejectedForm(toAddressList, ccAddressList, bidNum, approverComments, bidTitle, customerName, url)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------comments:      " + comments);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------isPGSQuote:    " + isPGSQuote);
        logContext.debug(this, "------isSubmittedOnSQO: " + isSubmittedOnSQO);
        try {
        	String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_REJECTED_PGS : MailConstants.MAIL_TEMPLATE_BID_REJECTED;
            MailProcessFactory.singleton().create().sendBidRejectedForm(toAddress, ccAddress, webQuoteNum,
                    comments, bidTitle, customerName, url, mailTemplate,returnReason);
            isRejected = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidRejectedForm()");
        return isRejected;
    }

    public boolean returnForChanges(String assignedApproverEmail, SpecialBidApprvrOjbect specialBidApprvr,
            String webQuoteNum, String comments,String returnReasonDesc) throws QuoteException {
        boolean isReturnForChanges = false;

        Quote quote = this.getQuoteInfoByWebQuoteNum(webQuoteNum);
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        String toAddress = quote.getQuoteHeader().getSubmitterId();
        
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + webQuoteNum;
        if ( isPGSQuote && !isSubmittedOnSQO )
        {
        	String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
            + "&quoteNum=" + webQuoteNum;
        	url = partnerUrl;
        }
        String ccAddress = quote.getQuoteHeader().getOpprtntyOwnrEmailAdr();
        ccAddress = this.getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
        
        logContext.debug(this, "Begin to call mail process: sendBidReturnedForChangesForm(toAddressList, ccAddressList, bidNum, approverComments, bidTitle, customerName, url)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------comments:      " + comments);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------returnReason:      " + returnReasonDesc);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------isPGSQuote:    " + isPGSQuote);
        logContext.debug(this, "------isSubmittedOnSQO: "+ isSubmittedOnSQO);
        try {
        	String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_RETURN_CHANGE_PGS : MailConstants.MAIL_TEMPLATE_BID_RETURN_CHANGE;
            MailProcessFactory.singleton().create().sendBidReturnedForChangesForm(toAddress, ccAddress,
                    webQuoteNum, comments, bidTitle, customerName, url, mailTemplate,returnReasonDesc);
            isReturnForChanges = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidReturnedForChangesForm()");
        return isReturnForChanges;
    }

    public boolean additionalInfoNeeded(String assignedApproverEmail, SpecialBidApprvrOjbect specialBidApprvr,
            String webQuoteNum, String comments,String returnReasonDesc) throws QuoteException {
        boolean isAdditionalInfoNeeded = false;

        Quote quote = this.getQuoteInfoByWebQuoteNum(webQuoteNum);
        String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
        + "&quoteNum=" + webQuoteNum;
        
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        String toAddress = quote.getQuoteHeader().getSubmitterId();
        if ( isPGSQuote && !isSubmittedOnSQO )
        {
        	String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
            + "&quoteNum=" + webQuoteNum;
        	url = partnerUrl;
        }
        
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        
        String ccAddress = quote.getQuoteHeader().getOpprtntyOwnrEmailAdr();
        
        ccAddress = this.getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
        
        logContext.debug(this, "Begin to call mail process: sendBidRequestAddlInfoForm(toAddressList, ccAddressList, bidNum, approverComments, bidTitle, customerName, url)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------comments:      " + comments);
        logContext.debug(this, "------returnReason:  " + returnReasonDesc);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------isPGSQuote:    " + isPGSQuote);
        logContext.debug(this, "------isSubmittedOnSQO: " + isSubmittedOnSQO);
        try {
        	String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_REQUEST_INFO_PGS : MailConstants.MAIL_TEMPLATE_BID_REQUEST_INFO;
        	MailProcessFactory.singleton().create().sendBidRequestAddlInfoForm(toAddress, ccAddress, webQuoteNum, comments, bidTitle, customerName, url, mailTemplate,returnReasonDesc);
            isAdditionalInfoNeeded = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidRequestAddlInfoForm()");
        return isAdditionalInfoNeeded;
    }

    public boolean additionalInfoProvided(String webQuoteNum, String assignedApproverEmail, String SubmitterEmail,
            String comments) throws QuoteException {
        boolean isAdditionalInfoProvided = false;
        
        Quote quote = this.getQuoteInfoByWebQuoteNum(webQuoteNum);
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
        String toAddress = assignedApproverEmail;
        String ccAddress = SubmitterEmail;
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        String sqoUrl = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + webQuoteNum;

        String partnerUrl = null;

        if ( isPGSQuote )
        {
        	partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
            	+ "&quoteNum=" + webQuoteNum;
        }
        
        ccAddress = this.getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
        
        logContext.debug(this, "Begin to call mail process: sendBidAddlInfoProvidedForm(toAddressList, ccAddressList, bidNum, bidTitle, customerName, comments, url)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------comments:      " + comments);
        logContext.debug(this, "------sqoUrl:           " + sqoUrl);
        logContext.debug(this, "------partnerUrl:           " + partnerUrl);
        logContext.debug(this, "------isPGSQuote:    " + isPGSQuote);
        logContext.debug(this, "------isSubmittedOnSQO: " + isSubmittedOnSQO);
        try {
        	if ( !isPGSQuote )
        	{
        		MailProcessFactory.singleton().create().sendBidAddlInfoProvidedForm(toAddress, ccAddress, webQuoteNum, bidTitle, customerName, comments, sqoUrl, MailConstants.MAIL_TEMPLATE_BID_INFO_PROVIDED);
        	}
        	else
        	{
        		if ( isSubmittedOnSQO )
        		{
        			MailProcessFactory.singleton().create().sendBidAddlInfoProvidedForm(toAddress, ccAddress, webQuoteNum, bidTitle, customerName, comments, sqoUrl, MailConstants.MAIL_TEMPLATE_BID_INFO_PROVIDED_INTRL_PGS);
        		}
        		else
        		{
        			MailProcessFactory.singleton().create().sendBidAddlInfoProvidedForm(toAddress, null, webQuoteNum, bidTitle, customerName, comments, sqoUrl, MailConstants.MAIL_TEMPLATE_BID_INFO_PROVIDED_INTRL_PGS);
        			MailProcessFactory.singleton().create().sendBidAddlInfoProvidedForm(ccAddress, null, webQuoteNum, bidTitle, customerName, comments, partnerUrl, MailConstants.MAIL_TEMPLATE_BID_INFO_PROVIDED_PGS);
        		}
        	}
        	isAdditionalInfoProvided = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidAddlInfoProvidedForm()");
        return isAdditionalInfoProvided;
    }

    /**
     * @param webQuoteNum
     * @param approverName
     * @param userId
     * @param toAddress  submitter id
     * @param ccAddress  approver id
     * @param bidTitle
     * @return
     * @throws EmailException
     */
    public boolean approverSaveJustAndNotify(String webQuoteNum, String approverName, String userId, String submitterId, String ccAddress, String bidTitle, String customerName, boolean isPGSQuote, boolean isSubmittedOnSQO) throws EmailException {
        boolean isAdditionalInfoProvided = false;
        String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + webQuoteNum;
        String toAddress = submitterId;
        ccAddress = getEditorEmailAddress(webQuoteNum, ccAddress, isSubmittedOnSQO);
        
        logContext.debug(this, "Begin to call mail process: sendApproverSaveJustAndNotify(toAddressList, ccAddressList, bidNum, bidTitle, customerName, url)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------approverName: " + approverName);
        logContext.debug(this, "------userId: " + userId);
        logContext.debug(this, "------isPGSQuote: " + isPGSQuote);
        logContext.debug(this, "------isSubmittedOnSQO: " + isSubmittedOnSQO);
        try {
        	if ( !isPGSQuote )
        	{
        		MailProcessFactory.singleton().create().sendApproverSaveJustAndNotify(toAddress, ccAddress, webQuoteNum, bidTitle, customerName, url, approverName, userId, MailConstants.MAIL_TEMPLATE_BID_SAVE_AND_NOTIFY);
        	}
        	else
        	{
        		if ( isSubmittedOnSQO )
        		{
        			MailProcessFactory.singleton().create().sendApproverSaveJustAndNotify(toAddress, ccAddress, webQuoteNum, bidTitle, customerName, url, approverName, userId, MailConstants.MAIL_TEMPLATE_BID_SAVE_AND_NOTIFY_PGS);
        		}
        		else
        		{
        			String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB) + "&quoteNum=" + webQuoteNum;
        			MailProcessFactory.singleton().create().sendApproverSaveJustAndNotify(toAddress, null, webQuoteNum, bidTitle, customerName, partnerUrl, approverName, userId, MailConstants.MAIL_TEMPLATE_BID_SAVE_AND_NOTIFY_PGS);
        			MailProcessFactory.singleton().create().sendApproverSaveJustAndNotify(ccAddress, null, webQuoteNum, bidTitle, customerName, url, approverName, userId, MailConstants.MAIL_TEMPLATE_BID_SAVE_AND_NOTIFY_PGS);
        		}
        	}
        	isAdditionalInfoProvided = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendApproverSaveJustAndNotify()");
        return isAdditionalInfoProvided;
    }

    /**
     * @param webQuoteNum
     * @param fullName
     * @param userId
     * @param toAddress
     * @param ccAddress
     * @param bidTitle
     * @param customerName
     * @param apprvrComments
     */
    public boolean cancelApprovedBid(String webQuoteNum, String approverName, String userId, String submitterId, String opprtntyOwnrEmailAdr, 
            	String bidTitle, String customerName, String apprvrComments, boolean isPGSQuote, boolean isSubmittedOnSQO, Quote quote) throws EmailException {
        boolean flag = false;
        String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + webQuoteNum;
        String toAddress = submitterId;
        String ccAddress = opprtntyOwnrEmailAdr;
        
        logContext.debug(this, "Begin to call mail process: cancelApprovedBid(toAddressList, ccAddressList, bidNum, bidTitle, customerName, url, aproverName, apprvrComments)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------approverName: " + approverName);
        logContext.debug(this, "------userId: " + userId);
        logContext.debug(this, "------approver comments: " + apprvrComments);
        logContext.debug(this, "------isPGSQuote: " + isPGSQuote);
        logContext.debug(this, "------isSubmittedOnSQO: " + isSubmittedOnSQO);
        
        try {
        	if ( !isPGSQuote || isSubmittedOnSQO )
        	{
        		MailProcessFactory.singleton().create().sendCancelApprovedBid(toAddress, ccAddress, webQuoteNum, bidTitle, customerName, url, approverName, userId, apprvrComments, MailConstants.MAIL_TEMPLATE_CANCEL_APPROVED_BID);
        	}
        	if ( isPGSQuote )
        	{
        		String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
                	+ "&quoteNum=" + webQuoteNum;
        		String toBP = this.getToBPMail(quote);
        		String ccVAD = this.getCCToVADY9ForT2(quote);
        		MailProcessFactory.singleton().create().sendCancelApprovedBid(toBP, ccVAD, webQuoteNum, bidTitle, customerName, partnerUrl, approverName, userId, apprvrComments, MailConstants.MAIL_TEMPLATE_CANCEL_APPROVED_BID_PGS);
        	}
        	flag = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: cancelApprovedBid()");
        return flag;
    }
    
    public void addReviewer(Quote quote, String reviewEmail, String approverId, String aprReviewCmts) throws EmailException
    {
    	QuoteHeader quoteHeader = quote.getQuoteHeader();
    	String bidTitle = quoteHeader.getQuoteTitle();
    	String customerName = quoteHeader.getCustName();
    	
    	
    	String quoteNum = quoteHeader.getWebQuoteNum();
    	String link = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
         			+ "&quoteNum=" + quote.getQuoteHeader().getWebQuoteNum();
    	 
		 boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
		 boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
		 String ccAddressList = approverId;
		 if ( !isPGSQuote || isPGSQuote && isSubmittedOnSQO )
		 {
			 ccAddressList += "," + quoteHeader.getSubmitterId();
		 }
		 ccAddressList = getEditorEmailAddress(quoteHeader.getWebQuoteNum(), ccAddressList, isSubmittedOnSQO);
		 logContext.debug(this, "cc ccAddressList: " + ccAddressList);
		 String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_ADD_REVIEWER_PGS : MailConstants.MAIL_TEMPLATE_BID_ADD_REVIEWER;
		 
		 logContext.debug(this, "Begin to call mail process: sendAdditionalReview");
	        logContext.debug(this, "------toAddressList: " + reviewEmail);
	        logContext.debug(this, "------ccAddressList: " + ccAddressList);
	        logContext.debug(this, "------bidNum:        " + quoteNum);
	        logContext.debug(this, "------bidTitle:      " + bidTitle);
	        logContext.debug(this, "------customerName:  " + customerName);
	        logContext.debug(this, "------url:           " + link);
	        logContext.debug(this, "------reviewerEmail: " + reviewEmail);
	        logContext.debug(this, "------approverId: " + approverId);
	        logContext.debug(this, "------approver comments: " + aprReviewCmts);
	        logContext.debug(this, "------isPGSQuote: " + isPGSQuote);
	        logContext.debug(this, "------isSubmittedOnSQO: " + isSubmittedOnSQO);
		 
		 try {
			 MailProcessFactory.singleton().create().sendAddtionalReview(reviewEmail, ccAddressList, quoteHeader.getWebQuoteNum(),
			         approverId, quoteHeader.getQuoteTitle(), quoteHeader.getCustName(), aprReviewCmts, quoteHeader.getSubmitterId(), link, mailTemplate);
	        } catch (EmailException e) {
	        	logContext.error(this, e);
	            throw e;
	        }
	        
    }

    /**
     * Send mail to evaluator for bp submit special bid to STH
     * @param evalEmail
     * @param quoteNum
     * @param requestName
     * @param customerName
     * @param bidCreator
     * @param bidCreatorEmail
     * @param vadEmail
     * @return
     * @throws QuoteException 
     */
	public boolean submitForSTHApproval(String evalEmail, String quoteNum,
			String requestName, String customerName, String bidCreator,
			String bidCreatorEmail, String bpName,String opprtntyNum,String quoteExpDate, Quote quote) throws QuoteException {
		
		boolean isSubmitForSTHApproval = false;
        String sqoUrl = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.QUOTE_MAIL_LINK_DISPATCH)
                + "&quoteNum=" + quoteNum;

        String partnerUrl = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
    	+ "&quoteNum=" + quoteNum;
        
        logContext.debug(this, "Begin to call mail process: sendSubmitForSTHApprovalForm(evalEmail,quoteNum,requestName,customerName,bidCreator,bidCreatorEmail,vadEmail,sqoUrl,partnerUrl)");
        logContext.debug(this, "------evalEmail: " + evalEmail);
        logContext.debug(this, "------quoteNum: " + quoteNum);
        logContext.debug(this, "------requestName: " + requestName);
        logContext.debug(this, "------customerName: " + customerName);
        logContext.debug(this, "------bidCreator: " + bidCreator);
        logContext.debug(this, "------bidCreatorEmail: " + bidCreatorEmail);
        logContext.debug(this, "------sqoUrl: " + sqoUrl);
        logContext.debug(this, "------partnerUrl: " + partnerUrl);
        
        try {
        	// for evaluator
        	String subject = quoteNum+"  A Partner Quote Request for "+customerName+" requires your evaluation";
        	
        	QuoteHeader header = quote.getQuoteHeader();
    		String evalEmailTemp = header.getEvalEmailAdr();
    		String emailVm = "";
    		if( StringUtils.isNotEmpty(evalEmailTemp) ){
    			//<quote #>  A Partner Quote Request for <Customer Name> has been resubmitted for your evaluation
    			subject = quoteNum+"  A Partner Quote Request for "+customerName+" has been resubmitted for your evaluation";
    			emailVm = MailConstants.MAIL_TEMPLATE_RESUBMIT_FOR_STH;
    		}else{
    			subject = quoteNum+"  A Partner Quote Request for "+customerName+" requires your evaluation";
    			emailVm = MailConstants.MAIL_TEMPLATE_SUBMIT_FOR_STH;
    		}
    		
    		//boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
    		// --- set third param to true only when the quote is submitted in PGS for evaluator
    		String ccEditor = "";
    		ccEditor = getEditorEmailAddress(quoteNum, ccEditor, true);
        	
        	Map paramMap = new HashMap();
        	paramMap.put("quoteNum", quoteNum);
        	paramMap.put("requestName", StringEscapeUtils.escapeHtml(requestName));
        	paramMap.put("customerName", com.ibm.dsw.quote.base.util.StringHelper.handleSpecialChar(customerName));
        	paramMap.put("sqoUrl", StringEscapeUtils.escapeHtml(sqoUrl));
        	paramMap.put("bidCreator", StringEscapeUtils.escapeHtml(bidCreator));
        	
        	paramMap.put("bpName", bpName);
        	paramMap.put("opprtntyNum", opprtntyNum);
        	paramMap.put("quoteExpDate", quoteExpDate);
        	
        	//paramMap.put("bidCreatorEmail", bidCreatorEmail);
        	
        	MailProcessFactory.singleton().create().sendEmailForm(evalEmail,ccEditor,null,subject,emailVm,paramMap);
        	logContext.info(this,"Send mail to evaluator for bp submit special bid to STH ( to evaluator).  to: "+evalEmail+"; subject: "+ subject +"; quote number: "+quoteNum + "; bpName=" + bpName);
        	
			// for VAD
    		String ccVAD = this.getCCToVADY9ForT2(quote);
        	subject = quoteNum+"  Quote Request for "+customerName+" has been submitted for review";
        	paramMap = new HashMap();
        	paramMap.put("quoteNum", quoteNum);
        	paramMap.put("requestName", StringEscapeUtils.escapeHtml(requestName));
        	paramMap.put("customerName", com.ibm.dsw.quote.base.util.StringHelper.handleSpecialChar(customerName));
        	paramMap.put("partnerUrl", StringEscapeUtils.escapeHtml(partnerUrl));
			MailProcessFactory.singleton().create().sendEmailForm(bidCreatorEmail,ccVAD,null,subject,MailConstants.MAIL_TEMPLATE_SUBMIT_FOR_STH_PGS,paramMap);
			logContext.info(this,"Send mail to evaluator for bp submit special bid to STH ( to creater ).  to: "+bidCreatorEmail+"; cc: "+ccVAD+"; subject: "+ subject +"; quote number: "+quoteNum);
			
			
        	isSubmitForSTHApproval = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendSubmitForSTHApprovalForm()");
        return isSubmitForSTHApproval;
	}

//    
//    public void sendReminder(String webQuoteNum, String bidTitle, String custName, String salesRepName, String apprEmail, String creatorId, String comments, String pendDays) throws QuoteException {
//        
//        String toAddress = apprEmail;
//        String ccAddress = creatorId;
//        String salesRepEmail = creatorId;
//        
//        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
//        String url = appContext.getConfigParameter(ApplicationProperties.APPLICATION_QUOTE_APPURL);
//        url = url + HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB) + "&quoteNum=" + webQuoteNum;
//        
//        logContext.debug(this, "Begin to call mail process: sendBidEscalateForm(toAddressList, ccAddressList, pendDays, bidNum, approverComments, bidTitle, customerName, salesRepName, salesRepEmail, url)");
//        logContext.debug(this, "------toAddressList: " + toAddress);
//        logContext.debug(this, "------ccAddressList: " + ccAddress);
//        logContext.debug(this, "------pendDays:      " + pendDays);
//        logContext.debug(this, "------bidNum:        " + webQuoteNum);
//        logContext.debug(this, "------comments:      " + comments);
//        logContext.debug(this, "------bidTitle:      " + bidTitle);
//        logContext.debug(this, "------customerName:  " + custName);
//        logContext.debug(this, "------salesRepName:  " + salesRepName);
//        logContext.debug(this, "------salesRepEmail: " + salesRepEmail);
//        logContext.debug(this, "------url:           " + url);
//        try {
//            MailProcessFactory.singleton().create().sendBidEscalateForm(toAddress, ccAddress, pendDays, webQuoteNum, comments,
//                    bidTitle, custName, salesRepName, salesRepEmail, url);
//        } catch (EmailException e) {
//            e.printStackTrace();
//            throw e;
//        }
//        logContext.debug(this, "End of calling mail process: sendBidEscalateForm()");
//    }
    
    
    public boolean evalReturn(Quote quote)throws EmailException{
        boolean isEvalReturn = false;
        String toAddress = quote.getQuoteHeader().getCreatorEmail();
        String ccAddress = this.getCCToVADY9ForT2(quote);
        String title = quote.getQuoteHeader().getQuoteTitle();
        String comments = quote.getSpecialBidInfo().getEvaltnComment();
        String customerName = quote.getQuoteHeader().getCustName();
        String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();
        String pgsUrl = new StringBuffer().append(HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB)).append("&quoteNum=").append(webQuoteNum).toString();
        String subject = new StringBuffer().append("Quote Request, ").append(webQuoteNum).append(" has been returned.").toString();
         
        logContext.debug(this, "Begin to call mail process: evalReturn(Quote quote)");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------ccAddressList: " + ccAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------bidTitle:      " + title);
        logContext.debug(this, "------subject:           " + subject);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------comments:      " + comments);
        logContext.debug(this, "------sqoUrl:           " + pgsUrl);
        
        Map<String, String> paramMap = new HashMap<String,String>();
		paramMap.put("bidNum", webQuoteNum);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(title));
		paramMap.put("customerName", StringHelper.handleSpecialChar(customerName));
		paramMap.put("comments", comments);
		paramMap.put("url", StringEscapeUtils.escapeHtml(pgsUrl));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
        
        try {
        	MailProcessFactory.singleton().create().sendEmailForm(toAddress, ccAddress, null, subject, MailConstants.MAIL_TEMPLATE_BID_EVAL_RETURN, paramMap);
        	isEvalReturn = true;
        } catch (EmailException e) {
        	logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: sendBidAddlInfoProvidedForm()");
        return isEvalReturn;
    }
    
}
