package com.ibm.dsw.quote.findquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>FindQuoteBySiebelNumContract</code> class.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class FindQuoteBySiebelNumContract extends DisplayFindQuoteBySiebelNumContract {
    public void load(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
    }

}
