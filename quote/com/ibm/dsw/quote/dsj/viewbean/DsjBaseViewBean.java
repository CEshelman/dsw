package com.ibm.dsw.quote.dsj.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class DsjBaseViewBean extends BaseViewBean {
	private DsjBaseContract dbc = null;
	
	

	public DsjBaseContract getDsjBaseContract() {
		return dbc;
	}



	public void setDsjBaseContract(DsjBaseContract dbc) {
		this.dbc = dbc;
	}



	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		this.setDsjBaseContract((DsjBaseContract)params.getParameter(DsjKeys.dsjBaseContract));
	}

}
