package com.ibm.dsw.quote.partner.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerProcessFactory</code> class is the abstract
 * process factory for Partner process.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public abstract class PartnerProcessFactory {
    private static PartnerProcessFactory singleton = null;

    public abstract PartnerProcess create() throws QuoteException;

    public static PartnerProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        if (PartnerProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        PartnerProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PartnerProcessFactory.singleton = (PartnerProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartnerProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PartnerProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartnerProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
