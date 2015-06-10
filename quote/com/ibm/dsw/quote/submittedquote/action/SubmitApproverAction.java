package com.ibm.dsw.quote.submittedquote.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmitApproverContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitApproverAction<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SubmitApproverAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -2011742595077772587L;
    transient LogContext logContext = LogContextFactory.singleton().getLogContext();
    static final String MSG_MAIL_SEND_FAIL = MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_MAIL_SEND_FAIL + ":" + MessageBeanKeys.ERROR;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        logContext.debug(this,"Begin to execute SubmitApproverAction.executeBiz");
        SubmitApproverContract apprvrContract = (SubmitApproverContract) contract;
        logContext.debug(this, "approv comments: " + apprvrContract.getApprvrComments());
        Locale locale = apprvrContract.getLocale();
        String webQuoteNum = apprvrContract.getWebQuoteNum();
        String userId = apprvrContract.getUserId();
        String apprvrAction = apprvrContract.getApprvrAction();
        String apprvrComments = apprvrContract.getApprvrComments();
        String returnReason = StringUtils.trimToEmpty(apprvrContract.getReturnReason());
        String returnReasonDesc = "";
        
        if ( apprvrComments == null )
        {
        	String msg = getI18NString(SubmittedQuoteMessageKeys.APPRVL_COMMENTS_NULL, I18NBundleNames.ERROR_MESSAGE,
                    apprvrContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            logContext.info(this, "approval comments is null: webQuoteNum=" + webQuoteNum + ", userId=" + userId + ", approve action=" + apprvrAction);
            return handler.getUndoResultBean();
        }
        
        QuoteUserSession salesRep = apprvrContract.getQuoteUserSession();
        int result = 0;
        boolean isDB2UpdateSuccessful = false;
        boolean isNotifyWorkFlowSuccessful = false;
        String currentApprEmail = null;
        List redirectMsgList = new ArrayList();
        int apprvrLevel = 0;
        //To retrieve the submitted quote
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        Quote quote = null;
        try {
            quote = qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, userId, null);
        } catch (NoDataException e1) {
            throw new QuoteException(e1);
        } catch (QuoteException e1) {
            throw e1;
        }
        
        //Validate user access  
        boolean hasActionPrivelege = true;
        if (!this.validateUserAccess(quote, apprvrContract))
        {
//            throw new QuoteException("User has no privilege to perform this action.");
            String msg = getI18NString(SubmittedQuoteMessageKeys.NO_PRIVILEGE_PERFORM, I18NBundleNames.ERROR_MESSAGE,
                    apprvrContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            hasActionPrivelege = false;
            logContext.info(this, "user has no privilege to perform this action: webQuoteNum=" + webQuoteNum + ", userId=" + userId);
            return handler.getUndoResultBean();
        }
        //verify if the approver is submitter or creator
       	if ( StringUtils.equalsIgnoreCase(apprvrContract.getUserId(), quote.getQuoteHeader().getSubmitterId()) 
       			|| StringUtils.equalsIgnoreCase(apprvrContract.getUserId(), quote.getQuoteHeader().getCreatorId()) ) 
       	{
       		String msg = getI18NString(SubmittedQuoteMessageKeys.SUBMITER_SAME_APPRVR, I18NBundleNames.ERROR_MESSAGE,
                       apprvrContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            hasActionPrivelege = false;
            logContext.info(this, "the approver is the same with the submiiter or creator, quoteNum=" + webQuoteNum 
               		+ ", userId=" + userId + ", submiter=" + quote.getQuoteHeader().getSubmitterId() + ", creator=" + quote.getQuoteHeader().getCreatorId());
            return handler.getUndoResultBean();
       	}
        if ( hasActionPrivelege )
        {
	        //here if action is save and notify
	        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
	        
	        apprvrLevel = quote.getQuoteUserAccess().getPendingAppLevel();
	        
			if(!"".equals(returnReason)){
				returnReasonDesc = process.getReasonDescByCode(returnReason);
			}
	        
	        logContext.debug(this, "Pending approval level is " + apprvrLevel);
	        logContext.debug(this, "approve level from post form is: " + apprvrContract.getApproveLevel());
	        logContext.debug(this, "isCanSupersedeAppr is: " + quote.getQuoteUserAccess().isCanSupersedeAppr());
	        if ( apprvrLevel != apprvrContract.getApproveLevel() && !quote.getQuoteUserAccess().isCanSupersedeAppr())
            {
                logContext.info(this, "approve level not match, return"); 
                String msg = getI18NString(SubmittedQuoteMessageKeys.RESUBMIT_APPROVE_ACTION, I18NBundleNames.ERROR_MESSAGE,
                        apprvrContract.getLocale());
                handler.addMessage(msg, MessageBeanKeys.ERROR);
                return handler.getUndoResultBean();
            }
            else
            {
            	// If it's a supersede approver, set the approval level to current user's approval level instead of  the pending level.
            	if (quote.getQuoteUserAccess().isCanSupersedeAppr()){
            		apprvrLevel = quote.getQuoteUserAccess().getAppLevel();
            	}
            		
		        //Get the user role 
		        StringBuffer userRole = new StringBuffer();
		        userRole.append(SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_APPROVER_LEVEL);
		        userRole.append(apprvrLevel);
		        
		        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
                String approverName = salesRep.getFullName();
                String customerName = quote.getQuoteHeader().getCustName();
		        if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_SAVE_DRAFT, apprvrAction) )
		        {
		            this.logContext.debug(this, "execute save as draft");
		            if ( !StringUtils.isBlank(apprvrComments) )
	                {
		                String quoteTxtTypeCode = "_L" + apprvrLevel + "_APR";
		                process.persistQuoteComment(webQuoteNum, userId, quoteTxtTypeCode, 
		                        apprvrComments, -1, -1);
	                }
		            
		            String approverAddReviewComments = apprvrContract.getApvrReviewComments();
		            if ( !StringUtils.isBlank(approverAddReviewComments) )
		            {
		                String quoteTxtTypeCode = "_L" + apprvrLevel + "_ARW";
	    				process.persistQuoteComment(webQuoteNum, userId, quoteTxtTypeCode, 
	    				        approverAddReviewComments, -1, -1);
		            }
		        }
		        else if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_SAVE_AND_NOTIFY, apprvrAction) )
		        {
		        	this.logContext.debug(this, "not empty execute here");
	                if ( !StringUtils.isBlank(apprvrContract.getTncTxt()) )
	                {
	                    logContext.debug(this, "save tnc txt: " + apprvrContract.getTncTxt());
		            	if (!hasDupQuoteTxt(process,webQuoteNum,apprvrContract.getTncTxt(),SpecialBidInfo.CommentInfo.CREDIT_J)) {
	                       process.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.CREDIT_J, apprvrContract.getTncTxt(), SpecialBidInfo.BEGIN_TNC, -1);
	                    }
	                }
	                if ( !StringUtils.isBlank(apprvrContract.getSummaryTxt()) )
	                {
	                    logContext.debug(this, "save summary txt: " + apprvrContract.getSummaryTxt());
		            	if (!hasDupQuoteTxt(process,webQuoteNum,apprvrContract.getSummaryTxt(),SpecialBidInfo.CommentInfo.SPBID_J)) {
		            		process.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.SPBID_J, apprvrContract.getSummaryTxt(), SpecialBidInfo.BEGIN_SUBMITTER, -1);
		            	}
	                }
	                if ( !StringUtils.isBlank(apprvrComments) )
	                {
	                    process.persistApproverActHistWithTransaction(webQuoteNum, userId, userRole.toString(), SubmittedQuoteConstants.APPRVR_ACTION_ADD_APRVR_COMMENT, apprvrComments);
	                }
	                //delete approver approve comments
	                process.deleteUserDraftComments(webQuoteNum, userId, 2);
	                //here add mail process to notify submittor
