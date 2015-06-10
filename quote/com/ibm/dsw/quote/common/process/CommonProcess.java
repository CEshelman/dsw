package com.ibm.dsw.quote.common.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.PartPriceTranPriceLevel;
import com.ibm.dsw.quote.common.domain.Quote;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CommonProcess</code> class is to wrap interfaces upon some common domains.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-9
 */
public interface CommonProcess {

    /**
     * @param totalPts
     * @param tSVP
     * @return
     * @throws QuoteException
     */
    public PartPriceTranPriceLevel getNextTranPriceLevel(double totalPts, String tSVP) throws QuoteException;

    /**
     * check whether this quote can be converted, PA -> PAE or PAE -> PA
     * 
     * @param quote
     * @return validation result
     */
    public boolean validateQuoteConvert(Quote quote);
    
    /**
     * @param lob
     * @param cntryCode
     * @return
     * @throws QuoteException
     */
    public boolean isSBButtonDisplay(String lob, String cntryCode, String isMigration, List mtrlGrpCodes) throws QuoteException;

}
