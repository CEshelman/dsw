package com.ibm.dsw.quote.submittedquote.domain;

import java.util.List;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidApprvrFactory<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public abstract class SBApprvrActHistFactory {
    private static SBApprvrActHistFactory singleton = null;

    /**
     *  
     */
    public SBApprvrActHistFactory() {
        super();
    }

    public abstract SBApprvrActHist createSBApprvrActHist() throws TopazException;

    public abstract List findActHistsByQuoteNum(String webQuoteNum) throws TopazException;

    public static SBApprvrActHistFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        SBApprvrActHistFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SBApprvrActHistFactory.singleton = (SBApprvrActHistFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SpecialBidApprvrFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SpecialBidApprvrFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SpecialBidApprvrFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
