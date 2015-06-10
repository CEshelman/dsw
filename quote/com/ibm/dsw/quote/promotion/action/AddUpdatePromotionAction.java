package com.ibm.dsw.quote.promotion.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.promotion.config.PromotionKeys;
import com.ibm.dsw.quote.promotion.contract.QuotePromotionContract;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcess;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AddPromotionAction</code>
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: 2010-10-20
 */
public class AddUpdatePromotionAction extends BaseContractActionHandler {
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		QuotePromotionContract ct = (QuotePromotionContract) contract;
		QuotePromotionProcess prmtnProcess = QuotePromotionProcessFactory.singleton()
		.create();
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		
		String webQuoteNum = ct.getWebQuoteNum();
		
		if (ct.getUpdatePrmtnFlag() != null && "1".equals(ct.getUpdatePrmtnFlag())) {
			List prmtnList = null;
			String[] prmtnValues = ct.getPromotionValues();
			try {
				prmtnList = prmtnProcess.findPromotionsByQuote(webQuoteNum);
				for (int i = 0; i < prmtnValues.length ; i++) {
            				String[] promotion = (String[]) prmtnList.get(i);
            				int quoteTxtId = Integer.valueOf(promotion[0]);
            				if (promotion[1].equals(prmtnValues[i])) {//user dose not change the promotion value
            					continue;
            				} else {//update quote promotion and record quote audit history
            					try {
            						prmtnProcess.updateQuotePromotion(webQuoteNum,
											ct.getUserId(), prmtnValues[i],
											quoteTxtId);
								} catch (Exception e) {
									logContext.error(this, "Update Promotion Error :" + e.getMessage());
									throw new QuoteException(e);
								}
								try {// record audit history
									quoteProcess.addQuoteAuditHist(webQuoteNum,
											null, ct.getUserId(), "UPDATE_PRMTN_NUM",
											promotion[1], prmtnValues[i]);
								} catch (Exception e) {
									logContext.error(this, "Add quote audit history Error :" + e.getMessage());
									throw new QuoteException(e);
								}
            				}
            	}
			} catch (Exception e) {
				logContext.error(this, "Get Promotion Error :" + e.getMessage());
				throw new QuoteException(e);
			}
		} else {
			try {
				prmtnProcess.insertQuotePromotion(webQuoteNum, ct.getUserId(), ct.getPromotionNum());
			} catch (Exception e) {
				logContext.error(this, "Add Promotion Error :" + e.getMessage());
				throw new QuoteException(e);
			}
			// redirect to sales info tab action
			handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil.getURLForAction(ct.getTargetAction()));
			
		}
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		return handler.getResultBean();
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.action.BaseActionHandlerAdapter#validate(com.ibm.ead4j.common.contract.ProcessContract)
     */
    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        } else {
            QuotePromotionContract ct = (QuotePromotionContract) contract;
            String promotionNum = ct.getPromotionNum();
            List promotionsList =  null;
            try {
            	promotionsList = QuotePromotionProcessFactory.singleton().create()
				.findPromotionsByQuote(ct.getWebQuoteNum());
			} catch (QuoteException e) {
				logContext.error(this, "Get Promotion Error :" + e.getMessage());
				return false;
			}
            
            if (ct.getUpdatePrmtnFlag() != null && "1".equals(ct.getUpdatePrmtnFlag())) {//for promotion update
            	String[] prmtnValues = ct.getPromotionValues();
            	
            	for (int i = 0; prmtnValues != null && i< prmtnValues.length ; i++) {
            		if (validatePrmtnNum(ct, prmtnValues[i])) {
            			for (int j = 0; j < promotionsList.size(); j++) {
            				String[] promotion = (String[]) promotionsList.get(j);
            				if ( i == j && promotion[1].equals(prmtnValues[i])) {//user dose not change the promotion value
            					continue;
            				} else if (i != j && promotion[1].equals(prmtnValues[i])) {//the changed value is duplicate with existing promotion
            					return false;
            				}
            			}
            		} else {
            			return false;
            		}
            	}
            	return true;
            } else {// for promotion add
            	if (validatePrmtnNum(ct, promotionNum)){
            		Iterator itr = promotionsList.iterator();
            		while (itr.hasNext()) {
            			String[] promotion = (String[])itr.next();
            			if(promotion[1].equals(promotionNum)) {
            				return false;
            			}
            		}
            	} else {
            		return false;
            	}
            }
            
            return true;
        }
    }

	private boolean validatePrmtnNum(QuotePromotionContract ct, String promotionNum) {
		if (StringUtils.isBlank(promotionNum)) {
		    HashMap vMap = new HashMap();
		    FieldResult fieldResult = new FieldResult();
		    fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, MessageKeys.MSG_QUPTE_PROMOTION_NUM_ERR);
		    fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.PROMOTION_NUM);
		    
		    vMap.put(PromotionKeys.PARAM_PROMOTIONNUM, fieldResult);
		    addToValidationDataMap(ct, vMap);
		    return false;
		}
		return true;
	}
    
    protected String getValidationForm() {
        return PromotionKeys.QUOTE_PROMOTIONN_FORM;
    }
}
