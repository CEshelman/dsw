package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.ead4j.topaz.exception.TopazException;

public class MonthlySwQuoteDomain {
	
	private transient List<MonthlySwLineItem> monthlySoftwares = new ArrayList();
	private transient List<MonthlySwLineItem> masterMonthlySwLineItems = new ArrayList();
	private transient List<MonthlySoftwareConfiguration> monthlySwConfgrtns = new ArrayList();
	private transient Map<MonthlySoftwareConfiguration, List<MonthlySwLineItem>> monthlySwConfigrtnsMap = new HashMap();
	
	public List<MonthlySwLineItem> getMonthlySoftwares() {
		return monthlySoftwares;
	}
	private void setMonthlySoftwares(List<MonthlySwLineItem> monthlySoftwares) {
		this.monthlySoftwares = monthlySoftwares;
	}
	
	public List<MonthlySwLineItem> getMasterMonthlySwLineItems() {
		return masterMonthlySwLineItems;
	}
	private void setMasterMonthlySwLineItems(
			List<MonthlySwLineItem> masterMonthlySwLineItems) {
		this.masterMonthlySwLineItems = masterMonthlySwLineItems;
	}
	
	public Map<MonthlySoftwareConfiguration, List<MonthlySwLineItem>> getMonthlySwConfigrtnsMap() {
		return monthlySwConfigrtnsMap;
	}
	private void setMonthlySwConfigrtnsMap(
			Map<MonthlySoftwareConfiguration, List<MonthlySwLineItem>> monthlySwConfigrtnsMap) {
		this.monthlySwConfigrtnsMap = monthlySwConfigrtnsMap;
	}
	
	public List<MonthlySoftwareConfiguration> getMonthlySwConfgrtns() {
		return monthlySwConfgrtns;
	}
	private void setMonthlySwConfgrtns(
			List<MonthlySoftwareConfiguration> monthlySwConfgrtns) {
		this.monthlySwConfgrtns = monthlySwConfgrtns;
	}

    /**
     * @param allLineItems <br>
     * 1)fill in all monthly software parts <br>
     * 2)build for master monthly software parts
     * @throws TopazException
     */
	public void fillMonthlySwLineItems(List<QuoteLineItem> allLineItems) throws TopazException {
		if(allLineItems == null || allLineItems.size() == 0){
			return;
		}
		List<MonthlySwLineItem> monthlySoftwares = new ArrayList();
		for (Iterator iterator = allLineItems.iterator(); iterator.hasNext();) {
			QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
			if(quoteLineItem.isMonthlySoftwarePart()){
				monthlySoftwares.add((MonthlySwLineItem)quoteLineItem);
			}
		}
		this.setMonthlySoftwares(monthlySoftwares);
		buildMonthlySwLineItemsWithRampUp();
		buildMasterMonthlySwLineItemList();
	}

