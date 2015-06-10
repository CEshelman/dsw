package com.ibm.dsw.quote.newquote.action;

import java.util.HashMap;

import org.apache.commons.lang.BooleanUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.contract.SelectSavedQuoteContract;
import com.ibm.dsw.quote.newquote.exception.NewQuoteUnAuthorizedException;
import com.ibm.dsw.quote.newquote.util.NewQuoteUtils;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
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
 * The <code>LoadSavedQuoteAction</code> class is to load a saved draft quote
 * into a session quote.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on Feb 28, 2007
 */
public class SelectSavedQuoteAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        SelectSavedQuoteContract savedQuoteContract = (SelectSavedQuoteContract) contract;

        String quoteNum = savedQuoteContract.getSavedQuoteNum();
        String creatorId = savedQuoteContract.getUserId();
        
        if (!NewQuoteUtils.isQuoteBelongsToUser(creatorId, quoteNum))
            throw new NewQuoteUnAuthorizedException();

        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        QuoteHeader quoteHeader = null;
        try {
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(quoteNum);
        } catch (TopazException e) {
            throw new QuoteException(e);
        }
        
        boolean openNewFlag = BooleanUtils.toBoolean(savedQuoteContract.getOpenAsNew());
        quoteProcess.loadDraftQuoteToSession(quoteNum, creatorId, openNewFlag);
        if(!openNewFlag && quoteHeader.isPGSQuote() && QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(savedQuoteContract.getQuoteUserSession().getAudienceCode()) ){
        	if( quoteHeader.isReturnForChgByEval() ){
        		quoteProcess.updateBPQuoteStage(creatorId,quoteNum,QuoteConstants.QUOTE_STAGE_CODE_EDIT_FORRETCHG_EVAL,0,0,"");
        	}
        	else if(quoteHeader.isSubmittedForEval()){
        		quoteProcess.updateBPQuoteStage(creatorId,quoteNum,QuoteConstants.QUOTE_STAGE_CODE_SESSION,0,0,"");
        	}
        }
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
        
        if((quoteHeader.isBidIteratnQt() && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))
                || (quoteHeader.isCopied4PrcIncrQuoteFlag() && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))
                || (quoteHeader.isExpDateExtendedFlag() && QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))){
            logContext.debug(this, "bid iteration quote flag " + quoteHeader.isBidIteratnQt() + ", copy for price increase flag " + quoteHeader.isCopied4PrcIncrQuoteFlag()
            		+", copy for extending expiration date flag" + quoteHeader.isExpDateExtendedFlag() );
            String redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB);
            redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ParamKeys.PARAM_QUOTE_NUM, quoteNum).toString();
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
            return handler.getResultBean();
        }
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));

        return handler.getResultBean();

    }

    protected boolean validate(ProcessContract contract) {
        SelectSavedQuoteContract savedQuoteContract = (SelectSavedQuoteContract) contract;

        String quoteNum = savedQuoteContract.getSavedQuoteNum();
        if (quoteNum == null || quoteNum.equals("")) {
            FieldResult field = new FieldResult();
            HashMap map = new HashMap();

            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.MSG_URL_PARAM_NOT_VALID);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.URL_PARAM);
            map.put(NewQuoteParamKeys.URL_PARAM, field);
            addToValidationDataMap(savedQuoteContract, map);
            return false;
        }

        return true;
    }

}
