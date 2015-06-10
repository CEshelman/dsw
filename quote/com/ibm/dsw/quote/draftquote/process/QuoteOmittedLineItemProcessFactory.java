package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * 
 * @author guotao
 * 
 */
public abstract class QuoteOmittedLineItemProcessFactory {
	private static QuoteOmittedLineItemProcessFactory singleton;

	public abstract QuoteOmittedLineItemProcess create() throws QuoteException;

	/**
	 * 
	 * @return
	 */
	public static QuoteOmittedLineItemProcessFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (QuoteOmittedLineItemProcessFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = FactoryNameHelper.singleton()
						.getDefaultClassName(
								QuoteOmittedLineItemProcessFactory.class.getName());
				Class factoryClass = Class.forName(factoryClassName);
				QuoteOmittedLineItemProcessFactory.singleton = (QuoteOmittedLineItemProcessFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(QuoteOmittedLineItemProcessFactory.class, iae,
						iae.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(QuoteOmittedLineItemProcessFactory.class, cnfe,
						cnfe.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(QuoteOmittedLineItemProcessFactory.class, ie,
						ie.getMessage());
			}
		}
		return singleton;
	}
}
