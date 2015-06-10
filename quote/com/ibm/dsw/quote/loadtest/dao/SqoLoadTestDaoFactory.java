package com.ibm.dsw.quote.loadtest.dao;

import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * @author julia.liu
 *
 */
public abstract class SqoLoadTestDaoFactory {

	private static SqoLoadTestDaoFactory singleton;
	
	public abstract SqoLoadTestDao create();
	
	@SuppressWarnings("rawtypes")
	public static SqoLoadTestDaoFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		if (SqoLoadTestDaoFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(SqoLoadTestDaoFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				SqoLoadTestDaoFactory.singleton = (SqoLoadTestDaoFactory) factoryClass.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(SqoLoadTestDaoFactory.class, iae, LogThrowableUtil.getStackTraceContent(iae));
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(SqoLoadTestDaoFactory.class, cnfe, LogThrowableUtil.getStackTraceContent(cnfe));
			} catch (InstantiationException ie) {
				logCtx.error(SqoLoadTestDaoFactory.class, ie, LogThrowableUtil.getStackTraceContent(ie));
			}
		}
		return singleton;
	}
	
}
