package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>NewQuoteInvalidInputException</code> class.
 * 
 * @author wangxu@cn.ibm.com
 *
 * Created on 2007-3-30
 */
public class NewQuoteInvalidInputException extends QuoteException {

    /**
     * 
     */
    public NewQuoteInvalidInputException() {
        super();
    }
    /**
     * @param message
     */
    public NewQuoteInvalidInputException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteInvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public NewQuoteInvalidInputException(Throwable cause) {
        super(cause);
    }
    

    public String getMessageKey() {
        return ErrorKeys.MSG_INPUT_INVALID;
    }
    
    public void setMessageKey(String msgKey) {
        throw new IllegalArgumentException();
    }
}
