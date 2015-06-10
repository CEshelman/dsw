package com.ibm.dsw.quote.common.service;

import javax.xml.rpc.handler.MessageContext;

import org.apache.commons.lang.StringEscapeUtils;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class QuoteUnescapeHtmlWSHandler extends QuoteWSHandler {
	
	 private static  LogContext logContext = LogContextFactory.singleton().getLogContext();
	 
	 protected void logMessage(MessageContext context, String msg) {
			
			javax.xml.soap.SOAPMessage message = ((javax.xml.rpc.handler.soap.SOAPMessageContext) context)
					.getMessage();
			try {
				
			    logContext.info(this, msg);
			    logContext.info(this, StringEscapeUtils.unescapeHtml(message.getSOAPBody().toString()).replaceAll("\n\r","").replaceAll("\n",""));			
							
			} catch (Exception e) {
				logContext.debug(this, e.getMessage());
			}
		}

}
