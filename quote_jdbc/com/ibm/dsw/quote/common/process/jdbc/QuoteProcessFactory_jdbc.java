package com.ibm.dsw.quote.common.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:doris_yuen@us.ibm.com">Doris Yuen</a><br/>
 *
 */
public class QuoteProcessFactory_jdbc extends QuoteProcessFactory {

    /**
     * Creates a new JDBC create <code>QuoteProcess</code>
     * @return the QuoteProcess_jdbc
     * @throws TopazException
     * @see com.ibm.dsw.quote.common.process.QuoteProcessFactory#create()
     */
    public QuoteProcess create() throws QuoteException {
        return new QuoteProcess_jdbc();
    }
}
