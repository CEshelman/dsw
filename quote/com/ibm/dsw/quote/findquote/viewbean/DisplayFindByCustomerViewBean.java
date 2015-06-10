package com.ibm.dsw.quote.findquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByCustomerContract;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByCustomerViewBean</code> class.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindByCustomerViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the agreementNum.
     */
    public String getAgreementNum() {
        return ((DisplayFindQuoteByCustomerContract) findContract).getAgreementNum();
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return ((DisplayFindQuoteByCustomerContract) findContract).getCountry();
    }

    /**
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return ((DisplayFindQuoteByCustomerContract) findContract).getCustomerName();
    }

    /**
     * @return Returns the nameComparison.
     */
    public String getNameComparison() {
        return ((DisplayFindQuoteByCustomerContract) findContract).getNameComparison();
    }

    public String getNameComparisonName() {
        String nameComparison = ((DisplayFindQuoteByCustomerContract) findContract).getNameComparison();
        if (nameComparison.equalsIgnoreCase("0"))
            return getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteMessageKeys.START_OF_NAME);
        else if (nameComparison.equalsIgnoreCase("1"))
            return getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteMessageKeys.WITHIN_NAME);
        else if (nameComparison.equalsIgnoreCase("2"))
            return getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteMessageKeys.EXACT_MATCH);
        else
            return "";
    }

    /**
     * @return Returns the siteNum.
     */
    public String getSiteNum() {
        return ((DisplayFindQuoteByCustomerContract) findContract).getSiteNum();
    }

    public String getFindType() {
        return ((DisplayFindQuoteByCustomerContract) findContract).getFindType();
    }

    public List getCountryList() {
        if (countryListObj == null || countryListObj.size() == 0)
            return new ArrayList();

        List countryList = new ArrayList();
        Iterator iter = countryListObj.iterator();
        String defaultCountry = ((DisplayFindQuoteByCustomerContract) findContract).getCountry();

        countryList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteMessageKeys.ALL_COUNTRIES), "", ((defaultCountry == null) || (defaultCountry
                .equalsIgnoreCase(""))) ? true : false));
        while (iter.hasNext()) {
            CodeDescObj country = (CodeDescObj) iter.next();
            SelectOptionImpl countrySO = new SelectOptionImpl(country.getCodeDesc(), country.getCode(),
                    (defaultCountry == null) ? false : defaultCountry.equalsIgnoreCase(country.getCode()));
            countryList.add(countrySO);
        }
        return countryList;
    }

    public Collection generateNameComparisonOptions() {

        List nameComparisonOptionList = new ArrayList();

        nameComparisonOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteMessageKeys.START_OF_NAME), "0", (this.getNameComparison() == null)
                || (this.getNameComparison().equalsIgnoreCase("0"))));
        nameComparisonOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteMessageKeys.WITHIN_NAME), "1", (this.getNameComparison() != null)
                && (this.getNameComparison().equalsIgnoreCase("1"))));
        nameComparisonOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteMessageKeys.EXACT_MATCH), "2", (this.getNameComparison() != null)
                && (this.getNameComparison().equalsIgnoreCase("2"))));

        return nameComparisonOptionList;

    }

    public String getCustomerPageURL() {
        String customerURL = "";

        customerURL += "&" + FindQuoteParamKeys.SITE_NUM + "=" + this.getSiteNum();
        customerURL += "&" + FindQuoteParamKeys.AGREEMENT_NUM + "=" + this.getAgreementNum();
        customerURL += "&" + FindQuoteParamKeys.CUSTOMER_NAME + "=" + this.getCustomerName();
        customerURL += "&" + FindQuoteParamKeys.NAME_COMPARISON + "=" + this.getNameComparison();
        customerURL += "&" + FindQuoteParamKeys.COUNTRY + "=" + this.getCountry();

        return customerURL;

    }

    public String getPrePageURL() {
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_CUSTOMER");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getCustomerPageURL();

        return prePageURL;
    }

    public String getNextPageURL() {
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_CUSTOMER");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getCustomerPageURL();

        return nextPageURL;
    }

    public String getChangeCriteriaURL() {
        String criteriaURL = HtmlUtil.getURLForAction("DISPLAY_FIND_QUOTE_BY_CUSTOMER_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getCustomerPageURL();
        return criteriaURL;
    }
}
