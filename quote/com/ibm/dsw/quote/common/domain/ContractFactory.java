package com.ibm.dsw.quote.common.domain;

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
 * This <code>ContractFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Nov 2, 2009
 */

public abstract class ContractFactory {
    
    private static ContractFactory singleton = null;

    public ContractFactory() {
        super();
    }
    
    public abstract void createOrUpdateWebCtrct(Contract contract, String userID) throws TopazException;
    
    public abstract Contract getContractByNum(String sapCtrctNum, String lob) throws TopazException;
    
    public static ContractFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (ContractFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(ContractFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                ContractFactory.singleton = (ContractFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(ContractFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(ContractFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(ContractFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
