package com.ibm.dsw.quote.ps.process;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SearchProcessFactory.java</code> class is an abstract factory
 * class to create SearchProcess
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class PartSearchProcessFactory {

    private static PartSearchProcessFactory singleton = null;

    public PartSearchProcessFactory() {

    }

    public abstract PartSearchProcess create() throws TopazException;

    public static PartSearchProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (PartSearchProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        PartSearchProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PartSearchProcessFactory.singleton = (PartSearchProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartSearchProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PartSearchProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartSearchProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
