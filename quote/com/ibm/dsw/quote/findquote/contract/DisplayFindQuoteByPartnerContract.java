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
 * The <code>DisplayFindQuoteByPartnerContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class DisplayFindQuoteByPartnerContract extends FindQuoteContract {

    String partnerSiteNum;

    String partnerTypeForNum;

    String partnerName;

    String partnerTypeForName;

    String nameComparison;

    String country;

    String markCountryDefault;

    String findType;

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
        this.setPartnerSiteNum(parameters.getParameterAsString(FindQuoteParamKeys.PARTNER_SITE_NUM));
        this.setPartnerTypeForNum(parameters.getParameterAsString(FindQuoteParamKeys.PARTNER_TYPE_FOR_NUM));
        this.setPartnerName(parameters.getParameterAsString(FindQuoteParamKeys.PARTNER_NAME));
        this.setPartnerTypeForName(parameters.getParameterAsString(FindQuoteParamKeys.PARTNER_TYPE_FOR_NAME));
        this.setNameComparison(parameters.getParameterAsString(FindQuoteParamKeys.NAME_COMPARISON));
        this.setCountry(parameters.getParameterAsString(FindQuoteParamKeys.COUNTRY));
        this.setMarkCountryDefault(parameters.getParameterAsString(FindQuoteParamKeys.MARK_COUNTRY_DEFAULT));
        this
                .setFindType(parameters.getParameter(FindQuoteParamKeys.FIND_BY_PART_NAME_FLAG) != null ? FindQuoteParamKeys.FIND_TYPE_PARTNER_NAME
                        : "" + parameters.getParameter(FindQuoteParamKeys.FIND_BY_PART_NUM_FLAG) != null ? FindQuoteParamKeys.FIND_TYPE_PARTNER_NUMBER
                                : "");
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        this.setCountry(QuoteCookie.getCountry(sqoCookie));
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
     * @return Returns the markCountryDefault.
     */
    public String getMarkCountryDefault() {
        return markCountryDefault;
    }

    /**
     * @param markCountryDefault
     *            The markCountryDefault to set.
     */
    public void setMarkCountryDefault(String markCountryDefault) {
        this.markCountryDefault = markCountryDefault;
    }

    /**
     * @return Returns the nameComparison.
     */
    public String getNameComparison() {
        return notNullString(nameComparison);
    }

    /**
     * @param nameComparison
     *            The nameComparison to set.
     */
    public void setNameComparison(String nameComparison) {
        this.nameComparison = nameComparison;
    }

    /**
     * @return Returns the partnerName.
     */
    public String getPartnerName() {
        return notNullString(partnerName);
    }

    /**
     * @param partnerName
     *            The partnerName to set.
     */
    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    /**
     * @return Returns the partnerSiteNum.
     */
    public String getPartnerSiteNum() {
        return notNullString(partnerSiteNum);
    }

    /**
     * @param partnerSiteNum
     *            The partnerSiteNum to set.
     */
    public void setPartnerSiteNum(String partnerSiteNum) {
        this.partnerSiteNum = partnerSiteNum;
    }

    /**
     * @return Returns the partnerTypeForName.
     */
    public String getPartnerTypeForName() {
        return notNullString(partnerTypeForName);
    }

    /**
     * @param partnerTypeForName
     *            The partnerTypeForName to set.
     */
    public void setPartnerTypeForName(String partnerTypeForName) {
        this.partnerTypeForName = partnerTypeForName;
    }

    /**
     * @return Returns the partnerTypeForNum.
     */
    public String getPartnerTypeForNum() {
        return notNullString(partnerTypeForNum);
    }

    /**
     * @param partnerTypeForNum
     *            The partnerTypeForNum to set.
     */
    public void setPartnerTypeForNum(String partnerTypeForNum) {
        this.partnerTypeForNum = partnerTypeForNum;
    }

    /**
     * @return Returns the findType.
     */
    public String getFindType() {
        return notNullString(findType);
    }

    /**
     * @param findType
     *            The findType to set.
     */
    public void setFindType(String findType) {
        this.findType = findType;
    }

}
