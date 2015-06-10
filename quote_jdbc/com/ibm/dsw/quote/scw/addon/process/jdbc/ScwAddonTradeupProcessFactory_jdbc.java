package com.ibm.dsw.quote.scw.addon.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.scw.addon.process.ScwAddonTradeupProcess;
import com.ibm.dsw.quote.scw.addon.process.ScwAddonTradeupProcessFactory;

public class ScwAddonTradeupProcessFactory_jdbc extends ScwAddonTradeupProcessFactory {
	
	public ScwAddonTradeupProcessFactory_jdbc() {
		super();
	}

	@Override
	public ScwAddonTradeupProcess create() throws QuoteException {
		return new ScwAddonTradeupProcess_jdbc();
	}

}
