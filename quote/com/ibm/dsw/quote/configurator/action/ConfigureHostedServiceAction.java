package com.ibm.dsw.quote.configurator.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.util.DebugUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorStateKeys;
import com.ibm.dsw.quote.configurator.contract.ConfigureHostedServiceContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ConfigureHostedServiceAction extends BaseContractActionHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4374196974593335699L;
	private static final LogContext logger = LogContextFactory.singleton()
			.getLogContext();

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {

		// validate params -- fix appscan issue
		// referred to
		// com.ibm.dsw.quote.customer.action.CustomerCreateAction.validateParams(ProcessContract)
		validateParams(contract);

		ConfigureHostedServiceContract chsContract = (ConfigureHostedServiceContract) contract;
		
		ConfiguratorPartProcess process = ConfiguratorPartProcessFactory.singleton().create();
		ConfiguratorHeader header = new ConfiguratorHeader();
		
		if (StringUtils.isBlank(chsContract.getOperationType())) {	//Not GO button action.
			try {
				process.buildConfiguratorHeader(chsContract, header);
			} catch (TopazException e) {
				logger.error(this, e.getMessage());
				throw new QuoteException(e);
			}
		}else if(chsContract.getOperationType().equalsIgnoreCase(ConfiguratorConstants.OPERATION_TYPE_GO)){//GO button action = change ramp up periods action.
			try {
				logger.debug(this, "begin to change ramp up periods....");
				process.changeRampUpPeriods(chsContract, header);
			} catch (TopazException e) {
				logger.error(this, e.getMessage());
				throw new QuoteException(e);
			}
		}else if(chsContract.getOperationType().equalsIgnoreCase(ConfiguratorConstants.OPERATION_TYPE_REMOVE_COTERM)){//Remove co-term button action.
			try {
				logger.debug(this, "begin to remove co-term....");
				chsContract.setCTFlag(ConfiguratorConstants.CTFLAG_FALSE);
				chsContract.setConfigrtnActionCode(PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT);
				DebugUtil.showString(chsContract.getConfigrtnActionCode(), "chsContract.getConfigrtnActionCode()");
				process.buildConfiguratorHeader(chsContract, header);
			} catch (TopazException e) {
				logger.error(this, e.getMessage());
				throw new QuoteException(e);
			}
		}
		handler.addObject(ConfiguratorParamKeys.CONFIGURATOR_HEADER, header);

		handler.setState(getState(contract));
		return handler.getResultBean();
	}


	protected String getState(ProcessContract contract) {
		return ConfiguratorStateKeys.STATE_CONFIG_HOSTED_SERVICE;
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
		ConfigureHostedServiceContract ct = (ConfigureHostedServiceContract) contract;

		// OfferingCode, overrideFlag, AddOnTradeUpFlag, configrtnActionCode,
		// configId, webQuoteNum,
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
