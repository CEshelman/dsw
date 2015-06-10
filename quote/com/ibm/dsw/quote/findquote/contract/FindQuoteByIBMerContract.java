package com.ibm.dsw.quote.findquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteByIBMerContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class FindQuoteByIBMerContract extends DisplayFindQuoteByIBMerContract {

    public void load(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
    }

}
