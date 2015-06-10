package com.ibm.dsw.quote.appcache.util;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.PropertyCacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;

public class PropertyConfigUtil {
	public static String getUrlConfigFromCacheByName(String name){
		String result = null;
		try {
			result = PropertyCacheProcessFactory.singleton().create().getPropertyConfigByName(name);
		} catch (QuoteException e) {
			e.printStackTrace();
		}
		
		return StringUtils.stripToEmpty(result);
	}
}
