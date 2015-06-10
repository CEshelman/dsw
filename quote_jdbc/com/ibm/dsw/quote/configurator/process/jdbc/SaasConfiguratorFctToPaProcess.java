package com.ibm.dsw.quote.configurator.process.jdbc;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.topaz.exception.TopazException;

public class SaasConfiguratorFctToPaProcess extends AbstractSaasConfiguratorUpdateCaProcess {


	@Override
    protected List filterConfiguratorParts(AddOrUpdateConfigurationContract ct, Map configuratorPartMap,
            Map<ConfiguratorPart, ConfiguratorPartCombine> configuratorPartCombineMap) throws TopazException, QuoteException {

		List<ConfiguratorPart> lineItemList = new ArrayList<ConfiguratorPart>();

		List<ConfiguratorPart> configuratorPartList = associateParts(getConfiguratorParts(ct, configuratorPartMap));

		List<ConfiguratorPart> partsFromChrgAgrmList = saasConfiguratorDao.getPartsFromChrgAgrm(ct, configuratorPartMap);
		// get original charge agreement number and configuration id.
		String orignlSalesOrdRefNum = null;
		String orignlConfigrtnId = null;

		if (partsFromChrgAgrmList != null) {
			for (ConfiguratorPart partChrAgrm : partsFromChrgAgrmList) {
				// For Finalization, each original sales order ref num in CA
				// should be same.
				orignlSalesOrdRefNum = partChrAgrm.getOrignlSalesOrdRefNum();
				orignlConfigrtnId = partChrAgrm.getOrignlConfigrtnId();
				break;
			}
		}

		chrgAgrmPartList = associateParts(partsFromChrgAgrmList);
		Map<String, ConfiguratorPart> origPartMap = convertConfiguratorPartListToMap(chrgAgrmPartList);

        newlyAddedSubscrptnPartList = new ArrayList<ConfiguratorPart>();
        removedSubscrptnPartList = new ArrayList<ConfiguratorPart>();

		for (ConfiguratorPart newPart : configuratorPartList) {
			// Only Sub and On Demand parts are in CA, per Finalization rules.
			if (newPart.isSubscrptn()) {
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());

				if (origPart != null) {
					// Per Finalization rules, all parts in CA will be marked
					// replaced
					// and correspond new line items will be added.
					lineItemList.add(newPart);

					origPart.markAsReplaced(newPart);
					lineItemList.add(origPart);

					ConfiguratorPart newOveragePart = newPart.getAssociatedOveragePart();
					if (newOveragePart != null) {
						lineItemList.add(newOveragePart);
					}

					ConfiguratorPart origOveragePart = origPart.getAssociatedOveragePart();
					if (origOveragePart != null) {
						origOveragePart.markAsReplaced(newOveragePart);
						lineItemList.add(origOveragePart);
					}

					if (newPart.getAssociatedDailyPart() != null) {
						lineItemList.add(newPart.getAssociatedDailyPart());// Add
																			// new
																			// daily
																			// parts
																			// as
																			// new
																			// line
																			// items
					}

				} else {
					// No original part with same part#, this is a new part
					lineItemList.add(newPart);

					if (newPart.getAssociatedDailyPart() != null) {
						lineItemList.add(newPart.getAssociatedDailyPart());
					}

					if (newPart.getAssociatedOveragePart() != null) {
						lineItemList.add(newPart.getAssociatedOveragePart());
					}

					logContext.debug(this, "new part added from configurator : " + newPart.toString());
					newlyAddedSubscrptnPartList.add(newPart);
				}

			} else if (newPart.isSetUp()) {
				// Update on 2012-1-12 per PL :
				// Notes://CAMDB10/85256B890058CBA6/5D3D446FEB23FC918525718E006EAFDE/7446F20C4604F57985257982005B3409
				if (ct.isFromCPQConfigurator()) { // from GST
					ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());
					if (origPart != null) { // in CA.
						int origQty = origPart.getPartQty().intValue();
						int newQty = newPart.getPartQty().intValue();
						if (newQty > origQty) {
							int delta = newQty - origQty;
							newPart.setPartQty(delta);
							lineItemList.add(newPart);
						} else {
							// Do nothing here
						}
					} else { // not in CA
						lineItemList.add(newPart);
					}
				} else { // from Basic
					lineItemList.add(newPart);
					if (newPart.getAssociatedSubsumedSubscrptnPart() != null) {
						lineItemList.add(newPart.getAssociatedSubsumedSubscrptnPart());
					}
				}

			} else if (newPart.isAddiSetUp()) {
				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());

