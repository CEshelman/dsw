package com.ibm.dsw.quote.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * <p>
 * The <code>QuoteCookie</code> encapsulate the cookie operations in SQO.
 * </p>
 * <br>
 * Usage:
 * 
 * <pre>
 * Cookie cookie = QuoteCookie.findQuoteCookie(request);
 * // read or write cookie
 * // cookie value in client brower will look like:
 * // time_7@owner_goshen@otherKey_otherValue
 * // note that there is no assumption about the key/value order 
 * 
 * 
 * </pre>
 * 
 * @author panyg@cn.ibm.com
 * @since DSW 9.1 Creation date: 2007-2-10
 */
public final class QuoteCookie {

    //cookie properties
    public final static String quoteCookieName = ApplicationProperties.getInstance().getQuoteCookieName();//"quoteCookie";

    public final static String stCookieName = ApplicationProperties.getInstance().getStCookieName();//"stCookie";

    public final static String quoteCookieDomain = ApplicationProperties.getInstance().getQuoteCookieDomain();//".ibm.com";

    public final static int quoteCookieExpires = ApplicationProperties.getInstance().getQuoteCookieExpires();//365

    // * 24
    // * 60
    // * 60

    public final static String quoteCookiePath = ApplicationProperties.getInstance().getQuoteCookiePath();//"/";

    //Cookie value keys
    public final static String quoteCookieTimeKey = ApplicationProperties.getInstance().getQuoteCookieTimeKey();//"time";

    public final static String quoteCookieOwnerKey = ApplicationProperties.getInstance().getQuoteCookieOwnerKey();//"onwer";

    public final static String quoteCookieLOBKey = ApplicationProperties.getInstance().getQuoteCookieLOBKey();//"lob";

    public final static String quoteCookieCountryKey = ApplicationProperties.getInstance().getQuoteCookieCountryKey();//"cntry";
    
    public final static String quoteCookieSubRegionKey = ApplicationProperties.getInstance().getQuoteCookieSubRegionKey();//"sub region";
    
    public final static String quoteCookieAcquisitionKey = ApplicationProperties.getInstance().getQuoteCookieAcquisitionKey();//"acqstn"
    
    public final static String quoteCookieSearchAcqstnKey = ApplicationProperties.getInstance().getQuoteCookieSearchAcqstnKey();//"fqacquisition";
    
    public final static String quoteCookieSearchClsfctnKey = ApplicationProperties.getInstance().getQuoteCookieSearchClsfctnKey();//"qsclsfctn";

    public final static String mandatoryFlagKey = ApplicationProperties.getInstance().getMandatoryFlagKey();

    public final static String displayDetailFlagKey = ApplicationProperties.getInstance().getDisplayDetailFlagKey();

    public final static String browsePartBrandKey = ApplicationProperties.getInstance().getBrowsePartBrandKey();

    public final static String quoteCookieBusOrgKey = ApplicationProperties.getInstance().getQuoteCookieBusOrgKey();

    public final static String quoteTypeKey = ApplicationProperties.getInstance().getQuoteTypeKey();

    public final static String renewalOrSalesKey = ApplicationProperties.getInstance().getRenewalOrSalesKey();

    public final static String overallStatusKey = ApplicationProperties.getInstance().getOverallStatusKey();

    public final static String statusSortByKey = ApplicationProperties.getInstance().getStatusSortByKey();
    
    public final static String approverGroupDateKey = ApplicationProperties.getInstance().getApproverGroupDateKey();
    
    public final static String approverTypeDateKey = ApplicationProperties.getInstance().getApproverTypeDateKey();

    public final static String submittedTimeKey = ApplicationProperties.getInstance().getSubmittedTimeKey();
    
    public final static String submittedTimeOptionsKey = ApplicationProperties.getInstance().getSubmittedTimeOptionsKey();

    public final static String submittedOwnerKey = ApplicationProperties.getInstance().getSubmittedOwnerKey();

    public final static String submittedOwnerRoleKey = ApplicationProperties.getInstance().getSubmittedOwnerRoleKey();

    public final static String submittedCustStateKey = ApplicationProperties.getInstance().getSubmittedCustStateKey();

    public final static String submittedCustSBRegionKey = ApplicationProperties.getInstance()
            .getSubmittedCustSBRegionKey();

    public final static String submittedCustSBDistrictKey = ApplicationProperties.getInstance()
            .getSubmittedCustSBDistrictKey();

