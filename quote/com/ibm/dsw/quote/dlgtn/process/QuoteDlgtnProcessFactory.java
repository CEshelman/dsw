package com.ibm.dsw.quote.dlgtn.process;

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
 * The <code>QuoteDlgtnProcessFactory</code> class is QuoteDlgtnProcess
 * factory
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 13, 2007
 */

public abstract class QuoteDlgtnProcessFactory {

    private static QuoteDlgtnProcessFactory singleton = null;

    public abstract QuoteDlgtnProcess create() throws TopazException;

    public static QuoteDlgtnProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == QuoteDlgtnProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteDlgtnProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteDlgtnProcessFactory.singleton = (QuoteDlgtnProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteDlgtnProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {

                logCtx.error(QuoteDlgtnProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteDlgtnProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
