package com.ibm.dsw.quote.common.domain.jdbc;

import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteContactFactory;
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
 * This <code>QuoteContactFactory_jdbc<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */

public class QuoteContactFactory_jdbc extends QuoteContactFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public QuoteContactFactory_jdbc(){
        super();
    }

    public QuoteContact updateQuoteContact(String creatorId) throws TopazException {
        QuoteContact quoteContact = (QuoteContact) this.getFromCache(creatorId);
        if(quoteContact == null){
            QuoteContact_jdbc quoteContact_jdbc = new QuoteContact_jdbc(creatorId);
            quoteContact_jdbc.isNew(true); //persist to DB2
            quoteContact = quoteContact_jdbc;
            this.putInCache(creatorId, quoteContact);
        }
        return quoteContact;
    }
    
    
    public QuoteContact findQuoteContact(String quoteNum, String cntPrtnrFuncCode) throws TopazException {
        QuoteContact quoteContact = (QuoteContact) this.getFromCache(quoteNum);
        if(quoteContact == null){
            QuoteContact_jdbc quoteContact_jdbc = new QuoteContact_jdbc(quoteNum, cntPrtnrFuncCode);
            quoteContact_jdbc.hydrate(TopazUtil.getConnection());
            quoteContact = quoteContact_jdbc;
            this.putInCache(quoteNum, quoteContact);
        }
        return quoteContact;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(QuoteContactFactory.class, objectId, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        // get instance from cache
        return TransactionContextManager.singleton().getTransactionContext().get(QuoteContactFactory.class, objectId);
    }
}
