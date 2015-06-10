package com.ibm.dsw.quote.partner.process.jdbc;

import java.util.Collections;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.ControlledProductPortfolioFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.partner.process.PartnerProcess_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerProcess_jdbc</code> class is the jdbc implementation of
 * Partner process.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public class PartnerProcess_jdbc extends PartnerProcess_Impl {
    
    public List findCtrldProductPorfolios() throws QuoteException{
		List list = null;
        try {
            this.beginTransaction();
            list = ControlledProductPortfolioFactory.singleton().findAllControlledProductPorfolios();
            Collections.sort(list);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
			rollbackTransaction();
		}
        return list;
    }
}
