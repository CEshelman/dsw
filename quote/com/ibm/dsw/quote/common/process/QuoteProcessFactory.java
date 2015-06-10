package com.ibm.dsw.quote.common.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * @author <a href="doris_yuen@us.ibm.com">Doris Yuen </a> <br/>
 *  
 */
public abstract class QuoteProcessFactory {

    private static QuoteProcessFactory singleton = null;

    /**
     * Constructor
     */
    public QuoteProcessFactory() {
        super();
    }

    public abstract QuoteProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static QuoteProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteProcessFactory.singleton = (QuoteProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
