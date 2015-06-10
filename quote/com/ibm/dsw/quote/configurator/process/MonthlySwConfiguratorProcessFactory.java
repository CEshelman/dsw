/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * @ClassName: MonthlyConfiguratorProcessFactory
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 1:58:53 PM
 *
 */
public abstract class MonthlySwConfiguratorProcessFactory {
	
	private static MonthlySwConfiguratorProcessFactory singleton;
	
	public abstract MonthlyConfiguratorProcess createConfiguratorProcess(String configrtnActionCode) throws QuoteException; 
	
	
	  /**
     * 
     * @return
     */
    public static MonthlySwConfiguratorProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (MonthlySwConfiguratorProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		MonthlySwConfiguratorProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                MonthlySwConfiguratorProcessFactory.singleton = (MonthlySwConfiguratorProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(MonthlySwConfiguratorProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(MonthlySwConfiguratorProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(MonthlySwConfiguratorProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
