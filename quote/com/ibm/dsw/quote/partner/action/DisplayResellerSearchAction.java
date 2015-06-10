package com.ibm.dsw.quote.partner.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerStateKeys;
import com.ibm.dsw.quote.partner.contract.DisplayPartnerSearchContract;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayResellerSearchAction</code> class is to display reseller
 * search.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class DisplayResellerSearchAction extends DisplayPartnerSearchAction {
    
    protected String getState(ProcessContract contract) {
        
        DisplayPartnerSearchContract dpsCtrct = (DisplayPartnerSearchContract) contract;
        if ("true".equalsIgnoreCase(dpsCtrct.getIsSubmittedQuote()))
            return PartnerStateKeys.STATE_DISPLAY_SBMTD_QT_RSEL_SEARCH;
        else
            return PartnerStateKeys.STATE_DISPLAY_RESELLER_SEARCH;
    }
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        ResultBean superResult = super.executeBiz(contract, handler);
        DisplayPartnerSearchContract dpsCtrct = (DisplayPartnerSearchContract) contract;
        
        if (DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE.equalsIgnoreCase(superResult.getState()
                .getStateAsString()))
            return superResult;
        
        if ("true".equalsIgnoreCase(dpsCtrct.getIsSubmittedQuote())) {
            boolean hasCtrldPart = hasControlledParts(dpsCtrct);
            handler.addObject(SubmittedQuoteParamKeys.PARAM_HAS_CTRLD_PART, Boolean.valueOf(hasCtrldPart));
        }
        
        String  pageFrom=dpsCtrct.getPageFrom();
        if(pageFrom.equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
	       	 handler.addObject(DraftQuoteParamKeys.PAGE_FROM, pageFrom);
	       	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, dpsCtrct.getMigrationReqNum());
       }
        
        return handler.getResultBean();
    }
    
    protected boolean hasControlledParts(DisplayPartnerSearchContract dpsCtrct) throws QuoteException {
        
        String webQuoteNum = dpsCtrct.getWebQuoteNum();
        
        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
        boolean hasCtrldPart = process.quoteHasCtrldParts(webQuoteNum);
        
        return hasCtrldPart;
    }

    protected void addContractContentToResult(ProcessContract contract, ResultHandler handler) {
        super.addContractContentToResult(contract, handler);
        DisplayPartnerSearchContract c = (DisplayPartnerSearchContract) contract;
        if (c.isNew()) {
            handler.addObject(PartnerParamKeys.PARAM_TIER1_RESELLER, "on");
            handler.addObject(PartnerParamKeys.PARAM_TIER2_RESELLER, "on");
        } else {
            if (c.isFromSearchByNum()) {
                handler.addObject(PartnerParamKeys.PARAM_TIER1_RESELLER, "on");
                handler.addObject(PartnerParamKeys.PARAM_TIER2_RESELLER, "on");
                handler.addObject(PartnerParamKeys.PARAM_MULTIPLE_PROD_CHK, "");
            } else if(c.isFromSearchByPort()){
                handler.addObject(PartnerParamKeys.PARAM_TIER1_RESELLER, "on");
                handler.addObject(PartnerParamKeys.PARAM_TIER2_RESELLER, "on");
            }else {
                handler.addObject(PartnerParamKeys.PARAM_TIER1_RESELLER, c.getTier1());
                handler.addObject(PartnerParamKeys.PARAM_TIER2_RESELLER, c.getTier2());
                handler.addObject(PartnerParamKeys.PARAM_MULTIPLE_PROD_CHK, "");
            }
        }
    }
}
