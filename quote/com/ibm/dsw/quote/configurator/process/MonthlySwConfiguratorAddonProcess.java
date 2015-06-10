/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.config.MonthlySwAddonTradeUpReasonCode;
import com.ibm.dsw.quote.configurator.contract.BuildMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwAddonTradeupConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwOnDemandConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwOverageConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwRampUpSubscriptionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwSubscrptnConfiguratorPart;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlySwConfiguratorAddonProcess
 * @author Frank
 * @Description: TODO
 * @date Jan 8, 2014 10:09:33 PM
 *
 */
public class MonthlySwConfiguratorAddonProcess extends
		AbstractMonthlyConfiguratorProcess {
	
	/**
	 * @param monlySwConfigJDBC
	 */
	public MonthlySwConfiguratorAddonProcess(
			MonthlySwConfiguratorJDBC monlySwConfigJDBC) {
		super(monlySwConfigJDBC);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#setMonthlySwProducts(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm, com.ibm.dsw.quote.configurator.contract.BuildMonthlySwConfiguratorContract)
	 */
	@Override
	protected void setMonthlySwProducts(
			MonthlySwConfiguratorForm configuratorForm,
			BuildMonthlySwConfiguratorContract buildContract)
			throws TopazException {

		// get parts from ca
		List<MonthlySwConfiguratorPart> masterMonthlySwPartsFromCA = monlySwConfigJDBC
				.getMonthlySwConfgiuratorParts(buildContract.getWebQuoteNum(),
						buildContract.getChrgAgrmtNum(),buildContract.getOrgConfigId(),buildContract.getUserId());

		
		processMothlySwParts(masterMonthlySwPartsFromCA , configuratorForm);

	}
	
	private void processMothlySwParts (List<MonthlySwConfiguratorPart> masterMonthlySwPartsFromCA,MonthlySwConfiguratorForm configuratorForm){
		
		if (masterMonthlySwPartsFromCA == null || masterMonthlySwPartsFromCA.size() < 1){
			return ;
		}
		
		List<MonthlySwConfiguratorPart> updatedMonthlyParts = new ArrayList<MonthlySwConfiguratorPart>();
		List<MonthlySwConfiguratorPart> addionalMonthlyParts = new ArrayList<MonthlySwConfiguratorPart>();
		List<MonthlySwConfiguratorPart> noChangedMonthlyParts = new ArrayList<MonthlySwConfiguratorPart>();
		
		
		for (MonthlySwConfiguratorPart configuratorPart : masterMonthlySwPartsFromCA){
			MonthlySwAddonTradeupConfiguratorPart addonPart =(MonthlySwAddonTradeupConfiguratorPart) configuratorPart.getConfiguratorActionPart();
			
			// add updated parts
			if (addonPart.isUpdatedMonthly()){
				updatedMonthlyParts.add(configuratorPart);
				
			}
			
			// add additional parts
			else if (addonPart.isAddionalMonthly()){
				addionalMonthlyParts.add(configuratorPart);
				
			}
			
			// add no changed parts
			else if (addonPart.isNoChangedMonthly()){
				noChangedMonthlyParts.add(configuratorPart);
			}
		}
		
		configuratorForm.setUpdatedMonthlySwProducts(getConfiguratorProductList(updatedMonthlyParts));
		
		configuratorForm.setAddionMonthlySwProducts(getConfiguratorProductList(addionalMonthlyParts));
		
		configuratorForm.setNoChangedMonthlySwProducts(getConfiguratorProductList(noChangedMonthlyParts));
		
	}


	/**
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#getEndDate(com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract, java.util.List, java.util.List)
	 * configuratorPartsFromPage  configurator parts in Page . contain (subscrption parts , ondemand parts ,replace parts)
	 * masterMonthlySwPartsFromDb configurator parts in ca and web. contain (suscrption parts, ondemand parts,replace parts)
	 * 
	 * In two steps
	 * 1. get need calculate parts
	 * 2. calculate end date
	 */
	@Override
	public Date getEndDate(
			SubmittedMonthlySwConfiguratorContract submitContract,
			List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
			Map<String,MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap)
			throws TopazException {
		
		
		//page is not parts
		if (configuratorPartsFromPage == null || configuratorPartsFromPage.size() < 1) {
			return null;
		}
		
		// It is not parts in CA
		if (masterMonthlySwPartsFromDbMap == null || masterMonthlySwPartsFromDbMap.size() < 1){
			return null;
		}
	
		//get need calculate configurator parts
		List<MonthlySwConfiguratorPart> needCalculateParts = getNeedCalculateConfigutorParts(configuratorPartsFromPage,masterMonthlySwPartsFromDbMap);
		
		return calculateEndDate(needCalculateParts);
	}
	
	/**
	 * 1. get need calculate configurator parts
	 * 	1.1 configurator parts exsit in ca
	 *  1.2 configurator parts exsit in page
	 *  1.3 is not addition parts
	 *  1.4 only just subscription parts
	 *  1.5 configurator part's qty is not deteted from page
	 *  
	 *  note: masterMonthlySwPartsFromDbMap from  ca and web . contain (All ca parts and web table's data)
	 */

	private List<MonthlySwConfiguratorPart> getNeedCalculateConfigutorParts(
			List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
			Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap) {
		List<MonthlySwConfiguratorPart> needCalculateConfigutorParts = new ArrayList<MonthlySwConfiguratorPart>();

		for (MonthlySwConfiguratorPart configuratorPartFromPage : configuratorPartsFromPage) {
			
			MonthlySwConfiguratorPart configuratorPartFromDb = masterMonthlySwPartsFromDbMap
					.get(configuratorPartFromPage.getKey());
			
			if (configuratorPartFromDb == null){
				continue;
			}
			
			// 2. part is deleted
			if (isDeleted(configuratorPartFromPage.getPartQty(),
					configuratorPartFromDb)) {
				continue;
			}
			
			if (configuratorPartFromDb instanceof MonthlySwSubscrptnConfiguratorPart &&
					!configuratorPartFromDb.getConfiguratorActionPart()
					.isAddionalMonthly()){
				needCalculateConfigutorParts.add(configuratorPartFromDb);
			}
			
		}

		return needCalculateConfigutorParts;
	}
	
	
	/**
	 * calculate rule:
	 * 1.  end date is valid , calculate by end date . 
	 * 	  example :  if (latesteEndDate < part1.endDate) 
	 * 					latestEndDate = part.endDate
	 * 
	 * 2.  end date is invalid ,renewal end date is valid. calculate by renewal end date
	 * 	   example : if (latesteEndDate < renewal end date) 
	 * 				latesteEndDate = part.renewalEndDate
	 * 
	 * 3.  end date is invalid ,renewal end date also is invalid . calculate by next renewal end date
	 * 		example : if (sapbillingFrequcy == "M") 
	 * 				latesteEndDate = part.nextRenwlEndDate + renewal term month
	 * 
	 * 
	 * @param needCalculateParts
	 * @return
	 */
	private Date calculateEndDate(List<MonthlySwConfiguratorPart> needCalculateParts){
		
		Date latestDate = null;
		
		if (needCalculateParts == null || needCalculateParts.size() < 1){
			return latestDate;
		}
		
		for (MonthlySwConfiguratorPart configuratorPart : needCalculateParts){
			MonthlySwActionConfiguratorPart actionPart = configuratorPart.getConfiguratorActionPart();
			
			// first: calculate by end date
			if (isValidEndDate(actionPart.getEndDate())){
				latestDate = getLatestDate(latestDate,actionPart.getEndDate());
				continue;
			} 
			
			 //second : calculate by renewal end date
			if (isValidEndDate(actionPart.getRenewalEndDate())) {
				latestDate = getLatestDate(latestDate,actionPart.getRenewalEndDate());
				continue;
			} 
			
			//finally: calculate by nextRenwlDate
			if (isValidEndDate(actionPart.getNextRenwlDate())){
				if (ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY.equals(actionPart.getSapBillgFrqncyOptCode())){
					latestDate = getLatestDate (actionPart.getNextRenwlDate() , actionPart.getRenwlTermMths());
				}
				continue;
			}
			
		}
		
		return latestDate ;
	}

	/**
	 * 
	 * @param currenLatestDate
	 * @param endDate
	 * @return
	 */
	private Date getLatestDate(Date currenLatestDate ,Date endDate){
		if (currenLatestDate == null) {
			return endDate;
		}
		return currenLatestDate.after(endDate)? currenLatestDate : endDate;
		
	}
	
	/**
	 * 
	 * @param nextRewalDate
	 * @param renwalTermMonths
	 * @return
	 */
	private Date getLatestDate (Date nextRewalDate , Integer renwalTermMonths){
		return DateUtil.plusMonth(nextRewalDate , renwalTermMonths);
	}
	
	/**
	 * 
	 * @param endDate
	 * @return
	 */
	private boolean isValidEndDate(Date endDate){
		
		return endDate != null && !DateUtil.isDateBeforeToday(endDate);
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processDeleteLineItem(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, java.util.Map)
	 */
	@Override
	protected void processDeleteLineItem(
			MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorPartFromPage,
			Map<String, MonthlySwLineItem> monthlylineItemsMap)
			throws TopazException {
		
		configuratorPartFromDB.deleteFromQuote(monthlylineItemsMap);
		if (configuratorPartFromDB.getConfiguratorActionPart().isUpdatedMonthly()
				&& (configuratorPartFromPage.getPartQty() == null || configuratorPartFromPage.getPartQty() == 0)) {
		} else if (isNeedRemoveReplacePart(configuratorPartFromDB, configuratorPartFromPage)) {
			removeReplacePart(configuratorPartFromDB,monthlylineItemsMap);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processUpdateLineItem(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, java.util.Map)
	 */
    @Override
    protected void processUpdateLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract, CotermParameter parameter) throws TopazException {
		configuratorPartFromDB.update(configuratorPartFromPage, monthlylineItemsMap, submitContract);
		
		MonthlySwLineItem parentQli = monthlylineItemsMap.get(configuratorPartFromDB.getKey());
		MonthlySwLineItem replaceQli = monthlylineItemsMap.get(configuratorPartFromDB.getPartKey(configuratorPartFromDB.getPartNum()));
		
        if (isNeedUdpateCoTerm(configuratorPartFromDB, configuratorPartFromPage, replaceQli)) {
            if (parameter != null) {
                parentQli.setRelatedCotermLineItmNum(parameter.getRefDocLineItemSeqNum());
                boolean hasTR = hasTR(monthlylineItemsMap);
                if (hasTR) {
                    parentQli.setAddReasonCode(MonthlySwAddonTradeUpReasonCode.TA_ADD_REASON_CODE);
                } else {
                    parentQli.setAddReasonCode(MonthlySwAddonTradeUpReasonCode.AP_ADD_REASON_CODE);
                }

                if (configuratorPartFromDB instanceof MonthlySwSubscrptnConfiguratorPart) {
                    MonthlySwSubscrptnConfiguratorPart subscrptnConfiguratorPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPartFromDB;
                    if (subscrptnConfiguratorPart.hasRelatedOveragePart()) {
                        MonthlySwLineItem overageLineItem = subscrptnConfiguratorPart.getOveargePart()
                                .getNeedUpdateMonthlySwLineItem(monthlylineItemsMap);
                        overageLineItem.setAddReasonCode(parentQli.getAddReasonCode());
                    }
                    if (subscrptnConfiguratorPart.hasRelatedDailyPart()) {
                        MonthlySwLineItem dailyLineItem = subscrptnConfiguratorPart.getDailyPart()
                                .getNeedUpdateMonthlySwLineItem(monthlylineItemsMap);
                        dailyLineItem.setAddReasonCode(parentQli.getAddReasonCode());
                    }

                }
            }
            
            if (parentQli.getRelatedCotermLineItmNum() != null){
            	parentQli.setRefDocLineNum(parentQli.getRelatedCotermLineItmNum());
            }
        }
        
		if (isNeedUpdateReplacePart(configuratorPartFromDB,replaceQli)){
			updateReplacePart(configuratorPartFromDB, configuratorPartFromPage, submitContract, parentQli, monthlylineItemsMap);
		}
		
	}

    /**
     * DOC Comment method "hasTR".
     * 
     * @param monthlylineItemsMap
     * @return
     */
    private boolean hasTR(Map<String, MonthlySwLineItem> monthlylineItemsMap) {
        for (Iterator<String> iterator = monthlylineItemsMap.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            MonthlySwLineItem monthlySwLineItem = monthlylineItemsMap.get(key);
            if (MonthlySwAddonTradeUpReasonCode.TR_REPLACED_REASON_CODE.equals(monthlySwLineItem.getReplacedReasonCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC Comment method "isNeedUdpateCoTerm".
     * 
     * @param configuratorPartFromDB
     * @param configuratorPartFromPage
     * @param replaceQli
     * @return
     */
    private boolean isNeedUdpateCoTerm(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwLineItem replaceQli) {
        MonthlySwActionConfiguratorPart configuratorActionPart = configuratorPartFromDB.getConfiguratorActionPart();
        return replaceQli == null && configuratorPartFromDB instanceof MonthlySwSubscrptnConfiguratorPart
                && configuratorPartFromPage != null && configuratorPartFromPage.getPartQty() > 0
                && configuratorActionPart != null && configuratorActionPart.isAddionalMonthly();
    }

    /**
     * 1.Replace part is not exsit <br>
     * 2.only subscription part and overage part<br>
     * 3. is not addional section
     * 
     * @param replaceQli
     * @param configuratorPartFromDB
     * @return
     */
	private boolean isNeedUpdateReplacePart(
			MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwLineItem replaceQli) {
		return replaceQli == null
				&& configuratorPartFromDB instanceof MonthlySwSubscrptnConfiguratorPart
				&& !configuratorPartFromDB.getConfiguratorActionPart().isAddionalMonthly();
	}
	

	/**
	 * 
	 * @param configuratorPartFromDB
	 * @param configuratorPartFromPage
	 * @param submitContract
	 * @param parentQli
	 * @throws TopazException
	 */
	private void updateReplacePart(MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorPartFromPage,
			SubmittedMonthlySwConfiguratorContract submitContract,
			MonthlySwLineItem parentQli, Map<String, MonthlySwLineItem> monthlylineItemsMap) throws TopazException {
		addReplacePart(configuratorPartFromDB, configuratorPartFromPage, submitContract, parentQli, monthlylineItemsMap);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processAddLineItem(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract)
	 */
    @Override
    protected boolean processAddLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract) throws TopazException {
		MonthlySwLineItem parentQli = null;
        boolean added = false;
		configuratorPartFromDB.deleteFromQuote(monthlylineItemsMap);

		if (isNeedRemoveReplacePart(configuratorPartFromDB, configuratorPartFromPage)
				|| isNeedAddReplacePart(configuratorPartFromDB, configuratorPartFromPage)) {
			removeReplacePart(configuratorPartFromDB, monthlylineItemsMap);
		}

		if (isNeedAddMainPart(configuratorPartFromPage,configuratorPartFromDB)){
            parentQli = configuratorPartFromDB.addPartToQuote(submitContract, configuratorPartFromPage, configuratorPartFromDB);
			monthlylineItemsMap.put(parentQli.getPartKey(), parentQli);
            added = true;
		}
		
		if (isNeedAddReplacePart(configuratorPartFromDB,
				configuratorPartFromPage)) {
			addReplacePart(configuratorPartFromDB, configuratorPartFromPage, submitContract, parentQli, monthlylineItemsMap);
            added = true;
		}
        return added;

	}
	
	/**
	 * In 2 case
	 * 1. 
	 * @param configuratorPartFromDB
	 * @param monthlylineItemsMap
	 * @throws TopazException 
	 */
	private void removeReplacePart(
			MonthlySwConfiguratorPart configuratorPartFromDB,
			Map<String, MonthlySwLineItem> monthlylineItemsMap)
			throws TopazException {

		MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPartFromDB;

		subscrptnPart.deleteFromQuote(monthlylineItemsMap,
				subscrptnPart.getPartKey(subscrptnPart.getPartNum()));

		//remove overage part
		if (subscrptnPart.hasRelatedOveragePart()) {
			MonthlySwOverageConfiguratorPart oveargePart = subscrptnPart
					.getOveargePart();
			oveargePart.deleteFromQuote(monthlylineItemsMap,
					oveargePart.getPartKey(oveargePart.getPartNum()));
		}

	}
	
	/**
	 * 
	 * @param configuratorPartFromDB
	 * @param configuratorPartFromPage
	 * @return
	 */
	private boolean isNeedRemoveReplacePart(
			MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorPartFromPage) {
		return configuratorPartFromDB instanceof MonthlySwSubscrptnConfiguratorPart
				&& isDeleted(configuratorPartFromPage.getPartQty(),
						configuratorPartFromDB);
	}
	
	
    private void addReplacePart(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorPartFromPage, SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwLineItem parentQli, Map<String, MonthlySwLineItem> monthlylineItemsMap) throws TopazException {
        MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) configuratorPartFromDB;

        MonthlySwLineItem parentItem = addSubscrptnReplacePart(subscrptnPart, submitContract, parentQli,
                configuratorPartFromPage, configuratorPartFromDB);
        monthlylineItemsMap.put(parentItem.getPartNum(), parentItem);

        // add overage replace part
        if (subscrptnPart.hasRelatedOveragePart()) {
            addOveragReplacePart(subscrptnPart, submitContract, parentItem, configuratorPartFromPage, monthlylineItemsMap,
                    configuratorPartFromDB);
        }

    }
	
	/**
	 * 
	 * @param configuratorPartFromPage
	 * @return
	 */
	private boolean isNeedAddMainPart(MonthlySwConfiguratorPart configuratorPartFromPage,MonthlySwConfiguratorPart configuratorPartFromDB){
		return !isDeleted(configuratorPartFromPage,configuratorPartFromDB);
	}

    /**
     * Add subscription Replace part
     * 
     * @param subscrptnPart
     * @param submitContract
     * @param configuratorPartFromDB
     * @throws TopazException
     */
    private MonthlySwLineItem addSubscrptnReplacePart(MonthlySwSubscrptnConfiguratorPart subscrptnPart,
            SubmittedMonthlySwConfiguratorContract submitContract, MonthlySwLineItem parentQli,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
            throws TopazException {

        MonthlySwLineItem qli = subscrptnPart.addPartToQuote(submitContract, subscrptnPart.getPartNum());

		qli.setLocalExtPrc(null);
		qli.setLocalExtProratedPrc(null);
		qli.setLocalExtProratedDiscPrc(subscrptnPart
				.getConfiguratorActionPart().getLocalExtndPrice());
		qli.setSaasBidTCV(subscrptnPart.getConfiguratorActionPart()
				.getTotCmmtmtVal());
		qli.setChannelExtndPrice(subscrptnPart.getConfiguratorActionPart()
				.getLocalExtndPrice());
		qli.setSaasBpTCV(subscrptnPart.getConfiguratorActionPart()
				.getTotCmmtmtVal());
		qli.setPartQty(subscrptnPart.getConfiguratorActionPart().getPartQty());
		qli.setReplacedPart(true);
		qli.setMonthlySwPart(true);
		qli.setMonthlySwSubscrptnPart(true);
		qli.setMonthlySwTcvAcv(true);
		qli.setUpdateSectionFlag(true);
		qli.setBillgFrqncyCode(subscrptnPart.getConfiguratorActionPart().getSapBillgFrqncyOptCode());
		
        qli.setICvrageTerm(configuratorPartFromPage.getTerm());
        qli.setCumCvrageTerm(configuratorPartFromPage.getTerm());
		qli.setConfigrtnId(submitContract.getOrgConfigId());
        MonthlySwActionConfiguratorPart configuratorActionPart = subscrptnPart.getConfiguratorActionPart();
        if (configuratorActionPart != null && configuratorActionPart.isUpdateFromCA()
                && configuratorActionPart instanceof MonthlySwAddonTradeupConfiguratorPart) {
            MonthlySwAddonTradeupConfiguratorPart monthlySwAddonTradeupConfiguratorPart = (MonthlySwAddonTradeupConfiguratorPart) configuratorActionPart;
            if (parentQli != null) {
                parentQli.setRelatedCotermLineItmNum(monthlySwAddonTradeupConfiguratorPart.getRefDocLineNum());
            }
        }
		if (parentQli != null){
			qli.setIRelatedLineItmNum(parentQli.getDestSeqNum());
		}
		
        MonthlySwSubscrptnConfiguratorPart.setReasonCodes(qli, submitContract, configuratorPartFromPage, configuratorPartFromDB);

		return qli;
	}

    /**
     * Add overage ReplacePart
     * 
     * @param subscrptnPart
     * @param submitContract
     * @param configuratorPartFromDB
     * @throws TopazException
     */
    private void addOveragReplacePart(MonthlySwSubscrptnConfiguratorPart subscrptnPart,
            SubmittedMonthlySwConfiguratorContract submitContract, MonthlySwLineItem parentQli,
            MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
            MonthlySwConfiguratorPart configuratorPartFromDB) throws TopazException {

        MonthlySwOverageConfiguratorPart oveargePart = subscrptnPart.getOveargePart();
        MonthlySwLineItem qli = oveargePart.addPartToQuote(submitContract, oveargePart.getPartNum());
		
		qli.setLocalUnitProratedPrc(null);
    	qli.setLocalUnitProratedDiscPrc(oveargePart.getConfiguratorActionPart().getLocalOvrageAmt());
    	qli.setChannelUnitPrice(oveargePart.getConfiguratorActionPart().getLocalOvrageAmt());
    	qli.setIRelatedLineItmNum(parentQli.getDestSeqNum());
		qli.setReplacedPart(true);
		qli.setMonthlySwPart(true);
		qli.setMonthlySwSubscrptnOvragePart(true);
		qli.setConfigrtnId(submitContract.getOrgConfigId());
        
        qli.setAddReasonCode(parentQli.getAddReasonCode());
		qli.setReplacedReasonCode(parentQli.getReplacedReasonCode());
		
		monthlylineItemsMap.put(qli.getPartNum(), qli);
	}
	
	/**
	 * 1. only subscription part and ovearage part has replace
	 * 2. only update and no change section has replace
	 * 3. qty is not the same
	 * @param configuratorPartFromDB
	 * @param configuratorPartFromPage
	 * @return
	 */
	private boolean isNeedAddReplacePart(
			MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorPartFromPage) {
		return configuratorPartFromDB instanceof MonthlySwSubscrptnConfiguratorPart
				&& !(configuratorPartFromPage.getPartQty() == null ? new Integer(0) : configuratorPartFromPage.getPartQty())
						.equals(configuratorPartFromDB.getConfiguratorActionPart()
								.getPartQty())
				&& !configuratorPartFromDB.getConfiguratorActionPart()
						.isAddionalMonthly();
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#isDeleted(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
	 */
	@Override
	protected boolean isDeleted(
			MonthlySwConfiguratorPart configuratorPartFromPage,
			MonthlySwConfiguratorPart configuratorPartFromDB) {
		boolean isDeleted = configuratorPartFromPage
				.getSubmitConfiguratorPart().isDeleted();

		//delete part
		if (!isDeleted) {
			isDeleted = isDeleted(configuratorPartFromPage.getPartQty(),configuratorPartFromDB);
		} 

		return isDeleted;
	}
	
	/**
	 * 
	 * @param partQty  from page qty
	 * @param configuratorPartFromDB
	 * ondemand part is checked . no need compare qty
	 * @return
	 */
	private boolean isDeleted(Integer partQty,
			MonthlySwConfiguratorPart configuratorPartFromDB) {

		boolean isDeleted = false;
		
		
		if (configuratorPartFromDB instanceof MonthlySwOnDemandConfiguratorPart){
			// do nothing
		} else if (partQty == null
				|| ((configuratorPartFromDB.getConfiguratorActionPart().isNoChangedMonthly() || configuratorPartFromDB
						.getConfiguratorActionPart().isUpdatedMonthly()) && partQty.equals(configuratorPartFromDB
						.getConfiguratorActionPart().getPartQty()))) {
			isDeleted = true;
		}

		return isDeleted;
	}

	@Override
	protected void addMonthlyLineItemToMap(List lineItems,
			Map<String, MonthlySwLineItem> monthlyLineItemsMap) {
		for (Object lineItem : lineItems){
			if (lineItem instanceof MonthlySwLineItem) {
				MonthlySwLineItem item = (MonthlySwLineItem)lineItem;
				
				if (item.isReplacedPart()){
					monthlyLineItemsMap.put(item.getPartNum(), item);
				} else {
					monthlyLineItemsMap.put(item.getPartKey(), item);
				}
			}
		}
	}

	protected MonthlySoftwareConfiguration processConfiguration(SubmittedMonthlySwConfiguratorContract submitContract,
			List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
			Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap) throws TopazException {
		String webQuoteNum = submitContract.getWebQuoteNum();
		String orgConfigId = submitContract.getOrgConfigId();

		/**
		 * get monthly softwareConfiguration
		 */
		List<MonthlySoftwareConfiguration> monthlySwConfigurationList = MonthlySoftwareConfigurationFactory.singleton()
				.findMonthlySwConfiguration(webQuoteNum);

		if (monthlySwConfigurationList == null || monthlySwConfigurationList.size() < 1) {
			// create configuraor
			MonthlySoftwareConfiguration monthlyConfigurtn = MonthlySoftwareConfigurationFactory.singleton()
					.createMonthlyConfiguration(submitContract.getUserId());
			monthlyConfigurtn.setConfigrtnActionCode(submitContract.getConfigrtnActionCode());
			monthlyConfigurtn.setChrgAgrmtNum(submitContract.getChrgAgrmtNum());
			monthlyConfigurtn.setWebQuoteNum(webQuoteNum);
			monthlyConfigurtn.setConfigrtnId(orgConfigId);
			monthlyConfigurtn.setConfigrtnActionCode(submitContract.getConfigrtnActionCode());
			monthlyConfigurtn.setEndDate(getEndDate(submitContract, configuratorPartsFromPage, masterMonthlySwPartsFromDbMap));
			monthlyConfigurtn.setNeedReconfigFlag(Boolean.FALSE);
			monthlyConfigurtn.setAddNewMonthlySWFlag(Boolean.FALSE);
			monthlyConfigurtn.setConfigrtnIdFromCa(orgConfigId);
			newlyAddedConfiguration = true;
			return monthlyConfigurtn;
		} else {
			// update
			for (MonthlySoftwareConfiguration monthlyConfigurtn : monthlySwConfigurationList) {
				monthlyConfigurtn.setConfigrtnActionCode(submitContract.getConfigrtnActionCode());
				monthlyConfigurtn.setChrgAgrmtNum(submitContract.getChrgAgrmtNum());
				monthlyConfigurtn.setNeedReconfigFlag(Boolean.FALSE);
				monthlyConfigurtn.setAddNewMonthlySWFlag(Boolean.FALSE);
				monthlyConfigurtn
						.setEndDate(getEndDate(submitContract, configuratorPartsFromPage, masterMonthlySwPartsFromDbMap));
				monthlyConfigurtn.setConfigrtnIdFromCa(orgConfigId);
				// always set dirty to config, force to always call SP to update
				// price_recalculate_flag = true
				// fix the issue of not call pricing service after update
				// configuration
				monthlyConfigurtn.setDirty();
				return monthlyConfigurtn;

			}
		}
		return null;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processForCoterm(java.util.Map,
     * java.util.Map, java.util.List)
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    protected CotermParameter processForCoterm(Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap,
            Map<String, MonthlySwLineItem> monthlylineItemsMap, List<MonthlySwConfiguratorPart> configuratorPartsFromPage) {
        // build replacedChrgAgrmParts. Refer to the logic implemented for SaaS in
        // ConfiguratorPartProcess_Impl.getCotermToPartInfo
        List<MonthlySwConfiguratorPart> existSubscrtnReplacedChrgAgrmParts = new ArrayList<MonthlySwConfiguratorPart>();
        List<MonthlySwConfiguratorPart> removedSubscrtnReplacedChrgAgrmParts = new  ArrayList<MonthlySwConfiguratorPart>();
        for (MonthlySwConfiguratorPart configuratorPartFromPage : configuratorPartsFromPage) {
            // skill all RampUpSubscription part, will deal with them in
            // Subscription part
            if (configuratorPartFromPage instanceof MonthlySwRampUpSubscriptionConfiguratorPart) {
                continue;
            }

            // came from CA or web
            MonthlySwConfiguratorPart configuratorPartFromDB = masterMonthlySwPartsFromDbMap.get(configuratorPartFromPage
                    .getPartKey());
            if (isNeedAddReplacePart(configuratorPartFromDB, configuratorPartFromPage)) {
            	if (configuratorPartFromPage.getPartQty() == null ||configuratorPartFromPage.getPartQty().intValue() <= 0 ){
            		removedSubscrtnReplacedChrgAgrmParts.add(configuratorPartFromDB);
            	} else {
            		existSubscrtnReplacedChrgAgrmParts.add(configuratorPartFromDB);
            	}
            }
        }
        
        List replacedChrgAgrmParts = existSubscrtnReplacedChrgAgrmParts;
        
        if (removedSubscrtnReplacedChrgAgrmParts != null && removedSubscrtnReplacedChrgAgrmParts.size() > 0){
        	replacedChrgAgrmParts = removedSubscrtnReplacedChrgAgrmParts;
        }
        CotermParameter cotermParameter = calculateCoTerm(replacedChrgAgrmParts);
        if (replacedChrgAgrmParts.size() > 0) {
            if (cotermParameter != null) {
                logContext.debug(this, "Coterm parameter derived from replaced parts: " + cotermParameter.toString());
                return cotermParameter;
            } else {
                logContext.debug(this, "Unable to derive coterm parameter from replaced parts");
            }
        }
        ArrayList<MonthlySwConfiguratorPart> chrgAgrmLineItemList = new ArrayList<MonthlySwConfiguratorPart>();
        Iterator iterator = masterMonthlySwPartsFromDbMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            chrgAgrmLineItemList.add(masterMonthlySwPartsFromDbMap.get(key));
        }
        cotermParameter = calculateCoTerm(chrgAgrmLineItemList);
        if (cotermParameter != null) {
            logContext.debug(this, "Coterm parameter derived from not replaced CA parts: " + cotermParameter.toString());
            return cotermParameter;
        } else {
            logContext.debug(this, "Unable to derive coterm parameter from un-replaced parts");
            return null;
        }
    }

    /**
     * DOC Comment method "calculateCoTerm".
     * 
     * @param replacedChrgAgrmParts
     * @return
     */
    @SuppressWarnings("rawtypes")
    private CotermParameter calculateCoTerm(List<MonthlySwConfiguratorPart> replacedChrgAgrmParts) {
        Date latestEndDate = null;
        MonthlySwConfiguratorPart latestEndDatePart = null;
        for (Iterator iterator = replacedChrgAgrmParts.iterator(); iterator.hasNext();) {
            MonthlySwConfiguratorPart monthlySwConfiguratorPart = (MonthlySwConfiguratorPart) iterator.next();
            MonthlySwActionConfiguratorPart configuratorActionPart = monthlySwConfiguratorPart.getConfiguratorActionPart();
            if (configuratorActionPart != null) {
                // part end date before today.
                if (configuratorActionPart.getEndDate() == null
                        || DateUtil.isDateBeforeToday(configuratorActionPart.getEndDate())) {
                    if (configuratorActionPart.getRenewalEndDate() == null
                            || DateUtil.isDateBeforeToday(configuratorActionPart.getRenewalEndDate())) {
                        // do nothing
                    } else {
                        if (latestEndDate == null) {
                            latestEndDate = configuratorActionPart.getRenewalEndDate();
                            latestEndDatePart = monthlySwConfiguratorPart;
                        } else {
                            if (configuratorActionPart.getRenewalEndDate().after(latestEndDate)) {
                                latestEndDate = configuratorActionPart.getRenewalEndDate();
                                latestEndDatePart = monthlySwConfiguratorPart;
                            }
                        }
                    }
                } else {
                    if (latestEndDate == null) {
                        latestEndDate = configuratorActionPart.getEndDate();
                        latestEndDatePart = monthlySwConfiguratorPart;
                    } else {
                        if (configuratorActionPart.getEndDate().after(latestEndDate)) {
                            latestEndDate = configuratorActionPart.getEndDate();
                            latestEndDatePart = monthlySwConfiguratorPart;
                        }
                    }
                }
            }
        }
        if (latestEndDatePart != null) {
            MonthlySwActionConfiguratorPart configuratorActionPart = latestEndDatePart.getConfiguratorActionPart();
            return new CotermParameter(latestEndDate, configuratorActionPart == null ? 0
                    : configuratorActionPart.getRefDocLineNum(), "");
        }
        return null;
    }

}
