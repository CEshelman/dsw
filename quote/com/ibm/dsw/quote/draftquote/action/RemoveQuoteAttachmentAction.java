package com.ibm.dsw.quote.draftquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.contract.RemoveQuoteAttachmentContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
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
 * This <code>RemoveQuoteAttachmentAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 23, 2008
 */

public class RemoveQuoteAttachmentAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -8473364355834072626L;

	/* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	Exception exc = null;
    	RemoveQuoteAttachmentContract rmContract = null;
    	try
    	{
    		rmContract= (RemoveQuoteAttachmentContract) contract;
	        
	        QuoteAttachmentProcess process = QuoteAttachmentProcessFactory.singleton().create();
	        process.removeQuoteAttachment(rmContract.getWebQuoteNum(), rmContract.getAttchmtSeqNum());
    	}
    	catch ( Exception e )
    	{
    		this.logContext.error(this, e);
    		exc = e;
    	}
        if ( StringUtils.isEmpty(rmContract.getAttCode()) )
        {
        	if ( exc != null )
        	{
        		throw new QuoteException(exc);
        	}
	        String redirectURL = this.getRedirectURL(contract);
	        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
	        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
	        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        }
        else
        {
        	if ( exc == null )
        	{
        		handler.addObject(ParamKeys.PARAM_AJAX_OPER_STATUS, "1");
        		handler.addObject(ParamKeys.PARAM_AJAX_OPER_MESS, DraftQuoteViewKeys.MSG_ATTCHMT_DEL_SUCC);
        	}
        	else
        	{
        		handler.addObject(ParamKeys.PARAM_AJAX_OPER_STATUS, "0");
        		handler.addObject(ParamKeys.PARAM_AJAX_OPER_MESS, DraftQuoteViewKeys.MSG_ATTCHMT_DEL_FAIL);
        	}
        	handler.setState(StateKeys.STATE_DISPLAY_AJAX_OPER);
        }
        return handler.getResultBean();
    }
    
    protected String getRedirectURL(ProcessContract contract) {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB);
        return redirectURL;
    }

}