    /**
     * set master monthly software part ramp-up line items list
     * 
     * @throws TopazException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void buildMonthlySwLineItemsWithRampUp() throws TopazException {
		if ((this.monthlySoftwares == null) || (this.monthlySoftwares.size() == 0)) {
            return;
        }

        // first, put all items in a map with seq num as its key;
		Map<String, List<MonthlySwLineItem>> itemsMap = new HashMap();
        for (Iterator itemIt = this.monthlySoftwares.iterator(); itemIt.hasNext();) {
            QuoteLineItem item = (QuoteLineItem) itemIt.next();
            if(!item.isRampupPart()){
            	continue;
            }
			String key = new String(item.getPartNum());
			if (itemsMap.get(key) == null) {
				List rampUpList = new ArrayList();
				rampUpList.add((MonthlySwLineItem) item);
				itemsMap.put(key, rampUpList);
			} else {
				itemsMap.get(key).add((MonthlySwLineItem) item);
			}
        }
		if (!itemsMap.isEmpty()) {
			for (Iterator itemIt = this.monthlySoftwares.iterator(); itemIt.hasNext();) {
				MonthlySwLineItem currItem = (MonthlySwLineItem) itemIt.next();
				// if subscription part, loop the related ramp-up parts, then
				// add them to the ramp-up list
				if ((currItem.isMonthlySwSubscrptnPart() || currItem.isMonthlySwSubscrptnOvragePart())
						&& !currItem.isRampupPart() && !currItem.isReplacedPart()) {
					List<MonthlySwLineItem> newRampUpLineItems = itemsMap.get(currItem.getPartNum());
                    if (newRampUpLineItems != null) {
						currItem.setRampUpLineItems(newRampUpLineItems);
						// sort the ram-up parts by it's seqNum
                        List rampUpLineItems = currItem.getRampUpLineItems();
                        Collections.sort(rampUpLineItems, new Comparator() {
							@Override
							public int compare(Object o1, Object o2) {
								QuoteLineItem item1 = (QuoteLineItem) o1;
								QuoteLineItem item2 = (QuoteLineItem) o2;
								return item1.getSeqNum() - item2.getSeqNum();
							}
						});
                        // set the ramp-up period num by the sort order
                        // set iRelatedLineItmNum for Ramp Up parts and currItem
                        QuoteLineItem previousLineItem = null;
						for (int i = 0; i < rampUpLineItems.size(); i++) {
							QuoteLineItem rampUpQli = (QuoteLineItem) rampUpLineItems.get(i);
							rampUpQli.setRampUpPeriodNum(i + 1);

							if (currItem.isMonthlySwSubscrptnPart() && !currItem.isMonthlySwSubscrptnOvragePart()) {
								if (i == 0) {
									rampUpQli.setIRelatedLineItmNum(-1);
								} else {
									rampUpQli.setIRelatedLineItmNum(previousLineItem.getDestSeqNum());
								}

								if (i == rampUpLineItems.size() - 1) {
									currItem.setIRelatedLineItmNum(rampUpQli.getDestSeqNum());
								}
								previousLineItem = rampUpQli;
							}
                            }
						// set the sub over rage parts to the master over rage
						if (currItem.isMonthlySwSubscrptnOvragePart()) {
							currItem.setMasterOvrage(true);
						}
					}
				}
			}

        }
	}
	
	
	/**
	 * @param masterMonthlySwLineItems
	 * @param confgrtnList
	 * @return
	 * monthly software configuration map<MonthlySoftwareConfiguration,List<MonthlySwLineItem>>
	 */
	public Map<MonthlySoftwareConfiguration,List<MonthlySwLineItem>> getMonthlySwConfigurations(List masterMonthlySwLineItems, List confgrtnList){
		Map<MonthlySoftwareConfiguration,List<MonthlySwLineItem>> confgrnsMap = new HashMap();
		if((masterMonthlySwLineItems == null) || (masterMonthlySwLineItems.size() == 0)){
			return confgrnsMap;
		}
		if((confgrtnList == null) || (confgrtnList.size() == 0)){
			return confgrnsMap;
		}

		Iterator cnfgrtnIt = confgrtnList.iterator();
		while (cnfgrtnIt.hasNext()) {
			MonthlySoftwareConfiguration ppc = (MonthlySoftwareConfiguration) cnfgrtnIt.next();
			List<MonthlySwLineItem> qliList = new ArrayList();
			for (int j = 0; j < masterMonthlySwLineItems.size(); j++) {
				MonthlySwLineItem qli = (MonthlySwLineItem)masterMonthlySwLineItems.get(j);
				if((ppc.getConfigrtnId() != null) && ppc.getConfigrtnId().equals(qli.getConfigrtnId())){
					qliList.add(qli);
				}
			}
			confgrnsMap.put(ppc, qliList);
		}
		return confgrnsMap;
	}

	

	
    /**
     * set the master monthly software line items that exclude the ramp-up parts and sub overrage parts
     */
    private void buildMasterMonthlySwLineItemList(){
    	List masterMonthlySwLineItemList = new ArrayList();
    	if(this.monthlySoftwares == null || this.monthlySoftwares.size() == 0){
    		return;
    	}
    	masterMonthlySwLineItemList.addAll(this.monthlySoftwares);
    	for (Iterator iterator = masterMonthlySwLineItemList.iterator(); iterator.hasNext();) {
    		MonthlySwLineItem qli = (MonthlySwLineItem) iterator.next();
    		if(qli.isRampupPart() || (qli.isMonthlySwSubscrptnPart() && !qli.isMasterOvrage())){
    			iterator.remove();
    		}
		}
    	this.setMasterMonthlySwLineItems(masterMonthlySwLineItemList);
    }
    
