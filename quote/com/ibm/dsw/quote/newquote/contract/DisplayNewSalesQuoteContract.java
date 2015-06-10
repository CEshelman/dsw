package com.ibm.dsw.quote.newquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This
 * <code>DisplayNewSalesQuoteContract<code> class wraps up the default country & lob saved in user's cookie
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class DisplayNewSalesQuoteContract extends QuoteBaseCookieContract {

    String defaultLOB = null;

    String defaultCountry = null;
    
    String defaultAcquisition = null;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        //load default value from cookie
        defaultCountry = QuoteCookie.getCountry(sqoCookie);
        defaultLOB = QuoteCookie.getLOB(sqoCookie);
        defaultAcquisition = QuoteCookie.getAcquisition(sqoCookie);
    }

    public String getDefaultCountry() {
        return defaultCountry;
    }

    public String getDefaultLOB() {
        return defaultLOB;
    }
    
    public String getDefaultAcquisition() {
        return defaultAcquisition;
    }
}
