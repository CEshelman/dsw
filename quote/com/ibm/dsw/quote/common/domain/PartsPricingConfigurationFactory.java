package com.ibm.dsw.quote.common.domain;

import java.util.List;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class PartsPricingConfigurationFactory {
	private static PartsPricingConfigurationFactory singleton = null;
	public PartsPricingConfigurationFactory(){
		super();
	}
	
	public abstract List findPartsPricingConfiguration(String webQuoteNum) throws TopazException;
	
	public static PartsPricingConfigurationFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (PartsPricingConfigurationFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(PartsPricingConfigurationFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PartsPricingConfigurationFactory.singleton = (PartsPricingConfigurationFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartsPricingConfigurationFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PartsPricingConfigurationFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartsPricingConfigurationFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
