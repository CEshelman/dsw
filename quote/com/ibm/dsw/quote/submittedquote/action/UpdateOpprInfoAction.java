package com.ibm.dsw.quote.submittedquote.action;

import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.opportunity.exception.OpportunityDSException;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcess;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.contract.UpdateOpprInfoContract;
import com.ibm.ead4j.jade.bean.MessageBean;
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
 * The <code>UpdateOpprInfoAction</code> class is to update the opportunity
 * info.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 15, 2007
 */
public class UpdateOpprInfoAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        UpdateOpprInfoContract opprContract = (UpdateOpprInfoContract) contract;

        QuoteHeader quoteHeader = getQTHeaderByNum(opprContract.getQuoteNum());
        if (quoteHeader == null) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }

        String opprNum = quoteHeader.getOpprtntyNum() == null ? "" : quoteHeader.getOpprtntyNum();
        String exemptionCode = quoteHeader.getExemptnCode() == null ? "" : quoteHeader.getExemptnCode();
        
        String opprNumOld = opprNum;
        String exemptionCodeOld = exemptionCode;
        
        if (DraftQuoteConstants.OPPNUM_RADIO_OPPNUM.equals(opprContract.getOppNumRadio())) {
            opprNum = StringUtils.trimToEmpty(opprContract.getOpprtntyNum());
            exemptionCode = "";
        } else if (DraftQuoteConstants.SELECT_RADIO_OPPNUM.equals(opprContract.getOppNumRadio())) {
            opprNum = StringUtils.trimToEmpty(opprContract.getOpprtntyNumSel());
            exemptionCode = "";
        }else if (DraftQuoteConstants.OPPNUM_RADIO_80_EXEMPTNCODE.equals(opprContract.getOppNumRadio())) {
            opprNum = "";
            exemptionCode = StringUtils.trimToEmpty(opprContract.getExemptnCode());
        }else if (DraftQuoteConstants.OPPNUM_RADIO_EXEMPTNCODE.equals(opprContract.getOppNumRadio())) {
            opprNum = "";
            exemptionCode = StringUtils.trimToEmpty(opprContract.getExemptnCode());
        }
        
	    //1. Call SAP RFC to update the quote oppr info
	     if (quoteHeader.isRenewalQuote() && opprContract.getQuoteUserSession() == null) {
	        return handleFailedRequest(handler, opprContract.getLocale());
	     }
	     else if (!updateSAPQuoteOppr(quoteHeader, opprNum, exemptionCode, opprContract.getQuoteUserSession())) {
	        return handleFailedRequest(handler, opprContract.getLocale());
	     }
	
	     //2. Store the oppr info and add audit history item in DB2
	     try {
	         QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
	         quoteProcess.updateOpprInfo(quoteHeader.getWebQuoteNum(), opprNumOld, opprNum,
	         		exemptionCodeOld, exemptionCode, opprContract.getUserId());
	         quoteProcess.updateSapIDocNum(quoteHeader.getWebQuoteNum(), quoteHeader.getSapIntrmdiatDocNum(), opprContract
	                 .getUserId(), SubmittedQuoteConstants.USER_ACTION_CHG_OPP_NUM);
	     } catch (QuoteException qe) {
	         logContext.error(this, LogThrowableUtil.getStackTraceContent(qe));
	         return handleFailedRequest(handler, opprContract.getLocale());
	     }

	        return handleSuccessfulRequest(handler, opprContract.getLocale());
    }

    private boolean updateSAPQuoteOppr(QuoteHeader quoteHeader, String opprNum, String exemptionCode,
            QuoteUserSession salesRep) {
        logContext.debug(this, "Calling SAP QuoteModify RFC to update the submitted quote ["
                + quoteHeader.getWebQuoteNum() + "] ...");
        boolean successful = true;
        try {
            quoteHeader.setOpprtntyNum(opprNum);
            quoteHeader.setExemptnCode(exemptionCode);
            QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
            if (quoteHeader.isSalesQuote()) {
                quoteModifyService.modifySQExempCodeAndOppNum(quoteHeader);
            } else if (quoteHeader.isRenewalQuote()) {
                quoteModifyService.modifyRQExempCodeAndOppNum(quoteHeader, salesRep);
            }
        } catch (WebServiceException re) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(re));
            successful = false;
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
        }
        if (successful) {
            logContext.debug(this, "Successful to modify the submitted quote : " + quoteHeader.getWebQuoteNum());
        } else {
            logContext.debug(this, "Failed to modify the submitted quote : " + quoteHeader.getWebQuoteNum());
        }
        return successful;
    }

    private QuoteHeader getQTHeaderByNum(String webQuoteNum) throws QuoteException {
        QuoteHeader quoteHeader = null;
        try {
            quoteHeader = QuoteProcessFactory.singleton().create().getQuoteHdrInfoByWebQuoteNum(webQuoteNum);
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
        }
        return quoteHeader;
    }

    protected String getValidationForm() {
        return SubmittedQuoteViewKeys.FORM_OPPR_INFO;
    }

    protected ResultBean handleFailedRequest(ResultHandler handler, Locale locale) {
        return handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, ErrorKeys.MSG_REQUEST_FAILED);
    }
    
    protected ResultBean handleBluePageUnavailable(ResultHandler handler, Locale locale) {
        return handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                SubmittedQuoteMessageKeys.MSG_BLUE_PAGE_UNAVAILABLE);
    }
    
    protected ResultBean handleUndo(ResultHandler handler, Locale locale, String messageBundle, String messageKey) {
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(messageKey, messageBundle, locale);
        
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        return resultBean;
    }
    
    protected ResultBean handleSuccessfulRequest(ResultHandler handler, Locale locale) throws ResultBeanException {
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        ResultBean resultBean = handler.getResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_UPDATE_OPPRINFO_SUCCESSFUL,
                I18NBundleNames.BASE_MESSAGES, locale);
        messageBean.addMessage(msg, MessageBeanKeys.INFO);
        resultBean.setMessageBean(messageBean);
        return resultBean;
    }
    
    @SuppressWarnings("unchecked")
	protected boolean validate(ProcessContract contract){
    	UpdateOpprInfoContract ct = (UpdateOpprInfoContract) contract;
        if (!super.validate(contract)) {
            return false;
        }
        QuoteHeader quoteHeader;
		try {
			quoteHeader = getQTHeaderByNum(ct.getQuoteNum());
			if (quoteHeader == null) {
				return false;
			}
			HashMap vMap = new HashMap();
			String opprNum = "";
			if (DraftQuoteConstants.OPPNUM_RADIO_EXEMPTNCODE.equals(ct.getOppNumRadio())
				||(DraftQuoteConstants.SELECT_RADIO_OPPNUM.equals(ct.getOppNumRadio())&&!StringUtils.isEmpty(ct.getOpprtntyNumSel()))){
					return true;
			}  else {
					opprNum = StringUtils.trimToEmpty(ct.getOpprtntyNum());
			}
			boolean valid = true;
			if (null != opprNum && !"".equals(opprNum)){
				logContext.debug(this, "validate opportunity number from EIW:" + opprNum);
				OpportunityCommonProcess opportunityCommonProcess = OpportunityCommonProcessFactory.singleton().create();
				try {
					boolean isValidOpptNum = opportunityCommonProcess.isValidOpptNum(opprNum);
					if(!isValidOpptNum){
						FieldResult fieldResult = new FieldResult();
						valid = false;
						fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_OPP_NUM);
						fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.OPP_NUM);
						vMap.put(DraftQuoteParamKeys.PARAM_SI_SIEBEL_INPUT, fieldResult);
					}
					
				} catch (OpportunityDSException e) {
					FieldResult fieldResult = new FieldResult();
					valid = false;
                    String[] arg = {opprNum};
                    String oppMsg = getI18NString(DraftQuoteMessageKeys.OPP_CANNOT_VALIDATE_FOR_EIW,I18NBundleNames.BASE_MESSAGES,ct.getLocale(),arg);
                    fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, oppMsg);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.OPP_NUM);
					vMap.put(DraftQuoteParamKeys.PARAM_SI_SIEBEL_INPUT, fieldResult);
					logContext.error(this, e.getMessage());
				}
			} else {
				FieldResult fieldResult = new FieldResult();
				valid = false;
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_OPP_NUM);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.OPP_NUM);
				vMap.put(DraftQuoteParamKeys.PARAM_SI_SIEBEL_INPUT, fieldResult);
			}
			addToValidationDataMap(contract, vMap);
			return valid;
		} catch (QuoteException e1) {
				logContext.error(this, e1.getMessage());
				return false;
			}
    }
}
