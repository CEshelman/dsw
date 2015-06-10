package com.ibm.dsw.quote.findquote.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteBySiebelNumContract;
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
 * The <code>FindQuoteBySiebelNumAction</code> clas.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class FindQuoteBySiebelNumAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	FindQuoteBySiebelNumContract findBySiebelNumContract = (FindQuoteBySiebelNumContract) contract;

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results;
        Map infoMap = new HashMap();

        if ("1".equals(findBySiebelNumContract.getCommonCriteriaFlag())) {

            String salesQuoteFlag = getSalesQuoteFlag(findBySiebelNumContract);

            String quoteType = getQuoteType(findBySiebelNumContract);

            String overallStatus = getOverallStatus(findBySiebelNumContract);

            String submittedDays = getSubmittedDays(findBySiebelNumContract);

            String ecareFlag = getEcareFlag(findBySiebelNumContract);

            String classification = getClassification(findBySiebelNumContract);
            
            String acqrtnCode = findBySiebelNumContract.isPGSFlag()? "" : findBySiebelNumContract.getActuationFilter();
            
            results = quoteStatusProcess.findBySiebelNumber(findBySiebelNumContract.getNumber(),
                    findBySiebelNumContract.getUserId(), findBySiebelNumContract.getReportingSalesReps(),
                    getEcareFlag(findBySiebelNumContract), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findBySiebelNumContract.getSortFilter(), findBySiebelNumContract.getPageIndex(),
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, classification, acqrtnCode,
                    findBySiebelNumContract.getRelatedQuoteFlag(), findBySiebelNumContract.getCommonCriteriaFlag(),infoMap);

        } else {
            results = quoteStatusProcess.findBySiebelNumber(findBySiebelNumContract.getNumber(),
                    findBySiebelNumContract.getUserId(), findBySiebelNumContract.getReportingSalesReps(),
                    getEcareFlag(findBySiebelNumContract), "", "", "", "0", "0", "1",
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, "", "", findBySiebelNumContract.getRelatedQuoteFlag(),
                    findBySiebelNumContract.getCommonCriteriaFlag(),infoMap);
        }

        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findBySiebelNumContract);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_SIEBEL_NUM;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        forms[0] = "findBySiebelNumForm";
        return "";
    }

    protected boolean validateLoBTypes(ProcessContract contract) {
        return false;
    }
}
