/*
 * Created on 2007-4-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.domain;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PartPriceDetailFactory {
    
    private static PartPriceDetailFactory singleton;
    
    public abstract PartPriceDetail getPartDetails(String partNumber, String webQNumber, String priceType, boolean loadCoPrerequisites)throws QuoteException;
    
    public static PartPriceDetailFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (PartPriceDetailFactory.singleton == null) {
            synchronized(PartPriceDetailFactory.class){
                if(PartPriceDetailFactory.singleton == null){
	                String factoryClassName = null;
	                try {
		                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(PartPriceDetailFactory.class.getName());
		                Class factoryClass = Class.forName(factoryClassName);
		                PartPriceDetailFactory.singleton = (PartPriceDetailFactory) factoryClass.newInstance();
		            } catch (IllegalAccessException iae) {
		                logCtx.error(QuoteHeaderFactory.class, iae, iae.getMessage());
		            } catch (ClassNotFoundException cnfe) {
		                logCtx.error(QuoteHeaderFactory.class, cnfe, cnfe.getMessage());
		            } catch (InstantiationException ie) {
		                logCtx.error(QuoteHeaderFactory.class, ie, ie.getMessage());
		            }
                }
            }
        }
        return singleton;
    }
    
}
