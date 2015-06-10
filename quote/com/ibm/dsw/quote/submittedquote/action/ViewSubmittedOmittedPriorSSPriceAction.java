package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPriorSSPriceContract;
import com.ibm.dsw.quote.submittedquote.process.PriorYearPriceProcess;
import com.ibm.dsw.quote.submittedquote.process.PriorYearPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
/**
 * 
 * @author GuoTao Li
 *
 */
public class ViewSubmittedOmittedPriorSSPriceAction extends BaseContractActionHandler {
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
		throws QuoteException, ResultBeanException {
		SubmittedPriorSSPriceContract prepareSSPriceContract = (SubmittedPriorSSPriceContract)contract;
		PriorYearPriceProcess process;
		
		try {
			process = PriorYearPriceProcessFactory.singleton().create();
		} catch (QuoteException e) {
			logContext.fatal(this, e.getMessage());
			throw new QuoteException(e);
		}
		// Here we re-use parameter names defined in Draft quote
		SubmittedPriorSSPriceContract priorSSPriceContract = process.getSubmittedOmittedPriorSSPrice(prepareSSPriceContract.getQuoteNumber(),prepareSSPriceContract.getLineSeqNum(),prepareSSPriceContract.getRenewalNum());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM,prepareSSPriceContract.getQuoteNumber());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP,prepareSSPriceContract.getLpp());
		handler.addObject(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE,prepareSSPriceContract.getCurrencyCode());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE,prepareSSPriceContract.getQuoteCurrencyCode());
		handler.addObject(DraftQuoteParamKeys.LINE_SEQ_NUM,prepareSSPriceContract.getLineSeqNum());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT,priorSSPriceContract);
		logContext.debug(this, "process the priorSSPrice");
		handler.setState(SubmittedQuoteStateKeys.STATE_DISPLAY_OMITTED_PRIOR_SS_PRICE);
		
		return handler.getResultBean();
	}
}