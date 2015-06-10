/*
 * Created on 2007-5-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.contract.SpecialBidCommentDocumentsContract;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddSpecialBidCommentAttechmentsAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -5898876619777790094L;
    static final String MSG_MAIL_SEND_FAIL = MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_MAIL_SEND_FAIL + ":" + MessageBeanKeys.ERROR;

	/* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        logContext.debug(this,"Begin to execute AddSpecialBidCommentAttechmentsAction.executeBiz");
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract)contract;
//        List files = sbContract.getFiles();
        String role = sbContract.getUserRole();
        String webQuoteNum = sbContract.getWebQuoteNumber();
        String userId = sbContract.getUserId();
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        Quote quote = null;
        int apprvrLevel = -1;
        try {
            quote = qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, userId, null);
        } catch (NoDataException e1) {
        	logContext.error(this, e1);
            throw new QuoteException(e1);
        } catch (QuoteException e1) {
            throw e1;
        }
        
        boolean isSubmitter = false;
        boolean isReviewer = false;
        boolean isApprover = false;
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        List redirectMsgList = new ArrayList();
        QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
        Map rules = capabilityProcess.getSubmittedQuoteActionButtonsRule(sbContract.getQuoteUserSession(), quote);
        if(StringUtils.equals(role,SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_APPROVER)){
            logContext.debug(this,"Run as a approver.");
            isApprover = ((Boolean)rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION)).booleanValue();
            boolean isOtherApprover = ((Boolean)rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS)).booleanValue();
            isApprover = isApprover || isOtherApprover;
            if(!isApprover){
                logContext.error(this,"Run as a approver.But user is not a approver.");
                throw new QuoteException("User has no privilege to perform this action.");
            }
            apprvrLevel = quote.getQuoteUserAccess().getAppLevel();
            StringBuffer userRole = new StringBuffer();
            userRole.append(SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_APPROVER_LEVEL);
            userRole.append(apprvrLevel);
            role = userRole.toString();
        }else if(StringUtils.equals(role,SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_REVIEWER)){
            logContext.debug(this,"Run as a reviewer.");
            isReviewer = ((Boolean)rules.get(QuoteCapabilityProcess.DISPLAY_REVIEWER_SAVE_COMMENTS)).booleanValue();
            if(!isReviewer){
                logContext.error(this,"Run as a reviewer.But user is not a reviewer.");
                throw new QuoteException("User has no privilege to perform this action.");
            }
        }else if(StringUtils.equals(role,SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_SUBMITTER)){
            logContext.debug(this,"Run as a submitter.");
            isSubmitter = ((Boolean)rules.get(QuoteCapabilityProcess.DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST)).booleanValue();
            if(!isSubmitter){
                logContext.error(this,"Run as a submitter.But user is not a submitter.");
                throw new QuoteException("User has no privilege to perform this action.");
            }
        }else{
            throw new QuoteException("Cannot determine what role the user belongs to");
        }
        
        String lastApprover  = null;
        String action = null;
        try {
            SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
            if(isApprover){
                action = SubmittedQuoteConstants.APPRVR_ACTION_ADD_APRVR_COMMENT;
            }else if(isSubmitter){
            	SpecialBidApprvr apprvr = sqProcess.getCurrentApprover(webQuoteNum);
                if(apprvr.getApprvrAction().equalsIgnoreCase(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO)){
                    action = SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO;
                    lastApprover = apprvr.getApprvrEmail();
                }else{
                    action = SubmittedQuoteConstants.APPRVR_ACTION_APPRVL_PENDG;
                }
            }else if(isReviewer){
                action = SubmittedQuoteConstants.REVIEWER_ACTION_ADD_COMMENT;
                SpecialBidApprvr apprvr = sqProcess.getCurrentApprover(webQuoteNum);
                if(apprvr != null){
                    lastApprover = apprvr.getApprvrEmail();
                }
            }
            
            //System.err.println("is Approver is: "+ isApprover);
            
            if(!StringUtils.isBlank(sbContract.getSpecialBidComment())){
                logContext.debug(this,"Trying to save the user's comments.");
                //System.err.println("User Action is:"+action);
                sqProcess.persistApproverActHistWithTransaction(webQuoteNum, userId, role, action, sbContract.getSpecialBidComment());
                logContext.debug(this,"User's comments have been saved.");
            }
            if ( isApprover )
            {
            	sqProcess.deleteUserDraftComments(webQuoteNum, userId, 0);
            }
            if(isSubmitter){
                //here save tnc and summary
                if ( !StringUtils.isBlank(sbContract.getTncTxt()) )
                {
                    logContext.debug(this, "save tnc txt: " + sbContract.getTncTxt());
	            	if (!hasDupQuoteTxt(sqProcess,webQuoteNum,sbContract.getTncTxt(),SpecialBidInfo.CommentInfo.CREDIT_J)) {          
	            		sqProcess.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.CREDIT_J, sbContract.getTncTxt(), SpecialBidInfo.BEGIN_TNC, -1);
	            	}
                }
                
                if ( !StringUtils.isBlank(sbContract.getSummaryTxt()) )
                {
                    logContext.debug(this, "save summary txt: " + sbContract.getSummaryTxt());
	            	if (!hasDupQuoteTxt(sqProcess,webQuoteNum,sbContract.getSummaryTxt(),SpecialBidInfo.CommentInfo.SPBID_J)) {          
	            		sqProcess.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.SPBID_J, sbContract.getSummaryTxt(), SpecialBidInfo.BEGIN_SUBMITTER, -1);
	            	}
                }
                
                if(StringUtils.equals(action, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO)){
                    sqProcess.persistApproverAction(webQuoteNum, userId, "ADD_INFO_PROVIDED", 0);
                    sqProcess.deleteUserDraftComments(webQuoteNum, userId, 1);
	                //notify the workflow system.
	                logContext.debug(this,"Trying to nodify the workflow system for the updation of additional info.");
	                boolean isSuccessful = false;
	                try {
	                    SpecialBidEmailHelper helper = new SpecialBidEmailHelper();
	                    isSuccessful = helper.additionalInfoProvided(webQuoteNum,lastApprover,quote.getQuoteHeader().getSubmitterId(),sbContract.getSpecialBidComment());//sqProcess.notifyWorkflowOfApprvrAction(webQuoteNum, lastApprover, userId, action, sbContract.getSpecialBidComment());
	                } catch (QuoteException e1) {
	                	logContext.error(this, e1);
	                	redirectMsgList.add(MSG_MAIL_SEND_FAIL);
	                }
//	                if(!isSuccessful){
//	                    logContext.error(this, "Failed to notify workflow engine.");
//	                    handler.addMessage(this.getI18NString(SubmittedQuoteMessageKeys.MSG_WORKFLOW_NOT_AVAILABLE,
//	                            MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, sbContract.getLocale()), MessageBeanKeys.ERROR);
//	                    redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_WORKFLOW_NOT_AVAILABLE + ":" + MessageBeanKeys.ERROR);
//	                }else{
//	                    logContext.info(this,"Workflow system has been nodified.");
//	                }
                }
                else
                {
                	sqProcess.deleteUserDraftComments(webQuoteNum, userId, 0);
                }
            }
            if(isReviewer){
                //send email to approvers.
                try {
                	sqProcess.deleteUserDraftComments(webQuoteNum, userId, 0);
                    QuoteHeader quoteHeader = QuoteProcessFactory.singleton().create().getQuoteHdrInfoByWebQuoteNum(webQuoteNum);
//                    sqProcess.deleteUserDraftComments(webQuoteNum, userId, 0);
                    if(lastApprover != null){
                        StringBuffer toSB = new StringBuffer(lastApprover);
                        
                        if(toSB.length() > 0){
                            String link = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                                    + "&quoteNum=" + webQuoteNum;
                            boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();
                            String creatorSubmitter = ( !isPGSQuote || isPGSQuote && isSubmittedOnSQO )  ? quote.getQuoteHeader().getSubmitterId() : null;
                            String ccAddress = (new SpecialBidEmailHelper()).getEditorEmailAddress(webQuoteNum, creatorSubmitter, isSubmittedOnSQO);
                            String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_COMMENT_ADDED_PGS : MailConstants.MAIL_TEMPLATE_BID_COMMENT_ADDED;
                            MailProcessFactory.singleton().create().sendCommentAdded(toSB.toString(),ccAddress, webQuoteNum,
                                    quoteHeader.getQuoteTitle(), quoteHeader.getCustName(), sbContract.getSpecialBidComment(),
                                    link, quoteHeader.getLob().getCodeDesc(), mailTemplate);
                            //Review add comments no need to send mail to submitter for PGS quote
//                            if ( quote.getQuoteHeader().isPGSQuote() )
//                            {
//                            	link = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
//                            		+ "&quoteNum=" + webQuoteNum;
//                            	SalesRep partner = SubmittedQuoteProcessFactory.singleton().create().getSalesRepById(quote.getQuoteHeader().getSubmitterId());
//                            	MailProcessFactory.singleton().create().sendCommentAdded(partner.getEmailAddress(), null, webQuoteNum,
//                                        quoteHeader.getQuoteTitle(), quoteHeader.getCustName(), sbContract.getSpecialBidComment(),
//                                        link, quoteHeader.getLob().getCodeDesc(), MailConstants.MAIL_TEMPLATE_BID_COMMENT_ADDED_PGS);
//                            }
                        }
                    }
                } catch (EmailException e) {
//                    String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_MAIL_SEND_ERROR, I18NBundleNames.BASE_MESSAGES, sbContract
//                            .getLocale());
//                    handler.addMessage(msg, MessageBeanKeys.ERROR);
//                    redirectMsgList.add(I18NBundleNames.BASE_MESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_MAIL_SEND_ERROR + ":" + MessageBeanKeys.ERROR);
                    logContext.error(this, "Send mail failed:" + e.getMessage());
                    redirectMsgList.add(MSG_MAIL_SEND_FAIL);
                }
            }
        } catch (QuoteException e) {
            throw e;
        } catch (Exception e) {
        	logContext.error(this, e);
            throw new QuoteException(e);
        }
        logContext.debug(this,"End executing AddSpecialBidCommentAttechmentsAction.executeBiz");
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, sbContract.getRedirectURL());
        handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, redirectMsgList);
        return handler.getResultBean();
    }
    
    protected boolean validate(ProcessContract contract) {
        logContext.debug(this,"Validating inputs.");
        HashMap vMap = new HashMap();
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract)contract;
        String comment = sbContract.getSpecialBidComment();
//        List attachments = sbContract.getFiles();
        if ( !StringUtils.equals("1", sbContract.getSalesRepUpdatedData()) )
        {
            if(StringUtils.isBlank(comment))
            {
	            logContext.debug(this,"Validation failed, neither comments nor attachement has a value!");
	            FieldResult fieldResult = new FieldResult();
	            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SubmittedQuoteMessageKeys.MSG_COMMENT_OR_ATTACHMENTS_REQUIRED);
	            vMap.put(SubmittedQuoteParamKeys.PARAM_SB_CMMT , fieldResult);
	            addToValidationDataMap(contract,vMap);
	            return false;
            }
        }
        else
        {
            if ( StringUtils.isBlank(comment) && StringUtils.isBlank(sbContract.getTncTxt()) && StringUtils.isBlank(sbContract.getSummaryTxt()) )
            {
                logContext.debug(this,"Validation failed, neither comments nor attachement has a value!");
	            FieldResult fieldResult = new FieldResult();
	            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SubmittedQuoteMessageKeys.MSG_REQUIRE_WHEN_SAVE_AND_NOTIFY);
	            vMap.put(SubmittedQuoteParamKeys.PARAM_SB_CMMT , fieldResult);
	            addToValidationDataMap(contract,vMap);
	            return false;
            }
        }
        return super.validate(contract);
    }
        
    private boolean hasDupQuoteTxt(SubmittedQuoteProcess process,String webQuoteNum, String quoteText,String qtTxtTypeCode){
        // Duplicate record check for text
         if (quoteText != null){
     	   if (StringUtils.isNotBlank(quoteText)){
     		   List quoteTxts = null;
     		   try {
     			   quoteTxts = (List)process.getQuoteTxtHistory(webQuoteNum,qtTxtTypeCode,1);
     		   } catch (QuoteException e) {
     			   e.printStackTrace();
     		   }     		   
	           if ( quoteTxts != null ) {
	              if (quoteTxts.size() > 0 ){
	     	         for ( int j = 0; j < quoteTxts.size(); j++ )
	 	             {
	     	        	com.ibm.dsw.quote.common.domain.QuoteTxt qtTxt = (com.ibm.dsw.quote.common.domain.QuoteTxt)quoteTxts.get(j);
	     	        	String quoteDBText = qtTxt.getQuoteText();
	     	        	if ( quoteDBText != null ) {
	     	        		if (quoteDBText.equals(quoteText)) {
	     	        			// Duplicate text
	     	        			return true;
	     	        		}
	     	        	}
	                 }
	              }
	          }
     	   }	                	   
        }    
    	return false;
    }
}
