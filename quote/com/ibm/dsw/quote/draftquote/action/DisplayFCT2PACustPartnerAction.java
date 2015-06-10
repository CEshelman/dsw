package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrateRequest_Impl;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DisplayFCT2PACustPartnerContract;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcess;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DisplayFCT2PACustPartnerAction<code> class.
 *    
 * @author: zhoujunz@cn.ibm.com
 * 
 * Creation date: 2012-5-23
 */

public class DisplayFCT2PACustPartnerAction extends BaseContractActionHandler {


	private static final long serialVersionUID = -8801375449314882214L;
	static final LogContext logContext = LogContextFactory.singleton()
	.getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.draftQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return "";
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	
    	MigrateRequest migrateResqt = new MigrateRequest_Impl();
    	
    	DisplayFCT2PACustPartnerContract cpContract = (DisplayFCT2PACustPartnerContract) contract;
    	String migrationReqNum= cpContract.getMigrationReqNum();
		logContext.debug(this, "migrationReqNum := " + migrationReqNum);
        if (migrationReqNum != null) {
        	MigrationRequestProcess process = MigrationRequestProcessFactory.singleton().create();
        	migrateResqt=process.getMigrtnReqDetailByReqNum(migrationReqNum);
         handler.addObject(DraftQuoteParamKeys.CA_NUM, migrateResqt.getOrginalCANum());
    	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, cpContract.getMigrationReqNum());
        }

        if (migrationReqNum == null ) {
           // if (migrationReqNum == null || sapCustNum == null) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        }else {
            handler.setState(DraftQuoteStateKeys.STATE_FCT2PA_CUST_PARTNER);
        }
      
        handler.addObject(ParamKeys.PARAM_MIGRATION_REQUEST_OBJECT, migrateResqt);
        addContractToResult(contract, handler);

        return handler.getResultBean();
    }


    protected boolean validate(ProcessContract contract) {
        return super.validate(contract);
    }

   

  
}

