package com.ibm.dsw.quote.findquote.viewbean;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByCountryAlterViewBean</code> class.
 * 
 * @author zyuyang@cn.ibm.com
 * 
 * Created on 2010-05-12
 */
public class DisplayFindByAppvlAttrAlterViewBean extends BaseViewBean {
    private String textSource = "";

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.viewbean.BaseViewBean#collectResults(com.ibm.ead4j.jade.util.Parameters)
     */
    public void collectResults(Parameters arg0) throws ViewBeanException {
        super.collectResults(arg0);
        try {
            StringBuffer buffer = new StringBuffer();

            List sbDistricts = (List) arg0.getParameter(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS);

            assambleXml(buffer, "districts", sbDistricts);
            buffer.append(":");

            this.textSource = buffer.toString();
        } catch (Exception e) {
            LogContextFactory.singleton().getLogContext().error(this, e, "Error when collectResults");
        }
    }

    /**
     * @param buffer
     * @param string
     * @param sbRegions
     */
    private void assambleXml(StringBuffer buffer, String name, List list) {
        Iterator iter = list.iterator();
        buffer.append(name + "=");
        while (iter.hasNext()) {
            buffer.append(iter.next() + ";");
        }

    }

    /**
     * @return Returns the textSource.
     */
    public String getTextSource() {
        return textSource;
    }

}
