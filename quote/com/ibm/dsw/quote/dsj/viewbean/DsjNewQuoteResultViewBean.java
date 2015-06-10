package com.ibm.dsw.quote.dsj.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.draftquote.util.StringUtils;
import com.ibm.dsw.quote.dsj.action.DsjNewSalesQuoteAction;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class DsjNewQuoteResultViewBean extends BaseViewBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5786551474254310318L;
	protected String returnCode = "";
    protected transient Quote quote;

	@SuppressWarnings("unchecked")

	public void collectResults(Parameters parameters) throws ViewBeanException {
        super.collectResults(parameters);
        returnCode = parameters.getParameterAsString(NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE);
        quote = (Quote)parameters.getParameter(DsjNewSalesQuoteAction.PARAM_NEW_DSJ_QUOTE);
    }

	public String getReturnCode() {
		return returnCode;
	}


	public String getNewDsjQuoteResultJson(){
		StringBuilder itBuf = new StringBuilder("\"quoteHeader\":{\"");
					itBuf.append("webQuoteNumber\":\"").append(StringUtils.jsonStringEncoding(quote.getQuoteHeader().getWebQuoteNum())).append("\"").append(",")
					    .append("\"").append("webQuoteLob\":\"").append(StringUtils.jsonStringEncoding(quote.getQuoteHeader().getLob().getCode())).append("\"")
						.append("}");

		return itBuf.toString();
	}
}
