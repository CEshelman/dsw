package com.ibm.dsw.quote.findquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByNumContract;
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
 * The <code>DisplayFindQuoteByNumAction</code> class.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindQuoteByNumAction extends DisplayFindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.DisplayFindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplayFindQuoteByNumContract findByNumContract = (DisplayFindQuoteByNumContract) contract;

        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByNumContract);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.DisplayFindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_BY_NUM;
    }
}
