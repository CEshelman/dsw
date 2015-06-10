package com.ibm.dsw.quote.submittedquote.action;

import java.util.Map;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.contract.RemoveQuoteAttachmentContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RemoveSubmittedQuoteAttachmentAction<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2009-1-12
 */
public class RemoveSubmittedQuoteAttachmentAction extends SaveDraftComemntsBaseAction {

    /* (non-Javadoc)
     * user is approver and quote status is: Waitting for approve or return for additional info
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        RemoveQuoteAttachmentContract rmContract = (RemoveQuoteAttachmentContract) contract;
        String webQuoteNum = rmContract.getWebQuoteNum();
        String userId = rmContract.getUserId();
        Quote quote = null;
        try
        {
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            quote = qProcess.getSubmittedQuoteBaseInfo(rmContract.getWebQuoteNum(), rmContract.getUserId(), null);
        }
        catch ( NoDataException e )
        {
            logContext.error(this, e);
            throw new QuoteException(e);
        }
        logContext.debug(this, "save user draft comments");
        this.saveUserDraftComments(quote, rmContract);
        if ( !validateUserAccess(quote, rmContract) )
        {
            String msg = getI18NString(SubmittedQuoteMessageKeys.NO_PRIVILEGE_PERFORM, I18NBundleNames.ERROR_MESSAGE,
                    rmContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            logContext.info(this, "user has no privilege to perform this action: webQuoteNum=" 
                    + rmContract.getWebQuoteNum() + ", userId=" + rmContract.getUserId() + ", seqNum=" + rmContract.getAttchmtSeqNum());
            return handler.getUndoResultBean();
        }
        
        QuoteAttachmentProcess process = QuoteAttachmentProcessFactory.singleton().create();
        
        process.removeQuoteAttachment(rmContract.getWebQuoteNum(), rmContract.getAttchmtSeqNum(), rmContract.getUserId());
        
        String redirectURL = this.getRedirectURL(contract);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }
    
    protected String getRedirectURL(ProcessContract contract) {
        RemoveQuoteAttachmentContract rmContract = (RemoveQuoteAttachmentContract) contract;
        StringBuffer buff = new StringBuffer();
        String redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB);
        buff.append(redirectURL);
        HtmlUtil.addURLParam(buff, "quoteNum", rmContract.getWebQuoteNum());
        return buff.toString();
    }
    
    protected boolean validateUserAccess(Quote quote, RemoveQuoteAttachmentContract rmContract) throws QuoteException
    {
        if ( this.isBidIterSpbidEditAble(quote) )
        {
            return true;
        }
        QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
        Map rules = capabilityProcess.getSubmittedQuoteActionButtonsRule(rmContract.getQuoteUserSession(), quote);
        boolean isApprover = false;
        if (rules != null) {
            isApprover = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION)).booleanValue() 
            	||  ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS)).booleanValue();
        }
        if (!isApprover) {
            logContext.info(this, "User has no privilege to remove attachment on this special bid quote!");
            return false;
        }     
        return true;
    }

}
