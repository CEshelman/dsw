package com.ibm.dsw.quote.customer.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.common.validator.FieldChecks;
import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.CustomerEnrollK9Rules;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.CreateApplianceAddressContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

public class CheckDupApplianceAddressAction extends CustomerBaseAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected Object getEObject(ProcessContract contract) throws QuoteException {
		return null;
	}

	@Override
	protected String getState(ProcessContract contract) {
		return StateKeys.STATE_REDIRECT_ACTION;
	}

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {

		CreateApplianceAddressContract applianceAddressCreateContract = (CreateApplianceAddressContract) contract;
		handler.setState(getState(contract));

		Object eObject = getEObject(contract);
		if (eObject != null) {
			handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
		}

		String country = applianceAddressCreateContract.getCountry();
		String companyName1 = applianceAddressCreateContract.getCompanyName1();
		String address1 = applianceAddressCreateContract.getAddress1();
		String city = applianceAddressCreateContract.getCity();
		String state = applianceAddressCreateContract.getState();
		String postalCode = applianceAddressCreateContract.getPostalCode();
		String isSubmittedQuote = applianceAddressCreateContract.getIsSubmittedQuote();

		Map<String,String> validMap = new HashMap<String,String>();
		validMap.put(ParamKeys.PARAM_ADDRESS_TYPE, applianceAddressCreateContract.getAddressType());
		validMap.put(ParamKeys.PARAM_IS_SBMT_QT, isSubmittedQuote);
		validMap.put(ParamKeys.PARAM_QUOTE_NUM, applianceAddressCreateContract.getQuoteNum());
		validMap.put(ParamKeys.PARAM_COUNTRY, country);
		validMap.put(ParamKeys.PARAM_LINE_OF_BUSINESS, applianceAddressCreateContract.getLob());
		validMap.put(ParamKeys.PARAM_AGREEMENT_NUM, applianceAddressCreateContract.getAgreementNum());
		if(!com.ibm.dsw.quote.common.domain.ApplianceAddress.validParams(validMap)){
			logContext.error(this, "HttpRequest params are incorrect.");
            throw new QuoteException("HttpRequest params are incorrect.");
		}
		
		try {

			CustomerProcess custProcess = CustomerProcessFactory.singleton()
					.create();
			// check for duplicate customers
			SearchResultList resultList = custProcess.findDuplCustomers(
					companyName1, address1, city, state, postalCode, country);

			handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, resultList);

			if (resultList.getResultList().size() > 0) {
				handler.setState(CustomerStateKeys.STATE_DISPLAY_DUP_APPLIANCE_ADDRESS);
				if(Boolean.valueOf(isSubmittedQuote))
					handler.setState(CustomerStateKeys.STATE_DISPLAY_SUBMITTEDQT_DUP_APPLIANCE_ADDRESS);
			} else {
				//redirect to appliance address creation
				String redirectURL = HtmlUtil.getURLForAction(CustomerActionKeys.CREATE_NEW_APPLIANCE_ADDRESS);
			    handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
			    handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
			    handler.setState(StateKeys.STATE_REDIRECT_ACTION);
			}
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException("error executing topaz process", e);
		}

		addContractToResult(contract, handler);
		return handler.getResultBean();
	}

	protected void addContractToResult(ProcessContract contract,
			ResultHandler handler) {

		CreateApplianceAddressContract applianceAddressCreateContract = (CreateApplianceAddressContract) contract;
		handler.addObject(ParamKeys.PARAM_COMPANY_NAME1,
				applianceAddressCreateContract.getCompanyName1());
		handler.addObject(ParamKeys.PARAM_COMPANY_NAME2,
				applianceAddressCreateContract.getCompanyName2());
		handler.addObject(ParamKeys.PARAM_ADDRESS1,
				applianceAddressCreateContract.getAddress1());
		handler.addObject(ParamKeys.PARAM_ADDRESS2,
				applianceAddressCreateContract.getAddress2());
		handler.addObject(ParamKeys.PARAM_CITY,
				applianceAddressCreateContract.getCity());
		handler.addObject(ParamKeys.PARAM_STATE,
				applianceAddressCreateContract.getState());
		handler.addObject(ParamKeys.PARAM_POSTAL_CODE,
				applianceAddressCreateContract.getPostalCode());
		handler.addObject(ParamKeys.PARAM_CNT_FNAME,
				applianceAddressCreateContract.getCntFirstName());
		handler.addObject(ParamKeys.PARAM_CNT_LNAME,
				applianceAddressCreateContract.getCntLastName());
		handler.addObject(ParamKeys.PARAM_CNT_PHONE,
				applianceAddressCreateContract.getCntPhoneNumFull());
		handler.addObject(ParamKeys.PARAM_COUNTRY,
				applianceAddressCreateContract.getCountry());
		handler.addObject(ParamKeys.PARAM_ADDRESS_TYPE,
				applianceAddressCreateContract.getAddressType());
		handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS,
				applianceAddressCreateContract.getLob());
		handler.addObject(ParamKeys.PARAM_QUOTE_NUM,
				applianceAddressCreateContract.getQuoteNum());
		handler.addObject(ParamKeys.PARAM_IS_SBMT_QT,
				applianceAddressCreateContract.getIsSubmittedQuote());
	}

	protected boolean validate(ProcessContract contract) {
		super.validate(contract);
		HashMap vMap = new HashMap();

		CreateApplianceAddressContract aaCreateContract = (CreateApplianceAddressContract) contract;
		if (aaCreateContract == null)
			return false;

		Country countryObject = getCntryObject(aaCreateContract.getCountry());
		String cntryCode = countryObject.getCode3();
		boolean isValid = true;

		String custName = aaCreateContract.getCompanyName1();
		String address1 = aaCreateContract.getAddress1();
		String city = aaCreateContract.getCity();
		String state = aaCreateContract.getState();
		String postalCode = aaCreateContract.getPostalCode();
		String firstName = aaCreateContract.getCntFirstName();
		String lastName = aaCreateContract.getCntLastName();
		String phone = aaCreateContract.getCntPhoneNumFull();

		if (StringUtils.isBlank(custName)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_REQUIRED);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.COMPANY_NAME1);
			vMap.put(ParamKeys.PARAM_COMPANY_NAME1, field);
			isValid = false;
		} else if (!FieldChecks.validateISOLatin1(custName)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.COMPANY_NAME1);
			vMap.put(ParamKeys.PARAM_COMPANY_NAME1, field);
			isValid = false;
		}

		if (StringUtils.isBlank(address1)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_REQUIRED);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.ADDR_LINE_1);
			vMap.put(ParamKeys.PARAM_ADDRESS1, field);
			isValid = false;
		} else if (!FieldChecks.validateISOLatin1(address1)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.ADDR_LINE_1);
			vMap.put(ParamKeys.PARAM_ADDRESS1, field);
			isValid = false;
		}

		if (StringUtils.isBlank(city)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_REQUIRED);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.CITY);
			vMap.put(ParamKeys.PARAM_CITY, field);
			isValid = false;
		} else if (!FieldChecks.validateISOLatin1(city)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.CITY);
			vMap.put(ParamKeys.PARAM_CITY, field);
			isValid = false;
		}

		if (StringUtils.isBlank(postalCode)) {
			if (countryObject.getPostalCodeRequiredFlag()) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_REQUIRED);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.POSTAL_CODE);
				vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
				isValid = false;
			}
		} else { // if not blank, check maxlength and minlength
			int psMinLength = new Integer(
					countryObject.getPostalCodeMinLength()).intValue();
			int psMaxLength = new Integer(
					countryObject.getPostalCodeMaxLength()).intValue();

			if (cntryCode.equalsIgnoreCase("USA")
					&& ((postalCode.length() != 5) && (postalCode.length() != 10))) {
				Object[] args = { Integer.toString(5), Integer.toString(10) };
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_POST_CODE_VAR_REQ);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.POSTAL_CODE);
				field.addArg("5", false);
				field.addArg("10", false);
				vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
				isValid = false;
			} else if (cntryCode.equalsIgnoreCase("PRT")
					&& ((postalCode.length() != 4) && (postalCode.length() != 8))) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_POST_CODE_VAR_REQ);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.POSTAL_CODE);
				field.addArg("4", false);
				field.addArg("8", false);
				vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
				isValid = false;
			} else if ((postalCode.length() < psMinLength)
					|| (postalCode.length() > psMaxLength)) {
				if (!cntryCode.equalsIgnoreCase("USA")
						&& !cntryCode.equalsIgnoreCase("PRT")) {
					Object[] args = { Integer.toString(psMinLength),
							Integer.toString(psMaxLength) };
					FieldResult field = new FieldResult();
					field.setMsg(MessageKeys.MSG_POST_CODE_BETWEEN_REQ);
					field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
							CustomerMessageKeys.POSTAL_CODE);
					field.addArg(String.valueOf(psMinLength), false);
					field.addArg(String.valueOf(psMaxLength), false);
					vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
					isValid = false;
				}
			}
			HashMap errMsgs = new HashMap();
			errMsgs.put("USA", MessageKeys.MSG_US_POSTCODE_FORMAT);
			errMsgs.put("CAN", MessageKeys.MSG_CANADA_POSTCODE_FORMAT);
			errMsgs.put("CZE", MessageKeys.MSG_CZ_SV_POSTCODE_FORMAT);
			errMsgs.put("NLD", MessageKeys.MSG_NL_POSTCODE_FORMAT);
			errMsgs.put("POL", MessageKeys.MSG_POLAND_POSTCODE_FORMAT);
			errMsgs.put("KOR", MessageKeys.MSG_SKOREA_POSTCODE_FORMAT);
			errMsgs.put("SWE", MessageKeys.MSG_SWEDEN_POSTCODE_FORMAT);
			errMsgs.put("PRT", MessageKeys.MSG_PORTUGAL_POSTCODE_FORMAT);

			String k9check = CustomerEnrollK9Rules.singleton().checkPostalCode(
					cntryCode, postalCode, errMsgs);
			if (!k9check.equalsIgnoreCase("")) {
				FieldResult field = new FieldResult();
				field.setMsg(k9check);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.POSTAL_CODE);
				vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
				isValid = false;
			}
		}

		if (QuoteConstants.IND.equalsIgnoreCase(cntryCode)
				|| QuoteConstants.USA.equalsIgnoreCase(cntryCode)
				|| QuoteConstants.CAN.equalsIgnoreCase(cntryCode)) {
			if (StringUtils.isBlank(state)) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_REQUIRED);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.STATE);
				vMap.put(ParamKeys.PARAM_STATE, field);
				isValid = false;
			}
		}

		if (StringUtils.isBlank(firstName)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_REQUIRED);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.FIRST_NAME);
			vMap.put(ParamKeys.PARAM_CNT_FNAME, field);
			isValid = false;
		}

		if (StringUtils.isBlank(lastName)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_REQUIRED);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.LAST_NAME);
			vMap.put(ParamKeys.PARAM_CNT_LNAME, field);
			isValid = false;
		}

		if (StringUtils.isBlank(phone)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_REQUIRED);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.PHONE_NUM);
			vMap.put(ParamKeys.PARAM_CNT_PHONE, field);
			isValid = false;
		} else if (!FieldChecks.validateISOLatin1(phone)) {
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.PHONE_NUM);
			vMap.put(ParamKeys.PARAM_CNT_PHONE, field);
			isValid = false;
		}

		addToValidationDataMap(aaCreateContract, vMap);
		return isValid;
	}
}
