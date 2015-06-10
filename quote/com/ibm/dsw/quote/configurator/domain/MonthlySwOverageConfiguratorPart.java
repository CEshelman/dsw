/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * @ClassName: OverageConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 11:42:05 AM
 *
 */
public class MonthlySwOverageConfiguratorPart extends MonthlySwConfiguratorPart {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MonthlySwSubscrptnConfiguratorPart subscrptnPart;
	
	private MonthlySwDailyConfiguratorPart dailyPart;

	public MonthlySwSubscrptnConfiguratorPart getSubscrptnPart() {
		return subscrptnPart;
	}

	public void setSubscrptnPart(MonthlySwSubscrptnConfiguratorPart subscrptnPart) {
		this.subscrptnPart = subscrptnPart;
	}

	public MonthlySwDailyConfiguratorPart getDailyPart() {
		return dailyPart;
	}

	public void setDailyPart(MonthlySwDailyConfiguratorPart dailyPart) {
		this.dailyPart = dailyPart;
	}
	
	public boolean hasRelatedSubscrptnPart(){
		return subscrptnPart != null;
	}
	
	public boolean hasRelatedDailyPart(){
		return dailyPart != null;
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart#addPartToQuote(com.ibm.dsw.quote.configurator
     * .contract.SubmittedMonthlySwConfiguratorContract,
     * com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
     */
    @Override
    public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
            throws TopazException {
        MonthlySwLineItem monthlySwLineItem = super.addPartToQuote(submitContract, configuratorPartFromPage,
                configuratorPartFromDB);
        monthlySwLineItem.setMonthlySwPart(true);
        monthlySwLineItem.setMonthlySwSubscrptnOvragePart(true);
		monthlySwLineItem.setUpdateSectionFlag(true);
        return monthlySwLineItem;
    }

}
