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
import com.ibm.dsw.quote.findquote.contract.FindQuoteByPartnerContract;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcess;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants.OverallStatus;;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteByPartnerAction</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class FindQuoteByPartnerAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        FindQuoteByPartnerContract findByPartnerContract = (FindQuoteByPartnerContract) contract;
        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results = null;

        String salesQuoteFlag = getSalesQuoteFlag(findByPartnerContract);

        String quoteType = getQuoteType(findByPartnerContract);

        String overallStatus = getOverallStatus(findByPartnerContract);

        String submittedDays = getSubmittedDays(findByPartnerContract);

        String ecareFlag = getEcareFlag(findByPartnerContract);

        String classification = getClassification(findByPartnerContract);
        
        Map infoMap = new HashMap();
        
        String audienceCode = getAudienceCode(findByPartnerContract);
        
        String actuation = findByPartnerContract.getActuationFilter();

        if(findByPartnerContract.isPGSFlag()){
        	results = quoteStatusProcess.findByPartNum(findByPartnerContract.getUserId(), findByPartnerContract
                    .getReportingSalesReps(), salesQuoteFlag, quoteType, getOverallStatusForPGS(overallStatus), submittedDays,
                    findByPartnerContract.getSortFilter(), findByPartnerContract.getPageIndex(),
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, "", "", "", "", "", findByPartnerContract
                            .getPartnerSiteNum(), "", "", "0",
                            classification, actuation ==null?"":actuation, infoMap, audienceCode);
        }else{
        	if (findByPartnerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_PARTNER_NAME)) {
	            String partnerName = findByPartnerContract.getPartnerName();
	            if (findByPartnerContract.getPartnerTypeForName().equalsIgnoreCase(
	                    FindQuoteParamKeys.PARTNER_TYPE_FOR_NAME_RESELLER))
	                results = quoteStatusProcess.findByPartnerName(findByPartnerContract.getUserId(), findByPartnerContract
	                        .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
	                        findByPartnerContract.getSortFilter(), findByPartnerContract.getPageIndex(),
	                        FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, "", "", "", findByPartnerContract
	                                .getCountry(), "", "", partnerName, "", findByPartnerContract.getNameComparison(),
	                                classification, findByPartnerContract.getActuationFilter(),infoMap, audienceCode);
	            else if (findByPartnerContract.getPartnerTypeForName().equalsIgnoreCase(
	                    FindQuoteParamKeys.PARTNER_TYPE_FOR_NAME_DISTRIBUTER))
	                results = quoteStatusProcess.findByPartnerName(findByPartnerContract.getUserId(), findByPartnerContract
	                        .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
	                        findByPartnerContract.getSortFilter(), findByPartnerContract.getPageIndex(),
	                        FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, "", "", "", findByPartnerContract
	                                .getCountry(), "", "", "", partnerName, findByPartnerContract.getNameComparison(),
	                                classification, findByPartnerContract.getActuationFilter(),infoMap, audienceCode);
	        } else if (findByPartnerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_PARTNER_NUMBER)) {
	            if (findByPartnerContract.getPartnerTypeForNum().equalsIgnoreCase(
	                    FindQuoteParamKeys.PARTNER_TYPE_FOR_NUM_RESELLER))
	                results = quoteStatusProcess.findByPartNum(findByPartnerContract.getUserId(), findByPartnerContract
	                        .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
	                        findByPartnerContract.getSortFilter(), findByPartnerContract.getPageIndex(),
	                        FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, "", "", "", "", findByPartnerContract
	                                .getPartnerSiteNum(), "", "", "", "0",
	                                classification, findByPartnerContract.getActuationFilter(), infoMap,audienceCode);
	            else if (findByPartnerContract.getPartnerTypeForNum().equalsIgnoreCase(
	                    FindQuoteParamKeys.PARTNER_TYPE_FOR_NUM_DISTRIBUTER))
	                results = quoteStatusProcess.findByPartNum(findByPartnerContract.getUserId(), findByPartnerContract
	                        .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
	                        findByPartnerContract.getSortFilter(), findByPartnerContract.getPageIndex(),
	                        FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, "", "", "", "", "", findByPartnerContract
	                                .getPartnerSiteNum(), "", "", "0",
	                                classification, findByPartnerContract.getActuationFilter(), infoMap, audienceCode);
	
	        }
        }
        String countryName = "";
        if (findByPartnerContract.getCountry() != null && !findByPartnerContract.getCountry().equals(""))
            countryName = CacheProcessFactory.singleton().create()
                    .getCountryByCode3(findByPartnerContract.getCountry()).getDesc();

        javax.servlet.http.Cookie cookie = findByPartnerContract.getSqoCookie();
        if (findByPartnerContract.getMarkCountryDefault() != null
                && !findByPartnerContract.getMarkCountryDefault().equals("")) {
            QuoteCookie.setCountryCookieValue(cookie, findByPartnerContract.getCountry());

        }
        
        handler.addObject(FindQuoteParamKeys.PO_GEN_STATUS, infoMap.get(FindQuoteParamKeys.PO_GEN_STATUS));
        handler.addObject(FindQuoteParamKeys.COUNTRY_NAME, countryName);
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByPartnerContract);
        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_PARTNER;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        FindQuoteByPartnerContract findByPartnerContract = (FindQuoteByPartnerContract) contract;
        if (findByPartnerContract.isPGSFlag()) {
			return "";
		}
        if (findByPartnerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_PARTNER_NUMBER)) {
            return "findByPartnerSiteNum";
        } else if (findByPartnerContract.getFindType().equalsIgnoreCase(FindQuoteParamKeys.FIND_TYPE_PARTNER_NAME)) {
            return "findByPartnerName";
        }
        return "";
    }
    
    /**
     * set QS001=QS001,QS002,QS003 for PGS
     * @param overallStatus
     * @return
     */
    private String getOverallStatusForPGS(String overallStatus) {
    	return overallStatus.replace(OverallStatus.AWAITING_SPEC_BID_APPR, OverallStatus.AWAITING_SPEC_BID_APPR + ","
    				+ OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO + ","
    				+ OverallStatus.SPEC_BID_RETURN_FOR_CHG);
    }
}
