package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.common.validator.ValidatorMessageKeys;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.ValidationHelper;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.ExtendQuoteExpDateContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExtendQuoteExpDateAction<code> class.
 * 
 * @author: bjlbo@us.ibm.com
 * 
 *          Creation date: Jan 20, 2015
 * 
 * 
 */

public class ExtendQuoteExpDateBaseAction extends BaseContractActionHandler {
	QuoteHeader quoteHeader = null;
	Date originalQuoteExpDate = null;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {

		ExtendQuoteExpDateContract expContract = (ExtendQuoteExpDateContract) contract;
		String webQuoteNum = expContract.getQuoteNum();

		if (quoteHeader == null) {
			boolean loadQuoteResult = loadQuote(expContract);
			if (!loadQuoteResult)
				return handleFailedRequest(handler, expContract.getLocale());
		}
		//If new expDate is null or earlier than current date, should not call SAP web service.
		if (quoteHeader.isSubmittedQuote()&&expContract.isExpDateFilled()&&expContract.isQtExpDateValid()) {

			logContext.debug(this, "Calling SAP QuoteModify RFC...");
			QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
			try {
				boolean result = quoteModifyService.modifySQDateByService(
						quoteHeader, expContract);
				logContext.debug(this,
						"Calling SAP QuoteModify RFC for sales quote completely and result is "
								+ result);
			} catch (WebServiceException e) {
				logContext.error(this,
						"Failed to modify quote." + e.getMessage());
				return handleFailedRequest(handler, expContract.getLocale());
			}
			logContext
					.debug(this,
							"Calling SAP QuoteModify RFC for sales quote completely and result is " + true);

		}
		QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
		qProcess.updateQuoteExpirationDateExtension(webQuoteNum,
				expContract.getUserId(), expContract.getExpDate(),
				expContract.getExpDateExtensionJustification(),
				expContract.getUpdateSavedQuoteFlag());

		expContract.setSaveSuccess("1");
//		String redirectURL = generateRedirectURL(expContract);
//		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
		//handler.addObject(ParamKeys.PARAM_REDIRECT_URL, getRedirectURL(expContract.getDisplayTabUrl(),webQuoteNum));
		
		//If PARAM_FORWARD_FLAG is setted to true  ,when click 'Go' or 'Save quote as draft' link
		//the next action 'DISPLAY_SUBMITTEDQT_PART_PRICE_TAB' will lose quoteNum parameter . 
		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
//		handler.addObject(ParamKeys.PARAM_SAVE_MSG_2_HTTP_REQ_FLAG,
//				Boolean.TRUE);
		
		//When I click 'save quote as draft' link and quote is saved successfuly,quote header sill prompt a success message
		//So PARAM_FORWARD_FLAG should be setted to true
		afterSaveQuote(expContract, handler);
		
		ResultBean resultBean = handler.getResultBean();
		
		
		Locale locale = expContract.getLocale();
		HashMap vMap = expContract.getValidationDataMap();

		vMap = (HashMap) vMap.get(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY);
		if (vMap != null) {
			HashMap manualMsg = ValidationHelper.getManualValidationErrMsg(
					vMap, locale);
			MessageBean validationMessagesBean = MessageBeanFactory
					.create(manualMsg);

			resultBean.setMessageBean(validationMessagesBean);
		}

		return resultBean;

	}

	protected void afterSaveQuote(ExtendQuoteExpDateContract expContract,
			ResultHandler handler) {

	}

	/**
	 * @param expContract
	 */
	protected boolean loadQuote(ExtendQuoteExpDateContract expContract) {
		try {
			quoteHeader = QuoteProcessFactory
					.singleton()
					.create()
					.getQuoteHdrInfoByWebQuoteNumWithTransaction(
							expContract.getQuoteNum());

			if (quoteHeader.getPriorQuoteNum() != null) {
				originalQuoteExpDate = quoteHeader.getPriorQuoteExpDate();
			}
		} catch (NoDataException e) {
			logContext.error(this,
					"Failed to load prior quote." + e.getMessage());
			return false;
		} catch (QuoteException e) {
			logContext.error(this,
					"Failed to load prior quote." + e.getMessage());
			return false;
		}
		return true;
	}

	private String getRedirectURL(String displayTabUrl, String quoteNum) {
		if (null == displayTabUrl || "".equals(displayTabUrl))
			displayTabUrl = SubmittedQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL;
		String baseUrl = HtmlUtil.getURLForAction(displayTabUrl);
		StringBuffer url = new StringBuffer(baseUrl);
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
		return url.toString();
	}