    public final static String submittedApproverGroupKey = ApplicationProperties.getInstance()
            .getSubmittedApproverGroupKey();
    
    public final static String submittedApproverTypeKey = ApplicationProperties.getInstance()
            .getSubmittedApproverTypeKey();
    
    public final static String approverTypeFilterKey = ApplicationProperties.getInstance().getApproverTypeFilterKey();
    
    public final static String approverGroupFilterKey = ApplicationProperties.getInstance().getApproverGroupFilterKey();

    public final static String lineItemDetailFlagKey = ApplicationProperties.getInstance().getLineItemDetailFlagKey();
    
    public final static String brandDetailFlagKey = ApplicationProperties.getInstance().getBrandDetailFlagKey();

    public final static String aprQueueTypeKey = ApplicationProperties.getInstance().getAprQueueTypeKey();
    
    public final static String aprQueueSortFilterKey = ApplicationProperties.getInstance().getAprQueueSortFilterKey();
    
    public final static String aprQueueTrackerTypeKey = ApplicationProperties.getInstance().getAprQueueTrackerTypeKey();
    
    public final static String aprQueueTrackerSortFilterKey = ApplicationProperties.getInstance().getAprQueueTrackerSortFilterKey();
    //Default cookie values
    public final static String defaultBrowsePartBrand = ApplicationProperties.getInstance().getBrowsePartBrandDefault();

    public final static String defaultTimeFilter = ApplicationProperties.getInstance().getTimeFilterDefault();

    public final static String defaultOwnerFilter = ApplicationProperties.getInstance().getOwnerFilterDefault();

    public final static String defaultMandatoryFlag = ApplicationProperties.getInstance().getMandatoryFlagDefault();

    public final static String defaultDisplayDetailFlag = ApplicationProperties.getInstance()
            .getDisplayDetailFlagDefault();

    public final static String defaultBusinessOrg = ApplicationProperties.getInstance().getBusinessOrgDefault();

    public final static String quoteTypeDefault = ApplicationProperties.getInstance().getQuoteTypeDefault();

    public final static String renewalOrSalesDefault = ApplicationProperties.getInstance().getRenewalOrSalesDefault();

    public final static String overallStatusDefault = ApplicationProperties.getInstance().getOverallStatusDefault();
    
    public final static String approverTypeDefault = ApplicationProperties.getInstance().getApproverTypeDefault();
    
    public final static String approverGroupDefault = ApplicationProperties.getInstance().getApproverGroupDefault();

    public final static String statusSortByDefault = ApplicationProperties.getInstance().getStatusSortByDefault();

    public final static String submittedTimeDefault = ApplicationProperties.getInstance().getSubmittedTimeDefault();

    public final static String submittedOwnerDefault = ApplicationProperties.getInstance().getSubmittedOwnerDefault();

    public final static String submittedOwnerRoleDefault = ApplicationProperties.getInstance()
            .getSubmittedOwnerRoleDefault();

    public final static String submittedCustStateDefault = ApplicationProperties.getInstance()
            .getSubmittedCustStateDefault();

    public final static String submittedCustSBRegionDefault = ApplicationProperties.getInstance()
            .getSubmittedCustSBRegionDefault();

    public final static String submittedCustSBDistrictDefault = ApplicationProperties.getInstance()
            .getSubmittedCustSBDistrictDefault();

    public final static String submittedApproverGroupDefault = ApplicationProperties.getInstance()
            .getSubmittedApproverGroupDefault();

    public final static String submittedApproverTypeDefault = ApplicationProperties.getInstance()
            .getSubmittedApproverTypeDefault();
    
    public final static String lineItemDetailFlagDefault = ApplicationProperties.getInstance()
            .getLineItemDetailFlagDefault();
    
    public final static String brandDetailFlagDefault = ApplicationProperties.getInstance().getBrandDetailFlagDefault();

    public final static String aprQueueTypeDefault = ApplicationProperties.getInstance().getAprQueueTypeDefault();
    
    public final static String aprQueueSortFilterDefault = ApplicationProperties.getInstance().getAprQueueSortFilterDefault();
    
    public final static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public final static String aprSearchTypeKey = ApplicationProperties.getInstance().getAprSearchTypeKey();
    
    public final static String aprSearchTypeDefault = ApplicationProperties.getInstance().getAprSearchTypeDefault();
    

    private QuoteCookie() {
    }

