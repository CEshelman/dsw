package com.ibm.dsw.quote.dsj.contract;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class DsjLaunchContract extends DsjBaseContract {

    protected transient Cookie sqoCookie = null;
    
    String defaultLOB = null;

    String defaultCountry = null;
    
    String defaultAcquisition = null;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        //load default value from cookie
        sqoCookie = getCookie(parameters);
        
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
    


    /**
     * @return Returns the sqoCookie.
     */
    public Cookie getSqoCookie() {
        return sqoCookie;
    }


    protected Cookie getCookie(Parameters parameters) {
        return (Cookie) parameters.getParameter(ParamKeys.PARAM_QUOTE_COOKIE);
    }

}
