package com.ibm.dsw.quote.provisng.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;


public abstract class RedirectProvisngProcessFactory {
	
	private static RedirectProvisngProcessFactory singleton;

	public abstract RedirectProvisngProcess create() throws QuoteException;
	
	public static RedirectProvisngProcessFactory singleton() {
		
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        
        if(RedirectProvisngProcessFactory.singleton == null){
        	String factoryClassName = null;
        	try{
        		factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
        				RedirectProvisngProcessFactory.class.getName());
        		Class factoryClass = Class.forName(factoryClassName);
        		
        		RedirectProvisngProcessFactory.singleton = (RedirectProvisngProcessFactory)factoryClass.newInstance();
        	}catch (IllegalAccessException iae) {
                logCtx.error(RedirectProvisngProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(RedirectProvisngProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(RedirectProvisngProcessFactory.class, ie, ie.getMessage());
            }
        }
        
        return singleton;
	}
}
