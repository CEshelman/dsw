package com.ibm.dsw.quote.customer.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.SelectApplianceAddressContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class SelectApplianceAddressAction extends BaseContractActionHandler {

	private static final long serialVersionUID = -3673885910715476045L;
	public LogContext logger = LogContextFactory.singleton().getLogContext();

	@Override
	/** Required parameters: quoteNum, country, lob, addressType */
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException 
	{
		SelectApplianceAddressContract appContract = (SelectApplianceAddressContract) contract;
		String state = DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE;
		String quoteNum = appContract.getQuoteNum();
		String country = appContract.getCountry();
		String lob = appContract.getLob();
		String agreementNumber = appContract.getAgreementNumber();
		String isSubmittedQuote = appContract.getIsSubmittedQuote();
		
		Map<String,String> validMap = new HashMap<String,String>();
		validMap.put(ParamKeys.PARAM_QUOTE_NUM, quoteNum);
		validMap.put(ParamKeys.PARAM_COUNTRY, country);
		validMap.put(ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
		validMap.put(ParamKeys.PARAM_ADDRESS_TYPE, appContract.getAddressType());
		validMap.put(ParamKeys.PARAM_AGREEMENT_NUM, agreementNumber);
		validMap.put(ParamKeys.PARAM_IS_SBMT_QT, isSubmittedQuote);
		if(!com.ibm.dsw.quote.common.domain.ApplianceAddress.validParams(validMap)){
			logContext.error(this, "HttpRequest params are incorrect.");
            throw new QuoteException("HttpRequest params are incorrect.");
		}
		
		if (isNotBlank(quoteNum) && isNotBlank(country) && isNotBlank(lob) ) {
	        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, quoteNum);
			handler.addObject(ParamKeys.PARAM_COUNTRY,  country);
			handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
			handler.addObject(ParamKeys.PARAM_ADDRESS_TYPE, appContract.getAddressType() );
			handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, agreementNumber);
	        
	        state = CustomerStateKeys.STATE_DISPLAY_SELECT_APPLIANCE_ADDRESS;
	        if(Boolean.valueOf(isSubmittedQuote))
	        {
	        	handler.addObject(ParamKeys.PARAM_IS_SBMT_QT, isSubmittedQuote);
	        	state = CustomerStateKeys.STATE_DISPLAY_SUBMITTEDQT_SELECT_APPLIANCE_ADDRESS;
	        }
		}
		handler.setState(state);
		return handler.getResultBean();
	}

}
