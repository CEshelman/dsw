package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>NewQuoteUnAuthorizedException</code> class.
 * 
 * @author wangxu@cn.ibm.com
 *
 * Created on 2007-4-17
 */
public class NewQuoteUnAuthorizedException extends QuoteException {
    /**
     * 
     */
    public NewQuoteUnAuthorizedException() {
        super();
    }
    /**
     * @param message
     */
    public NewQuoteUnAuthorizedException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteUnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public NewQuoteUnAuthorizedException(Throwable cause) {
        super(cause);
    }
    

    public String getMessageKey() {
        return ErrorKeys.MSG_UN_AUTHORIZED;
    }
    
    public void setMessageKey(String msgKey) {
        throw new IllegalArgumentException();
    }

}
