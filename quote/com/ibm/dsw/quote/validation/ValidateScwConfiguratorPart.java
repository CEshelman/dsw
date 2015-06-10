package com.ibm.dsw.quote.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.scw.addon.ScwAddonTradeUpErrorCode;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

public class ValidateScwConfiguratorPart extends ValidateConfiguratorPart {

	public ValidateScwConfiguratorPart() {
		this.scwAddonTradeUpErrorCodeList = new ArrayList<ScwAddonTradeUpErrorCode>();
	}

	@Override
	protected void processErrorMsg(String msg, String arg, String key,
			AddOrUpdateConfigurationContract contract) {
		assembleSCWErrorMsg(msg, key);

	}

	@Override
	protected void processErrorMsg(String msg, String[] args,
			boolean[] isArgsResource, String key,
			AddOrUpdateConfigurationContract contract) {
		assembleSCWErrorMsg(msg, key, args);

	}
	
	@Override
	public boolean isValidateFrequencyCode(){
		return true;
	}

	/**
	 * the method is to put the errorCode and errorCodeDesc to
	 * scwAddonTradeUpErrorCodeList for SCW
	 * 
	 * @param msg
	 * @param key
	 * @param args
	 */
	
	private void assembleSCWErrorMsg(String msg, String key, Object... args) {
		int firstUnderlineIndex = key.indexOf("_");
		int lastUnderlineIndex = key.lastIndexOf("_");
		String partNum = key.substring(firstUnderlineIndex + 1,
				lastUnderlineIndex);
		ScwAddonTradeUpErrorCode scwAddonTradeUpErrorCode = new ScwAddonTradeUpErrorCode();
		Locale locale = LocaleHelperImpl.getDefaultDSWLocale();
		// get the errorCode
		String errorCode = ApplicationContextFactory
				.singleton()
				.getApplicationContext()
				.getI18nValueAsString(I18NBundleNames.CONFIGURATOR_SCW_ADDON_MESSAGE,
						locale, msg);

		String errorCodeMsg = ApplicationContextFactory
				.singleton()
				.getApplicationContext()
				.getI18nValueAsString(I18NBundleNames.SCW_ADDON_MESSAGE,
						locale, errorCode);
		// get the errorCodeDesc
		String errorCodeDesc = MessageFormat.format(errorCodeMsg, args);
		scwAddonTradeUpErrorCode.setErrorCode(errorCode);
		scwAddonTradeUpErrorCode
				.setErrorCodeDesc(partNum + "-" + errorCodeDesc);
		scwAddonTradeUpErrorCodeList.add(scwAddonTradeUpErrorCode);
	}
}
