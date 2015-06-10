package com.ibm.dsw.quote.configurator.process.jdbc;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;

public class SaasConfiguratorNewNoCaNoCotermProcess extends AbstractSaasConfiguratorNewCaProcess {


	@Override
	protected boolean shouldCoTerm() {
		return false;
	}

	@Override
	protected CotermParameter processForCoterm(List configuratorPartList, String chrgAgrmtNum, String configrtnId,
			Map configuratorPartMap, String actionCode) throws QuoteException {
		// for new-no-ca-no-coterm scenario, don't need to do coterm
		return null;
	}

}