				if (origPart != null) {
					// keep the old codes, actually, for Finalization, there are
					// only Sub and On Demand in CA.
					if (setUpPartWithSameSubIdReturned(newPart.getSubId(), configuratorPartList)) {
						lineItemList.add(newPart);
						origPart.markAsReplaced();
						lineItemList.add(origPart);
					} else {
						// Do nothing here
					}
				} else {
					// No original part with same part#, this is a new part
					lineItemList.add(newPart);
				}

			} else if (newPart.isOnDemand()) {

				ConfiguratorPart origPart = origPartMap.remove(newPart.getPartNum());
				if (origPart != null) {
					// Per Finalization rules, all parts in CA will be marked
					// replaced
					// and correspond new line items will be added.
					lineItemList.add(newPart);
					origPart.markAsReplaced(newPart);
					lineItemList.add(origPart);

				} else {
					lineItemList.add(newPart);
				}
			} else if (newPart.isHumanSrvs()) {
				lineItemList.add(newPart);
			}
		}

		Map<String, ConfiguratorPart> configuratorPartMap2 = convertConfiguratorPartListToMap(configuratorPartList);
		for (ConfiguratorPart origPart : origPartMap.values()) {
			if (origPart.isSubscrptn()) {
				ConfiguratorPart newPart = configuratorPartMap2.get(origPart.getPartNum());
				boolean isMissingPart = false;// refer to rtc#215704. If currnet
												// ca part is a missing part,
												// the part will be added into
												// replaced list.
				if (missingPartLst != null && missingPartLst.contains(origPart.getPartNum()))
					isMissingPart = true;
				if (newPart == null && !isMissingPart) {
					origPart.markAsReplaced();
					lineItemList.add(origPart);

					ConfiguratorPart overagePart = origPart.getAssociatedOveragePart();
					if (overagePart != null) {
						overagePart.markAsReplaced();
						lineItemList.add(overagePart);
					}

					logContext.debug(this, "original part removed from configurator: " + origPart.toString());
					removedSubscrptnPartList.add(origPart);
				}
			}
		}

		processForTradeUp(newlyAddedSubscrptnPartList, removedSubscrptnPartList);

		for (ConfiguratorPart part : lineItemList) {
			// FCT TO PA Finalization
			part.setOrignlSalesOrdRefNum(orignlSalesOrdRefNum);
			part.setOrignlConfigrtnId(orignlConfigrtnId);
			// Clear daily/overage parts for replaced subscription parts
			if (part.isPartReplaced() && part.isSubscrptn()) {
                ConfiguratorPartCombine configuratorPartCombine = new ConfiguratorPartCombine();
                configuratorPartCombine.setAssociatedDailyPart(part.getAssociatedDailyPart());
                configuratorPartCombine.setAssociatedOveragePart(part.getAssociatedOveragePart());
                configuratorPartCombineMap.put(part, configuratorPartCombine);

                part.setAssociatedDailyPart(null);
                part.setAssociatedOveragePart(null);

			}
		}

		return lineItemList;

	}

	@Override
	protected String getConfigId(AddOrUpdateConfigurationContract ct) {
		String configrtnId = ct.getConfigId();
		String orgConfigrtnId = ct.getOrgConfigId();
		// if original configrtnId is null or configrtnId is equal to
		// orgConfigrtnId, it means first time to Finalization.
		// So need to create new configuration id.
		if (StringUtils.isBlank(orgConfigrtnId) || configrtnId.equalsIgnoreCase(orgConfigrtnId)) {
			orgConfigrtnId = configrtnId;
			configrtnId = QuoteCommonUtil.getNewSQOConfigurationId(ct.getPid());
		}
		return configrtnId;
	}

	@Override
	protected String getCotermConfigrtnId(String configrtnId, String orgConfigrtnId) {
		String cotermConfigrtnId = null;
		if (shouldCoTerm()) {
			cotermConfigrtnId = orgConfigrtnId;
			if (!shouldCoTermToConfigrtn()) {
				cotermConfigrtnId = null;
			}
		}
		return cotermConfigrtnId;
	}

	@Override
	protected String getOrgConfigrtnId(AddOrUpdateConfigurationContract ct) {
		String configrtnId = ct.getConfigId();
		String orgConfigrtnId = ct.getOrgConfigId();
		// if original configrtnId is null or configrtnId is equal to
		// orgConfigrtnId, it means first time to Finalization.
		// So need to create new configuration id.
		if (StringUtils.isBlank(orgConfigrtnId) || configrtnId.equalsIgnoreCase(orgConfigrtnId)) {
			orgConfigrtnId = configrtnId;
		}
		return orgConfigrtnId;
	}

	protected String getConfigIdForSetDefaultProvisngDays(String configId, String orgConfigrtnId) {
		return orgConfigrtnId;
	}

	@Override
    protected void initialCoterm(ConfiguratorPart part, ConfiguratorPartInfo configuratorPartInfo) {
		// for FCT_TO_PA, need to initial coterm
		if (part.getCotermEndDate() != null) {
            if (configuratorPartInfo != null) {
                configuratorPartInfo.latestEndDate = part.getCotermEndDate();
                configuratorPartInfo.latestEndDatePart = part;
            }
		}
	}

	@Override
	protected boolean skillAllCotermCalculation(Date cotermEndDate) {
		// for FCT_TO_PA, need to do below logic
		if (cotermEndDate != null) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean skillOnePartCotermCalculation(Date cotermEndDate) {
		// for FCT_TO_PA, need to do below logic
		if (cotermEndDate == null) {
			return true;
		}
		return false;
	}

	/**
	 * @param caEndDateFrmContract
	 * @param cotermParameter
	 * @return for FCT_TO_PA implementation, if for default logic, need to check
	 *         super class
	 */
	@Override
	protected Date getCotermEndDate4AddPart(String caEndDateFrmContract, CotermParameter cotermParameter) {
		Date coTermEndDate = DateUtil.parseDate(caEndDateFrmContract);
		return coTermEndDate;
	}

	/**
	 * @param cotermParameter
	 * @return for FCT_TO_PA implementation, if for default logic, need to check
	 *         super class
	 */
	@Override
	protected String getCotermConfigrtnId4AddPart(CotermParameter cotermParameter) {
		return null;
	}


}
