package com.ibm.dsw.quote.promotion.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.promotion.config.PromotionKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuotePromotionContract</code>
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: 2010-10-20
 */
public class QuotePromotionContract extends QuoteBaseContract {
	
	 public void load(Parameters parameters, JadeSession session) {
	        super.load(parameters, session);
	        this.promotionValues = parameters.getParameterWithMultiValues(PromotionKeys.PARAM_PRMTN_VALUES);
	    }
	
	private String webQuoteNum = null;
    
    /**
     * @return Returns the quoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
	private String promotionNum = null;

	public String getPromotionNum() {
		return promotionNum;
	}

	public void setPromotionNum(String promotionNum) {
		this.promotionNum = promotionNum;
	}
	
	private String quoteTxtId = null;
	
    public String getQuoteTxtId() {
		return quoteTxtId;
	}
	public void setQuoteTxtId(String quoteTxtId) {
		this.quoteTxtId = quoteTxtId;
	}

	private String targetAction = null;
    
    /**
     * @return Returns the targetAction.
     */
    public String getTargetAction() {
        return targetAction;
    }
    /**
     * @param targetAction The targetAction to set.
     */
    public void setTargetAction(String targetAction) {
        this.targetAction = targetAction;
    }
    
    private String[] promotionValues = null;

	public String[] getPromotionValues() {
		return promotionValues;
	}
	public void setPromotionValues(String[] promotionValues) {
		this.promotionValues = promotionValues;
	}
	
	private String updatePrmtnFlag = null;

	public String getUpdatePrmtnFlag() {
		return updatePrmtnFlag;
	}
	public void setUpdatePrmtnFlag(String updatePrmtnFlag) {
		this.updatePrmtnFlag = updatePrmtnFlag;
	}
	
	
    
}
