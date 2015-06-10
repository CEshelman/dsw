package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteOmittedLineItemVO;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcessFactory;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitApproverAction<code> class.
 *    
 * @author: liguotao@cn.ibm.com
 * 
 * Creation date: June 25, 2013
 */
public class DisplaySubmittedOmittedLineItemAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -2011742595077772587L;
    

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;
		QuoteOmittedLineItemProcess quoteOmittedLineItemProcess = QuoteOmittedLineItemProcessFactory
				.singleton().create();
		Quote quote = null;
		try {
			quote = QuoteProcessFactory.singleton().create().getSubmittedQuoteBaseInfo(baseContract.getQuoteNum(), baseContract.getUserId(), null);
		} catch (NoDataException e) {
			  throw new QuoteException("can't get current quote", e);
		}
		List<Map<String,List<QuoteOmittedLineItemVO>>> summaryList = new ArrayList<Map<String,List<QuoteOmittedLineItemVO>>>();
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String custName = quoteHeader.getCustName();
		String custCity = quoteHeader.getCountry().getDesc();
		List<QuoteOmittedLineItemVO> renewalQuoteVOList = new ArrayList<QuoteOmittedLineItemVO>();
		List renewalNumList = new ArrayList();
		String currencyCode = quoteHeader.getCurrencyCode(); 
		
		quoteOmittedLineItemProcess.getOmittedLineItemList(renewalQuoteVOList,renewalNumList, baseContract.getQuoteNum());
		
		
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

		
		handler.addObject(ParamKeys.PARAM_REDIRECT_MSG,baseContract.getRedirectMsg());
		handler.addObject(ParamKeys.PARAM_QUOTE_NUM,baseContract.getQuoteNum());
		handler.addObject(ParamKeys.OMITTED_LINEITEM_LIST,summaryList);
		handler.addObject(ParamKeys.PARAM_CUST_NAME, custName);
		handler.addObject(ParamKeys.PARAM_COUNTRY, custCity);
		handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
		handler.addObject(ParamKeys.PARAM_QUOTE_CURRENCY_CODE, currencyCode);
		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SUBMITTED_OMITTED_LINEITEM_LIST);
		
		return handler.getResultBean();
	}
 }