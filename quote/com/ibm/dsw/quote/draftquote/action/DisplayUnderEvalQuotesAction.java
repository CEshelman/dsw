package com.ibm.dsw.quote.draftquote.action;

import java.util.List;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.EvalQuote;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.EvalQuoteStatusContract;
import com.ibm.dsw.quote.draftquote.process.EvalProcess;
import com.ibm.dsw.quote.draftquote.process.EvalProcessFactory;
import com.ibm.dsw.quote.home.action.QuoteRightColumnBaseAction;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class DisplayUnderEvalQuotesAction extends QuoteRightColumnBaseAction {
	private static final long serialVersionUID = 1L;
	private LogContext logger = LogContextFactory.singleton().getLogContext();

	@Override
	public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
			ResultBeanException {
		// TODO Auto-generated method stub		
		logger.debug(this, "Get evaluation quote list.");
		List<EvalQuote> list = getEvalQtUnderValidation(contract);
		logger.debug(this, "Set value for the action handler.");
        handler.addObject(NewQuoteParamKeys.PARAM_EVAL_QUOTE_LIST, list);
        handler.setState(this.getState(contract));
        logger.debug(this, "Set STATE for the return forward.");
        return handler.getResultBean();
	}
	
	/**
	 * get the Quotes, their status are "Under Evaluation"
	 * @param contract
	 * @return
	 * @throws QuoteException
	 */
	private List<EvalQuote> getEvalQtUnderValidation(ProcessContract contract) throws QuoteException {
		List<EvalQuote> result = null; 
		EvalQuoteStatusContract evalQuoteContract = (EvalQuoteStatusContract) contract;
		EvalProcess evalProcess = EvalProcessFactory.singleton().create();
		String creatorId = evalQuoteContract.getCreatorId();
		String isTeleSales = evalQuoteContract.getIsTeleSales();
		logger.debug(this, "creatorId:" + creatorId);
		logger.debug(this, "isTeleSales:" + isTeleSales);
		
		result = evalProcess.getUnderEvalQtList(creatorId, isTeleSales);
		return result;
	}

	@Override
	protected String getState(ProcessContract contract) {
		// TODO Auto-generated method stub
		return DraftQuoteStateKeys.STATE_DISPLAY_EVAL_QUOTE_UNDER_EVALUATION;
	}

}
