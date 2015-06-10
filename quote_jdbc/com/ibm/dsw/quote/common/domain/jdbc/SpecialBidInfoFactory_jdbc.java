package com.ibm.dsw.quote.common.domain.jdbc;

import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfoFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidInfoFactory_jdbc</code> class .
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 20, 2007
 */
public class SpecialBidInfoFactory_jdbc extends SpecialBidInfoFactory {

    /**
     *  
     */

    public SpecialBidInfo createSpecialBidInfo(String webQuoteNum) throws TopazException {
        SpecialBidInfo_jdbc spBidInfo = new SpecialBidInfo_jdbc(webQuoteNum);
        spBidInfo.isNew(true);
        this.putInCache(webQuoteNum, spBidInfo);
        return spBidInfo;
    }

    /**
     *  
     */

    public SpecialBidInfo findByQuoteNum(String webQuoteNum) throws TopazException {
        SpecialBidInfo_jdbc spBidInfo = (SpecialBidInfo_jdbc) this.getFromCache(webQuoteNum);
        if (null == spBidInfo) {
            spBidInfo = new SpecialBidInfo_jdbc(webQuoteNum);
            spBidInfo.hydrate(TopazUtil.getConnection());
        }
        return spBidInfo;
    }
    
    public SpecialBidInfo getSpeclBidInfoHeader(String webQuoteNum) throws TopazException {
        SpecialBidInfo_jdbc spBidInfo = (SpecialBidInfo_jdbc) this.getFromCache(webQuoteNum);
        if (null == spBidInfo) {
            spBidInfo = new SpecialBidInfo_jdbc(webQuoteNum);
            spBidInfo.hydrateHeader(TopazUtil.getConnection());
        }
        return spBidInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext()
                .put(SpecialBidInfoFactory.class, objectId, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        // get instance from cache
        return TransactionContextManager.singleton().getTransactionContext().get(SpecialBidInfoFactory.class, objectId);
    }

}
