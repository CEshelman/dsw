/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: May 31, 2013
 */
package com.ibm.dsw.quote.common.util.validator;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;

/**
 * DOC validate service date. This class implements the interface PartsPricingConfigurationValidator. Refactored from
 * com.ibm.dsw.quote.common.util.SalesQuoteValidationRule
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ServiceDateValidatorImpl implements PartsPricingConfigurationValidator {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.common.util.validator.PartsPricingConfigurationValidator#validate(com.ibm.dsw.quote.common.
     * domain.Quote, com.ibm.dsw.quote.common.domain.PartsPricingConfiguration, java.util.Map,
     * com.ibm.dsw.quote.base.domain.QuoteUserSession, javax.servlet.http.Cookie, com.ibm.ead4j.opal.log.LogContext)
     */
    @Override
    public void validate(Quote quote, PartsPricingConfiguration config, Map validateResult, QuoteUserSession user, Cookie cookie,
            LogContext logContext) {
        // head info
        Date quoteStDate = quote.getQuoteHeader().getQuoteStartDate();
        Date quoteExpDate = quote.getQuoteHeader().getQuoteExpDate();
        Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate();

        if (!config.isTermExtension())
            return;
        // Provisioning Days
        int provDays = config.getProvisngDays();

        // Extension service date
        Date extServiceDate = config.getServiceDate();

        // Extension service type

        ServiceDateModType type = config.getServiceDateModType();

        // Estimated Line Item Start Date
        Date estmtProvisionStartDate = getProvsngDate(quote, config);

        // EXTENSION_END_DATE_VALID_MESSAGE

        if (estmtdOrdDate != null) {
            if (type != null) {
                if (type.equals(ServiceDateModType.RS) || type.equals(ServiceDateModType.LS)) {
                    if (extServiceDate != null) {
                        if (DateUtil.calculateDateBefore(estmtdOrdDate, extServiceDate) < provDays) {
                            validateResult.put(QuoteCapabilityProcess.EXTENSION_START_DATE_VALID_MESSAGE, Boolean.TRUE);
                        }
                    }
                } else if (type.equals(ServiceDateModType.CE)) {
                    // Quote Expiration Date after calculate
                    Date quoteExpDateCal = addDays(QuoteValidationRule.getLastProvDateByEsmtLineStDate(estmtProvisionStartDate),
                            -provDays);

                    // Quote Start Date after calculate
                    Date quoteStartDateCal = addDays(
                            QuoteValidationRule.getEarliestProvDateByEsmtLineStDate(estmtProvisionStartDate), -provDays);

                    /*Delete by Wade for 15.2 BF requirement
                    Calendar extCal = Calendar.getInstance();
                    extCal.setTime(extServiceDate);
                    extCal.add(Calendar.DATE, 1);
                    if (extCal.get(Calendar.DATE) != 1) {
                        validateResult.put(QuoteCapabilityProcess.QUOTE_END_DATE_IS_NOT_LAST_DAY_IN_MONTH, Boolean.TRUE);
                    }
                    */
                    
                    if (quoteStDate.compareTo(quoteStartDateCal) < 0) {
                        validateResult.put(QuoteCapabilityProcess.QUOTE_START_DATE_NOT_AFTER_EARLIEST_PROVISIONING, Boolean.TRUE);
                    }

                    if (quoteStDate.compareTo(quoteExpDate) > 0 || quoteExpDate.compareTo(quoteExpDateCal) > 0) {
                        validateResult.put(QuoteCapabilityProcess.QUOTE_EXPIR_DATE_NOT_AFTER_LASTEST_PROVISIONING, Boolean.TRUE);
                    }
                }
            } else {
                validateResult.put(QuoteCapabilityProcess.EXTENSION_TYPE_VALID_MESSAGE, Boolean.TRUE);
            }
        }
    }

    public static Date getProvsngDate(Quote quote, PartsPricingConfiguration ppc) {
        Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate() == null ? DateUtil.getCurrentYYYYMMDDDate() : quote
                .getQuoteHeader().getEstmtdOrdDate();
        int provisngDays = ppc.getProvisngDays() == null ? 0 : ppc.getProvisngDays().intValue();
        Calendar estdPrvsngCal = Calendar.getInstance();// Estimated provisioning Date
        estdPrvsngCal.setTime(estmtdOrdDate);
        estdPrvsngCal.add(Calendar.DATE, provisngDays);// Estimated provisioning Date = estimated Order Date + item's
                                                       // provisioning days
        java.sql.Date provsngDate = new java.sql.Date(estdPrvsngCal.getTime().getTime());
        return provsngDate;
    }

    public static Date addDays(Date date, int days) {
        Date resultDate = new Date();
        resultDate.setTime(date.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(resultDate);
        cal.add(Calendar.DATE, days);
        resultDate.setTime(cal.getTimeInMillis());
        return resultDate;
    }


}
