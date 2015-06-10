package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteInvalidStardEndDateException</code> class.
 * 
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */
public class NewQuoteInvalidStardEndDateException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.INVALID_START_END_DATE;
    /**
     * 
     */
    public NewQuoteInvalidStardEndDateException() {
        super();
        messageKey = NewQuoteMessageKeys.INVALID_START_END_DATE;
    }
    /**
     * @param message
     */
    public NewQuoteInvalidStardEndDateException(String message, String msgKey) {
        super(message);
        messageKey = msgKey;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteInvalidStardEndDateException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.INVALID_START_END_DATE;
    }
    /**
     * @param cause
     */
    public NewQuoteInvalidStardEndDateException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.INVALID_START_END_DATE;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
