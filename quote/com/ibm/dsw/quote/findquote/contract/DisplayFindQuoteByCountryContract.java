package com.ibm.dsw.quote.findquote.contract;

import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindQuoteByCountryContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class DisplayFindQuoteByCountryContract extends FindQuoteContract {

    String country;

    String state;
    
    String subRegion;

    String markCountryRegionDefault;

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
        this.setCountry(parameters.getParameterAsString(FindQuoteParamKeys.COUNTRY));
        this.setState(parameters.getParameterAsString(FindQuoteParamKeys.STATE));
        this.setSubRegion(parameters.getParameterAsString(FindQuoteParamKeys.SUB_REGION));
        this.setMarkCountryRegionDefault(parameters
                .getParameterAsString(FindQuoteParamKeys.MARK_COUNTRY_REGION_DEFAULT));
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        this.setCountry(QuoteCookie.getCountry(sqoCookie));
        this.setState(QuoteCookie.getSubmittedCustState(sqoCookie));
        this.setSubRegion(QuoteCookie.getSubRegion(sqoCookie));
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return notNullString(country);
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return Returns the markCountryRegionDefault.
     */
    public String getMarkCountryRegionDefault() {
        return markCountryRegionDefault;
    }

    /**
     * @param markCountryRegionDefault
     *            The markCountryRegionDefault to set.
     */
    public void setMarkCountryRegionDefault(String markCountryRegionDefault) {
        this.markCountryRegionDefault = markCountryRegionDefault;
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
        return notNullString(state);
    }

    /**
     * @param state
     *            The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    
    /**
     * @return Returns the subRegion.
     */
    public String getSubRegion() {
        return notNullString(subRegion);
    }
    /**
     * @param subRegion The subRegion to set.
     */
    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }
}
