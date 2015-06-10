package com.ibm.dsw.quote.dlgtn.process;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteDlgtnProcess</code> class is business interface for a single
 * quote delegation
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 13, 2007
 */

public interface QuoteDlgtnProcess {
    /**
     * add a delegate for a specific quote
     * 
     * @param webQuoteNum
     *            the web quote number
     * @param creatorId
     *            the quote creator
     * @param dlgUserId
     *            delegate user id
     * 
     * @throws QuoteException
     */
    public void addQuoteDelegation(String webQuoteNum, String creatorId, String dlgUserId) throws QuoteException;
    
    public void addQuoteDelegation(String webQuoteNum, String creatorId, String dlgUserId, String cntryCode,
            String notesId, String fullName, String firstName, String lastName, String intlPhoneNumFull,
            String intlFaxNumFull) throws QuoteException;

    /**
     * remove a delegate for a specific quote
     * 
     * @param webQuoteNum
     *            the web quote number
     * @param creatorId
     *            the quote creator id
     * @param dlgUserId
     *            delegate user id
     * 
     * @throws QuoteException
     */
    public void removeQuoteDelegation(String webQuoteNum, String creatorId, String dlgUserId) throws QuoteException;
}
