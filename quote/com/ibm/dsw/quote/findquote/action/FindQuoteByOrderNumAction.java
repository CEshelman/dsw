package com.ibm.dsw.quote.findquote.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteByOrderNumContract;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcess;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
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
 * The <code>FindQuoteByOrderNumAction</code> clas.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class FindQuoteByOrderNumAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	FindQuoteByOrderNumContract findByOrderNumContract = (FindQuoteByOrderNumContract) contract;

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results;
        Map infoMap = new HashMap();

        results = quoteStatusProcess.findByOrderNumber(findByOrderNumContract.getNumber(),
        		findByOrderNumContract.getUserId(), findByOrderNumContract.getReportingSalesReps(),
                    getEcareFlag(findByOrderNumContract), "1",
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, infoMap);

        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByOrderNumContract);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_ORDER_NUM;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        forms[0] = "findByOrderNumForm";
        return "";
    }

    protected boolean validateLoBTypes(ProcessContract contract) {
        return false;
    }
}
