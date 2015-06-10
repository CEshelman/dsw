package com.ibm.dsw.quote.findquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcess;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteStatusProcessFactory_jdbc.java</code>
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on: Apr 28, 2007
 */

public class QuoteStatusProcessFactory_jdbc extends QuoteStatusProcessFactory {

    public QuoteStatusProcess create() throws QuoteException {
        return new QuoteStatusProcess_jdbc();
    }

}
