/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.config.MonthlySwAddonTradeUpReasonCode;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlySwSubscrptnConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 11:39:45 AM
 *
 */
@SuppressWarnings("rawtypes")
public class MonthlySwSubscrptnConfiguratorPart extends MonthlySwConfiguratorPart {
	
	
	private MonthlySwOverageConfiguratorPart oveargePart;
	
	private MonthlySwDailyConfiguratorPart dailyPart;
	
	private List<MonthlySwRampUpSubscriptionConfiguratorPart> rampUpParts = new LinkedList<MonthlySwRampUpSubscriptionConfiguratorPart>();

	private MonthlySwRampUpSubscriptionConfiguratorPart nextRampUpSubscriptionPart;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer term ;
	
	private String publshdPriceDurtnCode;
	
	private String publshdPriceDurtnCodeDscr;
	

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public MonthlySwOverageConfiguratorPart getOveargePart() {
		return oveargePart;
	}

	public void setOveargePart(MonthlySwOverageConfiguratorPart oveargePart) {
		this.oveargePart = oveargePart;
	}

	public MonthlySwDailyConfiguratorPart getDailyPart() {
		return dailyPart;
	}

	public void setDailyPart(MonthlySwDailyConfiguratorPart dailyPart) {
		this.dailyPart = dailyPart;
	}
	
	public boolean hasRelatedOveragePart(){
		return oveargePart != null;
	}
	
	public boolean hasRelatedDailyPart(){
		return dailyPart != null;
	}

	public boolean hasRelatedNextRampUpSubscriptionPart() {
		return nextRampUpSubscriptionPart != null;
	}

	public String getPublshdPriceDurtnCode() {
		return publshdPriceDurtnCode;
	}

	public void setPublshdPriceDurtnCode(String publshdPriceDurtnCode) {
		this.publshdPriceDurtnCode = publshdPriceDurtnCode;
	}

	public String getPublshdPriceDurtnCodeDscr() {
		return publshdPriceDurtnCodeDscr;
	}

	public void setPublshdPriceDurtnCodeDscr(String publshdPriceDurtnCodeDscr) {
		this.publshdPriceDurtnCodeDscr = publshdPriceDurtnCodeDscr;
	}

	public List<MonthlySwRampUpSubscriptionConfiguratorPart> getRampUpParts() {
		return rampUpParts;
	}

	public void setRampUpParts(List<MonthlySwRampUpSubscriptionConfiguratorPart> rampUpParts) {
		this.rampUpParts = rampUpParts;
	}
	
	public boolean hasRampUpParts(){
		return rampUpParts != null && rampUpParts.size() > 0;
	}
	
	@Override
	public void deleteFromQuote(Map<String,MonthlySwLineItem> monthlySwLineItems) throws TopazException{
		
		 super.deleteFromQuote(monthlySwLineItems);
		
		
		if (hasRelatedOveragePart()){
			//delete overage part
			oveargePart.deleteFromQuote(monthlySwLineItems);
		}
		
		if (hasRelatedDailyPart()){
			//delete dailyPart
			dailyPart.deleteFromQuote(monthlySwLineItems);
		}
		
		if (hasRampUpParts()){
			//delete rampUpParts
			for (MonthlySwRampUpSubscriptionConfiguratorPart rampUpPart : rampUpParts) {
				rampUpPart.deleteFromQuote(monthlySwLineItems);
			}
		}
		
	}

