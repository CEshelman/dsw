package com.ibm.dsw.quote.common.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.SpecialBidRselAuthRuleProcess;
import com.ibm.dsw.quote.common.process.SpecialBidRselAuthRuleProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidRselAuthRuleProcessFactory_jdbc<code> class.
 *    
 * @author: changwei@cn.ibm.com
 * 
 * Creation date: Apr 21, 2009
 */

public class SpecialBidRselAuthRuleProcessFactory_jdbc extends SpecialBidRselAuthRuleProcessFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.process.WebXmlCnstntProcessFactory#create()
     */
    public SpecialBidRselAuthRuleProcess create() throws QuoteException {
        return new SpecialBidRselAuthRuleProcess_jdbc();
    }

}
