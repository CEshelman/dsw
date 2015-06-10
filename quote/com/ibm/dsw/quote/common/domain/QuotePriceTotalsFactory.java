
package com.ibm.dsw.quote.common.domain;

import java.util.List;

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
 * This <code>QuotePriceTotalsFactory<code> class is the factory to create QuotePriceTotals
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 11, 2007
 */

public abstract class QuotePriceTotalsFactory {
    
    
    private static QuotePriceTotalsFactory singleton = null;

   
    public abstract List getQuotePriceTotals(String webQuoteNum,String userID) throws TopazException;

    public abstract QuotePriceTotals createQuotePriceTotals(String webQuoteNum,String distChannelCode,String prcType,String prcSumLevelCode,String currencyCode,String userID, String revnStrmCategoryCode) throws TopazException ;
    
    public abstract void removePriceTotals(String webQuoteNum) throws TopazException;
    
    public static QuotePriceTotalsFactory singleton() {
        
        GlobalContext globalCtx = GlobalContext.singleton();
        
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuotePriceTotalsFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuotePriceTotalsFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuotePriceTotalsFactory.singleton = (QuotePriceTotalsFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuotePriceTotalsFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuotePriceTotalsFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuotePriceTotalsFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
