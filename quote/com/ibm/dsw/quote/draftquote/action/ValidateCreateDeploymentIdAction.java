package com.ibm.dsw.quote.draftquote.action;

import java.util.Map;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.ValidateCreateDeploymentIdContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ValidateCreateDeploymentIdAction<code> class.
 *    
 * @author: xuyaojia@cn.ibm.com
 * 
 * Creation date: Des 5, 2013
 */

public class ValidateCreateDeploymentIdAction extends
		BaseContractActionHandler {

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {		
		ValidateCreateDeploymentIdContract validateCreateDeploymentIdContract = (ValidateCreateDeploymentIdContract) contract;
		String webQuoteNum = validateCreateDeploymentIdContract.getWebQuoteNum();
		String seqNum = validateCreateDeploymentIdContract.getSeqNumber();
		String deployAssociation = validateCreateDeploymentIdContract.getDeployAssociation();
		String deployId = validateCreateDeploymentIdContract.getDeployId();
		
		PartPriceProcess process = PartPriceProcessFactory.singleton().create();
		
		int quoteLineItemSeqNum = -1;
		if (seqNum != null) {
			quoteLineItemSeqNum = Integer.parseInt(seqNum);
		}
		int deployModelOption = -1;
		if (deployAssociation != null) {
			deployModelOption = Integer.parseInt(deployAssociation);
		}
		logContext.debug(this, "parameters: " + webQuoteNum +", " + seqNum + ", " + deployAssociation + ", " + deployId);
		Map result= process.validateOrCreateDeploymentId(webQuoteNum, quoteLineItemSeqNum, deployModelOption, deployId);	
		String status = null;
		String message = null;
		if ("1".equals(deployAssociation)) {
			status = "1";
			if (result != null && result.get("DEPLOYMT_ID_INVD") != null) {
				message = result.get("DEPLOYMT_ID_INVD").toString();
			}
		} else if ("2".equals(deployAssociation)) {
			status = "2";
			if (result != null && result.get("DEPLOYMT_ID") != null) {
				message = result.get("DEPLOYMT_ID").toString();
			}
		} else {
			// Empty
		}
		logContext.info(this, "Complete the validate or create ID operation.");
        handler.addObject(ParamKeys.PARAM_AJAX_OPER_STATUS, status);
        handler.addObject(ParamKeys.PARAM_AJAX_OPER_MESS,  message);
		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_AJAX_OPER);
		return handler.getResultBean();
	}

}