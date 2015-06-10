package com.ibm.dsw.quote.newquote.process.jdbc;

import com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcess;
import com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

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
public class UploadSalesQuoteProcessFactory_jdbc extends
        UploadSalesQuoteProcessFactory {

    /**
     * 
     */
    public UploadSalesQuoteProcessFactory_jdbc() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.UploadSalesQuoteProcessFactory#create()
     */
    public UploadSalesQuoteProcess create() throws TopazException {
       return new UploadSalesQuoteProcess_jdbc();
    }

}
