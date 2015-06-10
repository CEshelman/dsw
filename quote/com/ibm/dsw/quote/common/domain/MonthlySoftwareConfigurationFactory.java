package com.ibm.dsw.quote.common.domain;

import java.util.List;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class MonthlySoftwareConfigurationFactory {

	private static MonthlySoftwareConfigurationFactory singleton = null;
	public MonthlySoftwareConfigurationFactory(){
		super();
	}
	
	public abstract List<MonthlySoftwareConfiguration> findMonthlySwConfiguration(String webQuoteNum) throws TopazException;
	
	public abstract MonthlySoftwareConfiguration createMonthlyConfiguration(String userId) throws TopazException;
	
	public static MonthlySoftwareConfigurationFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (MonthlySoftwareConfigurationFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(MonthlySoftwareConfigurationFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                MonthlySoftwareConfigurationFactory.singleton = (MonthlySoftwareConfigurationFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(MonthlySoftwareConfigurationFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(MonthlySoftwareConfigurationFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(MonthlySoftwareConfigurationFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }


}
