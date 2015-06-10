package com.ibm.dsw.quote.submittedquote.action;

import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
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

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteBaseAction<code> class.
 *    
 * @author: gancm@cn.ibm.com
 * 
 * Creation date: 2007-05-14
 */

public class CancelQuoteAction extends BaseContractActionHandler {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;
        String userId = baseContract.getUserId();
        String webQuoteNum = baseContract.getQuoteNum();
        Quote quote = null;
        QuoteHeader quoteHeader = null;
        boolean isSuccessful = true;
        List dervdBids = null;

        try {
            // Get quote information
            SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
            quote = sqProcess.getSubmittedQuoteForCancel(webQuoteNum);
            quoteHeader = quote.getQuoteHeader();
            
            if (QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(quoteHeader.getQuoteStageCode())) {
                return handlerSuccessfullRequest(handler, baseContract.getLocale(), quoteHeader.isRenewalQuote());
            }
            
            // Cancel the quote in SAP & DB
            isSuccessful = cancelQuote(quote, userId);
            if (!isSuccessful)
                return handleFailedRequest(handler, baseContract.getLocale());
            
            // Cancel all derived quotes
            dervdBids = sqProcess.getDerivedApprvdBids(webQuoteNum);
            for (int i = 0; dervdBids != null && i < dervdBids.size(); i++) {
                QuoteHeader dervdHeader = (QuoteHeader) dervdBids.get(i);
                cancelDervdQuote(dervdHeader, userId);
            }

        } catch (NoDataException nde) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(nde));
            throw new QuoteException("The input submitted quote number is invalid.", nde);
        }
        
        // Call special bid workflow service
        if (quoteHeader.getSpeclBidFlag() == 1
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
                || quoteHeader.isRenewalQuote()) {
            try {
                logContext.debug(this, "Try to call serviceHelper.cancelApprovalProcess.");
                SpecialBidEmailHelper serviceHelper = new SpecialBidEmailHelper();
                isSuccessful = serviceHelper.cancelApprovalProcess(quoteHeader.getWebQuoteNum());
            } catch (QuoteException e) {
                logContext.error(this, "Failed to call cancelApproval service.");
                isSuccessful = false;
                String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_MAIL_SEND_FAIL, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, baseContract.getLocale());
                handler.addMessage(msg, MessageBeanKeys.ERROR);
            }

//            if (isSuccessful) {
//                logContext.info(this, "Successfully called work flow engine service");
//            } else {
//                logContext.error(this, "Failed to call work flow engine service.");
//                return handleFailedRequest(handler, baseContract.getLocale());
//            }
        } else {
            logContext.debug(this, "It's not a specialbid or OverallStatus is not Special bid approved.");
        }
        
        //@todo 435375 add code here for quote cancel by evaluator/editor
        if ( quoteHeader.isPGSQuote() )
        {
        	if((quoteHeader.getSpeclBidFlag() != 1) 
        			|| (quoteHeader.getSpeclBidFlag() == 1 && quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED))){
        		try {
                    logContext.debug(this, "Try to call serviceHelper.cancelApprovalProcessToBPandEditor.");
                    SpecialBidEmailHelper serviceHelper = new SpecialBidEmailHelper();
                    isSuccessful = serviceHelper.cancelApprovalProcessToBPandEditor(quote);
                } catch (QuoteException e) {
                    logContext.error(this, "Failed to call cancelApproval service.");
                    isSuccessful = false;
                    String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_MAIL_SEND_FAIL, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, baseContract.getLocale());
                    handler.addMessage(msg, MessageBeanKeys.ERROR);
                }
        	}
        }

        return handlerSuccessfullRequest(handler, baseContract.getLocale(), quoteHeader.isRenewalQuote());
    }
    
    protected boolean cancelDervdQuote(QuoteHeader header, String userId) {
        
        // If the derived quote is cancelled or ordered, don't call the quote-status-change service.
        if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER)
                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED))
            return true;
        
        boolean success = true;
        String webQuoteNum = header.getWebQuoteNum();
        
        try {
            // Call quote status change service
            QuoteModifyServiceHelper serviceHelper = new QuoteModifyServiceHelper();
            serviceHelper.cancelQuote(header);
            
            // Update quote stage to cancel in DB
            SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
            sqProcess.updateQuoteStageToCancel(userId, webQuoteNum);
            
        } catch (WebServiceException e) {
            logContext.error(this, "Failed to cancel quote " + webQuoteNum + " in SAP.");
            success = false;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            success = false;
        }
        
        return success;
    }
    
    protected boolean cancelQuote(Quote quote, String userId) {
        boolean success = true;
        String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();
        
        try {
            // Call quote status change service
            SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
            success = sqProcess.callQuoteStatusChangeServiceToCancel(quote);
            
            if (success) {
                logContext.debug(this, "Successfully cancelled quote " + webQuoteNum + " in SAP.");
            } else {
                logContext.error(this, "Failed to cancel quote " + webQuoteNum + " in SAP.");
                return false;
            }
            
            // Update quote stage to cancel in DB
            sqProcess.updateQuoteStageToCancel(userId, webQuoteNum);
            
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            success = false;
        }
        
        return success;
    }

    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;

        handler.addObject(ParamKeys.PARAM_USER_OBJECT, baseContract.getUser());
        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, baseContract.getQuoteNum());
    }

    protected ResultBean handleFailedRequest(ResultHandler handler, Locale locale) {

        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(SubmittedQuoteMessageKeys.MSG_CANCEL_FAILED,
                MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        return resultBean;
    }
    
    private ResultBean handlerSuccessfullRequest(ResultHandler handler, Locale locale, boolean isRenewalQuote) throws ResultBeanException {
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        ResultBean resultBean = handler.getResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        this.logContext.info(this, "mess in mb: " + messageBean.getMessages());
        this.logContext.info(this, "mess in mb: " + messageBean.getMessage());
        String infoMsg = null;
        if(isRenewalQuote){
            infoMsg = getI18NString(SubmittedQuoteMessageKeys.MSG_CANCEL_SB_SUCCESS, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        } else {
            infoMsg = getI18NString(SubmittedQuoteMessageKeys.MSG_CANCEL_SUCCESS, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        }
        messageBean.addMessage(infoMsg, MessageBeanKeys.INFO);
        resultBean.setMessageBean(messageBean);
        return resultBean;
    }

}
