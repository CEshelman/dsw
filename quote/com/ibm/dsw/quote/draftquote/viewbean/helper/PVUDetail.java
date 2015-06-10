package com.ibm.dsw.quote.draftquote.viewbean.helper;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PVUDetailViewBean</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class PVUDetail extends PartPriceCommon {
    
    
    public PVUDetail(Quote quote)
    {
        super(quote);
    }
    /**
     * 
     * @param lineItem
     * @return
     */
    public boolean showPVUDetailsRow(QuoteLineItem lineItem) {
        if (isPAE() || isPA() || isFCT() || isOEM()) {
            if (lineItem.isPvuPart()) {
                //If chargeable unit on the part is PVU
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param lineItem
     * @return
     */
    public boolean showPVUDetails(QuoteLineItem lineItem) {
        if (showPVUDetailsRow(lineItem)) {
            if (lineItem.getLineItemConfigs() != null && lineItem.getLineItemConfigs().size() != 0) {
                return true;
            }
        }

        return false;
    }
    
    public String getPVUDetail_PRC_CORES() {
        return PartPriceConstants.PVUDetail.PROCESSOR_CORES;
    }
    
    public String getPVUDetail_PVUs_PER_CORE() {
        return PartPriceConstants.PVUDetail.PVUs_PER_CORE;
    }
    
    public String getPVUDetail_TOTAL_PVUs() {
        return PartPriceConstants.PVUDetail.TOTAL_PVUs;
    }
    
    /**
     * if the chargeable unit on the part is PVU, and PVU details have been provided from the calculator
     * @return
     */
    public boolean showSubmittedPVUDetails(QuoteLineItem lineItem) {
        if (isPA() || isPAE() || isFCT() || isOEM()) {
            if (lineItem.getLineItemConfigs() != null && lineItem.getLineItemConfigs().size() != 0) {
                return lineItem.isPvuPart();
            }
        }
        
        return false;
    }

    public boolean showPVUCalcOverride(QuoteLineItem lineItem) {
        if (showPVUDetailsRow(lineItem)) {
	        if (PartPriceConstants.QTY_CACLTR_OVERRIDDEN.equalsIgnoreCase(lineItem.getPVUOverrideQtyIndCode())) {
                return true;
	        }
        }

        return false;
    }
    
    /**
     * if the chargeable unit on the part is PVU, quantity was provided by the calculator, and user changed that quantity manually
     * @param lineItem
     * @return
     */
    public boolean showSubmittedPVUCalcOverride(QuoteLineItem lineItem) {
        if (isPA() || isPAE() || isFCT() || isOEM()) {
            if (PartPriceConstants.QTY_CACLTR_OVERRIDDEN.equalsIgnoreCase(lineItem.getPVUOverrideQtyIndCode())) {
                if (lineItem.isPvuPart()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean showPVUQuantityManualEntered(QuoteLineItem lineItem) {
        if (showPVUDetailsRow(lineItem)) {
	        if (PartPriceConstants.QTY_MANUAL_ENTERED.equalsIgnoreCase(lineItem.getPVUOverrideQtyIndCode())) {
                return true;
	        }
        }
        
        return false;
    }
    
    /**
     * if the chargeable unit on the part is PVU and user entered quantity without using the calculator
     * @param lineItem
     * @return
     */
    public boolean showSubmittedPVUQuantityManualEntered(QuoteLineItem lineItem) {
        if (isPA() || isPAE() || isFCT() || isOEM()) {
            if (PartPriceConstants.QTY_MANUAL_ENTERED.equalsIgnoreCase(lineItem.getPVUOverrideQtyIndCode())) {
                if (lineItem.isPvuPart()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 
     * @return
     */
    public boolean showCalcPartQtyLink(QuoteLineItem lineItem) {
        if (lineItem.getLineItemConfigs() == null || lineItem.getLineItemConfigs().size() == 0) {
            if (lineItem.isPvuPart()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public boolean showRevisePartQtyLink(QuoteLineItem lineItem) {
        if (lineItem.getLineItemConfigs() != null && lineItem.getLineItemConfigs().size() != 0) {
            if (showPVUDetailsRow(lineItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Display if chargeable unit on the part is PVU
     * For PA & PAE, only display if the user's preference is to show line item details
     * @param qli
     * @return
     */
    public boolean showSubmittedPVUSection(QuoteLineItem qli,boolean userPreference) {
        if (isPA() || isPAE() || isOEM()) {
            return qli.isPvuPart() && userPreference;
        }
        else if (isFCT()) {
            return qli.isPvuPart();
        }
        
        return false;
    }
}