	private List<String> getRedirectMsgList() {
		List<String> redirectMsgList = new ArrayList<String>();
		redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":"
				+ SubmittedQuoteMessageKeys.QT_CRAD_SUCCESS_MSG + ":"
				+ MessageBeanKeys.SUCCESS);
		return redirectMsgList;
	}

	protected ResultBean handleInvalidDate(ResultHandler handler, Locale locale) {
		return this.handleUndo(handler, locale,
				MessageKeys.BUNDLE_APPL_I18N_QUOTE,
				DraftQuoteMessageKeys.MSG_ENTER_VALID_EXTENSION_EXP_DATE);
	}

	protected ResultBean handleRequiredFieldExpDate(ResultHandler handler,
			Locale locale) {
		return this.handleUndo(handler, locale,
				MessageKeys.BUNDLE_APPL_I18N_QUOTE,
				DraftQuoteMessageKeys.MSG_EXP_DATE_MUST_BE_FILLED);
	}

	protected ResultBean handleRequiredFieldJustification(
			ResultHandler handler, Locale locale) {
		return this
				.handleUndo(
						handler,
						locale,
						MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						DraftQuoteMessageKeys.MSG_EXP_DATE_EXTENSION_JUSTIFICATION_MUST_BE_FILLED);
	}

	protected ResultBean handleExtensionExpDateBeforeOriginalExpDate(
			ResultHandler handler, Locale locale, Date originalExpDate) {
		String dateStr = DateHelper.getDateByFormat(originalExpDate,
				"dd MMM yyyy");
		return this
				.handleUndo(
						handler,
						locale,
						MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						DraftQuoteMessageKeys.MSG_EXTENSION_EXP_DATE_MUST_BE_ON_OR_AFTER_ORIGINAL_EXP_DATE,
						dateStr);
	}

	protected ResultBean handleExtensionExpDateAfterOriginalExpDateQuarter(
			ResultHandler handler, Locale locale, Date lastDayOfQuarter) {
		String dateStr = DateHelper.getDateByFormat(lastDayOfQuarter,
				"dd MMM yyyy");

		return this
				.handleUndo(
						handler,
						locale,
						MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						DraftQuoteMessageKeys.MSG_EXTENSION_EXP_DATE_MUST_BE_ON_OR_BEFORE_ORIGINAL_EXP_DATE_QUARTER_LAST_DAY,
						dateStr);
	}

	protected ResultBean handleFailedRequest(ResultHandler handler,
			Locale locale) {
		return this.handleUndo(handler, locale,
				MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES,
				ErrorKeys.MSG_REQUEST_FAILED);
	}

	protected ResultBean handleBluePageUnavailable(ResultHandler handler,
			Locale locale) {
		return this.handleUndo(handler, locale,
				MessageKeys.BUNDLE_APPL_I18N_QUOTE,
				SubmittedQuoteMessageKeys.MSG_BLUE_PAGE_UNAVAILABLE);
	}

	protected ResultBean handleUndo(ResultHandler handler, Locale locale,
			String messageBundle, String messageKey) {
		ResultBean resultBean = handler.getUndoResultBean();
		MessageBean messageBean = resultBean.getMessageBean();
		String errorMsg = this.getI18NString(messageKey, messageBundle, locale);

		messageBean.addKeyedMessage(messageKey, errorMsg);

		String errKey = messageKey.substring(PARAM_VALIDATION.length());
		String sArg = PARAM_ARGUMENT + errKey;
		String argMsg = this.getI18NString(sArg, messageBundle, locale);
		messageBean.addKeyedMessage(sArg, argMsg);
		resultBean.setMessageBean(messageBean);
		handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
		return resultBean;
	}

	protected ResultBean handleUndo(ResultHandler handler, Locale locale,
			String messageBundle, String messageKey, String appendMessage) {
		ResultBean resultBean = handler.getUndoResultBean();
		MessageBean messageBean = resultBean.getMessageBean();
		String errorMsg = this.getI18NString(messageKey, messageBundle, locale);

		messageBean.addMessage(errorMsg + " " + appendMessage + ".",
				MessageBeanKeys.ERROR);
		resultBean.setMessageBean(messageBean);
		handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
		return resultBean;
	}

	private final String PARAM_VALIDATION = "vld_";

	private final String PARAM_ARGUMENT = "arg_";

	private String generateRedirectURL(ExtendQuoteExpDateContract baseContract) {
		String redirectURL = baseContract.getRedirectURL();

		String connector = "&";
		StringBuffer sb = new StringBuffer(redirectURL);
		sb.append(connector).append(ParamKeys.PARAM_QUOTE_NUM).append("=")
				.append(baseContract.getQuoteNum());
		return sb.toString();
	}
}
