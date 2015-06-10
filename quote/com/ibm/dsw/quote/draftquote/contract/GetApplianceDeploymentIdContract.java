package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GetApplianceDeploymentIdContract<code> class.
 *    
 * @author: xuyaojia@cn.ibm.com
 * 
 * Creation date: Jan 13, 2014
 */

public class GetApplianceDeploymentIdContract extends
PostDraftQuoteBaseContract {

	private String applncMachineType;
	private String applncMachineModel;
	private String applncSerialNumber;
	
	public String getApplncMachineType() {
		return applncMachineType;
	}
	
	public void setApplncMachineType(String applncMachineType) {
		this.applncMachineType = applncMachineType;
	}

	public String getApplncMachineModel() {
		return applncMachineModel;
	}

	public void setApplncMachineModel(String applncMachineModel) {
		this.applncMachineModel = applncMachineModel;
	}

	public String getApplncSerialNumber() {
		return applncSerialNumber;
	}

	public void setApplncSerialNumber(String applncSerialNumber) {
		this.applncSerialNumber = applncSerialNumber;
	}

	public void load(Parameters parameters, JadeSession session) {
    	super.load(parameters, session);
    	if(parameters.getParameter(DraftQuoteParamKeys.APPLNC_MTM_TYPE) != null) {
    		this.setApplncMachineType(parameters.getParameterAsString(DraftQuoteParamKeys.APPLNC_MTM_TYPE));
    	}
    	if(parameters.getParameter(DraftQuoteParamKeys.APPLNC_MTM_MODEL) != null) {
    		this.setApplncMachineModel(parameters.getParameterAsString(DraftQuoteParamKeys.APPLNC_MTM_MODEL));
    	}
    	if(parameters.getParameter(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER) != null) {
    		this.setApplncSerialNumber(parameters.getParameterAsString(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER));
    	}
    }
}
