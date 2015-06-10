package com.ibm.dsw.quote.partner.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.partner.process.PartnerProcess;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerProcessFactory_jdbc</code> class is the jdbc
 * implementation of Partner process factory.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public class PartnerProcessFactory_jdbc extends PartnerProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.PartnerProcessFactory#create()
     */
    public PartnerProcess create() throws QuoteException {
        return new PartnerProcess_jdbc();
    }
}
