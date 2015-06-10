package com.ibm.dsw.quote.common.process;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceTranPriceLevel;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CacheProcess_Impl</code> class is abstract implementation of
 * Common Process.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-9
 */
public abstract class CommonProcess_Impl extends TopazTransactionalProcess implements CommonProcess {

    /**
     *  
     */
    public PartPriceTranPriceLevel getNextTranPriceLevel(double totalPts, String tSVP) throws QuoteException {
        return PartPriceConfigFactory.singleton().findNextPriceLevel(totalPts, tSVP);
    }

    public boolean validateQuoteConvert(Quote quote) {
        if (quote.getQuoteHeader() == null) {
            return false;
        }

        String lob = quote.getQuoteHeader().getLob().getCode();
        LogContextFactory.singleton().getLogContext().info(this,
                "lob:<" + lob + ">, total points:" + quote.getQuoteHeader().getTotalPoints());

        if (CustomerConstants.LOB_PA.equalsIgnoreCase(StringUtils.trimToEmpty(lob))) {
            //PA -> PAE
            QuoteHeader header = quote.getQuoteHeader();
            return header.isPAQuote() && !header.isPAUNQuote()
                    && (header.hasNewCustomer() || header.hasExistingCustomer()) && header.getHasPartsFlag();

        } else if (CustomerConstants.LOB_PAE.equalsIgnoreCase(StringUtils.trimToEmpty(lob))) {
            // PAE -> PA
            return (noCustomerOrCustomerIsNew(quote)) && (quote.getQuoteHeader().getTotalPoints() >= 500);

        } else {
            return false;
        }
    }

    private boolean noCustomerOrCustomerIsNew(Quote quote) {
        //same with part&pricing tab.
        return (quote.getCustomer() != null && StringUtils.isBlank(quote.getQuoteHeader().getSoldToCustNum()))
                || (quote.getCustomer() == null);
    }
    
    public boolean isSBButtonDisplay(String lob, String cntryCode, String isMigration, List mtrlGrpCodes) throws QuoteException {
        return ButtonDisplayRuleFactory.singleton().isSBButtonDisplay(lob, cntryCode, isMigration, mtrlGrpCodes);
    }

}
