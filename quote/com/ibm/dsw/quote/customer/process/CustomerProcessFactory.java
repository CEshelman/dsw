package com.ibm.dsw.quote.customer.process;

import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerProcessFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public abstract class CustomerProcessFactory {

    private static CustomerProcessFactory singleton = null;

    /**
     * Constructor
     */
    public CustomerProcessFactory() {
        super();
    }

    public abstract CustomerProcess create() throws TopazException;

    /**
     * 
     * @return
     */
    public static CustomerProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (CustomerProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        CustomerProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                CustomerProcessFactory.singleton = (CustomerProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