    public void fillMonthlySwConfigurationForDraft(List<MonthlySoftwareConfiguration> confgrtnList){
    	if(confgrtnList == null || confgrtnList.size() == 0){
    		return;
    	}
    	this.setMonthlySwConfgrtns(confgrtnList);
    	this.setMonthlySwConfigrtnsMap(buildMonthlySwConfgrtnMapforDraft());
    }
    
    public void fillMonthlySwConfigurationForSubmit(List<MonthlySoftwareConfiguration> confgrtnList){
    	if(confgrtnList == null || confgrtnList.size() == 0){
    		return;
    	}
    	this.setMonthlySwConfgrtns(confgrtnList);
    	this.setMonthlySwConfigrtnsMap(buildMonthlySwConfgrtnMapforSubmit());
    }
    
	/**
	 * @param mastermonthlySwLineItems
	 * @param confgrtnList
	 * @return
	 * group by PID, PID description, configuration id
	 */
	private Map buildMonthlySwConfgrtnMapforDraft(){
		Map confgrnsMap = new HashMap();
		if((this.masterMonthlySwLineItems == null) || (this.masterMonthlySwLineItems.size() == 0)){
			return confgrnsMap;
		}
		if((this.monthlySwConfgrtns == null) || (this.monthlySwConfgrtns.size() == 0)){
			return confgrnsMap;
		}

		Iterator cnfgrtnIt = this.monthlySwConfgrtns.iterator();
		while (cnfgrtnIt.hasNext()) {
			MonthlySoftwareConfiguration ppc = (MonthlySoftwareConfiguration) cnfgrtnIt.next();
			List qliList = new ArrayList();
			for (int j = 0; j < this.masterMonthlySwLineItems.size(); j++) {
				MonthlySwLineItem qli = (MonthlySwLineItem)this.masterMonthlySwLineItems.get(j);
				if((ppc.getConfigrtnId() != null) && ppc.getConfigrtnId().equals(qli.getConfigrtnId())){
					qliList.add(qli);
				}
			}
			confgrnsMap.put(ppc, qliList);
		}
		return confgrnsMap;
	}
	
