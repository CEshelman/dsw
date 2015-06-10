package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class SaasConfiguratorProcessFactory {

	private static SaasConfiguratorProcessFactory singleton;
	
	public abstract SaasConfiguratorProcess create(String configrtnActionCode) throws QuoteException;

	/**
	 * 
	 * @return
	 */
	public static SaasConfiguratorProcessFactory singleton() {
		GlobalContext globalCtx = GlobalContext.singleton();
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (SaasConfiguratorProcessFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
						SaasConfiguratorProcessFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				SaasConfiguratorProcessFactory.singleton = (SaasConfiguratorProcessFactory) factoryClass.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(SaasConfiguratorProcessFactory.class, iae, iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(SaasConfiguratorProcessFactory.class, cnfe, cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(SaasConfiguratorProcessFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}
}
