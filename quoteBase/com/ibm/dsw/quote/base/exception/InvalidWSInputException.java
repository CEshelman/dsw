package com.ibm.dsw.quote.base.exception;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>InvalidWSInputException<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 6, 2009
 */

public class InvalidWSInputException extends WebServiceException {

    /**
     * 
     */
    public InvalidWSInputException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidWSInputException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidWSInputException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public InvalidWSInputException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param messageKey
     */
    public InvalidWSInputException(String message, String messageKey) {
        super(message, messageKey);
    }

    /**
     * @param message
     * @param messageKey
     * @param serviceType
     */
    public InvalidWSInputException(String message, String messageKey, int serviceType) {
        super(message, messageKey, serviceType);
    }

    /**
     * @param message
     * @param messageKey
     * @param cause
     */
    public InvalidWSInputException(String message, String messageKey, Throwable cause) {
        super(message, messageKey, cause);
    }

}
