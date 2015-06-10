package com.ibm.dsw.quote.opportunity.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OpportunityDSException</code> class.
 * 
 * @author: jiewbj@cn.ibm.com
 * 
 * Creation date: 2013-09-05
 */
public class OpportunityDSException extends QuoteException {

    /**
     * 
     */
    public OpportunityDSException() {
        super();
         
    }
    /**
     * @param message
     */
    public OpportunityDSException(String message) {
        super(message);
         
    }
    /**
     * @param message
     * @param cause
     */
    public OpportunityDSException(String message, Throwable cause) {
        super(message, cause);
         
    }
    
    public OpportunityDSException(String message, String messageKey, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }
    /**
     * @param cause
     */
    public OpportunityDSException(Throwable cause) {
        super(cause);
         
    }
}
