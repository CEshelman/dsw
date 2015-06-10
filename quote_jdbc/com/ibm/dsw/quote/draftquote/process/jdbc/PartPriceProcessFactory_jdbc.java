package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartPriceProcessFactory_jdbc</code> class is jdbc implementation
 * of PartPriceProcess.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-27
 */
public class PartPriceProcessFactory_jdbc extends PartPriceProcessFactory {

    /**
     *  
     */

    public PartPriceProcess create() throws QuoteException {
        return new PartPriceProcess_jdbc();
    }

}
