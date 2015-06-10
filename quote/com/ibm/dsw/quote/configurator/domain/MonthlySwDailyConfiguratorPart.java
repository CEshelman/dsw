/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: DailyConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 11:42:45 AM
 *
 */
public class MonthlySwDailyConfiguratorPart extends MonthlySwConfiguratorPart {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MonthlySwSubscrptnConfiguratorPart subscrptnPart;
	
	private MonthlySwOverageConfiguratorPart oveargePart;

	public MonthlySwSubscrptnConfiguratorPart getSubscrptnPart() {
		return subscrptnPart;
	}

	public void setSubscrptnPart(MonthlySwSubscrptnConfiguratorPart subscrptnPart) {
		this.subscrptnPart = subscrptnPart;
	}

	public MonthlySwOverageConfiguratorPart getOveargePart() {
		return oveargePart;
	}

	public void setOveargePart(MonthlySwOverageConfiguratorPart oveargePart) {
		this.oveargePart = oveargePart;
	}
	 
	public boolean hasRealtedSubscriptnPart(){
		return subscrptnPart != null;
	}

	public boolean hasRelatedDailyPart(){
		return oveargePart != null;
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
        monthlySwLineItem.setMonthlySwDailyPart(true);
        monthlySwLineItem.setUpdateSectionFlag(configuratorActionPart.isUpdatedMonthly());
        monthlySwLineItem.setAdditionSectionFlag(configuratorActionPart.isAddionalMonthly());

        return monthlySwLineItem;
    }
	
}
