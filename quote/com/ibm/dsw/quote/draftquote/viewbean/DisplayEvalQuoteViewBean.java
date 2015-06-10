package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.List;

import com.ibm.dsw.quote.common.domain.EvalQuote;
import com.ibm.dsw.quote.home.viewbean.QuoteRightColumnViewBean;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class DisplayEvalQuoteViewBean extends QuoteRightColumnViewBean {

	private static final long serialVersionUID = 1L;
	transient List<EvalQuote> evalQuoteList = null;
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void collectResults(Parameters params) throws ViewBeanException {
		// TODO Auto-generated method stub
		super.collectResults(params);
		this.setEvalQuoteList((List<EvalQuote>)params.getParameter(NewQuoteParamKeys.PARAM_EVAL_QUOTE_LIST));
	}


	public List<EvalQuote> getEvalQuoteList() {
		return evalQuoteList;
	}


	public void setEvalQuoteList(List<EvalQuote> evalQuoteList) {
		this.evalQuoteList = evalQuoteList;
	}
}
