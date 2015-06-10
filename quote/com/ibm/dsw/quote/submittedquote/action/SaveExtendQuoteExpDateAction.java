package com.ibm.dsw.quote.submittedquote.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.ExtendQuoteExpDateContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
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
public class SaveExtendQuoteExpDateAction extends ExtendQuoteExpDateBaseAction {

	protected void afterSaveQuote(ExtendQuoteExpDateContract expContract,
			ResultHandler handler) {
		if ("1".equals(StringUtils.trimToEmpty(expContract.getSaveSuccess()))) {
			MessageBean mBean = MessageBeanFactory.create();
			String message = this
					.getI18NString(
							DraftQuoteMessageKeys.SAVE_DRAFT_QUOTE_SUCCESS_MSG,
							MessageKeys.BUNDLE_APPL_I18N_QUOTE,
							expContract.getLocale());
			mBean.addMessage(message, MessageBeanKeys.SUCCESS);
			handler.setMessage(mBean);
		}

	}

	protected boolean validate(ProcessContract contract) {
		super.validate(contract);
		ExtendQuoteExpDateContract expContract = (ExtendQuoteExpDateContract) contract;
		HashMap vMap = new HashMap();
		
		boolean loadQuoteResult = loadQuote(expContract);
		if (!loadQuoteResult)
			return false;

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
			return false;
		}

		return true;

	}

}
