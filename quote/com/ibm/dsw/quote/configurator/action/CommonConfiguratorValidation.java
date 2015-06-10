/**
 * 
 */
package com.ibm.dsw.quote.configurator.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorViewKeys;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwRampUpSubscriptionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwSubscrptnConfiguratorPart;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @ClassName: CommonConfiguratorValidation
 * @author Linda
 * @Description: TODO
 * @date Jan 15, 2014
 * 
 */
public abstract class CommonConfiguratorValidation {
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

	public boolean validateTerm(MonthlySwConfiguratorPart part, ProcessContract contract,
			BaseContractActionHandler baseContractActionHandler) {

		// validate term for part: term should be number
		if (part.getPartQty() != null && part.getPartQty().intValue() != 0) {
			try {
				int term = Integer.parseInt(part.getTermStr());
				if (term < 1 || term > 999) {
					addTermValidationErrorMsg(part, contract,
							baseContractActionHandler);
					return false;
				}
			} catch (Exception e) {
				addTermValidationErrorMsg(part, contract, baseContractActionHandler);
				return false;
			}
		}
		if (part instanceof MonthlySwSubscrptnConfiguratorPart
				&& ((MonthlySwSubscrptnConfiguratorPart) part).getRampUpParts() != null
				&& ((MonthlySwSubscrptnConfiguratorPart) part).getRampUpParts().size() > 0) {
			String firstRampUpKey = "";
			for (int i = 0; i < ((MonthlySwSubscrptnConfiguratorPart) part).getRampUpParts().size(); i++) {
				MonthlySwConfiguratorPart rampPart = (MonthlySwConfiguratorPart) ((MonthlySwSubscrptnConfiguratorPart) part)
						.getRampUpParts().get(i);

				String key = ConfiguratorParamKeys.paramIDPreFix + rampPart.getPartNum() + "_" + rampPart.getSeqNum()
						+ ConfiguratorParamKeys.rampUpDurationSuffix;
				if (i == 0) {
					firstRampUpKey = key;
				}
				int duration = 0;
				try {
					duration = Integer.parseInt(rampPart.getSubmitConfiguratorPart().getRampUpDurationStr());
				} catch (Exception e) {
					addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_INTEGER,
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_HDR, key, contract, baseContractActionHandler);

					return false;
				}
				if (duration <= 0) {
					addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR,
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_LARGER_THAN_ZERO,
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_HDR, key, contract, baseContractActionHandler);

					return false;
				}

			}
			int total = 0;
			for (MonthlySwConfiguratorPart rampPart : ((MonthlySwSubscrptnConfiguratorPart) part).getRampUpParts()) {
				total += rampPart.getSubmitConfiguratorPart().getRampUpDuration();
			}

