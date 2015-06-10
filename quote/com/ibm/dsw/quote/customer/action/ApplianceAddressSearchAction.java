package com.ibm.dsw.quote.customer.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.config.CustomerViewKeys;
import com.ibm.dsw.quote.customer.contract.ApplianceAddressSearchContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ApplianceAddressSearchAction extends CustomerBaseAction {

	private static final long serialVersionUID = 7114492660554554198L;
	

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) 
    	throws QuoteException, ResultBeanException 
    {
        ApplianceAddressSearchContract c = (ApplianceAddressSearchContract) contract;
        
        String country = c.getCountry();
        String lob = c.getLob();
        String agreementNumber = c.getAgreementNumber();
        String isSubmittedQuote = c.getIsSubmittedQuote();
        
        if (StringUtils.isBlank(country) || StringUtils.isBlank(lob)) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }
        
        handler.setState(getState(c) );
        handler.addObject(ParamKeys.PARAM_ADDRESS_TYPE, c.getAddressType() );
        handler.addObject(ParamKeys.PARAM_IS_SBMT_QT, isSubmittedQuote);
        if(Boolean.valueOf(isSubmittedQuote))
        	handler.setState(CustomerStateKeys.STATE_DISPLAY_SUBMITTEDQT_APPLIANCE_ADDRESS_RESULTS);
        addContractToResult(contract, handler);
        
        CustomerSearchResultList resultList = (CustomerSearchResultList) getEObject(contract);
        if (resultList != null && resultList.getResultCount() > 0) {
        	handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, agreementNumber);
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, resultList);
            return handler.getResultBean();
        } else {
            handler.setState(CustomerStateKeys.STATE_DISPLAY_SELECT_APPLIANCE_ADDRESS);
            if(Boolean.valueOf(isSubmittedQuote))
            	handler.setState(CustomerStateKeys.STATE_DISPLAY_SUBMITTEDQT_SELECT_APPLIANCE_ADDRESS);
            return handleNoDataMessage(handler, c.getLocale() );
        }
    }

    protected Object getEObject(ProcessContract contract) throws QuoteException {
    	ApplianceAddressSearchContract c = (ApplianceAddressSearchContract) contract;
        
        int shareAgreement = searchByAgreement(c) ? 1 : 0;
        int startPos = getStartPos(c);
        
        // get the result list from db
        CustomerProcess process = getProcess();		
		CustomerSearchResultList result = process.searchAddressByAttr(
				c.getQuoteNum(), c.getCustomerName(), c.getState(), 
				c.getSiteNumber(), shareAgreement, startPos, c.getUserId());
		result.setLob(c.getLob() );
		
		return result;
    }

	private CustomerProcess getProcess() throws QuoteException {
       	try { return CustomerProcessFactory.singleton().create();
		} catch (TopazException e) {
	        LogContextFactory.singleton().getLogContext().error(this, e.getMessage() );
	        throw new QuoteException("Topaz Exception: Unable to create CustomerProcessFactory", e);
		}
	}

    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
    	ApplianceAddressSearchContract c = (ApplianceAddressSearchContract) contract;
        super.addContractToResult(c, handler);
        
        if (c.getCountry() != null) {
            Country cntryObj = this.getCntryObject(c.getCountry() );
            handler.addObject(ParamKeys.PARAM_COUNTRY_OBJECT, cntryObj);
        }
        handler.addObject(ParamKeys.PARAM_CUST_NAME, c.getCustomerName() );
        handler.addObject(ParamKeys.PARAM_SITE_NUM, c.getSiteNumber());
        handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, c.getAgreementNum());
        handler.addObject(ParamKeys.PARAM_STATE, c.getState() );
        handler.addObject(ParamKeys.PARAM_START_POSITION, getStartPos(c) );
        handler.addObject(ParamKeys.PARAM_SEARCH_FOR, c.getSearchFor() );
    }
    
    protected int getStartPos(ApplianceAddressSearchContract c) {
    	String startPosStr = c.getStartPos();
    	if (StringUtils.isBlank(startPosStr) ) return 0;
    	return Integer.parseInt(startPosStr);
    }

	protected String getState(ProcessContract contract) {
		return CustomerStateKeys.STATE_DISPLAY_APPLIANCE_ADDRESS_RESULTS;
	}
	
    protected boolean validate(ProcessContract contract) {
    	return super.validate(contract);
    }

    protected String[] getValidationForms(ProcessContract contract) {
    	ApplianceAddressSearchContract c = (ApplianceAddressSearchContract) contract;
    	String form;
    	if (searchByAttr(c) ) { form = CustomerViewKeys.CUST_SRCH_ATTR_FORM;
    	} else if (searchBySiteNum(c) ) { form = CustomerViewKeys.CUST_SRCH_DSWID_FORM;
    	} else form = CustomerViewKeys.CUST_SRCH_AGREEMENT_FORM;
    	
    	return new String[] { form };
    }
    
    public boolean searchByAttr(ApplianceAddressSearchContract c) {
    	return CustomerConstants.SEARCH_BY_ATTR.equalsIgnoreCase(c.getSearchFor() );
    }

    public boolean searchBySiteNum(ApplianceAddressSearchContract c) {
    	return CustomerConstants.SEARCH_BY_SITE_NUM.equalsIgnoreCase(c.getSearchFor() );
    }
    
    public boolean searchByAgreement(ApplianceAddressSearchContract c) {
    	return CustomerConstants.SEARCH_BY_AGREEMENT.equalsIgnoreCase(c.getSearchFor() );
    }    


}
