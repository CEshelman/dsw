package com.ibm.dsw.quote.relatedbid.process;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidProcess_Impl<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */
 

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.relatedbid.domain.RelatedBidFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

 public abstract class RelatedBidProcess_Impl extends TopazTransactionalProcess implements RelatedBidProcess {

    public RelatedBidProcess_Impl() {
        super();
    }

    
    public List searchRelatedbyNum(String quoteNumber) throws QuoteException {
    	List resultList = null;
        try {
            this.beginTransaction();

            resultList = RelatedBidFactory.singleton().findByNum(quoteNumber);

            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        
        return resultList;
    }
}