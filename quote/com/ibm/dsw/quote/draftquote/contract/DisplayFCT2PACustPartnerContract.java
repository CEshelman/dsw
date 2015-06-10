package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2012 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DisplayFCT2PACustPartnerContract<code> class.
 *    
 * @author: zhoujunz@cn.ibm.com
 * 
 * Creation date: 2012-5-23
 */

public class DisplayFCT2PACustPartnerContract extends QuoteBaseCookieContract {
    
	private  String migrationReqNum = "";
	private String fulfillcode;
	

    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        migrationReqNum = (String) parameters.getParameter(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
        fulfillcode = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_FUL_FILL_SRC);
    }

	public String getMigrationReqNum() {
		return migrationReqNum;
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getFulfillcode() {
		return fulfillcode;
	}

	public void setFulfillcode(String fulfillcode) {
		this.fulfillcode = fulfillcode;
	}
    
   
}
