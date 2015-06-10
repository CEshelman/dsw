package com.ibm.dsw.quote.draftquote.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.action.validator.PartPriceUIValidator;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 *
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * 
 * @author xiuliw@cn.ibm.com
 *
 * 2007-8-14
 */
public class ApplyQuoteDiscountAction extends PostPartPriceTabAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    @Override
	protected void innerPostPartPriceTab(ProcessContract contract,
            ResultHandler handler) throws QuoteException {
        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;

        if (ct.getItems().values().size() == 0) {
            logContext.debug(this, "no parts in the quote, no need to perform the post");
            return;

        }

        if (ct.isRenwalQuote() && !ct.isRqEditable()) {
            logContext.debug(this, "Renewal Qutoe, but not editable");
            return;
        }

        PartPriceProcess process;
        process = PartPriceProcessFactory.singleton().create();

        process.applyDiscount(ct);
    }

    private boolean checkMainDiscount(HashMap vMap,PostPartPriceTabContract ct) {
        //validate the quote discount is a positive between 0 and 100 (inclusive).
		if (StringUtils.isBlank(ct.getQuoteDiscountPercent())) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_DISCOUNT_PERCENT_NULL_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.QUOTE_DISC_PCT);
            vMap.put(DraftQuoteParamKeys.QUOTE_DIS_PER_INPUT, fieldResult);

            addToValidationDataMap(ct, vMap);
            
            return false;
        }
        else {
            try {
                double discount = Double.parseDouble(ct.getQuoteDiscountPercent());
	            if (discount < 0) {
	                FieldResult fieldResult = new FieldResult();
	                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_DISCOUNT_PERCENT_POSITIVE_MSG);
	                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.QUOTE_DISC_PCT);
	                vMap.put(DraftQuoteParamKeys.QUOTE_DIS_PER_INPUT, fieldResult);
	
	                addToValidationDataMap(ct, vMap);
	                
	                return false;
	            }
	            else if (discount > 100) {
	                FieldResult fieldResult = new FieldResult();
	                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_DISCOUNT_PERCENT_RANGE_MSG);
	                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.QUOTE_DISC_PCT);
	                vMap.put(DraftQuoteParamKeys.QUOTE_DIS_PER_INPUT, fieldResult);
	
	                addToValidationDataMap(ct, vMap);
	                
	                return false;
	            }
            }
            catch (Exception e) {
                FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_DISCOUNT_PERCENT_MSG);
                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.QUOTE_DISC_PCT);
                vMap.put(DraftQuoteParamKeys.QUOTE_DIS_PER_INPUT, fieldResult);

                addToValidationDataMap(ct, vMap);
                
                return false;
            }
        }
        
        return true;
    }

    @Override
	protected boolean innerValidate(ProcessContract contract){
        PartPriceUIValidator validator = PartPriceUIValidator.create(this, (PostPartPriceTabContract) contract);
        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;
        
        HashMap vMap = new HashMap();
        if (!checkMainDiscount(vMap,ct)) return false;

        try {
            if (!validator.validate(contract,false)) {
                return false;
            }

        } catch (QuoteException e) {
            logContext.fatal(this, "validate quote discount percent data error:" + e.getMessage());
            return false;
        }

        return true;
    }    
}