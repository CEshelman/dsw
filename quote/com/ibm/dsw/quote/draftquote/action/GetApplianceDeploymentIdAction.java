package com.ibm.dsw.quote.draftquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.GetApplianceDeploymentIdContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GetApplianceDeploymentIdAction<code> class.
 *    
 * @author: xuyaojia@cn.ibm.com
 * 
 * Creation date: Jan 13, 2014
 */

public class GetApplianceDeploymentIdAction extends
		BaseContractActionHandler {
	
	private static final long serialVersionUID = 1L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {	
		GetApplianceDeploymentIdContract getApplianceDeploymentIdContract = (GetApplianceDeploymentIdContract) contract;
		String applncMachineType = getApplianceDeploymentIdContract.getApplncMachineType();
		String applncMachineModel = getApplianceDeploymentIdContract.getApplncMachineModel();
		String applncSerialNumber = getApplianceDeploymentIdContract.getApplncSerialNumber();
		
		logContext.debug(this, "parameters: " + applncMachineType +", " + applncMachineModel + ", " + applncSerialNumber);
		String deploymentId = null;
		if (StringUtils.isNotEmpty(applncMachineType) && StringUtils.isNotEmpty(applncMachineModel) 
				&& StringUtils.isNotEmpty(applncSerialNumber)) {
			PartPriceProcess process = PartPriceProcessFactory.singleton().create();
			deploymentId = process.getApplianceDeploymentIdByMTM(applncMachineType, applncMachineModel, applncSerialNumber);	
		}		
		String message = "";
		if (deploymentId != null) {
			message = deploymentId;
		}
        handler.addObject(ParamKeys.PARAM_AJAX_OPER_STATUS, "1");
        handler.addObject(ParamKeys.PARAM_AJAX_OPER_MESS,  message);
		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_AJAX_OPER);
		return handler.getResultBean();
	}

}