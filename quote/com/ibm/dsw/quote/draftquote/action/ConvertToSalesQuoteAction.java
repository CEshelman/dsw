package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.contract.ConvertContract;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ConvertToSalesQuoteAction</code> class is to convert renewval
 * quote to sales quote
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 13, 2007
 */
public class ConvertToSalesQuoteAction extends ConvertRQBaseAction {

    public boolean canBeConverted(ConvertContract cnvtContract, Quote quote) throws QuoteException {
        QuoteValidationRule validationRule = null;
        QuoteUserSession user = cnvtContract.getQuoteUserSession();
        
        try {
            validationRule = QuoteValidationRule.createRule(user, quote, null);
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
        }
        return validationRule.canCnvrtToSalesQuote();
    }
    
    public void executeConvertion(ConvertContract cnvtContract) throws QuoteException {
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        process.convertToSalsQuote(cnvtContract.getUserId(), "", false);
    }
    
    public String getValidationMessage() {
        String msg = HtmlUtil.getTranMessageParam(I18NBundleNames.ERROR_MESSAGE,
                DraftQuoteMessageKeys.MSG_CAN_NOT_CONVERT, false, null);
        return msg;
    }
    
}
