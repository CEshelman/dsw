package com.ibm.dsw.quote.submittedquote.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPriorSSPriceContract;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author J Zhang
 */
public class DisplayPriorSSPriceViewBean extends BaseViewBean {
	private String quoteNumber;
	private String lineItemSeqNumber;
	private String currencyCode;
	private String quoteCurrencyCode;
	private String lpp;
	private SubmittedPriorSSPriceContract contract;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public SubmittedPriorSSPriceContract getContract() {
		return contract;
	}

	public void setContract(SubmittedPriorSSPriceContract contract) {
		this.contract = contract;
	}

	public String getLpp() {
		return lpp;
	}

	public void setLpp(String lpp) {
		this.lpp = lpp;
	}

	public String getQuoteCurrencyCode() {
		return quoteCurrencyCode;
	}

	public void setQuoteCurrencyCode(String quoteCurrencyCode) {
		this.quoteCurrencyCode = quoteCurrencyCode;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public String getLineItemSeqNumber() {
		return lineItemSeqNumber;
	}

	public void setLineItemSeqNumber(String lineItemSeqNumber) {
		this.lineItemSeqNumber = lineItemSeqNumber;
	}

	public void collectResults(Parameters params) throws ViewBeanException {
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM) != null) {
			this.setQuoteNumber(params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM));
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.LINE_SEQ_NUM) != null) {
			this.setLineItemSeqNumber(params.getParameterAsString(DraftQuoteParamKeys.LINE_SEQ_NUM));
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE) != null) {
			this.setCurrencyCode(params.getParameterAsString(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE));
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE) != null) {
			this.setQuoteCurrencyCode(params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE));
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP) != null) {
			this.setLpp(params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP));
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP) != null) {
			this.setLpp(params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP));
		}
		if (params.getParameter(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT) != null) {
			this.setContract((SubmittedPriorSSPriceContract) params .getParameter(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT));
		}
	}
	
	public boolean isNoSystemComputedLPP(){
		if(contract == null){
			return false;
		}
		return DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP.equalsIgnoreCase(contract.getType());
	}
}
