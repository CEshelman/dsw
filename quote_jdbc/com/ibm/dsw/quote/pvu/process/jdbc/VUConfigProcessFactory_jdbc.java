package com.ibm.dsw.quote.pvu.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.pvu.process.VUConfigProcess;
import com.ibm.dsw.quote.pvu.process.VUConfigProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>VUConfigProcessFactory_jdbc</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public class VUConfigProcessFactory_jdbc extends VUConfigProcessFactory{

    /**
     * Default constructor
     */
    public VUConfigProcessFactory_jdbc() {
        super();
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.process.VUConfigProcessFactory#create()
     */
    public VUConfigProcess create() throws QuoteException {
        return new VUConfigProcess_jdbc();
    }

}
