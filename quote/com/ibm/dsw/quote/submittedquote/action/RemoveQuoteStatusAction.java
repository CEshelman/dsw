/*
 * Created on May 15, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteStatusChangeService;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author zhangln
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveQuoteStatusAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;
        String userId = baseContract.getUserId();
        String webQuoteNum = baseContract.getQuoteNum();
        QuoteHeader quoteHeader = null;

        try{
        SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
        quoteHeader = sqProcess.getQuoteHeaderByQuoteNum(webQuoteNum);
        
        QuoteStatusChangeService qscService = new QuoteStatusChangeService();
        qscService.execute4RemoveStatus(quoteHeader,QuoteStatusChangeService.SQ_STAT_PAO_BLOCK);
        
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        qProcess.addQuoteAuditHist(webQuoteNum, null, userId, SubmittedQuoteConstants.USER_ACTION_UNLOCK_PAO_ACCESS, null, null);

        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        }catch(Exception e){
            throw new QuoteException(e);
        }
        return handler.getResultBean();
    }

}
