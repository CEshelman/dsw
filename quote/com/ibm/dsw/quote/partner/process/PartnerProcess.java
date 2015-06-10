package com.ibm.dsw.quote.partner.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerProcess</code> class is the process interface for Partner
 * search.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public interface PartnerProcess {
    public SearchResultList findResellers(String lobCode, String custCnt, String custName, String county, String state,
            int tierType, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws QuoteException;

    public SearchResultList findResellers(String lobCode, String custCnt, String custNum, int tierType,
            int pageIndex, String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws QuoteException;

    public SearchResultList findDistributors(String lobCode, String custCnt, String custNum, int pageIndex,
            String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws QuoteException;

    public SearchResultList findDistributors(String lobCode, String custCnt, String custName, String county,
            String state, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws QuoteException;

    public List findValidCountryList(String countryCod, String lobCod) throws QuoteException;
    
    public SearchResultList findResellersByPortfolio(String portfolios, String lob, String custCntry, int pageIndex, int multipleProdFlag)
            throws QuoteException;
    
    public List findCtrldProductPorfolios() throws QuoteException;
}
