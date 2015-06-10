package com.ibm.dsw.quote.customer.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class CreateNewQuoteRedirectBaseViewBean extends BaseViewBean{
	private String createResult = null;
	
	public void collectResults(Parameters params) throws ViewBeanException {
		if(params.getParameterAsString(CustomerParamKeys.PARAM_RESULT_CREATE_QUOTE_AJAX)!=null){
        	this.setCreateResult(params.getParameterAsString(CustomerParamKeys.PARAM_RESULT_CREATE_QUOTE_AJAX));
    	}
	}

	public String getCreateResult() {
		return createResult;
	}

	public void setCreateResult(String createResult) {
		this.createResult = createResult;
	}
}
