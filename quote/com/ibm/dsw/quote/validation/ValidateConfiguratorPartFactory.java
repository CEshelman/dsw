package com.ibm.dsw.quote.validation;

import com.ibm.dsw.quote.validation.config.ValidateAddonTradeUpConstants;

public class ValidateConfiguratorPartFactory {
	private static ValidateConfiguratorPartFactory singleten = new ValidateConfiguratorPartFactory();

	private ValidateConfiguratorPartFactory() {

	}

	public static ValidateConfiguratorPartFactory singleten() {
		return singleten;
	}

	public ValidateConfiguratorPart create(String validationType) {
		if (ValidateAddonTradeUpConstants.VALIDATE_BASIC_ADDON_TRADEUP.equals(validationType)) {
			return new ValidateBasicConfiguratorPart();
		} else if (ValidateAddonTradeUpConstants.VALIDATE_SCW_ADDON_TRADEUP.equals(validationType)) {
			return new ValidateScwConfiguratorPart();
		} else {
			return new ValidateBasicConfiguratorPart();
		}
	}
}
