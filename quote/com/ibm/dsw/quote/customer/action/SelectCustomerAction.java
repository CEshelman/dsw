package com.ibm.dsw.quote.customer.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrateRequest_Impl;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.contract.CustomerSearchContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcess;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SelectCustomerAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 8, 2007
 */

public class SelectCustomerAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        CustomerSearchContract customerContract = (CustomerSearchContract) contract;
        String creatorId = customerContract.getUserId();
        String customerNum = customerContract.getSiteNumber();
        String contractNum = customerContract.getAgreementNumber();
        String currency = customerContract.getCurrency();
        String endUserFlag = customerContract.getEndUserFlag();
        boolean returnStatus = false;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        QuoteRightColumn quoteRightColumn = process.getQuoteRightColumnInfo(creatorId);
        if (quoteRightColumn == null) {
            logContext.debug(this, "can't find session quote by creatorId: " + creatorId);
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        } else {
    
        	if(customerContract.getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
              	MigrateRequest migrateResqt = new MigrateRequest_Impl();
        	 	MigrationRequestProcess migrationReqProcess = MigrationRequestProcessFactory.singleton().create();
            	migrateResqt.setRequestNum(customerContract.getMigrationReqNum());
            	migrateResqt.setSoldToCustNum(customerNum);
            	migrateResqt.setReslCustNum("");
            	migrateResqt.setPayerCustNum("");
            	migrateResqt.setFulfillmentSrc("");
            	migrateResqt.setLob("");
				contractNum = contractNum == null ? "" : contractNum;
            	migrateResqt.setSapCtrctNum(contractNum);
            	migrationReqProcess.updateMigrateInfByRequestNum(migrateResqt,customerContract.getUserId() );
            	
	        	handler.addObject(DraftQuoteParamKeys.PAGE_FROM, customerContract.getPageFrom());
	          	handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, customerContract.getMigrationReqNum());
	            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
	                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_FCT2PA_CUST_PARTNER));
        	}else{
	            process.updateQuoteHeaderCustInfo(creatorId, null, customerNum, contractNum, -1, currency,endUserFlag);
	            // handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_DRAFT_QUOTE);
	
	            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
	                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
        	}
            handler.setState(StateKeys.STATE_REDIRECT_ACTION);
            handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
        }
        
        /**
         * Erase EC data for ELA contract
         */
        if(StringUtils.equalsIgnoreCase("1", customerContract.getEraseECFlag())
        		&& StringUtils.equalsIgnoreCase(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO, customerContract.getQuoteUserSession().getAudienceCode())){
        	process.eraseECData(creatorId);
        }
        
        return handler.getResultBean();
    }

    // to validate that customer number is in the URL
    protected boolean validate(ProcessContract contract) {
        CustomerSearchContract cusomerContract = (CustomerSearchContract) contract;
        if (cusomerContract != null) {
            String customerNum = cusomerContract.getSiteNumber();
            if (StringUtils.isNotBlank(customerNum)) {
                return super.validate(cusomerContract);
            } else
                return false;
        } else
            return false;

    }
    
}
