package com.ibm.dsw.quote.ps.process.jdbc;

import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>oooooo.java</code> class is to oooooo
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class PartSearchProcessFactory_jdbc extends PartSearchProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.demo.process.SearchProcessFactory#create()
     */
    public PartSearchProcess create() throws TopazException {
        return new PartSearchProcess_jdbc();
    }

}
