package com.ibm.dsw.quote.base.exception;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>WebServiceException<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-6-4
 */

public class WebServiceException extends Exception {
    
    public static final int QUOTE_CREATE_SERVICE = 0;
    public static final int QUOTE_MODIFY_SERVICE = 1;
    public static final int CUSTOMER_CREATE_SERVICE = 2;
    public static final int QUOTE_TIMESTAMP_SERVICE = 3;
    
    protected String messageKey = "";
    protected int serviceType = 0;

    /**
     * 
     */
    public WebServiceException() {
        super();
    }

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(Throwable cause) {
        super(cause);
    }
    
    public WebServiceException(String message, String messageKey) {
        super(message);
        this.messageKey = messageKey;
    }
    
    public WebServiceException(String message, String messageKey, int serviceType) {
        super(message);
        this.serviceType = serviceType;
        this.messageKey = messageKey;
    }
    
    public WebServiceException(String message, String messageKey, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
}
