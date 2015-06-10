package com.ibm.dsw.quote.newquote.action;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteStateKeys;
import com.ibm.dsw.quote.newquote.contract.UploadSalesQuoteContract;
import com.ibm.dsw.quote.newquote.exception.NewQuotHasBeenLockedException;
import com.ibm.dsw.quote.newquote.exception.NewQuotHasBeenModifiedException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteExistBothSWSaaSPartsException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidAcquisitionException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCustCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidLOBException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidStardEndDateException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidStartEndDateDurationException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidTermException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteUnSupportedLOBException;
import com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcess;
import com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcessFactory;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetUtil;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UploadQuoteActionHandler</code> receives a Excel XML file and
 * build draft quote based on the spreadsheet.
 * 
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: Mar 07, 2007
 */

public class UploadQuoteActionHandler extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.QuoteRightColumnBaseAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract,
            ResultHandler handler) throws  ResultBeanException, QuoteException {
        UploadSalesQuoteContract uploadSalesQuoteContract = (UploadSalesQuoteContract) contract;
        QuoteUserSession quoteUserSession = uploadSalesQuoteContract.getQuoteUserSession();
        String creatorId = uploadSalesQuoteContract.getUserId();
        Locale loc = uploadSalesQuoteContract.getLocale();
        boolean isXMLFile = true;
        boolean validNativeExcel = true;
        boolean validXML = true;
        String returnedMsgKey = null;
        boolean isToImportNewQuote = NewQuoteParamKeys.UPLOAD_OPTOIN_VALUE_NEW_QUOTE.equals(uploadSalesQuoteContract.getUploadOption());
        boolean isToUnlockQuote = NewQuoteParamKeys.UPLOAD_OPTOIN_VALUE_UNLOCK_QUOTE.equals(uploadSalesQuoteContract.getUploadOption());
        boolean isPGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
        
        SpreadSheetQuote spreadSheetQuote = null;

        /* Retrieve uploaded file from UploadSalesQuoteContract */
        File file = uploadSalesQuoteContract.getUploadedFile();
        
        if(null == file){
        	/* Check whether spreadsheet file had been selected*/
        	returnedMsgKey = NewQuoteMessageKeys.UPLOAD_WARNING_NO_SPREADSHEET_SELECTED;
        	return handleInvalidSpreadSheet(returnedMsgKey, loc, handler, creatorId);
        }
        
        SpreadSheetUtil util = SpreadSheetUtil.singleton();

        /* Check whether the file is XML spreadsheet or Native Excel spreadsheet */
        isXMLFile = util.isXMLFile(file);
        if (isXMLFile) {
            /* Check whether the XML Excel is well formed */
            validXML = util.isValidXML(file);
            if (!validXML) {
                returnedMsgKey = NewQuoteMessageKeys.INVALID_XML_FORMAT;
                file.delete();
                return handleInvalidSpreadSheet(returnedMsgKey, loc, handler, creatorId);
            }            
        } else {
            /* Check whether the Native Excel is well formed */
            validNativeExcel = util.isValidNativeExcel(file);
            if (!validNativeExcel) {
                returnedMsgKey = NewQuoteMessageKeys.INVALID_NATIVE_EXCEL_FORMAT;
                file.delete();
                return handleInvalidSpreadSheet(returnedMsgKey, loc, handler, creatorId);
            }
        }
        
        //extract quote data from spreasheet
        long start = System.currentTimeMillis();
        if (isXMLFile) {
            spreadSheetQuote = util.importXMLSpreadSheet(file);
        } else {
            spreadSheetQuote = util.importNativeExcelSpreadSheet(file);
        }
        
        if(null == spreadSheetQuote) {
            if (isXMLFile) {
                returnedMsgKey = NewQuoteMessageKeys.INVALID_DATA_CONTENT;
            } else {
                returnedMsgKey = NewQuoteMessageKeys.INVALID_NATIVE_EXCEL_DATA_CONTENT;
            }
        	file.delete();
        	return handleInvalidSpreadSheet(returnedMsgKey, loc, handler, creatorId);
        }
        
        try {
            UploadSalesQuoteProcess uProcess = UploadSalesQuoteProcessFactory.singleton().create();
            spreadSheetQuote.setToImportNewQuote(isToImportNewQuote);
            spreadSheetQuote.setToUnlockQuote(isToUnlockQuote);
            spreadSheetQuote.setPGSFlag(isPGSFlag);
            
            uProcess.importSpreadSheetQuote(spreadSheetQuote, creatorId);

            //redirect invalid data to cust & partner tab url
            String redirectURL = addInvalidDataToURL(spreadSheetQuote);
            logContext.debug(this, "redirectURL: " + redirectURL);
            handler.addObject(NewQuoteParamKeys.PARAM_REDIRECT_URL,redirectURL);
            file.delete();
            long end = System.currentTimeMillis();
            logContext.debug(this, "import spreadsheet cost : " + (end - start) + " ms");
        } catch (NewQuoteInvalidCntryCurrencyException cure) {
            return handleInvalidSpreadSheet(cure.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidCustCurrencyException custcure) {
            return handleInvalidSpreadSheet(custcure.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidLOBException lobe) {
            return handleInvalidSpreadSheet(lobe.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteUnSupportedLOBException lobe) {
        	return handleInvalidSpreadSheet(lobe.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidCntryException cntrye) {
        	return handleInvalidSpreadSheet(cntrye.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidAcquisitionException ace) {
                return handleInvalidSpreadSheet(ace.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidStardEndDateException date) {
            return handleInvalidSpreadSheet(date.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidStartEndDateDurationException dur) {
            return handleInvalidSpreadSheet(dur.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteInvalidTermException term) {
            return handleInvalidSpreadSheet(term.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuoteExistBothSWSaaSPartsException bothparts) {
            return handleInvalidSpreadSheet(bothparts.getMessageKey(), loc, handler, creatorId);
        } catch (NewQuotHasBeenLockedException locked) {
            String[] args = { spreadSheetQuote.getLockedBy() };
            return handleLockedSpreadSheet(locked.getMessageKey(), loc, handler, creatorId, args, true);
        } catch (NewQuotHasBeenModifiedException modified) {
            String[] args = null;
            String messageKey = NewQuoteMessageKeys.QUOTE_HAS_BEEN_MODIFIED_BY_YOURSELF;
            if(StringUtils.isNotBlank(spreadSheetQuote.getLockedBy())){
                args = new String[]{spreadSheetQuote.getLockedBy()};
                messageKey = modified.getMessageKey();;
            }            
            return handleLockedSpreadSheet(messageKey, loc, handler, creatorId, args, false);
        } catch (TopazException e) {
        	logContext.error(this,LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } 
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.QuoteRightColumnBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return DraftQuoteStateKeys.STATE_DISPLAY_SQ_CUST_PRTNR_TAB;
    }
    
    
    /**
     * Presents error messages for a invalid XML Excel spread sheet file on JSP.
     * @param errorMsgKey
     * @param locale 
     * @param handler
     * @return resultBean
     * @throws ResultBeanException
     */
    private ResultBean handleInvalidSpreadSheet(String errorMsgKey, Locale locale, ResultHandler handler, String creatorId) throws ResultBeanException{
        handler.setState(NewQuoteStateKeys.STATE_DISPLAY_UPLOAD_PAGE);
        try {
            // get session quote information for right column
            QuoteRightColumn sessionQuote = null;
            if (creatorId != null && !"".equals(creatorId)) {
                QuoteProcess process = QuoteProcessFactory.singleton().create();
                sessionQuote = process.getQuoteRightColumnInfo(creatorId);
            }
            handler.addObject(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN, sessionQuote);
        } catch (QuoteException e) {
        	logContext.error(this, "Handle invalid spreadSheet error: " + e.getMessage());
        }
        MessageBean mBean = MessageBeanFactory.create();
        String message = super.getI18NString(errorMsgKey,super.getBaseName(),locale);
        mBean.addMessage(message,MessageBeanKeys.ERROR);
        handler.setMessage(mBean);
        return handler.getResultBean();
    }
    
    private ResultBean handleLockedSpreadSheet(String errorMsgKey, Locale locale, ResultHandler handler, String creatorId, String[] args, boolean isLocked) throws ResultBeanException{
        handler.setState(NewQuoteStateKeys.STATE_DISPLAY_UPLOAD_PAGE);
        try {
            // get session quote information for right column
            QuoteRightColumn sessionQuote = null;
            if (creatorId != null && !"".equals(creatorId)) {
                QuoteProcess process = QuoteProcessFactory.singleton().create();
                sessionQuote = process.getQuoteRightColumnInfo(creatorId);
            }
            handler.addObject(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN, sessionQuote);
        } catch (QuoteException e) {
        	logContext.error(this, "Handle locked spreadSheet error: " + e.getMessage());
        }
        handler.addObject(NewQuoteParamKeys.PARAM_IS_LOCKED, new Boolean(isLocked));
        
        MessageBean mBean = MessageBeanFactory.create();
        String message = super.getI18NString(errorMsgKey,super.getBaseName(),locale);
        if (args != null) {
            message = MessageFormat.format(message, args);   
        }
        mBean.addMessage(message,MessageBeanKeys.ERROR);
        handler.setMessage(mBean);
        return handler.getResultBean();
    }
    
   
    /**
     * @param spreadSheetQuote
     */
    protected String addInvalidDataToURL(SpreadSheetQuote squote) {
        
        //pointing redirect url to DISPLAY_CUST_PRTNR_TAB
        String redirectAction = DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB;
        String redirectURL = HtmlUtil.getURLForAction(redirectAction);
        StringBuffer invalidDataBuffer = new StringBuffer(NewQuoteParamKeys.UPLOAD_WARNING);
        boolean validationFailed = false;
        boolean includingSaaS = false;
        
        //cusotmer num
        if(!squote.isCustValid()){
            validationFailed = true;
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_CUST_NUM);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //contract num
        if(!squote.isSapCtrctNumValid()){
            validationFailed = true;
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_CTRCT_NUM);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //all parts including SaaS parts list
        List eqSaaSPartList = squote.getSaaSEqPartList();
        if(null != eqSaaSPartList && !eqSaaSPartList.isEmpty()){
        	validationFailed = true;
        	includingSaaS = true;
        	invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_INCLUDING_SAAS_PART_NUM_LIST);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //duplicate SaaS part list
        List duplicateSaaSPartNumList = squote.getDuplicateSaaSPartNumList();
        StringBuffer partsBuffer = new StringBuffer();
        if( null != duplicateSaaSPartNumList && !duplicateSaaSPartNumList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_DUPLICATE_SAAS_PART_NUM_LIST);
            validationFailed = true;
            for(Iterator it = duplicateSaaSPartNumList.iterator(); it.hasNext();)
                partsBuffer.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            String partsStr = partsBuffer.toString();
            partsStr = partsStr.substring(0, partsStr.length()-1);
            invalidDataBuffer.append(partsStr);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //Inactive SaaS part list
        List inactiveSaaSPartNumList = squote.getInactiveSaaSPartNumList();
        StringBuffer partsBuffer2 = new StringBuffer();
        if( null != inactiveSaaSPartNumList && !inactiveSaaSPartNumList.isEmpty()){
        	invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_INACTIVE_SAAS_PART_NUM_LIST);
        	validationFailed = true;
        	for(Iterator it = inactiveSaaSPartNumList.iterator(); it.hasNext();)
        		partsBuffer2.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
        	String partsStr = partsBuffer2.toString();
        	partsStr = partsStr.substring(0, partsStr.length()-1);
        	invalidDataBuffer.append(partsStr);
        	invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //uncertified SaaS part list
        List uncertifiedSaaSPartNumList = squote.getUncertifiedSaaSPartNumList();
        StringBuffer partsBuffer3 = new StringBuffer();
        if( null != uncertifiedSaaSPartNumList && !uncertifiedSaaSPartNumList.isEmpty()){
        	invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_UNCERTIFIED_SAAS_PART_NUM_LIST);
        	validationFailed = true;
        	for(Iterator it = uncertifiedSaaSPartNumList.iterator(); it.hasNext();)
        		partsBuffer3.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
        	String partsStr = partsBuffer3.toString();
        	partsStr = partsStr.substring(0, partsStr.length()-1);
        	invalidDataBuffer.append(partsStr);
        	invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //invalid SW part list
        List invalidPartList = squote.getInvalidPartList();
        StringBuffer partsBuffer4 = new StringBuffer();
        if( null != invalidPartList && !invalidPartList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_PART_NUM_LIST);
            validationFailed = true;
            for(Iterator it = invalidPartList.iterator(); it.hasNext();)
                partsBuffer4.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            String partsStr = partsBuffer4.toString();
            partsStr = partsStr.substring(0, partsStr.length()-1);
            invalidDataBuffer.append(partsStr);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //invalid SW including SaaS part list
        List saasPartNumList = squote.getSaaSPartNumList();
        StringBuffer partsBuffer5 = new StringBuffer();
        if( null != saasPartNumList && !saasPartNumList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_SW_INCLUDING_SAAS_PART_NUM_LIST);
            validationFailed = true;
            for(Iterator it = saasPartNumList.iterator(); it.hasNext();)
                partsBuffer5.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            String partsStr = partsBuffer5.toString();
            partsStr = partsStr.substring(0, partsStr.length()-1);
            invalidDataBuffer.append(partsStr);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //invalid SaaS part list
        List invalidSaaSPartList = squote.getInvalidSaaSPartList();
        StringBuffer saasPartsBuffer = new StringBuffer();
        if( null != invalidSaaSPartList && !invalidSaaSPartList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_SAAS_PART_NUM_LIST);
            validationFailed = true;
            for(Iterator it = invalidSaaSPartList.iterator(); it.hasNext();)
            	saasPartsBuffer.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            String partsStr = saasPartsBuffer.toString();
            partsStr = partsStr.substring(0, partsStr.length()-1);
            invalidDataBuffer.append(partsStr);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //invalid SaaS including SW part list
        List partNumList = squote.getPartNumList();
        StringBuffer saasPartsBuffer2 = new StringBuffer();
        if( null != partNumList && !partNumList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_SAAS_INCLUDING_SW_PART_NUM_LIST);
            validationFailed = true;
            for(Iterator it = partNumList.iterator(); it.hasNext();)
            	saasPartsBuffer2.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            String partsStr = saasPartsBuffer2.toString();
            partsStr = partsStr.substring(0, partsStr.length()-1);
            invalidDataBuffer.append(partsStr);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        //invalid billing frequency list
        List invalidFrequencyPartNumList = squote.getInvalidFrequencyPartNumList();
        StringBuffer saasPartsBuffer3 = new StringBuffer();
        if( null != invalidFrequencyPartNumList && !invalidFrequencyPartNumList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_FREQENCY_PART_NUM_LIST);
            validationFailed = true;
            for(Iterator it = invalidFrequencyPartNumList.iterator(); it.hasNext();)
            	saasPartsBuffer3.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            String partsStr = saasPartsBuffer3.toString();
            partsStr = partsStr.substring(0, partsStr.length()-1);
            invalidDataBuffer.append(partsStr);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
         //invalid appliance qty partlist   remove by sunzw
//        List invalidApplianceQtyPartList = squote.getInvalidApplianceQtyPartlist();
//        StringBuffer appliancePartsBuffer = new StringBuffer();
//        if( null != invalidApplianceQtyPartList && !invalidApplianceQtyPartList.isEmpty()){
//            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_INVALID_APPLIANCE_PARTS_QUANTITY);
//            validationFailed = true;
//            for(Iterator it = invalidApplianceQtyPartList.iterator(); it.hasNext();)
//            	appliancePartsBuffer.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
//            String partsStr = appliancePartsBuffer.toString();
//            partsStr = partsStr.substring(0, partsStr.length()-1);
//            invalidDataBuffer.append(partsStr);
//            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
//        }  
        
        
        //check if user had put in dates for a OCS parts after the spread-sheet is imported, 
        //if yes, do not honor the rep entered dates for the OCS parts, a text msg should be shown on the UI
        List invalidOCSPartList = squote.getInvalidOCSPartList();
        if( null != invalidOCSPartList && !invalidOCSPartList.isEmpty()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_OCS_PART_NUM_LIST);
            validationFailed = true;
        }
        
        // check if user had put in future dates for licence parts after the spread-sheet is imported, if yes, ignore the future dates
        if( squote.hasInvalidFutureDates()){
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_INVALID_FUTURE_DATE);
            validationFailed = true;
        }
        
        //if exceed max part qty, 250
        if(squote.isExceedMaxPartsQty()){
            validationFailed = true;
            invalidDataBuffer.append(NewQuoteParamKeys.UPLOAD_WARNING_MAX_PART_QTY);
            invalidDataBuffer.append(NewQuoteDBConstants.UPLOADWARNING_DELIMIT);
        }
        
        if(validationFailed){
            return redirectURL + invalidDataBuffer.toString();
        }else {
        	return redirectURL;
        }
    }

}
