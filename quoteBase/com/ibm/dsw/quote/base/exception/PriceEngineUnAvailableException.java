package com.ibm.dsw.quote.base.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 5, 2007
 */

public class PriceEngineUnAvailableException extends Exception {

    protected String messageKey = ErrorKeys.MSG_PRICE_ENGINE_UNAVAILABLE;

    /**
     *  
     */
    public PriceEngineUnAvailableException() {
        super();
        
    }

    /**
     * @param message
     */
    public PriceEngineUnAvailableException(String message) {
        super(message);
       
    }
    
    public PriceEngineUnAvailableException(String message, Throwable cause) {
        super(message, cause);

    }

    public PriceEngineUnAvailableException(Throwable cause) {
        super(cause);

    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String string) {
        messageKey = string;
    }
    
  
}
