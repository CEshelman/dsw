package com.ibm.dsw.quote.base.exception;

import com.ibm.dsw.quote.base.config.MessageKeys;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>WebServiceUnavailableException<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-5-31
 */

public class WebServiceUnavailableException extends WebServiceException {

    /**
     * 
     */
    public WebServiceUnavailableException() {
        super();
        messageKey = MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR;
    }
    
    public WebServiceUnavailableException(String message) {
        super(message, MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR);
    }
    
    public WebServiceUnavailableException(String message, int serviceType) {
        super(message, MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, serviceType);
    }
    
    public WebServiceUnavailableException(String message, Throwable cause) {
        super(message, MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, cause);
    }

    public WebServiceUnavailableException(String message, String messageKey, int serviceType) {
        super(message, messageKey, serviceType);
    }
    
    public WebServiceUnavailableException(String message, String messageKey, Throwable cause) {
        super(message, messageKey, cause);
    }
}
