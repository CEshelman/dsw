package com.ibm.dsw.quote.submittedquote.action;

import is.domainx.User;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.contract.EditRQContract;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UpdateRQSBAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: May 14, 2007
 */

public class UpdateRQSBAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.EditRQBaseAction#validate(is.domainx.User, com.ibm.dsw.quote.base.domain.QuoteUserSession, java.lang.String)
     */
    public int validate(User user, QuoteUserSession quoteUserSession, String renewalQuoteNum) throws QuoteException {
        QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
        return capabilityProcess.validateEditRQ(user, quoteUserSession, renewalQuoteNum);
    }
    

    private void populateQuote(EditRQContract editRQContract) throws QuoteException {
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        quoteProcess.updateRenewalQuoteSBInfo(editRQContract.getUserId(), editRQContract.getQuoteNum());
    }
  
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        EditRQContract editRQContract = (EditRQContract) contract;
        String redirectURL = "";
        if (validate(editRQContract.getUser(), editRQContract.getQuoteUserSession(), editRQContract.getRenewalQuoteNum()) == QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS) {
            populateQuote(editRQContract);
            //redirect to draft renewal quote cust&partner tab
            redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
        } else {
            logContext.debug(this, "Validation failed, this quote can't be edit.");
            //TODO if use redirectURL, this action should be the requested action
            redirectURL = HtmlUtil.getURLForAction(editRQContract.getRequestedAction());
            redirectURL = redirectURL + "&" + ParamKeys.PARAM_QUOTE_NUM + "=" + editRQContract.getQuoteNum();
            String msgInfo = HtmlUtil.getTranMessageParam(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                    SubmittedQuoteMessageKeys.MSG_RQ_NOT_EDITABLE, true, null);
            StringBuffer sb = new StringBuffer();
            sb.append(redirectURL);
            sb.append("&" + ParamKeys.PARAM_TRAN_MSG + "=" + msgInfo);
            redirectURL = sb.toString();
        }
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        logContext.debug(this, "This action will be redirected to " + redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }
}
