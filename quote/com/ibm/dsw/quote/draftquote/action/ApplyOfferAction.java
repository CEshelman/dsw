package com.ibm.dsw.quote.draftquote.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.OfferPriceException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.QuoteMessageKeys;
import com.ibm.dsw.quote.common.config.QuoteParamKeys;
import com.ibm.dsw.quote.draftquote.action.validator.PartPriceUIValidator;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Creation date: Aug 8, 2007
 */
public class ApplyOfferAction extends PostPartPriceTabAction {
    @Override
	protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {

        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        PartPriceProcess process = PartPriceProcessFactory.singleton().create();
        try{
            process.applyOffer(ct);
        }catch(OfferPriceException e){
            String  errorMessage = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale());
            MessageBean mBean = MessageBeanFactory.create();
            mBean.addMessage(errorMessage, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
        }

    }   
    
    @Override
	protected String getValidationForm() {
    	return "applyOfferForm";
    }    

    private boolean checkOfferPrice(HashMap vMap,PostPartPriceTabContract ct) {
        //validate the offerPrice is not null and  must be a positive number or zero.
		if (StringUtils.isBlank(ct.getOfferPrice())) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, QuoteMessageKeys.CURRENT_OFFER_NULL_MSG);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.APPLY_OFFER_TXT);
            vMap.put(QuoteParamKeys.APPLY_OFFER_INPUT, fieldResult);

            addToValidationDataMap(ct, vMap);
            
            return false;
        }
        else {
            try {
                double offerPrice = Double.parseDouble(ct.getOfferPrice());
	            if (offerPrice < 0) {
	                FieldResult fieldResult = new FieldResult();
	                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.PRICE_POSITIVE_MSG);
	                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.APPLY_OFFER_TXT);
	                vMap.put(QuoteParamKeys.APPLY_OFFER_INPUT, fieldResult);
	
	                addToValidationDataMap(ct, vMap);
	                
	                return false;
	            }
            }
            catch (Exception e) {
                FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, QuoteMessageKeys.PRICE_NUMERIC_MSG);
                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.APPLY_OFFER_TXT);
                vMap.put(QuoteParamKeys.APPLY_OFFER_INPUT, fieldResult);

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
        if (!checkOfferPrice(vMap,ct)) return false;

        try {
            if (!validator.validate(contract,false)) {
                return false;
            }

        } catch (QuoteException e) {
            logContext.fatal(this, "validate offer price data error:" + e.getMessage());
            return false;
        }

        return true;
    }    
}
