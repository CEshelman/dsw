package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class RetrievePartFromCognosProcessFactory {

    private static RetrievePartFromCognosProcessFactory singleton;

    public abstract RetrievePartFromCognosProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static RetrievePartFromCognosProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (RetrievePartFromCognosProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		RetrievePartFromCognosProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                RetrievePartFromCognosProcessFactory.singleton = (RetrievePartFromCognosProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(RetrievePartFromCognosProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(RetrievePartFromCognosProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(RetrievePartFromCognosProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