//	                boolean isSuccessful = false;
	                try {
	                    SpecialBidEmailHelper helper = new SpecialBidEmailHelper();
	                    //update cc to approver
	                    String toAddress = quote.getQuoteHeader().getSubmitterId();
	                    String ccAddress = userId;
	                    
//	                    isSuccessful = 
	                    helper.approverSaveJustAndNotify(webQuoteNum,approverName,userId, toAddress, ccAddress, bidTitle, 
	                    		customerName, quote.getQuoteHeader().isPGSQuote(), quote.getQuoteHeader().isSubmittedOnSQO());//sqProcess.notifyWorkflowOfApprvrAction(webQuoteNum, lastApprover, userId, action, sbContract.getSpecialBidComment());
	                } catch (EmailException e1) {
	                	logContext.error(this, "send mail fail for save and notify: " + e1);
	                	redirectMsgList.add(MSG_MAIL_SEND_FAIL);
	                }		                
		        }
		        else
		        {
			        //Get the pending approver level
			        //To retrieve the expire date from quote header
			        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy");
			        String bidExpireDate = sdfDate.format(quote.getQuoteHeader().getQuoteExpDate());
			        
			        //Call the SAP quote modify web service base on the approver action:     
			        
			        result = process.updateQuoteStatusByApprvrAction(quote, apprvrContract.getUser(), apprvrAction, salesRep, apprvrLevel);
			        logContext.debug(this, "SAP quote modify web service call result returns: " + result);
			        
			        if (result == SubmittedQuoteConstants.SAP_SUCCESS) {
			            //Notify workflow engine..if failed at interim approval or RETURN_FOR_ADD_INFO, we don't call SAP and don't make db2 change        
			            SpecialBidApprvrOjbect specialBidApprvr = new SpecialBidApprvrOjbect();
			            specialBidApprvr.setApprvrEmail(userId);
			            specialBidApprvr.setApprvrFirstName(apprvrContract.getUser().getFirstName());
			            specialBidApprvr.setApprvrlastName(apprvrContract.getUser().getLastName());
			            specialBidApprvr.setApprvrLevel(apprvrLevel);
			            
			            if ( !StringUtils.isBlank(apprvrContract.getTncTxt()) )
		                {
		                    logContext.debug(this, "save tnc txt: " + apprvrContract.getTncTxt());
			            	if (!hasDupQuoteTxt(process,webQuoteNum,apprvrContract.getTncTxt(),SpecialBidInfo.CommentInfo.CREDIT_J)) {
			            	    process.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.CREDIT_J, apprvrContract.getTncTxt(), SpecialBidInfo.BEGIN_TNC, -1);
			            	}
		                }
		                if ( !StringUtils.isBlank(apprvrContract.getSummaryTxt()) )
		                {
		                    logContext.debug(this, "save summary txt: " + apprvrContract.getSummaryTxt());
			            	if (!hasDupQuoteTxt(process,webQuoteNum,apprvrContract.getSummaryTxt(),SpecialBidInfo.CommentInfo.SPBID_J)) {
		                        process.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.SPBID_J, apprvrContract.getSummaryTxt(), SpecialBidInfo.BEGIN_SUBMITTER, -1);
		                    }
		                }
		                process.deleteUserDraftComments(webQuoteNum, userId, 1);
			            currentApprEmail = process.getCurrentApprover(webQuoteNum).getApprvrEmail();
			            
