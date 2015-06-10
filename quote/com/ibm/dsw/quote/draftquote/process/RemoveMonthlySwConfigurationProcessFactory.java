package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;
/**
 * RemoveMonthlySwConfigurationProcessFactory.java
 *
 * <p>
 * Copyright 2014 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="jiamengz@cn.ibm.com">Linda</a> <br/>
 * Jan 2, 2014
 */
public abstract class RemoveMonthlySwConfigurationProcessFactory {
    
    private static RemoveMonthlySwConfigurationProcessFactory singleton;

    public abstract RemoveMonthlySwConfigurationProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static RemoveMonthlySwConfigurationProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		RemoveMonthlySwConfigurationProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                singleton = (RemoveMonthlySwConfigurationProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(RemoveMonthlySwConfigurationProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(RemoveMonthlySwConfigurationProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(RemoveMonthlySwConfigurationProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
