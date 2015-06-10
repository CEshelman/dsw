package com.ibm.dsw.quote.submittedquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * 
 * @author J Zhang
 * 
 */
public abstract class PriorYearPriceProcessFactory {
	private static PriorYearPriceProcessFactory singleton;
	public abstract PriorYearPriceProcess create() throws QuoteException;

	/**
	 * @return
	 */
	public static PriorYearPriceProcessFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (PriorYearPriceProcessFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton()
						.getDefaultClassName(
								PriorYearPriceProcessFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				PriorYearPriceProcessFactory.singleton = (PriorYearPriceProcessFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(PriorYearPriceProcessFactory.class, iae,
						iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(PriorYearPriceProcessFactory.class, cnfe,
						cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(PriorYearPriceProcessFactory.class, ie,
						ie.getMessage());
			}
		}
		return singleton;
	}
}