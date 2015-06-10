package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.QuoteToUProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteToUProcessFactory;

public class QuoteToUProcessFactory_jdbc extends QuoteToUProcessFactory{

	@Override
	public QuoteToUProcess create() throws QuoteException {
		
		return new QuoteToUProcess_jdbc();
	}

}
