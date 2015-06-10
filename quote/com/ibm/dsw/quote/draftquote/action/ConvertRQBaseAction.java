package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.ConvertContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ConvertRQBaseAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Feb 11, 2009
 */

public abstract class ConvertRQBaseAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        ConvertContract cnvtContract = (ConvertContract) contract;
        Quote quote = null;
        
        try {
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            quote = process.getDraftQuoteBaseInfo(cnvtContract.getUserId());
            
        } catch (NoDataException e) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }

        if (this.canBeConverted(cnvtContract, quote)) {
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
            this.executeConvertion(cnvtContract);

        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(HtmlUtil.getURLForAction(cnvtContract.getSrcAction()));
            sb.append("&");
            sb.append(ParamKeys.PARAM_TRAN_MSG);
            sb.append("=");
            sb.append(this.getValidationMessage());
            
            String target = sb.toString();
            LogContextFactory.singleton().getLogContext().warning(this, "can't convert. turn to " + target);

            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, target);
        }

        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }
    
    public abstract boolean canBeConverted(ConvertContract cnvtContract, Quote quote) throws QuoteException;
    
    public abstract void executeConvertion(ConvertContract cnvtContract) throws QuoteException;
    
    public abstract String getValidationMessage();

}
