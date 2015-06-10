package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.mail.process.MailProcess;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SendNewCustomerEmailAction</code> class is to add reviewers to
 * submitted quote
 * 
 * @author jfwei@cn.ibm.com
 * 
 * Created on 2010-5-18
 */
public class SendNewCustomerEmailAction extends SaveDraftComemntsBaseAction {

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        SubmittedQuoteBaseContract submittedQuoteBaseContract = (SubmittedQuoteBaseContract) contract;

        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

        String webQuoteNum = submittedQuoteBaseContract.getQuoteNum();
        Quote quote = null;
        try {
            quote = quoteProcess.getSubmittedQuoteBaseInfo(webQuoteNum, submittedQuoteBaseContract.getUserId(),
                    submittedQuoteBaseContract.getQuoteUserSession().getUp2ReportingUserIds());
        } catch (TopazException e) {
            logContext.error(this, "Failed to get submitted qutoe base info, webQuoteNum= " + webQuoteNum);
            throw new QuoteException(e);
        }
        String mailTo = null;
        String contractUserId = submittedQuoteBaseContract.getUserId();
        String submitterId = quote.getQuoteHeader().getSubmitterId();
        if (!contractUserId.equalsIgnoreCase(submitterId)) {
            mailTo = contractUserId + "," + submitterId;
        } else {
            mailTo = submitterId;
        }
        //Send Email
        MailProcess mailProcess = MailProcessFactory.singleton().create();
        mailProcess.sendCreateCustomer(mailTo, quote.getQuoteHeader(), submittedQuoteBaseContract.getLocale(), quote
                .getCustomer());
        SubmittedQuoteProcess submittedQuoteProcess = SubmittedQuoteProcessFactory.singleton().create();
        //update send mail flag
        submittedQuoteProcess.updatePARegstrnEmailFlag(contractUserId, webQuoteNum);

        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();

    }

}
