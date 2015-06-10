package com.ibm.dsw.quote.findquote.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteByCustomerContract;
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
 * The <code>FindQuoteByCustomerAction</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class FindQuoteByCustomerAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        FindQuoteByCustomerContract findByCustomerContract = (FindQuoteByCustomerContract) contract;

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results = null;

        String salesQuoteFlag = getSalesQuoteFlag(findByCustomerContract);

        String quoteType = getQuoteType(findByCustomerContract);

        String overallStatus = getOverallStatus(findByCustomerContract);

        String submittedDays = getSubmittedDays(findByCustomerContract);

        String ecareFlag = getEcareFlag(findByCustomerContract);

        String classification = getClassification(findByCustomerContract);
        
        Map infoMap = new HashMap();
        
        String audienceCode = getAudienceCode(findByCustomerContract);

        if (findByCustomerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_SITE_NUMBER)) {
            results = quoteStatusProcess.findByCustomerNum(findByCustomerContract.getUserId(), findByCustomerContract
                    .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByCustomerContract.getSortFilter(), findByCustomerContract.getPageIndex(),
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, findByCustomerContract.getSiteNum(),
                    findByCustomerContract.getAgreementNum(), "", "", "", "", "", "", "0",
                    classification,findByCustomerContract.getActuationFilter(),infoMap,audienceCode);
        } else if (findByCustomerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_CUSTOMER_NAME)) {
            results = quoteStatusProcess.findByCustomerName(findByCustomerContract.getUserId(), findByCustomerContract
                    .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByCustomerContract.getSortFilter(), findByCustomerContract.getPageIndex(),
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, "", "", findByCustomerContract
                            .getCustomerName(), findByCustomerContract.getCountry(), "", "", "", "",
                    findByCustomerContract.getNameComparison(),classification,findByCustomerContract.getActuationFilter(),infoMap,audienceCode);
        }

        String countryName = "";
        if (findByCustomerContract.getCountry() != null && !findByCustomerContract.getCountry().equals(""))
            countryName = CacheProcessFactory.singleton().create().getCountryByCode3(
                    findByCustomerContract.getCountry()).getDesc();

        javax.servlet.http.Cookie cookie = findByCustomerContract.getSqoCookie();
        if (findByCustomerContract.getMarkCountryDefault() != null
                && !findByCustomerContract.getMarkCountryDefault().equals("")) {
            QuoteCookie.setCountryCookieValue(cookie, findByCustomerContract.getCountry());
        }

        handler.addObject(FindQuoteParamKeys.PO_GEN_STATUS, infoMap.get(FindQuoteParamKeys.PO_GEN_STATUS));
        handler.addObject(FindQuoteParamKeys.COUNTRY_NAME, countryName);
        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByCustomerContract);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_CUSTOMER;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        FindQuoteByCustomerContract findByCustomerContract = (FindQuoteByCustomerContract) contract;
        if (findByCustomerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_SITE_NUMBER)) {
            return "findByAgreementNum";
        } else if (findByCustomerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_CUSTOMER_NAME)) {
            return "findByCustomerName";
        }
        return "";
    }
}
