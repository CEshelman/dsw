package com.ibm.dsw.quote.common.util.spbid;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteAccessFactory;
import com.ibm.dsw.quote.common.util.spbid.date.SalesContinuousCoverageChecker;
import com.ibm.dsw.quote.common.util.spbid.date.TrmlsssrContinuousCoverageChecker;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SalesQuoteSpecialBidRule<code> class.
 *    
 * @author: liuxinlx@cn.ibm.com
 * 
 * Creation date: Feb 25, 2008
 */

class SalesQuoteSpecialBidRule extends SpecialBidRule{

    SalesQuoteSpecialBidRule(Quote quote) {
        super(quote);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.util.validation.ValidationRule#checSpecialBidForMaintConverage()
     */
    public boolean checkSpecialBidForMaintConverage() {
        SalesContinuousCoverageChecker checker = new SalesContinuousCoverageChecker(quote);
        TrmlsssrContinuousCoverageChecker trmlsssrChecker = new TrmlsssrContinuousCoverageChecker(quote);
        return checker.checkSpecialBid() || trmlsssrChecker.checkSpecialBid();
    }
    
    protected boolean rnwlQuoteStatusRequiresSpecialBid(String userId){
    	if(!quote.getQuoteHeader().isSpBiddableRQ()){
    		return false;
    	}

		String rnwlQuoteNum = quote.getQuoteHeader().getRenwlQuoteNum();

    	if(StringUtils.isBlank(rnwlQuoteNum)){
    		return false;
    	}
    	
    	try {
			QuoteAccess access = QuoteAccessFactory.singleton().getRenwlQuoteAccess(rnwlQuoteNum, userId, 1, 1,
                    QuoteConstants.RNWL_QUOTE_DURATION, false);

			if (access == null) {
				return false;
			}
			
			if(access.isCanCreateRQSpeclBid() && !access.isCanEditRQ()){
				return true;
			}
			
		} catch (TopazException te) {
			logContext.error(this, te);
			logContext.error(this, "retrieve renewal quote status from quote["
					  + quote.getQuoteHeader().getWebQuoteNum() + "] failed, renewal quote num["
					  + quote.getQuoteHeader().getRenwlQuoteNum() + "]");

		}
    	
    	return false;
    }
    
    protected boolean skip(){
        return false;
    }
}
