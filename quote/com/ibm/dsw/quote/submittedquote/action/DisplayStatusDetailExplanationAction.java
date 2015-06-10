/*
 * Created on Feb 13, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.DisplayStatusDetailExplanationContract;
import com.ibm.dsw.quote.submittedquote.domain.StatusExplanation;
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
public class DisplayStatusDetailExplanationAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplayStatusDetailExplanationContract sdeContract = (DisplayStatusDetailExplanationContract)contract;
        String sapDocNum = sdeContract.getSapDocNum();
        String statusCode = sdeContract.getStatusCode();
        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
        StatusExplanation explanation = process.getStatusDetailExplanation(sapDocNum, statusCode);
        handler.addObject(SubmittedQuoteParamKeys.STATUS_DETAIL_EXPLANATION,explanation);
        handler.setState(SubmittedQuoteStateKeys.STATE_DISPLAY_STATUS_DETAIL_EXPLANATION);
        return handler.getResultBean();
    }

}
