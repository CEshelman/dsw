/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: May 31, 2013
 */
package com.ibm.dsw.quote.common.util.validator;

import java.util.Map;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.common.domain.EquityCurve;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.ead4j.opal.log.LogContext;

/**
 * DOC EC validation. Refactored from com.ibm.dsw.quote.common.util.SalesQuoteValidationRule
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ECValidator implements QuoteLineItemValidator {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.util.validator.QuoteLineItemValidator#validate(com.ibm.dsw.quote.common.domain.Quote, com.ibm.dsw.quote.common.domain.QuoteLineItem, java.util.Map, com.ibm.dsw.quote.base.domain.QuoteUserSession, javax.servlet.http.Cookie, com.ibm.ead4j.opal.log.LogContext)
     */
    @Override
    public void validate(Quote quote, QuoteLineItem item, Map validateResult, QuoteUserSession user, Cookie cookie,
            LogContext logContext) {
        EquityCurve ec = item.getEquityCurve();

        // non EC parts then continue
        if (ec == null || !ec.isEquityCurveFlag()) {
            return;
        }
        // for special bids if no discount is applied to EC parts then allow quote submission
        if (item.getLineDiscPct() != 0) {
            validateResult.put(QuoteCapabilityProcess.EC_RECALCULATE_NEEDED, Boolean.TRUE);
            return;
        }

    }

}
