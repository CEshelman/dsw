package com.ibm.dsw.quote.configurator.action;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.common.validator.ValidatorMessageKeys;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorStateKeys;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcess;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.validation.ValidateConfiguratorPart;
import com.ibm.dsw.quote.validation.ValidateConfiguratorPartFactory;
import com.ibm.dsw.quote.validation.config.ValidateAddonTradeUpConstants;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class AddOrUpdateConfigurationAction extends BaseContractActionHandler {
	private static final LogContext logger = LogContextFactory.singleton()
			.getLogContext();

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {

        // validate params -- fix appscan issue
        // referred to com.ibm.dsw.quote.customer.action.CustomerCreateAction.validateParams(ProcessContract)
        validateParams(contract);

		AddOrUpdateConfigurationContract ct = (AddOrUpdateConfigurationContract) contract;
		
		if(ct.getConfiguratorType() != null && (!ct.getConfiguratorType().equals(CustomerParamKeys.CPQ) && !ct.getConfiguratorType().equals(CustomerParamKeys.SQO) && !ct.getConfiguratorType().equals(CustomerParamKeys.PGS))){
        	logContext.error(this, "invalid configuratorType value:" +ct.getConfiguratorType());
        	throw new QuoteException("get configuratorType failed.configuratorType:"+ct.getConfiguratorType());
        }

		if (!ct.isCancellConfigrtn()) {
			try {
				SaasConfiguratorProcess process = SaasConfiguratorProcessFactory.singleton().create(ct.getConfigrtnActionCode());
				process.addSaasPartsToQuote(ct);
			} catch (TopazException te) {
				throw new QuoteException(te);
			}

			handler.addObject(ConfiguratorParamKeys.CANCEL_CONFIGRTN,
					Boolean.FALSE);
		} else {
			handler.addObject(ConfiguratorParamKeys.CANCEL_CONFIGRTN,
					Boolean.TRUE);
		}

		String redirectURL = HtmlUtil
				.getURLForAction(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);
		redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL),
				DraftQuoteParamKeys.PART_LIMIT_EXCEED_CODE,
				String.valueOf(ct.getExceedCode())).toString();
		if (ct.isFromCPQConfigurator()) {
			// CPQ configurator
			handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
			handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		} else {
			// SQO configurator
			handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
			handler.setState(ConfiguratorStateKeys.STATE_REDIRECT_TO_REDIRECT_COMPONENT);
		}

		return handler.getResultBean();
	}

    protected boolean validate(ProcessContract contract) {
		AddOrUpdateConfigurationContract ct = (AddOrUpdateConfigurationContract) contract;
		ValidateConfiguratorPart validateBasicConfiguratorPart = ValidateConfiguratorPartFactory
				.singleten()
				.create(ValidateAddonTradeUpConstants.VALIDATE_BASIC_ADDON_TRADEUP);
		validateBasicConfiguratorPart.validate(ct);
		Map validateBasicConfiguratorMap = ct.getValidationDataMap();
		if (validateBasicConfiguratorMap
				.containsKey(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY)) {
			return false;
		}
		return true;
	}

    /**
     * DOC Comment method "validateParams".
     * 
     * @param contract
     * @throws QuoteException
     */
    private void validateParams(ProcessContract contract) throws QuoteException {
        // You can add the validate param here or you can add it in the child
        // class.
        AddOrUpdateConfigurationContract ct = (AddOrUpdateConfigurationContract) contract;

        // OfferingCode, overrideFlag, AddOnTradeUpFlag, configrtnActionCode, configId, webQuoteNum,
        // configuratorType

        if (!SecurityUtil.isValidInput("pid", ct.getPid(), "AlphaNumeric", 35, true)) {
            throw new QuoteException("Invalid pid value");
        }
        if (!SecurityUtil.isValidInput("overrideFlag", ct.getOverrideFlag(), "Numeric", 5, true)) {
            throw new QuoteException("Invalid overrideFlag value");
        }
        if (!SecurityUtil.isValidInput("addOnTradeUpFlag", ct.getAddOnTradeUpFlag(), "Numeric", 5, true)) {
            throw new QuoteException("Invalid addOnTradeUpFlag value");
        }
        if (!SecurityUtil.isValidInput("configrtnActionCode", ct.getConfigrtnActionCode(), "Alpha", 35, true)) {
            throw new QuoteException("Invalid configrtnActionCode value");
        }
        if (!SecurityUtil.isValidInput("configId", ct.getConfigId(), "AlphaNumeric", 35, true)) {
            throw new QuoteException("Invalid configId value");
        }

        if (StringUtils.isNotBlank(ct.getWebQuoteNum())) {
            if (!SecurityUtil.isValidInput("webQuoteNum", ct.getWebQuoteNum(), "Numeric", 10, true)) {
                throw new QuoteException("Invalid webQuoteNum value.");
            }
        }
        if (!SecurityUtil.isValidInput("configuratorType", ct.getConfiguratorType(), "Alpha", 10, true)) {
            throw new QuoteException("Invalid configuratorType value");
        }

    }

}
