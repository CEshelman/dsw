package com.ibm.dsw.quote.submittedquote.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DuplicatedReviewerException</code> class is to indicate that no
 * reviewer is duplicated.
 * 
 * @author: yujjing@cn.ibm.com
 * 
 * Created on: Mar 28, 2007
 */
public class DuplicatedReviewerException extends QuoteException {

    /**
     *  
     */
    public DuplicatedReviewerException() {
        super();
    }

    /**
     * @param message
     */
    public DuplicatedReviewerException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicatedReviewerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public DuplicatedReviewerException(Throwable cause) {
        super(cause);
    }

}
