package com.ibm.dsw.quote.findquote.viewbean;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
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
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-5-22
 */
public class DisplayFindByCountryAlterViewBean extends BaseViewBean {
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
            
            List countrys = (List) arg0.getParameter(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC);
            List states = (List) arg0.getParameter(FindQuoteParamKeys.PARAM_STATE_LIST);
            
            String requestFrom = (String)arg0.getParameter("requestFrom");
            
            if (requestFrom != null) {
                if (requestFrom.equalsIgnoreCase("subRegion")) {
                    buffer.append("subRegion");
                } else if (requestFrom.equalsIgnoreCase("country")) {
                    buffer.append("country");
                }
            }
            buffer.append("::");
            assambleXml(buffer, "country", countrys);
            buffer.append("::");
            assambleXml(buffer, "states", states);
            this.textSource = buffer.toString();
            
        } catch (Exception e) {
            LogContextFactory.singleton().getLogContext().error(this, e, "Error when collectResults");
        }
    }

    /**
     * @param buffer
     * @param string
     * @param list
     */
    private void assambleXml(StringBuffer buffer, String name, List list) {
        buffer.append(name + "==");
        if(list == null || list.size() <= 0){
            return;
        }else{
	        Iterator iter = list.iterator();
	        while (iter.hasNext()) {
	            CodeDescObj o = (CodeDescObj) iter.next();
	            buffer.append(o.getCode());
	            buffer.append("__");
	            buffer.append(o.getCodeDesc());
	            buffer.append(";;");
	        }
        }
    }

    /**
     * @return Returns the textSource.
     */
    public String getTextSource() {
        return textSource;
    }

}
