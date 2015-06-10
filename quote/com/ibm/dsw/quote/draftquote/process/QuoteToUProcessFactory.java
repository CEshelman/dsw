package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class QuoteToUProcessFactory {
	private static QuoteToUProcessFactory singleton;
	
	public abstract QuoteToUProcess create() throws QuoteException;
	
	public static QuoteToUProcessFactory singleton() {
		
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		
		if (singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		QuoteToUProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                singleton = (QuoteToUProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteToUProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteToUProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteToUProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
	}
}
