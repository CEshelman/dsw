package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.QtDateContract;
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
 * This <code>UpdateQuoteDateAction<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 14, 2007
 * 
 * 
 * Modification date:July 18,2013
 * @author: jiewbj@cn.ibm.com
 * Description: handler add ParamKeys.PARAM_REDIRECT_MSG,ParamKeys.PARAM_REDIRECT_URL.
 */

public class UpdateQuoteDateAction extends BaseContractActionHandler {
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
		ResultBeanException {

        QtDateContract expContract = (QtDateContract) contract;
		String webQuoteNum = expContract.getQuoteNum();
		QuoteUserSession salesRep = expContract.getQuoteUserSession();

        boolean isQtExpDateValid = expContract.isQtExpDateValid();
        boolean isQtStartDateValid = expContract.isQtStartDateValid();
        boolean isStartDateAfterExpDate = expContract.isStartDateAfterExpDate();
        boolean isCRADDateValid =  expContract.isCRADDateValid();
        
		if ( !isQtExpDateValid )
            return handleInvalidDate(handler, expContract.getLocale());
		
		if ( !isQtStartDateValid || isStartDateAfterExpDate )
            return handleInvalidStartDate(handler, expContract.getLocale());
		
		 if( !isCRADDateValid ){
         	return handleInvalidCRADDate(handler, expContract.getLocale());
         }
		
		QuoteHeader quoteHeader;
		Date expDate = expContract.getExpDate();
		Date startDate = expContract.getStartDate();
		Date cradDate = expContract.getCradDate();
	    try {
	        quoteHeader = QuoteProcessFactory.singleton().create().getQuoteHdrInfoByWebQuoteNum(webQuoteNum);
	    } catch (NoDataException e) {
	        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
	        return handler.getResultBean();
	    }
	    
	    if(quoteHeader.isHasAppMainPart() && startDate!=null && quoteHeader.getCustReqstArrivlDate()!=null){
	    	if(startDate.after(quoteHeader.getCustReqstArrivlDate())){
	            return handleInvalidStartDateForCRAD(handler, expContract.getLocale());
	    	}
	    }
	    if( (expDate!=null && !expDate.equals(quoteHeader.getQuoteExpDate()))
	    		|| (startDate!=null && !startDate.equals(quoteHeader.getQuoteStartDate()))
	    		|| (cradDate!=null && !cradDate.equals(quoteHeader.getCustReqstArrivlDate()))){
	        try {
	            logContext.debug(this, "Calling SAP QuoteModify RFC...");
	            QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();	            
	            
	            if (quoteHeader.isSalesQuote()) {
	            	boolean result = quoteModifyService.modifySQDateByService(quoteHeader, expContract);
	            	logContext.debug(this, "Calling SAP QuoteModify RFC for sales quote completely and result is " + result);
	            } else {
	                if (salesRep != null){
	                	boolean result = quoteModifyService.modifyRQSpecBidExpiratrionDate(quoteHeader, salesRep, expContract);
	                	logContext.debug(this, "Calling SAP QuoteModify RFC for sales rep completely and result is " + result);
	                }
	                else
	                    return this.handleBluePageUnavailable(handler, expContract.getLocale());
	            }
	            
	        } catch (WebServiceException e) {
	            logContext.error(this, "Failed to modify quote." + e.getMessage());
	            return handleFailedRequest(handler, expContract.getLocale());
	        }
	        
		    QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quoteProcess.updateExpICNCRD(webQuoteNum, expDate, null, null, expContract.getUserId(), startDate, cradDate);
			if((expDate!=null && !expDate.equals(quoteHeader.getQuoteExpDate()))
	    		|| (startDate!=null && !startDate.equals(quoteHeader.getQuoteStartDate())))
				quoteProcess.updateSapIDocNum(webQuoteNum, quoteHeader.getSapIntrmdiatDocNum(), expContract.getUserId(),
				SubmittedQuoteConstants.USER_ACTION_CHG_EXP_DATE);
			if(cradDate!=null && !cradDate.equals(quoteHeader.getCustReqstArrivlDate()))
				quoteProcess.updateSapIDocNum(webQuoteNum, quoteHeader.getSapIntrmdiatDocNum(), expContract.getUserId(),
		        SubmittedQuoteConstants.USER_ACTION_MODIFY_CRAD);
			
			//Add change log for CRAD updated
			if((cradDate!=null && !cradDate.equals(quoteHeader.getCustReqstArrivlDate()))){
				quoteProcess.addQuoteAuditHist(webQuoteNum,
						null, expContract.getUserId(), SubmittedQuoteConstants.USER_ACTION_UPDT_CRAD,
						quoteHeader.getCustReqstArrivlDate().toString(),new java.sql.Date(cradDate.getTime()).toString());  
			}
	    }
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, getRedirectURL(expContract.getDisplayTabUrl(),webQuoteNum));
		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE); 
		if(cradDate!=null && !cradDate.equals(quoteHeader.getCustReqstArrivlDate()))
		{
				handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, getRedirectMsgList());
				}
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		return handler.getResultBean();
	
	}
    
	private String getRedirectURL(String displayTabUrl,String quoteNum){
		if(null==displayTabUrl||"".equals(displayTabUrl))displayTabUrl=SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB;
		String baseUrl = HtmlUtil.getURLForAction(displayTabUrl);
    	StringBuffer url = new StringBuffer(baseUrl);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
    	return url.toString();
	}

	private List<String> getRedirectMsgList() {
		List<String> redirectMsgList = new ArrayList<String>();
    	redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.QT_CRAD_SUCCESS_MSG + ":" + MessageBeanKeys.SUCCESS);
    	return redirectMsgList;
	}

	protected ResultBean handleInvalidDate(ResultHandler handler, Locale locale) {
        return this.handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                DraftQuoteMessageKeys.MSG_ENTER_VALID_EXP_DATE);
	}
    
    protected ResultBean handleInvalidStartDate(ResultHandler handler, Locale locale) {
        return this.handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                DraftQuoteMessageKeys.MSG_ENTER_VALID_START_DATE);
	}

    protected ResultBean handleInvalidStartDateForCRAD(ResultHandler handler, Locale locale) {
        return this.handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                DraftQuoteMessageKeys.MSG_ENTER_VALID_START_DATE_FOR_CRAD);
	}
    
    protected ResultBean handleInvalidCRADDate(ResultHandler handler, Locale locale) {
        return this.handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                DraftQuoteMessageKeys.MSG_INVALID_CUST_REQSTD_ARRIVL_DATE_);
	}
	
	protected ResultBean handleFailedRequest(ResultHandler handler, Locale locale) {
	    return this.handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES,
	            ErrorKeys.MSG_REQUEST_FAILED);
	}
	
	protected ResultBean handleBluePageUnavailable(ResultHandler handler, Locale locale) {
	    return this.handleUndo(handler, locale, MessageKeys.BUNDLE_APPL_I18N_QUOTE,
	            SubmittedQuoteMessageKeys.MSG_BLUE_PAGE_UNAVAILABLE);
	}
	
	protected ResultBean handleUndo(ResultHandler handler, Locale locale, String messageBundle, String messageKey) {
	    ResultBean resultBean = handler.getUndoResultBean();
	    MessageBean messageBean = resultBean.getMessageBean();
	    String errorMsg = this.getI18NString(messageKey, messageBundle, locale);
	    
	    messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
	    resultBean.setMessageBean(messageBean);
	    handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
	    return resultBean;
	}

}
