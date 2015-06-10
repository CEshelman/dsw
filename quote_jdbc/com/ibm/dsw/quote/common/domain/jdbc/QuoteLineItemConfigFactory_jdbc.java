package com.ibm.dsw.quote.common.domain.jdbc;

import com.ibm.dsw.quote.common.domain.QuoteLineItemConfig;
import com.ibm.dsw.quote.common.domain.QuoteLineItemConfigFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteLineItemConfigFactory_jdbc</code> class is jdbc
 * implementation of QuoteLineItemConfigFactory.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class QuoteLineItemConfigFactory_jdbc extends QuoteLineItemConfigFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemConfigFactory#create(java.lang.String,
     *      int)
     */
    public QuoteLineItemConfig create(String webQuoteNum, int seqNum, String userID) throws TopazException {
        QuoteLineItemConfig_jdbc config = new QuoteLineItemConfig_jdbc();
        config.setWebQuoteNum(webQuoteNum);
        config.setQuoteLineItemSecNum(seqNum);
        config.isNew(true);
        config.setUserID(userID);
        return config;
    }
}
