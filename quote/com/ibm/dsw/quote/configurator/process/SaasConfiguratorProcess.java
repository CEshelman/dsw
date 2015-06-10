package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.ead4j.topaz.exception.TopazException;

public interface SaasConfiguratorProcess {

	public void addSaasPartsToQuote(AddOrUpdateConfigurationContract contract) throws TopazException, QuoteException;

}
