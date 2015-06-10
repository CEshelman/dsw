package com.ibm.dsw.quote.configurator.process.jdbc;


public class SaasConfiguratorNewCaCotermProcess extends AbstractSaasConfiguratorNewCaProcess {


	@Override
	protected boolean shouldCoTerm() {
		return true;
	}

}
