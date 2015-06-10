package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
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
 * This <code>ConfiguratorHeaderFactory<code> class.
 * 
 * @author: Crimson
 * 
 *          Creation date: 22/01/2014
 */

public abstract class ConfiguratorHeaderFactory {
	private static ConfiguratorHeaderFactory singleton = null;

	public ConfiguratorHeaderFactory() {
		super();
	}

	public static ConfiguratorHeaderFactory singleton() {
		GlobalContext globalCtx = GlobalContext.singleton();
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (ConfiguratorHeaderFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(ConfiguratorHeaderFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				ConfiguratorHeaderFactory.singleton = (ConfiguratorHeaderFactory) factoryClass.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(ConfiguratorHeaderFactory.class, iae, iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(ConfiguratorHeaderFactory.class, cnfe, cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(ConfiguratorHeaderFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}

	public abstract ConfiguratorHeader getHdrInfoForMonthlyConfiguratorByWebQuoteNum(String webQuoteNum, String userId)
			throws TopazException;
}
