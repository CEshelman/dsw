package com.ibm.dsw.quote.login.history.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class QuoteLoginHistoryProcessFactory {

    private static QuoteLoginHistoryProcessFactory singleton = null ;
    
    /**
     * Constructor
     */
    public QuoteLoginHistoryProcessFactory() {
        super();
    }

    public abstract QuoteLoginHistoryProcess create() throws QuoteException;
    
    
    public static QuoteLoginHistoryProcessFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteLoginHistoryProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteLoginHistoryProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteLoginHistoryProcessFactory.singleton = (QuoteLoginHistoryProcessFactory)factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteLoginHistoryProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteLoginHistoryProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteLoginHistoryProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