	/**
	 * @param monthlySwLineItems
	 * @param confgrtnList
	 * @return
	 * group by PID, PID description, configuration id
	 */
	private Map buildMonthlySwConfgrtnMapforSubmit(){
		Map confgrnsMap = new HashMap();
		if((this.monthlySoftwares == null) || (this.monthlySoftwares.size() == 0)){
			return confgrnsMap;
		}
		if((this.monthlySwConfgrtns == null) || (this.monthlySwConfgrtns.size() == 0)){
			return confgrnsMap;
		}

		Iterator cnfgrtnIt = this.monthlySwConfgrtns.iterator();
		while (cnfgrtnIt.hasNext()) {
			MonthlySoftwareConfiguration ppc = (MonthlySoftwareConfiguration) cnfgrtnIt.next();
			List qliList = new ArrayList();
			for (int j = 0; j < this.monthlySoftwares.size(); j++) {
				MonthlySwLineItem qli = (MonthlySwLineItem)this.monthlySoftwares.get(j);
				if((ppc.getConfigrtnId() != null) && ppc.getConfigrtnId().equals(qli.getConfigrtnId())){
					qliList.add(qli);
				}
			}
			confgrnsMap.put(ppc, qliList);
		}
		return confgrnsMap;
	}
	
	
	/**
	 * @param lineItems
	 * @return
	 * if quote has monthly software part, return true
	 */
	public boolean isQuoteHasMonthlySwPart(){
		if(this.monthlySoftwares == null || this.monthlySoftwares.size() == 0){
			return false;
		}
		return true;
	}
	
	
	/**
	 * calculate monthly software configration's net increase and unused price
	 */
	public void calculateMonthlySwIncreaseUnusedTCV(boolean isChannelQuote) {
		List configrtnList = this.getMonthlySwConfgrtns();
		if (configrtnList == null || configrtnList.size() == 0) {
			return;
		}
		for (Iterator iterator = configrtnList.iterator(); iterator.hasNext();) {
			MonthlySoftwareConfiguration configrtn = (MonthlySoftwareConfiguration) iterator.next();
			if (!configrtn.isAddOnTradeUp()) {
				continue;
			}
			List<MonthlySwLineItem> masterMonthlySwPartList = this.getMonthlySwConfigrtnsMap().get(configrtn);
			boolean hasRepacedPart = false;
			for (Iterator iterator2 = masterMonthlySwPartList.iterator(); iterator2.hasNext();) {
				MonthlySwLineItem qli = (MonthlySwLineItem) iterator2.next();
				if (qli.isMonthlySwSubscrptnPart() && qli.isReplacedPart()) {
					hasRepacedPart = true;
					break;
				}
			}
			if (!hasRepacedPart) {
				continue;
			}
			double unusedBidTCV = 0.0;
			double increaseBidTCV = 0.0;
			double totBidTCV = 0.0;
			for (Iterator iterator2 = masterMonthlySwPartList.iterator(); iterator2.hasNext();) {
				MonthlySwLineItem qli = (MonthlySwLineItem) iterator2.next();
				totBidTCV += caculateIncreaseTotTCV4EachPart(qli, isChannelQuote);
				unusedBidTCV += caculateIncreaseUnusedTCV4EachPart(qli, isChannelQuote);
				if (qli.getRampUpLineItems() != null && qli.getRampUpLineItems().size() > 0) {
					for (Iterator iterator3 = qli.getRampUpLineItems().iterator(); iterator3.hasNext();) {
						MonthlySwLineItem rampUpPart = (MonthlySwLineItem) iterator3.next();
						totBidTCV += caculateIncreaseTotTCV4EachPart(rampUpPart, isChannelQuote);
						unusedBidTCV += caculateIncreaseUnusedTCV4EachPart(rampUpPart, isChannelQuote);
					}
				}
			}
			configrtn.setUnusedBidTCV(new Double(unusedBidTCV));
			increaseBidTCV = totBidTCV - unusedBidTCV;
			configrtn.setIncreaseBidTCV(new Double(increaseBidTCV));
		}
	}

	private double caculateIncreaseUnusedTCV4EachPart(MonthlySwLineItem qli, boolean isChannelQuote) {
		double unusedBidTCV = 0.0;
		if (qli.isMonthlySwSubscrptnPart() && qli.isReplacedPart()) {
            int remainingTermTillCAEndDate = qli.getRemainingTermTillCAEndDate() == null ? 0 : qli
                    .getRemainingTermTillCAEndDate();
			if (isChannelQuote) {
				if (qli.getICvrageTerm() != null && qli.getSaasBpTCV() != null && remainingTermTillCAEndDate != 0) {
					unusedBidTCV += qli.getSaasBpTCV().doubleValue() / qli.getICvrageTerm().intValue() * remainingTermTillCAEndDate;
				}
			} else {
				if (qli.getICvrageTerm() != null && qli.getSaasBidTCV() != null && remainingTermTillCAEndDate != 0) {
					unusedBidTCV += qli.getSaasBidTCV().doubleValue() / qli.getICvrageTerm().intValue() * remainingTermTillCAEndDate;
				}
			}
		}
		return unusedBidTCV;
	}

