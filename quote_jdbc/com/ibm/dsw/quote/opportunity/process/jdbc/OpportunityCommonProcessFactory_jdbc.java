package com.ibm.dsw.quote.opportunity.process.jdbc;

import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcess;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-14
 */
public class OpportunityCommonProcessFactory_jdbc extends
OpportunityCommonProcessFactory {

    /**
     * 
     */
    public OpportunityCommonProcessFactory_jdbc() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.UploadSalesQuoteProcessFactory#create()
     */
    public OpportunityCommonProcess create(){
       return new OpportunityCommonProcess_jdbc();
    }

}
