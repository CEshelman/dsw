package com.ibm.dsw.quote.customerlist.process;

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
 * This <code>CustomerListProcessFactory<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2008-6-24
 */

public abstract class CustomerListProcessFactory {

    private static CustomerListProcessFactory singleton ;
    
    public abstract CustomerListProcess create() throws QuoteException;
    
    public static CustomerListProcessFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == CustomerListProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        CustomerListProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                CustomerListProcessFactory.singleton = (CustomerListProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(CustomerListProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(CustomerListProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(CustomerListProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
