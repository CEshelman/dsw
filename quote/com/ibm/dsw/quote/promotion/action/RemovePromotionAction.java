package com.ibm.dsw.quote.promotion.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.exception.RowExistingException;
import com.ibm.dsw.quote.promotion.contract.QuotePromotionContract;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcess;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RemovePromotionAction</code>
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: 2010-10-20
 */
public class RemovePromotionAction extends BaseContractActionHandler {
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		QuotePromotionContract ct = (QuotePromotionContract) contract;

		try {
			QuotePromotionProcess process = QuotePromotionProcessFactory.singleton()
					.create();
			process.removeQuotePromotion(ct.getWebQuoteNum(), ct.getQuoteTxtId(),0);
		} catch (Exception e) {
			logContext
					.error(this, "Remove Promotion num Error :" + e.getMessage());
			throw new QuoteException(e);
		}

		// redirect to sales info tab action
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
				.getURLForAction(ct.getTargetAction()));
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);

		return handler.getResultBean();
	}
	
	 /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.action.BaseActionHandlerAdapter#validate(com.ibm.ead4j.common.contract.ProcessContract)
     */
    protected boolean validate(ProcessContract contract) {
    	return super.validate(contract);
    }
}
