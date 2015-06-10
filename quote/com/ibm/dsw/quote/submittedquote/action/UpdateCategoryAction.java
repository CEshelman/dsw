/*
 * Created on 2009-10-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

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
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.UpdateCategoryContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author Fred
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UpdateCategoryAction extends SaveDraftComemntsBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        
        UpdateCategoryContract uaContract = (UpdateCategoryContract)contract;
        
        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();

        Quote quote = null;
        try {
            quote = QuoteProcessFactory.singleton().create()
                    .getSubmittedQuoteBaseInfo(uaContract.getWebQuoteNum(), uaContract.getUserId(), null);
        } catch (NoDataException e1) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e1));
            throw new QuoteException("The input submitted quote number is invalid.", e1);
        } catch (QuoteException e1) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e1));
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
        sbp.updateCategory(quote.getQuoteHeader().getWebQuoteNum(), uaContract.getSpBidCategories(), uaContract.getUserId());
        
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);

        
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM
                + "="
                + uaContract.getWebQuoteNum());
        
        return handler.getResultBean();
    }
}
