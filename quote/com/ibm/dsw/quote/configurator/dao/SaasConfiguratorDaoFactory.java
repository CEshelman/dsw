package com.ibm.dsw.quote.configurator.dao;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class SaasConfiguratorDaoFactory {

	private static SaasConfiguratorDaoFactory singleton;

	public abstract SaasConfiguratorDao create();

	/**
	 * 
	 * @return
	 */
	public static SaasConfiguratorDaoFactory singleton() {
		GlobalContext globalCtx = GlobalContext.singleton();
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (SaasConfiguratorDaoFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(SaasConfiguratorDaoFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				SaasConfiguratorDaoFactory.singleton = (SaasConfiguratorDaoFactory) factoryClass.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(SaasConfiguratorDaoFactory.class, iae, iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(SaasConfiguratorDaoFactory.class, cnfe, cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(SaasConfiguratorDaoFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}

}
