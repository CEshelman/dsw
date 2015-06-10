package com.ibm.dsw.quote.customer.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.EndUserSearchContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>EndUserSearchDSWIDAction<code> class.
 *    
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: Dec. 15, 2009
 */

public class EndUserSearchDSWIDAction extends CustomerBaseAction {
    
    static final int CUST_NUM_MAX_LENGTH = 10;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.CustomerBaseAction#getEObject(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected Object getEObject(ProcessContract contract) throws QuoteException {

        EndUserSearchContract endUserSearchContract = (EndUserSearchContract) contract;
        String country = endUserSearchContract.getCountry();
        if(CustomerConstants.CHECKBOX_CHECKED.equals(endUserSearchContract.getFindAllCntryCusts())){
	        	country = "%";
	    }
        int startPos = 0;
        
        if (StringUtils.isNotBlank(endUserSearchContract.getStartPos()))
            startPos = Integer.parseInt(endUserSearchContract.getStartPos());

        CustomerProcess process = null;
        CustomerSearchResultList resultList = null;
        try {
            process = CustomerProcessFactory.singleton().create();
            resultList = process.searchEndCustByDswID(endUserSearchContract.getSiteNumber(), endUserSearchContract.getLob(),country, startPos);
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

        Object eObject = getEObject(endUserSearchContract);
        CustomerSearchResultList resultList = (CustomerSearchResultList) eObject;

        if (resultList != null && resultList.getResultCount() > 0) {
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
            addContractToResult(contract, handler);
            return handler.getResultBean();
        }
        else {
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
        super.addContractToResult(contract, handler);

        EndUserSearchContract csContract = (EndUserSearchContract) contract;
        String cntryCode = csContract.getCountry();

        if (cntryCode != null) {
            Country cntryObj = this.getCntryObject(cntryCode);
            handler.addObject(ParamKeys.PARAM_COUNTRY_OBJECT, cntryObj);
        }
        
        handler.addObject(ParamKeys.PARAM_SITE_NUM, csContract.getSiteNumber());
        handler.addObject(ParamKeys.PARAM_CUSTOMER_SEARCH_CRITERIA, new Integer(0));
        int startPos = 0;
        if (StringUtils.isNotBlank(csContract.getStartPos()))
            startPos = Integer.parseInt(csContract.getStartPos());
        handler.addObject(ParamKeys.PARAM_START_POSITION, new Integer(startPos));
    }

    protected boolean validate(ProcessContract contract) {
        
        boolean isValid = super.validate(contract);
        if (!isValid)
            return isValid;
        
        HashMap vMap = new HashMap();
        EndUserSearchContract csContract = (EndUserSearchContract) contract;
        if (csContract == null)
            return false;

        
        String siteNum = csContract.getSiteNumber();
        if(siteNum!=null && !"".equals(siteNum)){
        	siteNum = siteNum.trim();
        }

        if (StringUtils.isBlank(siteNum)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.SITE_NUM);
            vMap.put(ParamKeys.PARAM_SITE_NUM, field);
            isValid = false;
        } else if (siteNum.length() > CUST_NUM_MAX_LENGTH) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_MAXLENGTH);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.SITE_NUM);
            vMap.put(ParamKeys.PARAM_SITE_NUM, field);
            field.addArg(String.valueOf(CUST_NUM_MAX_LENGTH), false );
            isValid = false;    
        }   
        addToValidationDataMap(csContract, vMap);
        return isValid;
    }

}
