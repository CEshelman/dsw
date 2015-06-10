package com.ibm.dsw.quote.draftquote.action;

import java.util.HashMap;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.contract.ChangeAdditionalMaintContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code></code> class is 
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 19, 2007
 */

public class ChangeAdditionalMaintAction extends PostDraftQuoteBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
    	ChangeAdditionalMaintContract ct = (ChangeAdditionalMaintContract) contract;
    	try {
    		Integer iPartQty = null;
            Double dOverrideUnitprice = null;
            double iDiscPct = 0;
    		Quote quote = null;
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quote = quoteProcess.getDraftQuoteBaseInfo(ct.getUserId());
			String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();			
			PartPriceProcess process = PartPriceProcessFactory.singleton().create();
			if (ct.getPartQty() != null && ct.getPartQty().trim().length() != 0) {
                if (!ct.getPartQty().equals("")) {
                	iPartQty = new Integer(Integer.parseInt(ct.getPartQty()));
                }
            }
            
            if (ct.getOverrideUnitPrc() != null && ct.getOverrideUnitPrc().trim().length() != 0) {                
                dOverrideUnitprice= Double.valueOf(ct.getOverrideUnitPrc());
            }
            
            if (ct.getLineDiscPct() != null && ct.getLineDiscPct().trim().length() != 0) {
            	iDiscPct = Double.parseDouble(ct.getLineDiscPct());
            }
            
			process.changeAdditionalMaint(webQuoteNum,ct.getPartNum(),ct.getSeqNum(),ct.getAdditionalYears(),
					iPartQty,ct.getManualSortSeqNum(),dOverrideUnitprice,iDiscPct,
					ct.getProrationFlag(),ct.getUserId());
    	} catch (TopazException e) {
            throw new QuoteException(e);
		} 
    }
    protected boolean validate(ProcessContract contract) {
    	ChangeAdditionalMaintContract ct = (ChangeAdditionalMaintContract) contract;
        if (!super.validate(contract)) {
            return false;
        }
        HashMap vMap = new HashMap();
        //validate quantity to be integer
        try {
            logContext.debug(this, "Quantity =" + ct.getPartQty());

            if (ct.getPartQty() != null && ct.getPartQty().trim().length() != 0) {
                if (!ct.getPartQty().equals("")) {
                    Integer.parseInt(ct.getPartQty());
                }

            }
        } catch (Exception e) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUANTITY_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.QTY_HDR);
            vMap.put(ct.getKey() + DraftQuoteParamKeys.quantitySuffix, fieldResult);

            addToValidationDataMap(contract, vMap);
            return false;
        }

        //validate override price to be double
        try {
            logContext.debug(this, "Override price =" + ct.getOverrideUnitPrc());
            if (ct.getOverrideUnitPrc() != null && ct.getOverrideUnitPrc().trim().length() != 0) {
                double tempOverridePrice = Double.parseDouble(ct.getOverrideUnitPrc());
            }
        } catch (Exception e) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.OVERRIDE_PRICE_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.OVERRIDE_PRICE_HDR);
            vMap.put(ct.getKey() + DraftQuoteParamKeys.overridePriceSuffix, fieldResult);
            addToValidationDataMap(contract, vMap);
            return false;
        }

        //validate discount percent to be double
        try {
            logContext.debug(this, "discount  =" + ct.getLineDiscPct());
            if (ct.getLineDiscPct() != null && ct.getLineDiscPct().trim().length() != 0) {
                double tempDiscountPercent = Double.parseDouble(ct.getLineDiscPct());
            }
        } catch (Exception e) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.DISCOUNT_PERCENT_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.DIS_PER_HDR);
            vMap.put(ct.getKey() + DraftQuoteParamKeys.discountPriceSuffix, fieldResult);

            addToValidationDataMap(contract, vMap);
            return false;
        }

        return true;
    }

}
