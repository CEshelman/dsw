package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EmployeeDlgtnDiscountFactory</code> class.
 * 
 * @author: zhangdy@cn.ibm.com
 * 
 * Created on: Apr 10, 2008
 */
public abstract class EmployeeDlgtnDiscountFactory {
    private static EmployeeDlgtnDiscountFactory singleton = null;
    /**
     * 
     */
    public EmployeeDlgtnDiscountFactory() {
        super();
    }
    
    public abstract int[] checkEmpDlgtnDisc(QuoteHeader header, String rsAndMaxDisc) throws TopazException;
    
    public abstract int[] checkEmpDlgtnDiscByCrrntUser(QuoteHeader header, String rsAndMaxDisc, String userId) throws TopazException;

    public static EmployeeDlgtnDiscountFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (EmployeeDlgtnDiscountFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        EmployeeDlgtnDiscountFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                EmployeeDlgtnDiscountFactory.singleton = (EmployeeDlgtnDiscountFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(EmployeeDlgtnDiscountFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(EmployeeDlgtnDiscountFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(EmployeeDlgtnDiscountFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
