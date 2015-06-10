/**
 * 
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.ValidationMessageFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuickEnrollmentServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.OrderSubmittedQuoteContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OrderSubmittedQuoteAction<code> class.
 * 
 * Creation date: 2014-02-26
 * 
 * @author: liruiwh@cn.ibm.com 
 * Description: Do some validation before order quote. If no issue, redirect to eorder url.
 * 
 */
@SuppressWarnings("rawtypes")
public class OrderSubmittedQuoteAction extends BaseContractActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7576504846571380516L;

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
	throws QuoteException, ResultBeanException {
		OrderSubmittedQuoteContract osctrct = (OrderSubmittedQuoteContract) contract;
		// fix appscan issue
		validateParams(osctrct);
		
		//if it contains saas part, then validate ToU.
		if(osctrct.isHasSaas()){
			Map validation = validateMissingTou(osctrct);
			if(validation != null && validation.size() > 0){
				return handleValidateResult(validation, handler, osctrct.getLocale());
			}
		}
		QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
		Quote quote;
		try {
			quote = qProcess.getDraftQuoteBaseInfoByQuoteNum(osctrct.getQuoteNum());
			if (null != quote) {
				// Quick enrollment CSRA customer that no contract num
				if (quote.getQuoteHeader().isCSRAQuote()
						&& (quote.getQuoteHeader().hasNewCustomer() || quote.getQuoteHeader().getWebCtrctId() > 0)
						&& StringUtils.isEmpty(quote.getQuoteHeader().getContractNum())) {
					this.quickEnrollment(quote);
				}
			}
		} catch (NoDataException e) {
			logContext.error(this, e.getMessage());
		} catch (WebServiceFailureException e) {
			logContext.error(this, e.getMessage());
			e.printStackTrace();
		}

		//if passed validation: redirect to eorder application
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, getEOrderUrl(osctrct));
		handler.setState(StateKeys.STATE_REDIRECT_ORDER_ACTION);
		return handler.getResultBean();
	}
	
	private void validateParams(OrderSubmittedQuoteContract osctrct)throws QuoteException{
		
		if(StringUtils.isNotBlank(osctrct.getQuoteNum())){
			if(!SecurityUtil.isValidInput(SubmittedQuoteParamKeys.PARAM_QUOTE_NUM,osctrct.getQuoteNum(), "Numeric", 10, true)){
				throw new QuoteException("Invalid quoteNum value.");
			}
		}
		if(!SecurityUtil.isValidInput(SubmittedQuoteParamKeys.PARAM_SAP_QUOTE_NUM,osctrct.getSapQuoteNum(), "Numeric", 10, true)){
			throw new QuoteException("Invalid sapQuoteNum value.");
		}
		if(!SecurityUtil.isValidInput(SubmittedQuoteParamKeys.PARAM_PO,osctrct.getP0(), "Alpha", 3, true)){
			throw new QuoteException("Invalid P0 value.");
		}
		if(!SecurityUtil.isValidInput(SubmittedQuoteParamKeys.PARAM_ISORDERNOW,osctrct.getIsOrderNow(), "Alpha", 5, true)){
			throw new QuoteException("Invalid isOrderNow value.");
		}
		if(StringUtils.isNotBlank(osctrct.getHasSaasStr())){
			if(!SecurityUtil.isValidInput(SubmittedQuoteParamKeys.PARAM_HASSAAS,osctrct.getHasSaasStr(), "Alpha", 5, true)){
				throw new QuoteException("Invalid hasSaas value.");
			}
		}
	}
	
	/**
	 * generate the eorder url which was redirected to.
	 * @param contract
	 * @return
	 */
	private String getEOrderUrl(OrderSubmittedQuoteContract contract){
		String orderUrl = ApplicationContextFactory.singleton().getApplicationContext()
		.getConfigParameter(
				ApplicationProperties.SUBMITTED_SALES_QUOTE_ORDER_URL);
		orderUrl += orderUrl.indexOf("?") < 0? "?" :"&";
		
		//prepare params:
		//   P0=&isOrderNow=false&quote=
		Map<String, String> params = new HashMap<String, String>();
		params.put(SubmittedQuoteParamKeys.PARAM_PO, SecurityUtil.htmlEncode(contract.getP0()));
		params.put(SubmittedQuoteParamKeys.PARAM_ISORDERNOW, SecurityUtil.htmlEncode(contract.getIsOrderNow()));
		params.put(SubmittedQuoteParamKeys.PARAM_SAP_QUOTE_NUM, SecurityUtil.htmlEncode(contract.getSapQuoteNum()));
		//append params
		orderUrl += buildParamsQuery(params);
		
		return orderUrl;
	}
	
	private Map validateMissingTou(OrderSubmittedQuoteContract contract) throws QuoteException{
		try {
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			Quote quote = quoteProcess.getQuoteForSubmittedCRAD(contract.getQuoteNum());
			QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
			Map result = qcProcess.validateTouForOrder(contract.getQuoteUserSession(), quote, null);
			//String missingTouMsg = (String)result.get(QuoteCapabilityProcess.SUBMIT_PART_LOST_TOU_MSG);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    protected String getValidMsgHead(Locale locale) {
        return this.getI18NString(DraftQuoteMessageKeys.ORDER_FAILED_MSG, BaseI18NBundleNames.QUOTE_BASE, locale);
    }
    
    protected ResultBean handleValidateResult(Map validResult, ResultHandler handler, Locale locale) {
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        
        String msgHead = getValidMsgHead(locale);
        messageBean.addMessage(msgHead, MessageKeys.LAYER_MSG_HEAD);

        Set keySet = validResult.keySet();
        Iterator iter = keySet.iterator();
        
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = validResult.get(key);
            String param = null;
            
            // if the value is a string, it will be considered as the message parameter.
            if (value != null && value instanceof String) {
                param = (String) value;
            }

            String message = ValidationMessageFactory.singleton().getValidationMessage(key, locale, param);
            messageBean.addMessage(message, MessageKeys.LAYER_MSG_ITEM);
        }

        return resultBean;
    }

	/**
	 * build the param map to a URL query string
	 * @param params
	 * @return
	 */
	private String buildParamsQuery(Map<String, String> params) {
		if(params == null || params.size() == 0) return "";
		StringBuffer s = new StringBuffer();
		Set<String> keys = params.keySet();
		for(String key : keys){
			s.append(key).append("=");
			String val = params.get(key);
			if( val != null){
				try {
					s.append(URLEncoder.encode(val, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					s.append(val);
				}
			}
			s.append("&");
		}
		if (s.length() > 0) s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	/**
	 * To call SAP RFC to enrollment this new customer in SAP
	 * 
	 * @param quote
	 * @throws QuoteException
	 * @throws WebServiceFailureException
	 */
	protected void quickEnrollment(Quote quote) throws QuoteException, WebServiceFailureException {
		logContext.debug(this, "To call SAP RFC to enrollment this new customer in SAP:..." + quote.getCustomer().getCustName());
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		String enrllmtNum = quoteProcess.getWebEnrollmentNum();
		boolean isRequireSignature = CountrySignatureRuleFactory.singleton().isRequireSignature(
				quote.getCustomer().getCountryCode());
		try {
			QuickEnrollmentServiceHelper service = new QuickEnrollmentServiceHelper();
			boolean isSuccessfull = service.callQuickEnrollmentService(quote, isRequireSignature, enrllmtNum);
			if (isSuccessfull) {
				quoteProcess.updateSapCtrctNum(quote.getQuoteHeader().getWebQuoteNum(), quote.getQuoteHeader().getContractNum());
			}
		} catch (WebServiceException e) {
			throw new WebServiceFailureException("Failed to call SAP to quick enroll the new custoemr.",
					MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
		}
	}
}
