package com.ibm.dsw.quote.customer.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.contract.EndUserSearchContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
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
 * This <code>SelectEndUserAction<code> class.
 *    
 * @author: doris_yuen@us.ibm.com
 * 
 * Date: Dec. 15, 2009
 */

public class SelectEndUserAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        EndUserSearchContract euContract = (EndUserSearchContract) contract;
        String creatorId = euContract.getUserId();
        String customerNum = euContract.getSiteNumber();
        boolean returnStatus = false;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        QuoteRightColumn quoteRightColumn = process.getQuoteRightColumnInfo(creatorId);
        if (quoteRightColumn == null) {
            logContext.debug(this, "can't find session quote by creatorId: " + creatorId);
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        } else {
            process.updateQuoteHeaderEndUserInfo(creatorId, euContract.getQuoteNum(), customerNum);
            handler.setState(StateKeys.STATE_REDIRECT_ACTION);
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
        }
        return handler.getResultBean();
    }

}
