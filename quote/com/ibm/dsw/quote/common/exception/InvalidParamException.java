package com.ibm.dsw.quote.common.exception;

import com.ibm.dsw.quote.common.config.CommonDBConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>InvalidParamException</code> class is to indicate the the input
 * is not valid.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 28, 2007
 */
public class InvalidParamException extends SPException {
    
    /**
     *  
     */
    public InvalidParamException() {
        super(CommonDBConstants.DB2_SP_RETURN_SIGN_INPUT_INVALID);
    }

    /**
     * @param message
     */
    public InvalidParamException(String message) {
        super(message, CommonDBConstants.DB2_SP_RETURN_SIGN_INPUT_INVALID);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidParamException(String message, Throwable cause) {
        super(message, cause, CommonDBConstants.DB2_SP_RETURN_SIGN_INPUT_INVALID);
    }

    /**
     * @param cause
     */
    public InvalidParamException(Throwable cause) {
        super(cause, CommonDBConstants.DB2_SP_RETURN_SIGN_INPUT_INVALID);
    }
}
