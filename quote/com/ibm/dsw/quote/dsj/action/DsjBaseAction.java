package com.ibm.dsw.quote.dsj.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.jade.session.JadeSession;

public class DsjBaseAction extends BaseContractActionHandler {
	

    protected void preExecute(ProcessContract contract, JadeSession session, ResultHandler handler) {
    	DsjBaseContract dbc = (DsjBaseContract)contract;
    	handler.addObject(DsjKeys.dsjBaseContract, dbc);
    	
    }

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		// TODO Auto-generated method stub
		return this.executeBiz((DsjBaseContract)contract, handler);
	}

	public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		// TODO Auto-generated method stub
		return null;
	}

}
