/*
 * Created on Mar 21, 2007
 */
package com.ibm.dsw.quote.customer.action;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.contract.CustomerCreateContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Lavanya
 */
public class InsertCustAfterDupAction extends CustomerBaseAction{
    
    protected Object getEObject(ProcessContract contract) throws QuoteException {
        return null;
    }
    
    protected String getState(ProcessContract contract) {
        return StateKeys.STATE_REDIRECT_ACTION;
    }
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, 
    	ResultBeanException {
        
        super.executeBiz(contract, handler);
        if (handler.getResultBean().getState().getStateAsString().equals(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE)){
            return handler.getResultBean();
        }
                
        CustomerCreateContract custCreateContract =(CustomerCreateContract)contract;
        String creatorId = custCreateContract.getUserId();
        String currencyCode = custCreateContract.getCurrency();
        String agrmntNum = custCreateContract.getAgreementNumber();
        
        setContractValues(custCreateContract);
        
        CustomerProcess custProcess = null;
        
        try {
            custProcess = CustomerProcessFactory.singleton().create();
            //call insertCustomer
            Customer customer = custProcess.createCustomer(custCreateContract);
            int webCustId = customer.getWebCustId();
            
            //update
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            process.updateQuoteHeaderCustInfo(creatorId, null, "", agrmntNum,webCustId, currencyCode,custCreateContract.getEndUserFlag());
            
            handler.setState(StateKeys.STATE_REDIRECT_ACTION);
            handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
            
        } catch (TopazException e) {
            throw new QuoteException("error executing topaz process", e);
        } 
        
    	addContractToResult(contract, handler);
    	return handler.getResultBean();
    }
    
    
    private void setContractValues(CustomerCreateContract custCreateContract) {
        
        custCreateContract.setCustomerNumber("");
        custCreateContract.setSapContractNum("");
        custCreateContract.setTempAccessNum("");
        custCreateContract.setSapContactId("0");
        if (CustomerConstants.LOB_PAE.equals(custCreateContract.getLob()))
            custCreateContract.setWebCustTypeCode(CustomerConstants.PAX);
        else
            custCreateContract.setWebCustTypeCode(custCreateContract.getLob());
        custCreateContract.setSapIntlPhoneNum(custCreateContract.getCntPhoneNumFull());
        custCreateContract.setMktgEmailFlag("1");
        custCreateContract.setWebCustStatCode(CustomerConstants.DRAFT);
        if(CustomerConstants.LOB_SSP.equals(custCreateContract.getLob())){
        	custCreateContract.setSapCntPrtnrFuncCode(CustomerConstants.PRTNR_FUNC_CODE_ZG);
        }else{
        	custCreateContract.setSapCntPrtnrFuncCode(CustomerConstants.PRTNR_FUNC_CODE_ZW);
        }
    
    }
   

}
