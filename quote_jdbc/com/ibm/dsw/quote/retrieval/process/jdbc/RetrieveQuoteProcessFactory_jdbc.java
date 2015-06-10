package com.ibm.dsw.quote.retrieval.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.retrieval.process.RetrieveQuoteProcess;
import com.ibm.dsw.quote.retrieval.process.RetrieveQuoteProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteProcessFactory_jdbc.java</code>
 * 
 * @author: tom_boulet@us.ibm.com
 * 
 * Created on: 2007-05-08
 */

public class RetrieveQuoteProcessFactory_jdbc extends RetrieveQuoteProcessFactory {

    public RetrieveQuoteProcess create() throws QuoteException {
        return new RetrieveQuoteProcess_jdbc();
    }

}
