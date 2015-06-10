package com.ibm.dsw.quote.common.service;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import DswCustomerLibrary.CustomerCreate;
import DswCustomerLibrary.CustomerCreateInput;
import DswCustomerLibrary.CustomerCreateOutput;
import DswCustomerLibrary.CustomerInfo;
import DswCustomerLibrary.MediaType;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.CustomerCreateWebServiceFailureException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.opal.log.LogContextFactoryDefault;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerCreateService<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 20, 2007
 */

public class CustomerCreateService {

    public static final String DEFAULT_VALUE_IDENTIFIER = "PASQ";
    
    public static final String FCT_PORTAL_IDENTIFIER = "FCTC";
    
    public static final String SSP_PORTAL_IDENTIFIER = "SSP";
    
    public static final String SHIP_TO_CUSTOMER_IDENTIFIER = "WEBS";
    
    public static final String INSTALL_AT_CUSTOMER_IDENTIFIER = "WEBI";
    
    private static final String NEW_CUST_MEDIA_TYPE = "P";

    private static final String SAP_OUT_SUCCESS = "01";

    private static final String SAP_OUT_FAIL = "03";

    private static final String SAP_OUT_FAIL_NO_COUNTRY = "04";

    private static final String SAP_OUT_FAIL_NO_CREDIT = "05";
    
    private static final String SAP_OUT_FAIL_SERVICE_UNAVAILABLE = "15";
    
    private static final String SAP_OUT_FAIL_NUMBER_EXISTS = "70";
    
    private static final String SAP_OUT_FAIL_NUMBER_INVALID_FIRST3_DIGITS = "71";
    
    private static final String SAP_OUT_FAIL_NUMBER_INVALID_FIRST4_DIGITS = "72";
    
    private static final String SAP_OUT_FAIL_NUMBER_INVALID_LENGTH = "73";
    
    private static final String SAP_OUT_FAIL_NUMBER_INVALID_DATA_INCORRECT = "74";


    public boolean execute(Customer customer) throws RemoteException, WebServiceFailureException, CustomerCreateWebServiceFailureException{
        return makeWSSAPCall(customer);
    }
    protected CustomerInfo createAddressStructure(Customer customer) {
        
        String countryCode2 = "";
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            Country country = process.getCountryByCode3(customer.getCountryCode());
            countryCode2 = country.getCode2();
        } catch (QuoteException e) {
            LogContextFactoryDefault.singleton().getLogContext().error(this, e, "Get country code2 error");
        }
        
        CustomerInfo custInfo = new CustomerInfo();
        LogContext log = LogContextFactory.singleton().getLogContext();
        log.info(this, "Begin creating CustomerInfo:  ");
        // add portal identifier code for FCT
        if (CustomerConstants.LOB_FCT.equalsIgnoreCase(customer.getWebCustTypeCode()))
            custInfo.setIdentifierCode(FCT_PORTAL_IDENTIFIER);
        else if (CustomerConstants.SSP_END_USER.equalsIgnoreCase(customer.getWebCustTypeCode())
        		|| CustomerConstants.LOB_SSP.equalsIgnoreCase(customer.getWebCustTypeCode()))
        	custInfo.setIdentifierCode(SSP_PORTAL_IDENTIFIER);
        else
            custInfo.setIdentifierCode(DEFAULT_VALUE_IDENTIFIER);
        
