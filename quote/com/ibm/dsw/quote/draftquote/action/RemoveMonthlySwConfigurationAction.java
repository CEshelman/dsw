package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.contract.RemoveMonthlySwConfigurationContract;
import com.ibm.dsw.quote.draftquote.process.RemoveMonthlySwConfigurationProcess;
import com.ibm.dsw.quote.draftquote.process.RemoveMonthlySwConfigurationProcessFactory;
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
 * This <code>RemoveMonthlySwConfigurationAction<code> class.
 *    
 * @author: jiamengz@cn.ibm.com
 * 
 * Creation date: Jan 03, 2014
 */

public class RemoveMonthlySwConfigurationAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -8473364355834072626L;

	/* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	RemoveMonthlySwConfigurationContract rmContract = null;
    	try
    	{
    		rmContract= (RemoveMonthlySwConfigurationContract) contract;
	        RemoveMonthlySwConfigurationProcess process = RemoveMonthlySwConfigurationProcessFactory.singleton().create();
	        process.removeMonthlySwConfigurations(rmContract.getWebQuoteNum(), rmContract.getConfigurationId());
    	}
    	catch ( Exception e )
    	{
    		this.logContext.error(this, e);
    		throw new QuoteException(e);
    	}
	        String redirectURL = this.getRedirectURL(contract);
	        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
	        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
	        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        
        return handler.getResultBean();
    }
    
    protected String getRedirectURL(ProcessContract contract) {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB);
        return redirectURL;
    }
}
