package com.ibm.dsw.quote.draftquote.action;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.JustSection_Impl;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostDraftQuoteBaseContract;
import com.ibm.dsw.quote.draftquote.contract.PostSpecialBidTabContract;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
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
 * This <code>PostDraftQuoteBaseAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 28, 2007
 */

public abstract class PostDraftQuoteBaseAction extends BaseContractActionHandler {

	transient LogContext logContext = LogContextFactory.singleton().getLogContext();

    // for sales quote
    protected static final String SAVE_QUOTE_AS_DRAFT = "1";

    // for sales quote
    protected static final String EXPORT_AS_SPREADSHEET = "2";

    // for sales quote
    protected static final String EMAIL_DRAFT_QUOTE = "3";

    // for sales quote & renewal quote
    protected static final String SUBMIT_FOR_APPROVAL = "4";

    // for sales quote & renewal quote
    protected static final String SUBMIT_AS_FINAL = "5";

    // for sales quote & renewal quote
    protected static final String ORDER = "6";

    // for renewal quote
    protected static final String CONVERT_TO_SALES_QUOTE = "7";

    // for sales quote
    protected static final String SAVE_QUOTE_AS_NEW_DRAFT = "8";
    
    // for sales quote
    protected static final String EXPORT_AS_NATIVE_EXCEL = "9";
    
    protected static final String ACCEPT = "10";
    
    protected static final String DETAILED_RENEWAL_QUOTE = "11";
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        PostDraftQuoteBaseContract baseContract = (PostDraftQuoteBaseContract) contract;
        if (baseContract.isHttpGETRequest()) {
            return this.handleInvalidHttpRequest(contract, handler);
        }
        
