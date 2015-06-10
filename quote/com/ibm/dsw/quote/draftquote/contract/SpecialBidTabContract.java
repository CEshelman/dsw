package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidTabContract.java</code> class is to 
 * 
 * @author: lijiatao@cn.ibm.com
 * 
 * Creation date: 2007-4-2
 */
public class SpecialBidTabContract extends DraftQuoteBaseContract {
	/**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters, com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }
}
