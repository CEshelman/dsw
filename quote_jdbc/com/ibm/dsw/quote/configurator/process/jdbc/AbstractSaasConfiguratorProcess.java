package com.ibm.dsw.quote.configurator.process.jdbc;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants.PartRelNumAndTypeDefault;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SaasConfiguration;
import com.ibm.dsw.quote.common.domain.SaasPart;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDao;
import com.ibm.dsw.quote.configurator.dao.impl.SaasConfiguratorDaoJdbc;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.DomainAdapter;
import com.ibm.dsw.quote.configurator.helper.AddOnTradeUpReasonCode;
import com.ibm.dsw.quote.configurator.helper.ConfigurationRetrievalService;
import com.ibm.dsw.quote.configurator.helper.ConfiguratorUtil;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcess_Impl;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.builder.DraftQuoteBuilder;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractSaasConfiguratorProcess extends SaasConfiguratorProcess_Impl {
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	String configrtnId;
	String orgConfigrtnId;
	protected List<ConfiguratorPart> chrgAgrmPartList = new ArrayList<ConfiguratorPart>();
	protected String configrtnErrCode;
	protected List<String> missingPartLst = null;// refer to rtc#215704
	protected SaasConfiguratorDao saasConfiguratorDao = new SaasConfiguratorDaoJdbc();

    protected Map<ConfiguratorPart, ConfiguratorPartCombine> configuratorPartCombineMap = new HashMap<ConfiguratorPart, ConfiguratorPartCombine>();

	@Override
	public void addSaasPartsToQuote(AddOrUpdateConfigurationContract contract)
			throws TopazException, QuoteException {
		addConfiguratorPartsToQuote(contract);
		setDefaultProvisngDays(contract.getWebQuoteNum());
		removeRenwlModel(contract);
		subsequentProcess(contract.getWebQuoteNum(), contract.getUserId());
	}

	protected void addConfiguratorPartsToQuote(
			AddOrUpdateConfigurationContract ct) throws TopazException,
			QuoteException {
		try {
			TransactionContextManager.singleton().begin();
			List<ConfiguratorPart> allConfiguratorPartsFrmPid = ct.getAllPartsFrmPid();
			Map<String, ConfiguratorPart> configuratorPartFrmPidMap = convertConfiguratorPartListToMap(allConfiguratorPartsFrmPid);
            List<ConfiguratorPart> configuratorPartList = filterConfiguratorParts(ct, configuratorPartFrmPidMap,
                    configuratorPartCombineMap);
			configrtnId = getConfigId(ct);
			orgConfigrtnId = getOrgConfigrtnId(ct);
			if (isSkeepTheProcess(configuratorPartList)) {
				return;
			}
			List allPartsFromQuote = getAllPartsFromQuote(ct.getWebQuoteNum());
			List allSaaSPartsFromQuote = getAllSaasPartsFromQuote(allPartsFromQuote);

			String cotermConfigId = getCotermConfigrtnId(configrtnId, orgConfigrtnId);
			CotermParameter parameter = processForCoterm(configuratorPartList, ct.getChrgAgrmtNum(), cotermConfigId,
					configuratorPartFrmPidMap, ct.getConfigrtnActionCode());

			List<ConfiguratorPart> fullLineItemList = processForRelatedParts(configuratorPartList);
			if (isExceedPartNumLimit(allPartsFromQuote, configuratorPartList)) {
				ct.setExceedCode(DBConstants.DB2_SP_EXCEED_MAX);
				return;
			}
            createQuoteLineItem(configrtnId, ct, fullLineItemList, parameter, allSaaSPartsFromQuote);
			TransactionContextManager.singleton().commit();
		} catch (TopazException e) {
			logContext.error(this, e);
			TransactionContextManager.singleton().rollback();
			throw e;
		}
	}

	public List getAllPartsFromQuote(String webQuoteNum) throws TopazException {
		return QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
				webQuoteNum);
	}

	public List getAllSaasPartsFromQuote(List allParts) throws TopazException {
		List list = new ArrayList();
		if (allParts != null) {
			list = QuoteLineItemFactory.singleton().findSaaSLineItems(allParts);
		}
		return list;
	}

    protected abstract List filterConfiguratorParts(AddOrUpdateConfigurationContract ct, Map configuratorPartMap,
            Map<ConfiguratorPart, ConfiguratorPartCombine> configuratorPartCombineMap) throws TopazException, QuoteException;

	protected String getConfigId(AddOrUpdateConfigurationContract ct) {
		String configrtnId = ct.getConfigId();
		if (StringUtils.isBlank(configrtnId)) {
			configrtnId = QuoteCommonUtil.getNewSQOConfigurationId(ct.getPid());
		}
		return configrtnId;
	}

	protected abstract boolean shouldCoTerm();

	protected abstract boolean shouldCoTermToConfigrtn();

	protected String getCotermConfigrtnId(String configrtnId,
			String orgConfigrtnId) {
		String cotermConfigrtnId = null;
		if (shouldCoTerm()) {
			cotermConfigrtnId = configrtnId;
			if (!shouldCoTermToConfigrtn()) {
				cotermConfigrtnId = null;
			}
		}
		return cotermConfigrtnId;
	}

	protected CotermParameter processForCoterm(
			List<ConfiguratorPart> configuratorPartList, String chrgAgrmtNum,
			String configrtnId,
			Map<String, ConfiguratorPart> configuratorPartMap, String actionCode)
			throws QuoteException {
		List<DomainAdapter> replacedParts = new ArrayList<DomainAdapter>();

		for (ConfiguratorPart part : configuratorPartList) {
			if (part.isPartReplaced()) {
				replacedParts.add(DomainAdapter.create(part));
			}
		}

		CotermParameter ctParam = getCotermToPartInfo(chrgAgrmtNum,
				configrtnId, replacedParts, actionCode);
		if (ctParam == null) {
			return null;
		}

		for (ConfiguratorPart part : configuratorPartList) {
			if ((part.isSubscrptn() && !part.isPartReplaced())
					|| part.isSetUp() || part.isSubsumedSubscrptn()) {
				part.setRelatedCotermLineItmNum(ctParam
						.getRefDocLineItemSeqNum());
			}
		}

		return ctParam;
	}

	/**
	 * 
	 * @param chrgAgrmtNum
	 * @param configrtnId
	 * @param replacedParts
	 * @param actionCode
	 * @return
	 * @throws QuoteException
	 */
	public CotermParameter getCotermToPartInfo(String chrgAgrmtNum,
			String configrtnId, List<DomainAdapter> replacedParts,
			String actionCode) throws QuoteException {
		// 1.Retrieve all active subscription parts within a charge agreement
		List<ConfiguratorPart> chrgAgrmLineItemList = saasConfiguratorDao
				.getSubPartsFromChrgAgrm(chrgAgrmtNum, configrtnId);
		/**
		 * <pre>
		 * 2.Compute replaced subscription part
		 * 2.1 Replaced subscription subscription parts within current CA are stored in replacedChrgAgrmParts 
		 * 2.2 replaced subscription parts are deleted from chrgAgrmLineItemList and replacedParts
		 * </pre>
		 */
		List<ConfiguratorPart> replacedChrgAgrmParts = new ArrayList<ConfiguratorPart>();
		for (Iterator<ConfiguratorPart> chrgIt = chrgAgrmLineItemList
				.iterator(); chrgIt.hasNext();) {
			ConfiguratorPart part = chrgIt.next();

			if (!part.isSubscrptn() && !part.isSubsumedSubscrptn()) {
				chrgIt.remove();
				continue;
			}

			for (Iterator<DomainAdapter> it = replacedParts.iterator(); it
					.hasNext();) {
				DomainAdapter adapter = it.next();

				if (adapter.getPartNum().equals(part.getPartNum())
						&& (adapter.getRefDocLineItemNum() == part
								.getRefDocLineNum())) {
					chrgIt.remove();
					it.remove();

					replacedChrgAgrmParts.add(part);
					break;
				}
			}
		}
		// 3.get coter parameter from replaced part within CA
		CotermParameter param = null;
		if (replacedChrgAgrmParts.size() > 0) {
			param = calculateCoTerm(replacedChrgAgrmParts, actionCode);

			if (param != null) {
				logContext.debug(this,
						"Coterm parameter derived from replaced parts: "
								+ param.toString());
				return param;
			} else {
				logContext
						.debug(this,
								"Unable to derive coterm parameter from replaced parts");
			}
		}
		// 4.get coter parameter from no-replated part within CA
		param = calculateCoTerm(chrgAgrmLineItemList, actionCode);

		if (param != null) {
			logContext.debug(this,
					"Coterm parameter derived from not replaced CA parts: "
							+ param.toString());
			return param;
		} else {
			logContext.debug(this,
					"Unable to derive coterm parameter from un-replaced parts");
			return null;
		}
	}

    protected class ConfiguratorPartInfo {

        Date latestEndDate;

        ConfiguratorPart latestEndDatePart;

        /**
         * DOC ConfiguratorPartInfo constructor comment.
         * 
         * @param latestEndDate
         * @param latestEndDatePart
         */
        public ConfiguratorPartInfo(Date latestEndDate, ConfiguratorPart latestEndDatePart) {
            this.latestEndDate = latestEndDate;
            this.latestEndDatePart = latestEndDatePart;
        }

    }

	private CotermParameter calculateCoTerm(List<ConfiguratorPart> list,
			String actionCode) {
        Date latestEndDate = null;
        ConfiguratorPart latestEndDatePart = null;

        // use ConfiguratorPartInfo to accept return results: latestEndDate, latestEndDatePart
        ConfiguratorPartInfo configuratorPartInfo = new ConfiguratorPartInfo(latestEndDate, latestEndDatePart);
		for (ConfiguratorPart part : list) {
			if (!part.isSubscrptn() && !part.isSubsumedSubscrptn()) {
				continue;
			}

            initialCoterm(part, configuratorPartInfo);
            // set return results back to variables
            latestEndDate = configuratorPartInfo.latestEndDate;
            latestEndDatePart = configuratorPartInfo.latestEndDatePart;

			if (skillAllCotermCalculation(part.getCotermEndDate())) {
				break;
			}
			if (skillOnePartCotermCalculation(part.getCotermEndDate())) {
				continue;
			}


			if (part.getEndDate() == null
					|| DateUtil.isDateBeforeToday(part.getEndDate())) {

				if (part.getRenewalEndDate() == null
						|| DateUtil.isDateBeforeToday(part.getRenewalEndDate())) {
					if (ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY
							.equals(part.getSapBillgFrqncyOptCode())) {
						if (part.getNextRenwlDate() != null) {
							latestEndDate = DateUtil.plusMonth(
									part.getNextRenwlDate(),
									part.getRenwlTermMths());
							latestEndDatePart = part;
						}
					}
				} else {
					if (latestEndDate == null) {
						latestEndDate = part.getRenewalEndDate();
						latestEndDatePart = part;
					} else {
						if (part.getRenewalEndDate().after(latestEndDate)) {
							latestEndDate = part.getRenewalEndDate();
							latestEndDatePart = part;
						}
					}
				}
			} else {
				if (latestEndDate == null) {
					latestEndDate = part.getEndDate();
					latestEndDatePart = part;
				} else {
					if (part.getEndDate().after(latestEndDate)) {
						latestEndDate = part.getEndDate();
						latestEndDatePart = part;
					}
				}
			}
		}

		if (latestEndDatePart != null) {
			return new CotermParameter(latestEndDate,
					latestEndDatePart.getRefDocLineNum(),
					latestEndDatePart.getConfigrtnId());
		}

		return null;
	}

	protected List<ConfiguratorPart> associateParts(List<ConfiguratorPart> list) {
		List<ConfiguratorPart> lineItemList = new ArrayList<ConfiguratorPart>();
		Map<String, SubscrptnPartGroup> map = new HashMap<String, SubscrptnPartGroup>();
		Map<String, SetUpPartGroup> setUpMap = new HashMap<String, SetUpPartGroup>();

		for (ConfiguratorPart part : list) {
			// For add-on/trade-up, there could be duplicate
			// subscription parts with same part number, just ignore replaced
			// parts
			if (part.isPartReplaced()) {
				lineItemList.add(part);
				continue;
			}

			if (part.isSubscrptn() || part.isDaily() || part.isOvrage()) {
				SubscrptnPartGroup grp = map.get(part.getSubId());

				if (grp == null) {
					grp = new SubscrptnPartGroup();
					map.put(part.getSubId(), grp);
				}
				grp.addPart(part);

			}

			// set AssociateSubscrptnPart for setup Part by wwideProdCode
			if (part.isSetUp()) {
				for (ConfiguratorPart partInner : list) {
					if (partInner.isSubscrptn()
							&& partInner.getWwideProdCode().equals(
									part.getWwideProdCode())) {
						part.setAssociatedSubscrptnPart(partInner);
						break;
					}
				}
			}

			// set AssociateSubscrptnPart group for setup Part by subId
			if (part.isSubscrptn() || part.isSetUp()
					|| part.isSubsumedSubscrptn()) {

				SetUpPartGroup setUpGroup = setUpMap.get(part.getSubId());
				if (setUpGroup == null) {
					setUpGroup = new SetUpPartGroup();
					setUpMap.put(part.getSubId(), setUpGroup);
				}
				setUpGroup.addPart(part);

			} else if (!part.isOvrage() && !part.isDaily()) {
				lineItemList.add(part);
			}
		}

		for (SubscrptnPartGroup grp : map.values()) {
			if (grp.hasSubscriptionPart()) {
				lineItemList.add(grp.getSubscrptnPart());
			}
		}

		for (SetUpPartGroup setUpGrp : setUpMap.values()) {
			if (setUpGrp.hasSetUpPart()) {
				lineItemList.add(setUpGrp.getSetUpPart());
			}
		}

		return lineItemList;
	}

	class SubscrptnPartGroup {
		void addPart(ConfiguratorPart part) {
			if (part.isSubscrptn()) {
				this.subscrptnPart = part;
			}

			if (part.isDaily()) {
				this.dailyPart = part;
			}

			if (part.isOvrage()) {
				this.overagePart = part;
			}
		}

		boolean hasSubscriptionPart() {
			return subscrptnPart != null;
		}

		ConfiguratorPart getSubscrptnPart() {
			subscrptnPart.setAssociatedDailyPart(dailyPart);
			subscrptnPart.setAssociatedOveragePart(overagePart);

			return subscrptnPart;
		}

		ConfiguratorPart subscrptnPart;
		ConfiguratorPart dailyPart;
		ConfiguratorPart overagePart;
	}

	class SetUpPartGroup {
		void addPart(ConfiguratorPart part) {
			if (part.isSubscrptn()) {
				this.subscrptnPart = part;
			}

			if (part.isSetUp()) {
				this.setUpPart = part;
			}

			if (part.isSubsumedSubscrptn()) {
				this.subsumedScrptnPart = part;
			}
		}

		boolean hasSetUpPart() {
			return setUpPart != null;
		}

		ConfiguratorPart getSetUpPart() {
			setUpPart.setAssociatedSubsumedSubscrptnPart(subsumedScrptnPart);

			setUpPart.setAssociatedSubscrptnPart(subscrptnPart);

			return setUpPart;
		}

		ConfiguratorPart subscrptnPart;
		ConfiguratorPart setUpPart;
		ConfiguratorPart subsumedScrptnPart;
	}

    private List processForRelatedParts(
			List<ConfiguratorPart> configuratorPartList) {
		List<ConfiguratorPart> fullLineItemList = associateParts(configuratorPartList);

		List<ConfiguratorPart> setUpPart = new ArrayList<ConfiguratorPart>();
		List<ConfiguratorPart> subscrptnPart = new ArrayList<ConfiguratorPart>();
		List<ConfiguratorPart> others = new ArrayList<ConfiguratorPart>();

		for (ConfiguratorPart part : fullLineItemList) {
			if (part.isSetUp()) {
				setUpPart.add(part);

			} else if (part.isSubscrptn()) {
				List<ConfiguratorPart> rampUpList = part.getRampUpLineItems();
				if (rampUpList != null && rampUpList.size() > 0) {
					for (int i = 0; i < rampUpList.size(); i++) {
						ConfiguratorPart rampUpPart = rampUpList.get(i);

						// associate daily part with the first ramp up period
						if (i == 0 && part.getAssociatedDailyPart() != null) {
							rampUpPart.setAssociatedDailyPart(part
									.getAssociatedDailyPart());
							part.setAssociatedDailyPart(null);
						}

						if (part.getAssociatedOveragePart() != null) {
							rampUpPart.setAssociatedOveragePart(part
									.getAssociatedOveragePart().clone());
						}
					}
				}
				subscrptnPart.add(part);

			} else {
				others.add(part);
			}
		}

		fullLineItemList.clear();

		int destObjSeqNum = 0;
		for (ConfiguratorPart part : subscrptnPart) {

			int lastRampSeqNum = destObjSeqNum;
			boolean hasRampUp = false;

			List<ConfiguratorPart> rampUpList = part.getRampUpLineItems();
			if (rampUpList != null && rampUpList.size() > 0) {
				hasRampUp = true;

				for (int k = 0; k < rampUpList.size(); k++) {
					ConfiguratorPart rampUpPart = rampUpList.get(k);
					rampUpPart
							.setRampUpFlag(ConfiguratorConstants.RAMP_FLAG_YES);

					fullLineItemList.add(rampUpPart);
					int j = destObjSeqNum;
					rampUpPart.setDestObjSeqNum(destObjSeqNum);
					destObjSeqNum++;

					// First ramp up will get related seq num as null
					if (k != 0) {
						rampUpPart.setRelatedSeqNum(lastRampSeqNum);
					}
					lastRampSeqNum = j;

					// Process for overage and daily parts
					ConfiguratorPart dailyPart = rampUpPart
							.getAssociatedDailyPart();
					ConfiguratorPart overagePart = rampUpPart
							.getAssociatedOveragePart();

					if (dailyPart != null) {
						fullLineItemList.add(dailyPart);
						dailyPart.setDestObjSeqNum(destObjSeqNum);
						destObjSeqNum++;
						dailyPart.setRelatedSeqNum(rampUpPart
								.getDestObjSeqNum());
					}
					if (overagePart != null) {
						fullLineItemList.add(overagePart);
						overagePart.setDestObjSeqNum(destObjSeqNum);
						destObjSeqNum++;
						overagePart.setRelatedSeqNum(rampUpPart
								.getDestObjSeqNum());
					}
				}
			}

			fullLineItemList.add(part);
			part.setDestObjSeqNum(destObjSeqNum);
			destObjSeqNum++;
			if (hasRampUp) {
				part.setRelatedSeqNum(lastRampSeqNum);
			}

			// Process for overage and daily parts
			ConfiguratorPart dailyPart = part.getAssociatedDailyPart();
			ConfiguratorPart overagePart = part.getAssociatedOveragePart();

			if (dailyPart != null) {
				fullLineItemList.add(dailyPart);
				dailyPart.setDestObjSeqNum(destObjSeqNum);
				destObjSeqNum++;
				dailyPart.setRelatedSeqNum(part.getDestObjSeqNum());
			}
			if (overagePart != null) {
				fullLineItemList.add(overagePart);
				overagePart.setDestObjSeqNum(destObjSeqNum);
				destObjSeqNum++;
				overagePart.setRelatedSeqNum(part.getDestObjSeqNum());
			}
		}

		for (ConfiguratorPart part : setUpPart) {
			fullLineItemList.add(part);

			part.setDestObjSeqNum(destObjSeqNum);
			destObjSeqNum++;

			ConfiguratorPart subsumedPart = part
					.getAssociatedSubsumedSubscrptnPart();

			if (subsumedPart != null) {
				fullLineItemList.add(subsumedPart);
				subsumedPart.setDestObjSeqNum(destObjSeqNum);
				destObjSeqNum++;
				part.setRelatedSeqNum(subsumedPart.getDestObjSeqNum());
				subsumedPart.setRelatedSeqNum(part.getDestObjSeqNum());
			} else {
				ConfiguratorPart subPart = part.getAssociatedSubscrptnPart();
				if (subPart != null) {
					part.setRelatedSeqNum(subPart.getDestObjSeqNum());
				}
			}

		}

		for (ConfiguratorPart part : others) {
			fullLineItemList.add(part);
			part.setDestObjSeqNum(destObjSeqNum);
			destObjSeqNum++;
		}

		for (ConfiguratorPart part : fullLineItemList) {
			if (part.isPartReplaced()) {
				// This is to set related seq number for related parts
				part.setReplacementRelation();
			}
			// call setRelatedSeqNum for Subsumed Subscription Part and Set Up
			// part. call setPartQtyStr and
			// setPartQty
			// for Subsumed Subscription Part
			if (part.isSubsumedSubscrptn()) {
				ConfiguratorPart relatedSetUpPart = getSetUpPart(
						fullLineItemList, part);
				part.setPartQty(relatedSetUpPart.getPartQty());
				part.setPartQtyStr(relatedSetUpPart.getPartQty() + "");
				part.setBillgUpfrntFlag("1");
				part.setBillingFrequencyCode(ConfiguratorConstants.BILLING_FREQUENCY_UPFRONT);
			}

		}

		return fullLineItemList;
	}

	/**
	 * DOC get the corresponding set up part of the subsumed subscription part
	 * 
	 * @param cps
	 * @param subsumedSubscriptionPart
	 * @return
	 */
	private ConfiguratorPart getSetUpPart(List<ConfiguratorPart> cps,
			ConfiguratorPart subsumedSubscriptionPart) {
		for (ConfiguratorPart cp : cps) {
			if (cp.isSetUp()
					&& cp.getSubId()
							.equals(subsumedSubscriptionPart.getSubId())) {
				return cp;
			}
		}
		return null;
	}

	private boolean isExceedPartNumLimit(List quoteExistingLineItems,
			List configratorPartList) {
		int quoteExistingPartCounts = quoteExistingLineItems == null ? 0 : quoteExistingLineItems.size();
		int configratorPartCounts = configratorPartList == null ? 0 : configratorPartList.size();
		int limit = PartPriceConfigFactory.singleton().getElaLimits();
		if (quoteExistingPartCounts + configratorPartCounts > limit) {
			return true;
		}
		return false;
	}

	private void setTermExtensionToContract(AddOrUpdateConfigurationContract ct)
			throws TopazException {
		if (StringUtils.isEmpty(ct.getConfigId()))
			return;

		List<PartsPricingConfiguration> configurators = this.saasConfiguratorDao
				.findConfiguratorsByWebQuoteNum(ct.getWebQuoteNum());

		if (configurators != null) {
			for (PartsPricingConfiguration ppc : configurators) {
				if (ppc.getConfigrtnId().equals(ct.getConfigId())) {
					ct.setTermExtensionFlag(ppc.isTermExtension() ? "1" : "0");
					ct.setSeviceDate(DateUtil.formatDate(ppc.getServiceDate()));
					ct.setServiceDateModType(ppc.getServiceDateModType() != null ? ppc
							.getServiceDateModType().toString() : null);
					break;
				}
			}
		}

	}

    private void createQuoteLineItem(String configrtnId, AddOrUpdateConfigurationContract ct,
            List<ConfiguratorPart> configuratorPartList, CotermParameter cotermParameter, List quoteExistingSaasPartList)
            throws TopazException, QuoteException {
		// 1.compute coTerm ref information
		Date coTermEndDate = getCotermEndDate4AddPart(ct.getCaEndDate(), cotermParameter);
		String coTermConfigrtnId = getCotermConfigrtnId4AddPart(cotermParameter);
		// 2.compute overrideFlag
		// overrideFlag logic is changed, refer to rtc #200014
		String overrideFlag = ct.getOverrideFlag();
		if (StringUtils.isBlank(overrideFlag))
			overrideFlag = ConfiguratorConstants.OVERRIDE_FLAG_NO;
		if (StringUtils.isNotBlank(ct.getOverridePilotFlag())
				&& ct.getOverridePilotFlag().equals(
						ConfiguratorConstants.OVERRIDE_PILOT_FLAG_YES)) {
			overrideFlag = ConfiguratorConstants.OVERRIDE_FLAG_YES;
		} else if (StringUtils.isNotBlank(ct.getOverrideRstrctFlag())
				&& ct.getOverrideRstrctFlag().equals(
						ConfiguratorConstants.OVERRIDE_RSTRCT_FLAG_YES)) {
			overrideFlag = ConfiguratorConstants.OVERRIDE_FLAG_YES;
		}

		// 3.set TermExtention
		setTermExtensionToContract(ct);
		// 4. update EXT_ENTIRE_CONFIGRTN_FLAG
		int extEntireConfigFlag = getExtEntireConfigFlagAccordingToParts(chrgAgrmPartList, configuratorPartList,
						ct.getTermExtensionFlag());
		SaasConfiguration saasConfiguration = new SaasConfiguration();
		saasConfiguration.setWebQuoteNum(ct.getWebQuoteNum());
		saasConfiguration.setConfigrtnId(configrtnId);
		saasConfiguration.setConfigrtrConfigrtnId(ct.getCpqConfigurationID());
		saasConfiguration.setUserId(ct.getUserId());
		saasConfiguration.setRefDocNum(ct.getChrgAgrmtNum());
		saasConfiguration.setConfigrtnErrCode(configrtnErrCode);
		saasConfiguration.setConfigrtnActionCode(ct.getConfigrtnActionCode());
		saasConfiguration.setEndDate(coTermEndDate);
		saasConfiguration.setCotermConfigrtnId(coTermConfigrtnId);
		saasConfiguration.setConfigrtnOvrrdn(new Integer(overrideFlag));
		saasConfiguration.setImportFlag("0");
		saasConfiguration.setTermExtFlag(ct.getTermExtensionFlag());
		saasConfiguration
				.setServiceDate(DateUtil.parseDate(ct.getSeviceDate()));
		saasConfiguration.setServiceDateModType(ct.getServiceDateModType());
		saasConfiguration.setProvisioningId("");
		saasConfiguration.setConfigEntireExtended(extEntireConfigFlag);
		int destObjSeqNum = this.saasConfiguratorDao
				.addOrUpdateConfigrtn(saasConfiguration);

		// sort all configurator parts, let original existing parts sort in
		// front
        new ConfiguratorUtil().sortConfiguratorParts(configuratorPartList, quoteExistingSaasPartList);

        Map<ConfiguratorPart, QuoteLineItem> map = new HashMap<ConfiguratorPart, QuoteLineItem>();

        for (ConfiguratorPart part : configuratorPartList) {
            createQli(destObjSeqNum, configrtnId, ct.getWebQuoteNum(), ct.getUserId(), part, quoteExistingSaasPartList, ct,
                    cotermParameter, quoteExistingSaasPartList, configuratorPartList, map);
        }
        adjustReasonCodes4DailyOverageParts(map);

        adjustReasonCodes4AP_TA(map);

		StringBuilder sb = new StringBuilder("\n");
		for (ConfiguratorPart part : configuratorPartList) {
			sb.append(part.toString()).append("\n");
		}
		logContext
				.debug(this, "parts added to session quote: " + sb.toString());
	}

    /**
     * 3. Adding a new subscription to an existing service offering: <br>
     * b. With co-terming to an existing service configuration, <br>
     * Customer subscribes to one subscription under C&SI. They decide to add a second subscription to their existing
     * service configuration, and they co-term the new subscription to their existing subscription:<br>
     * <br>
     * <b>AP- Add Part</b> <br>
     * 5. Trade-up: Cancelling an existing subscription and replacing it with one or more new subscriptions within a
     * service offering <br>
     * b. With co-terming to an existing service configuration, <br>
     * Customer subscribes to one subscription under C&SI. They decide to replace it with a different subscription, and
     * they co-term the new subscription to their existing subscription. <br>
     * <br>
     * <b>TA - Trade Up Add</b>
     * 
     * @throws TopazException
     */
    private void adjustReasonCodes4AP_TA(Map<ConfiguratorPart, QuoteLineItem> map) throws TopazException {
        // if any TR, should change all AP to TA, except Set Up part
        boolean existTR = isExistTR(map);
        if (existTR) {
            for (Iterator<ConfiguratorPart> iterator = map.keySet().iterator(); iterator.hasNext();) {
                ConfiguratorPart part = iterator.next();
                QuoteLineItem quoteLineItem = map.get(part);
                if (quoteLineItem != null) {
                    if (AddOnTradeUpReasonCode.AP_ADD_REASON_CODE.equals(quoteLineItem.getAddReasonCode())) {
                        quoteLineItem.setAddReasonCode(AddOnTradeUpReasonCode.TA_ADD_REASON_CODE);
                    }
                }
            }
        }
    }

    /**
     * DOC Comment method "isExistTR".
     * 
     * @param map
     * @return
     */
    private boolean isExistTR(Map<ConfiguratorPart, QuoteLineItem> map) {
        for (Iterator<ConfiguratorPart> iterator = map.keySet().iterator(); iterator.hasNext();) {
            ConfiguratorPart part = iterator.next();
            QuoteLineItem quoteLineItem = map.get(part);
            if (AddOnTradeUpReasonCode.TR_REPLACED_REASON_CODE.equals(quoteLineItem.getReplacedReasonCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC Comment method "adjustReasonCodes4DailyOverageParts".
     * 
     * @param map
     * @param map
     * @throws TopazException
     */
    private void adjustReasonCodes4DailyOverageParts(Map<ConfiguratorPart, QuoteLineItem> map) throws TopazException {
        // adjust reason codes for overage parts: iterate all subsction parts, set reason codes for their overage parts;
        // for replaced parts
        for (Iterator<ConfiguratorPart> iterator = map.keySet().iterator(); iterator.hasNext();) {
            ConfiguratorPart part = iterator.next();
            if (part.isSubscrptn()) {
                QuoteLineItem subscrptnLineItem = map.get(part);
                ConfiguratorPart overagePart = getOveragePartFromCombineMap(part, map);
                if (overagePart != null && map.get(overagePart) != null) {
                    QuoteLineItem overageLineItem = map.get(overagePart);

                    overageLineItem.setAddReasonCode(subscrptnLineItem.getAddReasonCode());
                    overageLineItem.setReplacedReasonCode(subscrptnLineItem.getReplacedReasonCode());
                }

                ConfiguratorPart dailyPart = getDailyPartFromCombineMap(part, map);
                if (dailyPart != null && map.get(dailyPart) != null) {
                    QuoteLineItem dailyLineItem = map.get(dailyPart);
                    dailyLineItem.setAddReasonCode(subscrptnLineItem.getAddReasonCode() == null ? subscrptnLineItem
                            .getReplacedReasonCode() : subscrptnLineItem.getAddReasonCode());
                }

            }
        }

    }

    /**
     * DOC Comment method "getDailyPartFromCombineMap".
     * 
     * @param part
     * @param map 
     * @return
     */
    private ConfiguratorPart getDailyPartFromCombineMap(ConfiguratorPart part, Map<ConfiguratorPart, QuoteLineItem> map) {
        if (part.isPartReplaced()) {
            ConfiguratorPartCombine configuratorPartCombine = configuratorPartCombineMap.get(part);
            if (configuratorPartCombine != null) {
                return configuratorPartCombine.getAssociatedDailyPart();
            }
        }
        // for new parts, which replaced 'replaced' parts:
		else {
            for (Iterator<ConfiguratorPart> iterator = map.keySet().iterator(); iterator.hasNext();) {
                ConfiguratorPart configuratorPart = iterator.next();
                // find the corresponding replaced subscription part.
                if (configuratorPart.getPartNum().equals(part.getPartNum()) && !configuratorPart.equals(part)) {
                    // find associatedDailyPart of the subscription part, then use the partNum of the
                    // associatedDailyPart to find corresponding new daily part
                    ConfiguratorPartCombine configuratorPartCombine = configuratorPartCombineMap.get(configuratorPart);
                    if (configuratorPartCombine != null) {
                        ConfiguratorPart associatedDailyPart = configuratorPartCombine.getAssociatedDailyPart();
						if (associatedDailyPart != null) {
							ConfiguratorPart dailyPart = getConfiguratorPartFromMap(
									associatedDailyPart, map);
							return dailyPart;
						}
                    }
                }
            }
        }
        // for new parts, which without 'replaced' parts: which means it's added new subscription part
        if (!part.isPartReplaced()) {
            if (part.getAssociatedDailyPart() != null) {
                return part.getAssociatedDailyPart();
            }
        }

        return null;
    }

    /**
     * DOC Comment method "getConfiguratorPartFromMap".
     * 
     * @param associatedPart
     * @param map
     * @return
     */
    private ConfiguratorPart getConfiguratorPartFromMap(ConfiguratorPart associatedPart,
            Map<ConfiguratorPart, QuoteLineItem> map) {
        String partNum = associatedPart.getPartNum();
        for (Iterator<ConfiguratorPart> iterator = map.keySet().iterator(); iterator.hasNext();) {
            ConfiguratorPart configuratorPart = iterator.next();
            if (configuratorPart.getPartNum().equals(partNum) && !associatedPart.equals(configuratorPart)) {
                return configuratorPart;
            }
        }
        return null;
    }

    /**
     * DOC Comment method "getOveragePartFromCombineMap".
     * 
     * @param part
     * @param map 
     * @return
     */
    private ConfiguratorPart getOveragePartFromCombineMap(ConfiguratorPart part, Map<ConfiguratorPart, QuoteLineItem> map) {
        if (part.isPartReplaced()) {
            ConfiguratorPartCombine configuratorPartCombine = configuratorPartCombineMap.get(part);
            if (configuratorPartCombine != null) {
                return configuratorPartCombine.getAssociatedOveragePart();
            }
		} else {
            for (Iterator<ConfiguratorPart> iterator = map.keySet().iterator(); iterator.hasNext();) {
                ConfiguratorPart configuratorPart = iterator.next();
                // find the corresponding replaced subscription part.
                if (configuratorPart.getPartNum().equals(part.getPartNum()) && !configuratorPart.equals(part)) {
                    // find associatedDailyPart of the subscription part, then use the partNum of the
                    // associatedDailyPart to find corresponding new daily part
                    ConfiguratorPartCombine configuratorPartCombine = configuratorPartCombineMap.get(configuratorPart);
                    if (configuratorPartCombine != null) {
						ConfiguratorPart associatedOveragePart = configuratorPartCombine
								.getAssociatedOveragePart();
						if (associatedOveragePart != null) {
							ConfiguratorPart overagePart = getConfiguratorPartFromMap(
									associatedOveragePart, map);
							return overagePart;
						}
                    }
                }
            }
        }
        if (!part.isPartReplaced()) {
            if (part.getAssociatedOveragePart() != null) {
                return part.getAssociatedOveragePart();
            }
        }

        return null;
    }

	private boolean  isSamePartTypeAndNum(QuoteLineItem lineItem,ConfiguratorPart part){
    	boolean result=false;
		// same part number and both of them are ramp-up
		if (lineItem.isRampupPart() && part.isRampUp()
				&& lineItem.getPartNum().equals(part.getPartNum())) {
    		result=true;
    	}
		// same part number and both of them are subscription
		if ((!lineItem.isRampupPart() && lineItem.isSaasSubscrptnPart())
				&& (part.isSubscrptn() && !part.isRampUp())
				&& lineItem.getPartNum().equals(part.getPartNum())) {
    		result=true;
    	}
		return result;
    }
    protected void createQli(int baseDestObjNum, String configrtnId, String webQuoteNum, String userId, ConfiguratorPart part,
            List SaaSLineItems, AddOrUpdateConfigurationContract ct, CotermParameter cotermParameter,
            List quoteExistingSaasPartList, List<ConfiguratorPart> configuratorPartList, Map<ConfiguratorPart, QuoteLineItem> map)
            throws TopazException, QuoteException {

        QuoteHeader quoteHeader = QuoteHeaderFactory.singleton()
				.findByWebQuoteNum(webQuoteNum);

        QuoteLineItem qli = QuoteLineItemFactory.singleton().createQuoteLineItem(webQuoteNum, new SaasPart(part.getPartNum()),
                userId);

        map.put(part, qli);

		for (Object obj : SaaSLineItems) {
			QuoteLineItem lineItem = (QuoteLineItem) obj;
			if (lineItem.isReplacedPart())
				continue;
			if (lineItem.getPartNum().equals(part.getPartNum())) {
				qli.setOverrideUnitPrc(lineItem.getOverrideUnitPrc());
				qli.setOvrrdExtPrice(lineItem.getOvrrdExtPrice());
				qli.setLocalExtProratedDiscPrc(lineItem.getOvrrdExtPrice());
				qli.setLineDiscPct(lineItem.getLineDiscPct());
				qli.setManualSortSeqNum(lineItem.getManualSortSeqNum());
				qli.setSwSubId(lineItem.getSwSubId());
				qli.setRevnStrmCodeDesc(lineItem.getRevnStrmCodeDesc());
				qli.setSeqNum(lineItem.getSeqNum());
				qli.setChnlOvrrdDiscPct(lineItem.getChnlOvrrdDiscPct());
				break;
			}
		}
		// because ramp-up and subscription part have different isNewSerive
		// value,so add this logic.
		for (Object obj : SaaSLineItems) {
			QuoteLineItem lineItem = (QuoteLineItem) obj;
			if (lineItem.isReplacedPart())
				continue;
			if (isSamePartTypeAndNum(lineItem, part)) {
				qli.setSaasRenwl(lineItem.isSaasRenwl());
				qli.setWebMigrtdDocFlag(lineItem.isWebMigrtdDoc());
				qli.setIsNewService(lineItem.isNewService());
				break;
			}
		}

		qli.setSwProdBrandCode(part.getSwProdBrandCode());
		qli.setProrateFlag(false);
		qli.setAssocdLicPartFlag(false);
		qli.setConfigrtnId(configrtnId);

		// Every part will get a dest seq num
		qli.setDestSeqNum(part.getDestObjSeqNum() + baseDestObjNum);
		qli.setSeqNum(qli.getDestSeqNum());
		qli.setIRelatedLineItmNum(getRelatedSeqNum(part.getRelatedSeqNum(),
				baseDestObjNum));
		qli.setPartQty(part.getPartQty());
		qli.setBillgFrqncyCode(part.getBillingFrequencyCode());
		qli.setCumCvrageTerm(part.getTotalTerm());
        qli.setICvrageTerm(part.getTerm());
        // for fixing wrong iCvrageTerm of replaced quote line item.
        if (part.isPartReplaced()) {
            QuoteLineItem lineItem = getCorrespondingReplacedLineItem(SaaSLineItems, qli);
            if (lineItem != null) {
                qli.setICvrageTerm(lineItem.getICvrageTerm());
            }
        }
		// qli.setRefDocLineNum(part.getRefDocLineNum());
		Integer refDocLineNum = part.getRefDocLineNum() == null
				|| part.getRefDocLineNum().intValue() == 0 ? part
				.getRelatedCotermLineItmNum() : part.getRefDocLineNum();
		qli.setRefDocLineNum(refDocLineNum);

		qli.setRelatedCotermLineItmNum(part.getRelatedCotermLineItmNum());
		qli.setRampUp(part.isRampUp());
		qli.setReplacedPart(part.isPartReplaced());

		// FCT TO PA Finalization
		qli.setOrignlSalesOrdRefNum(part.getOrignlSalesOrdRefNum());
		qli.setOrignlConfigrtnId(part.getOrignlConfigrtnId());

		if (part.isPartReplaced()) {
			if (quoteHeader.isChannelQuote()) {
				if (part.isOvrage() || part.isAddiSetUp() || part.isOnDemand()) {
					qli.setLocalUnitProratedPrc(null);
					qli.setLocalUnitProratedDiscPrc(part
							.getLocalSaasOvrageAmt());
					qli.setChannelUnitPrice(part.getLocalSaasOvrageAmt());
				}
				if (part.isSubscrptn()) {
					qli.setLocalExtPrc(null);
					qli.setLocalExtProratedPrc(null);
					qli.setLocalExtProratedDiscPrc(part.getLocalExtndPrice());
					qli.setSaasBidTCV(part.getSaasTotCmmtmtVal());
					qli.setChannelExtndPrice(part.getLocalExtndPrice());
					qli.setSaasBpTCV(part.getSaasTotCmmtmtVal());
				}
			} else {
				if (part.isOvrage() || part.isAddiSetUp() || part.isOnDemand()) {
					qli.setLocalUnitProratedPrc(null);
					qli.setLocalUnitProratedDiscPrc(part
							.getLocalSaasOvrageAmt());
				}
				if (part.isSubscrptn()) {
					qli.setLocalExtPrc(null);
					qli.setLocalExtProratedPrc(null);
					qli.setLocalExtProratedDiscPrc(part.getLocalExtndPrice());
					qli.setSaasBidTCV(part.getSaasTotCmmtmtVal());
					// PL: 421951,fixed the total price missed. The detail
					// infomation
					// Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/FBF6BC9B291B7A9485257AD2005C4E3A
					qli.setChannelExtndPrice(part.getLocalExtndPrice());
					qli.setSaasBpTCV(part.getSaasTotCmmtmtVal());
				}
			}
		}
        try {
            int scwItemNumber = Integer.parseInt(part.getScwItemNumber());
            qli.setOriginatingItemNum(scwItemNumber);
        } catch (NumberFormatException e) {
            logContext.error(this, "error: wrong scwItemNumber: " + part.getScwItemNumber());
            // throw new QuoteException(e);
        }
        setReasonCodes(qli, configrtnId, webQuoteNum, part, ct, cotermParameter, quoteExistingSaasPartList, configuratorPartList);
        
        // 15.3 Set the part sub type based on hybrid flag
        // No matter newly added line item or updated line item(eg update QTY), it will be insert into web_quote_line_item
        // When submitting configurator, the PART_SUB_TYPE will be setted based on latest ODS hybrid offering flag.
        if(!part.isPartReplaced()){
            qli.setPartSubType(part.isHybridOfferg() ? QuoteConstants.PART_SUB_TYPE_HYBRID : null);
        }
	}

    /**
     * DOC Comment method "getCorrespondingLineItem".
     * 
     * @param saaSLineItems
     * @param qli
     * @return
     */
    private QuoteLineItem getCorrespondingReplacedLineItem(List saaSLineItems, QuoteLineItem qli) {
        for (Iterator iterator = saaSLineItems.iterator(); iterator.hasNext();) {
            QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
            if (quoteLineItem.isReplacedPart() && quoteLineItem.getPartNum().equals(qli.getPartNum())) {
                return quoteLineItem;
            }
        }
        return null;
    }

    /**
     * DOC Comment method "setReasonCodeForSCW".
     * 
     * @param qli
     * @param cotermParameter
     * @param quoteExistingSaasPartList
     * @param configuratorPartList
     * @throws TopazException
     */
    protected void setReasonCodes(QuoteLineItem qli, String configrtnId, String webQuoteNum, ConfiguratorPart part,
            AddOrUpdateConfigurationContract ct, CotermParameter cotermParameter, List quoteExistingSaasPartList,
            List<ConfiguratorPart> configuratorPartList) throws TopazException, QuoteException {
    }

    private Integer getRelatedSeqNum(Integer base, int increase) {
		if (base == null) {
			return PartRelNumAndTypeDefault.RELATED_LINE_ITM_NUM_DEFAULT;
		}

		return base + increase;
	}

	/**
	 * @param chrgAgrmPartList
	 * @param configuratorPartList
	 * @return for default implementation, return 0, for add-on/trade-up or
	 *         FCT_TO_PA, need to check child class
	 *         AbstractSaasConfiguratorUpdateCaProcess_jdbc
	 */
	protected int getExtEntireConfigFlagAccordingToParts(List<ConfiguratorPart> chrgAgrmPartList,
			List<ConfiguratorPart> configuratorPartList, String termExtensionFlagFrmContract) {
		return 0;
	}

	private boolean shouldSetProvDays() {
		return shouldCoTerm();
	}

	protected String getOrgConfigrtnId(AddOrUpdateConfigurationContract ct) {
		return "";
	}

	public void setDefaultProvisngDays(String webQuoteNum)
			throws TopazException {
		if (shouldSetProvDays()) {
			this.saasConfiguratorDao.setDefaultProvisngDays(webQuoteNum,
					this.configrtnId, this.orgConfigrtnId);
		}
	}

	protected boolean isSkeepTheProcess(List configuratorPartList) {
		if (configuratorPartList == null || configuratorPartList.size() == 0) {
			return true;
		} else {
			return false;
		}

	}


    protected void initialCoterm(ConfiguratorPart part, ConfiguratorPartInfo configuratorPartInfo) {
		// for default implementation, do nothing
	}

	protected boolean skillAllCotermCalculation(Date cotermEndDate) {
		// for default implementation, return false
		return false;
	}

	protected boolean skillOnePartCotermCalculation(Date cotermEndDate) {
		// for default implementation, return false
		return false;
	}

	/**
	 * @param caEndDateFrmContract
	 * @param cotermParameter
	 * @return for default implementation, if for FCT_TO_PA, need to check the
	 *         method in child class SaasConfiguratorFctToPaProcess_jdbc
	 */
	protected Date getCotermEndDate4AddPart(String caEndDateFrmContract, CotermParameter cotermParameter) {
		Date coTermEndDate = null;
		if (cotermParameter != null) {
			coTermEndDate = cotermParameter.getEndDate();
		}
		return coTermEndDate;
	}

	/**
	 * @param cotermParameter
	 * @return for default implementation, if for FCT_TO_PA, need to check the
	 *         method in child class SaasConfiguratorFctToPaProcess_jdbc
	 */
	protected String getCotermConfigrtnId4AddPart(CotermParameter cotermParameter) {
		String coTermConfigrtnId = null;
		if (cotermParameter != null) {
			coTermConfigrtnId = cotermParameter.getConfigrtnId();
		}
		return coTermConfigrtnId;
	}



	protected boolean setUpWithSameSubIdRemoved(String subId, List<ConfiguratorPart> list,
			Map<String, ConfiguratorPart> allPartsMap) {

		boolean hasSameSubIdSetUp = false;
		for (ConfiguratorPart part : allPartsMap.values()) {
			if (part.isSetUp() && part.getSubId().equals(subId)) {
				hasSameSubIdSetUp = true;
				break;
			}
		}

		// No existing set up with same sub id
		if (!hasSameSubIdSetUp) {
			logContext.debug(this, "no set up part for SubId=" + subId);
			return false;

		} else {
			logContext.debug(this, "find set up part for SubId=" + subId);

			// Check that same sub id set up is not deleted from configurator
			for (ConfiguratorPart part : list) {
				if (part.isSetUp() && part.getSubId().equals(subId)) {
					logContext.debug(this, "set up part returned from configurator for SubId=" + subId);
					return false;
				}
			}

			return true;
		}
	}

	protected boolean hasSetUpPartWithSameSubId(String subId, List<ConfiguratorPart> list) {
		for (ConfiguratorPart part : list) {
			if (part.isSetUp() && part.getSubId().equals(subId)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasSubscriptionPartWithSameSubId(String subId, List<ConfiguratorPart> list) {
		for (ConfiguratorPart part : list) {
			if (part.isSubscrptn() && part.getSubId().equals(subId)) {
				return true;
			}
		}

		return false;
	}

	protected boolean setUpPartWithSameSubIdReturned(String subId, List<ConfiguratorPart> list) {
		for (ConfiguratorPart part : list) {
			if (part.isSetUp() && part.getSubId().equals(subId)) {
				return true;
			}
		}

		return false;
	}

	protected void processForTradeUp(List<ConfiguratorPart> newPartList, List<ConfiguratorPart> removedPartList) {
		if (newPartList.size() == 0 || removedPartList.size() == 0) {
			return;
		}

		ConfiguratorPart minRefDocLineItemNumPart = null;
		int minRefDocLineItemNum = Integer.MAX_VALUE;

		// removedPartList is from CA, so every part has refDocLineItemNum
		for (ConfiguratorPart part : removedPartList) {
			if (part.getRefDocLineNum() < minRefDocLineItemNum) {
				minRefDocLineItemNum = part.getRefDocLineNum();
				minRefDocLineItemNumPart = part;
			}
		}

		ConfiguratorPart newPart = newPartList.get(0);
		// #todo remove set trade up part ref doc number
		// Notes://D01dbm17/8525788A006C6448//DCBF686CDD314599872578920047B86E
		// minRefDocLineItemNumPart.markAsReplaced(newPart);
		minRefDocLineItemNumPart.markAsReplaced();
	}

	protected List<ConfiguratorPart> getConfiguratorParts(AddOrUpdateConfigurationContract ct, Map<String, ConfiguratorPart> map)
			throws TopazException, QuoteException {
		List<ConfiguratorPart> list = new ArrayList<ConfiguratorPart>();

		if (ct.isFromCPQConfigurator()) {

			// Call web service here to get parts from CPQ
			try {
				StringBuffer bf = new StringBuffer();
				missingPartLst = new ArrayList<String>();// refer to rtc#215704
				list = ConfigurationRetrievalService.getInstance()
						.callWebService(ct.getCpqConfigurationID(), ct.getWebQuoteNum(), bf,
								PartPriceSaaSPartConfigFactory.singleton().shouldUseTokenCache(), missingPartLst);
				configrtnErrCode = bf.toString();

				setSaaSPartAttribute(list, map, ct.getPid(), ct.getTerm(),
						false);
			} catch (Exception e) {
				logContext.error(this, "error to retrieve parts from CPQ web service");
				throw new QuoteException(e);
			}

		} else {
			List<ConfiguratorPart> tmp = ct.getParts();
			setSaaSPartAttribute(tmp, map, ct.getPid(), ct.getTerm(), true);

			for (ConfiguratorPart part : tmp) {
				if (part.isAddiSetUp() && setUpWithSameSubIdRemoved(part.getSubId(), tmp, map)) {
					logContext.debug(this, "additional set up part " + part.toString()
							+ " removed from list as same SubId set up is not returned from configurator");
					continue;
				}

				if ((part.isOvrage() || part.isDaily()) && !hasSubscriptionPartWithSameSubId(part.getSubId(), tmp)) {
					continue;
				}

				if (part.isSubsumedSubscrptn() && !hasSetUpPartWithSameSubId(part.getSubId(), tmp)) {
					continue;
				}

				// Handle term, clear data for non-subscription parts
				if (!part.isSubscrptn() && !part.isSetUp() && !part.isSubsumedSubscrptn()) {
					part.setTotalTerm(null);
					part.setTerm(null);
				}

				list.add(part);
			}
		}

		return list;
	}

	private void removeRenwlModel(AddOrUpdateConfigurationContract ct) throws QuoteException, TopazException {
		try {
			TransactionContextManager.singleton().begin();
			// delete the redundant data from table EBIZ1.WEB_QUOTE_RENWL_MDL
			PartPriceProcess process;
			process = PartPriceProcessFactory.singleton().create();
			process.addRenwlModel(ct.getUserId(), "", ct.getWebQuoteNum(), "",
					QuoteConstants.SourceOfRenewalModel.FROM_BASIC_CONFIGURATOR);
			TransactionContextManager.singleton().commit();
		} catch (TopazException e) {
			logContext.error(this, e);
			TransactionContextManager.singleton().rollback();
			throw e;
		}
	}

    private void subsequentProcess(String webQuoteNum, String userId) throws TopazException, QuoteException {
		try {
			TransactionContextManager.singleton().begin();
			QuoteHeader quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);
			Quote quote = new Quote(quoteHeader);
			quote.fillLineItemsForQuoteBuilder();
			quote.fillAndSortSaasConfigurationForDraft();
			DraftQuoteBuilder builder = DraftQuoteBuilder.create(quote, userId);
			builder.calculateCoverageTerm(quote);
			builder.sortLineItems();
			TransactionContextManager.singleton().commit();
		} catch (TopazException e) {
			logContext.error(this, e);
			TransactionContextManager.singleton().rollback();
			throw e;
        } catch (QuoteException e) {
            logContext.error(this, e);
            TransactionContextManager.singleton().rollback();
            throw e;
        }
	}

}