        String redirectURL = generateRedirectURL(baseContract);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, baseContract.getForwardFlag());
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);

        try {
            // To post current tab's inputs.
            TransactionContextManager.singleton().begin();
            postDraftQuoteTab(contract, handler);
            TransactionContextManager.singleton().commit();
            
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException te) {
                logContext.error(this, te, "problems raised when doing rollback ");
            }
        }

        // Handle action button processes.
        handleActionButtonProcess(baseContract, handler);

        return handler.getResultBean();
        
    }

    protected boolean validate(ProcessContract contract) {
        boolean valid = super.validate(contract);
        if (!valid)
            return valid;
        
        PostDraftQuoteBaseContract baseContract = (PostDraftQuoteBaseContract) contract;
        HashMap map = new HashMap();
        boolean isQtStartDateValid = baseContract.isQtStartDateValid();
        boolean isQtExpDateValid = baseContract.isQtExpDateValid();
        //just validate estimated order date for add-on/trade-up/co-term
        boolean isEstmtdOrdDateValid = baseContract.isEstmtdOrdDateValid();
        int pymntTermsDays = baseContract.getPymntTermsDays();
        boolean isPymentTermsValid = baseContract.isQtPymntTermsValid();
        boolean isCRADValid = baseContract.isCustReqstdArrivlDateValid();

        if ( !isQtStartDateValid ) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_START_DATE);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.QUOTE_START_DAY);
            map.put(DraftQuoteParamKeys.PARAM_START_DAY, field);
        }
        
        // Display error msg for invalid start date
        if (!isQtExpDateValid) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_EXP_DATE);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.QUOTE_EXP_DAY);
            map.put(DraftQuoteParamKeys.PARAM_EXP_DAY, field);
        }
        
        if (!isPymentTermsValid) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_PYM_TERMS);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.PYMNT_TERMS_DAY);
            map.put(DraftQuoteParamKeys.PARAM_PAYMENT_TERMS_INPUT, field);
        }
        
        if ( !isEstmtdOrdDateValid ) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_ESTMTD_ORD_DATE);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.ESTIMATED_ORDER_DATE);
            map.put(DraftQuoteParamKeys.PARAM_ESTMTD_ORD_DAY, field);
        }
        
        if ( !isCRADValid ) {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_CUST_REQSTD_ARRIVL_DATE);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.CUST_REQSTD_ARRIVL_DATE);
            map.put(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVL_DAY, field);
        }  
        
        if ( !isQtStartDateValid || !isQtExpDateValid || !isPymentTermsValid || !isEstmtdOrdDateValid || !isCRADValid ) {
        	addToValidationDataMap(baseContract, map);
        	return false;
        }
        else {
        	return true;
        }
    }

    /**
     * To post the specific tab's inputs.
     * 
     * @param contract
     * @param handler
     */
    protected abstract void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException;

    /**
     * To handle the common button's process, like "save" or "submit"
     * 
     * @param baseContract
     */
    private void handleActionButtonProcess(PostDraftQuoteBaseContract baseContract, ResultHandler handler)
            throws QuoteException {
        // There will be a switch to handle different button
        String buttonName = baseContract.getButtonName();

        if (SAVE_QUOTE_AS_DRAFT.equalsIgnoreCase(buttonName)) {
            // save quote as draft
            handleSaveQuoteAsDraft(baseContract, handler, false);
        } else if (SAVE_QUOTE_AS_NEW_DRAFT.equalsIgnoreCase(buttonName)) {
            // save quote as new draft
            handleSaveQuoteAsDraft(baseContract, handler, true);
        } else if (EXPORT_AS_SPREADSHEET.equalsIgnoreCase(buttonName)) {
            // export as spreadsheet
        } else if (EXPORT_AS_NATIVE_EXCEL.equalsIgnoreCase(buttonName)) {
            // export as native excel spreadsheet
        } else if (EMAIL_DRAFT_QUOTE.equalsIgnoreCase(buttonName)) {
            // email draft quote
        } else if (SUBMIT_FOR_APPROVAL.equalsIgnoreCase(buttonName)) {
            // submit for approval
        } else if (SUBMIT_AS_FINAL.equalsIgnoreCase(buttonName)) {
            // submit as final
        } else if (ORDER.equalsIgnoreCase(buttonName)) {
            // order
        } else if (CONVERT_TO_SALES_QUOTE.equalsIgnoreCase(buttonName)) {
            // convert to sales quote
        } else if (ACCEPT.equalsIgnoreCase(buttonName) ) {
        	accept(baseContract, handler);
        } else if(DETAILED_RENEWAL_QUOTE.equalsIgnoreCase(buttonName)) {
        	getDetailedRQDesc(baseContract, handler);
        }
    }
    
    protected void getDetailedRQDesc(PostDraftQuoteBaseContract baseContract, ResultHandler handler)
    {
    	logContext.debug(this, "begin handle redirect to view quote details button");
    	try {
    		ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        	StringBuffer redirectURL = new StringBuffer();
        	QuoteProcess process = QuoteProcessFactory.singleton().create();
            QuoteHeader qtHeader = process.getQuoteHdrInfo(baseContract.getUserId());
            
        	redirectURL.append(appContext.getConfigParameter(ApplicationProperties.RENEWAL_QUOTE_DETAIL_URL)).append("&");
        	redirectURL.append(DraftRQParamKeys.PARAM_RPT_QUOTE_NUM).append("=").append(qtHeader.getRenwlQuoteNum());
            handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, false);
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL.toString());
    	} catch (Exception e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		} 
    }
    
    protected void accept(PostDraftQuoteBaseContract baseContract, ResultHandler handler)
    {
    	logContext.debug(this, "begin handle accept button");
		try {
			QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
			Quote quote = qProcess.getDraftQuoteBaseInfo(baseContract.getUserId());
			
			QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
			Map map = capabilityProcess.getDraftQuoteActionButtonsRule(baseContract.getQuoteUserSession(), quote, baseContract.getQuoteUserSession());
			Boolean flag = (Boolean)map.get(QuoteCapabilityProcess.DISPLAY_ACCEPT_QUOTE);
			if ( flag == null || !flag.booleanValue()  )
			{
				 String msg = getI18NString(SubmittedQuoteMessageKeys.NO_PRIVILEGE_PERFORM, I18NBundleNames.ERROR_MESSAGE,
						 baseContract.getLocale());
		         handler.addMessage(msg, MessageBeanKeys.ERROR);
		          
		         logContext.info(this, "user has no privilege to accept the quote: webQuoteNum=" + baseContract.getWebQuoteNum() + ", userId=" + baseContract.getUserId());
		         handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
		         return;
			}
			qProcess.updateBPQuoteStage(baseContract.getUserId(), baseContract.getWebQuoteNum(), QuoteConstants.QUOTE_STAGE_CODE_ACCEPTED_EVAL ,0,0, null);
		} catch (Exception e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		} 
    }

    protected void handleSaveQuoteAsDraft(PostDraftQuoteBaseContract baseContract, ResultHandler handler, boolean createNewCopy)
            throws QuoteException {
        Locale locale = baseContract.getLocale();
        QuoteProcess process = QuoteProcessFactory.singleton().create();

        try {
            String creatorId = baseContract.getUserId();
            process.saveDraftQuote(creatorId, createNewCopy);
            String redirectURL = (String) handler.getParameters().getParameter(ParamKeys.PARAM_REDIRECT_URL) + "&amp;"
                    + ParamKeys.PARAM_DRAFTQT_SAVE_SUCCUSS + "=1";
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        } catch (SPException spe) {
            int genStatus = spe.getGenStatus();

            if (genStatus == CommonDBConstants.NO_LOB_COUNTRY) {
                String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_EMPTY_DRAFT_QUOTE);
                handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
            } else if (genStatus == CommonDBConstants.NO_QUOTE_TITLE) {
                String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB);
                String msgInfo = HtmlUtil.getTranMessageParam(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
                        DraftQuoteMessageKeys.MSG_ENTER_QT_TITLE, true, null);
                StringBuffer sb = new StringBuffer(redirectURL);
                sb.append("&amp;").append(ParamKeys.PARAM_TRAN_MSG).append("=").append(msgInfo);
                handler.addObject(ParamKeys.PARAM_REDIRECT_URL, sb.toString());
            } else {
                throw new QuoteException(spe);
            }
        }
    }

    /**
     * To generate the redirectURL. First get the redirectURL from contract,
     * then append the additional params.
     * 
     * @param baseContract
     * @return
     */
    private String generateRedirectURL(PostDraftQuoteBaseContract baseContract) {
        String redirectURL = baseContract.getRedirectURL();
        HashMap redirectParams = getRedirerctParams(baseContract);

        if (redirectURL != null && redirectParams != null) {
            String connector = "&";

            if (redirectURL != null && redirectURL.indexOf("?") < 0) {
                connector = "?";
            }
            StringBuffer sb = new StringBuffer(redirectURL);
            Iterator it = redirectParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                sb.append(connector).append(key).append("=").append((String) redirectParams.get(key));
                connector = "&";
            }
            redirectURL = sb.toString();
        }
        return redirectURL;
    }

    /**
     * If additional params are needed, override this method to return them
     * 
     * @param contract
     * @return
     */
    protected HashMap getRedirerctParams(ProcessContract contract) {
        return null;
    }

    protected boolean validateExpDate(ProcessContract contract) {
        PostDraftQuoteBaseContract baseContract = (PostDraftQuoteBaseContract) contract;
        if (baseContract.getExpireDate() == null)
            return true;

        PostDraftQuoteBaseContract bContract = (PostDraftQuoteBaseContract) contract;
        String sMaxExpDays = bContract.getMaxExpirationDays();
        boolean valid = true;

        try {
            int maxExpDays = Integer.parseInt(sMaxExpDays);
            
            // Removed = 0 as maxExpDays can be 0 for FCT to PA RQ
            if (maxExpDays < 0)
                valid = false;
            else {
                Calendar curr = Calendar.getInstance();
                curr.add(Calendar.DATE, maxExpDays);
                Date maxDate = curr.getTime();
                if (maxDate.before(baseContract.getExpireDate()))
                    valid = false;
            }
        } catch (Exception e) {
            valid = false;
        }

        return valid;
    }

    protected ResultBean handleInvalidExpDate(ProcessContract contract, ResultHandler handler) {
        PostDraftQuoteBaseContract bContract = (PostDraftQuoteBaseContract) contract;
        Locale locale = bContract.getLocale();
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();

        String sExpDateMsg = this.getI18NString(DraftQuoteMessageKeys.MSG_EXP_DATE_BEYOND_LIMIT,
                MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
        messageBean.addMessage(sExpDateMsg, MessageBeanKeys.INFO);
        resultBean.setMessageBean(messageBean);

        return resultBean;
    }
    
    protected void setSection(SpecialBidInfo bidInfo, PostSpecialBidTabContract bidTabContract)
    {
        String[] textIDs = bidTabContract.getTextIDs();
        if ( textIDs == null )
        {
            return;
        }
        String[] secIDs = bidTabContract.getSectionIndexs();
        String[] lastModifyTimes = bidTabContract.getLastModifyTimes();
        String[] justTexts = bidTabContract.getSectionJustTexts();
        bidInfo.getJustSections().clear();
        for ( int i = 0; i < textIDs.length; i++ )
        {
            JustSection sec = new JustSection_Impl();
            sec.setSecId(Integer.parseInt(secIDs[i]));
            SpecialBidInfo.CommentInfo cmtInfo = new SpecialBidInfo.CommentInfo();
            cmtInfo.comment = justTexts[i];
            cmtInfo.secId = Integer.parseInt(secIDs[i]);
            cmtInfo.textId = textIDs[i];
            cmtInfo.commentDateText = lastModifyTimes[i];
            if ( i != 0 )
            {
                cmtInfo.typeCode = SpecialBidInfo.CommentInfo.SBADJUST;
            }
            else
            {
                cmtInfo.typeCode = SpecialBidInfo.CommentInfo.SPBID_J;
            }
            sec.getJustTexts().add(cmtInfo);
            bidInfo.getJustSections().add(sec);
        }
    }
}