	@Override
    public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
			throws TopazException {
        MonthlySwLineItem qli = super.addPartToQuote(submitContract, configuratorPartFromPage, configuratorPartFromDB);
		qli.setBillgFrqncyCode(configuratorPartFromPage.getBillingFrequencyCode());
		qli.setPartQty(configuratorPartFromPage.getPartQty());
		qli.setRampUpPeriodNum(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpDurationInt());
        boolean hasRamupPart = false;
		qli.setMonthlySwPart(true);
		qli.setMonthlySwSubscrptnPart(true);
        List<MonthlySwSubscrptnConfiguratorPart> rampUpLineItems = configuratorPartFromPage.getRampUpLineItems();
        if (rampUpLineItems != null && rampUpLineItems.size() != 0) {
            hasRamupPart = true;
        }
        qli.setHasRamupPart(hasRamupPart);

        // setReasonCodes
        setReasonCodes(qli, submitContract, configuratorPartFromPage, configuratorPartFromDB);

		//add overage part
		if (hasRelatedOveragePart()){
            MonthlySwLineItem overageLineItem = this.getOveargePart().addPartToQuote(submitContract, configuratorPartFromPage,
                    configuratorPartFromDB);
			overageLineItem.setIRelatedLineItmNum(qli.getDestSeqNum());
            overageLineItem.setHasRamupPart(hasRamupPart);

            // set AddReasonCode, ReplacedReasonCode
            overageLineItem.setAddReasonCode(qli.getAddReasonCode());
            overageLineItem.setReplacedReasonCode(qli.getReplacedReasonCode());
		}
		
		//add daily part
		if (hasRelatedDailyPart()){
            MonthlySwLineItem dailyPart = this.getDailyPart().addPartToQuote(submitContract, configuratorPartFromPage,
                    configuratorPartFromDB);
			dailyPart.setIRelatedLineItmNum(qli.getDestSeqNum());
            // set AddReasonCode
            dailyPart.setAddReasonCode(qli.getAddReasonCode());
		}
		qli.setUpdateSectionFlag(true);
        qli.setMonthlySwTcvAcv(true);

        // update term and billing value only for subsc.
        qli.setICvrageTerm(configuratorPartFromPage.getTerm());
        qli.setCumCvrageTerm(configuratorPartFromPage.getTerm());
        qli.setBillgFrqncyCode(configuratorPartFromPage.getBillingFrequencyCode());


		return qli;
	}

    @Override
    public void update(MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlySwLineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract) throws TopazException {
		MonthlySwLineItem lineItem = this.getNeedUpdateMonthlySwLineItem( monthlySwLineItemsMap);
		if (lineItem != null){
			setAttribute(lineItem,configuratorPartFromPage);
			
			//set it's related daily part related seq num
			if(hasRelatedDailyPart()){
				MonthlySwLineItem dailyLineItem = this.getDailyPart().getNeedUpdateMonthlySwLineItem(monthlySwLineItemsMap);
				dailyLineItem.setIRelatedLineItmNum(lineItem.getDestSeqNum());
			}
			
			//set it's related overage part related seq num
			if(hasRelatedOveragePart()){
				MonthlySwLineItem oveargeLineItem = this.getOveargePart().getNeedUpdateMonthlySwLineItem(monthlySwLineItemsMap);
				oveargeLineItem.setIRelatedLineItmNum(lineItem.getDestSeqNum());
			}
			
			if (configuratorActionPart.isSupportRampUpPart()){
				updateRampUpPart(configuratorPartFromPage,lineItem,monthlySwLineItemsMap,submitContract);
			}
	
		}
		
		
	}

	public MonthlySwRampUpSubscriptionConfiguratorPart getNextRampUpSubscriptionPart() {
		return nextRampUpSubscriptionPart;
	}

	public void setNextRampUpSubscriptionPart(MonthlySwRampUpSubscriptionConfiguratorPart nextRampUpSubscriptionPart) {
		this.nextRampUpSubscriptionPart = nextRampUpSubscriptionPart;
	}
	
	/**
	 * set suscription part some attribute
	 * if data from ca and not addional ,only set qty
	 * if data from web, need update all 
	 * @param lineItem
	 * @param configuratorPartFromPage
	 * @throws TopazException
	 */
	private void setAttribute(MonthlySwLineItem lineItem,MonthlySwConfiguratorPart configuratorPartFromPage) throws TopazException{
		lineItem.setPartQty(configuratorPartFromPage.getPartQty());
		if (configuratorActionPart.isUpdateFromCA()){
			lineItem.setBillgFrqncyCode(configuratorActionPart.getSapBillgFrqncyOptCode());
			lineItem.setICvrageTerm(this.getTerm());
			
		} else {
			lineItem.setBillgFrqncyCode(configuratorPartFromPage.getBillingFrequencyCode());
			lineItem.setICvrageTerm(configuratorPartFromPage.getTerm());
			lineItem.setRampUpPeriodNum(configuratorPartFromPage.getSubmitConfiguratorPart().getRampUpPeriod());
		}
		
	}
	
