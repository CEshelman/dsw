package com.ibm.dsw.quote.mail.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>EmailException.java</code> class is the exception for mail
 * sending.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-2
 */
public class EmailException extends QuoteException {

    /**
     *  
     */
    public EmailException() {
        super();
    }

    /**
     * @param message
     */
    public EmailException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public EmailException(Throwable cause) {
        super(cause);
    }
}
