package com.ibm.dsw.quote.export.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcess;
import com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ExportQuoteProcessFactory_jdbc</code> class is
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 11, 2007
 */
public class ExportQuoteProcessFactory_jdbc extends ExportQuoteProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory#create()
     */
    public ExportQuoteProcess create() throws QuoteException {
        return new ExportQuoteProcess_jdbc();
    }

}
