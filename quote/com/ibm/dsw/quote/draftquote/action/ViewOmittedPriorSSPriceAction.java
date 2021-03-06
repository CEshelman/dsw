package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.PrepareAddLPPFormContract;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

public class ViewOmittedPriorSSPriceAction extends BaseContractActionHandler {

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		PrepareAddLPPFormContract prepareAddLPPFormContract = (PrepareAddLPPFormContract) contract;
		PriorYearPriceProcess process;
		try {
			process = PriorYearPriceProcessFactory.singleton().create();
		} catch (QuoteException e) {
			logContext.fatal(this, e.getMessage());
			throw new QuoteException(e);
		}
		PriorSSPriceContract priorSSPriceContract = process.getOmittedPriorSSPrice(prepareAddLPPFormContract.getQuoteNumber(),prepareAddLPPFormContract.getLineSeqNum(),prepareAddLPPFormContract.getRenewalNum());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM,prepareAddLPPFormContract.getQuoteNumber());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP,prepareAddLPPFormContract.getLpp());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE,prepareAddLPPFormContract.getQuoteCurrencyCode());
		handler.addObject(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE,prepareAddLPPFormContract.getCurrencyCode());
		handler.addObject(DraftQuoteParamKeys.LINE_SEQ_NUM,prepareAddLPPFormContract.getLineSeqNum());
		handler.addObject(DraftQuoteParamKeys.PARAM_RENEWAL_NUM, prepareAddLPPFormContract.getRenewalNum());
		handler.addObject(DraftQuoteParamKeys.PARAM_SYSTEM_PRIOR_PRICE, prepareAddLPPFormContract.getSystemPriorPrice());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT,priorSSPriceContract);
		logContext.debug(this, "process the priorSSPrice");
		handler.setState(DraftQuoteStateKeys.STATE_REDIRECT_TO_VIEW_UPDATE_OMITTED_LPP_FORM);
		return handler.getResultBean();
	}

}
