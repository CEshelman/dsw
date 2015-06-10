/*
 * Created on 2007-4-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process;

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
 * The <code>SpecialBidProcessFactory.java</code> class is to 
 * 
 * @author: lijiatao@cn.ibm.com
 * 
 * Creation date: 2007-4-2
 */
public abstract class SpecialBidProcessFactory {
    
	private static SpecialBidProcessFactory singleton;

    public abstract SpecialBidProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static SpecialBidProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        SpecialBidProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                singleton = (SpecialBidProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SpecialBidProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SpecialBidProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SpecialBidProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
