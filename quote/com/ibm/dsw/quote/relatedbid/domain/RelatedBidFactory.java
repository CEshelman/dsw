package com.ibm.dsw.quote.relatedbid.domain;


import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.relatedbid.process.RelatedBidProcess;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;
//import com.ibm.dsw.quote.customer.contract.CustomerCreateContract;


/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidFactory<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 10, 2013
 */

public abstract class RelatedBidFactory {
    private static RelatedBidFactory singleton = null;

    public RelatedBidFactory() {
        super();
    }
	
     public abstract List findByNum(String renewalQuoteNum) throws TopazException; 
	
     public abstract RelatedBidProcess create() throws QuoteException;
     
	 public static RelatedBidFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (RelatedBidFactory.singleton == null) {
            String relatedClassName = null;
            try {
                relatedClassName = FactoryNameHelper.singleton().getDefaultClassName(RelatedBidFactory.class.getName());
                Class relatedClass = Class.forName(relatedClassName);
                RelatedBidFactory.singleton = (RelatedBidFactory) relatedClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(RelatedBidFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(RelatedBidFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(RelatedBidFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}