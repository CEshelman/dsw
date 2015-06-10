package com.ibm.dsw.quote.customer.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.config.CustomerViewKeys;
import com.ibm.dsw.quote.customer.contract.CustomerSearchContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.partner.process.PartnerProcess;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerSearchAttributeAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */

public class CustomerSearchAttributeAction extends CustomerBaseAction {
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.CustomerBaseAction#getEObject(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected Object getEObject(ProcessContract contract) throws QuoteException {
        //get input search criteria from contract
        CustomerSearchContract csContract = (CustomerSearchContract) contract;
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        
        int findActiveFlag = getSearchFindActiveFlag(csContract);
        String searchLob = getSearchLob(csContract);
        int startPos = getStartPos(csContract);
        
        //get the result list from db
        CustomerProcess process = null;
        CustomerSearchResultList resultList = null;
        try {
        	int searchType = 0;

        	if(CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(csContract.getSearchFor())){
        		searchType = 1;
        	}
            process = CustomerProcessFactory.singleton().create();
            resultList = process.searchCustomerByAttr(csContract.getCustomerName(), csContract.getCountry(), csContract.getContractOption(), 
                    csContract.getAnniversary(), searchLob, findActiveFlag, startPos, csContract.getState(), searchType, csContract.getQuoteUserSession().getAudienceCode(), csContract.getQuoteUserSession().getSiteNumber());
            
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing topaz process", e);
        }
        return resultList;
    }
    
    protected int getStartPos(CustomerSearchContract c) {
    	String startPosStr = c.getStartPos();
    	if (StringUtils.isBlank(startPosStr) ) return 0;
    	return Integer.parseInt(startPosStr);
    }
    
    protected String getSearchLob(CustomerSearchContract csContract) {
        String lob = csContract.getLob();
        String findActiveFlag = csContract.getFindActiveCusts();
        String searchLob = csContract.getLob();
        
        if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob)) {
            if (CustomerConstants.ALL_CUSTS.equalsIgnoreCase(findActiveFlag))
                searchLob = QuoteConstants.LOB_PAUN;
            else if (CustomerConstants.ACTIVE_PA_CUSTS.equalsIgnoreCase(findActiveFlag))
                searchLob = QuoteConstants.LOB_PA;
            else if (CustomerConstants.ACTIVE_PAE_CUSTS.equalsIgnoreCase(findActiveFlag))
                searchLob = QuoteConstants.LOB_PAE;
            else if (CustomerConstants.ACTIVE_CSA_CUSTS.equalsIgnoreCase(findActiveFlag))
                searchLob = QuoteConstants.LOB_CSA;
        }
        
        return searchLob;
    }
    
    protected int getSearchFindActiveFlag(CustomerSearchContract csContract) {
        String findActiveFlag = csContract.getFindActiveCusts();
        if (CustomerConstants.ALL_CUSTS.equalsIgnoreCase(findActiveFlag))
            return 0;
        else
            return 1;
    }
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        CustomerSearchContract csContract = (CustomerSearchContract) contract;
        
        String cntry = csContract.getCountry();
        String lob = csContract.getLob();
        
        if (StringUtils.isBlank(cntry) || StringUtils.isBlank(lob)) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }
        else
            handler.setState(getState(contract));

        Object eObject = getEObject(contract);
        CustomerSearchResultList resultList = (CustomerSearchResultList) eObject;
        
        String pageFrom=csContract.getPageFrom();
        if(pageFrom.equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
          	 handler.addObject(DraftQuoteParamKeys.PAGE_FROM, pageFrom);
          	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, csContract.getMigrationReqNum());
          }
        
        /**
         * Get EC Line flag in the quote head.
         */
        boolean isECEligible = super.getQuoteHdrInfo(csContract.getUserId()).isECEligible();
        handler.addObject(ParamKeys.ECELIGIBLE_FLAG,isECEligible);
        
        if (resultList != null && resultList.getResultCount() > 0) {
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
            addContractToResult(contract, handler);
            return handler.getResultBean();
        } else {
            handler.setState(CustomerStateKeys.STATE_DISPLAY_SEARCH_CUSTOMER);
            addContractToResult(contract, handler);
            
            if (CustomerConstants.LOB_FCT.equalsIgnoreCase(csContract.getLob())
                    && CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(csContract.getSearchFor())) {
                PartnerProcess psp = PartnerProcessFactory.singleton().create();
                List countryList = psp.findValidCountryList(csContract.getCountry(), csContract.getLob());
                if (countryList != null)
                    handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, countryList);
            }
            
            return handleNoDataMessage(handler, csContract.getLocale());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.CustomerBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return CustomerStateKeys.STATE_DISPLAY_CUSTOMER_RESULTS;
    }

    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        CustomerSearchContract csContract = (CustomerSearchContract) contract;
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        
        super.addContractToResult(csContract, handler);
        
        CodeDescObj ctrctVariant = null;
        try {
            ctrctVariant = CacheProcessFactory.singleton().create().getCtrctVariantByCode(csContract.getContractOption());
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }
        
        if (csContract.getCountry() != null) {
            Country cntryObj = this.getCntryObject(csContract.getCountry());
            handler.addObject(ParamKeys.PARAM_COUNTRY_OBJECT, cntryObj);
        }
        handler.addObject(ParamKeys.PARAM_CUST_NAME, csContract.getCustomerName());
        handler.addObject(ParamKeys.PARAM_STATE, csContract.getState());
        handler.addObject(ParamKeys.PARAM_CONTRACT_OPTION, csContract.getContractOption());
        handler.addObject(ParamKeys.PARAM_CONTRACT_OPTION_OBJECT, ctrctVariant);
        handler.addObject(ParamKeys.PARAM_ANNIVERSARY, csContract.getAnniversary());
        handler.addObject(ParamKeys.PARAM_FIND_ACTIVE_CUSTS, csContract.getFindActiveCusts());
        handler.addObject(ParamKeys.PARAM_CUSTOMER_SEARCH_CRITERIA, new Integer(2));
        int startPos = 0;
        if (StringUtils.isNotBlank(csContract.getStartPos())){
            startPos = Integer.parseInt(csContract.getStartPos());
        }
        handler.addObject(ParamKeys.PARAM_START_POSITION, new Integer(startPos));
        handler.addObject(CustomerParamKeys.SEARCH_FOR, csContract.getSearchFor());

    }
    
    protected String getValidationForm() {
         return CustomerViewKeys.CUST_SRCH_ATTR_FORM;
     }

}
