package com.ibm.dsw.quote.newquote.viewbean;

import com.ibm.dsw.quote.home.viewbean.QuoteRightColumnViewBean;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DisplayUploadQuoteViewBean<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: May 14, 2010
 */

public class DisplayUploadQuoteViewBean extends QuoteRightColumnViewBean {

    private boolean isLocked = false;

    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);
        Boolean temp = (Boolean)param.getParameter(NewQuoteParamKeys.PARAM_IS_LOCKED);
        if (temp != null) {
            isLocked = temp.booleanValue();
        }
    }

    public boolean isLocked() {
        return isLocked;
    }

}
