package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteInvalidCntryCurrencyException.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */
public class NewQuoteInvalidCntryCurrencyException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.INVALID_CNTRY_CURRENCY;
    /**
     * 
     */
    public NewQuoteInvalidCntryCurrencyException() {
        super();
        messageKey = NewQuoteMessageKeys.INVALID_CNTRY_CURRENCY;
    }
    /**
     * @param message
     */
    public NewQuoteInvalidCntryCurrencyException(String message) {
        super(message);
        messageKey = NewQuoteMessageKeys.INVALID_CNTRY_CURRENCY;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteInvalidCntryCurrencyException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.INVALID_CNTRY_CURRENCY;
    }
    /**
     * @param cause
     */
    public NewQuoteInvalidCntryCurrencyException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.INVALID_CNTRY_CURRENCY;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
