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
 * @author: changwei@cn.ibm.com
 * 
 * Creation date: Apr 21, 2009
 */
public abstract class SpecialBidRselAuthRuleProcessFactory {

    private static SpecialBidRselAuthRuleProcessFactory singleton = null;

    /**
     * Constructor
     */
    public SpecialBidRselAuthRuleProcessFactory() {
        super();
    }

    public abstract SpecialBidRselAuthRuleProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static SpecialBidRselAuthRuleProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (SpecialBidRselAuthRuleProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        SpecialBidRselAuthRuleProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SpecialBidRselAuthRuleProcessFactory.singleton = (SpecialBidRselAuthRuleProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SpecialBidRselAuthRuleProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SpecialBidRselAuthRuleProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SpecialBidRselAuthRuleProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
