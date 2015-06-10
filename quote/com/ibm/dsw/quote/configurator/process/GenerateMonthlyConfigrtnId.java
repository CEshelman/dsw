package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.draftquote.util.date.DateUtil;

public class GenerateMonthlyConfigrtnId {
	public static String getConfigurtnId(String webQuoteNum) {
		return webQuoteNum + DateUtil.formatDate(DateUtil.getCurrentDate(), DateUtil.PATTERN4);
	}
}
