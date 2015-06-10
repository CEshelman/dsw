package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteInvalidTermException.java</code> class.
 * 
 * @author: whlihui@cn.ibm.com
 * 
 * Creation date: 2012-6-15
 */
public class NewQuoteInvalidTermException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.INVALID_TERM;
    /**
     * 
     */
    public NewQuoteInvalidTermException() {
        super();
        messageKey = NewQuoteMessageKeys.INVALID_TERM;
    }
    /**
     * @param message
     */
    public NewQuoteInvalidTermException(String message) {
        super(message);
        messageKey = NewQuoteMessageKeys.INVALID_TERM;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteInvalidTermException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.INVALID_TERM;
    }
    /**
     * @param cause
     */
    public NewQuoteInvalidTermException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.INVALID_TERM;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
