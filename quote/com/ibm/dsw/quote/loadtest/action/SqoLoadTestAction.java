package com.ibm.dsw.quote.loadtest.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.loadtest.process.SqoLoadTestProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author julia.liu
 *
 */
public class SqoLoadTestAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 1L;

	@Override
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
    ResultBeanException {
		
		try {
			String result; 
			
			StringBuffer sb = SqoLoadTestProcessFactory.singleton().create().loadtest();
			if (sb ==null ) {
				result = "failed";
				handler.addObject("SqoLoadTest", "Call Load test function failed!");
			}else {
				result = sb.toString(); 
				handler.addObject("SqoLoadTest", "Call Load test function successful! "+result);
			}
			logContext.info(this, "SqoLoadTest response: " + result);
		} catch (TopazException e) {
			handler.addObject("SqoLoadTest", "Call Load test function failed! "+e.toString());		
			logContext.error(SqoLoadTestAction.class,
					LogThrowableUtil.getStackTraceContent(e)); 
		}
		handler.setState("STATE_SQOLOADTEST");
		
		return handler.getResultBean();
	}

}
