package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteInvalidLOBOrCountryException.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */
public class NewQuoteInvalidAcquisitionException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.INVALID_ACQUISITION;
    /**
     * 
     */
    public NewQuoteInvalidAcquisitionException() {
        super();
        messageKey = NewQuoteMessageKeys.INVALID_ACQUISITION;
    }
    /**
     * @param message
     */
    public NewQuoteInvalidAcquisitionException(String message) {
        super(message);
        messageKey = NewQuoteMessageKeys.INVALID_ACQUISITION;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteInvalidAcquisitionException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.INVALID_ACQUISITION;
    }
    /**
     * @param cause
     */
    public NewQuoteInvalidAcquisitionException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.INVALID_ACQUISITION;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
