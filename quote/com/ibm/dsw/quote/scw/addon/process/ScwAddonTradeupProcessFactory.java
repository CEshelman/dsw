package com.ibm.dsw.quote.scw.addon.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class ScwAddonTradeupProcessFactory {
	
	 private static ScwAddonTradeupProcessFactory singleton;

	    public abstract ScwAddonTradeupProcess create() throws QuoteException;

	    /**
	     * 
	     * @return
	     */
	    public static ScwAddonTradeupProcessFactory singleton() {
	        GlobalContext globalCtx = GlobalContext.singleton();
	        LogContext logCtx = LogContextFactory.singleton().getLogContext();

	        if (ScwAddonTradeupProcessFactory.singleton == null) {
	            String factoryClassName = null;
	            try {
	                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
	                		ScwAddonTradeupProcessFactory.class.getName());
	                Class factoryClass = Class.forName(factoryClassName);
	                ScwAddonTradeupProcessFactory.singleton = (ScwAddonTradeupProcessFactory) factoryClass.newInstance();
	            } catch (IllegalAccessException iae) {
	                logCtx.error(ScwAddonTradeupProcessFactory.class, iae, iae.getMessage());
	            } catch (ClassNotFoundException cnfe) {
	                logCtx.error(ScwAddonTradeupProcessFactory.class, cnfe, cnfe.getMessage());
	            } catch (InstantiationException ie) {
	                logCtx.error(ScwAddonTradeupProcessFactory.class, ie, ie.getMessage());
	            }
	        }
	        return singleton;
	    }

}
