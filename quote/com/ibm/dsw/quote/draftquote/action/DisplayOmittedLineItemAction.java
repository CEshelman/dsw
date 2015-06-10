package com.ibm.dsw.quote.draftquote.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteOmittedLineItemVO;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author GUOTAO
 */
public  class DisplayOmittedLineItemAction extends
PostPartPriceTabAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		PostPartPriceTabContract postPartPriceTabContract = (PostPartPriceTabContract) contract;
		
		QuoteOmittedLineItemProcess quoteOmittedLineItemProcess = QuoteOmittedLineItemProcessFactory
				.singleton().create();
		Quote quote = null;
		try {
			quote = QuoteProcessFactory.singleton().create().getDraftQuoteBaseInfo(postPartPriceTabContract.getUserId());
		} catch (NoDataException e) {
			  throw new QuoteException("can't get current quote", e);
		}
		List<Map<String,List<QuoteOmittedLineItemVO>>> summaryList = new ArrayList<Map<String,List<QuoteOmittedLineItemVO>>>();
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String custName = quoteHeader.getCustName();
		String custCity = quoteHeader.getCountry().getDesc();
		List<QuoteOmittedLineItemVO> renewalQuoteVOList = new ArrayList<QuoteOmittedLineItemVO>();
		List renewalNumList = new ArrayList();
		//String currencyCode = quoteHeader.getCurrencyCode(); 
		quoteOmittedLineItemProcess.getOmittedLineItemList(renewalQuoteVOList,renewalNumList, postPartPriceTabContract.getWebQuoteNum());
		
		
		for (int j = 0; j < renewalNumList.size(); j++) {
			Map map = new HashMap();
			List omittedItemList = new ArrayList();
			for (int i = 0; i < renewalQuoteVOList.size(); i++) {
				if (renewalQuoteVOList.get(i).getRenewalNum()
						.equals(renewalNumList.get(j))) {
					omittedItemList.add(renewalQuoteVOList.get(i));
				}
			}
			map.put(renewalNumList.get(j), omittedItemList);
			summaryList.add(map);
		}

		
		handler.addObject(ParamKeys.PARAM_QUOTE_NUM,postPartPriceTabContract.getWebQuoteNum());
		handler.addObject(ParamKeys.OMITTED_LINEITEM_LIST,summaryList);
		handler.addObject(ParamKeys.PARAM_CUST_NAME, custName);
		handler.addObject(ParamKeys.PARAM_COUNTRY, custCity);
		//handler.addObject(ParamKeys.PARAM_QUOTE_CURRENCY_CODE, currencyCode);
		handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_OMITTED_LINEITEM_LIST);
		
		return handler.getResultBean();
	}

}