        //set ship-t0/install at customer identifier
        if(DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equalsIgnoreCase(customer.getAddressType())){
        	custInfo.setIdentifierCode(SHIP_TO_CUSTOMER_IDENTIFIER);
        }else if(DraftQuoteConstants.APPLIANCE_INSTALLAT_ADDTYPE.equalsIgnoreCase(customer.getAddressType())){
        	custInfo.setIdentifierCode(INSTALL_AT_CUSTOMER_IDENTIFIER);
        }
        log.info(this, "custInfo.Identifier: " + custInfo.getIdentifierCode()); 
        custInfo.setCurrencyCode(StringUtils.trimToEmpty(customer.getCurrencyCode()));
        log.info(this, "custInfo.setCurrencyCode: " + custInfo.getCurrencyCode());
        custInfo.setLastNameBusinessContact(StringUtils.trimToEmpty(customer.getCntLastName()));
        log.info(this, "custInfo.setLastNameBusinessContact: " + custInfo.getLastNameBusinessContact());
        custInfo.setFirstNameBusinessContact(StringUtils.trimToEmpty(customer.getCntFirstName()));
        log.info(this, "custInfo.setFirstNameBusinessContact: " + custInfo.getFirstNameBusinessContact());	
        custInfo.setTelephoneNumber(StringUtils.trimToEmpty(customer.getCntPhoneNumFull()));
        log.info(this, "custInfo.setTelephoneNumber: " + custInfo.getTelephoneNumber());
        custInfo.setFaxNumber(StringUtils.trimToEmpty(customer.getCntFaxNumFull()));
        log.info(this, "custInfo.setFaxNumber: " + custInfo.getFaxNumber());
        custInfo.setEmailAddress(StringUtils.trimToEmpty(customer.getCntEmailAdr()));
        log.info(this, "custInfo.setEmailAddress: " + custInfo.getEmailAddress());
        custInfo.setCompanyName(StringUtils.trimToEmpty(customer.getCustName()));
        log.info(this, "custInfo.setCompanyName: " + custInfo.getCompanyName());
        custInfo.setAddressLine1(StringUtils.trimToEmpty(customer.getAddress1()));
        log.info(this, "custInfo.setAddressLine1: " + custInfo.getAddressLine1());
        custInfo.setAddressLine2(StringUtils.trimToEmpty(customer.getInternalAddress()));
        log.info(this, "custInfo.setAddressLine2: " + custInfo.getAddressLine2());
        custInfo.setCity(StringUtils.trimToEmpty(customer.getCity()));
        log.info(this, "custInfo.setCity: " + custInfo.getCity());
        custInfo.setPostalCode(StringUtils.trimToEmpty(customer.getPostalCode()));
        log.info(this, "custInfo.setPostalCode: " + custInfo.getPostalCode());
        custInfo.setIso2CountryCode(StringUtils.trimToEmpty(countryCode2));
        log.info(this, "custInfo.setIso2CountryCode: " + custInfo.getIso2CountryCode());
        custInfo.setRegionCode(StringUtils.trimToEmpty(customer.getSapRegionCode()));
        log.info(this, "custInfo.setRegionCode: " + custInfo.getRegionCode());
        custInfo.setVatNumber(StringUtils.trimToEmpty(customer.getCustVatNum()));
        log.info(this, "custInfo.setVatNumber: " + custInfo.getVatNumber());
        custInfo.setCommunicationLanguage(StringUtils.trimToEmpty(customer.getIsoLangCode()));
        log.info(this, "custInfo.setCommunicationLanguage: " + custInfo.getCommunicationLanguage());
        custInfo.setIbmCustomerNumber(StringUtils.trimToEmpty(customer.getIbmCustNum()));
        log.info(this, "custInfo.setIbmCustomerNumber:  " + custInfo.getIbmCustomerNumber());
        
        if(!DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equalsIgnoreCase(customer.getAddressType())
        		&& !DraftQuoteConstants.APPLIANCE_INSTALLAT_ADDTYPE.equalsIgnoreCase(customer.getAddressType())){
        	custInfo.setMediaType(MediaType.fromString(NEW_CUST_MEDIA_TYPE));
        }
        log.info(this, "custInfo.setMediaType: " + custInfo.getMediaType());
        custInfo.setUpgradeLanguage(StringUtils.trimToEmpty(customer.getUpgrLangCode()));
        log.info(this, "custInfo.setUpgradeLanguage: " + custInfo.getUpgradeLanguage());
        custInfo.setNumberOfEmployees(customer.getEmpTot());
        log.info(this, "custInfo.setNumberOfEmployees: " + custInfo.getNumberOfEmployees());
        custInfo.setIndustryCode(StringUtils.trimToEmpty(customer.getSapWwIsuCode()));
        log.info(this, "custInfo.setIndustryCode: " + custInfo.getIndustryCode());
        
        String authrztnGroup = CustomerConstants.AUTHRZTN_GRP_USA_FEDERAL.equalsIgnoreCase(StringUtils
                .trimToEmpty(customer.getAuthrztnGroup())) ? CustomerConstants.AUTHRZTN_GRP_USA_FEDERAL
                : CustomerConstants.AUTHRZTN_GRP_STATE_LOCAL;
        custInfo.setAuthGroup(authrztnGroup);
        log.info(this, "custInfo.setAuthGroup: " + custInfo.getAuthGroup());
        
