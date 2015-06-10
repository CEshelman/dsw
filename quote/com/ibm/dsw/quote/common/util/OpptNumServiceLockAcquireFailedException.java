package com.ibm.dsw.quote.common.util;

public class OpptNumServiceLockAcquireFailedException extends Exception {
	public OpptNumServiceLockAcquireFailedException(Throwable e){
		super(e);		
	}
	public OpptNumServiceLockAcquireFailedException(String errorMsg,Throwable e){
		super(errorMsg,e);		
	}
	public OpptNumServiceLockAcquireFailedException(){
		super();
	}
	public OpptNumServiceLockAcquireFailedException(String errorMsg){
		super(errorMsg);
	}
}
