package com.ibm.dsw.quote.common.domain.jdbc;

import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.domain.QuoteRightColumnFactory;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteRightColumnFactory_jdbc<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 5, 2007
 */

public class QuoteRightColumnFactory_jdbc extends QuoteRightColumnFactory {

    LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.SessionQuoteRightColumnFactory#getSessionQuoteRightColumnByID(java.lang.String)
     */
    public QuoteRightColumn findQuoteRightColumnByID(String creatorId) throws TopazException {

        QuoteRightColumn quoteRightColumn = (QuoteRightColumn) this.getFromCache(creatorId);
        if (quoteRightColumn == null) {
            QuoteRightColumn_jdbc quoteRightColumn_jdbc = new QuoteRightColumn_jdbc(creatorId);
            quoteRightColumn_jdbc.hydrate(TopazUtil.getConnection());
            quoteRightColumn = quoteRightColumn_jdbc;
            this.putInCache(creatorId, quoteRightColumn_jdbc);
        }
        return quoteRightColumn;
    }

    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(SalesRepFactory.class, objectId, object);
    }

    public Object getFromCache(Object objectId) throws TopazException {
        return TransactionContextManager.singleton().getTransactionContext().get(SalesRepFactory.class, objectId);
    }

}
