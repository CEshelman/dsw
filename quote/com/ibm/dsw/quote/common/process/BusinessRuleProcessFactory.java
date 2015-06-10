package com.ibm.dsw.quote.common.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;


/**
 * @author Vivian
 *
 */
public abstract class BusinessRuleProcessFactory {

    private static BusinessRuleProcessFactory singleton = null;

    /**
     * Constructor
     */
    public BusinessRuleProcessFactory() {
        super();
    }

    public abstract BusinessRuleProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static BusinessRuleProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (BusinessRuleProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		BusinessRuleProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                BusinessRuleProcessFactory.singleton = (BusinessRuleProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(BusinessRuleProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(BusinessRuleProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(BusinessRuleProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
