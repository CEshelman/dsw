/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.action.CommonConfiguratorValidation;
import com.ibm.dsw.quote.configurator.action.MonthlySwAddTrdConfiguratorValidation;
import com.ibm.dsw.quote.configurator.action.MonthlySwNewCAConfiguratorValidation;

/**
 * @ClassName: MonCommConfiguratorValiProcessFactory
 * @author Linda
 * @Description: TODO
 * @date Jan 14, 2014
 * 
 */
public class MonCommConfiguratorValiProcessFactory {

	public static CommonConfiguratorValidation createConfigurator(String configrtnActionCode) throws QuoteException {

		CommonConfiguratorValidation commConfigValidation = null;

		if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtnActionCode)) {
			// Add on /trade up
			commConfigValidation = new MonthlySwAddTrdConfiguratorValidation();

		} else if (PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(configrtnActionCode)) {
			// FCT to PA

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(configrtnActionCode)) {
			// existing charge agreement , co-term

		} else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT.equals(configrtnActionCode)) {
			// existing charge agreement , no co-term

		} else {
			// New CA
			commConfigValidation = new MonthlySwNewCAConfiguratorValidation();
		}

		return commConfigValidation;
	}

}
