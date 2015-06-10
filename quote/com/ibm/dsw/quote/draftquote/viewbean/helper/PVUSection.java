package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.util.List;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PVUSectionViewBean</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class PVUSection extends PartPriceCommon {
    
    public PVUSection(Quote quote)
    {
        super(quote);
    }
    /**
     * 
     * @return true if the chargeable unit on any part is PVU
     */
    public boolean showProcessValueUnitsSectionAndContents() {
        if (isPAE() || isPA() || isFCT() || isOEM()) {
            List lineItems = quote.getLineItemList();
            if (lineItems != null) {
                for (int i = 0; i < lineItems.size(); i++) {
                    QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
                    if (lineItem.isPvuPart()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return 
     */
    public boolean showPVUCalcOptional() {
        if (isPAE() || isPA() || isFCT() || isOEM()) {
            if (this.showProcessValueUnitsSectionAndContents()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return 
     */
    public boolean showProcessorDetails() {
        if (isPAE() || isPA() || isFCT() || isOEM()) {
            if (this.showProcessValueUnitsSectionAndContents()) {
                return true;
            }
        }
        return false;
    }
}