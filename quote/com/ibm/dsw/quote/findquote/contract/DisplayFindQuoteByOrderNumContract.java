package com.ibm.dsw.quote.findquote.contract;

import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindQuoteByOrderNumContract</code> class.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class DisplayFindQuoteByOrderNumContract extends FindQuoteContract {

    String number;

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return notNullString(number);
    }

    /**
     * @param number
     *            The number to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
        this.setNumber(parameters.getParameterAsString(FindQuoteParamKeys.NUMBER));
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
    }

}
