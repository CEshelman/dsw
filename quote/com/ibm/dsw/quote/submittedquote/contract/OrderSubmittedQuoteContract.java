/**
 * 
 */
package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author lirui
 *
 */
public class OrderSubmittedQuoteContract extends SubmittedQuoteBaseContract {
	private static final long serialVersionUID = 1L;
	
	//Parameter for Eorder system: P0
	private String P0;
	//Parameter for Eorder system: isOrderNow
	private String isOrderNow;
	//Parameter for Eorder system: quote (sap quote number)
	private String sapQuoteNum;
	
	//if there is saas item in this quote
	private boolean hasSaas;
	
	private String hasSaasStr;
	
	
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        P0 = parameters.getParameterAsString(SubmittedQuoteParamKeys.PARAM_PO);
        isOrderNow = parameters.getParameterAsString(SubmittedQuoteParamKeys.PARAM_ISORDERNOW);
        sapQuoteNum = parameters.getParameterAsString(SubmittedQuoteParamKeys.PARAM_SAP_QUOTE_NUM);
        hasSaas = parameters.getParameterAsBoolean(SubmittedQuoteParamKeys.PARAM_HASSAAS);
        hasSaasStr = parameters.getParameterAsString(SubmittedQuoteParamKeys.PARAM_HASSAAS);
    }

	public String getP0() {
		return P0;
	}

	public void setP0(String p0) {
		P0 = p0;
	}

	public String getIsOrderNow() {
		return isOrderNow;
	}

	public void setIsOrderNow(String isOrderNow) {
		this.isOrderNow = isOrderNow;
	}

	public String getSapQuoteNum() {
		return sapQuoteNum;
	}

	public void setSapQuoteNum(String sapQuoteNum) {
		this.sapQuoteNum = sapQuoteNum;
	}

	public boolean isHasSaas() {
		return hasSaas;
	}

	public void setHasSaas(boolean hasSaas) {
		this.hasSaas = hasSaas;
	}

	public String getHasSaasStr() {
		return hasSaasStr;
	}

	public void setHasSaasStr(String hasSaasStr) {
		this.hasSaasStr = hasSaasStr;
	}
	
}
