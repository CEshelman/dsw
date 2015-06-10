package com.ibm.dsw.quote.submittedquote.action;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteStatusChangeService;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.ICNPreCreditContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UpdateICNPreCreditAction<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 08, 2007
 */
public class UpdateICNPreCreditAction extends BaseContractActionHandler {
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
		ResultBeanException {
	
        ICNPreCreditContract icnContract = (ICNPreCreditContract) contract;
    
		String webQuoteNum = icnContract.getQuoteNum();
		String reqstICNFlag = icnContract.getChkReqCustNo();
		String reqstPreCreditChkFlag = icnContract.getChkReqCredChk();
		if ( StringUtils.isBlank(reqstICNFlag) && StringUtils.isBlank(reqstPreCreditChkFlag) ){
		    return handleInvalidInputParams(handler, icnContract.getLocale());
		}
		int iReqstICNFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(reqstICNFlag) ? 1 : 0; 
		int iReqstPreCreditChkFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(reqstPreCreditChkFlag) ? 1 : 0;
			
		QuoteHeader quoteHeader;
        try {
            quoteHeader = QuoteProcessFactory.singleton().create().getQuoteHdrInfoByWebQuoteNum(webQuoteNum);
        } catch (NoDataException e) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }
        
        int qtReqstIbmCustNumFlag = quoteHeader.getReqstIbmCustNumFlag();
        int qtReqstPreCreditCheckFlag = quoteHeader.getReqstPreCreditCheckFlag();
        
        if(qtReqstIbmCustNumFlag != iReqstICNFlag || qtReqstPreCreditCheckFlag != iReqstPreCreditChkFlag){
            // one of the two flags is modified,call webservice and sp.
            try {
                quoteHeader.setReqstIbmCustNumFlag(iReqstICNFlag);
                quoteHeader.setReqstPreCreditCheckFlag(iReqstPreCreditChkFlag);
                logContext.debug(this, "Calling Quote status change SAP webservice...");
                
                QuoteStatusChangeService service = new QuoteStatusChangeService();
                service.execute(quoteHeader);
                logContext.debug(this, "Successfully called QuoteStatusChange service");
                    
            } catch (TopazException e) {
                throw new QuoteException(e);
            } catch (WebServiceException e) {
                logContext.error(this, "Failed to call SAP QuoteStatusChange service.");
                return handleFailedRequest(handler, icnContract.getLocale());
            }
            	
    		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
    		quoteProcess.updateExpICNCRD(webQuoteNum, null, new Integer(iReqstICNFlag), new Integer(iReqstPreCreditChkFlag), icnContract.getUserId(), null);
        }
        
		return handlerSuccessfullRequest(handler, icnContract.getLocale());
	
    }
	
	protected ResultBean handleInvalidInputParams(ResultHandler handler, Locale locale) {
	    
	    ResultBean resultBean = handler.getUndoResultBean();
	    MessageBean messageBean = resultBean.getMessageBean();
	    String errorMsg = this.getI18NString(ErrorKeys.MSG_SELECT_CHKBOX,
	            MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, locale);
	    messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
	    resultBean.setMessageBean(messageBean);
	    handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
	    return resultBean;
	
	}
	
	protected ResultBean handleFailedRequest(ResultHandler handler, Locale locale) {
	    
	    ResultBean resultBean = handler.getUndoResultBean();
	    MessageBean messageBean = resultBean.getMessageBean();
	    String errorMsg = this.getI18NString(ErrorKeys.MSG_REQUEST_FAILED,
	            MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, locale);
	    messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
	    resultBean.setMessageBean(messageBean);
	    handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
	    return resultBean;
	
	}
	
	 protected ResultBean handlerSuccessfullRequest(ResultHandler handler, Locale locale) throws ResultBeanException {
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        ResultBean resultBean = handler.getResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String infoMsg = getI18NString(SubmittedQuoteMessageKeys.MSG_REQUEST_ICN_PRECREDIT_SUCCESS,
                MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        messageBean.addMessage(infoMsg, MessageBeanKeys.INFO);
        resultBean.setMessageBean(messageBean);
        return resultBean;
    }

}
