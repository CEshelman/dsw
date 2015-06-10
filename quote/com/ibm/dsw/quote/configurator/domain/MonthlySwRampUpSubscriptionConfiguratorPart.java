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
public class MonthlySwRampUpSubscriptionConfiguratorPart extends MonthlySwConfiguratorPart {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MonthlySwSubscrptnConfiguratorPart subscriptnPart;

	private MonthlySwRampUpSubscriptionConfiguratorPart nextRampUpSubscriptionPart;

	private MonthlySwRampUpOverageConfiguratorPart rampUpoveragePart;

	private String rampUpDuration;

	public MonthlySwSubscrptnConfiguratorPart getSubscriptnPart() {
		return subscriptnPart;
	}

	public void setSubscriptnPart(MonthlySwSubscrptnConfiguratorPart subscriptnPart) {
		this.subscriptnPart = subscriptnPart;
	}

	public boolean hasRelatedSubscriptionPart() {
		return subscriptnPart != null;
	}

	public boolean hasRelatedNextRampUpSubscriptionPart() {
		return nextRampUpSubscriptionPart != null;
	}

	public boolean hasRelatedRampUpoveragePart() {
		return rampUpoveragePart != null;
	}

	public String getRampUpDuration() {
		return getTerm() + "";
	}

	public void setRampUpDuration(String rampUpDuration) {
		this.rampUpDuration = rampUpDuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart#
	 * addPartToQuote(com.ibm.dsw.quote.configurator
	 * .contract.SubmittedMonthlySwConfiguratorContract,
	 * com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
	 */
	public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract,
			MonthlySwConfiguratorPart configuratorPartFromPage, boolean hasOveragePart, String overagePartNum)
			throws TopazException {
        MonthlySwLineItem lineItem = super.addPartToQuote(submitContract, configuratorPartFromPage, null);
		if (lineItem != null) {
            lineItem.setMonthlySwPart(true);
            lineItem.setPartQty(configuratorPartFromPage.getPartQty());
			lineItem.setICvrageTerm(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpDuration());
			lineItem.setRampUpPeriodNum(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpPeriod());
			lineItem.setRampUp(true);
			lineItem.setMonthlySwSubscrptnPart(true);
            lineItem.setMonthlySwPart(true);
            lineItem.setUpdateSectionFlag(true);
            lineItem.setAdditionSectionFlag(false);
			lineItem.setMonthlySwTcvAcv(true);
			lineItem.setConfigrtnId(submitContract.getConfigurtnId());
            lineItem.setCumCvrageTerm(subscriptnPart.getTotalTerm());
			
			if (hasOveragePart) {
				MonthlySwRampUpOverageConfiguratorPart rampUpOveragePart = new MonthlySwRampUpOverageConfiguratorPart(
						overagePartNum);
                MonthlySwLineItem overageLineItem = rampUpOveragePart.addPartToQuote(submitContract, configuratorPartFromPage,
                        null);
				overageLineItem.setIRelatedLineItmNum(lineItem.getDestSeqNum());
				overageLineItem.setRampUpPeriodNum(lineItem.getRampUpPeriodNum());
			}
		}
		return lineItem;
	}

    @Override
	public void update(MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlySwLineItemsMap,
			SubmittedMonthlySwConfiguratorContract submitContract)
			throws TopazException {
		MonthlySwLineItem lineItem = super.getNeedUpdateMonthlySwLineItem(monthlySwLineItemsMap);
		if (lineItem != null) {
			lineItem.setPartQty(configuratorPartFromPage.getPartQty());
			lineItem.setICvrageTerm(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpDuration());
            lineItem.setCumCvrageTerm(subscriptnPart.getTotalTerm());
			lineItem.setRampUpPeriodNum(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpPeriod());
		}
	}

	public void deleteFromQuote(Map<String, MonthlySwLineItem> monthlySwLineItems) throws TopazException {
		super.deleteFromQuote(monthlySwLineItems);
		if (hasRelatedRampUpoveragePart()) {
			rampUpoveragePart.deleteFromQuote(monthlySwLineItems);
		}
	}

	public MonthlySwRampUpOverageConfiguratorPart getRampUpoveragePart() {
		return rampUpoveragePart;
	}

	public void setRampUpoveragePart(MonthlySwRampUpOverageConfiguratorPart rampUpoveragePart) {
		this.rampUpoveragePart = rampUpoveragePart;
	}

	public MonthlySwRampUpSubscriptionConfiguratorPart getNextRampUpSubscriptionPart() {
		return nextRampUpSubscriptionPart;
	}

	public void setNextRampUpSubscriptionPart(MonthlySwRampUpSubscriptionConfiguratorPart nextRampUpSubscriptionPart) {
		this.nextRampUpSubscriptionPart = nextRampUpSubscriptionPart;
	}

}
