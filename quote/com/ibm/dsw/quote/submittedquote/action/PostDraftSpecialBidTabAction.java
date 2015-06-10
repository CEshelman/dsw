package com.ibm.dsw.quote.submittedquote.action;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PostSubmittedSpecialBidTabAction</code> class is to post the special bid
 * tab of a draft quote
 * 
 * @author: Fred (qinfengc@cn.ibm.com)
 * 
 * Creation date: Jun. 10, 2010
 */
public class PostDraftSpecialBidTabAction extends BaseContractActionHandler {
    static LogContext logContext = LogContextFactory.singleton().getLogContext();
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        try
        {
	        SubmittedQuoteBaseContract bidTabContract = (SubmittedQuoteBaseContract) contract;
	        if ( bidTabContract.isHttpGETRequest() )
	        {
	            return null;
	        }
	        
	        String userId = bidTabContract.getUserId();
	        String justTxt = bidTabContract.getJustCommentsDraft();
	        
	        QuoteHeader header = QuoteProcessFactory.singleton().create().getQuoteHdrInfoByWebQuoteNumAndUserId(null, userId);
	        String webQuoteNum = header.getWebQuoteNum();
	        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
	        
	        process.persistQuoteComment(webQuoteNum, userId, SpecialBidInfo.CommentInfo.SPBID_J, justTxt, 
	                SpecialBidInfo.BEGIN_SUBMITTER, -1);
	        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
	        return handler.getResultBean();
        }
        catch ( Exception e )
        {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            e.printStackTrace();
        }
        return null;
    }
}
