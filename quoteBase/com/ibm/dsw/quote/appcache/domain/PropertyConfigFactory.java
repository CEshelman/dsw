package com.ibm.dsw.quote.appcache.domain;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class PropertyConfigFactory {

	private static PropertyConfigFactory singleton = null;

	public abstract String getPropertyValueByName(String name) throws TopazException;

	public static PropertyConfigFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (PropertyConfigFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton()
						.getDefaultClassName(
								PropertyConfigFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				PropertyConfigFactory.singleton = (PropertyConfigFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(PropertyConfigFactory.class, iae, iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(PropertyConfigFactory.class, cnfe,
						cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(PropertyConfigFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}

}
