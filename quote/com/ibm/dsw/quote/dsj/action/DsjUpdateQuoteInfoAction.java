package com.ibm.dsw.quote.dsj.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DsjUpdateQuoteInfoAction</code> class is to update the dsj quote info
 * info.
 * 
 * @author: <a href="bjlbo@cn.ibm.com">Bourne </a>
 * 
 * Creation date: May 15, 2007
 */
public class DsjUpdateQuoteInfoAction extends DsjBaseAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	DsjBaseContract dsjUpdateQuoteInfoContract = (DsjBaseContract) contract;

	    
	     try {
	         QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
	         
	     } catch (QuoteException qe) {
	         logContext.error(this, LogThrowableUtil.getStackTraceContent(qe));
	        return handler.getResultBean();
	     }

	     handler.setState(StateKeys.STATE_REDIRECT_ACTION);
         handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
         handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
         return handler.getResultBean();
    }

}
