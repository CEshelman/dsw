package com.ibm.dsw.quote.scw.userlookup.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class UserLookupAPIProcessFactory {
	
	 private static UserLookupAPIProcessFactory singleton;

	    public abstract UserLookupAPIProcess create() throws QuoteException;

	    /**
	     * 
	     * @return
	     */
	    public static UserLookupAPIProcessFactory singleton() {
	        GlobalContext globalCtx = GlobalContext.singleton();
	        LogContext logCtx = LogContextFactory.singleton().getLogContext();

	        if (UserLookupAPIProcessFactory.singleton == null) {
	            String factoryClassName = null;
	            try {
	                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
	                		UserLookupAPIProcessFactory.class.getName());
	                Class factoryClass = Class.forName(factoryClassName);
	                UserLookupAPIProcessFactory.singleton = (UserLookupAPIProcessFactory) factoryClass.newInstance();
	            } catch (IllegalAccessException iae) {
	                logCtx.error(UserLookupAPIProcessFactory.class, iae, iae.getMessage());
	            } catch (ClassNotFoundException cnfe) {
	                logCtx.error(UserLookupAPIProcessFactory.class, cnfe, cnfe.getMessage());
	            } catch (InstantiationException ie) {
	                logCtx.error(UserLookupAPIProcessFactory.class, ie, ie.getMessage());
	            }
	        }
	        return singleton;
	    }

}
