package com.ibm.dsw.quote.configurator.process.jdbc;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.helper.AddOnTradeUpReasonCode;
import com.ibm.ead4j.topaz.exception.TopazException;

public abstract class AbstractSaasConfiguratorNewCaProcess extends AbstractSaasConfiguratorProcess {



    protected List filterConfiguratorParts(AddOrUpdateConfigurationContract ct, Map configuratorPartMap,
            Map<ConfiguratorPart, ConfiguratorPartCombine> configuratorPartCombineMap) throws TopazException, QuoteException {
        return getConfiguratorParts(ct, configuratorPartMap);
    }

	protected abstract boolean shouldCoTerm();

	@Override
	protected boolean shouldCoTermToConfigrtn() {
		return false;
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.configurator.process.jdbc.AbstractSaasConfiguratorProcess#setReasonCodes(com.ibm.dsw.quote.
     * common.domain.QuoteLineItem, java.lang.String, java.lang.String,
     * com.ibm.dsw.quote.configurator.domain.ConfiguratorPart,
     * com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract,
     * com.ibm.dsw.quote.configurator.domain.CotermParameter, java.util.List)
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected void setReasonCodes(QuoteLineItem qli, String configrtnId, String webQuoteNum, ConfiguratorPart part,
            AddOrUpdateConfigurationContract ct, CotermParameter cotermParameter, List quoteExistingSaasPartList,
            List<ConfiguratorPart> configuratorPartList) throws TopazException, QuoteException {

        // if chrgAgrmtNum is not NULL, means it's based on a existing CA
        String chrgAgrmtNum = ct.getChrgAgrmtNum();
        if (chrgAgrmtNum != null && !"".equals(chrgAgrmtNum)) {
            if (qli.isSaasSetUpPart() || part.isSetUp()) {
                qli.setAddReasonCode(AddOnTradeUpReasonCode.AP_ADD_REASON_CODE);
            } else {
                qli.setAddReasonCode(AddOnTradeUpReasonCode.AS_ADD_REASON_CODE);
            }
        }
        super.setReasonCodes(qli, configrtnId, webQuoteNum, part, ct, cotermParameter, quoteExistingSaasPartList,
                configuratorPartList);
    }

}
