package com.ibm.dsw.quote.common.exception;

import com.ibm.dsw.quote.common.config.CommonDBConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NoDataException<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-27
 */

public class NoDataException extends SPException {

    /**
     *  
     */
    public NoDataException() {
        super(CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA);
    }

    /**
     * @param message
     */
    public NoDataException(String message) {
        super(message, CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA);
    }

    /**
     * @param message
     * @param cause
     */
    public NoDataException(String message, Throwable cause) {
        super(message, cause, CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA);
    }

    /**
     * @param cause
     */
    public NoDataException(Throwable cause) {
        super(cause, CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA);
    }
}
