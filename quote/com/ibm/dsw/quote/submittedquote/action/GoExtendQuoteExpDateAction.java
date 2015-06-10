package com.ibm.dsw.quote.submittedquote.action;

import java.util.HashMap;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.contract.ExtendQuoteExpDateContract;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PostExtendQuoteExpDateAction </code> class .
 * 
 * 
 * @author <a href="bjlbo@cn.ibm.com">Bourne</a> <br/>
 * 
 *         Creation date: Feb 5, 2015
 */
public class GoExtendQuoteExpDateAction extends ExtendQuoteExpDateBaseAction {

	protected boolean validate(ProcessContract contract) {
		boolean isValid = super.validate(contract);
		if (!isValid)
			return isValid;

		ExtendQuoteExpDateContract expContract = (ExtendQuoteExpDateContract) contract;
		HashMap vMap = new HashMap();
		
		boolean loadQuoteResult = loadQuote(expContract);
		if (!loadQuoteResult)
			return false;
		

		// validate required fileds
		boolean isExpDateFilled = expContract.isExpDateFilled();
		if (!isExpDateFilled) {
			FieldResult fieldResult = new FieldResult();
			fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.MSG_EXP_DATE_MUST_BE_FILLED);
			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.EXTEND_EXP_DATE);
			vMap.put(DraftQuoteParamKeys.EXP_DATE_EXTENSION_INPUT, fieldResult);
			addToValidationDataMap(contract, vMap);
			isValid = false;
		}
		// expiration date is valid and after current date.
		boolean isQtExpDateValid = expContract.isQtExpDateValid();
		if (!isQtExpDateValid) {
			FieldResult fieldResult = new FieldResult();
			fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.MSG_ENTER_VALID_EXTENSION_EXP_DATE);
			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.EXTEND_EXP_DATE);
			vMap.put(DraftQuoteParamKeys.EXP_DATE_EXTENSION_INPUT, fieldResult);
			addToValidationDataMap(contract, vMap);
			isValid = false;
		}
		// extended expiration date cannot before original quote expiration date
		boolean isExtensionExpDateBeforeOriginalExpDate = expContract
				.isExtensionExpDateBeforeOriginalExpDate(originalQuoteExpDate);
		if (isExtensionExpDateBeforeOriginalExpDate) {
			FieldResult fieldResult = new FieldResult();
			String dateStr = DateHelper.getDateByFormat(originalQuoteExpDate,
					"dd MMM yyyy");
			String[] arg = { dateStr };
			String oppMsg = getI18NString(
					DraftQuoteMessageKeys.MSG_EXTENSION_EXP_DATE_MUST_BE_ON_OR_AFTER_ORIGINAL_EXP_DATE,
					I18NBundleNames.BASE_MESSAGES, expContract.getLocale(), arg);
			fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, oppMsg);

			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.EXTEND_EXP_DATE);
			vMap.put(DraftQuoteParamKeys.EXP_DATE_EXTENSION_INPUT, fieldResult);
			addToValidationDataMap(contract, vMap);
			isValid = false;
		}

		// extended expiration date cannot after the last day of quarter of
		// original quote expiration date
		boolean isExtensionExpDateAfterLastDayOfQuarterOfOriginalExpDate = expContract
				.isExtensionExpDateAfterLastDayOfQuarterOfOriginalExpDate(originalQuoteExpDate);
		if (isExtensionExpDateAfterLastDayOfQuarterOfOriginalExpDate) {
			FieldResult fieldResult = new FieldResult();
			String dateStr = DateHelper.getDateByFormat(
					DateUtil.getLastDayOfQuarter(originalQuoteExpDate),
					"dd MMM yyyy");
			String[] arg = { dateStr };
			String oppMsg = getI18NString(
					DraftQuoteMessageKeys.MSG_EXTENSION_EXP_DATE_MUST_BE_ON_OR_BEFORE_ORIGINAL_EXP_DATE_QUARTER_LAST_DAY,
					I18NBundleNames.BASE_MESSAGES, expContract.getLocale(), arg);
			fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, oppMsg);

			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.EXTEND_EXP_DATE);
			vMap.put(DraftQuoteParamKeys.EXP_DATE_EXTENSION_INPUT, fieldResult);
			addToValidationDataMap(contract, vMap);
			isValid = false;
		}

		boolean isExpDateExtensionJustificationFilled = expContract
				.isExpDateExtensionJustificationFilled();
		if (!isExpDateExtensionJustificationFilled) {
			FieldResult fieldResult = new FieldResult();
			fieldResult
					.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
							DraftQuoteMessageKeys.MSG_EXP_DATE_EXTENSION_JUSTIFICATION_MUST_BE_FILLED);
			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					DraftQuoteMessageKeys.EXP_DATE_EXTENSION_JUSTIFICATION);
			vMap.put(
					DraftQuoteParamKeys.EXP_DATE_EXTENSION_JUSTIFICATION_INPUT,
					fieldResult);
			addToValidationDataMap(contract, vMap);
			isValid = false;
		}

		return isValid;

	}

}
