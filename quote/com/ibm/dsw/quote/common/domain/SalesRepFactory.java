package com.ibm.dsw.quote.common.domain;

import java.util.List;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 */
public abstract class SalesRepFactory {

    /** the singleton instance */
    private static SalesRepFactory singleton = null;
    
    /** the logging facility */
    private static LogContext logCtx = LogContextFactory.singleton().getLogContext();
    
    /**
     * Creates a new <code>SalesRep</code>
     * @return SalesRep
     */
    public abstract SalesRep create(String internetId, int telesalesAccessLevel ) 
    throws TopazException;
    
    /**
     * Find a delegate list by quote number.
     * @param quoteNum
     * @throws TopazException
     */
    public abstract List findDelegatesByQuote(String quoteNum) throws TopazException;
    
    /**
     * Find salesRep's detail by ID
     * @param userId
     * @throws TopazException
     */
    public abstract SalesRep findDelegateByID(String userId) throws TopazException;
    
    
    /**
     * Creates a new SalesRepFactory
     * @return
     */
    public static SalesRepFactory singleton() {
        if (SalesRepFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(SalesRepFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SalesRepFactory.singleton = (SalesRepFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SalesRepFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SalesRepFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SalesRepFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

    /**
     * Creates a new <code>SalesRep</code> only by internetId
     * @param internetId the id of SalesRep     
     * @return SalesRep
     */
    public abstract SalesRep createSalesRep(String internetId) throws TopazException;
    /**
     * Find all delegates for a given internetId
     * @param internetId
     * @return SalesRep
     * @throws TopazException
     */
    public abstract List findDelegatesBySalesRep(String internetId) throws TopazException;
    
    public abstract String retriveFullRepName(String salesUserId)throws TopazException;
    
}
