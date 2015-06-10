package com.ibm.dsw.quote.findquote.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteByCountryContract;
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
 * The <code>FindQuoteByCountryAction</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class FindQuoteByCountryAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        FindQuoteByCountryContract findByCountryContract = (FindQuoteByCountryContract) contract;

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();

        String salesQuoteFlag = getSalesQuoteFlag(findByCountryContract);

        String quoteType = getQuoteType(findByCountryContract);

        String overallStatus = getOverallStatus(findByCountryContract);

        String submittedDays = getSubmittedDays(findByCountryContract);

        String ecareFlag = getEcareFlag(findByCountryContract);

        String classification = getClassification(findByCountryContract);

        String countryCode = findByCountryContract.getCountry();
        
        Map infoMap = new HashMap();
        
        SearchResultList results = quoteStatusProcess.findByCountry(findByCountryContract.getUserId(),
                findByCountryContract.getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                findByCountryContract.getSortFilter(), findByCountryContract.getPageIndex(),
                FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, countryCode,
                findByCountryContract.getState(), classification, findByCountryContract.getActuationFilter(),findByCountryContract.getSubRegion(),infoMap);
        
        //set cookie values if markCountryRegionDefault is set
        javax.servlet.http.Cookie cookie = findByCountryContract.getSqoCookie();
        if (findByCountryContract.getMarkCountryRegionDefault() != null
                && !findByCountryContract.getMarkCountryRegionDefault().equals("")) {
            QuoteCookie.setSubRegionCookieValue(cookie,findByCountryContract.getSubRegion());
            QuoteCookie.setCountryCookieValue(cookie, findByCountryContract.getCountry());
            QuoteCookie.setSubmittedCustState(cookie, findByCountryContract.getState());
        }
        
        if (StringUtils.isNotBlank(countryCode)) {
            Iterator iter = CacheProcessFactory.singleton().create().getStateListAsCodeDescObj(countryCode.trim())
                    .iterator();
            String stateName = "";
            while (iter.hasNext()) {
                CodeDescObj co = (CodeDescObj) iter.next();
                if (co.getCode().trim().equalsIgnoreCase(findByCountryContract.getState().trim())) {
                    stateName = co.getCodeDesc();
                    break;
                }
            }
            handler.addObject(FindQuoteParamKeys.STATE_NAME, stateName);
            handler.addObject(FindQuoteParamKeys.COUNTRY_NAME, CacheProcessFactory.singleton().create().getCountryByCode3(
                    countryCode).getDesc());
        }
        CodeDescObj subRegion = CacheProcessFactory.singleton().create().getSubRegionByCode(findByCountryContract.getSubRegion());
        if(subRegion != null){
            handler.addObject(FindQuoteParamKeys.SUB_REGION_NAME,subRegion.getCodeDesc()); 
        }
        
        handler.addObject(FindQuoteParamKeys.PO_GEN_STATUS, infoMap.get(FindQuoteParamKeys.PO_GEN_STATUS));
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByCountryContract);
        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_COUNTRY;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        return "";//findByCountryForm
    }
    
    protected boolean validate(ProcessContract contract) {
        boolean valid = super.validate(contract);
        HashMap map = new HashMap();
        FindQuoteByCountryContract findByCountryContract = (FindQuoteByCountryContract) contract;
        String country = findByCountryContract.getCountry().trim();
        String subRegion = findByCountryContract.getSubRegion().trim();
        if(StringUtils.isBlank(country) && StringUtils.isBlank(subRegion)){
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.ONE_OF_COUNTRY_AND_REGION_REQUIRES);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, FindQuoteParamKeys.IMT);
            map.put(FindQuoteParamKeys.SUB_REGION, field);
            addToValidationDataMap(contract, map);
            valid = false;
        }    
        return valid;
    }
}
