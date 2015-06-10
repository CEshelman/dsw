package com.ibm.dsw.quote.mail.process;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcessFactory</code> class is MassDlgtnProcess factory
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public abstract class MailProcessFactory {

    private static MailProcessFactory singleton = null;
	private final static Object synObject = new Object();
	
	public abstract MailProcess create();

    public static MailProcessFactory singleton() {
		synchronized (MailProcessFactory.synObject) {
	        GlobalContext globalCtx = GlobalContext.singleton();
	        LogContext logCtx = LogContextFactory.singleton().getLogContext();
	        if(MailProcessFactory.singleton == null){
	        	String factoryClassName = null;
	            try {
	                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
	                		MailProcessFactory.class.getName());
	                Class factoryClass = Class.forName(factoryClassName);
	                MailProcessFactory.singleton = (MailProcessFactory) factoryClass.newInstance();
	            } catch (IllegalAccessException iae) {
	                logCtx.error(MailProcessFactory.class, iae, iae.getMessage());
	            } catch (ClassNotFoundException cnfe) {

	                logCtx.error(MailProcessFactory.class, cnfe, cnfe.getMessage());
	            } catch (InstantiationException ie) {
	                logCtx.error(MailProcessFactory.class, ie, ie.getMessage());
	            }
	        }
		}
		return MailProcessFactory.singleton;
    }
}
