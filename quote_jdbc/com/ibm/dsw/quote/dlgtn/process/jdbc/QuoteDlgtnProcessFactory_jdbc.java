package com.ibm.dsw.quote.dlgtn.process.jdbc;

import com.ibm.dsw.quote.dlgtn.process.QuoteDlgtnProcess;
import com.ibm.dsw.quote.dlgtn.process.QuoteDlgtnProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteDlgtnProcessFactory_jdbc</code> class is the implementation
 * for interface QuoteDlgtnProcessFactory
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 13, 2007
 */

public class QuoteDlgtnProcessFactory_jdbc extends QuoteDlgtnProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.QuoteDlgtnProcessFactory#create()
     */
    public QuoteDlgtnProcess create() throws TopazException {

        return new QuoteDlgtnProcess_jdbc();

    }

}
