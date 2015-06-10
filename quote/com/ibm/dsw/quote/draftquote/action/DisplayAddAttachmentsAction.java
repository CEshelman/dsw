package com.ibm.dsw.quote.draftquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.AttachmentsContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author George
 */
public class DisplayAddAttachmentsAction extends BaseContractActionHandler {

    public static final String STAGE_FINALIZATION = "Finalization";
    public static final String STAGE_SUBMITTED = "Submitted";
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        
        addObjectToHandler(contract, handler);

        handler.setState(StateKeys.STATE_DISPLAY_ATTACH_FILES_TO_QUOTE);
        
        return handler.getResultBean();
    }
    
    protected void addObjectToHandler(ProcessContract contract, ResultHandler handler) throws QuoteException
    {
        AttachmentsContract aContract = (AttachmentsContract)contract;
        
        String webQuoteNum = aContract.getWebQuoteNumber();
        String stage = aContract.getStage();
        String secId = aContract.getSecId();
        
        if ( secId == null || secId.equals("") )
        {
            secId = "0";
        }
        
        if(StringUtils.isBlank(stage) || StringUtils.isBlank(webQuoteNum)){
            throw new QuoteException("Invalid request parameters. Stage/QuoteNum are required entries.");
        }
        
        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, webQuoteNum);
        handler.addObject(ParamKeys.PARAM_STAGE, stage);
        handler.addObject(DraftQuoteParamKeys.PARAM_SEC_ID, secId);
    }
}