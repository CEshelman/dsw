/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlySwConfiguratorJDBC
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 2:53:28 PM
 *
 */
public interface MonthlySwConfiguratorJDBC {
	
	/**
	 * 
	 * @param webQuoteNum
	 * @param caNum
	 * @return
	 */
	public List<MonthlySwConfiguratorPart> getMonthlySwConfgiuratorParts(
			String webQuoteNum, String caNum , String configId, String userId) throws TopazException;
	
	public Map<String,String> searchRestrictedMonthlyParts(String searchString, String webQuoteNum) throws TopazException;
}
