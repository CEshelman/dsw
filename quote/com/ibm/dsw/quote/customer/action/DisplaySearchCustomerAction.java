package com.ibm.dsw.quote.customer.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.config.CustomerViewKeys;
import com.ibm.dsw.quote.customer.contract.CustomerSearchContract;
import com.ibm.dsw.quote.partner.process.PartnerProcess;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author: chenzhh@cn.ibm.com
 * Creation date: Jan 26, 2007
 */
public class DisplaySearchCustomerAction extends CustomerBaseAction {

    protected Object getEObject(ProcessContract contract) throws QuoteException {

        CustomerSearchContract csContract = (CustomerSearchContract) contract;
      String  pageFrom=csContract.getPageFrom();
        List countryList = null;

        if (CustomerConstants.LOB_FCT.equalsIgnoreCase(csContract.getLob())
                && CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(csContract.getSearchFor())) {
            PartnerProcess psp = PartnerProcessFactory.singleton().create();
            countryList = psp.findValidCountryList(csContract.getCountry(), csContract.getLob());
        }
        
        if (CustomerConstants.LOB_PAUN.equalsIgnoreCase(csContract.getLob()) 
        		|| QuoteConstants.LOB_SSP.equalsIgnoreCase(csContract.getLob())) {
            PartnerProcess psp = PartnerProcessFactory.singleton().create();
            countryList = psp.findValidCountryList(csContract.getCountry(), csContract.getLob());
        }

        return countryList;
    }
    
    protected String getState(ProcessContract contract) {
        return CustomerStateKeys.STATE_DISPLAY_SEARCH_CUSTOMER;
    }
    
    
    protected void validateParams(ProcessContract contract)throws QuoteException{
    	super.validate(contract);
    	CustomerSearchContract custSearchContract = (CustomerSearchContract)contract;
    	
    	if(StringUtils.isNotBlank(custSearchContract.getQuoteNum())){
    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_QUOTE_NUM,custSearchContract.getQuoteNum(), "Numeric", 10, true)){
    			throw new QuoteException("Invalid quoteNum value.");
    		}
    	}
    	if(StringUtils.isNotBlank(custSearchContract.getLob())){
    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_LINE_OF_BUSINESS,custSearchContract.getLob(), "Alpha", 5, true)){
				throw new QuoteException("Invalid lob value.");
			}
    	}
    	if(StringUtils.isNotBlank(custSearchContract.getCountry())){
    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_COUNTRY,custSearchContract.getCountry(), "Alpha", 5, true)){
				throw new QuoteException("Invalid country value.");
			}
    	}
    	if(StringUtils.isNotBlank(custSearchContract.getSearchFor())){
    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_SEARCH_FOR,custSearchContract.getSearchFor(), "Alpha", 20, true)){
				throw new QuoteException("Invalid searchFor value.");
			}
    	}
    	if(StringUtils.isNotBlank(custSearchContract.getContractOption())){
    		if(!"%".equals(custSearchContract.getContractOption().trim()) && !SecurityUtil.isValidInput(ParamKeys.PARAM_CONTRACT_OPTION,custSearchContract.getContractOption(), "AlphaNumeric", 10, true)){
    				throw new QuoteException("Invalid contractOption value.");
    		}
    	}
    	if(StringUtils.isNotBlank(custSearchContract.getAnniversary())){
    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_ANNIVERSARY,custSearchContract.getAnniversary(), "Numeric", 2, true)){
				throw new QuoteException("Invalid anniversary value.");
			}
    	}
    	if(StringUtils.isNotBlank(custSearchContract.getFindActiveCusts())){
    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_FIND_ACTIVE_CUSTS,custSearchContract.getFindActiveCusts(), "Numeric", 1, true)){
				throw new QuoteException("Invalid findActiveCusts value.");
			}
    	}
    	
	}
    
    protected void  addContractToResult(ProcessContract contract, ResultHandler  handler) {
        super.addContractToResult(contract, handler);
        
        CustomerSearchContract custSearchContract = (CustomerSearchContract)contract;
        handler.addObject(ParamKeys.PARAM_SITE_NUM, custSearchContract.getSiteNumber());
        handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, custSearchContract.getAgreementNumber());
        handler.addObject(ParamKeys.PARAM_FIND_ALL_CNTRY_CUSTS, custSearchContract.getFindAllCntryCusts());
        handler.addObject(ParamKeys.PARAM_CUST_NAME, custSearchContract.getCustomerName());
        handler.addObject(ParamKeys.PARAM_STATE, custSearchContract.getState());
        handler.addObject(ParamKeys.PARAM_FIND_ACTIVE_CUSTS, custSearchContract.getFindActiveCusts());
        handler.addObject(ParamKeys.PARAM_CONTRACT_OPTION, custSearchContract.getContractOption());
        handler.addObject(ParamKeys.PARAM_ANNIVERSARY, custSearchContract.getAnniversary());
        handler.addObject(CustomerParamKeys.SEARCH_FOR, custSearchContract.getSearchFor());
        
    }
    
    protected String[] getValidationForms(ProcessContract contract) {

        return new String[] { CustomerViewKeys.CUST_BASE_FORM, CustomerViewKeys.DISP_CUST_SRCH_FORM };
    }
   
}
