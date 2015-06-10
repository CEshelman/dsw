package com.ibm.dsw.quote.submittedquote.action;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.CopyUpdateSbmtQuoteContract;
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
 * This <code>CopyUpdateSbmtQuoteAction<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 04, 2007
 */
public class CopyUpdateSbmtQuoteAction extends BaseContractActionHandler {
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
    	ResultBeanException {
		
        CopyUpdateSbmtQuoteContract cuContract = (CopyUpdateSbmtQuoteContract) contract;
        
		String webQuoteNum = cuContract.getQuoteNum();
		String creatorId = cuContract.getUserId();
		String webRefFlag = cuContract.getIncludeRef();
		String copyFlag = cuContract.getCopyUpdateFlag();
		String redirectURL = cuContract.getRedirectURL();
		
		if (StringUtils.isBlank(webQuoteNum) || (StringUtils.isBlank(copyFlag))) {
		    return handleInvalidInputParams(handler, cuContract.getLocale());
		}
		
		int iWebRefFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(webRefFlag) ? 1 : 0;
		
		//3 means copy approved sb for price increase
		//4 means bid iteration
		//5 means copy for output option change
		//6 means copy for changing expiration date
		if(QuoteConstants.QT_COPY_TYPE_PRICINC_STR.equalsIgnoreCase(webRefFlag) 
		        || QuoteConstants.QT_COPY_TYPE_BIDITER_STR.equalsIgnoreCase(webRefFlag)
		        || QuoteConstants.QT_COPY_TYPE_OPTCHG_STR.equalsIgnoreCase(webRefFlag)
		        || QuoteConstants.QT_COPY_TYPE_EXPIRDATE_STR.equalsIgnoreCase(webRefFlag)){
		    iWebRefFlag = Integer.parseInt(webRefFlag);
		}
		//if copyFlag=1, copy quote with webRefFlag value; if copyFlag=0, update quote with webRefFlag=1 
		iWebRefFlag = copyFlag.equals("1") ? iWebRefFlag : 1; //TODO check checkbox value - on or 1
		
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		String destQuoteNum = quoteProcess.copyUpdateSbmtQuoteToSession(webQuoteNum, creatorId, iWebRefFlag);
		
		if(QuoteConstants.QT_COPY_TYPE_PRICINC_STR.equalsIgnoreCase(webRefFlag)||QuoteConstants.QT_COPY_TYPE_EXPIRDATE_STR.equalsIgnoreCase(webRefFlag)){
		   redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ParamKeys.PARAM_QUOTE_NUM, destQuoteNum).toString();
		}

		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		return handler.getResultBean();
		
	}
		
	protected ResultBean handleInvalidInputParams(ResultHandler handler, Locale locale) {
	    
	    ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(NewQuoteMessageKeys.MSG_URL_PARAM_NOT_VALID,
                MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, locale);
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        return resultBean;

	}

}
