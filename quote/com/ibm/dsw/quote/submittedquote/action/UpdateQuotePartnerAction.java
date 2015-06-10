package com.ibm.dsw.quote.submittedquote.action;

import java.util.Map;

import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.UpdateQuotePartnerContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UpdateQuotePartnerAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Jul 1, 2008
 */

public class UpdateQuotePartnerAction extends UpdateQuotePrtnrBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        UpdateQuotePartnerContract updateContract = (UpdateQuotePartnerContract) contract;
        String webQuoteNum = updateContract.getQuoteNum();
        Quote quote = this.getQuoteInfo(webQuoteNum);
        QuoteHeader header = quote.getQuoteHeader();
        
        Map result = null;
        try {
            QuoteValidationRule rule = QuoteValidationRule.createRule(null, quote, null);
            result = rule.validateUpdateQuotePartners();
            
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
        
        if (result != null && !result.isEmpty())
            return handleValidateResult(result, handler, updateContract.getLocale());
        
        int chkEmailY9PrtnrAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(updateContract.getChkEmailY9PartnerAddrList()) ? 1 : 0;
        int chkEmailAddiPrtnrAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(updateContract.getChkEmailAddiPartnerAddr()) ? 1: 0;
        String addPrtnrAddr = updateContract.getCustEmailAddiPartnerAddr();
        if (chkEmailAddiPrtnrAddr == 0)
            addPrtnrAddr = "";
        
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        //supprsPARegstrnEmailFlag=3 means not to update this flag.
        quoteProcess.updateQuoteSubmission(null, header.getWebQuoteNum(), header.getReqstIbmCustNumFlag(), header
                .getReqstPreCreditCheckFlag(), header.getInclTaxFinalQuoteFlag(), header.getFirmOrdLtrFlag(), header.getSendQuoteToPrmryCntFlag(),
                header.getSendQuoteToQuoteCntFlag(), header.getSendQuoteToAddtnlCntFlag(), header.getAddtnlCntEmailAdr(), 
                null, header.getIncldLineItmDtlQuoteFlg(), chkEmailY9PrtnrAddr, addPrtnrAddr, header.getPAOBlockFlag(), 3,
                "", header.getFctNonStdTermsConds(), header.getQuoteOutputType(), null, null,null,header.getQuoteOutputOption(),header.getBudgetaryQuoteFlag());
        
        // Set user input values into quote header domain
        try {
            header.setSendQuoteToAddtnlPrtnrFlag(chkEmailY9PrtnrAddr == 1);
            header.setAddtnlPrtnrEmailAdr(addPrtnrAddr);
        } catch (TopazException tpe) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tpe));
            throw new QuoteException(tpe);
        }
        
        try {
            logContext.debug(this, "Calling SAP QuoteModify RFC...");
            QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
            quoteModifyService.updateQuotePartners(quote);
            
        } catch (WebServiceException e1) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e1));
            throw new QuoteException(e1);
        }
        
        quoteProcess.updateSapIDocNum(header.getWebQuoteNum(), header.getSapIntrmdiatDocNum(), updateContract
                .getUserId(), SubmittedQuoteConstants.USER_ACTION_UPDT_PRTNR_IDOC);
        
        MessageBean msgBean = MessageBeanFactory.create();
        String msg = this.getI18NString(SubmittedQuoteMessageKeys.MSG_PRTNR_UPDT_SUCC, BaseI18NBundleNames.QUOTE_BASE,
                updateContract.getLocale());
        msgBean.addMessage(msg, MessageBeanKeys.SUCCESS);
        handler.setMessage(msgBean);
        
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

}
