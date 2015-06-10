package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteUnSupportedLOBException</code> class.
 * 
 * @author: wjinfeng@cn.ibm.com
 * 
 * Creation date: 2012-07-04
 */
public class NewQuoteUnSupportedLOBException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.UNSUPPORTED_LOB;
    /**
     * 
     */
    public NewQuoteUnSupportedLOBException() {
        super();
        messageKey = NewQuoteMessageKeys.UNSUPPORTED_LOB;
    }
    /**
     * @param message
     */
    public NewQuoteUnSupportedLOBException(String message) {
        super(message);
        messageKey = NewQuoteMessageKeys.UNSUPPORTED_LOB;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteUnSupportedLOBException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.UNSUPPORTED_LOB;
    }
    /**
     * @param cause
     */
    public NewQuoteUnSupportedLOBException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.UNSUPPORTED_LOB;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
