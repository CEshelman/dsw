package com.ibm.dsw.quote.home.process.jdbc;

import com.ibm.dsw.quote.home.process.LoginProcess;
import com.ibm.dsw.quote.home.process.LoginProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class LoginProcessFactory_jdbc extends LoginProcessFactory {

    /**
     * Creates a new JDBC create <code>LoginProcess</code>
     * @return the LoginProcess_jdbc
     * @throws TopazException
     * @see com.ibm.dsw.quote.home.action.process.LoginProcessFactory#create()
     */
    public LoginProcess create() throws TopazException {
        return new LoginProcess_jdbc();
    }
}
