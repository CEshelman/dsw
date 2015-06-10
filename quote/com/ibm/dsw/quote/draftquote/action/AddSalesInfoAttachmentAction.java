package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author George
 */
public class AddSalesInfoAttachmentAction extends AddAttachmentAction {

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        logContext.debug(this, "Begin to execute AddSalesInfoAttachmentAction.executeBiz");
        uploadFile(contract, handler);
        addContractToResult(contract, handler);
        logContext.debug(this, "End executing AddSalesInfoAttachmentAction.executeBiz");
        
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_ADD_ATTACHS_COMPLETE_FOR_SALESINFO);
        return handler.getResultBean();
    }
    
    protected String getAttchmtClassfctnCode() {
        return QuoteConstants.QT_ATTCHMNT_RQ_SLS_CMMNT;
    }
}