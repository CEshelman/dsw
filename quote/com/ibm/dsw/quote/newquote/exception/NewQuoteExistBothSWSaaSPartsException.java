package com.ibm.dsw.quote.newquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteExistBothSWSaaSPartsException</code> class.
 * 
 * @author: whlihui@cn.ibm.com
 * 
 * Creation date: 2012-9-26
 */
public class NewQuoteExistBothSWSaaSPartsException extends QuoteException {
    
    private String messageKey = NewQuoteMessageKeys.INVALID_PARTS;
    /**
     * 
     */
    public NewQuoteExistBothSWSaaSPartsException() {
        super();
        messageKey = NewQuoteMessageKeys.INVALID_PARTS;
    }
    /**
     * @param message
     */
    public NewQuoteExistBothSWSaaSPartsException(String message) {
        super(message);
        messageKey = NewQuoteMessageKeys.INVALID_PARTS;
    }
    /**
     * @param message
     * @param cause
     */
    public NewQuoteExistBothSWSaaSPartsException(String message, Throwable cause) {
        super(message, cause);
        messageKey = NewQuoteMessageKeys.INVALID_PARTS;
    }
    /**
     * @param cause
     */
    public NewQuoteExistBothSWSaaSPartsException(Throwable cause) {
        super(cause);
        messageKey = NewQuoteMessageKeys.INVALID_PARTS;
    }
    
    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.exception.QuoteException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}
}
