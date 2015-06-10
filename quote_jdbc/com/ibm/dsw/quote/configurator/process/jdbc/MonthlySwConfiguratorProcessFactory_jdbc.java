/**
 * 
 */
package com.ibm.dsw.quote.configurator.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorNewCaProcess;
import com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcess;
import com.ibm.dsw.quote.configurator.process.MonthlySwConfiguratorAddonProcess;
import com.ibm.dsw.quote.configurator.process.MonthlySwConfiguratorProcessFactory;

/**
 * @ClassName: MonthlySwConfiguratorFactory_jdbc
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 4:24:47 PM
 * 
 */
public class MonthlySwConfiguratorProcessFactory_jdbc extends
		MonthlySwConfiguratorProcessFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcessFactory
	 * #createConfiguratorProcess(java.lang.String)
	 */
	@Override
	public MonthlyConfiguratorProcess createConfiguratorProcess(
			String configrtnActionCode) throws QuoteException {

		MonthlyConfiguratorProcess monthlySwConfigProcess = null;

		if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD
				.equals(configrtnActionCode)) {
			monthlySwConfigProcess = new MonthlySwConfiguratorAddonProcess(new MonthlySwAddonConfigurator_jdbc());

		} else if (PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL
				.equals(configrtnActionCode)) {
			// FCT to PA

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT
				.equals(configrtnActionCode)) {
			// existing charge agreement , co-term

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT
				.equals(configrtnActionCode)) {
			// existing charge agreement , no co-term

		} else {
			// New CA
			monthlySwConfigProcess = new MonthlyConfiguratorNewCaProcess(
					new MonthlySwNewCAConfigurator_jdbc());
		}

		return monthlySwConfigProcess;
	}

}
