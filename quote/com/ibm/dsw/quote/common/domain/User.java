package com.ibm.dsw.quote.common.domain;

/**
 * 
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public interface User {

    /**
     * Gets the login id
     * @return the login id
     */
    public String getLoginId();	
    
    /**
     * Sets the login id
     * @param loginId the login id to set
     */
    public void setLoginId( String loginId );
}
