package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrateRequest_Impl;
import com.ibm.dsw.quote.draftquote.contract.DisplayFCT2PACustPartnerContract;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcess;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PostDisplayFCT2PACustPartnerAction<code> class.
 *    
 * @author: zhoujunz@cn.ibm.com
 * 
 * Creation date: 2012-5-23
 */

public class PostDisplayFCT2PACustPartnerAction extends BaseContractActionHandler {


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
    	String userId=cpContract.getUserId();
		logContext.debug(this, "migrationReqNum := " + migrationReqNum);
		logContext.debug(this, "userId := " + userId);
       
   /*     String redirectURL = generateRedirectURL(cpContract);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, baseContract.getForwardFlag());*/
     //   handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);

        try {
            // To post current tab's inputs.
            TransactionContextManager.singleton().begin();
            if (migrationReqNum != null) {
            	MigrationRequestProcess process = MigrationRequestProcessFactory.singleton().create();
        		migrateResqt=process.getMigrtnReqDetailByReqNum(migrationReqNum);
            	migrateResqt.setRequestNum(migrationReqNum);
            	migrateResqt.setSoldToCustNum("");
            	migrateResqt.setReslCustNum("");
            	migrateResqt.setPayerCustNum("");
            	migrateResqt.setFulfillmentSrc(cpContract.getFulfillcode());
            	migrateResqt.setLob(migrateResqt.getLob());
            	migrateResqt.setSapCtrctNum("");
            	//update FULFILLMENT_SRC column
            	process.updateMigrateInfByRequestNum(migrateResqt, userId);
            }
            TransactionContextManager.singleton().commit();
            
        } catch (TopazException e) {
        	logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException te) {
                logContext.error(this, te, "problems raised when doing rollback ");
            }
        }

        // Handle action button processes.

        return handler.getResultBean();
        
    }


    protected boolean validate(ProcessContract contract) {
        return super.validate(contract);
    }

   

  
}

