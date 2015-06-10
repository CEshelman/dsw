/**
 * 
 */
package com.ibm.dsw.quote.configurator.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorStateKeys;
import com.ibm.dsw.quote.configurator.contract.BuildMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm;
import com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcess;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * @ClassName: MonthlyConfiguratorNewCaAction
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 2:38:01 PM
 *
 */
public class BuildMonthlyConfiguratorAction extends MonthlySwBaseConfiguratorAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
	 */
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		BuildMonthlySwConfiguratorContract buildMonthlyContract = (BuildMonthlySwConfiguratorContract)contract;
		
		MonthlyConfiguratorProcess buildMonthlyProcess = getMonthlySwProcess(buildMonthlyContract.getConfigrtnActionCode());
		
		MonthlySwConfiguratorForm configuratorForm = null;
		
		try {
			 configuratorForm = buildMonthlyProcess.bulidMonthlyConfigurator(buildMonthlyContract);
		} catch (TopazException e) {
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		}
		
		handler.addObject(ParamKeys.PARAM_CONFIGURATOR_FORM, configuratorForm);
		
		handler.addObject(ConfiguratorParamKeys.existedRestriectedPartList,buildMonthlyContract.getAlreadyExistedPartListStr());
		handler.addObject(ConfiguratorParamKeys.notFoundRestrictedPartList, buildMonthlyContract.getNotFoundPartListStr());
		handler.addObject(ConfiguratorParamKeys.neededProcessRestrictedPartList, buildMonthlyContract.getProcessedPartListStr());
		
		handler.setState(getState(contract));
		
		return handler.getResultBean();
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.action.MonthlySwBaseConfiguratorAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 * get different state (mapping view bean) by addon/trade and new ca
	 */
	@Override
	protected String getState(ProcessContract contract) {
		BuildMonthlySwConfiguratorContract buildMonthlyContract = (BuildMonthlySwConfiguratorContract)contract;
		
		String stateKey = ConfiguratorStateKeys.STATE_BUILD_MONTHLY_SW_NEWCA_CONFIG ;
		
		if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(buildMonthlyContract.getConfigrtnActionCode())){
			stateKey = ConfiguratorStateKeys.STATE_BUILD_MONTHLY_SW_ADDON_CONFIG;
		}
		
		return stateKey;
	}



}
