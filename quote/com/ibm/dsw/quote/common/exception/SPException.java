package com.ibm.dsw.quote.common.exception;

import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This
 * <code>SPException<code> class is the base class of all the DB exceptions.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-27
 */

public class SPException extends TopazException {

    protected int genStatus = CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS;

    /**
     *  
     */
    public SPException() {
        super();
    }

    /**
     * @param message
     */
    public SPException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SPException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public SPException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param genStatus
     */
    public SPException(int genStatus) {
        super();
        this.genStatus = genStatus;
    }

    /**
     * 
     * @param genStatus
     * @param message
     */
    public SPException(String message, int genStatus) {
        super(message);
        this.genStatus = genStatus;
    }
    
    /**
     * 
     * @param genStatus
     * @param cause
     */
    public SPException(Throwable cause, int genStatus) {
        super(cause);
        this.genStatus = genStatus;
    }

    /**
     * 
     * @param message
     * @param cause
     * @param genStatus
     */
    public SPException(String message, Throwable cause, int genStatus){
        super(message, cause);
        this.genStatus = genStatus;
    }
    
    /**
     * @return Returns the genStatus.
     */
    public int getGenStatus() {
        return genStatus;
    }
}