			if (part.getTerm() < total) {
				addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.RAMPUP_PERIODS_LESS_THEN_ONE,
						ConfiguratorViewKeys.TERM, firstRampUpKey, contract, baseContractActionHandler);
				return false;
			}
		}

		return true;
	}

	private void addTermValidationErrorMsg(MonthlySwConfiguratorPart part, ProcessContract contract, BaseContractActionHandler baseContractActionHandler) {
		String key = ConfiguratorParamKeys.paramIDPreFix + part.getKey() + ConfiguratorParamKeys.termSuffix;
		addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.TERM_MSG_WHOLE_NUMBER, ConfiguratorViewKeys.MONTHLY_TERM, key,
				contract, baseContractActionHandler);
	}

	public boolean validateBlgOption(MonthlySwConfiguratorPart part, ProcessContract contract,
			BaseContractActionHandler baseContractActionHandler) {
		if (part.getTerm() == null || StringUtils.isBlank(part.getBillingFrequencyCode())) {
			return true;
		}
		String boCode = part.getBillingFrequencyCode();
		int term = part.getTerm();
		if (StringUtils.isNotBlank(boCode)) {
			String key = part.getKey() + ConfiguratorParamKeys.billingFrequencySuffix;

			key = ConfiguratorParamKeys.paramIDPreFix + key;

			if ("N/A".equals(boCode)) {
				addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.NO_BILLING_OPTIONS,
						ConfiguratorViewKeys.BILLING_FREQUENCY, key, contract, baseContractActionHandler);

				return false;
			}

			// No need to validate contract term when billing option is UPFRONT
			// or EVENT
			if (QuoteConstants.SaaSBillFreq.UPFRONT.equals(boCode) || QuoteConstants.SaaSBillFreq.EVENT.equals(boCode)) {
				return true;
			}
			try {
				int months = QuoteCommonUtil.getBillingOptioMonths(boCode);
				if ((term % months) != 0) {
					addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.BILLING_OPTION_NOT_VALID,
							ConfiguratorViewKeys.BILLING_FREQUENCY, key, contract, baseContractActionHandler);
					return false;
				}
			} catch (QuoteException e) {
				logger.error(this, e);
				return false;
			}
		}

		return true;
	}

	public boolean validateQuantity(MonthlySwConfiguratorPart part, ProcessContract contract,
			BaseContractActionHandler baseContractActionHandler) {
		if (part.getPartQtyStr() == null) {
			return true;
		}

		// validate for non-rampup part
		if (!(part instanceof MonthlySwRampUpSubscriptionConfiguratorPart)
				&& !validateQuantity(part, false, 0, contract, baseContractActionHandler)) {
			return false;
		}

		if (part instanceof MonthlySwSubscrptnConfiguratorPart
				&& ((MonthlySwSubscrptnConfiguratorPart) part).getRampUpParts() != null) {
			int mainQty = part.getPartQty() == null ? 0 : part.getPartQty().intValue();
			for (MonthlySwConfiguratorPart rampUpPart : ((MonthlySwSubscrptnConfiguratorPart) part).getRampUpParts()) {
				if (!validateQuantity(rampUpPart, true, 0, contract, baseContractActionHandler)) {
					return false;
				}

				// quantity is null, means going to be deleted, continue to next
				// part validation
				if (rampUpPart.getPartQty() == null) {
					continue;
				}

				if (rampUpPart.getPartQty() > mainQty) {
					String key = ConfiguratorParamKeys.paramIDPreFix + rampUpPart.getPartNum() + "_" + rampUpPart.getSeqNum()
							+ ConfiguratorParamKeys.rampUpQtySuffix;

					addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.RAMP_UP_QTY_LESS_THAN_MAIN,
							ConfiguratorViewKeys.QUANTITY_MSG_HDR, key, contract, baseContractActionHandler);

					return false;
				}
			}
		}

		return true;
	}

	public boolean validateQuantity(MonthlySwConfiguratorPart part, boolean isRampUp, int rampSeqNum, ProcessContract contract,
			BaseContractActionHandler baseContractActionHandler) {
		if (part.isMustHaveQty()) {
			// for part with quantity, quantity must be integer
			String key = part.getKey() + ConfiguratorParamKeys.qtySuffix;
			if (isRampUp) {
				key = part.getPartNum() + "_" + part.getSeqNum() + ConfiguratorParamKeys.rampUpQtySuffix;
			}

			key = ConfiguratorParamKeys.paramIDPreFix + key;

			int partQty = 0;

			try {
				partQty = Integer.parseInt(StringUtils.isBlank(part.getPartQtyStr()) ? "0" : part.getPartQtyStr());
			} catch (Exception e) {
				addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.QUANTITY_MSG_WHOLE_NUMBER,
						ConfiguratorViewKeys.QUANTITY_MSG_HDR, key, contract, baseContractActionHandler);

				return false;
			}

			if (partQty < 0 || partQty > 2147483647) {

				addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.QUANTITY_MSG_WHOLE_NUMBER,
						ConfiguratorViewKeys.QUANTITY_MSG_HDR, key, contract, baseContractActionHandler);

				return false;
			}

			// validate part quantity is multiple of tier quantity
			if (part.getTierQtyMeasre() != null) {
				int qty = part.getPartQty().intValue();

				if (!isRampUp && (qty % part.getTierQtyMeasre() != 0)) {
					addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.QUANTITY_MSG_MULTIPLE,
							new String[] { ConfiguratorViewKeys.QUANTITY_MSG_HDR, part.getTierQtyMeasre().toString() },
							new boolean[] { true, false }, key, contract, baseContractActionHandler);

					return false;
				}

				if (isRampUp && ((qty % part.getTierQtyMeasre() != 0) && (qty != 1))) {
					addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR, ConfiguratorViewKeys.QUANTITY_MSG_MULTIPLE_OR_ONE,
							new String[] { ConfiguratorViewKeys.QUANTITY_MSG_HDR, part.getTierQtyMeasre().toString() },
							new boolean[] { true, false }, key, contract, baseContractActionHandler);

					return false;
				}
			}
		}

		return true;
	}

	protected void addErrorMsg(String boundle, String msg, String arg, String key, ProcessContract contract,
			BaseContractActionHandler baseContractActionHandler) {
		HashMap map = new HashMap();
		FieldResult fieldResult = new FieldResult();

		fieldResult.setMsg(boundle, msg);
		fieldResult.addArg(boundle, arg);

		map.put(key, fieldResult);

		baseContractActionHandler.addToValidationDataMap(contract, map);
	}

	private void addErrorMsg(String boundle, String msg, String[] args, boolean[] isArgsResource, String key,
			ProcessContract contract, BaseContractActionHandler baseContractActionHandler) {
		HashMap map = new HashMap();
		FieldResult fieldResult = new FieldResult();

		fieldResult.setMsg(boundle, msg);

		for (int i = 0; i < args.length; i++) {
			fieldResult.addArg(boundle, args[i], isArgsResource[i]);
		}

		map.put(key, fieldResult);

		baseContractActionHandler.addToValidationDataMap(contract, map);
	}

}
