package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.util.CommonUIValidator;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.OverrideDatePopupContract;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayOverrideDatePopupAction</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-9
 */
public class DisplayOverrideDatePopupAction extends DraftQuoteBaseAction   {
	private static final LogContext log = LogContextFactory.singleton().getLogContext();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getDraftQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        OverrideDatePopupContract ct = (OverrideDatePopupContract)contract;
        if(!validateParameter(ct)){
        	throw new QuoteException("Invalid input parameter specified");
        }
        
        handler.addObject(DraftQuoteParamKeys.PART_NUM,ct.getPartNum());
        handler.addObject(DraftQuoteParamKeys.SEQ_NUM,ct.getSeqNum());
        handler.addObject(DraftQuoteParamKeys.START_DATE,ct.getStartDate());
        handler.addObject(DraftQuoteParamKeys.END_DATE,ct.getEndDate());
        handler.addObject(DraftQuoteParamKeys.REVN_STRM_CODE, ct.getRevnStrmCode());
        handler.addObject(DraftQuoteParamKeys.IS_SPECIAL_BID_RNWL_PART, ct.isSpecialBidRnwlPart() + "");
        handler.addObject(DraftQuoteParamKeys.ALLOW_EDIT_START_DATE, ct.getAllowEditStartDate());
        handler.addObject(DraftQuoteParamKeys.ALLOW_EDIT_END_DATE, ct.getAllowEditEndDate());
        handler.addObject(DraftQuoteParamKeys.CMPRSS_CVRAGE_APPLIED, ct.isCmprssCvrageApplied() + "");
        
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_OVERRIDE_DATE_POPUP);
    }
    
    private boolean validateParameter(OverrideDatePopupContract ct){
    	if(!CommonUIValidator.isPartNumberValid(ct.getPartNum())){
    		log.info(this, "Invalid part number : " + ct.getPartNum());
    		return false;
    	}
    	
    	if(!CommonUIValidator.isPartSeqNumberValid(ct.getSeqNum())){
    		log.info(this, "Invalid line item seq number : " + ct.getSeqNum());
    		return false;
    	}
    	
    	return true;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        // TODO Auto-generated method stub
        return null;
    }
    
    protected boolean checkQuoteInDraftStatus(Quote quote) {
        if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        
        if(quote.getQuoteHeader().isExpDateExtendedFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        return true;        
    }    
}