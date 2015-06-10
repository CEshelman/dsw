package com.ibm.dsw.quote.configurator.process.jdbc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.helper.AddOnTradeUpReasonCode;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpConfiguration;
import com.ibm.ead4j.topaz.exception.TopazException;

@SuppressWarnings("rawtypes")
public abstract class AbstractSaasConfiguratorUpdateCaProcess extends AbstractSaasConfiguratorProcess {

    List<ConfiguratorPart> newlyAddedSubscrptnPartList;

    List<ConfiguratorPart> removedSubscrptnPartList;

    protected abstract List filterConfiguratorParts(AddOrUpdateConfigurationContract ct, Map configuratorPartMap,
            Map<ConfiguratorPart, ConfiguratorPartCombine> configuratorPartCombineMap) throws TopazException, QuoteException;

	@Override
	protected boolean shouldCoTerm() {
		return true;
	}

	@Override
	protected boolean shouldCoTermToConfigrtn() {
		return true;
	}

	/**
	 * @param chrgAgrmPartList
	 * @param configuratorPartList
	 * @return for add-on/trade-up or FCT_TO_PA implementation, return 0, for
	 *         default implementation, need to check super class
	 */
	@Override
	public int getExtEntireConfigFlagAccordingToParts(List<ConfiguratorPart> chrgAgrmPartList,
			List<ConfiguratorPart> configuratorPartList, String termExtensionFlagFrmContract) {


		int extEntireConfigFlag = "1".equals(termExtensionFlagFrmContract) ? 1 : 0;

		if (extEntireConfigFlag == 1) {
			Map<String, ConfiguratorPart> origPartMap = convertConfiguratorPartListToMap(chrgAgrmPartList);
			if (origPartMap == null || origPartMap.size() < 1)
				return extEntireConfigFlag;

			if (origPartMap != null && origPartMap.size() > 0
					&& (configuratorPartList == null || configuratorPartList.size() < 1)) {
				return extEntireConfigFlag = 0;
			}

			for (ConfiguratorPart lineItem : configuratorPartList) {
				origPartMap.remove(lineItem.getPartNum());
			}

			if (origPartMap.size() > 0) {
				extEntireConfigFlag = 0;
			}
		}

		return extEntireConfigFlag;
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.configurator.process.jdbc.AbstractSaasConfiguratorProcess#setReasonCodes(com.ibm.dsw.quote.
     * common.domain.QuoteLineItem, java.lang.String, java.lang.String,
     * com.ibm.dsw.quote.configurator.domain.ConfiguratorPart, java.util.List,
     * com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract)
     */
    @Override
    protected void setReasonCodes(QuoteLineItem qli, String configrtnId, String webQuoteNum, ConfiguratorPart part,
            AddOrUpdateConfigurationContract ct, CotermParameter cotermParameter, List quoteExistingSaasPartList,
            List<ConfiguratorPart> configuratorPartList) throws TopazException, QuoteException {
        // if Adding a new subscription to an existing service offering, with co-terming to an existing service
        // configuration, Reason For New Line: AP- Add Part

        if (AddOnTradeUpConfiguration.CONFIGCODE_U.equals(ct.getUpdateCAConfigCode())) {
            qli.setNewConfigFlag("N");
        } else if (AddOnTradeUpConfiguration.CONFIGCODE_A.equals(ct.getUpdateCAConfigCode())) {
            qli.setNewConfigFlag("Y");
        }

        // if is overage part, do not set at here, set at 'adjust reason codes for overage parts'
        if (part.isOvrage()) {
            return;
        }

        boolean isNewPart = isNewPart(part);
        boolean isRemovedPart = isRemovedPart(part);

        // do not set for daily part?
        if (isNewPart || (part.isOnDemand() && !qli.isReplacedPart())) {
            // AS_ADD_REASON_CODE: 1. Adding a new service offering configuration
            // if it's adding a new service, will not come to this process, but goto
            // AbstractSaasConfiguratorNewCaProcess

            // AP_ADD_REASON_CODE
            // isAddingNewService(qli, configrtnId, webQuoteNum, part, ct, cotermParameter, quoteExistingSaasPartList)
            // returns false at here
            boolean isCoterm = isCoterming(cotermParameter);

            if (isCoterm) {
                qli.setAddReasonCode(AddOnTradeUpReasonCode.AP_ADD_REASON_CODE);
            } else {
                // DO NOTHING
            }

        }
        // is not new, is not replaced
        else if (!isRemovedPart & !isNewPart) {
            // AQ_ADD_REASON_CODE & AQ_REPLACED_REASON_CODE
            // isNewPart returns false
            boolean isCoterm = isCoterming(cotermParameter);
            if (isCoterm) {
                if (isAddQuantity(qli, part, ct, configuratorPartList)) {
                    qli.setAddReasonCode(AddOnTradeUpReasonCode.AQ_ADD_REASON_CODE);
                    if (qli.isReplacedPart()) {
                        qli.setReplacedReasonCode(AddOnTradeUpReasonCode.AQ_REPLACED_REASON_CODE);
                    }
                } else {
                    qli.setAddReasonCode(AddOnTradeUpReasonCode.DQ_ADD_REASON_CODE);
                    if (qli.isReplacedPart()) {
                        qli.setReplacedReasonCode(AddOnTradeUpReasonCode.DQ_REPLACED_REASON_CODE);
                    }
                }
            }
            //
            if (qli.isSaasSetUpPart() || part.isSetUp()) {
                qli.setAddReasonCode(AddOnTradeUpReasonCode.AP_ADD_REASON_CODE);
            }
        }
        // TR_REPLACED_REASON_CODE & TA_ADD_REASON_CODE
        else if (isRemovedPart) {
            // should really set addReasonCode to 'TA - Trade Up Add'?
            qli.setAddReasonCode(AddOnTradeUpReasonCode.TA_ADD_REASON_CODE);
            if (qli.isReplacedPart()) {
                qli.setReplacedReasonCode(AddOnTradeUpReasonCode.TR_REPLACED_REASON_CODE);
            }
        }

        // if qli is replaced, set ReplacedReasonCode, do NOT set AddReasonCode.
        if (qli.isReplacedPart()) {
            qli.setAddReasonCode("");
        }

    }

