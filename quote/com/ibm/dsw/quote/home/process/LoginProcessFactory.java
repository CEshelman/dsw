package com.ibm.dsw.quote.home.process;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public abstract class LoginProcessFactory {


    private static LoginProcessFactory singleton = null;
    
    /**
     * Constructor
     */
    public LoginProcessFactory() {
        super();
    }

    public abstract LoginProcess create() throws TopazException;
   
    /**
     *
     * @return
     */
    public static LoginProcessFactory singleton(){
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (LoginProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        LoginProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                LoginProcessFactory.singleton = (LoginProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(LoginProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(LoginProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(LoginProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    
}
