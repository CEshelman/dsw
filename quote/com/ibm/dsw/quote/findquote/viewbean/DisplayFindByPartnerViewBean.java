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
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByPartnerContract;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByPartnerViewBean</code> class.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindByPartnerViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the nameComparison.
     */
    public String getNameComparison() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getNameComparison();
    }

    public String getNameComparisonName() {
        String nameComparison = ((DisplayFindQuoteByPartnerContract) findContract).getNameComparison();
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
     * @return Returns the partnerName.
     */
    public String getPartnerName() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getPartnerName();
    }

    /**
     * @return Returns the partnerSiteNum.
     */
    public String getPartnerSiteNum() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getPartnerSiteNum();
    }

    /**
     * @return Returns the partnerTypeForName.
     */
    public String getPartnerTypeForName() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getPartnerTypeForName();
    }

    public String getPartnerTypeForNameName() {

        return ((DisplayFindQuoteByPartnerContract) findContract).getPartnerTypeForName().equalsIgnoreCase("0") ? getI18NString(
                I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.RESELLER)
                : getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.DISTRIBUTOR_PAYER);
    }

    /**
     * @return Returns the partnerTypeForNum.
     */
    public String getPartnerTypeForNum() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getPartnerTypeForNum();
    }

    public String getPartnerTypeForNumName() {

        return ((DisplayFindQuoteByPartnerContract) findContract).getPartnerTypeForNum().equalsIgnoreCase("0") ? getI18NString(
                I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.RESELLER)
                : getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.DISTRIBUTOR_PAYER);
    }

    public String getCountry() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getCountry();
    }

    public String getFindByPartNumFlag() {
        return ((DisplayFindQuoteByPartnerContract) findContract).getFindType();
    }

    public List getCountryList() {
        if (countryListObj == null || countryListObj.size() == 0)
            return new ArrayList();

        List countryList = new ArrayList();
        Iterator iter = countryListObj.iterator();
        String defaultCountry = ((DisplayFindQuoteByPartnerContract) findContract).getCountry();

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

    public String getPartnerPageURL() {
        String partnerURL = "";

        partnerURL += "&" + FindQuoteParamKeys.PARTNER_SITE_NUM + "=" + this.getPartnerSiteNum();
        partnerURL += "&" + FindQuoteParamKeys.PARTNER_TYPE_FOR_NUM + "=" + this.getPartnerTypeForNum();
        partnerURL += "&" + FindQuoteParamKeys.PARTNER_NAME + "=" + this.getPartnerName();
        partnerURL += "&" + FindQuoteParamKeys.NAME_COMPARISON + "=" + this.getNameComparison();
        partnerURL += "&" + FindQuoteParamKeys.COUNTRY + "=" + this.getCountry();
        partnerURL += "&" + FindQuoteParamKeys.PARTNER_TYPE_FOR_NAME + "=" + this.getPartnerTypeForName();

        return partnerURL;

    }

    public String getPrePageURL() {
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_PARTNER");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getPartnerPageURL();

        return prePageURL;
    }

    public String getNextPageURL() {
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_PARTNER");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getPartnerPageURL();

        return nextPageURL;
    }

    public String getChangeCriteriaURL() {
        String criteriaURL = HtmlUtil.getURLForAction("DISPLAY_FIND_QUOTE_BY_PARTNER_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getPartnerPageURL();
        return criteriaURL;
    }

}
