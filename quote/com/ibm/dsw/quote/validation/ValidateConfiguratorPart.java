package com.ibm.dsw.quote.validation;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorViewKeys;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.scw.addon.ScwAddonTradeUpErrorCode;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public abstract class ValidateConfiguratorPart {

	private static final LogContext logger = LogContextFactory.singleton()
			.getLogContext();

	protected List<ScwAddonTradeUpErrorCode> scwAddonTradeUpErrorCodeList = null;

	public void validate(AddOrUpdateConfigurationContract ct) {

		// No need to do validation for CPQ configurator
		if (ct.isFromCPQConfigurator()) {
			return;
		}

		if (ct.isCancellConfigrtn()) {
			return;
		}
		// Term validation logic Do not apply for SCW,AddOnTradeUp and CoTerm
		boolean validateTerm = true;
		if (ct.isAddOnTradeUp() || ct.isCoTerm()
				|| "scw".equalsIgnoreCase(ct.getUserId())) {
			validateTerm = false;
		}
		if (validateTerm)
			validateTerm(ct);

		List<ConfiguratorPart> list = null;

		try {
			list = ct.getParts();
		} catch (QuoteException e) {
			logger.error(this,
					"exception when trying to get saas parts from contract");
			logger.error(this, e);

			return;
		}

		boolean validateBlgFrqncy = true;
		if (ct.isAddOnTradeUp() || ct.isCoTerm()) {
			validateBlgFrqncy = false;
		}
		if (list != null) {
			int term = 0;
			if (ct.getTerm() != null) {
				term = ct.getTerm().intValue();
			}
			for (ConfiguratorPart part : list) {
				if (part.isDeleted()) {
					continue;
				}
				if (part.isMustHaveQty()) {
					validateQuantity(part, ct);
				}

				if (validateBlgFrqncy) {
					validateBlgOption(term, part, ct);
				}

				validateRampUpTerm(part, ct);
			}
		}


	}

	private void validateRampUpTerm(ConfiguratorPart part,
			AddOrUpdateConfigurationContract contract) {
		List<ConfiguratorPart> ramUpLineList = part.getRampUpLineItems();
		String key = ConfiguratorParamKeys.paramIDPreFix + part.getPartNum()
				+ "_" + 0 + ConfiguratorParamKeys.rampUpDurationSuffix;
		// 1. duration can not be small than 0 or must be a number
		if (part.getRampUpLineItems() != null
				&& part.getRampUpLineItems().size() > 0) {
		for (ConfiguratorPart rampPart : ramUpLineList) {
				int duration = 0;
				try {
					duration = Integer
							.parseInt(rampPart.getRampUpDurationStr());
				} catch (Exception e) {
					processErrorMsg(
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_INTEGER,
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_HDR, key,
							contract);
					continue;
				}
				if (duration <= 0) {
					processErrorMsg(
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_LARGER_THAN_ZERO,
							ConfiguratorViewKeys.RAMP_UP_DURATION_MSG_HDR, key,
							contract);

				}
			}
			// 2. compute ramp up total
			int total = 0;
			for (ConfiguratorPart rampPart : part.getRampUpLineItems()) {
				if (rampPart.getRampUpDuration() != null)
				total += rampPart.getRampUpDuration();
			}
			// // 3. ram up total must small than 59
			// if (total > PartPriceConstants.TOTAL_RAMP_UP_DURATION_MONTH) {
			// processErrorMsg(ConfiguratorViewKeys.RAMPUP_PERIODS_EXCEED,
			// ConfiguratorViewKeys.TERM, key, contract);
			// }
			// 4.ramp up's duration total must be small than configuration term
			if (part.getTerm() != null && part.getTerm() < 1) {
				processErrorMsg(
						ConfiguratorViewKeys.RAMPUP_PERIODS_LESS_THEN_ONE,
					ConfiguratorViewKeys.TERM, key, contract);
			}
		}
	}

	public boolean isValidateFrequencyCode(){
		return false;
	}

	private void validateBlgOption(int term, ConfiguratorPart part,
			AddOrUpdateConfigurationContract contract) {
		if (part.isSubscrptn() || part.isSetUp() || (part.isHumanSrvs()&&isValidateFrequencyCode())) {
			// get billing frequency code
			String boCode = part.getBillingFrequencyCode();
			if (StringUtils.isBlank(boCode)) {
				processErrorMsg(
						ConfiguratorViewKeys.BILLING_OPTION_CANNOT_BLANK,
						ConfiguratorViewKeys.BILLING_FREQUENCY,
						ConfiguratorParamKeys.paramIDPreFix + part.getPartNum()
								+ ConfiguratorParamKeys.billingFrequencySuffix,
						contract);
			} else {
				// 1. billing frequency code must not be "N/A"
				if ("N/A".equals(boCode)) {
					processErrorMsg(
							ConfiguratorViewKeys.NO_BILLING_OPTIONS,
							ConfiguratorViewKeys.BILLING_FREQUENCY,
							ConfiguratorParamKeys.paramIDPreFix
									+ part.getPartNum()
									+ ConfiguratorParamKeys.billingFrequencySuffix,
							contract);

				}
				if (!(QuoteConstants.SaaSBillFreq.UPFRONT.equals(boCode)
						|| QuoteConstants.SaaSBillFreq.EVENT.equals(boCode)
						|| QuoteConstants.SaaSBillFreq.MONTHLY.equals(boCode)
						|| QuoteConstants.SaaSBillFreq.QUARTERLY.equals(boCode) || QuoteConstants.SaaSBillFreq.ANNUAL
							.equals(boCode))) {
					processErrorMsg(
							ConfiguratorViewKeys.BILLING_OPTION_MUST_BE_AUEMQ,
							ConfiguratorViewKeys.BILLING_FREQUENCY,
							ConfiguratorParamKeys.paramIDPreFix
									+ part.getPartNum()
									+ ConfiguratorParamKeys.billingFrequencySuffix,
							contract);
					return;
				}
				// 2.No need to validate contract term when billing option is
				// UPFRONT
				// or EVENT
				if (QuoteConstants.SaaSBillFreq.UPFRONT.equals(boCode)
						|| QuoteConstants.SaaSBillFreq.EVENT.equals(boCode)) {
					return;
				}
				/**
				 * <pre>
				 *  3.justify billing frequency accord with months
				 *  3.1.if select quarterly , months must be multiple of 3
				 *  3.2.if select annual ,  mohths must be multiple of 12
				 * </pre>
				 */
				try {
					int months = QuoteCommonUtil.getBillingOptioMonths(boCode);
					if ((term % months) != 0) {
						processErrorMsg(
								ConfiguratorViewKeys.BILLING_OPTION_NOT_VALID,
								ConfiguratorViewKeys.BILLING_FREQUENCY,
								ConfiguratorParamKeys.paramIDPreFix
										+ part.getPartNum()
										+ ConfiguratorParamKeys.billingFrequencySuffix,
								contract);
					}
				} catch (QuoteException e) {
					logger.error(this, e);
					return;
				}
			}
		}
	}

	private void validateQuantity(ConfiguratorPart part,
			AddOrUpdateConfigurationContract contract) {
		// 1.validate main part Quantity
		validateQuantity(part, false, 0, contract);
		// 2.if the main part Qty is null set the default value 0
		int mainQty = part.getPartQty() == null ? 0 : part.getPartQty();
		List<ConfiguratorPart> rampUpPartList = part.getRampUpLineItems();
		if (part.getRampUpLineItems() != null) {
			int i = 0;
		for (ConfiguratorPart rampUpPart : rampUpPartList) {
			// 3.1 if rampUp Qty is null set the dafault value 0
				int rampUpQty = rampUpPart.getPartQty() == null ? 0
						: rampUpPart.getPartQty();
			// 3.2 if rampUp Qty can't be 0
				if (rampUpPart.getPartQty() == null) {
					String key = ConfiguratorParamKeys.paramIDPreFix
							+ part.getPartNum() + "_" + i
							+ ConfiguratorParamKeys.rampUpQtySuffix;

					processErrorMsg(ConfiguratorViewKeys.RAMP_UP_QTY_NOT_ZERO,
							ConfiguratorViewKeys.QUANTITY_MSG_HDR, key,
							contract);

				}
			// 3.3 validate rampUp Quantity
				validateQuantity(rampUpPart, true, i, contract);
			// 3.4 rampUpQty can't lager than mainQty
				if (rampUpQty > mainQty) {
					String key = ConfiguratorParamKeys.paramIDPreFix
							+ part.getPartNum() + "_" + i
							+ ConfiguratorParamKeys.rampUpQtySuffix;

					processErrorMsg(
							ConfiguratorViewKeys.RAMP_UP_QTY_LESS_THAN_MAIN,
							ConfiguratorViewKeys.QUANTITY_MSG_HDR, key,
							contract);

				}
				i++;
			}
		}
	}

	private void validateQuantity(ConfiguratorPart part, boolean isRampUp,
			int rampSeqNum, AddOrUpdateConfigurationContract contract) {
		if (part.isMustHaveQty()) {
			String key = part.getPartNum() + ConfiguratorParamKeys.qtySuffix;
			if (isRampUp) {
				key = part.getPartNum() + "_" + rampSeqNum
						+ ConfiguratorParamKeys.rampUpQtySuffix;
			}
			key = ConfiguratorParamKeys.paramIDPreFix + key;
			int partQty = 0;
			// 1. for part with quantity, quantity must be integer
			try {
				partQty = Integer.parseInt(part.getPartQtyStr());
			} catch (Exception e) {
				processErrorMsg(ConfiguratorViewKeys.QUANTITY_MSG_WHOLE_NUMBER,
						ConfiguratorViewKeys.QUANTITY_MSG_HDR, key, contract);

			}
			// 2. part quantity must lager than 0 and smaller than 2147483647
			if (partQty < 0 || partQty > 2147483647) {
				processErrorMsg(ConfiguratorViewKeys.QUANTITY_MSG_WHOLE_NUMBER,
						ConfiguratorViewKeys.QUANTITY_MSG_HDR, key, contract);
			}
			// 3. validate part quantity is multiple of tier quantity
			if (QuoteCommonUtil.needValidateSaaSMultiple(part)
					&& part.getPartQty() != null) {
				int qty = part.getPartQty().intValue();
				if (!isRampUp && (qty % part.getTierQtyMeasre() != 0)) {
					processErrorMsg(ConfiguratorViewKeys.QUANTITY_MSG_MULTIPLE,
							new String[] {
									ConfiguratorViewKeys.QUANTITY_MSG_HDR,
									part.getTierQtyMeasre().toString() },
							new boolean[] { true, false }, key, contract);
				}
				if (isRampUp
						&& ((qty % part.getTierQtyMeasre() != 0) && (qty != 1))) {
					processErrorMsg(
							ConfiguratorViewKeys.QUANTITY_MSG_MULTIPLE_OR_ONE,
							new String[] {
									ConfiguratorViewKeys.QUANTITY_MSG_HDR,
									part.getTierQtyMeasre().toString() },
							new boolean[] { true, false }, key, contract);
				}
			}
		}
	}

	private void validateTerm(AddOrUpdateConfigurationContract contract) {
		String key = ConfiguratorParamKeys.paramIDPreFix
				+ ConfiguratorParamKeys.term;
		if (contract.getTerm() == null) {
			processErrorMsg(ConfiguratorViewKeys.TERM_MSG_WHOLE_NUMBER,
					ConfiguratorViewKeys.TERM, key, contract);
		} else {
			int term = contract.getTerm().intValue();
			if (term <= 0 || term > 999) {
				processErrorMsg(ConfiguratorViewKeys.TERM_MSG_WHOLE_NUMBER,
						ConfiguratorViewKeys.TERM, key,
						contract);
			}
		}
	}

	protected abstract void processErrorMsg(String msg, String arg, String key,
			AddOrUpdateConfigurationContract contract);

	protected abstract void processErrorMsg(String msg, String[] args,
			boolean[] isArgsResource, String key,
			AddOrUpdateConfigurationContract contract);

	public List<ScwAddonTradeUpErrorCode> getScwAddonTradeUpErrorCodeList() {
		return scwAddonTradeUpErrorCodeList;
	}
}
