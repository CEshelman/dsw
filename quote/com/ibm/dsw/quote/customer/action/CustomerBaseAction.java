/*
 * Created on Feb 22, 2007
 */
package com.ibm.dsw.quote.customer.action;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.config.CustomerViewKeys;
import com.ibm.dsw.quote.customer.contract.CustomerBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Lavanya
 */
public abstract class CustomerBaseAction extends BaseContractActionHandler {

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

    	// validate params -- fix appscan issue
    	validateParams(contract);

        CustomerBaseContract customerBaseContract = (CustomerBaseContract) contract;
       String pageFrom=customerBaseContract.getPageFrom();
        String country = customerBaseContract.getCountry();
        String lineOfBusiness = customerBaseContract.getLob();
        if (StringUtils.isBlank(country) || StringUtils.isBlank(lineOfBusiness)) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        } else
            handler.setState(getState(contract));

        Object eObject = getEObject(contract);
        if (eObject != null) {
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
            
        }
        
        if(pageFrom.equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
        	 handler.addObject(DraftQuoteParamKeys.PAGE_FROM, pageFrom);
        	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, customerBaseContract.getMigrationReqNum());
        }
        
        addContractToResult(contract, handler);
        return handler.getResultBean();
    }

    protected abstract Object getEObject(ProcessContract contract) throws QuoteException;

    protected abstract String getState(ProcessContract contract);
    
    protected String getValidationForm() {

        return CustomerViewKeys.CUST_BASE_FORM;
    }

    protected void validateParams(ProcessContract contract)throws QuoteException{
    	// You can add the validate param here or you can add it in the child
        // class.
	}
    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        // You can add the contract object here or you can add it in the child
        // class.

        CustomerBaseContract customerBaseContract = (CustomerBaseContract) contract;
        handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, customerBaseContract.getLob());
        handler.addObject(ParamKeys.PARAM_COUNTRY, customerBaseContract.getCountry());
        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, customerBaseContract.getQuoteNum());
        handler.addObject(DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE, customerBaseContract.getProgMigrtnCode());
        handler.addObject(ParamKeys.PARAM_SITE_NUM, customerBaseContract.getSiteNumber());
        handler.addObject(ParamKeys.PARAM_WEBCUST_ID, customerBaseContract.getWebCustId());
        handler.addObject(ParamKeys.PARAM_COUNTRY_CODE2, customerBaseContract.getCountryCode2());
    }

    public Country getCntryObject(String cntryCode3) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CacheProcess cProcess = null;
        Country cntryObj = null;
        try {
            cProcess = CacheProcessFactory.singleton().create();
            cntryObj = cProcess.getCountryByCode3(cntryCode3);
            return cntryObj;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            return null;
        }
    }

    public ResultBean handleNoDataMessage(ResultHandler handler, Locale locale) throws ResultBeanException {
        ResultBean resultBean = handler.getResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String sNoDataMsg = this.getI18NString(CustomerMessageKeys.NO_CUSTOMER_DATA_HINT,
                MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        messageBean.addMessage(sNoDataMsg, MessageBeanKeys.INFO);
        resultBean.setMessageBean(messageBean);

        return resultBean;
    }
    
    
    /**
     * To get the quote head and put into the action.
     * @param creatorId (This is current user id.)
     * @return
     * @throws QuoteException
     */
    public QuoteHeader getQuoteHdrInfo(String creatorId) throws QuoteException{
    	QuoteProcess process = QuoteProcessFactory.singleton().create();
        QuoteHeader qtHeader = null;
        try {
            qtHeader = process.getQuoteHdrInfo(creatorId);
        } catch (NoDataException nde) {
            throw new QuoteException("Quote header is not found for the login user " + creatorId);
        }
        return qtHeader;
    }
        
      
    
    
    
}
