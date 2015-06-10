package com.ibm.dsw.quote.common.domain;

import java.util.List;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:kunzhwh@cn.ibm.com">Jason Zhang</a><br/>
 */
public abstract class EvalFactory {

    /** the singleton instance */
    private static EvalFactory singleton = null;
    
    /** the logging facility */
    private static LogContext logCtx = LogContextFactory.singleton().getLogContext();
    
    /**
     * Creates a new SalesRepFactory
     * @return
     */
    public static EvalFactory singleton() {
        if (EvalFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(EvalFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                EvalFactory.singleton = (EvalFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(EvalFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(EvalFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(EvalFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    
    public abstract List getEvalActionHis(String quoteNum) throws TopazException;
    
}
