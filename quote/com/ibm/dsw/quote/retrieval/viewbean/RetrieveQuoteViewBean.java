package com.ibm.dsw.quote.retrieval.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.retrieval.RetrieveQuoteResultCodes;
import com.ibm.dsw.quote.retrieval.config.RetrieveQuoteConstant;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteViewBean</code> class is the view bean for quote
 * retrieval action.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 25, 2007
 */
public class RetrieveQuoteViewBean extends BaseViewBean {
    private int resultCode = RetrieveQuoteResultCodes.UNKNOWN_ERROR_CODE;

    private Quote quote;

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);

        Integer result = (Integer) params.getParameter(RetrieveQuoteConstant.E_RESULT);
        if (null == result) {
            throw new ViewBeanException("Null result from the view bean");
        } else {
            resultCode = result.intValue();
        }

        if (RetrieveQuoteResultCodes.SUCCESS == resultCode) {
            quote = (Quote) params.getParameter(RetrieveQuoteConstant.E_QUOTE);
            if (null == quote) {
                throw new ViewBeanException("Null quote from the view bean");
            }
        }
    }

    public Quote getQuote() {
        return quote;
    }

    public int getResultCode() {
        return resultCode;
    }
    
}
