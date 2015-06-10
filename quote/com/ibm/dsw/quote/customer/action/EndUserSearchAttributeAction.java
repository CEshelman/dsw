package com.ibm.dsw.quote.customer.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.config.CustomerViewKeys;
import com.ibm.dsw.quote.customer.contract.EndUserSearchContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
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
 * This <code>EndUserSearchAttributeAction<code> class.
 *    
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: Dec. 15, 2009
 */

public class EndUserSearchAttributeAction extends CustomerBaseAction {
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.CustomerBaseAction#getEObject(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected Object getEObject(ProcessContract contract) throws QuoteException {
        //get input search criteria from contract
        EndUserSearchContract csContract = (EndUserSearchContract) contract;
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        int startPos = 0;
        if (StringUtils.isNotBlank(csContract.getStartPos())){
            startPos = Integer.parseInt(csContract.getStartPos());
        }
        //get the result list from db
        CustomerProcess process = null;
        CustomerSearchResultList resultList = null;
        try {
            process = CustomerProcessFactory.singleton().create();
            
            /**
             * Add customer country for SSP quote
             */
            String country = csContract.getCountry();
            if(QuoteConstants.LOB_SSP.equalsIgnoreCase(csContract.getLob())){
            	country = csContract.getCusCountry();
            	if("".equals(country)){
            		country = "%";
            	}
            }
            
            resultList = process.searchEndCustByAttr(csContract.getCustomerName(), country, 
                    csContract.getLob(), csContract.getState(), startPos);
            
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing topaz process", e);
        }
        return resultList;
    }

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        EndUserSearchContract endUserSearchContract = (EndUserSearchContract) contract;
        String cntry = endUserSearchContract.getCountry();
        String lob = endUserSearchContract.getLob();
        
        if (StringUtils.isBlank(cntry) || StringUtils.isBlank(lob)) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }
        else
            handler.setState(getState(contract));

        Object eObject = getEObject(contract);
        CustomerSearchResultList resultList = (CustomerSearchResultList) eObject;
        
        if (resultList != null && resultList.getResultCount() > 0) {
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
            handler.addObject(ParamKeys.CUSTOMER_COUNTRY, endUserSearchContract.getCusCountry());
            addContractToResult(contract, handler);
            return handler.getResultBean();
        } else {
            handler.setState(CustomerStateKeys.STATE_DISPLAY_SEARCH_END_USER);
            addContractToResult(contract, handler);
            
            if (QuoteConstants.LOB_SSP.equalsIgnoreCase(endUserSearchContract.getLob())) {
                List countryList = CacheProcessFactory.singleton().create().getCountryList();
                if (countryList != null)
                    handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, countryList);
            }

            return handleNoDataMessage(handler, endUserSearchContract.getLocale());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.CustomerBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return CustomerStateKeys.STATE_DISPLAY_END_USER_RESULTS;
    }

    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        
        EndUserSearchContract csContract = (EndUserSearchContract) contract;
        super.addContractToResult(csContract, handler);

        if (csContract.getCountry() != null) {
            Country cntryObj = this.getCntryObject(csContract.getCountry());
            handler.addObject(ParamKeys.PARAM_COUNTRY_OBJECT, cntryObj);
        }
        handler.addObject(ParamKeys.PARAM_CUST_NAME, csContract.getCustomerName());
        handler.addObject(ParamKeys.PARAM_STATE, csContract.getState());
        handler.addObject(ParamKeys.PARAM_CUSTOMER_SEARCH_CRITERIA, new Integer(2));
        int startPos = 0;
        if (StringUtils.isNotBlank(csContract.getStartPos())){
            startPos = Integer.parseInt(csContract.getStartPos());
        }
        handler.addObject(ParamKeys.PARAM_START_POSITION, new Integer(startPos));
    }
    
    protected String getValidationForm() {
         return CustomerViewKeys.CUST_SRCH_ATTR_FORM;
     }

}