        return custInfo;
    }
    

    private boolean makeWSSAPCall(Customer customer) throws RemoteException, WebServiceFailureException, CustomerCreateWebServiceFailureException {
        String returnCode = "";
        String returnMsg = "";
        LogContext log = LogContextFactoryDefault.singleton().getLogContext();

        try {
            log.info(this, customer.toString());
            CustomerCreateInput customerCreateInput = new CustomerCreateInput();
            CustomerCreateOutput customerCreateOutput = new CustomerCreateOutput();
            CustomerInfo customerInfo;
            customerInfo = this.createAddressStructure(customer);
            customerCreateInput.setCustomerInfo(customerInfo);

            ServiceLocator sLoc = new ServiceLocator();
            CustomerCreate port = (CustomerCreate) sLoc.getServicePort(CommonServiceConstants.CUSTOMER_CREATE_BINDING,
                    CustomerCreate.class);
            customerCreateOutput = port.execute(customerCreateInput);
            if (customerCreateOutput != null) {
                returnCode = customerCreateOutput.getStatusCode();             
                returnMsg = customerCreateOutput.getMessage();
                log.debug(this, "Return Code=" + returnCode + "; Return Msg=" + returnMsg);

                    if (SAP_OUT_SUCCESS.equalsIgnoreCase(returnCode)) {
                        log.debug(this, " rfc call SUCCESS.");
                        customer.setCustNum(customerCreateOutput.getCustomerNumber());
                        log.debug(this, "Custnum=" + customerCreateOutput.getCustomerNumber());
                        customer.setSapCntId(Integer.valueOf(customerCreateOutput.getContactNumber()).intValue());
                        log.debug(this, "ContactNumber=" + customerCreateOutput.getContactNumber());
                        customer.setTempAccessNum(customerCreateOutput.getTempAccessCode());
                        log.debug(this, "TempAccessCode=" + customerCreateOutput.getTempAccessCode());
                        return true;
                    }else{
                    	 HashMap vMap = new HashMap();                    	 
                  	     FieldResult fieldResult = new FieldResult(); 
                  	     String draftquoteMsg = "";
                    	 if(SAP_OUT_FAIL_NUMBER_EXISTS.equalsIgnoreCase(returnCode)){                		
                    	                       		
                    		 draftquoteMsg = DraftQuoteMessageKeys.MSG_CS_VAT_NUM_ALREADY_EXISTS;
                    	
                    	}else if(SAP_OUT_FAIL_NUMBER_INVALID_FIRST3_DIGITS.equalsIgnoreCase(returnCode)){
                    		                    		
                   		     draftquoteMsg = DraftQuoteMessageKeys.MSG_CS_VAT_NUM_FIRST_3_NOT_ALPHA;                  	   
                    		                   		
                    	}else if(SAP_OUT_FAIL_NUMBER_INVALID_FIRST4_DIGITS.equalsIgnoreCase(returnCode)){
                    		
                    		draftquoteMsg = DraftQuoteMessageKeys.MSG_CS_VAT_NUM_FIRST_4_NOT_ALPHA;
                    		
                    	}else if(SAP_OUT_FAIL_NUMBER_INVALID_LENGTH.equalsIgnoreCase(returnCode)){
                    		
                    		draftquoteMsg = DraftQuoteMessageKeys.MSG_CS_VAT_NUM_INVALID_LENGTH;

                    	}else if(SAP_OUT_FAIL_NUMBER_INVALID_DATA_INCORRECT.equalsIgnoreCase(returnCode)){
                    		
                    		draftquoteMsg = DraftQuoteMessageKeys.MSG_CS_VAT_NUM_DATA_INCORRECT;          		          
                    		
                    	} else if (SAP_OUT_FAIL_SERVICE_UNAVAILABLE.equalsIgnoreCase(returnCode)) {
                    		
                    		log.error(this, "Failed to call SAP to create the new customer:" + returnCode + ","+ returnMsg);
                    		throw new WebServiceFailureException(returnMsg, MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, WebServiceException.CUSTOMER_CREATE_SERVICE);
                    				
                    	} else {
                    	
                    		log.error(this, "Failed to call SAP to create the new customer:" + returnCode + ","+ returnMsg);
                    		throw new WebServiceFailureException(returnMsg, MessageKeys.MSG_CREATE_CUATOMER_SERVICE_ERROR, WebServiceException.CUSTOMER_CREATE_SERVICE);
                    		
                    	}
                    	
                       log.error(this, "Failed to call SAP to create the new customer:" + returnCode + ","+ returnMsg);                  	  
     		           fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, draftquoteMsg);
     		           fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_CS_VAT_REGISTRATION_NUMBER);
     		           vMap.put(ParamKeys.PARAM_VAT_NUM, fieldResult);
     		           
     		           throw new CustomerCreateWebServiceFailureException(returnMsg, MessageKeys.MSG_CREATE_CUATOMER_SERVICE_NUMBER_ERROR, WebServiceException.CUSTOMER_CREATE_SERVICE, vMap);
                    }
            } else {
            	log.error(this, "Failed to call SAP to create the new customer, no response from SAP");
            	throw new WebServiceFailureException(returnMsg, MessageKeys.MSG_CREATE_CUATOMER_SERVICE_ERROR, WebServiceException.CUSTOMER_CREATE_SERVICE);
            }
        } catch (ServiceLocatorException e) {
            log.error(this, "SAP : call customer create service " + e.toString());
            throw new WebServiceFailureException(returnMsg, MessageKeys.MSG_CREATE_CUATOMER_SERVICE_ERROR);
        }
        
    }
}
