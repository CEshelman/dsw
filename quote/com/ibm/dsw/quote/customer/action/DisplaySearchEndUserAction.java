package com.ibm.dsw.quote.customer.action;

import java.util.List;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.EndUserSearchContract;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: Dec. 15, 2009
 */
public class DisplaySearchEndUserAction extends CustomerBaseAction {

    protected String getState(ProcessContract contract) {
        return CustomerStateKeys.STATE_DISPLAY_SEARCH_END_USER;
    }
        
    protected void  addContractToResult(ProcessContract contract, ResultHandler  handler) {
        super.addContractToResult(contract, handler);
        
        EndUserSearchContract custSearchContract = (EndUserSearchContract)contract;
        handler.addObject(ParamKeys.PARAM_SITE_NUM, custSearchContract.getSiteNumber());
        handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, custSearchContract.getAgreementNumber());
        handler.addObject(ParamKeys.PARAM_CUST_NAME, custSearchContract.getCustomerName());
        handler.addObject(ParamKeys.PARAM_STATE, custSearchContract.getState()); 
    }
    
    protected Object getEObject(ProcessContract contract) throws QuoteException {
    	EndUserSearchContract endUserSearchContract = (EndUserSearchContract)contract;
    	
        List countryList = null;
        
        if (QuoteConstants.LOB_SSP.equalsIgnoreCase(endUserSearchContract.getLob())) {
            countryList = CacheProcessFactory.singleton().create().getCountryList();
        }
    	
        return countryList;
    }
   
}
