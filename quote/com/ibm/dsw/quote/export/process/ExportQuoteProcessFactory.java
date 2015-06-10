package com.ibm.dsw.quote.export.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExportQuoteProcessFactory</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public abstract class ExportQuoteProcessFactory {
    
    private static ExportQuoteProcessFactory singleton ;
    
    public abstract ExportQuoteProcess create() throws QuoteException;
    
    public static ExportQuoteProcessFactory sigleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == ExportQuoteProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        ExportQuoteProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                ExportQuoteProcessFactory.singleton = (ExportQuoteProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(ExportQuoteProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(ExportQuoteProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(ExportQuoteProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
