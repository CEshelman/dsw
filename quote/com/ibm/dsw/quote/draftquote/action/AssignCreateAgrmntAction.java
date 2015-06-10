package com.ibm.dsw.quote.draftquote.action;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.exception.CtrctInactiveException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.contract.AssignCreateAgrmntContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>AssignCreateAgrmntAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Oct 30, 2009
 */

public class AssignCreateAgrmntAction extends BaseContractActionHandler {
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        AssignCreateAgrmntContract agrmCtrct = (AssignCreateAgrmntContract) contract;
        String agrmntType = agrmCtrct.getAgreementType();
        String agrmntNum = null;
        Integer webCtrctId = null;
        String userID = agrmCtrct.getUserId();
        
        if (StringUtils.isBlank(agrmntType)) {
			String msg = getI18NString(CustomerMessageKeys.AGRMNT_EMPTY_MSG,
					MessageKeys.BUNDLE_APPL_I18N_QUOTE, agrmCtrct.getLocale());
			handler.addMessage(msg, MessageBeanKeys.ERROR);
			return handler.getUndoResultBean();
		}
        
        
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        
        if (CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(agrmntType)) {
            agrmntNum = agrmCtrct.getAgreementNumber();
            if (StringUtils.isBlank(agrmntNum)) {
    			String msg = getI18NString(CustomerMessageKeys.INVLD_CTRCT_ERR_MSG,
    					MessageKeys.BUNDLE_APPL_I18N_QUOTE, agrmCtrct.getLocale());
    			handler.addMessage(msg, MessageBeanKeys.ERROR);
    			return handler.getUndoResultBean();
            }
        }
        else {
            int retCtrctId = quoteProcess.createOrUpdateWebCtrct(agrmCtrct.getWebCtrctIdAsInt(), agrmCtrct
                    .getAgreementType(), agrmCtrct.getAuthrztnGroup(), agrmCtrct.getTransSVPLevel(), userID);
            webCtrctId = new Integer(retCtrctId);
        }
        
        // if contract is inactive, return undo.
        try {
            quoteProcess.assignNewAgreement(agrmCtrct.getUserId(), agrmntNum, webCtrctId);
        } catch (CtrctInactiveException e) {
            logContext.error(this, e.getMessage());
            return handleCtrctInactive(handler, agrmCtrct.getLocale());
        }
        
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
        
        return handler.getResultBean();
    }
    
    protected ResultBean handleCtrctInactive(ResultHandler handler, Locale locale) {

        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(CustomerMessageKeys.INVLD_CTRCT_ERR_MSG,
                MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        return resultBean;
    }

}
