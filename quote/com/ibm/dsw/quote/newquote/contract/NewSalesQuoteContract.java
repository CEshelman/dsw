package com.ibm.dsw.quote.newquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewSalesQuoteContract<code> class. 
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */
public class NewSalesQuoteContract extends QuoteBaseCookieContract{
  
    String lob = null;
    String country = null;
    String acquisition = null;
    String markAsDefault = null;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getLob() {
        return lob;
    }
    public void setLob(String lob) {
        this.lob = lob;
    }
    public String getAcquisition() {
        return acquisition;
    }
    public void setAcquisition(String acquisition) {
        this.acquisition = acquisition;
    }
    public String getMarkAsDefault() {
        return markAsDefault;
    }
    public void setMarkAsDefault(String markAsDefault) {
        this.markAsDefault = markAsDefault;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append("country = ").append(getCountry());
        buffer.append("lob = ").append(getLob());
        buffer.append("markAsDefault = ").append(getMarkAsDefault());
        return buffer.toString();
    }
}
