package com.ibm.dsw.quote.customer.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.contract.DisplayCreateApplianceAddressContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

public class DisplayCreateApplianceAddressAction extends CustomerBaseAction {

	private static final long serialVersionUID = -7272815861655793289L;

	@Override
	protected String getState(ProcessContract contract) {
		String stateStr = DraftQuoteStateKeys.STATE_DISPLAY_CREATE_APPLIANCE_ADDRESS;
		DisplayCreateApplianceAddressContract customerBaseContract = (DisplayCreateApplianceAddressContract) contract;
		if(Boolean.valueOf(customerBaseContract.getIsSubmittedQuote()))
			stateStr = DraftQuoteStateKeys.STATE_DISPLAY_SUBMITTEDQT_CREATE_APPLIANCE_ADDRESS;
		return stateStr;
	}

	@Override
	protected Object getEObject(ProcessContract contract) throws QuoteException {
		DisplayCreateApplianceAddressContract customerBaseContract = (DisplayCreateApplianceAddressContract) contract;
		Map<String,String> validMap = new HashMap<String,String>();
		validMap.put(ParamKeys.PARAM_QUOTE_NUM, customerBaseContract.getQuoteNum());
		validMap.put(ParamKeys.PARAM_COUNTRY, customerBaseContract.getCountry());
		validMap.put(ParamKeys.PARAM_LINE_OF_BUSINESS, customerBaseContract.getLob());
		validMap.put(ParamKeys.PARAM_ADDRESS_TYPE, customerBaseContract.getAddressType());
		validMap.put(ParamKeys.PARAM_AGREEMENT_NUM, customerBaseContract.getAgreementNumber());
		validMap.put(ParamKeys.PARAM_IS_SBMT_QT, customerBaseContract.getIsSubmittedQuote());
		if(!com.ibm.dsw.quote.common.domain.ApplianceAddress.validParams(validMap)){
			logContext.error(this, "HttpRequest params are incorrect.");
			throw new QuoteException("HttpRequest params are incorrect.");
		}
        return null;
	}
	
	@Override
	protected void addContractToResult(ProcessContract contract,
			ResultHandler handler) {
		super.addContractToResult(contract, handler);
		
		DisplayCreateApplianceAddressContract customerBaseContract = (DisplayCreateApplianceAddressContract) contract;
		handler.addObject(ParamKeys.PARAM_ADDRESS_TYPE, customerBaseContract.getAddressType());
		if(Boolean.valueOf(customerBaseContract.getIsSubmittedQuote())){
			handler.addObject(ParamKeys.PARAM_IS_SBMT_QT, customerBaseContract.getIsSubmittedQuote());
		}
	}

}
