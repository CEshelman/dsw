package com.ibm.dsw.quote.promotion.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcess;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcessFactory;
/**
 * 
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuotePromotionProcessFactory_jdbc</code>
 * 
 * @author zyuyang@cn.ibm.com
 * 
 * Created on 2010-10-20
 */
public class QuotePromotionProcessFactory_jdbc extends QuotePromotionProcessFactory{
	public QuotePromotionProcess create() throws QuoteException {
        return new QuotePromotionProcess_jdbc();
    }
	
	
}
