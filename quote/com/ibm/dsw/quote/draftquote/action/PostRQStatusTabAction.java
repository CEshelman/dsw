package com.ibm.dsw.quote.draftquote.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftRQMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostRQStatusTabContract;
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
 * The <code>PostRQStatusTabAction</code> class is to update current draft
 * renewal quote status.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 18, 2007
 */
public class PostRQStatusTabAction extends PostDraftQuoteBaseAction {
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        
        PostRQStatusTabContract rqContract = (PostRQStatusTabContract) contract;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        String userID = rqContract.getUserId();
        process.updateQuoteHeaderCustPrtnrTab(rqContract.getUserId(), rqContract.getExpireDate(), null, null, -1, -1,
                rqContract.getQuoteClassfctnCode(), rqContract.getStartDate(), rqContract.getOemAgrmntType(), -1, -1, null,rqContract.getCustReqstdArrivlDate(),rqContract.getSspType());

        QuoteAccess qtAccess = rqContract.getQuote().getQuoteAccess();

        if (qtAccess.isCanEditRQ() || qtAccess.isCanUpdateRQStatus()) {
            process.postRQStatusTab(rqContract.getQuoteNum(), rqContract.getPrimaryStatus(),
                    QuoteStatus.S_BLOCKED_4_AUTORENEWAL, rqContract.getSecondaryStatus() != null, rqContract
                            .getTermReason(), rqContract.getTermComment().trim(), userID);
        }
    }
    
    private void getQuoteForStatusTab(PostRQStatusTabContract contract) throws QuoteException {
        
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        String userId = contract.getUserId();
        QuoteHeader qtHeader = null;
        QuoteAccess quoteAccess = null;
        
        try {
            qtHeader = process.getQuoteHdrInfo(contract.getUserId());
        } catch (NoDataException nde) {
            throw new QuoteException("Quote header is not found for the login user " + contract.getUserId());
        }
        
        if (qtHeader == null || !qtHeader.isRenewalQuote() || !qtHeader.getWebQuoteNum().equals(contract.getQuoteNum())) {
            throw new QuoteException("Can not find the current renewal quote [" + qtHeader.getWebQuoteNum() + ", "
                    + contract.getQuoteNum() + "," + qtHeader.isRenewalQuote() + "]");
        }
        
        if (qtHeader != null && StringUtils.isNotBlank(qtHeader.getWebQuoteNum()) ){
            QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
            quoteAccess = qcProcess.getRnwlQuoteAccess(userId, qtHeader.getRenwlQuoteNum());
        }
        
        if (quoteAccess == null) {
            throw new QuoteException("Quote access for the current renewal quote [" + qtHeader.getWebQuoteNum() + "]");
        }
        
        Quote quote = new Quote(qtHeader);
        quote.setQuoteAccess(quoteAccess);
        contract.setQuote(quote);
    }

    private boolean validateStatus(PostRQStatusTabContract rqContract) throws QuoteException {
        
        this.getQuoteForStatusTab(rqContract);
        QuoteAccess qtAccess = rqContract.getQuote().getQuoteAccess();
        
        if (!qtAccess.isCanEditRQ() && !qtAccess.isCanUpdateRQStatus()) {
            return true;
        }

        QuoteProcess process = QuoteProcessFactory.singleton().create();
        HashMap vMap = new HashMap();

        String pStatus = rqContract.getPrimaryStatus();
        int accessFlag = 0;
        if (qtAccess.isCanEditRQ())
            accessFlag = 1;
        else if (qtAccess.isCanUpdateRQStatus())
            accessFlag = 2;
        
        if (!isValidCode(pStatus, process.getAvailablePrimaryStatus(rqContract.getQuoteNum(), accessFlag))) {
            //errorMsg: Primary status is not a valid status;
            addValidationMsg(rqContract, DraftRQMessageKeys.RQ_RENEWAL_STATUS, DraftRQParamKeys.PARAM_RQ_P_STATUS, vMap);
            return false;
        }

        String sStatus = rqContract.getSecondaryStatus();
        if (sStatus != null && !QuoteStatus.S_BLOCKED_4_AUTORENEWAL.equals(sStatus)) {
            //errorMsg: Secondary status is not a valid status;
            addValidationMsg(rqContract, DraftRQMessageKeys.RQ_SECONDARY_STATUS, DraftRQParamKeys.PARAM_RQ_S_STATUS,
                    vMap);
            return false;
        }

        if (pStatus.equalsIgnoreCase(QuoteStatus.P_TERMINATED)) {
            String reason = rqContract.getTermReason();
            boolean validTermSection = true;
            if (!isValidCode(reason, process.getTerminationReasons())) {
                //errorMsg: Reason code is not a valid reason;
                addValidationMsg(rqContract, DraftRQMessageKeys.TERMINATION_REASON,
                        DraftRQParamKeys.PARAM_RQ_TERM_REASON, vMap);
                validTermSection = false;
            }
            if (rqContract.getTermComment() == null || rqContract.getTermComment().trim().length() == 0) {
                addValidationMsg(rqContract, DraftRQMessageKeys.TERMINATION_COMMENTS,
                        DraftRQParamKeys.PARAM_RQ_TERM_COMMENT, vMap);
                validTermSection = false;
            }
            if (!validTermSection) {
                return false;
            }
        }

        return true;
    }

    protected boolean validate(ProcessContract contract) {
        boolean valid = super.validate(contract);

        if (!valid) {
            return false;
        }

        try {
            return validateStatus((PostRQStatusTabContract)contract);
        } catch (QuoteException e) {
            LogContext logContext = LogContextFactory.singleton().getLogContext();
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            return false;
        }
    }

    private boolean isValidCode(String code, List codeDescList) {
        if (code == null || code.trim().length() == 0) {
            return false;
        }

        boolean validCode = false;
        for (int i = 0; i < codeDescList.size(); i++) {
            CodeDescObj pStatus = (CodeDescObj) codeDescList.get(i);
            if (pStatus.getCode().equals(code)) {
                validCode = true;
                break;
            }
        }
        return validCode;
    }

    private void addValidationMsg(PostRQStatusTabContract contract, String msgKey, String fieldName, HashMap vMap) {
        FieldResult fieldResult = new FieldResult();
        fieldResult.setMsg(MessageKeys.BUNDLE_BASE_I18N_VALIDATORMESSAGES, DraftRQMessageKeys.ERR_INVALID_ENTRY);
        fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, msgKey);
        vMap.put(fieldName, fieldResult);
        addToValidationDataMap(contract, vMap);
    }

    protected String getValidationForm() {
        return DraftRQViewKeys.RQ_STATUS_FORM;
    }
}
