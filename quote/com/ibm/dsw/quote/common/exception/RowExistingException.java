package com.ibm.dsw.quote.common.exception;

import com.ibm.dsw.quote.common.config.CommonDBConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RowExistingException</code> class is to indicate the the row is
 * already existing in database.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 29, 2007
 */
public class RowExistingException extends SPException {

    /**
     *  
     */
    public RowExistingException() {
        super(CommonDBConstants.DB2_SP_RETURN_SIGN_ROW_EXISTING);
    }

    /**
     * @param message
     */
    public RowExistingException(String message) {
        super(message, CommonDBConstants.DB2_SP_RETURN_SIGN_ROW_EXISTING);
    }

    /**
     * @param message
     * @param cause
     */
    public RowExistingException(String message, Throwable cause) {
        super(message, cause, CommonDBConstants.DB2_SP_RETURN_SIGN_ROW_EXISTING);
    }

    /**
     * @param cause
     */
    public RowExistingException(Throwable cause) {
        super(cause, CommonDBConstants.DB2_SP_RETURN_SIGN_ROW_EXISTING);
    }

}
