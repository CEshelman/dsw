package com.ibm.dsw.quote.massdlgtn.action;

import is.domainx.User;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.massdlgtn.config.MassDlgtnKeys;
import com.ibm.dsw.quote.massdlgtn.contract.DlgtnSalesRepContract;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SelectSalesRepAction</code> class is only for Sales Manager who
 * want select a sales rep, he/she need input a sales rep id, and click submit
 * button, this action will be called.
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class SelectSalesRepAction extends AbstractDlgAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeMassDlgtn(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        
        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;
        User webAuthUser = ct.getUser();
        
        if (this.isSalesManager) {
            if (QuoteConstants.ACCESS_LEVEL_APPROVER == webAuthUser.getAccessLevel(QuoteConstants.APP_CODE_SQO)
                    && ct.getUserId().equalsIgnoreCase(ct.getSalesUserId())) {
                return handleInvldSalesId(handler, ct.getLocale());
            }
        }
        else {
            // if current user is not sales manager, just redirect to Quote Home page
            logContext.info(this, "Current user is not sales manager :" + ct.getCurrentUserId());
            handler.setState(com.ibm.dsw.quote.base.config.StateKeys.STATE_DISPLAY_HOME);
            return handler.getResultBean();
        }

        BluePageUser user = getBluePageInfo(ct.getSalesUserId());
        String salesFullName = null;
        
        if (null == user) {
        	try{
                MassDlgtnProcess process = MassDlgtnProcessFactory.singleton().create();
                salesFullName = process.retriveFullRepName(ct.getSalesUserId());
        	}catch(Throwable e){
        		logContext.error("error when fullRepNameWhoHasOwnedQuote by "+ct.getSalesUserId(),e);
        	}
            //user and salesFullName can't be both null here pls refer to validate(ProcessContract contract) 
        	if(salesFullName ==null){
                ResultBean resultBean = handler.getUndoResultBean();
                MessageBean errMessagesBean = MessageBeanFactory.create("the mail address is not on bluepage!");
                resultBean.setMessageBean(errMessagesBean);
                return resultBean;
        	}
        } else {
            salesFullName = user.getFullName();
            List reportingSalesReps = ct.getReportingSalesReps();
            if ((null == reportingSalesReps) || (!reportingSalesReps.contains(ct.getSalesUserId()))) {
                logContext.info(this, "the sales rep (" + ct.getSalesUserId() + ")is not in reporting list");
                ResultBean resultBean = handler.getUndoResultBean();
                MessageBean errMessagesBean = MessageBeanFactory.create("the mail address is not in reporting list");
                resultBean.setMessageBean(errMessagesBean);
                return resultBean;
            }

        }

        fillDelegates(handler, ct.getSalesUserId());

        handler.addObject(MassDlgtnKeys.Params.SALES_USER_FULL_NAME, salesFullName);

        handler.setState(MassDlgtnKeys.State.STATE_DISPLAY_MASS_DLGTN);

        return handler.getResultBean();
    }
    
    protected ResultBean handleInvldSalesId(ResultHandler handler, Locale locale) {

        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(MassDlgtnKeys.Messages.APPRVR_NOT_DLGT_MSG,
                MessageKeys.BUNDLE_APPL_I18N_MASS_DLGTN, locale);
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        return resultBean;
    }

    protected String getValidationForm() {
        return MassDlgtnKeys.Forms.SALES_REPRESENTITIVE_MAIL;
    }

    protected boolean validate(ProcessContract contract) {

        String salesFullName = null;
        
        BluePageUser user = null;
        
        if (!super.validate(contract)) {
            return false;
        }

        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;

        user = getBluePageInfo(ct.getSalesUserId());

        if (null == user) {
        	try{
                MassDlgtnProcess process = MassDlgtnProcessFactory.singleton().create();
                salesFullName = process.retriveFullRepName(ct.getSalesUserId());
        	}catch(Throwable e){
        		logContext.error("error when fullRepNameWhoHasOwnedQuote by "+ct.getSalesUserId(),e);
        	}
        	if(salesFullName!=null){
        		return true;
        	}else{
                HashMap vMap = new HashMap();
                FieldResult fr = new FieldResult();
                fr.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, MessageKeys.EMAIL_ADDR_NOT_IN_BLUEPAGES);           
                fr.addArg(MessageKeys.BUNDLE_APPL_I18N_MASS_DLGTN, MessageKeys.SALES_USER_ID);
                vMap.put(MassDlgtnKeys.Params.SALES_USER_ID, fr);
                addToValidationDataMap(contract,vMap);
                return false;        		
        	}

        } else {

            salesFullName = user.getFullName();

            List reportingSalesReps = ct.getReportingSalesReps();
            
            if ((null == reportingSalesReps) || (!reportingSalesReps.contains(ct.getSalesUserId()))) {

                HashMap vMap = new HashMap();
                FieldResult fr = new FieldResult();
                fr.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, MessageKeys.EMAIL_ADDR_NOT_IN_REPORTING_LIST);
                fr.addArg(MessageKeys.BUNDLE_APPL_I18N_MASS_DLGTN, MessageKeys.SALES_USER_ID);
                vMap.put(MassDlgtnKeys.Params.SALES_USER_ID, fr);
                addToValidationDataMap(contract,vMap);
                return false;

            }
        }
        return true;

    }

}
