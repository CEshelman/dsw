package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ConfiguratorPartProcessFactory</code> class is ConfiguratorPartProcess Factory.
 * 
 * 
 * @author <a href="jhma@cn.ibm.com">Jun Hui Ma </a> <br/>
 * 
 * Creation date: 2011-6-17
 */
public abstract class ConfiguratorPartProcessFactory {
    private static ConfiguratorPartProcessFactory singleton;

    public abstract ConfiguratorPartProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static ConfiguratorPartProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (ConfiguratorPartProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		ConfiguratorPartProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                ConfiguratorPartProcessFactory.singleton = (ConfiguratorPartProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(ConfiguratorPartProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(ConfiguratorPartProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(ConfiguratorPartProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