    private void updateRampUpPart(
			MonthlySwConfiguratorPart configuratorPartFromPage,
			MonthlySwLineItem lineItem,
			Map<String, MonthlySwLineItem> monthlySwLineItemsMap,
			SubmittedMonthlySwConfiguratorContract submitContract) throws TopazException {
		if (configuratorPartFromPage instanceof MonthlySwSubscrptnConfiguratorPart) {
			MonthlySwSubscrptnConfiguratorPart monthlySwSubscrptnConfiguratorPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPartFromPage;
			if (monthlySwSubscrptnConfiguratorPart.hasRampUpParts()) {
				lineItem.setHasRamupPart(true);
				if (hasRelatedOveragePart()) {
					MonthlySwLineItem oveargeLineItem = this.getOveargePart()
							.getNeedUpdateMonthlySwLineItem(
									monthlySwLineItemsMap);
					oveargeLineItem.setHasRamupPart(true);
				}
				LinkedList<MonthlySwLineItem> relatedRampupList = new LinkedList<MonthlySwLineItem>();
				relatedRampupList.add(lineItem);
				Integer totalTerm = monthlySwSubscrptnConfiguratorPart
						.getTerm();
				for (Iterator iterator = (Iterator) monthlySwSubscrptnConfiguratorPart
						.getRampUpParts().iterator(); iterator.hasNext();) {
					MonthlySwRampUpSubscriptionConfiguratorPart currentRampUpPart = (MonthlySwRampUpSubscriptionConfiguratorPart) iterator
							.next();
					if (currentRampUpPart.getSubmitConfiguratorPart()
							.isDeleted()) {
						// if part flagged to be deleted, do nothing
						continue;
					}
					currentRampUpPart.setTerm(Integer
							.parseInt(currentRampUpPart
									.getSubmitConfiguratorPart()
									.getRampUpDurationStr()));
					totalTerm += currentRampUpPart.getTerm();
					currentRampUpPart
							.setSubscriptnPart(monthlySwSubscrptnConfiguratorPart);
				}
				// set Total term for MonthlySwSubscrptnConfiguratorPart and
				// MonthlySwRampUpSubscriptionConfiguratorPart parts.
				monthlySwSubscrptnConfiguratorPart.setTotalTerm(totalTerm);
				lineItem.setCumCvrageTerm(totalTerm);
				for (Iterator iterator = (Iterator) monthlySwSubscrptnConfiguratorPart
						.getRampUpParts().iterator(); iterator.hasNext();) {
					MonthlySwRampUpSubscriptionConfiguratorPart currentRampUpPart = (MonthlySwRampUpSubscriptionConfiguratorPart) iterator
							.next();
					if (currentRampUpPart.getSubmitConfiguratorPart()
							.isDeleted()) {
						// if part flagged to be deleted, do nothing
						continue;
					}
					currentRampUpPart.setTotalTerm(totalTerm);
					addRampUpSubscriptionPart(currentRampUpPart,
							submitContract, monthlySwSubscrptnConfiguratorPart,
							relatedRampupList, hasRelatedOveragePart(),
							hasRelatedOveragePart() ? this.getOveargePart()
									.getPartNum() : null);
				}
				setRaletedSeqNumForAllRampUpParts(relatedRampupList);
			} else {
				lineItem.setHasRamupPart(false);
			}

		}
	}
	
	
	private void addRampUpSubscriptionPart(MonthlySwRampUpSubscriptionConfiguratorPart rampUpPart,
			SubmittedMonthlySwConfiguratorContract submitContract, MonthlySwConfiguratorPart configuratorPartFromPage,
			LinkedList<MonthlySwLineItem> relatedRampupList, boolean hasOveragePart, String overagePartNum) throws TopazException {
		MonthlySwLineItem rampUpSubscriptionLineItem = rampUpPart.addPartToQuote(submitContract, rampUpPart,
				hasOveragePart, overagePartNum);
		relatedRampupList.add(rampUpSubscriptionLineItem);
	}

