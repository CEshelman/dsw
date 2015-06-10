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
import com.ibm.ead4j.opal.log.LogContext;

/**
 * DOC The classes implement this interface are used to validate all the 'PartsPricingConfiguration' of Sales Quote.
 */
public interface PartsPricingConfigurationValidator {

    @SuppressWarnings("rawtypes")
    public void validate(Quote quote, PartsPricingConfiguration config, Map validateResult, QuoteUserSession user, Cookie cookie,
            LogContext logContext);

}
