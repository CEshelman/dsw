package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>NewQuotHasBeenLockedException</code> class.
 * 
 * @author zhaoxw@cn.ibm.com
 *
 * Created on 2010-05-13
 */
public class NewQuotHasBeenModifiedException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.QUOTE_HAS_BEEN_MODIFIED;
    
    /**
     * 
     */
    public NewQuotHasBeenModifiedException() {
        super();
    }
    /**
     * @param message
     */
    public NewQuotHasBeenModifiedException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuotHasBeenModifiedException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public NewQuotHasBeenModifiedException(Throwable cause) {
        super(cause);
    }
    

    public String getMessageKey() {
        return this.messageKey;
    }
    
    public void setMessageKey(String msgKey) {
        throw new IllegalArgumentException();
    }

}