//			            if (isNotifyWorkFlowSuccessful) {
//			                logContext.info(this, "Successfully notify workflow engine service for quote number:" + webQuoteNum +" by approver:" + userId);
			                //Submit approver action and comments to db2 && call quote status change service if required in one transaction 
			                isDB2UpdateSuccessful = process.submitApprvrAction(webQuoteNum, userId, apprvrAction, userRole.toString(), apprvrComments, salesRep, quote.getQuoteUserAccess().isCanSupersedeAppr(),returnReason,apprvrLevel);
			                if (!isDB2UpdateSuccessful) {
			                    logContext.info(this, "Failed to update the data.");
			                    handler.addMessage(this.getI18NString(SubmittedQuoteMessageKeys.MSG_UNKNOW_ERR,
			                            MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, locale), MessageBeanKeys.ERROR);
			                    return handler.getUndoResultBean();
			                } else {					            
			                	if(quote.getQuoteUserAccess().isCanSupersedeAppr()){
			                		try
			                		{
			                			isNotifyWorkFlowSuccessful = process.notifyPendingApproverForSupersedeApprove(webQuoteNum, currentApprEmail, apprvrAction, apprvrComments, bidExpireDate, apprvrLevel, bidTitle, customerName, approverName, locale, quote.getQuoteHeader().isPGSQuote());
			                		}
			                		catch ( EmailException e )
			                		{
			                			logContext.error(this, "notify pending approver for supersese approve: " + e.getMessage());
			                			redirectMsgList.add(MSG_MAIL_SEND_FAIL);
			                		}
			                	} 
				                	//this statment will throw quote exception when getting quote data for mail send, throw email exception for mail send, but they both related for mail send
					            try
					            {
					            	isNotifyWorkFlowSuccessful = process.notifyWorkflowOfApprvrAction(webQuoteNum, currentApprEmail, specialBidApprvr, apprvrAction, apprvrComments, bidExpireDate,returnReasonDesc);
					            }
					            catch ( QuoteException e )
					            {
					            	logContext.error(this, "notify work flow of approver action: " + e.getMessage());
					            	redirectMsgList.add(MSG_MAIL_SEND_FAIL);
					            }
			                	
			                }
