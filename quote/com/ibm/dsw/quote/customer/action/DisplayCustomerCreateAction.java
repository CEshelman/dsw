/*
 * Created on Mar 6, 2007
 */
package com.ibm.dsw.quote.customer.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.AgreementTypeConfigFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.CustomerBaseContract;
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
 * @author Lavanya
 */
public class DisplayCustomerCreateAction extends CustomerBaseAction {

    protected Object getEObject(ProcessContract contract) throws QuoteException {
    	CustomerBaseContract cusContract = (CustomerBaseContract)contract;
    	
        List countryList = null;
        
        if (QuoteConstants.LOB_SSP.equalsIgnoreCase(cusContract.getLob())) {
            countryList = CacheProcessFactory.singleton().create().getCountryList();
        }
    	
        return countryList;
    }

    protected String getState(ProcessContract contract) {
        return DraftQuoteStateKeys.STATE_DISPLAY_DRAFT_QUOTE;
    }

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        
        super.executeBiz(contract, handler);
        if (handler.getResultBean().getState().getStateAsString().equals(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE)){
            return handler.getResultBean();
        }
        CustomerBaseContract custBaseContract = (CustomerBaseContract) contract;
        String quoteNum = custBaseContract.getQuoteNum();
        String agrmntNum = custBaseContract.getAgreementNumber();
        Customer customer = null;
        List agrmntTypeList = null;
        List agrmntOptionList = null;
        
        if (StringUtils.isBlank(quoteNum)) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        } else {
            try {
                if (StringUtils.isBlank(custBaseContract.getSiteNumber())) {
	                CustomerProcess process = CustomerProcessFactory.singleton().create();
	                customer = process.getWebCustomerByQuoteNum(quoteNum, custBaseContract.getLob(), custBaseContract
                            .getSiteNumber(), agrmntNum, custBaseContract.getWebCustIdAsInt());
                }
            } catch (SPException sp) {
                logContext.error(this, "There is no new customer created for this quote.");
            } catch (TopazException e) {
                logContext.error(this, e.getMessage());
                throw new QuoteException("error executing topaz process", e);
            }
            handler.setState(CustomerStateKeys.STATE_DISPLAY_CREATE_CUSTOMER);
        }
        // 0 means getting PA agreement types
        agrmntTypeList = AgreementTypeConfigFactory.singleton().getAgrmntTypeList(0);
        
//        if ( StringUtils.equalsIgnoreCase(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO, custBaseContract.getQuoteUserSession().getAudienceCode()) ) {
        	agrmntOptionList = AgreementTypeConfigFactory.singleton().getAgrmntOptionList();
        	if(agrmntTypeList!=null && agrmntTypeList.size()>0){
        		for(Object obj : agrmntTypeList){
        			if(CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(((com.ibm.dsw.quote.appcache.domain.CodeDescObj)obj).getCode()))
        				agrmntOptionList.add(0, obj);
        		}
        	}
        	
//        }
        String agrmtTypeCode = null;
        if(customer!=null && customer.isAddiSiteCustomer()){
        	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        	try {
				Quote quote = quoteProcess.getDraftQuoteBaseInfoByQuoteNum(quoteNum);
				agrmtTypeCode = quote.getQuoteHeader().getAgrmtTypeCode();
			} catch (NoDataException e) {
				logContext.error(this, e.getMessage());
                throw new QuoteException("error executing get quote infor process", e);
			}
        }	
        	
        handler.addObject(CustomerParamKeys.PARAM_AGRMTTYPECODE, agrmtTypeCode);
        handler.addObject(CustomerParamKeys.PARAM_CUSTOMER, customer);
        handler.addObject(CustomerParamKeys.PARAM_AGRMNT_TYPES, agrmntTypeList);
        handler.addObject(CustomerParamKeys.PARAM_AGRMNT_OPTIONS, agrmntOptionList);
        handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, agrmntNum);
        handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, custBaseContract.getLob());
        if (QuoteConstants.LOB_SSP.equalsIgnoreCase(custBaseContract.getLob())) {
        	 handler.addObject(ParamKeys.CUSTOMER_COUNTRY,custBaseContract.getCusCountry());
        }
        handler.addObject(ParamKeys.PARAM_END_USER_FLAG_NAME, custBaseContract.getEndUserFlag());
        addContractToResult(contract, handler);
        return handler.getResultBean();
    }

}
