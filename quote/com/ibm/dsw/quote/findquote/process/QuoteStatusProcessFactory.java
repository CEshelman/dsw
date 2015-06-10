package com.ibm.dsw.quote.findquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
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
 * The <code>QuoteStatusProcessFactory.java</code>
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on: Apr 28, 2007
 */

public abstract class QuoteStatusProcessFactory {

    private static QuoteStatusProcessFactory singleton = null ;
    
    /**
     * Constructor
     */
    public QuoteStatusProcessFactory() {
        super();
    }

    public abstract QuoteStatusProcess create() throws QuoteException;
    
    
    public static QuoteStatusProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteStatusProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteStatusProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteStatusProcessFactory.singleton = (QuoteStatusProcessFactory)factoryClass.newInstance();
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
