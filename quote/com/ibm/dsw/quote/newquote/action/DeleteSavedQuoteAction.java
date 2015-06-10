package com.ibm.dsw.quote.newquote.action;

import java.util.HashMap;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.contract.DisplaySavedQuoteContract;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidInputException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteUnAuthorizedException;
import com.ibm.dsw.quote.newquote.util.NewQuoteUtils;
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
 * The <code>DeleteSavedQuoteAction</code> class is to delete saved draft
 * quote.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on Feb 28, 2007
 */
public class DeleteSavedQuoteAction extends DisplaySavedQuoteAction {

    public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplaySavedQuoteContract displayDraftQuoteContract = (DisplaySavedQuoteContract) contract;

        String quoteNum = displayDraftQuoteContract.getQuoteId();
        if (quoteNum == null || quoteNum.equals(""))
            throw new NewQuoteInvalidInputException();

        if (!NewQuoteUtils.isQuoteBelongsToUser(displayDraftQuoteContract.getUserId(), quoteNum))
            throw new NewQuoteUnAuthorizedException();

        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        quoteProcess.deleteQuote(quoteNum, displayDraftQuoteContract.getUserId());

        return super.executeProcess(displayDraftQuoteContract, handler);
    }

    protected boolean validate(ProcessContract contract) {
        DisplaySavedQuoteContract savedQuoteContract = (DisplaySavedQuoteContract) contract;

        String quoteNum = savedQuoteContract.getQuoteId();
        String timeFilter = savedQuoteContract.getTimeFilter();
        String ownerFilter = savedQuoteContract.getOwnerFilter();
        String formFlag = savedQuoteContract.getFormFlag();
        if (quoteNum == null || quoteNum.equals("") || timeFilter == null || timeFilter.equals("")
                || ownerFilter == null || ownerFilter.equals("") || formFlag == null || formFlag.equals("")) {
            FieldResult field = new FieldResult();
            HashMap map = new HashMap();

            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.MSG_URL_PARAM_NOT_VALID);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.URL_PARAM);
            map.put(NewQuoteParamKeys.URL_PARAM, field);
            addToValidationDataMap(savedQuoteContract, map);
            return false;
        }

        return true;
    }
}
