package com.ibm.dsw.quote.loadtest.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.topaz.exception.TopazException;

public interface SqoLoadTestProcess {

	public StringBuffer loadtest() throws TopazException, QuoteException; 
	
}