//			            } 
//			            else {
//			                logContext.error(this, "Failed to notify workflow engine service for quote number:" + webQuoteNum +" by approver:" + userId);
//			                handler.addMessage(this.getI18NString(SubmittedQuoteMessageKeys.MSG_WORKFLOW_NOT_AVAILABLE,
//			                        MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, apprvrContract.getLocale()), MessageBeanKeys.ERROR);
//			                //if it's for APPRVR_ACTION_RETURN_FOR_ADD_INFO or not final approve, SAP call doesn't happen, and db2 update is not need also.
//			                if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO)
//			                        ||(StringUtils.equals(apprvrAction,SubmittedQuoteConstants.APPRVR_ACTION_APPROVE) && !process.isSBApprovedByAllApprovers(webQuoteNum, apprvrLevel))) {
//			                    logContext.error(this, "Approver " + userId + " is not the final approver or just return for additional information, the workflow has trouble so the approve action is paused");
//			                    return handler.getUndoResultBean();
//			                } else {
//			                    //reject, return for change or final approve will go on even thought special bid workflow call failed.
//			                    logContext.error(this, "Approver " + userId + " is trying to " + apprvrAction + " quote:" + webQuoteNum + ", although the workflow has trouble the approve action is going on");
//			                    isDB2UpdateSuccessful = process.submitApprvrAction(webQuoteNum, userId, apprvrAction, userRole.toString(), apprvrComments, salesRep, quote.getQuoteUserAccess().isCanSupersedeAppr());
//			                    if (!isDB2UpdateSuccessful) {
//			                        logContext.error(this, "Failed to update the data.");
//			                        handler.addMessage(this.getI18NString(SubmittedQuoteMessageKeys.MSG_UNKNOW_ERR,
//			                                MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, locale), MessageBeanKeys.ERROR);
//			                        return handler.getUndoResultBean();
//			                    }
//			                    else
//			                    {
//					                if(quote.getQuoteUserAccess().isCanSupersedeAppr()){
//							            isNotifyWorkFlowSuccessful = process.notifyPendingApproverForSupersedeApprove(webQuoteNum, currentApprEmail, apprvrAction, apprvrComments, bidExpireDate, apprvrLevel, bidTitle, customerName, approverName, locale, quote.getQuoteHeader().isPGSQuote());
//					                }
//			                    	redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_WORKFLOW_NOT_AVAILABLE + ":" + MessageBeanKeys.ERROR);
//			                    }
//			                }
//			            }
			        } 
			        else if (result == SubmittedQuoteConstants.SAP_FAILED) {
			            logContext.info(this, "Failed to call SAP QuoteModifyService.");
			            return handleFailedSAPCall(handler, locale, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES,
                                SubmittedQuoteMessageKeys.MSG_SAP_NOT_AVAILABLE);
			        }
			        else if (result == SubmittedQuoteConstants.SAP_INVALID_DATA) {
			            logContext.info(this, "Failed to call SAP QuoteModifySerivce due to invalid data.");
			            return handleFailedSAPCall(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                                SubmittedQuoteMessageKeys.INVLD_QT_DATA_MSG);
			        }
		        }
		        addApproveMsg(redirectMsgList,apprvrAction);
            }    
	        
        }
       
        //BaseServlet will continue to excute the second action after execute this one,
        //below code is not necessary in fact.
        //If there is no action2 incidentlly, we will redirect to special bid tab
        String redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB) + "&quoteNum=" + webQuoteNum;
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, redirectMsgList);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
        return handler.getResultBean();
    }
    
    protected ResultBean handleFailedSAPCall(ResultHandler handler, Locale locale, String msgBundle, String msgKey) {

        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(msgKey, msgBundle, locale);
        
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        
        return resultBean;
    }

    protected String getValidationForm() {
        return SubmittedQuoteViewKeys.APPR_ACTION_FORM;
    }

    protected boolean validate(ProcessContract contract) {

        SubmitApproverContract apprvrContract = (SubmitApproverContract) contract;

        if (!super.validate(contract)) {
            return false;
        }

        HashMap vMap = new HashMap();
        String apprvrAction = apprvrContract.getApprvrAction();
        String apprvrComments = apprvrContract.getApprvrComments();
        String returnReason = StringUtils.trimToEmpty(apprvrContract.getReturnReason()); 
        
        if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_SAVE_AND_NOTIFY, apprvrAction) )
        {
            if ( StringUtils.isBlank(apprvrContract.getTncTxt()) && StringUtils.isBlank(apprvrContract.getSummaryTxt()) && StringUtils.isBlank(apprvrComments) )
            {
                FieldResult field = new FieldResult();
                //field.setMsg(MessageKeys.MSG_REQUIRED);
                field.setMsg(I18NBundleNames.BASE_MESSAGES, SubmittedQuoteMessageKeys.MSG_REQUIRE_WHEN_SAVE_AND_NOTIFY);
                vMap.put(SubmittedQuoteParamKeys.PARAM_SB_APPROVER_CMMT, field);
                addToValidationDataMap(apprvrContract, vMap);
                return false;
            }
            return true;
        }
        
        boolean hasReturnReason = hasReturnReason(apprvrAction);
        if(hasReturnReason && StringUtils.isBlank(returnReason)){
        	FieldResult field = new FieldResult();
        	field.setMsg(MessageKeys.MSG_REQUIRED);
        	field.addArg(I18NBundleNames.BASE_MESSAGES, SubmittedQuoteViewKeys.APPRVL_RETURN_REASON);
        	vMap.put(SubmittedQuoteParamKeys.PARAM_SB_APPROVER_CMMT, field);
        	addToValidationDataMap(apprvrContract, vMap);
        	return false;
        }
        
        if ((!StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_APPROVE))
                && StringUtils.isBlank(apprvrComments)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(I18NBundleNames.BASE_MESSAGES, SubmittedQuoteViewKeys.APPRVL_COMMENTS);
            vMap.put(SubmittedQuoteParamKeys.PARAM_SB_APPROVER_CMMT, field);
            addToValidationDataMap(apprvrContract, vMap);
            return false;
        }
        return true;
    }

    /**
     * check if the user can take action on this special bid quote
     * 
     * @param apprvrContract
     * @return
     * @throws QuoteException
     * @throws TopazException
     */
    private boolean validateUserAccess(Quote quote, SubmitApproverContract apprvrContract) throws QuoteException{
        boolean isApprover = false;
        Map rules;

        QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
        rules = capabilityProcess.getSubmittedQuoteActionButtonsRule(apprvrContract.getQuoteUserSession(), quote);

        if (rules != null) {
            isApprover = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION)).booleanValue();
        }
        if (!isApprover) {
            logContext.info(this, "User has no privilege to take action on this special bid quote!");
            return false;
        }
        return true;
    }
    
    /**
     * Add the message for approve actions.
     * 
     * @param redirectMsgList
     * @param apprvrAction
     */
    private void addApproveMsg(List redirectMsgList, String apprvrAction) {
    	if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_APPROVE , apprvrAction) ){
    		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_APPROVE_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
    	}else if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_REJECT, apprvrAction) ){
    		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_REJECT_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
    	}else if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES, apprvrAction) ){
    		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_RETURN_FOR_CHANGES_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
    	}else if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO, apprvrAction) ){
    		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_RETURN_FOR_ADD_INFO_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
    	}else if ( StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_SAVE_AND_NOTIFY, apprvrAction) ){
    		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_SAVE_AND_NOTIFY_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
    	}else if (StringUtils.equals(SubmittedQuoteConstants.APPRVR_SAVE_DRAFT, apprvrAction) ){
    		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_SAVE_DRAFT_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
    	}
    		
	}
    
    private boolean hasReturnReason(String apprvrAction) {
    	boolean flag = false;
    	if(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO.equals(apprvrAction) ||
    			SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES.equals(apprvrAction) ||
    			SubmittedQuoteConstants.APPRVR_ACTION_REJECT.equals(apprvrAction)){
    		flag = true;
    	}
		return flag;
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