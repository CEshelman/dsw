package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * @author guotao
 * 
 */
public class AddOmittedPriorSSPriceAction extends PriorSSPriceAction {

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		PriorSSPriceContract priorSSPriceContract = (PriorSSPriceContract) contract;
		if (!validateInput(priorSSPriceContract, handler)) {
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_FLAG,"false");
		} else {
			PriorYearPriceProcess process;
			try {
				process = PriorYearPriceProcessFactory.singleton().create();
			} catch (QuoteException e) {
				logContext.error(this, e.getMessage());
				throw new QuoteException(e);
			}
			process.addOmittedPriorSSPrice(priorSSPriceContract);
			String ytyGrwthPct = process.getNewOmittedYtYGrowth(priorSSPriceContract.getQuoteNumber(),priorSSPriceContract.getRenewalNum(),priorSSPriceContract.getLineSeqNum()); 
			logContext.debug(this, "process the priorSSPrice");
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_FLAG,
					"true");
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,DecimalUtil.format(Double.valueOf(priorSSPriceContract.getLocalUnitPriceLpp())));
			
			handler.addObject(DraftQuoteParamKeys.PARAM_YTY_GROWTH,ytyGrwthPct);
		}
		handler.setState(DraftQuoteStateKeys.STATE_REDIRECT_TO_SUBMIT_OMITTED_LPP_FORM);
		return handler.getResultBean();
	}
}
