package com.ibm.dsw.quote.newquote.process;

import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-14
 */
public abstract class UploadSalesQuoteProcessFactory {

   private static UploadSalesQuoteProcessFactory singleton = null ;
   
   /**
    * Constructor
    */
   public UploadSalesQuoteProcessFactory() {
       super();
   }

   public abstract UploadSalesQuoteProcess create() throws TopazException;
   
   
   public static UploadSalesQuoteProcessFactory singleton() {
       LogContext logCtx = LogContextFactory.singleton().getLogContext();

       if (UploadSalesQuoteProcessFactory.singleton == null) {
           String factoryClassName = null;
           try {
               factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                       UploadSalesQuoteProcessFactory.class.getName());
               Class factoryClass = Class.forName(factoryClassName);
               UploadSalesQuoteProcessFactory.singleton = (UploadSalesQuoteProcessFactory)factoryClass.newInstance();
           } catch (IllegalAccessException iae) {
               logCtx.error(QuoteProcessFactory.class, iae, iae.getMessage());
           } catch (ClassNotFoundException cnfe) {
               logCtx.error(QuoteProcessFactory.class, cnfe, cnfe.getMessage());
           } catch (InstantiationException ie) {
               logCtx.error(QuoteProcessFactory.class, ie, ie.getMessage());
           }
       }
       return singleton;
   }

}
