/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * @ClassName: OnDemandConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 11:46:25 AM
 *
 */
public class MonthlySwOnDemandConfiguratorPart extends MonthlySwConfiguratorPart {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
            throws TopazException {
        MonthlySwLineItem monthlySwLineItem = super.addPartToQuote(submitContract, configuratorPartFromPage,
                configuratorPartFromDB);
		monthlySwLineItem.setMonthlySwPart(true);
		monthlySwLineItem.setMonthlySwOnDemandPart(true);
		monthlySwLineItem.setUpdateSectionFlag(true);
		return monthlySwLineItem;
	}

}
