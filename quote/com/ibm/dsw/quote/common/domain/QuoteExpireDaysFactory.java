package com.ibm.dsw.quote.common.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.common.util.DateHelper;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteExpireDaysFactory<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 21, 2007
 */

public class QuoteExpireDaysFactory extends PortalXMLConfigReader {

    private static QuoteExpireDaysFactory singleton = null;

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    private HashMap quoteExpireDaysMap;
    
    public static final int NEW_FMP_QT_EXP_DAYS = 105;
    public static final int MGRTED_FCT_QT_EXP_DAYS = 90;
    public static final int DEFAULT_EXP_DAYS = 30;

    public QuoteExpireDaysFactory() {
        super();
        quoteExpireDaysMap = new HashMap();
        loadConfig(buildConfigFileName());
    }

    public int findMaxQuoteExpireDays(QuoteHeader quoteHeader) {
        
        if (quoteHeader == null) {
            return -1;
        }
        
        String lob = quoteHeader.getLob() == null ? "" : StringUtils.trimToEmpty(quoteHeader.getLob().getCode());
        Country cntry = quoteHeader.getCountry();
        String geo = cntry == null ? "" : StringUtils.trimToEmpty(cntry.getSpecialBidAreaCode());
        boolean spBidFlag = (quoteHeader.getSpeclBidFlag() == 1);
        QuoteExpireDays quoteExpireDays = null;
        
        if (quoteExpireDaysMap == null) {
            quoteExpireDaysMap = new HashMap();
            loadConfig(buildConfigFileName());
        }
        
        if (quoteHeader.isFCTToPAQuote()) {
            if (StringUtils.isNotBlank(quoteHeader.getRenwlQuoteNum())) { // Migration quote from FCT renewal quote
                
                Date rnwlEndDate = quoteHeader.getRenwlEndDate();
                if (rnwlEndDate == null) {
                    return NEW_FMP_QT_EXP_DAYS;
                }

                return getDaysDifferenceFromNow(rnwlEndDate, MGRTED_FCT_QT_EXP_DAYS);
            }
            else { // FCT to PA new sales quote
                return NEW_FMP_QT_EXP_DAYS;
            }
        }
        else if (quoteHeader.isFCTQuote() && quoteHeader.isSalesQuote()) {
            if (quoteHeader.getRenwlEndDate() != null) { // FCT sales quote originating from FCT renewal quote
                return getDaysDifferenceFromNow(quoteHeader.getRenwlEndDate(), MGRTED_FCT_QT_EXP_DAYS);
            }
        }
        
        String key = getKey(lob, geo, String.valueOf(spBidFlag));
        logContext.debug(this, "finding quote expire days with key: " + key);
        quoteExpireDays = (QuoteExpireDays) quoteExpireDaysMap.get(key);

        if (quoteExpireDays != null) {
            return quoteExpireDays.getIQuoteMaxExpireDays();
        }
        else {
            // If there is no set up for max expiration days , use IBM default 30 days
        	return DEFAULT_EXP_DAYS;
        }
    }
    
    protected int getDaysDifferenceFromNow(Date rnwlEndDate, int expDays) {
        Date now = DateHelper.singleton().today();
        Date currDate = DateUtils.truncate(now, Calendar.DATE);

        // Add 30 days to FCT RQ end date
        Calendar rnwlEndDatePlus = Calendar.getInstance();
        rnwlEndDatePlus.setTime(rnwlEndDate);
        rnwlEndDatePlus.add(Calendar.DATE, expDays);

        // RQ expiration day is today if RQ end date + 30 is < today
        if ( currDate.after(rnwlEndDatePlus.getTime()) ) {
            return 0;
        // Max expiration day is RQ end date + 30 if RQ end date + 30 is >= today
        } else {
            return DateHelper.singleton().daysDifference(now, rnwlEndDatePlus.getTime());
        }
    }

    public static QuoteExpireDaysFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteExpireDaysFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = QuoteExpireDaysFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                QuoteExpireDaysFactory.singleton = (QuoteExpireDaysFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteExpireDaysFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteExpireDaysFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteExpireDaysFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

    private String getKey(String lob, String geo, String spBidflag) {
        return lob + "_" + geo + "_" + spBidflag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
     */
    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getQuoteMaxExpDaysConfigFileName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang.String)
     */
    protected void loadConfig(String fileName) {

        Element element = null;
        QuoteExpireDays quoteExpireDays = null;
        String lob = null;
        String geo = null;
        String spBidFlag = null;
        String expireDays = null;
        String key = null;
        try {
            logContext.debug(this, "Loading Quote Max Expire Days from file: " + fileName);
            Iterator quoteExpireDaysIterator = getRootElement(fileName).getChildren().iterator();
            while (quoteExpireDaysIterator.hasNext()) {
                element = (Element) quoteExpireDaysIterator.next();
                lob = element.getChildTextTrim("lob");
                spBidFlag = element.getChildTextTrim("specialBidFlag");
                geo = element.getChildTextTrim("geo");
                expireDays = element.getChildTextTrim("quoteMaxExpDays");
                quoteExpireDays = new QuoteExpireDays(lob, geo, Boolean.valueOf(spBidFlag).booleanValue(), new Integer(
                        expireDays).intValue());
                key = getKey(lob, geo, spBidFlag);
                quoteExpireDaysMap.put(key, quoteExpireDays);
            }
        } catch (Exception e) {
            logContext.error(this, e, "Exception loading Quote Max Expire Days from file: " + fileName);
        }
        logContext.debug(this, "Finished loading Quote Max Expire Days from file: " + fileName);
        logContext.debug(this, toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
     */
    protected void reset() {
        singleton = null;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();

        if ((quoteExpireDaysMap != null) && (quoteExpireDaysMap.size() > 0)) {
            Iterator itr = quoteExpireDaysMap.keySet().iterator();
            buffer.append("\n Quote Max Expire Days \n");

            while (itr.hasNext()) {
                String key = (String) itr.next();
                buffer.append(" key=");
                buffer.append(key);
                buffer.append(" value: ");
                buffer.append(quoteExpireDaysMap.get(key).toString());
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }
}
