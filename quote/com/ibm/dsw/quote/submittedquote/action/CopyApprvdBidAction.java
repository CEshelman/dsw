package com.ibm.dsw.quote.submittedquote.action;

import java.util.Map;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.submittedquote.contract.UpdateQuotePartnerContract;
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
 * This <code>CopyApprvdBidAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Nov 17, 2009
 */

public class CopyApprvdBidAction extends UpdateQuotePrtnrBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        UpdateQuotePartnerContract cpCtrct = (UpdateQuotePartnerContract) contract;
        String userId = cpCtrct.getUserId();
        String sbmtdQuoteNum = cpCtrct.getQuoteNum();
        
        // Copy the approved bid as session quote, set webRefFlag = 2.
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        process.copyUpdateSbmtQuoteToSession(sbmtdQuoteNum, userId, 2);
        
        // Get the newly created quote
        Quote quote = getNewCopiedQuote(userId,cpCtrct.isPGSEnv());
        quote.setPgsAppl(cpCtrct.isPGSEnv());
        QuoteHeader header = quote.getQuoteHeader();
        
        int chkEmailY9PrtnrAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(cpCtrct.getChkEmailY9PartnerAddrList()) ? 1 : 0;
        int chkEmailAddiPrtnrAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(cpCtrct.getChkEmailAddiPartnerAddr()) ? 1: 0;
        String addPrtnrAddr = chkEmailAddiPrtnrAddr == 0 ? "" : cpCtrct.getCustEmailAddiPartnerAddr();
        
        // Update partner email options. supprsPARegstrnEmailFlag=3 means not to update this flag.
        process.updateQuoteSubmission(userId, null, header.getReqstIbmCustNumFlag(), header.getReqstPreCreditCheckFlag(),
                header.getInclTaxFinalQuoteFlag(), header.getFirmOrdLtrFlag(), header.getSendQuoteToQuoteCntFlag(), header.getSendQuoteToPrmryCntFlag(),
                header.getSendQuoteToAddtnlCntFlag(), header.getAddtnlCntEmailAdr(), header.getQSubmitCoverText(),
                header.getIncldLineItmDtlQuoteFlg(), chkEmailY9PrtnrAddr, addPrtnrAddr, header.getPAOBlockFlag(), 3,
                "", header.getFctNonStdTermsConds(), header.getQuoteOutputType(), null, null,null,header.getQuoteOutputOption(),header.getBudgetaryQuoteFlag());
        
        Map result = null;
        try {
            // Update partner email options into quote header object
            header.setSendQuoteToAddtnlPrtnrFlag(chkEmailY9PrtnrAddr == 1);
            header.setAddtnlPrtnrEmailAdr(addPrtnrAddr);
            
            QuoteValidationRule rule = QuoteValidationRule.createRule(cpCtrct.getQuoteUserSession(), quote, null);
            result = rule.validateSubmitCopiedApprvdBid();
            
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
        
        if (result != null && !result.isEmpty()) {
            // validation failed, delete the session quote
            // do not update the quote stage if fails to submit quote after single quote reference
            // only set the SAVED_QUOTE_FLAG to 1
            // header.setQuoteStageCode(QuoteConstants.QUOTE_STAGE_CODE_SAVED);
            process.updateQuoteStage(userId, quote);
            
            return handleValidateResult(result, handler, cpCtrct.getLocale());
        }
        
        // Call web service to create quote in SAP
        try {
            process.callServicesToCreateQuote(userId, cpCtrct.getUser(), cpCtrct.getQuoteUserSession(),
                    quote);
        } catch (WebServiceException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            
            // Service call failed, delete the session quote
            // do not update the quote stage if fails to submit quote after single quote reference
            // only set the SAVED_QUOTE_FLAG to 1
            // header.setQuoteStageCode(QuoteConstants.QUOTE_STAGE_CODE_SAVED);
            process.updateQuoteStage(userId, quote);
            
            throw new QuoteException(e);
        }
        
        // Service call success, update quote stage to sap quote
        process.updateQuoteStageForSubmission(userId, quote);
        
        handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SPECIALBID_FINALIZATION);
        
        return handler.getResultBean();
    }
    
    protected Quote getNewCopiedQuote(String creatorId,boolean isPGSEnv) throws QuoteException {
        Quote quote = null;
        
        try {
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            quote = quoteProcess.getDraftQuoteDetails(creatorId,isPGSEnv);
            
            PartPriceProcess partPriceProcess = PartPriceProcessFactory.singleton().create();
            partPriceProcess.getCurrentPriceForSubmit(quote, creatorId);
            
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (PriceEngineUnAvailableException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
        
        return quote;
    }

}
