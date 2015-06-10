/*
 * Created on 2007-5-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.UpdateApproverContract;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UpdateApproverAction extends SaveDraftComemntsBaseAction {
	static final String MSG_MAIL_SEND_FAIL = MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_MAIL_SEND_FAIL + ":" + MessageBeanKeys.ERROR;

    /**
	 * 
	 */
	private static final long serialVersionUID = -5954710702039969967L;

	/* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        
        UpdateApproverContract uaContract = (UpdateApproverContract)contract;
        List approvers = uaContract.getApprovers();

//        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();

        //Validate if User has privilege
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
        boolean editableFlag = this.checkAccess(uaContract, quote);
        logContext.debug(this, "editFlag=" + editableFlag);
        if (!editableFlag) {
            String msg = getI18NString(SubmittedQuoteMessageKeys.NO_PRIVILEGE_PERFORM, I18NBundleNames.ERROR_MESSAGE, uaContract.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            return handler.getUndoResultBean();
        }
        //Notify updated approvers of the next approval level.
        SpecialBidProcess sbp = SpecialBidProcessFactory.singleton().create();
        sbp.processSpecialBidApproverSelection(quote.getQuoteHeader().getWebQuoteNum(),approvers, uaContract.getUserId(), uaContract.isPGSEnv());
        List redirectMsgList = new ArrayList();
        try
        {
        	sbp.notifyUpdatedApprovers(quote, approvers);
        }
        catch ( QuoteException e )
        {
        	logContext.error(this, "in update approver mail exception: " + e.getMessage());
        	redirectMsgList.add(MSG_MAIL_SEND_FAIL);
        }
        //String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_UPDATE_APPROVER_SELECTION_SUCCESSFULLY, MessageKeys.BUNDLE_APPL_I18N_QUOTE, uaContract.getLocale());
        //handler.addMessage(msg, MessageBeanKeys.INFO);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        
        redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_UPDATE_APPROVER_SELECTION_SUCCESSFULLY + ":" + MessageBeanKeys.INFO);
        handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, redirectMsgList);
        
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(uaContract.getDisplayAction())
                + "&"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM
                + "="
                + uaContract.getWebQuoteNum());
        
        return handler.getResultBean();
    }
    
     protected boolean validate(ProcessContract contract) {
         HashMap vMap = new HashMap();
         UpdateApproverContract uaContract = (UpdateApproverContract)contract;
         List approvers = uaContract.getApprovers();
         if(approvers != null){
             for(int i=0;i<approvers.size();i++){
                 SpecialBidApprvr approver = (SpecialBidApprvr)approvers.get(i);
                 BluePageUser user = BluePagesLookup.getBluePagesInfo(approver.getApprvrEmail());
                 if (null == user ) {
                	 logContext.info(this, "Update approver " + approver.getApplierEmail() + ":" + uaContract.getWebQuoteNum() 
                			 + " not in BP or BP is not available now");
                     FieldResult fieldResult = new FieldResult();
                     fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, SubmittedQuoteMessageKeys.MSG_APPROVER_NOTIN_BP);
//                     String msg = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(MessageKeys.BUNDLE_APPL_I18N_QUOTE, uaContract.getLocale(),SubmittedQuoteMessageKeys.MSG_APPROVER_OF_LEVEL)+" "+i;
                     fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SubmittedQuoteMessageKeys.MSG_APPROVER_OF_LEVEL);
                     vMap.put(DraftQuoteParamKeys.PARAM_APPROVER_LEVEL + approver.getSpecialBidApprLvl(), fieldResult);
                 }
             }
             if(vMap.size() > 0){
                 addToValidationDataMap(contract, vMap);
                 return false;
             }
         }
         return true;
    }
     
    private boolean checkAccess(UpdateApproverContract uaContract, Quote quote)
    {
        boolean editableFlag = (uaContract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER) && quote.getQuoteUserAccess().isAnyAppTypMember();
        if ( !editableFlag )
        {
            return false;
        }
        int pendingLvl = quote.getQuoteUserAccess().getPendingAppLevel();
        List approvers = uaContract.getApprovers();
        if(approvers != null){
            for(int i=0;i<approvers.size();i++){
                SpecialBidApprvr approver = (SpecialBidApprvr)approvers.get(i);
                if ( approver.getSpecialBidApprLvl() < pendingLvl )
                {
                    logContext.info(this, "Update approver level is lower than quote pending level");
                    return false;
                }
            }
        }
        return true;
    }
}
