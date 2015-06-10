package com.ibm.dsw.quote.pvu.process;


import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>VUConfigProcess_Impl</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public abstract class VUConfigProcess_Impl extends TopazTransactionalProcess implements VUConfigProcess{

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.process.VUConfigProcess#findByConfigNum(java.lang.Integer, java.lang.Integer)
     */
    public abstract SearchResultList findVUConfigByConfigNum(String configNum, String noDscrFlag) throws QuoteException;

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.process.VUConfigProcess#createQuoteLineItemConfig(java.lang.String, int)
     */
    public abstract void updateQuoteLineItemConfig(List vuConfigList, String lineItemSeqNum, String creatorId, String configNum) throws QuoteException;
    
    
}
