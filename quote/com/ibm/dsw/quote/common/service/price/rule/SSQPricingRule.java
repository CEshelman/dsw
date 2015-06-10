package com.ibm.dsw.quote.common.service.price.rule;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.submittedquote.util.SubmittedDateComparator;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jul 3, 2007
 */

public class SSQPricingRule extends PricingRule {
    boolean needBestPricing = false;

//    boolean entitledPriceChanged = false;

    private SubmittedDateComparator dateComparator;

    public SSQPricingRule(PricingRequest pr, boolean bestPricing, SubmittedDateComparator dateComparator) {

        super(pr);
        this.needBestPricing = bestPricing;
        this.dateComparator = dateComparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.service.price.rule.PricingRule#execute(DswSalesLibrary.ItemOut[])
     */
    public void execute(ItemOut[] itemOut) throws TopazException {

        logContext.debug(this, "begin to use SSQ pricing rule...");

        for (int i = 0; i < itemOut.length; i++) {

            ItemOut item = itemOut[i];

            QuoteLineItem sapLineItem = this.getLineItem(item.getPartNum(), item.getItmNum().intValue());

            if (null == sapLineItem) {
                logContext.info(this, "Can't find sap line item for (" + item.getPartNum() + "," + item.getItmNum()
                        + ")");
                continue;
            }
            String errCode = item.getErrCode();
            //for eol parts, PWS will always return error code
            if(!QuoteCommonUtil.acceptPrice(item, sapLineItem)){
                logContext.info(this,"Sap return error for part " + item.getPartNum()+",Price will be kept");
                logContext.info(this,"Error Code =" + errCode);
                logContext.info(this,"Error Message =" + item.getErrMsg());
                continue;
            }
//            if(sapLineItem.getLocalUnitPrc() == null){
//                this.entitledPriceChanged = true;
//            }
//            else if (DecimalUtil.isNotEqual(item.getLclUnitPrc().doubleValue(), sapLineItem.getLocalUnitPrc().doubleValue())) {
//                this.entitledPriceChanged = true;
//                logContext.debug(this, "Entitled price changed");
//            }

            logContext.debug(this, "Begin to process (" + sapLineItem.getPartNum() + "," + sapLineItem.getSeqNum()
                    + ")");

            if (needBestPricing) {
                logContext.debug(this, "Need apply best pricing");
                this.applyBestPricing(sapLineItem, item);

            } else {
                logContext.debug(this, "Just copy new prices");
                this.setNewPrice(sapLineItem, item);
            }

        }

    }

    protected void applyBestPricing(QuoteLineItem sapItem, ItemOut item) throws TopazException {

        if (!isSapPartInfoChanged(sapItem) || hasContractLevelPricing()) {

            setNewPrice(sapItem, item);

            logContext.debug(this, "Best Pricing: exception to best pricing rule:(" + sapItem.getPartNum() + ","
                    + sapItem.getSeqNum() + ")");

            return;
        }

        if (isProrationMonthChanged(sapItem)) {
            setNewPrice(sapItem, item);
            logContext.debug(this, "Best Pricing:  proration month changed ,always use new price from engine");

        } else if ((sapItem.getLocalUnitPrc()==null) 
                || (item.getLclUnitPrc().doubleValue() < sapItem.getLocalUnitPrc().doubleValue())) {

            setNewPrice(sapItem, item);
            logContext.debug(this, "Best Pricing: Price decreased, use new price from engine");

        } else {
            logContext.debug(this, "Best Pricing: Price no changes, keep original price");
        }

    }

    private boolean isProrationMonthChanged(QuoteLineItem lineItem) {

        if (this.dateComparator == null) {
            return false;
        } else {
            return this.dateComparator.isDurationChanged(lineItem);
        }

    }

    private boolean isSapPartInfoChanged(QuoteLineItem lineItem) {

        if (this.dateComparator == null) {

            return false;

        } else {
            return this.dateComparator.isDateChanged(lineItem);
        }

    }

//    /**
//     * @return Returns the entitledPriceChanged.
//     */
//    public boolean isEntitledPriceChanged() {
//        return entitledPriceChanged;
//    }
}
