package com.ibm.dsw.quote.base.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="cuixg@cn.ibm.com">Mark </a> <br/>
 * 
 * Creation date: Aug 23, 2007
 */

public class OfferPriceException extends RuntimeException {

    protected String messageKey = ErrorKeys.MSG_OFFER_PRICE_ERR;

    public String getMessageKey() {
        return messageKey;
    }
    
    public OfferPriceException(){
        
    }
    
    public OfferPriceException(String messageKey){
        this.messageKey = messageKey;
    }
}
