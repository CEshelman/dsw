package com.ibm.dsw.quote.ps.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>TreeControllerView.java</code>
 * 
 * @author: jinfahua@cn.ibm.com
 * 
 * Created on: Mar 7, 2007
 */
public class TreeControllerView extends BaseViewBean {
    private String htmlSource = "";

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.viewbean.BaseViewBean#collectResults(com.ibm.ead4j.jade.util.Parameters)
     */
    public void collectResults(Parameters arg0) throws ViewBeanException {
        super.collectResults(arg0);
        this.htmlSource = arg0.getParameterAsString(PartSearchParamKeys.PARAM_HTML_SOURCE_CODE);
    }
    /**
     * @return Returns the htmlSource.
     */
    public String getHtmlSource() {
        return htmlSource;
    }
}
