package com.ibm.dsw.quote.loadtest.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * @author julia.liu
 *
 */
public abstract class SqoLoadTestProcessFactory {

	private static SqoLoadTestProcessFactory singleton;
	
	public abstract SqoLoadTestProcess create() throws QuoteException;
	
    @SuppressWarnings("rawtypes")
	public static SqoLoadTestProcessFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (SqoLoadTestProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		SqoLoadTestProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SqoLoadTestProcessFactory.singleton = (SqoLoadTestProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SqoLoadTestProcessFactory.class, iae, LogThrowableUtil.getStackTraceContent(iae));
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SqoLoadTestProcessFactory.class, cnfe, LogThrowableUtil.getStackTraceContent(cnfe));
            } catch (InstantiationException ie) {
                logCtx.error(SqoLoadTestProcessFactory.class, ie, LogThrowableUtil.getStackTraceContent(ie));
            }
        }
        return singleton;
    }

}
