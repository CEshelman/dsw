package com.ibm.dsw.quote.draftquote.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceUnavailableException;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.ValidationMessageFactory;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.SubmitMigrationReqContract;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
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
 * This <code>SubmitMigrationReqAction<code> class.
 * This action is responsible for submitting FCT to PA migration request
 *    
 * @author: xiongxj@cn.ibm.com
 * 
 * Creation date: 2012-5-15
 */
public class SubmitMigrationReqAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 2541921284554135198L;
    protected Locale locale = null;
	@Override@SuppressWarnings("rawtypes")
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
			SubmitMigrationReqContract baseContract = (SubmitMigrationReqContract) contract;
		    locale = baseContract.getLocale();
		    String requestNum = baseContract.getMigrationReqNum();
		    String userId = baseContract.getUserId();
		    MigrateRequest migrateRequest = this.getMigrateRequestDetail(requestNum);
		    boolean isSuccessFlag = false;
			Map validResult = QuoteCapabilityProcessFactory.singleton().create().validateFCTToPAMigrationRequest(baseContract.getQuoteUserSession(), migrateRequest);
	        if (validResult != null && validResult.size() > 0){
	            return handleValidateResult(validResult, handler, locale);
	        }
	        
	        try {
	        	isSuccessFlag = MigrationRequestProcessFactory.singleton().create().callServicesToFCTToPAMigration(userId,migrateRequest,locale);

			} catch (WebServiceException e) {
				 if( e instanceof WebServiceUnavailableException){
					 // This case can resubmit migration request and view SaaS history ,such as network ,RFC service can not available
					 handler.setState(DraftQuoteStateKeys.STATE_RESUBMIT_MIGRATE_SCREEN);
					 MigrationRequestProcessFactory.singleton().create().updateMigrateRequestSubmission(2,"",requestNum,userId,"","");
				 }else {
					 // This case can only view SaaS history
					 handler.setState(DraftQuoteStateKeys.STATE_FCT2PA_MIGRATE_ERROR);
				 } 	  
				 	 handler.addObject(DraftQuoteParamKeys.CA_NUM, migrateRequest.getOrginalCANum());
				 	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, requestNum);
					 logContext.info(this, "FCT to PA migration request:"+requestNum+e.getMessage());
			         return handler.getResultBean();	
			}
			  handler.addObject(DraftQuoteParamKeys.CA_NUM, migrateRequest.getOrginalCANum());
			if (isSuccessFlag){
				handler.setState(DraftQuoteStateKeys.STATE_FCT2PA_CONFIRM_SCREEN);
		        handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, requestNum);
		        handler.addObject(ParamKeys.PARPM_SAP_CONFIRM_NUM, migrateRequest.getSapIDocNum());
			} else {
				handler.setState(DraftQuoteStateKeys.STATE_FCT2PA_FAIL_SCREEN);
				handler.addObject(ParamKeys.PARPM_MIGRATION_FAILURE_LINE_ITEMS, migrateRequest.getLineItems());
			}
	        return handler.getResultBean();
	}

	/**
	 * Get migrateRequest detail
	 * @param requestNum
	 * @return
	 * @throws QuoteException
	 */
    protected MigrateRequest getMigrateRequestDetail(String requestNum) throws QuoteException {
    	MigrateRequest migrateRequest = MigrationRequestProcessFactory.singleton().create().getMigrtnReqDetailByReqNum(requestNum);
    	return migrateRequest;
     }
    
    /**
     * You can override this method if needed.
     * @param validResult
     */
    @SuppressWarnings("rawtypes")
    protected ResultBean handleValidateResult(Map validResult, ResultHandler handler, Locale locale) {
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        
        String msgHead = getValidMsgHead(locale);
        messageBean.addMessage(msgHead, MessageKeys.LAYER_MSG_HEAD);

        Set keySet = validResult.keySet();
		Iterator iter = keySet.iterator();
        
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = validResult.get(key);
            String param = null;
            
            // if the value is a string, it will be considered as the message parameter.
            if (value != null && value instanceof String) {
                param = (String) value;
            }

            String message = ValidationMessageFactory.singleton().getValidationMessage(key, locale, param);
            messageBean.addMessage(message, MessageKeys.LAYER_MSG_ITEM);
        }

        return resultBean;
    }
    
    protected String getValidMsgHead(Locale locale) {
        return this.getI18NString(DraftQuoteMessageKeys.MIGRATION_FAILED_MSG, BaseI18NBundleNames.QUOTE_BASE, locale);
    }
}
