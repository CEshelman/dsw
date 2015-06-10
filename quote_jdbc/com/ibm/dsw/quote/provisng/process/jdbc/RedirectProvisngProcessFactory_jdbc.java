package com.ibm.dsw.quote.provisng.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.provisng.process.RedirectProvisngProcess;
import com.ibm.dsw.quote.provisng.process.RedirectProvisngProcessFactory;

public class RedirectProvisngProcessFactory_jdbc extends
		RedirectProvisngProcessFactory {

	@Override
	public RedirectProvisngProcess create() throws QuoteException {
		return new RedirectProvisngProcess_jdbc();
	}

}
