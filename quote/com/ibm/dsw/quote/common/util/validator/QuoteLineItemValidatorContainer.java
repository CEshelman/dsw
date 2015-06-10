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
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.opal.log.LogContext;

/**
 * DOC DOC This class contains all the 'QuoteLineItemValidator'. It's called by
 * com.ibm.dsw.quote.common.util.SalesQuoteValidationRule.validateQuoteLineItem(List<String>)
 */
@SuppressWarnings("rawtypes")
public class QuoteLineItemValidatorContainer {

    /**
     * DOC call initValidators() to init the validators.
     * 
     * @param validators
     */
    private QuoteLineItemValidatorContainer() {
        initValidators();
    }

    private static QuoteLineItemValidatorContainer instance;

    /**
     * Getter for instance.
     * 
     * @return the instance
     */
    public static synchronized QuoteLineItemValidatorContainer getInstance() {
        if (instance == null) {
            instance = new QuoteLineItemValidatorContainer();
        }
        return instance;
    }

    public final static String EC_VALIDATOR = "EC_VALIDATOR";

    private Map<String, QuoteLineItemValidator> validators = new HashMap<String, QuoteLineItemValidator>();

    private void initValidators() {
        validators.put(EC_VALIDATOR, new ECValidator());
    }

    public void validate(List<String> validatorKeys, Quote quote, QuoteLineItem item, Map validateResult,
            QuoteUserSession user, Cookie cookie, LogContext logContext) {
        for (Iterator<String> iterator = validatorKeys.iterator(); iterator.hasNext();) {
            String key = iterator.next();
            QuoteLineItemValidator validator = validators.get(key);
            validator.validate(quote, item, validateResult, user, cookie, logContext);

        }
    }
}
