package com.ibm.dsw.quote.submittedquote.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
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
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CancelApprovedBid<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2009-8-13
 */
public class CancelApprovedBidAction extends CancelQuoteAction{
    private static final long serialVersionUID = 250103022711541712L;
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
        SubmitApproverContract apprvrContract = (SubmitApproverContract) contract;
        logContext.debug(this, "approv comments: " + apprvrContract.getApprvrComments());
        
//        Locale locale = apprvrContract.getLocale();
        String webQuoteNum = apprvrContract.getWebQuoteNum();
        String userId = apprvrContract.getUserId();
//        String apprvrAction = apprvrContract.getApprvrAction();
        String apprvrComments = apprvrContract.getApprvrComments();
        QuoteUserSession salesRep = apprvrContract.getQuoteUserSession();
//        int result = 0;
//        boolean isDB2UpdateSuccessful = false;
//        boolean isNotifyWorkFlowSuccessful = false;
//        String currentApprEmail = null;
        List redirectMsgList = new ArrayList();
        int apprvrLevel = 0;
        
        //To retrieve the submitted quote
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
        
        Quote quote = null;
        try {
            quote = sqProcess.getSubmittedQuoteForCancel(webQuoteNum);
            QuoteUserAccess access = qProcess.getQuoteUserAccess(webQuoteNum, userId, null);
            quote.setQuoteUserAccess(access);
            
        } catch (NoDataException e1) {
            throw new QuoteException(e1);
        } catch (QuoteException e1) {
            throw e1;
        }
        
        //Validate user access  
//        boolean hasActionPrivelege = true;
        if (!this.validateUserAccess(quote, apprvrContract))
        {
            String msg = getI18NString(SubmittedQuoteMessageKeys.NO_PRIVILEGE_PERFORM, I18NBundleNames.ERROR_MESSAGE,
                    apprvrContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
//            hasActionPrivelege = false;
            logContext.info(this, "user has no privilege to perform this action: webQuoteNum=" + webQuoteNum + ", userId=" + userId);
            return handler.getUndoResultBean();
        }
        
        
        // Cancel the quote in SAP & DB
        boolean isSuccessful = cancelQuote(quote, userId);
        if (!isSuccessful)
            return handleFailedRequest(handler, apprvrContract.getLocale());
        
        // Cancel all derived quotes
        List dervdBids = sqProcess.getDerivedApprvdBids(webQuoteNum);
        for (int i = 0; dervdBids != null && i < dervdBids.size(); i++) {
            QuoteHeader dervdHeader = (QuoteHeader) dervdBids.get(i);
            cancelDervdQuote(dervdHeader, userId);
        }
        
        //insert approver comments to approve history
        apprvrLevel = quote.getQuoteUserAccess().getAppLevel();
        StringBuffer userRole = new StringBuffer();
        userRole.append(SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_APPROVER_LEVEL);
        userRole.append(apprvrLevel);
        sqProcess.persistApproverActHistWithTransaction(webQuoteNum, userId, userRole.toString(), SubmittedQuoteConstants.APPRVR_ACTION_CANCEL_APPROVED_BID, apprvrComments);
        logContext.debug(this, "save cancel comments succ");
        
        //send mail to creator and opportunity owner
        SpecialBidEmailHelper helper = new SpecialBidEmailHelper();
        String toAddress = quote.getQuoteHeader().getSubmitterId();
        String bidTitle = quote.getQuoteHeader().getQuoteTitle();
        String customerName = quote.getQuoteHeader().getCustName();
        try
        {
        	helper.cancelApprovedBid(webQuoteNum, salesRep.getFullName(), userId, toAddress, quote.getQuoteHeader().getOpprtntyOwnrEmailAdr(), bidTitle, 
        		customerName, apprvrComments, quote.getQuoteHeader().isPGSQuote(), quote.getQuoteHeader().isSubmittedOnSQO(), quote);
        }
        catch ( EmailException e )
        {
        	logContext.error(this, "cancel approved bid send mail: " + e.getMessage());
        	redirectMsgList.add(MSG_MAIL_SEND_FAIL);
        }
        
        //@todo 435375 add code here for approver cancel the quote
        
        //BaseServlet will continue to excute the second action after execute this one,
        //below code is not necessary in fact.
        //If there is no action2 incidentlly, we will redirect to special bid tab
        redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_CANCEL_SUCCESS + ":" + MessageBeanKeys.INFO);
        String redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB) + "&quoteNum=" + webQuoteNum;
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, redirectMsgList);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
        return handler.getResultBean();
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
//        String apprvrAction = apprvrContract.getApprvrAction();
        String apprvrComments = apprvrContract.getApprvrComments();
        
        
        if ( StringUtils.isBlank(apprvrComments) ) {
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
        
        boolean canDoCancel = true;
        QuoteUserSession user = apprvrContract.getQuoteUserSession();
        int userAccess = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        
        try {
            QuoteValidationRule rule = QuoteValidationRule.createRule(user, quote, null);
            canDoCancel = rule.canCancelApprovedBid(userAccess);
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }

        if (!canDoCancel) {
            logContext.info(this, "User has no privilege to take action on this special bid quote!");
        }
        
        return canDoCancel;
    }
}
