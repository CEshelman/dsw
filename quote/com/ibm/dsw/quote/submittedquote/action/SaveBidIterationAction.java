package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.contract.SaveBidIterationContract;
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
public class SaveBidIterationAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        SaveBidIterationContract saveBidContract = (SaveBidIterationContract)contract;
        String userId = saveBidContract.getUserId();
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        try {
            logger.debug(this, "before save bid iteration.");
            process.saveDraftQuote(userId, false);
        } catch (SPException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        
        String redirectURL = HtmlUtil.addURLParam(new StringBuffer(saveBidContract.getRedirectURL()), ParamKeys.PARAM_DRAFTQT_SAVE_SUCCUSS, "1").toString();
        
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        
        return handler.getResultBean();
    }

}
