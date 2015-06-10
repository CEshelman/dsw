package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.contract.AddRenewalPartsContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
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
 * The <code>AddRenewalPartAction</code> class is for "add renewal parts to
 * drafte quote".
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 6, 2007
 */
public class AddRenewalPartAction extends BaseContractActionHandler {

    /**
     *  
     */

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        AddRenewalPartsContract addRenewalPartsContract = (AddRenewalPartsContract) contract;
        String creatorId = addRenewalPartsContract.getUserId();
        String[] lineSeqNums = addRenewalPartsContract.getLineSeqNum();
        String rqNum = addRenewalPartsContract.getRqNum();
        
        int partLimitExceedCode = 0;
        try {
            logContext.debug(this, "begin to add item " + lineSeqNums + " from " + rqNum + " to current sales quote");
            partLimitExceedCode = callAddRenewalPartsProcess(creatorId, rqNum, lineSeqNums);
            logContext.debug(this, "end to add renewal quote items");
        } catch (QuoteException e) {
            // redirect to internal reporting
            logContext.debug(this, e.getMessage());
            logContext.debug(this, "call fails, redirect back to internal reporting...url="
                    + genGobackURL(addRenewalPartsContract));
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, genGobackURL(addRenewalPartsContract));
            handler.setState(StateKeys.STATE_REDIRECT_ACTION);
            return handler.getResultBean();
        }
        // redirect to part price tab action
        String ppTabURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);
        ppTabURL = HtmlUtil.addURLParam(new StringBuffer(ppTabURL), 
                                   DraftQuoteParamKeys.PART_LIMIT_EXCEED_CODE
                                   , String.valueOf(partLimitExceedCode)).toString();
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, ppTabURL);
        
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    /**
     * @param rqNum
     *  
     */

    private int callAddRenewalPartsProcess(String creatorId, String rqNum, String[] lineItemSeqNums)
            throws QuoteException {
        PartPriceProcess partPriceProcess = PartPriceProcessFactory.singleton().create();
        StringBuffer nums = new StringBuffer();
        for (int i = 0; i < lineItemSeqNums.length; i++) {
            nums.append(lineItemSeqNums[i] + ",");
        }
        
        return partPriceProcess.addRenewalPart(rqNum, nums.toString(), creatorId);
    }

    String genGobackURL(AddRenewalPartsContract addRQPartsContract) {
        StringBuffer sb = new StringBuffer();
        String rnwlQuoteDtlURL = HtmlUtil.getURLForReporting(addRQPartsContract.getRequestedAction());
        sb.append(rnwlQuoteDtlURL).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_QUOTE_NUM).append("=").append(addRQPartsContract.getRenewalQuoteNum())
                .append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_P1).append("=").append(addRQPartsContract.getP1()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_LIST_ACTION_NAME).append("=").append(
                addRQPartsContract.getListActionName()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_SEARCH_ACTION_NAME).append("=").append(
                addRQPartsContract.getSearchActionName()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_SEARCH_CRITERIA_URL_PARAM).append("=").append(
                addRQPartsContract.getSearchCriteriaUrlParam()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_SORT_BY).append("=").append(addRQPartsContract.getSortBy()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_ERROR_FLAG).append("=").append(1);
        return sb.toString();
    }

}
