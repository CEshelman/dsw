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
 * The <code>DisplayFindQuoteByCustomerContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class DisplayFindQuoteByCustomerContract extends FindQuoteContract {

    String siteNum;

    String agreementNum;

    String customerName;

    String nameComparison;

    String country;

    String markCountryDefault;

    String findType;

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
        this.setSiteNum(parameters.getParameterAsString(FindQuoteParamKeys.SITE_NUM));
        this.setAgreementNum(parameters.getParameterAsString(FindQuoteParamKeys.AGREEMENT_NUM));
        this.setCustomerName(parameters.getParameterAsString(FindQuoteParamKeys.CUSTOMER_NAME));
        this.setCountry(parameters.getParameterAsString(FindQuoteParamKeys.COUNTRY));
        this.setMarkCountryDefault(parameters.getParameterAsString(FindQuoteParamKeys.MARK_COUNTRY_DEFAULT));
        this
                .setFindType(parameters.getParameter(FindQuoteParamKeys.FIND_BY_AGREEMENT_NUM_FLAG) != null ? FindQuoteParamKeys.FIND_TYPE_SITE_NUMBER
                        : "" + parameters.getParameter(FindQuoteParamKeys.FIND_BY_CUST_NAME_FLAG) != null ? FindQuoteParamKeys.FIND_TYPE_CUSTOMER_NAME
                                : "");
        this.setNameComparison(parameters.getParameterAsString(FindQuoteParamKeys.NAME_COMPARISON));
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        this.setCountry(QuoteCookie.getCountry(sqoCookie));
    }

    /**
     * @return Returns the agreementNum.
     */
    public String getAgreementNum() {
        return notNullString(agreementNum);
    }

    /**
     * @param agreementNum
     *            The agreementNum to set.
     */
    public void setAgreementNum(String agreementNum) {
        this.agreementNum = agreementNum;
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
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return notNullString(customerName);
    }

    /**
     * @param customerName
     *            The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
     * @return Returns the siteNum.
     */
    public String getSiteNum() {
        return notNullString(siteNum);
    }

    /**
     * @param siteNum
     *            The siteNum to set.
     */
    public void setSiteNum(String siteNum) {
        this.siteNum = siteNum;
    }

    /**
     * @return Returns the markCountryDefault.
     */
    public String getMarkCountryDefault() {
        return markCountryDefault;
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