	private void setRaletedSeqNumForAllRampUpParts(LinkedList<MonthlySwLineItem> allRelatedRampupList) throws TopazException {
		String billingFrqncyCode = null;
		for (ListIterator iterator = (ListIterator) allRelatedRampupList.iterator(); iterator.hasNext();) {
			MonthlySwLineItem previousLineItem = null;
			MonthlySwLineItem currentLineItem = null;
			if (iterator.nextIndex() == 0) {
				// it's main subscription part, get it's billing code
				MonthlySwLineItem mainSubPart = (MonthlySwLineItem) iterator.next();
				billingFrqncyCode = mainSubPart.getBillgFrqncyCode();
			} else {
				previousLineItem = (MonthlySwLineItem) iterator.previous();
				// cause has call once previous(), in order to get the correct
				// Object, need to call next() twice
				iterator.next();
				currentLineItem = (MonthlySwLineItem) iterator.next();
			}
			if (previousLineItem != null && currentLineItem != null) {
				currentLineItem.setBillgFrqncyCode(billingFrqncyCode);
				currentLineItem.setIRelatedLineItmNum(previousLineItem.getDestSeqNum());
			}

		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart#setReasonCodes(com.ibm.dsw.quote.common.domain
     * .MonthlySwLineItem, com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract,
     * com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
     */
    /**
     * DOC Comment method "setReasonCodes".
     * 
     * @param monthlySwLineItem
     * @param configuratorPartFromPage
     * @param submitContract
     * @throws TopazException
     */
    public static void setReasonCodes(MonthlySwLineItem monthlySwLineItem, SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
            throws TopazException {
        boolean isAddOnTradUp = submitContract.isAddOnConfigrtn();
        if (isAddOnTradUp) {
            boolean isNewPart = isNewPart(monthlySwLineItem, submitContract, configuratorPartFromPage, configuratorPartFromDB);
            // set AddReasonCode to be AP_ADD_REASON_CODE
            if (isNewPart) {
                monthlySwLineItem.setAddReasonCode(MonthlySwAddonTradeUpReasonCode.AP_ADD_REASON_CODE);
            } else {
                boolean isAddQuantity = isAddQuantity(configuratorPartFromPage, configuratorPartFromDB);
                if (isAddQuantity) {
                    if (monthlySwLineItem.isReplacedPart()) {
                        monthlySwLineItem.setReplacedReasonCode(MonthlySwAddonTradeUpReasonCode.AQ_REPLACED_REASON_CODE);
                    } else {
                        monthlySwLineItem.setAddReasonCode(MonthlySwAddonTradeUpReasonCode.AQ_ADD_REASON_CODE);
                    }
                } else {
                    if (monthlySwLineItem.isReplacedPart()) {
                        if (configuratorPartFromPage == null || configuratorPartFromPage.getPartQty() == null
                                || configuratorPartFromPage.getPartQty() == 0) {
                            monthlySwLineItem.setReplacedReasonCode(MonthlySwAddonTradeUpReasonCode.TR_REPLACED_REASON_CODE);
                        } else {
                            monthlySwLineItem.setReplacedReasonCode(MonthlySwAddonTradeUpReasonCode.DQ_REPLACED_REASON_CODE);
                        }
                    } else {
                        monthlySwLineItem.setAddReasonCode(MonthlySwAddonTradeUpReasonCode.DQ_ADD_REASON_CODE);
                    }
                }

            }
        }
    }

    /**
     * DOC Comment method "isAddQuantity".
     * 
     * @param isReplaced
     * 
     * @param monthlySwLineItem
     * @param submitContract
     * @param configuratorPartFromPage
     * @param configuratorPartFromDB
     * @return
     */
    public static boolean isAddQuantity(MonthlySwConfiguratorPart configuratorPartFromPage,
            MonthlySwConfiguratorPart configuratorPartFromDB) {
        if (configuratorPartFromPage != null && configuratorPartFromPage.getPartQty() != null) {
            if (configuratorPartFromDB != null && configuratorPartFromDB.getPartQty() != null) {
                if (configuratorPartFromDB.getPartQty() > configuratorPartFromPage.getPartQty()) {
                    return false;
                } else if (configuratorPartFromDB.getPartQty() < configuratorPartFromPage.getPartQty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DOC Comment method "isNewPart".
     * 
     * @param monthlySwLineItem
     * @param submitContract
     * @param configuratorPartFromPage
     * @param configuratorPartFromDB
     * @return
     */
    private static boolean isNewPart(MonthlySwLineItem monthlySwLineItem, SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB) {
        return false;
    }

}
