package com.ibm.dsw.quote.partner.exception;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerNotFoundException</code> class is to indicate that no
 * partner is found.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 28, 2007
 */
public class PartnerNotFoundException extends QuoteException {

    /**
     *  
     */
    public PartnerNotFoundException() {
        super();
    }

    /**
     * @param message
     */
    public PartnerNotFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public PartnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public PartnerNotFoundException(Throwable cause) {
        super(cause);
    }

}
