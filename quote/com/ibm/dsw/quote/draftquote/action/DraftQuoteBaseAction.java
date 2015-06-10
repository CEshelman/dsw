package com.ibm.dsw.quote.draftquote.action;


import is.domainx.User;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DraftQuoteBaseContract;
import com.ibm.dsw.quote.export.config.ExportQuoteMessageKeys;
import com.ibm.dsw.quote.mail.config.MailMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>draftQuoteBaseAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-22
 */

public abstract class DraftQuoteBaseAction extends BaseContractActionHandler {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        handleMessage(contract, handler);

        handler.setState(getState(contract));
        DraftQuoteBaseContract dqbContract = (DraftQuoteBaseContract) contract;
        String creatorId = dqbContract.getUserId();
        User user = dqbContract.getUser();
        QuoteUserSession salesRep = dqbContract.getQuoteUserSession();
        int maxExpDays = -1;
        Quote quote = null;

        if (user != null)
            handler.addObject(ParamKeys.PARAM_USER_OBJECT, user);
        
        if (salesRep != null)
            handler.addObject(ParamKeys.PARAM_QUOTE_USER_SESSION, salesRep);

        try {
            // Get quote header and customer.
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            quote = qProcess.getDraftQuoteBaseInfo(creatorId);
            quote.setPgsAppl(dqbContract.isPGSEnv());
            if(!checkQuoteInDraftStatus(quote)){
                String redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB);
                redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ParamKeys.PARAM_QUOTE_NUM, quote.getQuoteHeader().getWebQuoteNum()).toString();
                handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
                handler.setState(StateKeys.STATE_REDIRECT_ACTION);
                return handler.getResultBean();
            }
            
            getDraftQuoteDetail(quote, contract, handler);
            handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
            addContractToResult(contract, handler);
            
            if ("1".equals(StringUtils.trimToEmpty(dqbContract.getSaveSuccess()))) {
                MessageBean mBean = MessageBeanFactory.create();
                String message = this.getI18NString(DraftQuoteMessageKeys.SAVE_DRAFT_QUOTE_SUCCESS_MSG,
                        MessageKeys.BUNDLE_APPL_I18N_QUOTE, dqbContract.getLocale());
                mBean.addMessage(message, MessageBeanKeys.SUCCESS);
                handler.setMessage(mBean);
            }
            
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum()) && quoteHeader.isChannelQuote()) {
                if (StringUtils.isNotBlank(quoteHeader.getPayerCustNum())) {
	                logContext.debug(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
	                Partner payer = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode());
	                quote.setPayer(payer);
	            }
            }
            
            handler.addObject(ParamKeys.PARAM_CUST_ACT_URL_PARAM, dqbContract.getSearchCriteriaUrlParam());
            handler.addObject(ParamKeys.PARAM_CUST_ACT_REQUESTOR, dqbContract.getRequestorEMail());
            
            addRedirectMessage(handler, dqbContract.getRedirectMsg(), dqbContract.getLocale());
            
            //Add MTM message
			addValidatorMTMMessage(
					QuoteCommonUtil.isAddApplncMTMMsgToMessageBean(quote),
					dqbContract.getLocale(), handler);
			
			addNeedConfigMonthlySWMessage(
					QuoteCommonUtil.isNeededConfigMonthlySWMsgToMessageBean(quote),
					dqbContract.getLocale(), handler);
            

        } catch (NoDataException nde) {
            logContext.info(this, "No data exception catched");
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        } catch (TopazException e) {
			throw new QuoteException(e);
		}
        return handler.getResultBean();
    }

	/**
     * 
     */
    protected boolean checkQuoteInDraftStatus(Quote quote) {
        if (quote.getQuoteHeader().isBidIteratnQt()
                && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        
        if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        
        if(quote.getQuoteHeader().isExpDateExtendedFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        
        return true;        
    }

    protected abstract void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException;

    protected abstract String getState(ProcessContract contract);

    protected boolean validate(ProcessContract contract) {
        return super.validate(contract);
    }

    protected void handleMessage(ProcessContract contract, ResultHandler handler) {
        DraftQuoteBaseContract baseContract = (DraftQuoteBaseContract) contract;
        String msgInfo = baseContract.getMessageInfo();
        String msgError = baseContract.getMessageError();
        boolean hasMsg = false;
        MessageBean mBean = MessageBeanFactory.create();

        if (StringUtils.isNotBlank(msgInfo)) {
            mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
            hasMsg = true;
        }

        if (StringUtils.isNotBlank(msgError)) {
            mBean.addMessage(msgError, MessageBeanKeys.ERROR);
            hasMsg = true;
        }

        if (hasMsg)
            handler.setMessage(mBean);
    }

    protected boolean isDisplayTranMessage(String bundleName, String bundleKey) {
        String[] keys = { MailMessageKeys.MAIL_QUOTE_SUCCESS, MailMessageKeys.MAIL_QUOTE_ERROR,
                DraftQuoteMessageKeys.MSG_CAN_NOT_CONVERT, ExportQuoteMessageKeys.INVALID_PART_QTY,
                DraftQuoteMessageKeys.MSG_CAN_NOT_CONVERT_SB };
        return ArrayUtils.contains(keys, bundleKey);
    }
}
