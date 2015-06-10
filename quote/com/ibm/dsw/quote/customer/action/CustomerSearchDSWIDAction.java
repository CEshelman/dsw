package com.ibm.dsw.quote.customer.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
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
 * This <code>CustomerSearchDSWIDAction<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Mar 1, 2007
 */

public class CustomerSearchDSWIDAction extends CustomerBaseAction {

    static final int CUST_NUM_MAX_LENGTH = 10;

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.base.action.CustomerBaseAction#getEObject(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected Object getEObject(ProcessContract contract) throws QuoteException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CustomerSearchContract csContract = (CustomerSearchContract) contract;

        int startPos = getStartPos(csContract);
        String country = getCountry(csContract);
        String searchLob = getSearchLob(csContract);
        String dswIcnRcdID = StringUtils.trimToEmpty(csContract.getSiteNumber() );
        String ctrctNum = StringUtils.trimToEmpty(csContract.getAgreementNumber() );
        
        boolean searchPayer = CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(csContract.getSearchFor())
                && QuoteConstants.LOB_FCT.equalsIgnoreCase(csContract.getLob());
        String progMigrtnCode = csContract.getProgMigrtnCode();

        CustomerProcess process = null;
        CustomerSearchResultList resultList = null;

        try {
            process = CustomerProcessFactory.singleton().create();
            resultList = process.searchCustomerByDswIcnRcdID(ctrctNum, dswIcnRcdID, country, searchLob, startPos,
                    searchPayer, progMigrtnCode,csContract.getQuoteUserSession().getAudienceCode(), csContract.getQuoteUserSession().getSiteNumber());
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing topaz process", e);
        }
        return resultList;
    }
    
    protected String getCountry(CustomerSearchContract c) {
    	boolean findAll = CustomerConstants.CHECKBOX_CHECKED.equals(c.getFindAllCntryCusts() );
    	return (findAll) ? "%" : c.getCountry();
    }
    
    protected int getStartPos(CustomerSearchContract c) {
    	String startPosStr = c.getStartPos();
    	if (StringUtils.isBlank(startPosStr) ) return 0;
    	return Integer.parseInt(startPosStr);
    }

    protected String getSearchLob(CustomerSearchContract csContract) {
        String lob = csContract.getLob();
        String searchLob = csContract.getLob();

        if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob)) {
            searchLob = QuoteConstants.LOB_PAUN;
        }

        return searchLob;
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

        Object eObject = getEObject(csContract);
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
        }
        else {
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
        super.addContractToResult(contract, handler);

        CustomerSearchContract csContract = (CustomerSearchContract) contract;
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
        handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, csContract.getAgreementNumber());
        handler.addObject(ParamKeys.PARAM_FIND_ALL_CNTRY_CUSTS, csContract.getFindAllCntryCusts());
        handler.addObject(CustomerParamKeys.SEARCH_FOR, csContract.getSearchFor());
    }

    protected boolean validate(ProcessContract contract) {

        boolean isValid = super.validate(contract);
        if (!isValid)
            return isValid;

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        HashMap vMap = new HashMap();

        CustomerSearchContract csContract = (CustomerSearchContract) contract;
        if (csContract == null)
            return false;

        String lob = csContract.getLob();

        String siteNum = csContract.getSiteNumber()!=null?csContract.getSiteNumber().trim():"";

        String agreementNum = csContract.getAgreementNumber()!=null?csContract.getAgreementNumber().trim():"";

        if ( lob.equalsIgnoreCase(CustomerConstants.LOB_PAUN) || lob.equalsIgnoreCase(CustomerConstants.LOB_PA) ||
                lob.equalsIgnoreCase(CustomerConstants.LOB_PAE) || lob.equalsIgnoreCase(CustomerConstants.LOB_FCT) ||
                lob.equalsIgnoreCase(CustomerConstants.LOB_OEM) || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)) {
            if (StringUtils.isBlank(siteNum) && StringUtils.isBlank(agreementNum)) {
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_ONE_OF_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.SITE_NUM);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.AGREEMENT_NUM);
                vMap.put(ParamKeys.PARAM_SITE_NUM, field);
                isValid = false;
            }
            if (!StringUtils.isBlank(siteNum) && siteNum.length() > CUST_NUM_MAX_LENGTH) {
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_MAXLENGTH);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.SITE_NUM);
                vMap.put(ParamKeys.PARAM_SITE_NUM, field);
                field.addArg(String.valueOf(CUST_NUM_MAX_LENGTH), false );
                isValid = false;
            }
            if (!StringUtils.isBlank(agreementNum) && agreementNum.length() > CUST_NUM_MAX_LENGTH) {
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_MAXLENGTH);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.AGREEMENT_NUM);
                vMap.put(ParamKeys.PARAM_AGREEMENT_NUM, field);
                field.addArg(String.valueOf(CUST_NUM_MAX_LENGTH), false );
                isValid = false;
            }
        } else {
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
        }
        addToValidationDataMap(csContract, vMap);
        return isValid;
    }

}