	private double caculateIncreaseTotTCV4EachPart(MonthlySwLineItem qli, boolean isChannelQuote) {
		double totBidTCV = 0.0;
		if (qli.isMonthlySwSubscrptnPart() && !qli.isReplacedPart()) {
			if (isChannelQuote) {
				if (qli.getSaasBpTCV() != null) {
					totBidTCV += qli.getSaasBpTCV().doubleValue();
				}
			} else {
				if (qli.getSaasBidTCV() != null) {
					totBidTCV += qli.getSaasBidTCV().doubleValue();
				}
			}
		}
		return totBidTCV;
	}

	/**
	 * @throws TopazException void before display PP tab, clear the override
	 *         unit price and discount for monthly software part
	 */
	public void clearMonthlyDailyParts() throws TopazException {
		List<MonthlySwLineItem> monthlySwlist = this.getMonthlySoftwares();
		if (monthlySwlist == null || monthlySwlist.size() == 0) {
			return;
		}
		for (int i = 0; i < monthlySwlist.size(); i++) {
			MonthlySwLineItem qli = (MonthlySwLineItem) monthlySwlist.get(i);
			if (qli.isMonthlySwDailyPart()) { // For saas daily part
				if (qli.getOverrideUnitPrc() != null) {
					qli.setOverrideUnitPrc(null);
				}
				if (qli.getLineDiscPct() != 0) {
					qli.setLineDiscPct(0);
				}
			}
		}
	}

	/**
	 * @param estmtdOrdDate
	 * TODO: need to add actual logic later
	 */
	public void calculateTermFortMonthlyParts(java.util.Date estmtdOrdDate) {
		if (this.getMonthlySwConfgrtns() == null || this.getMonthlySwConfgrtns().size() > 0) {
			return;
		}
		for (MonthlySoftwareConfiguration config : this.getMonthlySwConfgrtns()) {
			if (!config.isAddOnTradeUp()) {
				return;
			}
			for (MonthlySwLineItem monthlyPart : this.getMonthlySwConfigrtnsMap().get(config)) {
				// TODO: need to check only recalculate term for monthly
				// subscription part that has replaced part
				if (!monthlyPart.isMonthlySwSubscrptnPart()) {
					continue;
				}
				if (monthlyPart.isReplacedPart()) {
					continue;
				}
			}
		}
	}

    public void processMonthlyPartsForQutoeCreateService(List<QuoteLineItem> lineItemList) throws TopazException {
		if (lineItemList == null || lineItemList.size() == 0) {
			return;
		}

		fillMonthlySwLineItems(lineItemList);

		for (MonthlySwLineItem quoteLineItem : this.getMonthlySoftwares()) {
			if (quoteLineItem.isMonthlySwSubscrptnPart()) {
				if (quoteLineItem.getRampUpLineItems() != null && quoteLineItem.getRampUpLineItems().size() > 0) {
                    // refer to case of SaaS, do not set the ramp up flag of the subs part to be true.
                    // quoteLineItem.setRampUpIndicator4QuoteCreateService(true);
					for (int i = 0; i < quoteLineItem.getRampUpLineItems().size(); i++) {
						MonthlySwLineItem rampUp = (MonthlySwLineItem) quoteLineItem.getRampUpLineItems().get(i);
                        // refer to Lan Yan's response and response
                        // Notes://CAMDB10/85256B890058CBA6/5D3D446FEB23FC918525718E006EAFDE/5D6828C1C07AC51385257C6A005FD7A3,
                        // do not set the last Ramp Up part's flag to be false.
                        rampUp.setRampUpIndicator4QuoteCreateService(true);
					}
				}
			}
		}
	}

}
