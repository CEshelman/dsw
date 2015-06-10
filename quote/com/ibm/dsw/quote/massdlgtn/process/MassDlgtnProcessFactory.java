package com.ibm.dsw.quote.massdlgtn.process;

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
 * The <code>MassDlgtnProcessFactory</code> class is MassDlgtnProcess factory
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public abstract class MassDlgtnProcessFactory {

    private static MassDlgtnProcessFactory singleton = null;

    public abstract MassDlgtnProcess create() throws TopazException;

    public static MassDlgtnProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == MassDlgtnProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        MassDlgtnProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                MassDlgtnProcessFactory.singleton = (MassDlgtnProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(MassDlgtnProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {

                logCtx.error(MassDlgtnProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(MassDlgtnProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
