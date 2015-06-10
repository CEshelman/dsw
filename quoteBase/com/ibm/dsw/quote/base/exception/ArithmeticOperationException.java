package com.ibm.dsw.quote.base.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;

public class ArithmeticOperationException extends
		PriceEngineUnAvailableException {
	
	private boolean isArithmeticOperationIssue = false;
	
	private String arithmeticOperationIssueMsg;
	
	public ArithmeticOperationException(boolean isArithmeticOperationIssue,String arithmeticOperationIssueMsg){
		this.isArithmeticOperationIssue = isArithmeticOperationIssue;
		this.arithmeticOperationIssueMsg = arithmeticOperationIssueMsg;
		
		if (isArithmeticOperationIssue){
			messageKey = ErrorKeys.MSG_PRICE_ENGINE_UNAVAILABLE_FOR_ARITHMETIC_OPERATION_ISSUE;
		}
		
	}

	public boolean isArithmeticOperationIssue() {
		return isArithmeticOperationIssue;
	}

	public String getArithmeticOperationIssueMsg() {
		return arithmeticOperationIssueMsg;
	}
	
	

}
