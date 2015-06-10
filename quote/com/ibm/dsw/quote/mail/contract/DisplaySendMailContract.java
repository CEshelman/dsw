package com.ibm.dsw.quote.mail.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). 
 * 
 * The <code>DisplaySendMailContract</code> class is 
 * 
 * @author cuixg@cn.ibm.com
 *
 * Created on Apr 12, 2007
 */
public class DisplaySendMailContract extends QuoteBaseContract {
    private String srcAction;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        if (StringUtils.isBlank(srcAction)){
            srcAction = DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB;
        }
    }

    /**
     * @return Returns the srcAction.
     */
    public String getSrcAction() {
        return srcAction;
    }
    /**
     * @param srcAction The srcAction to set.
     */
    public void setSrcAction(String srcAction) {
        this.srcAction = srcAction;
    }
}
