package com.ibm.dsw.quote.opportunity.process;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2012-02-15
 */
public abstract class OpportunityCommonProcessFactory {

   private static OpportunityCommonProcessFactory singleton = null ;
   
   /**
    * Constructor
    */
   public OpportunityCommonProcessFactory() {
       super();
   }

   public abstract OpportunityCommonProcess create();
   
   
   public static OpportunityCommonProcessFactory singleton() {
       LogContext logCtx = LogContextFactory.singleton().getLogContext();

       if (OpportunityCommonProcessFactory.singleton == null) {
           String factoryClassName = null;
           try {
               factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                       OpportunityCommonProcessFactory.class.getName());
               Class factoryClass = Class.forName(factoryClassName);
               OpportunityCommonProcessFactory.singleton = (OpportunityCommonProcessFactory)factoryClass.newInstance();
           } catch (IllegalAccessException iae) {
               logCtx.error(OpportunityCommonProcessFactory.class, iae, iae.getMessage());
           } catch (ClassNotFoundException cnfe) {
               logCtx.error(OpportunityCommonProcessFactory.class, cnfe, cnfe.getMessage());
           } catch (InstantiationException ie) {
               logCtx.error(OpportunityCommonProcessFactory.class, ie, ie.getMessage());
           }
       }
       return singleton;
   }

}
