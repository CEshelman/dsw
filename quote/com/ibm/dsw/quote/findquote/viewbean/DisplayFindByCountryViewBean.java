package com.ibm.dsw.quote.findquote.viewbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByCountryContract;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByCountryViewBean</code> class.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindByCountryViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return ((DisplayFindQuoteByCountryContract) findContract).getCountry();
    }
    
    public String getSubRegion(){
        return ((DisplayFindQuoteByCountryContract) findContract).getSubRegion().trim();
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
        return ((DisplayFindQuoteByCountryContract) findContract).getState();
    }

    public List getStateList() {
        List stateOptionList = new ArrayList();

        if (!StringUtils.isEmpty(getCountry())) {
            String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                    FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
            stateOptionList.add(new SelectOptionImpl(selectOne, "", false));
            if (stateList != null && stateList.size() > 0)
                for (Iterator iter = stateList.iterator(); iter.hasNext();) {
                    CodeDescObj cdo = (CodeDescObj) iter.next();
                    stateOptionList.add(new SelectOptionImpl(cdo.getCodeDesc().trim(), cdo.getCode().trim(), cdo
                            .getCode().trim().equals(getState().trim())));
                }
        } else {
            String notAvailable = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                    FindQuoteMessageKeys.SELECT_COUNTRY_FIRST);
            stateOptionList.add(new SelectOptionImpl(notAvailable, "", false));
        }
        return stateOptionList;
    }
    
    public List getSubRegionOptionList() {
        List subRegionOptionList = new ArrayList();
        String defaultSubRegion = ((DisplayFindQuoteByCountryContract) findContract).getSubRegion();
        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
        
        subRegionOptionList.add(new SelectOptionImpl(selectOne, "", ((defaultSubRegion == null) || (defaultSubRegion
                .equalsIgnoreCase(""))) ? true : false));
        
        if (subRegions == null || subRegions.size() == 0)
            return subRegionOptionList;
        for (Iterator iter = subRegions.iterator(); iter.hasNext();) {
            CodeDescObj subRegion = (CodeDescObj) iter.next();
            subRegionOptionList.add(new SelectOptionImpl(subRegion.getCodeDesc(), subRegion.getCode(), subRegion.getCode().trim().equals(
                    getSubRegion())));
        }
        return subRegionOptionList;
    }

    public List getCountryOptionList() {
        List countryOptionList = new ArrayList();
        String defaultCountry = ((DisplayFindQuoteByCountryContract) findContract).getCountry();
        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
        
        if (countryListObj == null || countryListObj.size() == 0){
            String selectSubRegionFirst = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                    FindQuoteMessageKeys.SELECT_SUB_REGION_FIRST);
            countryOptionList.add(new SelectOptionImpl(selectSubRegionFirst, "", true));
            return countryOptionList;
        }
        countryOptionList.add(new SelectOptionImpl(selectOne, "", ((defaultCountry == null) || (defaultCountry
                .equalsIgnoreCase(""))) ? true : false));
        for (Iterator iter = countryListObj.iterator(); iter.hasNext();) {
            CodeDescObj cry = (CodeDescObj) iter.next();
            countryOptionList.add(new SelectOptionImpl(cry.getCodeDesc(), cry.getCode(), cry.getCode().equals(
                    getCountry())));
        }
        return countryOptionList;
    }

    public String getCountryPageURL(){
        String countryURL = "";
        countryURL += "&" + FindQuoteParamKeys.SUB_REGION + "=" + this.getSubRegion();
        countryURL += "&" + FindQuoteParamKeys.COUNTRY + "=" + this.getCountry();
        countryURL += "&" + FindQuoteParamKeys.STATE + "=" + this.getState();
		return countryURL;
        
    }
    
    public String getPrePageURL(){
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_COUNTRY");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getCountryPageURL();
        
        return prePageURL;
    }
    
    public String getNextPageURL(){
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_COUNTRY");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getCountryPageURL();
        
        return nextPageURL;
    }
    
    public String getChangeCriteriaURL(){
        String criteriaURL = HtmlUtil.getURLForAction("DISPLAY_FIND_QUOTE_BY_COUNTRY_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getCountryPageURL();
        return criteriaURL;
    }
}
