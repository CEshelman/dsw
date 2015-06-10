package com.ibm.dsw.quote.newquote.viewbean;

import java.util.Map;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-16
 */
public class UploadQuoteViewBean extends BaseViewBean {
    
    private transient Map invliadDataMap = null;
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        this.invliadDataMap = (Map) params.getParameter(NewQuoteParamKeys.PARAM_INVILD_DATA);
    }
    
    
    /**
     * @return Returns the invliadDataMap.
     */
    public Map getInvliadDataMap() {
        return invliadDataMap;
    }
}
