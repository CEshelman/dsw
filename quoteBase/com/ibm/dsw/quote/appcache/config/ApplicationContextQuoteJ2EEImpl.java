package com.ibm.dsw.quote.appcache.config;

import com.ibm.dsw.quote.appcache.util.PropertyConfigUtil;

public class ApplicationContextQuoteJ2EEImpl extends com.ibm.ead4j.jade.config.ApplicationContextJ2EEImpl{
	public String getConfigParameter(String key) {
		String propertyConfig = super.getConfigParameter(key);
		if (null == propertyConfig){
			propertyConfig = PropertyConfigUtil.getUrlConfigFromCacheByName(key);
		}
		return propertyConfig;
	}
}
