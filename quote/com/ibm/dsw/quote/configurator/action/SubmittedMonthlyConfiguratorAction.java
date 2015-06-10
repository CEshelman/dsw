/**
 * 
 */
package com.ibm.dsw.quote.configurator.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorStateKeys;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwRampUpSubscriptionConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.MonCommConfiguratorValiProcessFactory;
import com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcess;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: SubmittedMonthlyConfiguratorAction
 * @author Frank
 * @Description: TODO
 * @date Dec 19, 2013 11:46:11 AM
 * 
 */
public class SubmittedMonthlyConfiguratorAction extends MonthlySwBaseConfiguratorAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.dsw.quote.configurator.action.MonthlySwBaseConfiguratorAction
	 * #getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 */
	@Override
	protected String getState(ProcessContract contract) {
		return ConfiguratorStateKeys.STATE_SUBMITTED_MONTHLY_SW_CONFIG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com
	 * .ibm.ead4j.jade.contract.ProcessContract,
	 * com.ibm.ead4j.jade.bean.ResultHandler)
	 */
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {

		SubmittedMonthlySwConfiguratorContract submittedMonthlySwContrat = (SubmittedMonthlySwConfiguratorContract) contract;

		MonthlyConfiguratorProcess submittedMonthlyProcess = getMonthlySwProcess(submittedMonthlySwContrat
				.getConfigrtnActionCode());

		if (!submittedMonthlySwContrat.isCancellConfigrtn()) {
			try {
				submittedMonthlyProcess.addMonthlySwToQuote(submittedMonthlySwContrat);
			} catch (TopazException e) {
				logger.error(this, e.getMessage());
				throw new QuoteException(e);
			}
			handler.addObject(ConfiguratorParamKeys.CANCEL_CONFIGRTN, Boolean.FALSE);
		} else {
			handler.addObject(ConfiguratorParamKeys.CANCEL_CONFIGRTN, Boolean.TRUE);
		}

		String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);
		redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), DraftQuoteParamKeys.PART_LIMIT_EXCEED_CODE,
				String.valueOf(submittedMonthlySwContrat.getExceedCode())).toString();

		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(ConfiguratorStateKeys.STATE_REDIRECT_TO_REDIRECT_COMPONENT);
        String redirectAction = submittedMonthlySwContrat.getRedirectAction();
        if (redirectAction != null) {
            ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
            String urlPattern = appContext.getConfigParameter(ApplicationProperties.APPLICATION_URL_PATTERN);
            redirectAction = urlPattern + "?" + appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY) + "="
                    + redirectAction;
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectAction);
        }

		return handler.getResultBean();
	}

	protected boolean validate(ProcessContract contract) {
		SubmittedMonthlySwConfiguratorContract ct = (SubmittedMonthlySwConfiguratorContract) contract;
		CommonConfiguratorValidation commConfigValidation = null;
		try {
			commConfigValidation = MonCommConfiguratorValiProcessFactory.createConfigurator(ct.getConfigrtnActionCode());
			if (ct.isCancellConfigrtn()) {
				return true;
			}

			List<MonthlySwConfiguratorPart> list = ct.getConfiguratorPartList();
			boolean validateBlgFrqncy = true;
			if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ct.getConfigrtnActionCode())||
					PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(ct.getConfigrtnActionCode())||
					PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ct.getConfigrtnActionCode()))
				{
					validateBlgFrqncy = false;
				}
			
			if (list != null) {
				for (MonthlySwConfiguratorPart part : list) {
					if (!(part instanceof MonthlySwRampUpSubscriptionConfiguratorPart)) {

						if (validateBlgFrqncy && !commConfigValidation.validateBlgOption(part, contract, this)) {
							return false;
						}
						if (part.isMustHaveQty() && !commConfigValidation.validateQuantity(part, contract, this)) {
							return false;
						}
						if (!StringUtils.contains(ct.getPartListStrForSkipTermValidation(), part.getPartNum())) {
							if (!commConfigValidation.validateTerm(part, contract, this)) {
								return false;
							}
						}	
					}

				}
			}
		} catch (QuoteException e) {
			logger.error(this, e.getMessage());
		}

		return true;
	}

}
