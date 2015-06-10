package com.ibm.dsw.quote.newquote.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Conversion helper. Convert any other "Quote Configuration" object to 
 * PartsPricingConfiguration type.
 * @see SpreadSheetMonthlyConfigurationAdapter
 * @author lirui
 *
 */
public class SpreadSheetQuoteConfigurationHelper {
	static LogContext logger = LogContextFactory.singleton().getLogContext();
	/**
	 * Convert the quote configuration objects of saas and monthly to 
	 * the ISpreadSheetQuoteConfiguration object.
	 * Will convert to configuration objects from <pre>{@code 
	 * quote.getPartsPricingConfigrtnsList()
	 * quote.getPartsPricingConfigrtnsMap(), 
	 * quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns(),
	 * quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap()
	 * }</pre><p>
	 * for the list data listed above, will convert all its elements,
	 * for the map data listed above, will convert all its keys.
	 * the convert result will be set to the param <pre>{@code epQuote:
	 * epQuote.setPartsPricingConfigrtnsList(..),
	 * epQuote.setPartsPricingConfigrtnsMap(..)
	 * }</pre>
	 * </p>
	 * @param quote 
	 * @param epQuote
	 */
	public static void preparePartsPricingConfigrtns(Quote quote,
			SpreadSheetQuote epQuote) {
		if(quote == null || epQuote == null) return;
		//14.2 MonthlyLicensing: Andy updated to compatibility of both SaaS and Monthly
		//Monthly:
		List<MonthlySoftwareConfiguration> monthlyCfgList = quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();//item:MonthlySoftwareConfiguration
		Map<MonthlySoftwareConfiguration, List<MonthlySwLineItem>> monthlyCfgMap = quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();//key:MonthlySoftwareConfiguration
		//SaaS:
		List<PartsPricingConfiguration> saasCfgList = quote.getPartsPricingConfigrtnsList();//item:PartsPricingConfiguration
		Map<PartsPricingConfiguration, List<QuoteLineItem>> saasCfgMap = quote.getPartsPricingConfigrtnsMap();//key:PartsPricingConfiguration

		//wrapped:
		List<PartsPricingConfiguration> cfgList = new ArrayList<PartsPricingConfiguration>();
		Map<PartsPricingConfiguration, List<QuoteLineItem>> cfgMap = new HashMap<PartsPricingConfiguration, List<QuoteLineItem>>();

		if(monthlyCfgList != null && monthlyCfgList.size() > 0 ){
			CfgConverter conveter = new GenericConverter(monthlyCfgList, monthlyCfgMap);
			conveter.convert();
			cfgList.addAll(conveter.getConvertedList());
			cfgMap.putAll(conveter.getConvertedMap());
		}
		if(saasCfgList != null && saasCfgList.size() > 0 ){
			CfgConverter conveter = new GenericConverter(saasCfgList, saasCfgMap);
			conveter.convert();
			cfgList.addAll(conveter.getConvertedList());
			cfgMap.putAll(conveter.getConvertedMap());
		}

		epQuote.setPartsPricingConfigrtnsList(cfgList);
		epQuote.setPartsPricingConfigrtnsMap(cfgMap);

	}
	
	/**
	 * convert the quote configuration object to PartsPricingConfiguration.
	 * Only support MonthlySoftwareConfiguration and PartsPricingConfiguration now.
	 * @param cfg
	 * @return
	 */
	public static PartsPricingConfiguration convertQuoteConfiguration(Object cfg){
		if(cfg == null) return null;
		if(cfg instanceof PartsPricingConfiguration){
			return (PartsPricingConfiguration)cfg;
		}
		else if(cfg instanceof MonthlySoftwareConfiguration){
			return new SpreadSheetMonthlyConfigurationAdapter((MonthlySoftwareConfiguration)cfg);
		}
		else{
			logger.error(SpreadSheetQuoteConfigurationHelper.class, 
					"Unsupported type of parameter:"+cfg.getClass());
			throw new RuntimeException("Unsupported type :"+cfg.getClass());
		}
	}
}

/**
 * Abstract quote configuration converter. 
 * Convert any other quote configuration object to PartsPricingConfiguration object.
 * @author lirui
 *
 */
abstract class CfgConverter{
	protected List<PartsPricingConfiguration> list;
	protected Map<PartsPricingConfiguration, List<QuoteLineItem>> map;
	public abstract void convert();
	/**get the conversion result*/
	public List<PartsPricingConfiguration> getConvertedList(){return list;}
	/**get the conversion result*/
	public Map<PartsPricingConfiguration, List<QuoteLineItem>> getConvertedMap(){return map;}
}

/**
 * Generic configuration type converter.
 * @author lirui
 * @param <K>
 * @param <E>
 */
class GenericConverter<K extends Object, E extends QuoteLineItem> extends CfgConverter{
	List<K> cfgList;
	Map<K, List<E>> cfgMap;
	//constructor
	GenericConverter(List<K> cfgList, 
			Map<K, List<E>> cfgMap){
		this.cfgList = cfgList;
		this.cfgMap = cfgMap;
		list = new ArrayList<PartsPricingConfiguration>();
		map = new HashMap<PartsPricingConfiguration, List<QuoteLineItem>>();
	}
	//execute conversion.
	public void convert() {
		if(cfgList == null || cfgList.size() == 0) return;
		//convert list
		for(K cfg : cfgList){
			PartsPricingConfiguration ppCfg = SpreadSheetQuoteConfigurationHelper.convertQuoteConfiguration(cfg);
			list.add(ppCfg);
			//convert map key
			if( cfgMap!= null && cfgMap.containsKey(cfg)){
				map.put(ppCfg, extractLineItemList(cfgMap.get(cfg)));
			}
		}
	}
	//copy the map entry's value.
	private List<QuoteLineItem> extractLineItemList( List itemList){
		List<QuoteLineItem> lineitemlist = new ArrayList<QuoteLineItem>(itemList.size());
		lineitemlist.addAll(itemList);
		return lineitemlist;
	}

}
