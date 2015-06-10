package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SalesInfoTabContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 4, 2007
 */

public class SalesInfoTabContract extends DraftQuoteBaseContract {
    
    String defaultBusinessOrg = null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        defaultBusinessOrg = QuoteCookie.getBusinessOrg(sqoCookie);
    }
    /**
     * @return Returns the deafultBusinessOrg.
     */
    public String getDefaultBusinessOrg() {
        return defaultBusinessOrg;
    }
}
