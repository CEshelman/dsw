package com.ibm.dsw.quote.pvu.domain;

import com.ibm.dsw.quote.common.domain.SearchResultList;
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
 * This <code><code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-19
 */
public abstract class VUConfigFactory {
    private static VUConfigFactory singleton ;
    
    public abstract SearchResultList findByConfigNum(String configNum, String noDscrFlag) throws TopazException;
    
    public static VUConfigFactory singleton() {
        
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (VUConfigFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        VUConfigFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                VUConfigFactory.singleton = (VUConfigFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(VUConfigFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(VUConfigFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(VUConfigFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
