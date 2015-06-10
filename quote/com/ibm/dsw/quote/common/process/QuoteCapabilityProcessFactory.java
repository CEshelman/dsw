package com.ibm.dsw.quote.common.process;

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
 * The <code>QuoteCapabilityProcessFactory</code> class is abstract facotry
 * for QuoteCapabilityProcess.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 5, 2007
 */
public abstract class QuoteCapabilityProcessFactory {
    private static QuoteCapabilityProcessFactory singleton = null;

    public abstract QuoteCapabilityProcess create() throws QuoteException;

    public static QuoteCapabilityProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == QuoteCapabilityProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteCapabilityProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteCapabilityProcessFactory.singleton = (QuoteCapabilityProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteCapabilityProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {

                logCtx.error(QuoteCapabilityProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteCapabilityProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
