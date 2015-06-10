package com.ibm.dsw.quote.home.process.jdbc;

import com.ibm.dsw.quote.home.process.LoginProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class LoginProcess_jdbc extends LoginProcess_Impl {

    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
    /**
     * Constructor
     */
    public LoginProcess_jdbc() {
        super();
    }
}
