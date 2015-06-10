package com.ibm.dsw.quote.draftquote.process;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.FctToPaMigrationDetailReturn;
import DswSalesLibrary.FctToPaMigrationOutput;
import DswSalesLibrary.IdocResponse;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrationFailureLineItem;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.service.MigrationCreateServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MigrationRequestCustProcess_Impl</code> class is abstract implementation of
 * PartPriceProcess.
 * 
 * 
 * @author <a href="mmzhou@cn.ibm.com">Tyler Zhou </a> <br/>
 * 
 * Creation date: 2012-05-24
 */
public abstract class MigrationRequestProcess_Impl extends TopazTransactionalProcess implements MigrationRequestProcess {

    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    private static final int length = 4000;
    private static final int SAPIDocLen = 16;

    public MigrateRequest getMigrtnReqDetailByReqNum(String requestNum) throws QuoteException{
    	MigrateRequest request = null;
        try {
            this.beginTransaction();
            
            request = this.getMigrtnReqByReqNum(requestNum);
            
            if(StringUtils.isNotBlank(request.getSoldToCustNum())){
            	// get customer information
            	request.setCustomer(this.getCustomerInfoByRequest(request));
            }

            if(StringUtils.isNotBlank(request.getSoldToCustNum())){
            	// get reseller information
            	request.setReseller(this.getResellerInfoByRequest(request));
            	
            }

            if(StringUtils.isNotBlank(request.getSoldToCustNum())){
            	// get payer information
            	request.setPayer(this.getPayerInfoByRequest(request));
            }
            
            this.commitTransaction();

        } catch (TopazException tce) {
        	logContext.error(this, tce.getMessage());
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return request;
        
    }
    /**
     * Only get request base
     * @param requestNum
     * @return
     * @throws QuoteException
     */
    public MigrateRequest getMigrtnReq(String requestNum) throws QuoteException
    {
    	MigrateRequest request = null;
        try {
            this.beginTransaction();
            
            request = this.getMigrtnReqByReqNum(requestNum);
            
            this.commitTransaction();

        } catch (TopazException tce) {
        	logContext.error(this, tce.getMessage());
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return request;
    }
    
    public abstract MigrateRequest getMigrtnReqByReqNum(String requestNum) throws QuoteException;
    
    public abstract Customer getCustomerInfoByRequest(MigrateRequest request) throws QuoteException;

    public abstract Partner getResellerInfoByRequest(MigrateRequest request) throws QuoteException;
    

    public abstract Partner getPayerInfoByRequest(MigrateRequest request) throws QuoteException;
    
    public abstract void updateMigrateInfByRequestNum(MigrateRequest request ,String userId) throws QuoteException;
    
    /**
     *  Call FCT to PA RFC, if get error throws exception, if success, update  sapIDocNum information, if fail, save failure information.
     */
    public boolean callServicesToFCTToPAMigration(String userId,MigrateRequest migrateRequest,Locale locale)  throws QuoteException, WebServiceException {
		try {
		    this.beginTransaction();
	        MigrationCreateServiceHelper migrationService = new MigrationCreateServiceHelper();
	        FctToPaMigrationOutput fctToPaMigrationOutput = migrationService.callFCTToPAMigrationService(migrateRequest);
	        
	        IdocResponse  headerReturn = fctToPaMigrationOutput.getHeaderReturn();
	        String headErrorInfo = headerReturn.getReturnCode() + "%" + headerReturn.getReturnMessage();
	        if (headerReturn.getErrorFlag()
	        		&& !QuoteConstants.MAPPING_ERROR_CODE.equalsIgnoreCase(headerReturn.getReturnCode())
	        		&& !QuoteConstants.BILLING_FREQ_ERROR_CODE.equalsIgnoreCase(headerReturn.getReturnCode())){
		        	this.updateMigrateRequestSubmission(1, "", migrateRequest.getRequestNum(), userId, "", headErrorInfo);
		        	throw new WebServiceFailureException(headerReturn.getReturnMessage(), headerReturn.getReturnCode());
	        } 
	        boolean isSuccessFlag = false;
	        if (StringUtils.isBlank(headerReturn.getReturnCode()) && StringUtils.isNotBlank(headerReturn.getIdocNumber())){
	        	isSuccessFlag = true;
	        } 
	        logContext.info(this, "Migration request["+migrateRequest.getRequestNum()+"] isSuccessFlag:"+isSuccessFlag);
	        logContext.info(this, "Migration request["+migrateRequest.getRequestNum()+"] return code:"+headerReturn.getReturnCode());
		    if(isSuccessFlag){
		    	String sapIDocNum = StringUtils.leftPad(headerReturn.getIdocNumber(), SAPIDocLen, "0");
		    	this.updateMigrateRequestSubmission(0, sapIDocNum, migrateRequest.getRequestNum(), userId, "", "");
		    	migrateRequest.setSapIDocNum(sapIDocNum);
		    } else {
		    	List<MigratePart> parts = migrateRequest.getParts();
		    	FctToPaMigrationDetailReturn[] partsReturn = fctToPaMigrationOutput.getDetailReturns();
		    	List<MigrationFailureLineItem> items = new ArrayList<MigrationFailureLineItem>();
		    	StringBuffer buffer = new StringBuffer();
		    	if(partsReturn != null && partsReturn.length >= 1){
			    	for(int i = 0; i < partsReturn.length ;i++){
			    		MigrationFailureLineItem item = new MigrationFailureLineItem();
			    		for(MigratePart part : parts){
			    			if(part.getSeqNum() == partsReturn[i].getItemNumber()){
					    		buffer.append(partsReturn[i].getErrorCode() + "#");
					    		item.setPartNum(part.getPartNum());
			    				buffer.append(partsReturn[i].getErrorMessage() + "#");
			    				item.setPartDesc(part.getPartDesc());
					    		buffer.append(partsReturn[i].getItemNumber() + "%");
					    		item.setReason(partsReturn[i].getErrorMessage());
			    				break;
			    			}
			    		}
			    		items.add(item);
			    	}		    		
		    	}
		    	if(QuoteConstants.BILLING_FREQ_ERROR_CODE.equalsIgnoreCase(headerReturn.getReturnCode()) && parts != null && parts.size() >= 1){
		    		MigrationFailureLineItem itemBill = new MigrationFailureLineItem();
		    		itemBill.setPartNum(parts.get(0).getPartNum());
		    		itemBill.setPartDesc(parts.get(0).getPartDesc());
		    		itemBill.setReason(this.getMessageForBillingFreq(migrateRequest.getBillingFreq(), locale));
		    		items.add(itemBill);
		    		headErrorInfo = headerReturn.getReturnCode() + "%" + getMessageForBillingFreq(migrateRequest.getBillingFreq(),locale);
		    	}
		    	migrateRequest.setLineItems(items);
		    	List<String> partLenList = new ArrayList<String>();
		    	partLenList = this.splitStringUtil(partLenList, buffer.toString(), length);
		    	for(String partInfos:partLenList){
		    		this.updateMigrateRequestSubmission(1, "", migrateRequest.getRequestNum(), userId, partInfos,headErrorInfo);
		    	}
		    }
			this.commitTransaction();
			return isSuccessFlag;
		} catch (TopazException e) {
		    logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		    throw new QuoteException(e);
		} catch (WebServiceException e) {
		    logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		    throw e;
		} finally {
		    this.rollbackTransaction();
		}
	}
    
    public String getMessageForBillingFreq(String billingFreq,Locale locale)throws TopazException{
		return  getI18NString(DraftQuoteMessageKeys.MSG_BILLING_FREQUENCE, locale)+ " " +
				getBillingFrquence(billingFreq.trim())+ " " +
				getI18NString(DraftQuoteMessageKeys.MSG_BILLING_FREQUENCE_NOT_VALID1, locale)+
				getI18NString(DraftQuoteMessageKeys.MSG_BILLING_FREQUENCE_NOT_VALID2, locale);
    }
   
    public String getBillingFrquence(String billFrequence) throws TopazException {
		Map<String, BillingOption> billingOptionMap = BillingOptionFactory.singleton().getBillingOptionMap() ;
		return billingOptionMap.get(billFrequence).getCodeDesc();
    }
    
    protected String getI18NString(String key , Locale locale) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        Object i18nValue = appCtx.getI18nValue(BaseI18NBundleNames.QUOTE_BASE, locale, key);
        if (i18nValue instanceof String) {
            return (String) i18nValue;
        } else {
            return key;
        }
    }
    
    /**
     * Split string into some segments per the length
     * @param lists
     * @param string
     * @param length
     * @return
     */
    public List<String> splitStringUtil(List<String> lists,String string,int length){
    	char splitChar = '%';
    	int index = 0;
    	for (int i = length ; i > 0 ; i--){
    		char currChar;
    		if (string.getBytes().length > length){
    			currChar = string.charAt(i);
    		} else {
    			lists.add(string);
    			break;
    		}
    		if(currChar == splitChar){
    			index = i;
    			lists.add(string.substring(0, index+1));
    			string = string.substring(index+1);
    			lists = splitStringUtil(lists,string,length);
    			break;
    		}
    	}
    	return lists;
    }
    

    public void updateNewPAWebQuote(String userId, String webQuoteNum, String fctToPaMigrationFlag)throws QuoteException{

        try {
            this.beginTransaction();
            updateNewPAWebQuoteImpl(userId, webQuoteNum, fctToPaMigrationFlag);
            
            this.commitTransaction();

        } catch (TopazException tce) {
        	logContext.error(this, tce.getMessage());
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public abstract void updateNewPAWebQuoteImpl(String userId, String webQuoteNum, String fctToPaMigrationFlag)throws QuoteException;
    
    
}
