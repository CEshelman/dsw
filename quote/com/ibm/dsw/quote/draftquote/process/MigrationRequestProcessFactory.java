package com.ibm.dsw.quote.draftquote.process;

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
 * The <code>MigrationRequestCustProcessFactory</code> class is MigrationRequestCustProcess Factory.
 * 
 * 
 * @author <a href="mmzhou@cn.ibm.com">Tyler Zhou </a> <br/>
 * 
 * Creation date: 2012-05-24
 */
public abstract class MigrationRequestProcessFactory {
    private static MigrationRequestProcessFactory singleton;

    public abstract MigrationRequestProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static MigrationRequestProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (MigrationRequestProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        MigrationRequestProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                MigrationRequestProcessFactory.singleton = (MigrationRequestProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(MigrationRequestProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(MigrationRequestProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(MigrationRequestProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
