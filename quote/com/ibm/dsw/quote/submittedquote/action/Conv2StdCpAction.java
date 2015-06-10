package com.ibm.dsw.quote.submittedquote.action;

import org.apache.commons.lang.BooleanUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.contract.Conv2StdCpContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="zgsun@cn.ibm.com">Owen Sun </a> <br/>
 * 
 * Creation date: Apr 5, 2007
 */

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Conv2StdCpAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        Conv2StdCpContract conv2StdCpContract = (Conv2StdCpContract)contract;
        String userId = conv2StdCpContract.getUserId();
        String webQuoteNum = conv2StdCpContract.getQuoteNum();
        
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        
        logger.debug(this, "before convert bid iteration/date extension to standard copy, userId/quoteNum: " + userId + "/" + webQuoteNum);
        process.conv2StdCopy(userId, webQuoteNum);
        
        logger.debug(this, "convert bid iteration/date extension to standard copy successfully, redirect to draft customer partner tab.");
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
        
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, BooleanUtils.toBooleanObject(conv2StdCpContract.getForwardFlag()));
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        
        return handler.getResultBean();
    }
}
