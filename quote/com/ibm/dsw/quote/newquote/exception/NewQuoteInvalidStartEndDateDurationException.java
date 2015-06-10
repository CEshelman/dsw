package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteInvalidStartEndDateDurationException</code> class.
 * 
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */
public class NewQuoteInvalidStartEndDateDurationException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.INVALID_STARTEND_DATE_DURATION;
    /**
     * 
     */
    public NewQuoteInvalidStartEndDateDurationException() {
        super();
        messageKey = NewQuoteMessageKeys.INVALID_STARTEND_DATE_DURATION;
    }
    /**
     * @param message
     */
    public NewQuoteInvalidStartEndDateDurationException(String message) {
        super(message);
        messageKey = NewQuoteMessageKeys.INVALID_STARTEND_DATE_DURATION;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteInvalidStartEndDateDurationException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.INVALID_STARTEND_DATE_DURATION;
    }
    /**
     * @param cause
     */
    public NewQuoteInvalidStartEndDateDurationException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.INVALID_STARTEND_DATE_DURATION;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
