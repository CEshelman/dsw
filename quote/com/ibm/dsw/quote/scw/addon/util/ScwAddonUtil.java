package com.ibm.dsw.quote.scw.addon.util;

import java.util.Locale;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.scw.addon.ScwAddonTradeUpErrorCode;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

public class ScwAddonUtil {
	/**
	 * compute scw error bean from error code and error msg prefix
	 * @param errorCodeKey  get error code from this key 
	 * @param errorMsgPrefix  add this prefix to error msg 
	 * @return
	 */
	public static ScwAddonTradeUpErrorCode populateScwErrorBean(String errorCodeKey,String errorMsgPrefix){
		ScwAddonTradeUpErrorCode errorCodeBean=new ScwAddonTradeUpErrorCode();
		Locale locale = LocaleHelperImpl.getDefaultDSWLocale();
		// get the errorCode
		String errorCode = ApplicationContextFactory
				.singleton()
				.getApplicationContext()
				.getI18nValueAsString(I18NBundleNames.CONFIGURATOR_SCW_ADDON_MESSAGE,
						locale, errorCodeKey);
	
		String errorCodeMsg = ApplicationContextFactory
				.singleton()
				.getApplicationContext()
				.getI18nValueAsString(I18NBundleNames.SCW_ADDON_MESSAGE,
						locale, errorCode);
		// get the errorCodeDesc
		errorCodeBean.setErrorCode(errorCode);
		errorCodeBean.setErrorCodeDesc(errorMsgPrefix + "-" + errorCodeMsg);
		return errorCodeBean;
	}
	/**
	 * compute scw error bean from error code 
	 * @param errorCodeKey  get error code from this key 
	 * @return
	 */
	public static ScwAddonTradeUpErrorCode populateScwErrorBean(String errorCodeKey){		
		return populateScwErrorBean(errorCodeKey,"");
	}
}
