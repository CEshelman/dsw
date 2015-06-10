package com.ibm.dsw.quote.common.service;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;

import DswCustomerLibrary.QuickEnrollment;
import DswCustomerLibrary.QuickEnrollmentInput;
import DswCustomerLibrary.QuickEnrollmentOutput;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.exception.WebServiceUnavailableException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>QuickEnrollmentServiceHelper<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-17
 */

public class QuickEnrollmentServiceHelper extends QuoteBaseServiceHelper {
	public static final String pref = "0";
    /**
     *
     */
    public QuickEnrollmentServiceHelper() {
        super();
    }

    public boolean callQuickEnrollmentService(Quote quote,boolean signatureRequired,String webEnrollNum) throws WebServiceException {

        QuickEnrollmentOutput outPut = null;
        QuickEnrollmentInput inPut = initQuickEnrollmentInput(quote,signatureRequired,webEnrollNum);

        try {
            ServiceLocator serviceLocator = new ServiceLocator();
            QuickEnrollment QuickEnrollment = (QuickEnrollment) serviceLocator.getServicePort(
                    CommonServiceConstants.QUICK_ENROLLMENT_BINDING, QuickEnrollment.class);
            outPut = QuickEnrollment.execute(inPut);
       } catch (RemoteException e) {
           throw new WebServiceUnavailableException("The quick enrollment service is unavailable now.", e);
       } catch (ServiceLocatorException e) {
           throw new WebServiceUnavailableException("The quick enrollment service is unavailable now.", e);
       } catch (Exception e) {
           throw new WebServiceUnavailableException("The quick enrollment service is unavailable now.", e);
       }

       if(outPut.getErrorFlag() || StringUtils.isBlank(outPut.getIdocNumber())){
    	   throw new WebServiceFailureException("Failed to call quick enrollment service. the reason is follow:"+outPut.getReturnMessage(), MessageKeys.MSG_CREATE_QUOTE_SERVICE_ERROR);
       }else{
    	   quote.getQuoteHeader().setContractNum(StringUtils.leftPad(outPut.getIdocNumber(), 10, pref));
    	   return true;
        }
    }

	private QuickEnrollmentInput initQuickEnrollmentInput(Quote quote,boolean signatureRequired,String webEnrollNum) {
		QuickEnrollmentInput input = new QuickEnrollmentInput();
		input.setCustomerNumber(quote.getCustomer().getCustNum());
		input.setCustomerType(CustomerConstants.LOB_CSA);
		input.setSignatureRequired(signatureRequired);
		input.setWebReferenceNumber(webEnrollNum);
		logContext.debug(this, "customer = " + quote.getCustomer().getCustNum());
		logContext.debug(this, "customerType = " + CustomerConstants.LOB_CSA);
		logContext.debug(this, "signatureRequired = " + signatureRequired);
		logContext.debug(this, "webEnrollNum = " + webEnrollNum);
		return input;
	}

    
}
