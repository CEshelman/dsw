package com.ibm.dsw.quote.audit.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditProcessFactory<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public abstract class QuoteAuditProcessFactory {
	private static QuoteAuditProcessFactory singleton = null;
	private final static Object synObject = new Object();
	
	public abstract QuoteAuditProcess create() throws QuoteException;
	
	public static QuoteAuditProcessFactory singleton(){
		synchronized (QuoteAuditProcessFactory.synObject) {
	        GlobalContext globalCtx = GlobalContext.singleton();
	        LogContext logCtx = LogContextFactory.singleton().getLogContext();
	        if(QuoteAuditProcessFactory.singleton == null){
	        	String factoryClassName = null;
	            try {
	                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
	                		QuoteAuditProcessFactory.class.getName());
	                Class factoryClass = Class.forName(factoryClassName);
	                QuoteAuditProcessFactory.singleton = (QuoteAuditProcessFactory) factoryClass.newInstance();
	            } catch (IllegalAccessException iae) {
	                logCtx.error(QuoteAuditProcessFactory.class, iae, iae.getMessage());
	            } catch (ClassNotFoundException cnfe) {

	                logCtx.error(QuoteAuditProcessFactory.class, cnfe, cnfe.getMessage());
	            } catch (InstantiationException ie) {
	                logCtx.error(QuoteAuditProcessFactory.class, ie, ie.getMessage());
	            }
	        }
		}
		return QuoteAuditProcessFactory.singleton;
	}
}
