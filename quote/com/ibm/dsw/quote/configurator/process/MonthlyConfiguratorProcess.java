/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;


import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.contract.BuildMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.contract.SearchRestrictedMonthlyPartsContract;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlyConfiguratorProcess
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 1:48:59 PM
 *
 */
public interface MonthlyConfiguratorProcess {
	
	public MonthlySwConfiguratorForm bulidMonthlyConfigurator(
			BuildMonthlySwConfiguratorContract buildContract)
			throws TopazException;
	
	public void addMonthlySwToQuote(
			SubmittedMonthlySwConfiguratorContract submitContract)
			throws TopazException,QuoteException;
	
	public Map<String, String> searchRestrictedMonthlyPart(
			SearchRestrictedMonthlyPartsContract searchRestrictedPartContract)
			throws TopazException,QuoteException;
}
