package com.ibm.dsw.quote.findquote.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteByIBMerContract;
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
 * The <code>FindQuoteByIBMerAction</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class FindQuoteByIBMerAction extends FindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        FindQuoteByIBMerContract findByIBMerContract = (FindQuoteByIBMerContract) contract;

        QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();
        SearchResultList results = null;

        String salesQuoteFlag = getSalesQuoteFlag(findByIBMerContract);

        String quoteType = getQuoteType(findByIBMerContract);

        String overallStatus = getOverallStatus(findByIBMerContract);

        String submittedDays = getSubmittedDays(findByIBMerContract);

        String ecareFlag = getEcareFlag(findByIBMerContract);

        String classification = getClassification(findByIBMerContract);

        String ownerRoles = "";
        
        Map infoMap = new HashMap();
        
        for (int i = 0; i < findByIBMerContract.getOwnerRoles().length; i++) {
            ownerRoles = ownerRoles + findByIBMerContract.getOwnerRoles()[i]
                    + FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
        }

        if (findByIBMerContract.getOwnerType().equalsIgnoreCase(FindQuoteParamKeys.OWNER_TYPE_ME)) {
            results = quoteStatusProcess.findByIBMer(findByIBMerContract.getUserId(), findByIBMerContract
                    .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByIBMerContract.getSortFilter(), findByIBMerContract.getPageIndex(), getPageSize(), ecareFlag,
                    findByIBMerContract.getUserId(), "", "", ownerRoles, classification, findByIBMerContract.getActuationFilter(),infoMap);
        } else if (findByIBMerContract.getOwnerType().equalsIgnoreCase(FindQuoteParamKeys.OWNER_TYPE_OTHER)) {
            results = quoteStatusProcess.findByIBMer(findByIBMerContract.getUserId(), findByIBMerContract
                    .getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByIBMerContract.getSortFilter(), findByIBMerContract.getPageIndex(), getPageSize(), ecareFlag,
                    findByIBMerContract.getEmail(), findByIBMerContract.getFirstName(), findByIBMerContract
                            .getLastName(), ownerRoles, classification, findByIBMerContract.getActuationFilter(),infoMap);
        }

        javax.servlet.http.Cookie cookie = findByIBMerContract.getSqoCookie();
        if (findByIBMerContract.getMarkIBMerDefault() != null && !findByIBMerContract.getMarkIBMerDefault().equals("")) {
            QuoteCookie.setSubmittedOwner(cookie, findByIBMerContract.getOwnerType());
            String[] roles = findByIBMerContract.getOwnerRoles();
            List rolesList = new ArrayList();
            for (int i = roles.length; i > 0; i--) {
                rolesList.add(roles[i - 1]);
            }
            QuoteCookie.setSubmittedOwnerRole(cookie, rolesList);
        }

        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.addObject(FindQuoteParamKeys.PO_GEN_STATUS, infoMap.get(FindQuoteParamKeys.PO_GEN_STATUS));
        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByIBMerContract);
        return handler.getResultBean();
    }

    protected String getPageSize() {
        return FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_IBMER;
    }

    protected String getValidationForm2(String[] forms, ProcessContract contract) {
        return "findByIBMer";
    }
}
