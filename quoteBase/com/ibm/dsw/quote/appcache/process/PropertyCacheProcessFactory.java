package com.ibm.dsw.quote.appcache.process;

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
 * The <code>PropertyCacheProcessFactory</code> class is Common Process factory.
 */
public abstract class PropertyCacheProcessFactory {
    private static PropertyCacheProcessFactory singleton = null;

    public abstract PropertyCacheProcess create() throws QuoteException;

    public static PropertyCacheProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == PropertyCacheProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        PropertyCacheProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PropertyCacheProcessFactory.singleton = (PropertyCacheProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PropertyCacheProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {

                logCtx.error(PropertyCacheProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PropertyCacheProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
