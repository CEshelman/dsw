package com.ibm.dsw.quote.submittedquote.util.sort;

import java.util.Collections;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 9, 2007
 */

public class SubmittedQuoteSortUtil {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    public static void sort(boolean isSalesQuote, List lineItems, String lob, boolean isSubmitter)
            throws TopazException {
        
        
        //  For submitter :  the sorting rules are same for draft and submitted quote, 
        // since we already sort line items and save to order to db2 in web_quote_line_item.quote_sectn_seq_num                            
        // here just need to compare the quote_sectn_seq_num and put them in asc  order
        
        // For non-submitter, we need apply the new rule , but don't save the new order to db2
        // so in the part price tab, the order may not same as draft quote.
    	if(QuoteConstants.LOB_PA.equalsIgnoreCase(lob)
    			|| QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
    			|| QuoteConstants.LOB_OEM.equalsIgnoreCase(lob)){
    		
    		if(isSubmitter){
    			Collections.sort(lineItems, new SectnSeqNumComparator());
    		} else {
    			Collections.sort(lineItems, new NonSaleRepPAandPAEComparator());
    		}
    		
    	} else if(QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)
    			    || QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob)){
    		
    		Collections.sort(lineItems, new SectnSeqNumComparator());
    	}
    }
}
