package com.ibm.dsw.quote.configurator.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcess;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcessFactory;

public class SaasConfiguratorProcessFactory_jdbc extends SaasConfiguratorProcessFactory {

	@Override
	public SaasConfiguratorProcess create(String configrtnActionCode) throws QuoteException {

		SaasConfiguratorProcess saasSwConfigProcess = null;

		if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtnActionCode)) {
			saasSwConfigProcess = new SaasConfiguratorAddonTradeupProcess();

		} else if (PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(configrtnActionCode)) {
			saasSwConfigProcess = new SaasConfiguratorFctToPaProcess();

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(configrtnActionCode)) {
			saasSwConfigProcess = new SaasConfiguratorNewCaCotermProcess();

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT.equals(configrtnActionCode)) {
			saasSwConfigProcess = new SaasConfiguratorNewCaNoCotermProcess();

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_NCT.equals(configrtnActionCode)) {
			saasSwConfigProcess = new SaasConfiguratorNewNoCaNoCotermProcess();

		} else {
			// New CA
			saasSwConfigProcess = new SaasConfiguratorNewNoCaNoCotermProcess();
		}

		return saasSwConfigProcess;
	}

}
