package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.EvalProcess;
import com.ibm.dsw.quote.draftquote.process.EvalProcessFactory;

public class EvalProcessFactory_jdbc extends EvalProcessFactory {

	@Override
	public EvalProcess create() throws QuoteException {
		// TODO Auto-generated method stub
		return new EvalProcess_jdbc();
	}

}
