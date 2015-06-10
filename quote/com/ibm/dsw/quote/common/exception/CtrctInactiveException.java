package com.ibm.dsw.quote.common.exception;

import com.ibm.dsw.quote.common.config.CommonDBConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CtrctInactiveException<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Aug 16, 2009
 */

public class CtrctInactiveException extends SPException {

    /**
     * 
     */
    public CtrctInactiveException() {
        super(CommonDBConstants.DB2_SP_RETURN_CONTRACT_INACTIVE);
    }

    /**
     * @param message
     */
    public CtrctInactiveException(String message) {
        super(message, CommonDBConstants.DB2_SP_RETURN_CONTRACT_INACTIVE);
    }

    /**
     * @param message
     * @param cause
     */
    public CtrctInactiveException(String message, Throwable cause) {
        super(message, cause, CommonDBConstants.DB2_SP_RETURN_CONTRACT_INACTIVE);
    }

    /**
     * @param cause
     */
    public CtrctInactiveException(Throwable cause) {
        super(cause, CommonDBConstants.DB2_SP_RETURN_CONTRACT_INACTIVE);
    }

}
