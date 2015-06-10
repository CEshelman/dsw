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

import java.util.Map;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.ead4j.opal.log.LogContext;

/**
 * DOC validate for term extensions link when submit the quote: Term extensions must include the entire configuration,
 * Please select the "Extend the entire configuration's end date" option on the parts and pricing tab and resubmit.
 */
@SuppressWarnings("rawtypes")
public class TermExtensionValidatorImpl implements PartsPricingConfigurationValidator {

    private boolean showErrorMessage(PartsPricingConfiguration config) {
        if (config.isTermExtension() && config.isAddOnTradeUp() && !config.isConfigEntireExtended()) {
            return true;
        }
        return false;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.common.util.validator.PartsPricingConfigurationValidator#validate(com.ibm.dsw.quote.common.
     * domain.Quote, com.ibm.dsw.quote.common.domain.PartsPricingConfiguration, java.util.Map,
     * com.ibm.dsw.quote.base.domain.QuoteUserSession, javax.servlet.http.Cookie, com.ibm.ead4j.opal.log.LogContext)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate(Quote quote, PartsPricingConfiguration config, Map validateResult, QuoteUserSession user, Cookie cookie,
            LogContext logContext) {
        if (showErrorMessage(config)) {
            validateResult.put(QuoteCapabilityProcess.TERM_EXTENSIONS_MUST_INCLUDE_THE_ENTIRE_CONFIGURATION, Boolean.TRUE);
        }
        if (hasNotSetTermExtension(config)) {
            validateResult.put(QuoteCapabilityProcess.TERM_EXTENSIONS_EXTENSION_HAS_NOT_BEEN_SPECIFIED, Boolean.TRUE);
        }
    }

    /**
     * 
     * DOC
     * "Quote contains SaaS parts with term extended but term extension has not been specified for the offering. Please go through the basic configurator to reset the term for these parts."
     * 
     * @param config
     * @return
     */
    private boolean hasNotSetTermExtension(PartsPricingConfiguration config) {
        // configEntireExtended
        if (config.isTermExtension() == false && config.isConfigEntireExtended() == true) {
            return true;
        }
        return false;
    }

}
