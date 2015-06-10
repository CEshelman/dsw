package com.ibm.dsw.quote.common.service;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteWSHandler<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 9, 2007
 */

public class QuoteWSHandler extends GenericHandler {
    
    private static  LogContext logContext = LogContextFactory.singleton().getLogContext();

	public QName[] getHeaders() {
		// Fill in method body
		return new QName[0];
	}

	public boolean handleRequest(MessageContext context) {
		// Fill in method body or delete method to use GenericHandler
		this.logMessage(context, "REQUEST: " + context.getProperty("javax.xml.rpc.service.endpoint.address"));

		return true;
	}

	public boolean handleResponse(MessageContext context) {
		// Fill in method body or delete method to use GenericHandler
		this.logMessage(context, "RESPONSE: ");

		return true;
	}

	protected void logMessage(MessageContext context, String msg) {
		
		javax.xml.soap.SOAPMessage message = ((javax.xml.rpc.handler.soap.SOAPMessageContext) context)
				.getMessage();
		try {
			
		    logContext.info(this, msg);
		    logContext.info(this, message.getSOAPBody().toString());			
						
		} catch (Exception e) {
			logContext.debug(this, e.getMessage());
		}
	}


}
