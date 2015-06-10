/*
 * Created on Apr 6, 2007
 */
package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Lavanya
 */
public class DisplayDraftQuoteAction extends BaseContractActionHandler {
    
        
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        QuoteBaseContract qbContract = (QuoteBaseContract) contract;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        QuoteRightColumn quoteRightColumn = process.getQuoteRightColumnInfo(qbContract.getUserId());

        if (quoteRightColumn == null) {
            logger.info(this, "quoteRightColumn is null, redirect to emptyDraftQuote.jsp page.");
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        } else {
            
            handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
            QuoteLockInfo quoteLockInfo = process.getQuoteLockInfo(quoteRightColumn.getSWebQuoteNum(), quoteRightColumn.getCreatorId());
            
            if (quoteLockInfo == null) {
                logger.info(this, "failed to get quote lock info, redirect to emptyDraftQuote.jsp page.");
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            }else{
                if (quoteLockInfo.isLockedFlag()) {
                    logger.info(this, "quote is currently locked by others.");
                    handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_LOCKED_QUOTE);
                } else {
                    logger.info(this, "quote is not locked by others.");
                    handler.setState(StateKeys.STATE_REDIRECT_ACTION);
                }
                handler.addObject(ParamKeys.PARAM_QUOTE_LOCK_INFO, quoteLockInfo);
                handler.addObject(ParamKeys.PARAM_REDIRECT_URL, genearateRedirectURL(quoteRightColumn));
            }
        }
        return handler.getResultBean();
    }

    /**
     * @param quoteRightColumn
     * @return
     */
    private String genearateRedirectURL(QuoteRightColumn quoteRightColumn) {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
        
        // if copied quote for increasing price on approved sb or bid iteration
        if (quoteRightColumn.getQtCopyType() == QuoteConstants.QT_COPY_TYPE_PRICINC 
                || quoteRightColumn.getQtCopyType() == QuoteConstants.QT_COPY_TYPE_BIDITER 
                ) {
            redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB);
            redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ParamKeys.PARAM_QUOTE_NUM,quoteRightColumn.getSWebQuoteNum()).toString();
        }else if(quoteRightColumn.getQtCopyType() == QuoteConstants.QT_COPY_TYPE_OPTCHG){
        	redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL);
        }
        

        logger.debug(this, "redirect URL is " + redirectURL + ",quote copy type is " + quoteRightColumn.getQtCopyType());
        
        return redirectURL;
    }

}