    /**
     * DOC Since daily parts have no quanties, so need to set addReasonCode, replacedReasonCode for daily parts in
     * 'AbstractSaasConfiguratorProcess.createQuoteLineItem(String, AddOrUpdateConfigurationContract,
     * List<ConfiguratorPart>, CotermParameter, List)'
     * 
     * @param qli
     * @param part
     * @param ct
     * @param configuratorPartList
     * @return
     */
    private boolean isAddQuantity(QuoteLineItem qli, ConfiguratorPart part, AddOrUpdateConfigurationContract ct,
            List<ConfiguratorPart> configuratorPartList) {
        ConfiguratorPart corrspandingPart = getCorrspandingPart(qli, part, configuratorPartList);
        if (corrspandingPart == null) {
            return true;
        }
        // testing
        if (qli.isReplacedPart()) {
            if (part.getPartQty() != null && corrspandingPart.getPartQty() != null
                    && part.getPartQty() > corrspandingPart.getPartQty()) {
                return false;
            } else {
                return true;
            }
        } else {
            if (part.getPartQty() != null && corrspandingPart.getPartQty() != null
                    && part.getPartQty() > corrspandingPart.getPartQty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * DOC Comment method "getCorrspandingPart".
     * 
     * @param qli
     * @param part
     * @param configuratorPartList
     * @return
     */
    private ConfiguratorPart getCorrspandingPart(QuoteLineItem qli, ConfiguratorPart part,
            List<ConfiguratorPart> configuratorPartList) {
        for (Iterator iterator = configuratorPartList.iterator(); iterator.hasNext();) {
            ConfiguratorPart configuratorPart = (ConfiguratorPart) iterator.next();
            if (!part.equals(configuratorPart) && part.getPartNum().equals(configuratorPart.getPartNum())) {
                return configuratorPart;
            }
        }
        return null;
    }

    protected boolean isCoterming(CotermParameter cotermParameter) {
        return true;
    }

    /**
     * DOC Comment method "isNewPart".
     * 
     * @param part
     * @return
     */
    protected boolean isNewPart(ConfiguratorPart part) {
        // TODO by jack, check if the chrgAgrmPartList could contains SetUp part.
        if (!part.isSubscrptn() && !part.isSetUp()) {
            return false;
        }
        if (chrgAgrmPartList != null) {
            for (Iterator<ConfiguratorPart> iterator = chrgAgrmPartList.iterator(); iterator.hasNext();) {
                ConfiguratorPart configuratorPart = iterator.next();
                if (configuratorPart.getPartNum().equals(part.getPartNum())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * DOC Comment method "isNewPart".
     * 
     * @param part
     * @return
     */
    protected boolean isRemovedPart(ConfiguratorPart part) {
        if (removedSubscrptnPartList != null && removedSubscrptnPartList.contains(part)) {
            return true;
        }
        return false;
    }

}
