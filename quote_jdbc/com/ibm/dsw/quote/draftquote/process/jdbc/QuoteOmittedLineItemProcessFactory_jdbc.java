package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcessFactory;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcess;

public class QuoteOmittedLineItemProcessFactory_jdbc extends QuoteOmittedLineItemProcessFactory{

	@Override
	public QuoteOmittedLineItemProcess create() throws QuoteException {
		// TODO Auto-generated method stub
		return new QuoteOmittedLineItemProcess_jdbc();
	}

}
