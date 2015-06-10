package com.ibm.dsw.quote.findquote.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteByNumContract;
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
 * The <code>FindQuoteByNumAction</code> clas.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class FindQuoteByNumAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        FindQuoteByNumContract findByNumContract = (FindQuoteByNumContract) contract;

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results;
        Map infoMap = new HashMap();

        if ("1".equals(findByNumContract.getCommonCriteriaFlag())) {

            String salesQuoteFlag = getSalesQuoteFlag(findByNumContract);

            String quoteType = getQuoteType(findByNumContract);

            String overallStatus = getOverallStatus(findByNumContract);

            String submittedDays = getSubmittedDays(findByNumContract);

            String ecareFlag = getEcareFlag(findByNumContract);

            String classification = getClassification(findByNumContract);
            
            String acqrtnCode = findByNumContract.isPGSFlag()? "" : findByNumContract.getActuationFilter();
            
            results = quoteStatusProcess.findByQuoteNumber(findByNumContract.getNumber(),
                    findByNumContract.getUserId(), findByNumContract.getReportingSalesReps(),
                    getEcareFlag(findByNumContract), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByNumContract.getSortFilter(), findByNumContract.getPageIndex(),
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, classification, acqrtnCode,
                    findByNumContract.getRelatedQuoteFlag(), findByNumContract.getCommonCriteriaFlag(),infoMap);

        } else {
            results = quoteStatusProcess.findByQuoteNumber(findByNumContract.getNumber(),
                    findByNumContract.getUserId(), findByNumContract.getReportingSalesReps(),
                    getEcareFlag(findByNumContract), "", "", "", "0", "0", "1",
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, "", "", findByNumContract.getRelatedQuoteFlag(),
                    findByNumContract.getCommonCriteriaFlag(),infoMap);
        }
        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByNumContract);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_NUM;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        forms[0] = "findByNumForm";
        return "";
    }

    protected boolean validateLoBTypes(ProcessContract contract) {
        return false;
    }
}
