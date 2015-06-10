/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.util.Map;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: RampUpConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 11:47:29 AM
 * 
 */
public class MonthlySwRampUpOverageConfiguratorPart extends MonthlySwConfiguratorPart {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MonthlySwRampUpSubscriptionConfiguratorPart rampUpSubscriptionPart;

	public MonthlySwRampUpOverageConfiguratorPart(String partNum) {
		this.partNum = partNum;
	}

	public boolean hasRalatedRampUpSubscriptionPart() {
		return rampUpSubscriptionPart != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart#
	 * addPartToQuote(com.ibm.dsw.quote.configurator
	 * .contract.SubmittedMonthlySwConfiguratorContract,
	 * com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
	 */
	@Override
	public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
            throws TopazException {
        MonthlySwLineItem qli = super.addPartToQuote(submitContract, configuratorPartFromPage, configuratorPartFromDB);
        qli.setMonthlySwPart(true);
		qli.setRampUp(true);
        qli.setUpdateSectionFlag(true);
        qli.setAdditionSectionFlag(false);
		qli.setMonthlySwSubscrptnOvragePart(true);
		qli.setConfigrtnId(submitContract.getConfigurtnId());
		return qli;
	}

	@Override
	public void update(MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlySwLineItemsMap,
			SubmittedMonthlySwConfiguratorContract submitContract)
			throws TopazException {
		MonthlySwLineItem lineItem = super.getNeedUpdateMonthlySwLineItem(monthlySwLineItemsMap);
		if (lineItem != null) {
			lineItem.setBillgFrqncyCode(configuratorPartFromPage.getBillingFrequencyCode());
			lineItem.setPartQty(configuratorPartFromPage.getPartQty());
			lineItem.setICvrageTerm(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpDuration());
			lineItem.setRampUpPeriodNum(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpPeriod());
		}
	}

	public MonthlySwRampUpSubscriptionConfiguratorPart getRampUpSubscriptionPart() {
		return rampUpSubscriptionPart;
	}

	public void setRampUpSubscriptionPart(MonthlySwRampUpSubscriptionConfiguratorPart rampUpSubscriptionPart) {
		this.rampUpSubscriptionPart = rampUpSubscriptionPart;
	}

}
