package com.ibm.dsw.quote.promotion.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuotePromotionProcessFactory</code>
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: 2010-10-20
 */
public abstract class QuotePromotionProcessFactory {
	private static QuotePromotionProcessFactory singleton = null;

    public abstract QuotePromotionProcess create() throws QuoteException;
 
    public static QuotePromotionProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (null == QuotePromotionProcessFactory.singleton) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		QuotePromotionProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuotePromotionProcessFactory.singleton = (QuotePromotionProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuotePromotionProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {

                logCtx.error(QuotePromotionProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuotePromotionProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
