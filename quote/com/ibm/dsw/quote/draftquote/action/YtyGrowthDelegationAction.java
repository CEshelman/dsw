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
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * @author Tong Wang
 * 
 * Creation date: Feb 4, 2013
 */
public class YtyGrowthDelegationAction  extends PostPartPriceTabAction {
	
	
	private static final long serialVersionUID = 7413092252550652711L;

	@Override
	protected void innerPostPartPriceTab(ProcessContract contract,ResultHandler handler) throws QuoteException {
        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;
        PartPriceProcess process = PartPriceProcessFactory.singleton().create();
        process.applyYtyGrowthDelegation(ct);
    }
    
    
	
	 private boolean checkYtyGrowthDelegation(HashMap vMap,PostPartPriceTabContract ct) {
	        //validate the quote yty growth is a positive between 0 and 100 (inclusive).
		 
			if (StringUtils.isBlank(ct.getQuoteYty())) {
	            FieldResult fieldResult = new FieldResult();
	            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_YTY_PERCENT_NULL_MSG);
	            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.YTY);
	            vMap.put(DraftQuoteParamKeys.OVERALL_YTY_GROWTH, fieldResult);

	            addToValidationDataMap(ct, vMap);
	            
	            return false;
	        }
	        else {
	            try {
	                double discount = Double.parseDouble(ct.getQuoteYty());
		            if (discount < 0) {
		                FieldResult fieldResult = new FieldResult();
		                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_YTY_PERCENT_POSITIVE_MSG);
		                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.YTY);
		                vMap.put(DraftQuoteParamKeys.OVERALL_YTY_GROWTH, fieldResult);
		
		                addToValidationDataMap(ct, vMap);
		                
		                return false;
		            }
//		            else if (discount > 100) {
//		                FieldResult fieldResult = new FieldResult();
//		                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_YTY_PERCENT_RANGE_MSG);
//		                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.YTY);
//		                vMap.put(DraftQuoteParamKeys.OVERALL_YTY_GROWTH, fieldResult);
//		
//		                addToValidationDataMap(ct, vMap);
//		                
//		                return false;
//		            }
	            }
	            catch (Exception e) {
	                FieldResult fieldResult = new FieldResult();
	                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.QUOTE_YTY_PERCENT_MSG);
	                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.YTY);
	                vMap.put(DraftQuoteParamKeys.OVERALL_YTY_GROWTH, fieldResult);

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
	        if (!checkYtyGrowthDelegation(vMap,ct)) return false;

	        try {
	            if (!validator.validate(contract,false)) {
	                return false;
	            }

	        } catch (QuoteException e) {
	            logContext.fatal(this, "validate quote yty growth delegation data error:" + e.getMessage());
	            return false;
	        }

	        return true;
	    }    
}
