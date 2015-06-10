/*
 * Created on 2008-10-28
 * @author Xiao Guo Yi
 */
package com.ibm.dsw.quote.common.domain;

import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public abstract class QuoteReasonFactory {
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	
    private static QuoteReasonFactory singleton;
    
    public abstract void getBackDatingReason(QuoteHeader header) throws TopazException;
    
    public abstract void updateBackDatingReason(QuoteHeader header, String userID) throws TopazException;
    
    public abstract SpecialBidReason loadSpecialBidReason(String webQuoteNum) throws TopazException;
    
    public abstract void updateSpecialBidReason(String webQuoteNum, SpecialBidReason sbReason, String userID) throws TopazException;

    public static QuoteReasonFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteReasonFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		QuoteReasonFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteReasonFactory.singleton = (QuoteReasonFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartPriceProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PartPriceProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartPriceProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
