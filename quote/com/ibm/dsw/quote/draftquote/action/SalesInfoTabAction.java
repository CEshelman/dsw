package com.ibm.dsw.quote.draftquote.action;

import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.SalesInfoTabContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcess;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SaleInfoTabAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 24, 2007
 */

public class SalesInfoTabAction extends DraftQuoteBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getSessionQuoteDetail(com.ibm.dsw.quote.common.domain.Quote)
     */
    protected void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        
        SalesInfoTabContract salesInfoTabContract = (SalesInfoTabContract)contract;
        String defaultBusinessOrg = salesInfoTabContract.getDefaultBusinessOrg();
        handler.addObject(DraftQuoteParamKeys.PARAM_DEFAULT_BUSORG, defaultBusinessOrg);
        
        QuoteProcess process = QuoteProcessFactory.singleton().create();

    	process.getQuoteDetailForSalesInfoTab(quote);
    	
        if(!QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(salesInfoTabContract.getQuoteUserSession().getAudienceCode())){
        	
            QuoteHeader header = quote.getQuoteHeader();
            
            if((!ButtonDisplayRuleFactory.singleton().isDisplayPromotions(header))
            		&& (quote.getPromotionsList() != null && quote.getPromotionsList().size() > 0)){
            	QuotePromotionProcess promotionProcess = QuotePromotionProcessFactory.singleton().create();
            	promotionProcess.removeQuotePromotion(header.getWebQuoteNum(), null, 1);
            }
            
            if (header.isRenewalQuote()) {
                QuoteTxt qtCmmnt = process.getRenewalQuoteDetailComment(header.getWebQuoteNum());
                handler.addObject(DraftQuoteParamKeys.PARAM_SI_DETAIL_COMMENTS, qtCmmnt);
                
                QuoteAttachmentProcess attchProcess = QuoteAttachmentProcessFactory.singleton().create();
                List attachments = attchProcess.getRQSalesCommentAttachments(header.getWebQuoteNum());
                handler.addObject(DraftQuoteParamKeys.PARAM_ATTACHMENT_LIST, attachments);
            }
        }
        
        //if a quote is created by DSJ system
        QuoteHeader header = quote.getQuoteHeader();
        boolean isDsjFlag=process.isDsjQuote(header.getWebQuoteNum());
        header.setDsjFlag(isDsjFlag);
        
        if (quote == null || quote.getQuoteHeader() == null) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        }
        else if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
            handler.setState(DraftQuoteStateKeys.STATE_DRAFT_RQ_SALES_INFO_TAB);
        }
        else {
            handler.setState(DraftQuoteStateKeys.STATE_DRAFT_SQ_SALES_INFO_TAB);
        }

    }
    
    protected boolean isDisplayTranMessage(String bundleName, String bundleKey) {
        boolean isDisplay = super.isDisplayTranMessage(bundleName, bundleKey);
        if (isDisplay)
            return true;
        
        String[] keys = { DraftQuoteMessageKeys.MSG_ENTER_QT_TITLE };
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(bundleKey))
                return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return "";
    }

}
