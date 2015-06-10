package com.ibm.dsw.quote.draftquote.util;

import java.util.HashMap;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.rule.BusinessRuleUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>TuscanySWVNRuleHelper<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Apr 27, 2009
 */

public class TuscanySWVNRuleHelper {
    
    public static final String RULE_SET_ID = "TUSCANY_SP_BID_AND_SUBMIT";
    public static final String SPECL_BID_RULE_ID = "validateAllSpecialBidInterface";
    public static final String SUBMIT_RULE_ID = "validateAllPreventQuoteSubmitInterface";
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected Quote quote = null;
    protected BusinessRuleUtil xrule = null;

    public TuscanySWVNRuleHelper(Quote quote) throws QuoteException {
        if (quote == null)
            throw new IllegalStateException();
        this.quote = quote;
        this.xrule = new BusinessRuleUtil(RULE_SET_ID);
    }
    
    public boolean validateSpecialBid(HashMap reasons) throws QuoteException {
        return executeRule(RULE_SET_ID, SPECL_BID_RULE_ID, reasons);
    }
    
    public boolean validateSubmission(HashMap reasons) throws QuoteException {
        //If TBD distributor the reseller must be authorized to sell all the controlled parts 
        //else use xrule to validate the quote submission
        if (this.quote.getReseller()!= null && this.quote.getQuoteHeader().isDistribtrToBeDtrmndFlag()) {
            QuoteCommonUtil quoteCommonUtil = QuoteCommonUtil.create(quote);
            if (quoteCommonUtil.isResellerAuthorizedToAllWithDistrbtr())
                return true ;
            else {
                reasons.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_PORTFOLIO_TBD, Boolean.TRUE);
                return false ;
            }
        }
        else
            return executeRule(RULE_SET_ID, SUBMIT_RULE_ID, reasons);
    }
    
    protected boolean executeRule(String ruleSetId, String ruleId, HashMap reasons) throws QuoteException {

        QuoteCommonUtil quoteCommonUtil = QuoteCommonUtil.create(quote);
        Object result = xrule.executeRule(ruleSetId,
                ruleId, new Object[] { "quote", quote, "quoteCommonUtil", quoteCommonUtil, "sbReason", reasons });

        logContext.debug(this, "XRule implementation result is: " + result);
        logContext.debug(this, "Special bid or prevent submission reasons are: " + reasons.toString());

        return (!Boolean.valueOf(result.toString()).booleanValue());
    }

}
