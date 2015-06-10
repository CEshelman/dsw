package com.ibm.dsw.quote.base.exception;

import java.util.HashMap;

public class CustomerCreateWebServiceFailureException extends WebServiceException {
	
    /**
     * 
     */
	
	HashMap vMap = null;
	
	public HashMap getVMap(){
		
		return this.vMap = vMap; 
	}
	
    public CustomerCreateWebServiceFailureException() {
        super();
    }
    
    public CustomerCreateWebServiceFailureException(String message, String messageKey) {
        super(message, messageKey);
    }
    
    public CustomerCreateWebServiceFailureException(String message, String messageKey, int serviceType, HashMap vMap) {
    	
    	super (message, messageKey, serviceType);
    	this.vMap = vMap;
    }
    
    public CustomerCreateWebServiceFailureException(String message, String messageKey, Throwable cause) {
        super(message, messageKey, cause);
    }

}