    /**
     * @param request
     * @return
     */
    public static Cookie findQuoteCookie(javax.servlet.http.HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        return findQuoteCookie(cookies);
    }

    /**
     * @param cookies
     * @return
     * @deprecated
     */
    public static Cookie findQuoteCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (quoteCookieName.equals(cookie.getName())) {
                    return retrieveQuoteCookie(cookie);
                }
            }
        }
        return createQuoteCookie();
    }

    private static Cookie findCookie(javax.servlet.http.HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName())) {
                    return retrieveQuoteCookie(cookie);
                }
            }
        }
        return createQuoteCookie(cookieName);
    }

    public static Cookie findStatusTrackerCookie(javax.servlet.http.HttpServletRequest req) {
        return findCookie(req, stCookieName);
    }

    /**
     * @param cookie
     * @return
     */
    public static Cookie retrieveQuoteCookie(Cookie cookie) {
        cookie.setMaxAge(quoteCookieExpires);
        cookie.setDomain(quoteCookieDomain);
        cookie.setPath(quoteCookiePath);
        return cookie;
    }

    /**
     * @return
     */
    private static Cookie createQuoteCookie() {
        Cookie cookie = new Cookie(quoteCookieName, "");
        return retrieveQuoteCookie(cookie);
    }

    private static Cookie createQuoteCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        return retrieveQuoteCookie(cookie);
    }

    /**
     * 
     * return time filer value in cookie If filter is not found in cookie, a
     * default value of time filter will be returned
     * 
     * @param cookie
     * @return
     */
    public static String getTimeFilter(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieTimeKey, defaultTimeFilter);
    }

    /**
     * return owner filter value in cookie If owner filter is not found in
     * cookie, a default value of owner filter will be returned
     * 
     * @param cookie
     * @return
     */
    public static String getOwnerFilter(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieOwnerKey, defaultOwnerFilter);
    }

    public static String getMandatoryFlag(Cookie cookie) {
        return getCookieValue(cookie, mandatoryFlagKey, defaultMandatoryFlag);
    }

    public static String getDisplayDetailFlag(Cookie cookie) {
        return getCookieValue(cookie, displayDetailFlagKey, defaultDisplayDetailFlag);
    }

    public static String getBrowsePartBrand(Cookie cookie) {
        return getCookieValue(cookie, browsePartBrandKey, defaultBrowsePartBrand);
    }

    public static void setMandatoryFlag(Cookie cookie, String mandatoryFlag) {
        setCookieValue(cookie, mandatoryFlagKey, mandatoryFlag);
    }

    public static void setDisplayDetailFlag(Cookie cookie, String displayDetailFlag) {
        setCookieValue(cookie, displayDetailFlagKey, displayDetailFlag);
    }
    
    public static void setIsShowProvReminderFlag(Cookie cookie,String isShowProvReminderFlag ){
    	setCookieValue(cookie, "isShowProvReminderFlag", isShowProvReminderFlag);
    }
    
    public static String getIsShowProvReminderFlag(Cookie cookie) {
        return getCookieValue(cookie, "isShowProvReminderFlag", "false");
    }

    /**
     * set time filter to cookie
     * 
     * @param cookie
     * @param timeFilter
     */
    public static void setTimeFilter(Cookie cookie, String timeFilter) {
        setCookieValue(cookie, quoteCookieTimeKey, timeFilter);
    }

    /**
     * set onwer filter to cookie
     * 
     * @param cookie
     * @param ownerFilter
     */
    public static void setOwnerFilter(Cookie cookie, String ownerFilter) {
        setCookieValue(cookie, quoteCookieOwnerKey, ownerFilter);
    }

    public static void setLOBCookieValue(Cookie cookie, String value) {
        setCookieValue(cookie, quoteCookieLOBKey, value);
    }

    public static void setCountryCookieValue(Cookie cookie, String value) {
        setCookieValue(cookie, quoteCookieCountryKey, value);
    }
    
    public static void setSubRegionCookieValue(Cookie cookie, String value) {
        setCookieValue(cookie, quoteCookieSubRegionKey, value);
    }
    
    public static void setAcquisitionCookieValue(Cookie cookie, String value) {
        setCookieValue(cookie, quoteCookieAcquisitionKey, value);
    }

    public static void setSearchClsfctnCookieValues(Cookie cookie, List value) {
        setCookieValues(cookie, quoteCookieSearchClsfctnKey, value);
    }

    public static void setSearchAcqstnCookieValue(Cookie cookie, String value) {
        setCookieValue(cookie, quoteCookieSearchAcqstnKey, value);
    }

    /**
     * @param cookie
     * @param cookieKey
     * @param value
     */
    public static void setCookieValue(Cookie cookie, String cookieKey, String value) {
        String startValue = null;
        String endValue = null;
        String oldValue = getCookieValue(cookie, cookieKey);
        String cookieValue = cookie.getValue();
        String newValue = cookieKey + "_" + value + "@";
        int pos = cookieValue.indexOf(cookieKey);
        try {
            if (pos >= 0) {
                // exist, replace with new value;
                startValue = cookieValue.substring(0, pos);
                endValue = cookieValue.substring(pos + cookieKey.length() + oldValue.length() + 2);
                cookieValue = startValue + newValue + endValue;
            } else {
                // not exist, add value to the end
                cookieValue += newValue;
            }
            cookie.setValue(cookieValue);
        } catch (Exception e) {
            logContext.error(QuoteCookie.class, e, e.getMessage());
        }
    }

    /**
     * @param cookie
     * @param cookieKey
     * @param values
     */
    private static void setCookieValues(Cookie cookie, String cookieKey, List values) {
        if (values == null || values.size() == 0) {
            setCookieValue(cookie, cookieKey, "");
            return;
        }
        Iterator iter = values.iterator();
        String value = "";
        while (iter.hasNext()) {
            value = value + iter.next() + ":";
        }
        setCookieValue(cookie, cookieKey, value);
    }

    /**
     * @param cookie
     * @param cookieKey
     * @return
     */
    public static String getCookieValue(Cookie cookie, String cookieKey) {
        return getCookieValue(cookie, cookieKey, "");
    }

    /**
     * @param cookie
     * @param cookieKey
     * @param defaultValue
     * @return
     */
    public static String getCookieValue(Cookie cookie, String cookieKey, String defaultValue) {
        String strKeyValue;
        String strCookieValue = cookie.getValue();
        if (null == cookie || null == strCookieValue || strCookieValue.equals(""))
            return defaultValue;
        if (strCookieValue.indexOf(cookieKey) == -1)
            return defaultValue;
        try {
            strKeyValue = strCookieValue.substring(strCookieValue.indexOf(cookieKey), strCookieValue.length());
            strKeyValue = strKeyValue.substring(strKeyValue.indexOf("_") + 1, strKeyValue.indexOf("@"));
        } catch (Exception e) {
            logContext.error(QuoteCookie.class, e, e.getMessage());
            return defaultValue;
        }
        return strKeyValue;
    }

    /**
     * @param cookie
     * @param quoteTypeKey
     * @param quoteTypeDefault
     * @return
     */
    public static List getCookieValues(Cookie cookie, String cookieKey, String defaultValue) {
        String value = getCookieValue(cookie, cookieKey, defaultValue);
        List returnList = new ArrayList();

        try {
            if (value.indexOf(":") > 0) {
                while (value.indexOf(":") > 0) {
                    returnList.add(value.substring(0, value.indexOf(":")));
                    value = value.substring(value.indexOf(":") + 1, value.length());
                }
            } else {
                returnList.add(value);
            }

        } catch (Exception e) {
            logContext.error(QuoteCookie.class, e, e.getMessage());
            returnList.add(value);
            return returnList;
        }
        return returnList;
    }

    /**
     * @return Returns the quoteCookieCountryKey.
     */
    public static String getCountry(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieCountryKey);
    }
    
    /**
     * @return Returns the quoteCookieSubRegionKey.
     */
    public static String getSubRegion(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieSubRegionKey);
    }
    
    public static String getAcquisition(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieAcquisitionKey);
    }

    /**
     * @return Returns the quoteCookieLOBKey.
     */
    public static String getLOB(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieLOBKey);
    }

    public static String getBusinessOrg(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieBusOrgKey, defaultBusinessOrg);
    }

    public static void setBusinessOrg(Cookie cookie, String defaultBusinessOrg) {
        setCookieValue(cookie, quoteCookieBusOrgKey, defaultBusinessOrg);
    }

    /**
     * @return Returns the quoteCookieDomain.
     */
    public static String getQuoteCookieDomain() {
        return quoteCookieDomain;
    }

    /**
     * @return Returns the quoteCookieName.
     */
    public static String getQuoteCookieName() {
        return quoteCookieName;
    }

    /**
     * @return Returns the quoteCookiePath.
     */
    public static String getQuoteCookiePath() {
        return quoteCookiePath;
    }

    /**
     * @return Returns the quoteCookieExpires.
     */
    public static int getQuoteCookieExpires() {
        return quoteCookieExpires;
    }

    public static List getQuoteTypes(Cookie cookie) {
        return getCookieValues(cookie, quoteTypeKey, quoteTypeDefault);
    }

    public static List getRenewalOrSaleses(Cookie cookie) {
        return getCookieValues(cookie, renewalOrSalesKey, renewalOrSalesDefault);
    }

    public static List getOverallStatuses(Cookie cookie) {
        return getCookieValues(cookie, overallStatusKey, overallStatusDefault);
    }

    public static List getApproverTypeFilter(Cookie cookie) {
        return getCookieValues(cookie, approverTypeFilterKey, approverTypeDefault);
    }
    
    public static List getApproverGroupFilter(Cookie cookie) {
        return getCookieValues(cookie, approverGroupFilterKey, approverGroupDefault);
    }
    
    public static String getStatusSortBy(Cookie cookie) {
        return getCookieValue(cookie, statusSortByKey, statusSortByDefault);
    }

    public static String getSubmittedTime(Cookie cookie) {
        return getCookieValue(cookie, submittedTimeKey, submittedTimeDefault);
    }
    
    public static String getSubmittedTimeOptions(Cookie cookie) {
        return getCookieValue(cookie, submittedTimeOptionsKey);
    }

    public static String getSubmittedOwner(Cookie cookie) {
        return getCookieValue(cookie, submittedOwnerKey, submittedOwnerDefault);
    }

    public static List getSubmittedOwnerRoles(Cookie cookie) {
        return getCookieValues(cookie, submittedOwnerRoleKey, submittedOwnerRoleDefault);
    }

    public static String getSubmittedCustState(Cookie cookie) {
        return getCookieValue(cookie, submittedCustStateKey, submittedCustStateDefault);
    }

    public static String getSubmittedCustSBRegion(Cookie cookie) {
        return getCookieValue(cookie, submittedCustSBRegionKey, submittedCustSBRegionDefault);
    }

    public static String getSubmittedCustSBDistrict(Cookie cookie) {
        return getCookieValue(cookie, submittedCustSBDistrictKey, submittedCustSBDistrictDefault);
    }

    public static String getSubmittedApproverGroup(Cookie cookie) {
        return getCookieValue(cookie, submittedApproverGroupKey, submittedApproverGroupDefault);
    }
    public static String getSubmittedApproverType(Cookie cookie) {
        return getCookieValue(cookie, submittedApproverTypeKey, submittedApproverTypeDefault);
    }
    
    public static void setQuoteType(Cookie cookie, List quoteType) {
        setCookieValues(cookie, quoteTypeKey, quoteType);
    }

    public static void setRenewalOrSales(Cookie cookie, List renewalOrSales) {
        setCookieValues(cookie, renewalOrSalesKey, renewalOrSales);
    }

    public static void setOverallStatus(Cookie cookie, List overallStatus) {
        setCookieValues(cookie, overallStatusKey, overallStatus);
    }

    public static void setStatusSortBy(Cookie cookie, String statusSortBy) {
        setCookieValue(cookie, statusSortByKey, statusSortBy);
    }
    
    public static void setApproverGroupDate(Cookie cookie, String approverGroupDate){
        setCookieValue(cookie, approverGroupDateKey, approverGroupDate);
    }
    
    public static void setApproverTypeDate(Cookie cookie, String approverTypeDate){
        setCookieValue(cookie, approverTypeDateKey, approverTypeDate);
    }

    public static void setSubmittedTime(Cookie cookie, String submittedTime) {
        setCookieValue(cookie, submittedTimeKey, submittedTime);
    }

    public static void setSubmittedTimeOptions(Cookie cookie, String timeOptionsAsString) {
        setCookieValue(cookie, submittedTimeOptionsKey, timeOptionsAsString);
    }
    
    public static void setSubmittedOwner(Cookie cookie, String submittedOwner) {
        setCookieValue(cookie, submittedOwnerKey, submittedOwner);
    }

    public static void setSubmittedOwnerRole(Cookie cookie, List submittedOwnerRole) {
        setCookieValues(cookie, submittedOwnerRoleKey, submittedOwnerRole);
    }

    public static void setSubmittedCustState(Cookie cookie, String submittedCustState) {
        setCookieValue(cookie, submittedCustStateKey, submittedCustState);
    }

    public static void setSubmittedCustSBRegion(Cookie cookie, String submittedCustSBRegion) {
        setCookieValue(cookie, submittedCustSBRegionKey, submittedCustSBRegion);
    }

    public static void setSubmittedCustSBDistrict(Cookie cookie, String submittedCustSBDistrict) {
        setCookieValue(cookie, submittedCustSBDistrictKey, submittedCustSBDistrict);
    }

    public static void setSubmittedApproverGroup(Cookie cookie, String submittedApproverGroup) {
        setCookieValue(cookie, submittedApproverGroupKey, submittedApproverGroup);
    }
    
    public static void setSubmittedApproverType(Cookie cookie, String submittedApproverType) {
        setCookieValue(cookie, submittedApproverTypeKey, submittedApproverType);
    }
    
    public static void setApproverTypeFilter(Cookie cookie, List approverTypeFilter) {
        setCookieValues(cookie, approverTypeFilterKey, approverTypeFilter);
    }
    
    public static void setApproverGroupFilter(Cookie cookie, List approverGroupFilter) {
        setCookieValues(cookie, approverGroupFilterKey, approverGroupFilter);
    }
    
    public static void setLineItemDetailFlag(Cookie cookie, String lineItemDetailFlag) {
        setCookieValue(cookie, lineItemDetailFlagKey, lineItemDetailFlag);
    }
    
    public static void setBrandDetailFlag(Cookie cookie, String brandDetailFlag) {
        setCookieValue(cookie, brandDetailFlagKey, brandDetailFlag);
    }

    public static String getLineItemDetailFlag(Cookie cookie) {
        return getCookieValue(cookie, lineItemDetailFlagKey, lineItemDetailFlagDefault);
    }
    
    public static String getBrandDetailFlag(Cookie cookie) {
        return getCookieValue(cookie, brandDetailFlagKey, brandDetailFlagDefault);
    }

    public static String getAprQueueType(Cookie cookie) {
        return getCookieValue(cookie, aprQueueTypeKey, aprQueueTypeDefault);
    }
    public static String getAprQueueSortFilter(Cookie cookie) {
        return getCookieValue(cookie, aprQueueSortFilterKey, aprQueueSortFilterDefault);
    }
    public static void setAprQueueType(Cookie cookie, String aprQueueType) {
        setCookieValue(cookie, aprQueueTypeKey, aprQueueType);
    }

    public static void setAprQueueSortFilter(Cookie cookie, String aprQueueSortFilter) {
        setCookieValue(cookie, aprQueueSortFilterKey, aprQueueSortFilter);
    }
    
    public static String getAprQueueTrackerType(Cookie cookie) {
        return getCookieValue(cookie, aprQueueTrackerTypeKey, aprQueueTypeDefault);
    }
    public static String getAprQueueTrackerSortFilter(Cookie cookie) {
        return getCookieValue(cookie, aprQueueTrackerSortFilterKey, aprQueueSortFilterDefault);
    }
    
    public static void setAprQueueTrackerType(Cookie cookie, String aprQueueTrackerType) {
        setCookieValue(cookie, aprQueueTrackerTypeKey, aprQueueTrackerType);
    }

    public static void setAprQueueTrackerSortFilter(Cookie cookie, String aprQueueTrackerSortFilter) {
        setCookieValue(cookie, aprQueueTrackerSortFilterKey, aprQueueTrackerSortFilter);
    }

    public static String getSearchAcqstnCookieValue(Cookie cookie) {
        return getCookieValue(cookie, quoteCookieSearchAcqstnKey, "");
    }
    public static List getSearchClsfctnCookieValue(Cookie cookie) {
        return getCookieValues(cookie, quoteCookieSearchClsfctnKey, "");
    }
    
    public static String getApproverGroupDate(Cookie cookie) {
        return getCookieValue(cookie, approverGroupDateKey);
    }
    
    public static String getApproverTypeDate(Cookie cookie) {
        return getCookieValue(cookie, approverTypeDateKey);
    }
        
    public static String getAprSearchType(Cookie cookie) {
        return getCookieValue(cookie, aprSearchTypeKey, aprSearchTypeDefault);
    }
    
    public static void setAprSearchType(Cookie cookie, String aprSearchType) {
        setCookieValue(cookie, aprSearchTypeKey, aprSearchType);
    }
}
