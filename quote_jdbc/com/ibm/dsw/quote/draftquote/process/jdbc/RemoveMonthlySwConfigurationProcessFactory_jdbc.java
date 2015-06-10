package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.RemoveMonthlySwConfigurationProcess;
import com.ibm.dsw.quote.draftquote.process.RemoveMonthlySwConfigurationProcessFactory;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>RemoveMonthlySwConfigurationProcessFactory_jdbc</code> class is jdbc subClass of
 * RemoveMonthlySwConfigurationProcessFactory.
 *
 *
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 *
 * Creation date: 2014-1-03
 */
public class RemoveMonthlySwConfigurationProcessFactory_jdbc extends RemoveMonthlySwConfigurationProcessFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidFinalProcessFactory#create()
     */
    public RemoveMonthlySwConfigurationProcess create() throws QuoteException {
        return new RemoveMonthlySwConfigurationProcess_jdbc();
    }

}
