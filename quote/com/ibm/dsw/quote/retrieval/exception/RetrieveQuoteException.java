package com.ibm.dsw.quote.retrieval.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.retrieval.RetrieveQuoteResultCodes;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteException</code> class is the base exception for
 * quote retrieval service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 24, 2007
 */
public class RetrieveQuoteException extends QuoteException {
    private int resultCode = RetrieveQuoteResultCodes.UNKNOWN_ERROR_CODE;

    /**
     *  
     */
    public RetrieveQuoteException(int resultCode) {
        super();
        this.resultCode = resultCode;
    }

    /**
     * @param message
     */
    public RetrieveQuoteException(String message, int resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * @param message
     * @param cause
     */
    public RetrieveQuoteException(String message, Throwable cause, int resultCode) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    /**
     * @param cause
     */
    public RetrieveQuoteException(Throwable cause, int resultCode) {
        super(cause);
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}
