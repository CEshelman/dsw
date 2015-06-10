package com.ibm.dsw.quote.newquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidAcquisitionException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCustCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidLOBException;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class validates and create new draft quote from spead sheet.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-14
 */
public interface UploadSalesQuoteProcess  {
    
    /** Store extracted spreadsheet data to DB 
     * @throws NewQuoteInvalidAcquisitionException TODO
     * @throws QuoteException*/
    public void importSpreadSheetQuote(SpreadSheetQuote squote,  String creatorId) throws NewQuoteInvalidCntryCurrencyException, NewQuoteInvalidCustCurrencyException, NewQuoteInvalidLOBException, NewQuoteInvalidCntryException, NewQuoteInvalidAcquisitionException, QuoteException ;
 

}
