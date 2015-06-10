package com.ibm.dsw.quote.base.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * <P>
 * The <code>QuoteException.java</code> is the base class for all application
 * exceptions Exceptions.
 * <P>
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */

public class QuoteException extends Exception {

    protected String messageKey = ErrorKeys.MSG_UNKNOWN_ERR;

    /**
     * 
     */
    public QuoteException() {
        super();
         
    }
    /**
     * @param message
     */
    public QuoteException(String message) {
        super(message);
         
    }
    /**
     * @param message
     * @param cause
     */
    public QuoteException(String message, Throwable cause) {
        super(message, cause);
         
    }
    
    public QuoteException(String message, String messageKey, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }
    /**
     * @param cause
     */
    public QuoteException(Throwable cause) {
        super(cause);
         
    }
    /**
     * @return
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * @param string
     */
    public void setMessageKey(String string) {
        messageKey = string;
    }

}
