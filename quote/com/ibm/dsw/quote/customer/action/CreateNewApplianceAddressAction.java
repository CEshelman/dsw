package com.ibm.dsw.quote.customer.action;

import java.util.List;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.customer.contract.CreateApplianceAddressContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class CreateNewApplianceAddressAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 3512858676754588949L;
	private static LogContext logContext = LogContextFactory.singleton().getLogContext();

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
			ResultBeanException {
		logContext.debug(this, "Create appliance customer address.");
		try{
			boolean result = this.createApplianceAddr(contract);
			if (!result) {
				logContext.error(this, "Save appliance address failed.");
				throw new QuoteException("Save appliance address failed.");
			}
		} catch(WebServiceFailureException e){
			return this.undoResultBySAPErr(handler, e);
		}
		return returnResultSucc(contract, handler);
	}

	private ResultBean returnResultSucc(ProcessContract contract, ResultHandler handler) throws ResultBeanException {
		logContext.debug(this, "Set return value for view bean.");
		this.addContractToResult(contract, handler);
		logContext.debug(this, "Set state for return.");
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, this.getRedirectURL(contract));
		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE); 
		handler.setState(this.getState());
		return handler.getResultBean();
	}
	
	private ResultBean undoResultBySAPErr(ResultHandler handler, WebServiceFailureException e) {
		logContext.debug(this, "Set return value for SAP error.");
		String  errorMessage = e.getMessage();
		ResultBean result = handler.getUndoResultBean();
		MessageBean mBean = MessageBeanFactory.create();
		mBean.setMessage(errorMessage);
		result.setMessageBean(mBean);
		return result;
	}

	private String getRedirectURL(ProcessContract contract){
		CreateApplianceAddressContract applianAddrContr = (CreateApplianceAddressContract) contract;
		String baseUrl = HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_APPLIANCE_ADDRESS);
    	StringBuffer url = new StringBuffer(baseUrl);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, applianAddrContr.getQuoteNum());
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, applianAddrContr.getLob());
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_CUSTNUM, applianAddrContr.getCustomerNumber());
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_ADDRESS_TYPE, applianAddrContr.getAddressType());
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_COUNTRY, applianAddrContr.getCountry());
    	return url.toString();
	}
	
	protected String getState() {
		return StateKeys.STATE_REDIRECT_ACTION;
	}

	/**
	 * create new appliance customer address.
	 * 
	 * @param contract
	 * @return : true, created successfully, otherwise failed.
	 * @throws QuoteException
	 */
	private boolean createApplianceAddr(ProcessContract contract) throws WebServiceFailureException, QuoteException {
		boolean result = false;
		CreateApplianceAddressContract applianAddrContr = (CreateApplianceAddressContract) contract;
		CustomerProcess custProcess = null;
		try {
			custProcess = CustomerProcessFactory.singleton().create();
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException("Error executing create customer process object.", e);
		}
		   
		try {
			//call SAP customer create RFC
			Customer customer = custProcess.createApplianceCustomer(applianAddrContr);
			this.setContractValues(applianAddrContr, customer);
		} catch (WebServiceFailureException we){
			logContext.error(this, we.getMessage());
			throw we;
		} catch (Exception e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException("Error executing SAP call for insert appliance address.", e);
		} 

		//persist customer data into db2 at WEB side
		Customer customer = custProcess.createCustomer(applianAddrContr);
		if (null != customer)
			result = true;
		return result;
	}

	protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
		logContext.debug(this, "Set value for return.");
		CreateApplianceAddressContract applianAddrContr = (CreateApplianceAddressContract) contract;

		handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, applianAddrContr.getLob());
		handler.addObject(ParamKeys.PARAM_COUNTRY, applianAddrContr.getCountry());
		handler.addObject(ParamKeys.PARAM_QUOTE_NUM, applianAddrContr.getQuoteNum());
		handler.addObject(ParamKeys.PARAM_ADDRESS_TYPE, applianAddrContr.getAddressType());
		handler.addObject(ParamKeys.PARAM_CNT_FNAME, applianAddrContr.getCntFirstName());
		handler.addObject(ParamKeys.PARAM_CNT_LNAME, applianAddrContr.getCntLastName());
		handler.addObject(ParamKeys.PARAM_COMPANY_NAME1, applianAddrContr.getCompanyName1());
		handler.addObject(ParamKeys.PARAM_COMPANY_NAME2, applianAddrContr.getCompanyName2());
		handler.addObject(ParamKeys.PARAM_CITY, applianAddrContr.getCity());
		handler.addObject(ParamKeys.PARAM_CNT_PHONE, applianAddrContr.getCntPhoneNumFull());
		handler.addObject(ParamKeys.PARAM_POSTAL_CODE, applianAddrContr.getPostalCode());
	}

	private void setContractValues(CreateApplianceAddressContract applianAddrContr, Customer customer) {
		applianAddrContr.setCustomerNumber(StringHelper.fillString(customer.getCustNum(), 10, '0'));
		applianAddrContr.setWebCustId("0");
		applianAddrContr.setSapContractNum(String.valueOf(customer.getSapCntId()));
		applianAddrContr.setTempAccessNum("");
		applianAddrContr.setSapContactId(String.valueOf(customer.getSapCntId()));
		if (CustomerConstants.LOB_PAE.equals(applianAddrContr.getLob()))
			applianAddrContr.setWebCustTypeCode(CustomerConstants.PAX);
		else
			applianAddrContr.setWebCustTypeCode(applianAddrContr.getLob());
		applianAddrContr.setSapIntlPhoneNum(applianAddrContr.getCntPhoneNumFull());
		applianAddrContr.setMktgEmailFlag("1");
		applianAddrContr.setWebCustStatCode(CustomerConstants.DRAFT);
		if (CustomerConstants.LOB_SSP.equals(applianAddrContr.getLob())) {
			applianAddrContr.setSapCntPrtnrFuncCode(CustomerConstants.PRTNR_FUNC_CODE_ZG);
		} else {
			applianAddrContr.setSapCntPrtnrFuncCode(CustomerConstants.PRTNR_FUNC_CODE_ZW);
		}
		applianAddrContr.setCompanyName(applianAddrContr.getCompanyName1());
		applianAddrContr.setCurrency(this.getCurrency(applianAddrContr.getCountry()));
		applianAddrContr.setCompanySize("0");
		applianAddrContr.setIndustryIndicator("");
		applianAddrContr.setCommLanguage("");
		applianAddrContr.setMediaLanguage("");
		applianAddrContr.setCntFaxNumFull("");
		applianAddrContr.setCntEmailAdr("");
		applianAddrContr.setCustomerType("1");
		applianAddrContr.setVatNum("");
		applianAddrContr.setAgreementType("");
	}

	private String getCurrency(String cntryCode3) {
		String currencyCode = null;
		CacheProcess cProcess = null;
		Country cntryObj = null;
		try {
			cProcess = CacheProcessFactory.singleton().create();
			cntryObj = cProcess.getCountryByCode3(cntryCode3);
			List currency = cntryObj.getCurrencyList();
			CodeDescObj_jdbc currencyOpt = (CodeDescObj_jdbc) currency.get(0);
			currencyCode = currencyOpt.getCode();
		} catch (QuoteException e) {
			logContext.error(this, e.getMessage());
		}

		return currencyCode;
	}

}
