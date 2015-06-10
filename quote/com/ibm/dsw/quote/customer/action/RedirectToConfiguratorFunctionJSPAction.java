package com.ibm.dsw.quote.customer.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

public class RedirectToConfiguratorFunctionJSPAction extends BaseContractActionHandler{
	
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		
        handler.setState(StateKeys.STATE_REDIRECT_TO_CONF_FUNC_JSP);
		return handler.getResultBean();
	}
	    


}
