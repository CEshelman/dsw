package com.ibm.dsw.quote.findquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.QuoteMailLinkContract;
import com.ibm.dsw.quote.home.action.QuoteRightColumnBaseAction;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author wangshp@cn.ibm.com
 */
public class QuoteMailLinkDispatchAction extends QuoteRightColumnBaseAction {
	transient LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	QuoteMailLinkContract quoteMailLinkContract = (QuoteMailLinkContract) contract;
    	
    	String sWebQuoteNum = "";
    	QuoteRightColumn quoteRightColumn = (QuoteRightColumn)handler.getParameters().getParameter(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN);
    	if(quoteRightColumn != null) {
    		sWebQuoteNum = quoteRightColumn.getSWebQuoteNum();
    	}
    	
    	if(StringUtils.isBlank(sWebQuoteNum) || sWebQuoteNum.equals(quoteMailLinkContract.getQuoteNum())) {
    		logContext.debug(this, "There isn't currently a quote in the quote worksheet, redirect to check quote lock action(CHECK_QUOTE_LOCK)");
    		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
    	} else {
    		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
    	}
    	
    	handler.addObject(ParamKeys.PARAM_REDIRECT_URL, this.generateRedirectURL(quoteMailLinkContract.getQuoteNum()));
    	
        return handler.getResultBean();
    }

    protected String getState(ProcessContract contract) {
        return DraftQuoteStateKeys.STATE_DISPLAY_OVERWRITTEN_QUOTE;
    }
    
    private String generateRedirectURL(String webQuoteNum) {
    	return "quote.wss?jadeAction=CHECK_QUOTE_LOCK&redirectURL=quote.wss?jadeAction=SELECT_DRAFT_SALES_QUOTE"
    		+ "&forwardFlag=true&buttonName=2&openAsNew=false&webQuoteNum=" + webQuoteNum;
    }
}
