package com.ibm.dsw.quote.pvu.process;

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
 * This <code>VUConfigProcessFactory</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public abstract class VUConfigProcessFactory {
    
    private static VUConfigProcessFactory singleton ;
    
    public abstract VUConfigProcess create() throws QuoteException;
    
    public static VUConfigProcessFactory sigleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == VUConfigProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        VUConfigProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                VUConfigProcessFactory.singleton = (VUConfigProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(VUConfigProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(VUConfigProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(VUConfigProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
