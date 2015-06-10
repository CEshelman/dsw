package com.ibm.dsw.quote.loadtest.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.loadtest.process.SqoLoadTestProcess;
import com.ibm.dsw.quote.loadtest.process.SqoLoadTestProcessFactory;

/**
 * @author julia.liu
 *
 */
public class SqoLoadTestProcessFactory_jdbc extends SqoLoadTestProcessFactory {

	public SqoLoadTestProcessFactory_jdbc() {
		super();
	}
	
	public SqoLoadTestProcess create() throws QuoteException {
		return new SqoLoadTestProcess_jdbc();
	}

}
