package com.ibm.dsw.quote.retrieval.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteProcess</code> class is the interface for quote
 * retrieval service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 24, 2007
 */
public interface RetrieveQuoteProcess {
    /**
     * Retrieve the sales quote by SAP quote number, fulfillment source, user id
     * and user internal/external flag.
     * 
     * @param sapQuoteNum
     *            SAP quote number
     * @param sapQuoteIDoc
     *            SAP quote IDoc number
     * @param webQuoteNum
     *            Web quote number
     * @param fulfillment
     *            fulfillment source :
     *            <code>RetrieveQuoteConstant.FULFILLMENT_DIRECT</code>|
     *            <code>RetrieveQuoteConstant.FULFILLMENT_CHANNEL</code>
     * @param docType
     *            document type :
     *            <code>RetrieveQuoteConstant.DOCTYPE_QUOTE</code>|
     *            <code>RetrieveQuoteConstant.DOCTYPE_ORDER</code>
     * @param userID
     *            user id
     * @param external
     *            if the user is an external IBM id
     * @return the quote container object
     * @throws QuoteException
     *             if errors encountered
     */

    public Quote retrieveSalesQuote(String sapQuoteNum, String saoQuoteIDoc, String webQuoteNum, String fulfillment,
            String docType, String userID, boolean external) throws QuoteException;
}
