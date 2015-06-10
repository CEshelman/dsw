package com.ibm.dsw.quote.common.exception;

import com.ibm.dsw.quote.common.config.CommonDBConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>TooManyRowsException</code> class is to indicate that too many
 * rows are returned by SP.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 28, 2007
 */
public class TooManyRowsException extends SPException {

    /**
     *  
     */
    public TooManyRowsException() {
        super(CommonDBConstants.DB2_SP_RETURN_SIGN_TOOMANY_ROWS);
    }

    /**
     * @param message
     */
    public TooManyRowsException(String message) {
        super(message, CommonDBConstants.DB2_SP_RETURN_SIGN_TOOMANY_ROWS);
    }

    /**
     * @param message
     * @param cause
     */
    public TooManyRowsException(String message, Throwable cause) {
        super(message, cause, CommonDBConstants.DB2_SP_RETURN_SIGN_TOOMANY_ROWS);
    }

    /**
     * @param cause
     */
    public TooManyRowsException(Throwable cause) {
        super(cause, CommonDBConstants.DB2_SP_RETURN_SIGN_TOOMANY_ROWS);
    }
}
