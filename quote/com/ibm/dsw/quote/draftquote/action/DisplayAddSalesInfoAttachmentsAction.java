package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.AttachmentsContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author George
 */
public class DisplayAddSalesInfoAttachmentsAction extends DisplayAddAttachmentsAction {

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        
        AttachmentsContract attCtrct = (AttachmentsContract) contract;
        
        addObjectToHandler(contract, handler);

        if ("1".equals(attCtrct.getFileInputFlag()))
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_ADD_ATTACHS_INPUT_FOR_SALESINFO);
        else
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_ADD_ATTACHS_FOR_SALESINFO);
        
        return handler.getResultBean();
    }
}