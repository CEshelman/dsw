package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DispatchSubmittedQuoteTabsAction<code> class.
 * 
 * @author zhangln
 * 
 * Creation date: 2009-2-11
 *
 */
public class DispatchSubmittedQuoteTabsAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;
        String webQuoteNum = baseContract.getQuoteNum();
        String jadeAction = SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB;
        
        QuoteUserSession quoteUserSession = baseContract.getQuoteUserSession();
        String up2ReportingUserIds = quoteUserSession == null ? "" : quoteUserSession.getUp2ReportingUserIds();
        String userId = baseContract.getUserId();
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        QuoteUserAccess quoteUserAccess = qProcess.getQuoteUserAccess(webQuoteNum, userId, up2ReportingUserIds);
        
        if (quoteUserAccess.isCanViewExecSummry(baseContract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO)) && QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equals(quoteUserSession.getAudienceCode())) {
            jadeAction = SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_EXEC_SUMMARY_TAB;
        }else{
            jadeAction = SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB;
        }
        
        String redirectURL = collectRederectURL(jadeAction,webQuoteNum);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL,redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }
    
    protected String collectRederectURL(String jadeAction, String webQuoteNum){
        StringBuffer redirectURL = new StringBuffer(HtmlUtil.getURLForAction(jadeAction));
        HtmlUtil.addURLParam(redirectURL, ParamKeys.PARAM_QUOTE_NUM, webQuoteNum);
        return redirectURL.toString();
    }

}
