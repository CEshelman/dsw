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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.ead4j.opal.log.LogContext;

/**
 * DOC This class contains all the 'PartsPricingConfigurationValidator'. It's called by
 * com.ibm.dsw.quote.common.util.SalesQuoteValidationRule.validatePartsPricingConfiguration(List<String>)
 */
@SuppressWarnings("rawtypes")
public class PartsPricingConfigurationValidatorContainer {

    /**
     * DOC call initValidators() to init the validators.
     * 
     * @param validators
     */
    private PartsPricingConfigurationValidatorContainer() {
        initValidators();
    }

    private static PartsPricingConfigurationValidatorContainer instance;

    /**
     * Getter for instance.
     * 
     * @return the instance
     */
    public static synchronized PartsPricingConfigurationValidatorContainer getInstance() {
        if (instance == null) {
            instance = new PartsPricingConfigurationValidatorContainer();
        }
        return instance;
    }

    public final static String TERM_EXTENSION_VALIDATOR = "TERM_EXTENSION_VALIDATOR";

    public final static String SERVICE_DATE_VALIDATOR = "SERVICE_DATE_VALIDATOR";

    private Map<String, PartsPricingConfigurationValidator> validators = new HashMap<String, PartsPricingConfigurationValidator>();

    private void initValidators() {
        validators.put(TERM_EXTENSION_VALIDATOR, new TermExtensionValidatorImpl());
        validators.put(SERVICE_DATE_VALIDATOR, new ServiceDateValidatorImpl());
    }

    public void validate(List<String> validatorKeys, Quote quote, PartsPricingConfiguration config, Map validateResult,
            QuoteUserSession user, Cookie cookie, LogContext logContext) {
        for (Iterator<String> iterator = validatorKeys.iterator(); iterator.hasNext();) {
            String key = iterator.next();
            PartsPricingConfigurationValidator validator = validators.get(key);
            validator.validate(quote, config, validateResult, user, cookie, logContext);

        }
    }
}
