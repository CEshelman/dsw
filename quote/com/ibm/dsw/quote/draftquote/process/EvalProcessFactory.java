package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;


public abstract class EvalProcessFactory {

	private static EvalProcessFactory singleton = null;

	public abstract EvalProcess create() throws QuoteException;
	
	
	public static EvalProcessFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		
		if (EvalProcessFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(EvalProcessFactory.class.getName());
				EvalProcessFactory.singleton = (EvalProcessFactory) Class.forName(factoryClassName).newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(EvalProcessFactory.class, iae, iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(EvalProcessFactory.class, cnfe, cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(EvalProcessFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}

}
