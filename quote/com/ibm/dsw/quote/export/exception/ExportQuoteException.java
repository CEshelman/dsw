package com.ibm.dsw.quote.export.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.config.ExportQuoteMessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExportQuoteException</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */
public class ExportQuoteException extends QuoteException {
    
    /**
     * 
     */
    public ExportQuoteException() {
        super();
    }
    /**
     * @param message
     */
    public ExportQuoteException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param cause
     */
    public ExportQuoteException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public ExportQuoteException(Throwable cause) {
        super(cause);
    }
    
    public String getMessageKey() {
        return ExportQuoteMessageKeys.EXPORT_QUOTE_ERROR;
    }
    
}
