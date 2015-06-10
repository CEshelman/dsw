/*
 * Created on 2007-10-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;


/**
 * @author Leon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LOBListUtil {
	
	private static final Map lobsMap = new HashMap();
	
	public static List getLobs(Locale locale, String audienceCode){
		if(!lobsMap.containsKey(locale.getLanguage())){
			List lobs = new ArrayList();
			
			String pa_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PA);
			String pa_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PA_DESC);
			lobs.add(new CodeDescObj_jdbc(pa_code, pa_desc));
			
			String pae_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAE);
			String pae_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAE_DESC);
			lobs.add(new CodeDescObj_jdbc(pae_code, pae_desc));
			
			if(!QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(audienceCode)){
				String ppss_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PPSS);
				String ppss_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PPSS_DESC);
				lobs.add(new CodeDescObj_jdbc(ppss_code, ppss_desc));
				
				String ssp_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_SSP);
				String ssp_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_SSP_DESC);
				lobs.add(new CodeDescObj_jdbc(ssp_code, ssp_desc));
				
				String fct_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_FCT);
				String fct_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_FCT_DESC);
				lobs.add(new CodeDescObj_jdbc(fct_code, fct_desc));
				
				String fctToPA_code = QuoteConstants.MIGRTN_CODE_FCT_TO_PA;
				String fctToPA_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAUN_DESC);
				lobs.add(new CodeDescObj_jdbc(fctToPA_code, fctToPA_desc));
				
				String oem_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_OEM);
				String oem_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_OEM_DESC);
				lobs.add(new CodeDescObj_jdbc(oem_code, oem_desc));
			}
			lobsMap.put(locale.getLanguage(), lobs);
		}
		return (List)lobsMap.get(locale.getLanguage());
	}
}
