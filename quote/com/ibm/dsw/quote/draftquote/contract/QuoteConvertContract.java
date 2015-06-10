package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). 
 * 
 * The <code>QuoteConvertContract</code> class is 
 * 
 * @author cuixg@cn.ibm.com
 *
 * Created on Apr 3, 2007
 */
public class QuoteConvertContract extends QuoteBaseContract {
    private String srcAction;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }

    /**
     * @return Returns the srcAction.
     */
    public String getSrcAction() {
        return srcAction;
    }

    /**
     * @param srcAction
     *            The srcAction to set.
     */
    public void setSrcAction(String srcAction) {
        this.srcAction = srcAction;
    }
}
