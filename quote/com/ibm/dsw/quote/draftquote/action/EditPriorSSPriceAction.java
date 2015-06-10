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

public class EditPriorSSPriceAction extends PriorSSPriceAction {

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		PriorSSPriceContract priorSSPriceContract = (PriorSSPriceContract) contract;
		if (!validateInput(priorSSPriceContract, handler)) {
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_FLAG,
					"false");
		} else {
			PriorYearPriceProcess process;
			try {
				process = PriorYearPriceProcessFactory.singleton().create();
			} catch (QuoteException e) {
				logContext.fatal(this, e.getMessage());
				throw new QuoteException(e);
			}
			process.updatePriorSSPrice(priorSSPriceContract);
			logContext.debug(this, "process the priorSSPrice");
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_FLAG,
					"true");
			handler.addObject(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG,priorSSPriceContract.getGdPartFlag());
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
					DecimalUtil.format(Double.valueOf(priorSSPriceContract.getLocalUnitPriceLpp())));
		}
		handler.setState(DraftQuoteStateKeys.STATE_REDIRECT_TO_SUBMIT_LPP_FORM);
		return handler.getResultBean();
	}
}
