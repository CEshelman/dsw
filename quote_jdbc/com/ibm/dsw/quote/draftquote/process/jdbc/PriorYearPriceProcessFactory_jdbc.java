package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcessFactory;

/**
 * 
 * @author jason
 *
 */
public class PriorYearPriceProcessFactory_jdbc extends PriorYearPriceProcessFactory{

	public PriorYearPriceProcess create() throws QuoteException {
		return new PriorYearPriceProcess_jdbc();
	}

}
