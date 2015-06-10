package com.ibm.dsw.quote.submittedquote.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.UpdateSalesPlayNumContract;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UpdateSalesPlayNumAction</code> class is to update sales play num
 * 
 * @author qinfengc@cn.ibm.com
 * 
 * Created on 2011-05-30
 */
public class UpdateSalesPlayNumAction extends SaveDraftComemntsBaseAction {

	private static final long serialVersionUID = -2825288870178374569L;

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        
        UpdateSalesPlayNumContract uaContract = (UpdateSalesPlayNumContract)contract;
        
        Quote quote = null;
        try {
            quote = QuoteProcessFactory.singleton().create()
                    .getSubmittedQuoteBaseInfo(uaContract.getWebQuoteNum(), uaContract.getUserId(), null);
        } catch (NoDataException e1) {
            logContext.info(this, LogThrowableUtil.getStackTraceContent(e1));
            throw new QuoteException("The input submitted quote number is invalid.", e1);
        } catch (QuoteException e1) {
            logContext.info(this, LogThrowableUtil.getStackTraceContent(e1));
            throw new QuoteException("The input submitted quote number is invalid.", e1);
        }
        
        logContext.debug(this, "save user draft comments");
        this.saveUserDraftComments(quote, uaContract);
        boolean editableFlag = (uaContract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER) && quote.getQuoteUserAccess().isAnyAppTypMember();
        if (!editableFlag) {
            String msg = getI18NString(ErrorKeys.MSG_REQUEST_FAILED, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, uaContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            return handler.getUndoResultBean();
        }
        
        SpecialBidProcess sbp = SpecialBidProcessFactory.singleton().create();
        sbp.updateSalesPlayNum(quote.getQuoteHeader().getWebQuoteNum(), uaContract.getSalesPlayNum(), uaContract.getUserId());
        
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);

        
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM
                + "="
                + uaContract.getWebQuoteNum());
        
        return handler.getResultBean();
    }
    
    protected boolean validate(ProcessContract contract) {
        boolean flag = super.validate(contract);
        if ( !flag )
        {
            return flag;
        }
        UpdateSalesPlayNumContract uaContract = (UpdateSalesPlayNumContract)contract;
        int len = StringUtils.trim(uaContract.getSalesPlayNum()).length();
        if ( len == 0 || len > 32 )
        {
            FieldResult field = new FieldResult();
            HashMap map = new HashMap();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_SALES_PLAY_NUM);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.SALES_PLAY_NUM);
            map.put(SpecialBidParamKeys.SALES_PLAY_NUM, field);
            addToValidationDataMap(contract, map);
            return false;
        }
        return true;
    }
}
