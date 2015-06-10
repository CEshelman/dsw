/*
 * Created on Mar 13, 2007
 */
package com.ibm.dsw.quote.customer.action;

import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.common.validator.FieldChecks;
import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CustomerEnrollK9Rules;
import com.ibm.dsw.quote.common.util.CustomerEnrollVATRules;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.CustomerCreateContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Lavanya
 */
public class CustomerCreateAction extends CustomerBaseAction {
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected Object getEObject(ProcessContract contract) throws QuoteException {
        return null;
    }
    
    protected String getState(ProcessContract contract) {
        return StateKeys.STATE_REDIRECT_ACTION;
    }
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, 
    	ResultBeanException {

        super.executeBiz(contract, handler);
        if (handler.getResultBean().getState().getStateAsString().equals(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE)){
            return handler.getResultBean();
        }
                
        CustomerCreateContract custCreateContract =(CustomerCreateContract)contract;
        logContext.debug(this, custCreateContract.toString());
        
        String custName = custCreateContract.getCompanyName();
        String address = custCreateContract.getAddress1();
        String city = custCreateContract.getCity();
        String regionCode = custCreateContract.getState();
        String postalCode = custCreateContract.getPostalCode();
        String country = custCreateContract.getCountry();
        String creatorId = custCreateContract.getUserId();
        String currencyCode = custCreateContract.getCurrency();
        String agrmntNum = custCreateContract.getAgreementNumber();
        String agrmntType = custCreateContract.getAgreementType();
        
        setContractValues(custCreateContract);

        try {
            if (CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(agrmntType)) {
                
	            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
	            Contract sapCtrct = quoteProcess.getContractByNum(agrmntNum, CustomerConstants.LOB_PA);

	            // if contract is inactive, return undo state
	            if (sapCtrct.getIsContractActiveFlag() != 1) {
	                return handleCtrctInactive(handler, custCreateContract.getLocale(),
                            CustomerMessageKeys.INVLD_CTRCT_ERR_MSG);
	            }
	            // if chosen contract is GOV type, return undo state and display GOV site type option
	            else if (CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(sapCtrct.getSapContractVariantCode())
	                    && CustomerConstants.COUNTRY_USA.equalsIgnoreCase(country)
	                    && StringUtils.isBlank(custCreateContract.getGovSiteType())) {
	                
	                custCreateContract.setAddiSiteGovTypeDisplay(true);
	                return handleCtrctInactive(handler, custCreateContract.getLocale(),
                            CustomerMessageKeys.INVLD_GOV_ERR_MSG);
	            }
	            // if chosen contract isn't GOV type but GOV site type is selected, return undo state
	            else if ((!CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(sapCtrct.getSapContractVariantCode())
	                    || !CustomerConstants.COUNTRY_USA.equalsIgnoreCase(country))
	                    && StringUtils.isNotBlank(custCreateContract.getGovSiteType())) {
	                
	                custCreateContract.setAddiSiteGovTypeDisplay(false);
	                custCreateContract.setAddiSiteGovType(-1);
	                return handleCtrctInactive(handler, custCreateContract.getLocale(),
                            CustomerMessageKeys.CTRCT_NOT_GOV_MSG);
	            }
            }
            
            CustomerProcess custProcess = CustomerProcessFactory.singleton().create();
            //check for duplicate customers
            SearchResultList resultList = custProcess.findDuplCustomers(custName,address,city,regionCode,postalCode,country);
                        
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, resultList); 
            
            if (resultList.getResultList().size() > 0) {
                handler.setState(CustomerStateKeys.STATE_DISPLAY_DUP_CUST);
            } else {
                //call insertCustomer
                Customer customer = custProcess.createCustomer(custCreateContract);
                //update
                QuoteProcess process = QuoteProcessFactory.singleton().create();
                process.updateQuoteHeaderCustInfo(creatorId, null, "", agrmntNum, customer.getWebCustId(), currencyCode, custCreateContract.getEndUserFlag());
                
                /*
                 * Add by Teddy
                 */                
                String lob_code = SecurityUtil.htmlEncode(custCreateContract.getLob());
                
                handler.setState(StateKeys.STATE_REDIRECT_ACTION);
                handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
                //handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, custCreateContract.getLob());
                handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, lob_code);
                handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                        .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
            }
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing topaz process", e);
        } 
        
    	addContractToResult(contract, handler);
    	return handler.getResultBean();
    }
    
    protected ResultBean handleCtrctInactive(ResultHandler handler, Locale locale, String msgKey) {

        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(msgKey, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        return resultBean;
    }
	protected void validateParams(ProcessContract contract)throws QuoteException {
		// You can add the validate param here or you can add it in the child
		// class.
		CustomerCreateContract custCreateContract = (CustomerCreateContract) contract;
		if (StringUtils.isNotBlank(custCreateContract.getQuoteNum())) {
			if (!SecurityUtil.isValidInput(ParamKeys.PARAM_QUOTE_NUM, custCreateContract.getQuoteNum(), "Numeric", 10, true)) {
				throw new QuoteException("Invalid quoteNum value.");
			}
		}
		if (StringUtils.isNotBlank(custCreateContract.getLob())) {
			if (!SecurityUtil.isValidInput(ParamKeys.PARAM_LINE_OF_BUSINESS, custCreateContract.getLob(), "Alpha", 5, true)) {
				throw new QuoteException("Invalid lob value.");
			}
		}     	
	}
	
    protected boolean validate(ProcessContract contract) {
        super.validate(contract);
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        HashMap vMap = new HashMap();
        int ibmCustNumMaxLength = 7;
        int ibmCustNumMinLength = 6;
        int agrmntNumMaxLength = 10;

        CustomerCreateContract ccContract = (CustomerCreateContract) contract;
        if (ccContract == null)
            return false;

        Country countryObject = getCntryObject(ccContract.getCountry());
        String cntryCode = countryObject.getCode3();
        boolean isValid = true;
                       
        String currency = ccContract.getCurrency();
        String companyName = ccContract.getCompanyName();
        String address1 = ccContract.getAddress1();
        String address2 = ccContract.getAddress2();
        String city = ccContract.getCity();
        String state = ccContract.getState();
        String postalCode = ccContract.getPostalCode();
        String vatNum = StringUtils.trimToEmpty(ccContract.getVatNum());
        String industryInd = ccContract.getIndustryIndicator();
        String companySize = ccContract.getCompanySize();
        String firstName = ccContract.getCntFirstName();
        String lastName = ccContract.getCntLastName();
        String phone = ccContract.getCntPhoneNumFull();
        String fax = ccContract.getCntFaxNumFull();
        String email = ccContract.getCntEmailAdr();
        String commLanguage = ccContract.getCommLanguage();
        String mediaLanguage = ccContract.getMediaLanguage();
        String lob = ccContract.getLob();
        String cusCountry = ccContract.getCusCountry();
        
        if(QuoteConstants.LOB_SSP.equalsIgnoreCase(lob) && "1".equals(ccContract.getEndUserFlag())){
        	countryObject = getCntryObject(cusCountry);
        	cntryCode = countryObject.getCode3();
        }
        
        if(StringUtils.isBlank(lob)){
        	FieldResult field = new FieldResult();
	        field.setMsg(MessageKeys.MSG_REQUIRED);
	        field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.AGREEMENT_TYPE);
	        vMap.put(ParamKeys.PARAM_LINE_OF_BUSINESS, field);
	        isValid = false;
        }
        
        if (StringUtils.isBlank(currency)) {  
            FieldResult field = new FieldResult();
	        field.setMsg(MessageKeys.MSG_REQUIRED);
	        field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.CURRENCY);
	        vMap.put(ParamKeys.PARAM_CURRENCY, field);
	        isValid = false;
        }
        if (StringUtils.isBlank(companyName)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.COMPANY_NAME);
            vMap.put(ParamKeys.PARAM_COMPANY_NAME, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(companyName)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.COMPANY_NAME);
            vMap.put(ParamKeys.PARAM_COMPANY_NAME, field);
            isValid = false;
        }
        
        if (StringUtils.isBlank(address1)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.ADDR_LINE_1);
            vMap.put(ParamKeys.PARAM_ADDRESS1, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(address1)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.ADDR_LINE_1);
            vMap.put(ParamKeys.PARAM_ADDRESS1, field);
            isValid = false;
        }
        
        if ( (StringUtils.isNotBlank(address2)) && (!FieldChecks.validateISOLatin1(address2)) ) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.ADDR_LINE_2);
            vMap.put(ParamKeys.PARAM_ADDRESS2, field);
            isValid = false;
        }
        
        if (StringUtils.isBlank(city)) {  
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.CITY);
            vMap.put(ParamKeys.PARAM_CITY, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(city)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.CITY);
            vMap.put(ParamKeys.PARAM_CITY, field);
            isValid = false;
        }
        
        if(StringUtils.isBlank(postalCode)) {
            if (countryObject.getPostalCodeRequiredFlag()) {  
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.POSTAL_CODE);
                vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
                isValid = false;
            } 
        } else { //if not blank, check maxlength and minlength
            int psMinLength = new Integer(countryObject.getPostalCodeMinLength()).intValue();
            int psMaxLength = new Integer(countryObject.getPostalCodeMaxLength()).intValue();
            
            if (cntryCode.equalsIgnoreCase("USA") &&
                    ((postalCode.length() != 5) &&
                    (postalCode.length() != 10))) {
                Object[] args = { Integer.toString(5), Integer.toString(10) };
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_POST_CODE_VAR_REQ);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.POSTAL_CODE);
                field.addArg("5", false );
                field.addArg("10", false );
                vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
                isValid = false;
            } else if (cntryCode.equalsIgnoreCase("PRT") &&
                    ((postalCode.length() != 4) &&
                    (postalCode.length() != 8))) {
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_POST_CODE_VAR_REQ);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.POSTAL_CODE);
                field.addArg("4", false );
                field.addArg("8", false );
                vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
                isValid = false;
            } else if ((postalCode.length() < psMinLength) || (postalCode.length() > psMaxLength)) {
                if (!cntryCode.equalsIgnoreCase("USA") && !cntryCode.equalsIgnoreCase("PRT")) {
                    Object[] args = { Integer.toString(psMinLength), Integer.toString(psMaxLength) };
                    FieldResult field = new FieldResult();
                    field.setMsg(MessageKeys.MSG_POST_CODE_BETWEEN_REQ);
                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.POSTAL_CODE);
                    field.addArg(String.valueOf(psMinLength), false );
                    field.addArg(String.valueOf(psMaxLength), false );
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

            String k9check = CustomerEnrollK9Rules.singleton()
                                                .checkPostalCode(cntryCode,
                    postalCode, errMsgs);
            if (!k9check.equalsIgnoreCase("")) {
                //messages.addElement(k9check);
                FieldResult field = new FieldResult();
                field.setMsg(k9check);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.POSTAL_CODE);
                vMap.put(ParamKeys.PARAM_POSTAL_CODE, field);
                isValid = false;
            }
        }       
        if(StringUtils.isBlank(vatNum)) {
            if (countryObject.getVatExemptNumRequiredFlag()) {  
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.VAT);
                vMap.put(ParamKeys.PARAM_VAT_NUM, field);
                isValid = false;
            } 
        } else { //if not blank and VatExemptNumRequiredFlag is true, check maxlength and minlength
            if (countryObject.getVatExemptNumRequiredFlag()) {
	        	int isVATok = CustomerEnrollVATRules.singleton().checkVatNumber(vatNum, countryObject.getCode2(),
	                    countryObject.getVatExemptNumMinLength(), countryObject.getVatExemptNumMaxLength(), countryObject.getCntryCodePrefixFlag());
	            if (isVATok != 0) {
	                FieldResult field = null;
	                switch (isVATok) {
	                case 1:
	                    field = new FieldResult();
	                    field.setMsg(MessageKeys.MSG_MINLENGTH_ALPHANUMERIC);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.VAT);
	                    field.addArg(Integer.toString(countryObject.getVatExemptNumMinLength()),false );
	                    vMap.put(ParamKeys.PARAM_VAT_NUM, field);
	                    isValid = false;
	                    break;
	
	                case 2:
	                    field = new FieldResult();
	                    field.setMsg(MessageKeys.MSG_MAXLENGTH);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.VAT);
	                    field.addArg(Integer.toString(countryObject.getVatExemptNumMaxLength()),false );
	                    vMap.put(ParamKeys.PARAM_VAT_NUM, field);
	                    isValid = false;
	                    break;
	
	                case 3:
	                    field = new FieldResult();
	                    field.setMsg(MessageKeys.MSG_VAT_NUM_GAPS);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.VAT);
	                    vMap.put(ParamKeys.PARAM_VAT_NUM, field);
	                    isValid = false;
	                    break;
	
	                case 4:
	                    String tooLetterCode = "";
	
	                    if (cntryCode.equalsIgnoreCase("GRC")) {
	                        // SAP does not use the ISO two letter country code for Greece - need to change this to EL
	                        tooLetterCode = "EL";
	                    } else {
	                        tooLetterCode = countryObject.getCode2();
	                    }
	                    field = new FieldResult();
	                    field.setMsg(MessageKeys.MSG_VAT_NUM_CNTRY_CODE);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.VAT);
	                    field.addArg(tooLetterCode, false );
	                    vMap.put(ParamKeys.PARAM_VAT_NUM, field);
	                    isValid = false;
	                    break;
	
	                case 5:
	                    String code = "";
	
	                    if (cntryCode.equalsIgnoreCase("GRC")) {
	                        // SAP does not use the ISO two letter country code for Greece - need to change this to EL
	                        code = "EL";
	                    } else {
	                        code = countryObject.getCode2();
	                    }
	                    field = new FieldResult();
	                    field.setMsg(MessageKeys.MSG_VAT_NUM_FIXED_LEN);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.VAT);
	                    field.addArg(code, false );
	                    field.addArg(Integer.toString(countryObject.getVatExemptNumMaxLength() - 2),false );
	                    vMap.put(ParamKeys.PARAM_VAT_NUM, field);
	                    isValid = false;
	                    break;
	
	                default:
	                    break;
	                }
	            }
            }
        }
        
        if(QuoteConstants.IND.equalsIgnoreCase(cntryCode) || QuoteConstants.USA.equalsIgnoreCase(cntryCode) 
        		|| QuoteConstants.CAN.equalsIgnoreCase(cntryCode)|| QuoteConstants.BRA.equalsIgnoreCase(cntryCode)){
        	if(StringUtils.isBlank(state)){
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.MSG_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.STATE);
                vMap.put(ParamKeys.PARAM_STATE, field);
                isValid = false;        		
        	}
        }
        if (StringUtils.isBlank(industryInd)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.INDUSTRY);
            vMap.put(ParamKeys.PARAM_INDUSTRY_IND, field);
            isValid = false;
        }
        if (StringUtils.isBlank(companySize)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.COMP_SIZE);
            vMap.put(ParamKeys.PARAM_COMPANY_SIZE, field);
            isValid = false;
        }
        
        if (StringUtils.isBlank(firstName)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.FIRST_NAME);
            vMap.put(ParamKeys.PARAM_CNT_FNAME, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(firstName)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.FIRST_NAME);
            vMap.put(ParamKeys.PARAM_CNT_FNAME, field);
            isValid = false;
        }else if(firstName.contains("_") || firstName.contains("!")){
			FieldResult field = new FieldResult();
			field.setMsg(MessageKeys.MSG_NAME);
			field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					CustomerMessageKeys.FIRST_NAME
					);
			vMap.put(ParamKeys.PARAM_CNT_FNAME, field);
			isValid = false;
		}
        
        if (StringUtils.isBlank(lastName)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.LAST_NAME);
            vMap.put(ParamKeys.PARAM_CNT_LNAME, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(lastName)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.LAST_NAME);
            vMap.put(ParamKeys.PARAM_CNT_LNAME, field);
            isValid = false;
        }
        
        if (StringUtils.isBlank(phone)) {  
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.PHONE_NUM);
            vMap.put(ParamKeys.PARAM_CNT_PHONE, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(phone)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.PHONE_NUM);
            vMap.put(ParamKeys.PARAM_CNT_PHONE, field);
            isValid = false;
        }
        
       if(!FieldChecks.validateISOLatin1(fax)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.FAX_NUM);
            vMap.put(ParamKeys.PARAM_CNT_FAX, field);
            isValid = false;
        }
       
        
        if (StringUtils.isBlank(email)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.EMAIL_ADDR);
            vMap.put(ParamKeys.PARAM_CNT_EMAIL, field);
            isValid = false;
        } else if (!FieldChecks.validateISOLatin1(email)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.EMAIL_ADDR);
            vMap.put(ParamKeys.PARAM_CNT_EMAIL, field);
            isValid = false;
        } else if (!GenericValidator.isEmail(email)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_EMAIL);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.EMAIL_ADDR);
            vMap.put(ParamKeys.PARAM_CNT_EMAIL, field);
            isValid = false;
        }
        
        
        if (StringUtils.isBlank(commLanguage)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.COMM_LANGUAGE);
            vMap.put(ParamKeys.PARAM_COMM_LANGUAGE, field);
            isValid = false;
        }
        if (StringUtils.isBlank(mediaLanguage)) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.MSG_REQUIRED);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.MEDIA_LANGUAGE);
            vMap.put(ParamKeys.PARAM_MEDIA_LANGUAGE, field);
            isValid = false;
        }
        
        if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob)) {
            if (StringUtils.isBlank(ccContract.getAgreementType())) {
                setField(vMap, ParamKeys.PARAM_AGREEMENT_TYPE, MessageKeys.MSG_REQUIRED, CustomerMessageKeys.AGREEMENT_OPTION);
                isValid = false;
            }
            else {
                if (CustomerConstants.AGRMNT_TYPE_XSP.equalsIgnoreCase(ccContract.getAgreementType())) {
                    if (StringUtils.isBlank(ccContract.getTransSVPLevel())) {
                        setField(vMap, ParamKeys.PARAM_TRANS_SVP_LEVEL, MessageKeys.MSG_REQUIRED, CustomerMessageKeys.TRANS_SVP_LEVEL);
                        isValid = false;
                    }
                }
                else if (CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(ccContract.getAgreementType())) {
                    if (StringUtils.isBlank(ccContract.getAgreementNumber())) {
                        setField(vMap, ParamKeys.PARAM_AGREEMENT_NUM, MessageKeys.MSG_REQUIRED, CustomerMessageKeys.AGREEMENT_NUM);
                        isValid = false;
                    }
                    else if (ccContract.getAgreementNumber().length() > agrmntNumMaxLength) {
                        FieldResult field = new FieldResult();
                        field.setMsg(MessageKeys.MSG_MAXLENGTH);
                        field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, CustomerMessageKeys.AGREEMENT_NUM);
                        vMap.put(ParamKeys.PARAM_AGREEMENT_NUM, field);
                        field.addArg(String.valueOf(agrmntNumMaxLength), false);
                        isValid = false;
                    }
                }
                else if (CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(ccContract.getAgreementType())) {
                    boolean isGovTypeRequired = ButtonDisplayRuleFactory.singleton().isGovSiteTypeOptionDisplay(
                            ccContract.getCountry());
                    if (isGovTypeRequired && StringUtils.isBlank(ccContract.getGovSiteType())) {
                        setField(vMap, ParamKeys.PARAM_GOV_SITE_TYPE, MessageKeys.MSG_REQUIRED, CustomerMessageKeys.GOV_SITE_TYPE);
                        isValid = false;
                    }
                }
            }
        } else if (QuoteConstants.LOB_CSA.equalsIgnoreCase(lob)) {
        	if (StringUtils.isBlank(ccContract.getAgreementType())) {
                setField(vMap, ParamKeys.PARAM_AGREEMENT_OPTION, MessageKeys.MSG_REQUIRED, CustomerMessageKeys.AGREEMENT_OPTION);
                isValid = false;
        	}    
        }
        
        addToValidationDataMap(ccContract, vMap);
        return isValid;
    }
    
    protected void setField(HashMap map, String field, String message, String arg) {
        FieldResult result = new FieldResult();
        result.setMsg(message);
        result.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, arg);
        map.put(field, result);
    }
    
    private void setContractValues(CustomerCreateContract custCreateContract) {
        custCreateContract.setCustomerNumber("");
        custCreateContract.setSapContractNum("");
        custCreateContract.setTempAccessNum("");
        custCreateContract.setSapContactId("0");
        if (CustomerConstants.LOB_PAE.equals(custCreateContract.getLob()))
            custCreateContract.setWebCustTypeCode(CustomerConstants.PAX);    
        else if (CustomerConstants.LOB_SSP.equals(custCreateContract.getLob()) && StringUtils.equals("1", custCreateContract.getEndUserFlag()))
        	custCreateContract.setWebCustTypeCode(CustomerConstants.SSP_END_USER);
        else
            custCreateContract.setWebCustTypeCode(custCreateContract.getLob());
        custCreateContract.setSapIntlPhoneNum(custCreateContract.getCntPhoneNumFull());
        custCreateContract.setMktgEmailFlag("1");
        custCreateContract.setWebCustStatCode(CustomerConstants.DRAFT);
        if (CustomerConstants.LOB_SSP.equals(custCreateContract.getLob())){
        	 custCreateContract.setSapCntPrtnrFuncCode(CustomerConstants.PRTNR_FUNC_CODE_ZG);
        }else{
        	 custCreateContract.setSapCntPrtnrFuncCode(CustomerConstants.PRTNR_FUNC_CODE_ZW);
        }
    
    }
    
    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        super.addContractToResult(contract, handler);
        
        CustomerCreateContract custCreateContract = (CustomerCreateContract) contract;
        
        /*
         * Add by Teddy
         */  

        String comNameCode = SecurityUtil.htmlEncode(custCreateContract.getCompanyName());
        String address1_code = SecurityUtil.htmlEncode(custCreateContract.getAddress1());
        String city_code = SecurityUtil.htmlEncode(custCreateContract.getCity());
        String qutoeNum_code = SecurityUtil.htmlEncode(custCreateContract.getQuoteNum());
        String postalcode_code = SecurityUtil.htmlEncode(custCreateContract.getPostalCode());
        String cntFaxNumFull_code = SecurityUtil.htmlEncode(custCreateContract.getCntFaxNumFull());
        
        handler.addObject(ParamKeys.PARAM_CURRENCY, qutoeNum_code); 
        handler.addObject(ParamKeys.PARAM_CURRENCY, custCreateContract.getCurrency());
        handler.addObject(ParamKeys.PARAM_COMPANY_NAME, comNameCode);
        handler.addObject(ParamKeys.PARAM_ADDRESS1, address1_code);
        handler.addObject(ParamKeys.PARAM_ADDRESS2, custCreateContract.getAddress2());
        handler.addObject(ParamKeys.PARAM_CITY, city_code);
        handler.addObject(ParamKeys.PARAM_STATE, custCreateContract.getState());
        handler.addObject(ParamKeys.PARAM_POSTAL_CODE, postalcode_code);
        handler.addObject(ParamKeys.PARAM_VAT_NUM, custCreateContract.getVatNum());
        handler.addObject(ParamKeys.PARAM_INDUSTRY_IND, custCreateContract.getIndustryIndicator());
        handler.addObject(ParamKeys.PARAM_COMPANY_SIZE, custCreateContract.getCompanySize());
        handler.addObject(ParamKeys.PARAM_CNT_FNAME, custCreateContract.getCntFirstName());
        handler.addObject(ParamKeys.PARAM_CNT_LNAME, custCreateContract.getCntLastName());
        handler.addObject(ParamKeys.PARAM_CNT_PHONE, custCreateContract.getCntPhoneNumFull());
        handler.addObject(ParamKeys.PARAM_CNT_FAX, cntFaxNumFull_code);
        handler.addObject(ParamKeys.PARAM_CNT_EMAIL, custCreateContract.getCntEmailAdr());
        handler.addObject(ParamKeys.PARAM_COMM_LANGUAGE, custCreateContract.getCommLanguage());
        handler.addObject(ParamKeys.PARAM_MEDIA_LANGUAGE, custCreateContract.getMediaLanguage());
        handler.addObject(ParamKeys.PARAM_CUST_NUM, custCreateContract.getCustomerNumber());
        handler.addObject(ParamKeys.PARAM_SAP_CONTRACT_NUM, custCreateContract.getSapContractNum());
        handler.addObject(ParamKeys.PARAM_TEMP_ACCESS_NUM, custCreateContract.getTempAccessNum());
        handler.addObject(ParamKeys.PARAM_SAP_CONTACT_ID, custCreateContract.getSapContactId());
        handler.addObject(ParamKeys.PARAM_WEBCUST_TYPE_CODE, custCreateContract.getWebCustTypeCode());
        handler.addObject(ParamKeys.PARAM_SAP_INTL_PHONE, custCreateContract.getSapIntlPhoneNum());
        handler.addObject(ParamKeys.PARAM_MKTG_EMAIL_FLAG, custCreateContract.getMktgEmailFlag());
        handler.addObject(ParamKeys.PARAM_WEBCUST_STAT_CODE, custCreateContract.getWebCustStatCode());
        handler.addObject(ParamKeys.PARAM_SAP_CNT_PRTNR_FUNC_CODE, custCreateContract.getSapCntPrtnrFuncCode());
        handler.addObject(ParamKeys.PARAM_AGREEMENT_TYPE, custCreateContract.getAgreementType());
        handler.addObject(ParamKeys.PARAM_TRANS_SVP_LEVEL, custCreateContract.getTransSVPLevel());
        handler.addObject(ParamKeys.PARAM_GOV_SITE_TYPE, custCreateContract.getGovSiteType());
        handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, custCreateContract.getAgreementNumber());
        handler.addObject(DraftQuoteParamKeys.PARAM_ADDI_SITE_GOV_TYPE_DISPLAY, custCreateContract.getIsAddiSiteGovTypeDisplay());
    }
    
    
    /*private String getRedirectUrl(CustomerCreateContract custCreateContract) {
                
        String url = "DISPLAY_QUOTE_HOME2"
		+ "&" + ParamKeys.PARAM_COUNTRY + "=" + custCreateContract.getCountry()
		+ "&" + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + custCreateContract.getLob()
		+ "&" + ParamKeys.PARAM_QUOTE_NUM + "=" + custCreateContract.getQuoteNum();
        
        String redirectUrl = HtmlUtil.getURLForAction(url);
        
        System.out.println("just set redirectURL to " + redirectUrl);
        return redirectUrl;
        
    }*/
}
