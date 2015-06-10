package com.ibm.dsw.quote.newquote.action;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.home.action.QuoteRightColumnBaseAction;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteStateKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteViewKeys;
import com.ibm.dsw.quote.newquote.contract.DisplaySavedQuoteContract;
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
 * The <code>DisplaySavedQuoteAction</code> class is to display saved draft
 * quotes.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on Feb 28, 2007
 */
public class DisplaySavedQuoteAction extends QuoteRightColumnBaseAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.QuoteRightColumnBaseAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplaySavedQuoteContract displaySQContract = (DisplaySavedQuoteContract) contract;

        List retrievedDraftQuotes = findDraftQuotes(displaySQContract);

        javax.servlet.http.Cookie cookie = displaySQContract.getSqoCookie();

        if (NewQuoteParamKeys.PARAM_MARK_DEFAULT_VALUE.equalsIgnoreCase(displaySQContract.getMarkDefault())) {
            if (cookie != null) {
                QuoteCookie.setOwnerFilter(cookie, displaySQContract.getOwnerFilter());
                QuoteCookie.setTimeFilter(cookie, displaySQContract.getTimeFilter());
            }
        }
        handler.addObject(NewQuoteParamKeys.PARAM_DRAFT_SALES_QUOTE_LIST, retrievedDraftQuotes);
        handler.addObject(NewQuoteParamKeys.PARAM_DISPLAYED_DRAFT_SALES_QUOTE_CONTRACT, displaySQContract);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.QuoteRightColumnBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return NewQuoteStateKeys.DISPLAY_DRAFT_SALES_QUOTES;
    }

    /**
     * @param contract
     * @return
     * @throws QuoteException
     */
    protected List findDraftQuotes(DisplaySavedQuoteContract displayDraftQuoteContract) throws QuoteException {
        String ownerShipFilter = displayDraftQuoteContract.getOwnerFilter();
        String timeFilter = displayDraftQuoteContract.getTimeFilter();
        String userId = displayDraftQuoteContract.getUserId();

        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        return quoteProcess.findDraftQuotes(userId, ownerShipFilter, timeFilter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#validate(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected boolean validate(ProcessContract contract) {
        DisplaySavedQuoteContract displayDraftQuoteContract = (DisplaySavedQuoteContract) contract;
        if (displayDraftQuoteContract.getFormFlag() == null
                || !displayDraftQuoteContract.getFormFlag().equals(NewQuoteParamKeys.PARAM_FORM_FLAG_VALUE)) {
            return true;
        }

        return super.validate(displayDraftQuoteContract);
    }

    protected String getValidationForm() {
        return NewQuoteViewKeys.FORM_RETRIEVE_DRAFT_QUOTE;
    }
}
