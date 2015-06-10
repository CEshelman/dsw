package com.ibm.dsw.quote.common.util;

import java.util.Arrays;
import java.util.List;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>LineItemSecNumGenerator<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-6-12
 */

public class LineItemSecNumGenerator {
    
    private static final int DEFAULT_SEED = 0;
    private static final int DEFAULT_INCREMENT = 10;
    
    private Quote quote = null;
    private int seed = 0;
    private int increment = 0;

    /**
     * 
     */
    public LineItemSecNumGenerator(Quote quote) {
        this.quote = quote;
        this.initialize();
    }
    
    protected void initialize() {
        if (this.quote == null || this.quote.getLineItemList() == null)
            return;
        
        List itemList = this.quote.getLineItemList();
        int n = itemList.size();
        int[] secNums = new int[n];
        
        for (int i = 0; i < n; i++) {
            Object obj = itemList.get(i);
            if (obj instanceof QuoteLineItem) {
                QuoteLineItem item = (QuoteLineItem) obj;
                secNums[i] = item.getRenewalQuoteSeqNum();
            }
            else {
                secNums[i] = 0;
            }
        }
        
        if (n == 0) {
            seed = DEFAULT_SEED;
            increment = DEFAULT_INCREMENT;
        }
        else if (n == 1) {
            seed = secNums[0];
            if (secNums[0] <= 0)
                increment = DEFAULT_INCREMENT;
            else
                increment = secNums[0] - 0;
        }
        else {
            Arrays.sort(secNums);
            seed = secNums[n-1];
            increment = secNums[n-1] - secNums[n-2];
        }
    }
    
    public int getNextSecNum() {
        this.seed += this.increment;
        return this.seed;
    }

}
