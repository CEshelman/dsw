package com.ibm.dsw.quote.loadtest.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author julia.liu
 *
 */
public class SqoLoadTestContract extends QuoteBaseContract {

	private static final long serialVersionUID = 5257631721122521549L;
	
	private String webQuoteNum;
	private String pid;
	
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	
	public String getPid() {
		return pid;
	}
	
	public void load(Parameters parameters, JadeSession jadeSession) {
		super.load(parameters,jadeSession);
		
		this.webQuoteNum = parameters.getParameterAsString("webQuoteNum");
		this.pid = parameters.getParameterAsString("pid");

	}
}
