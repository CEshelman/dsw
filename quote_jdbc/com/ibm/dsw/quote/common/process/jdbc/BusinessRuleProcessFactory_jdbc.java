package com.ibm.dsw.quote.common.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.BusinessRuleProcess;
import com.ibm.dsw.quote.common.process.BusinessRuleProcessFactory;

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

public class BusinessRuleProcessFactory_jdbc extends BusinessRuleProcessFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.process.WebXmlCnstntProcessFactory#create()
     */
    public BusinessRuleProcess create() throws QuoteException {
        return new BusinessRuleProcess_jdbc();
    }

}
