package com.ibm.dsw.quote.base.exception;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteCreateServiceUnavailableException<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-25
 */

public class WebServiceFailureException extends WebServiceException {

    /**
     * 
     */
    public WebServiceFailureException() {
        super();
    }
    
    public WebServiceFailureException(String message, String messageKey) {
        super(message, messageKey);
    }
    
    public WebServiceFailureException(String message, String messageKey, int serviceType) {
        super(message, messageKey, serviceType);
    }
    
    public WebServiceFailureException(String message, String messageKey, Throwable cause) {
        super(message, messageKey, cause);
    }
}
