package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.PrepareAddLPPFormContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
/**
 * 
 * @author jason
 *
 */
public class PrepareAddLPPFormAction extends BaseContractActionHandler {

	 
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		PrepareAddLPPFormContract prepareAddLPPFormContract=(PrepareAddLPPFormContract)contract;
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM,prepareAddLPPFormContract.getQuoteNumber() );
		if("".equals(prepareAddLPPFormContract.getLpp())){
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP,"Could not be calculated");
		}else{
			handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP,prepareAddLPPFormContract.getLpp());
		}
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE,prepareAddLPPFormContract.getQuoteCurrencyCode() );
		handler.addObject(DraftQuoteParamKeys.LINE_SEQ_NUM,
				prepareAddLPPFormContract.getLineSeqNum());
		handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_SRC_CODE,
				prepareAddLPPFormContract.getType());
		handler.addObject(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE,
				prepareAddLPPFormContract.getCurrencyCode());
		handler.addObject(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG,prepareAddLPPFormContract.getGdPartFlag());
		handler.setState(DraftQuoteStateKeys.STATE_REDIRECT_TO_ADD_LPP_FORM);
		return handler.getResultBean();
	}

}
