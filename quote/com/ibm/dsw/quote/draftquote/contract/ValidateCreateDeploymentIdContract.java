package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

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

public class ValidateCreateDeploymentIdContract extends
PostDraftQuoteBaseContract {

	private String seqNumber;
	private String deployAssociation;
	private String deployId;
	
	public String getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(String seqNumber) {
		this.seqNumber = seqNumber;
	}
	
	public String getDeployAssociation() {
		return deployAssociation;
	}

	public void setDeployAssociation(String deployAssociation) {
		this.deployAssociation = deployAssociation;
	}

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}
	
	public void load(Parameters parameters, JadeSession session) {
    	super.load(parameters, session);
    	if(parameters.getParameter(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION)!=null){
    		this.setDeployAssociation(parameters.getParameterAsString(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION));
    	}
    	if(parameters.getParameter(DraftQuoteParamKeys.APPLNC_DEPLOY_ID)!=null){
    		this.setDeployId(parameters.getParameterAsString(DraftQuoteParamKeys.APPLNC_DEPLOY_ID));
    	}
    	if(parameters.getParameter(DraftQuoteParamKeys.PARAM_QUOTE_LINE_ITEM_SEQ_NUM)!=null){
    		this.setSeqNumber(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_QUOTE_LINE_ITEM_SEQ_NUM));
    	}
    }
}
