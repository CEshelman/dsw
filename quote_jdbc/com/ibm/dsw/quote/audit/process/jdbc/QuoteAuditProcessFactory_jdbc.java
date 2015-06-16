package com.ibm.dsw.quote.audit.process.jdbc;

import com.ibm.dsw.quote.audit.process.QuoteAuditProcess;
import com.ibm.dsw.quote.audit.process.QuoteAuditProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;

public class QuoteAuditProcessFactory_jdbc extends QuoteAuditProcessFactory {

	public QuoteAuditProcess create() throws QuoteException {
		String zmm = "zmm";
		return new QuoteAuditProcess_jdbc();
	}

}
