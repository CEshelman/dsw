package com.ibm.dsw.quote.submittedquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.submittedquote.process.PriorYearPriceProcess;
import com.ibm.dsw.quote.submittedquote.process.PriorYearPriceProcessFactory;

/**
 * 
 * @author J Zhang
 *
 */
public class PriorYearPriceProcessFactory_jdbc extends PriorYearPriceProcessFactory{

	public PriorYearPriceProcess create() throws QuoteException {
		return new PriorYearPriceProcess_jdbc();
	}
}
