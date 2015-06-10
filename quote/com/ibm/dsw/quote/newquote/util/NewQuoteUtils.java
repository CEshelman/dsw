package com.ibm.dsw.quote.newquote.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>NewQuoteUtils</code> class has a few of auxiliary methods of New
 * Quote actions.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-16
 */
public final class NewQuoteUtils {

    public static boolean isQuoteBelongsToUser(String creatorId, String quoteNum) throws QuoteException {
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        return quoteProcess.isQuoteBelongsToUser(creatorId, quoteNum);
    }

    public static boolean isQuoteBelongsToUser(String creatorId, String quoteNum, String ownerFilter, String timeFilter)
            throws QuoteException {
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        List savedQuotes = quoteProcess.findDraftQuotes(creatorId, ownerFilter, timeFilter);
        if (savedQuotes == null || savedQuotes.size() == 0)
            return false;
        Iterator iter = savedQuotes.iterator();
        while (iter.hasNext()) {
            QuoteHeader draftSQ = (QuoteHeader) iter.next();
            if (StringUtils.trimToEmpty(draftSQ.getWebQuoteNum()).equalsIgnoreCase(quoteNum)) {
                return true;
            }
        }
        return false;
    }
}
