package com.ibm.dsw.quote.draftquote.action;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.spbid.SpecialBidRule;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostSpecialBidTabContract;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PostSpecialBidTabAction</code> class is to post the special bid
 * tab of a draft quote
 * 
 * @author: Goshen Pan (panyg@cn.ibm.com)
 * 
 * Creation date: Apr. 02, 2007
 */
public class PostSpecialBidTabAction extends PostDraftQuoteBaseAction {
    
    protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        try {
            PostSpecialBidTabContract bidTabContract = (PostSpecialBidTabContract) contract;
            if ( bidTabContract.isHttpGETRequest() )
            {
                return;
            }
            if(bidTabContract.isSecTextError()){
            	throw new QuoteException("The input section just texts is invalid." + StringUtils.join(bidTabContract.getSectionJustTexts(),"&&"));
            }
            Quote quote = null;
            getTransactionContextManager().begin();
            QuoteProcess quoteProcess = getQuoteProcess();
            quote = quoteProcess.getDraftQuoteBaseInfo(bidTabContract.getUserId());
            quoteProcess.getQuoteDetailForSpecialBidTab(quote);
            
            //post expire date
            quoteProcess.updateQuoteHeaderCustPrtnrTab(bidTabContract.getUserId(), bidTabContract.getExpireDate(),
                    null, null, -1, -1, bidTabContract.getQuoteClassfctnCode(), bidTabContract.getStartDate(),
                    bidTabContract.getOemAgrmntType(), bidTabContract.getPymntTermsDays(), bidTabContract.getOemBidType(), bidTabContract.getEstmtdOrdDate(), bidTabContract.getCustReqstdArrivlDate(),bidTabContract.getSspType());
            
            // if it's renewal quote, make sure quote is editable.
            if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
                QuoteAccess quoteAccess = quote.getQuoteAccess();
                boolean quoteEditable = quoteAccess.isCanEditRQ();
                if (!quoteEditable)
                    return;
            }
            // make sure whether manually special bid is triggered
            // deleted logic from JAVA, done in SP
            quote.setPgsAppl(bidTabContract.isPGSEnv()); 
            // store special bid info
            SpecialBidInfo bidInfo = quote.getSpecialBidInfo();
            bidInfo.setCreditAndRebillFlag(bidTabContract.isCrediAndRebill());
            bidInfo.setCreditJustText(bidTabContract.getCreditJustText());
            bidInfo.setElaTermsAndCondsChgFlag(bidTabContract.isElaTermsAndCondsChg());
            bidInfo.setTermsAndCondsChgFlag(bidTabContract.isTermsAndCondsChg());
            bidInfo.setFulfllViaLanddMdlFlag(bidTabContract.isFulfllViaLanddMdl());
            bidInfo.setSpBidCategories(bidTabContract.getSpBidCategories());
            bidInfo.setPreApprvdCtrctLvlPricFlg(bidTabContract.isPreApprvdCtrctLvlPric());
            bidInfo.setRyltyDiscExcddFlag(bidTabContract.isRyltyDiscExcdd());
            bidInfo.setSalesDiscTypeCode(bidTabContract.getSalesDiscTypeCode());
            bidInfo.setSetCtrctLvlPricngFlag(bidTabContract.isSetCtrctLvlPricng());
            bidInfo.setSpBidCustIndustryCode(bidTabContract.getSpBidCustIndustryCode());
            bidInfo.setSpBidDist(bidTabContract.getSpBidDist());
            bidInfo.setSpBidRgn(bidTabContract.getSpBidRgn());
            bidInfo.setSpBidType(bidTabContract.getSpBidType());            
            
            if(!bidTabContract.isPGSEnv()){
            	bidInfo.setQuestions(bidTabContract.getSpBidQuestions());
            }
            
            bidInfo.setCompetitorName(bidTabContract.getCompetitorName());
            bidInfo.setCompetitorPrice(bidTabContract.getCompetitorPrice());
            bidInfo.setCompetitorProduct(bidTabContract.getCompetitorProduct());
            bidInfo.setCompetitorTC(bidTabContract.getCompetitorTC());
            bidInfo.setCompetitive(bidTabContract.isCompetitive());
            bidInfo.setUserMail(bidTabContract.getUserId());
            bidInfo.setRateBuyDown(bidTabContract.getRateBuyDown());
            //bidInfo.setSWGIncur(bidTabContract.getSWGIncur());
            if(bidTabContract.getRateBuyDown()!=0){
            	if(StringUtils.isNotEmpty(bidTabContract.getProgRBD())){
            		bidInfo.setProgRBD(Double.valueOf(bidTabContract.getProgRBD()));
            	}
            	
            	if(StringUtils.isNotEmpty(bidTabContract.getIncrRBD())){
            		bidInfo.setIncrRBD(Double.valueOf(bidTabContract.getIncrRBD()));
            	}
            }
            	
            bidInfo.setOrgnlSalesOrdNum(StringHelper.fillString(StringUtils.trim(bidTabContract.getOrgnlSalesOrdNum())));
            bidInfo.setOrgnlQuoteNum(StringHelper.fillString(StringUtils.trim(bidTabContract.getOrgnlQuoteNum())));
            bidInfo.setSalesPlayNum(StringUtils.trim(bidTabContract.getSalesPlayNum()));
            bidInfo.setInitSpeclBidApprFlag(bidTabContract.isInitSpeclBidApprFlag());
            bidInfo.setEvaltnAction(bidTabContract.getEvaloptionType());
            bidInfo.setEvaltnComment(bidTabContract.getEvalComment());
            bidInfo.setSplitBidFlag(bidTabContract.isSplitBid());
            bidInfo.setChannelOverrideDiscountReasonCode(bidTabContract.getChannelOverrideDiscountReason());
            setSection(bidInfo, bidTabContract);
            SpecialBidRule specialBidRule = SpecialBidRule.create(quote);
            specialBidRule.validate(bidTabContract.getUserId());
            bidInfo.setGridFlag(specialBidRule.isGridDelegationFlag());
            getTransactionContextManager().commit();
        } catch (Throwable e) {
            this.logContext.error(this, e.toString());
            this.logContext.error(this, e);
            throw new QuoteException(e);
        } finally {
            try {
                getTransactionContextManager().rollback();
            } catch (TopazException e1) {
            	this.logContext.error(this,e1);
            }
        }
    }
    
    protected boolean validate(ProcessContract contract) {
        boolean flag = super.validate(contract);
        if ( !flag )
        {
            return flag;
        }
        PostSpecialBidTabContract bidTabContract = (PostSpecialBidTabContract) contract;
        
        String orgnQuoteNum = bidTabContract.getOrgnlQuoteNum();
        HashMap map = new HashMap();
        if ( orgnQuoteNum != null && orgnQuoteNum.length() > 10 )
        {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_QUOTE_NUM);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.ORGNL_QUOTE_NUM);
            map.put(SpecialBidParamKeys.FINANCE_RATE, field);
            addToValidationDataMap(contract, map);
            flag = false;
            //return false;
        }
        
        orgnQuoteNum = bidTabContract.getOrgnlSalesOrdNum();
        if ( orgnQuoteNum != null && orgnQuoteNum.length() > 10 )
        {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_QUOTE_NUM);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.ORGNL_SALES_ORD_NUM);
            map.put(SpecialBidParamKeys.FINANCE_RATE, field);
            addToValidationDataMap(contract, map);
            flag = false;
            //return false;
        }
        
        if ( StringUtils.equals(bidTabContract.getSalesDiscTypeCode(), "2") )
        {
           
            int len = StringUtils.trim(bidTabContract.getSalesPlayNum()).length();
            if ( len == 0 || len > 32 )
            {
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_SALES_PLAY_NUM);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.SALES_PLAY_NUM);
                map.put(SpecialBidParamKeys.SALES_PLAY_NUM, field);
                addToValidationDataMap(contract, map);
                flag = false;
                //return false;
            }
        }
        
        if ( bidTabContract.getRateBuyDown() == 0 )
        {
            return flag;
        }

        String progRBD = bidTabContract.getProgRBD();
        String incrRBD = bidTabContract.getIncrRBD();
        boolean flag2 = true;
        boolean flag3 = true;
        if ( StringUtils.isEmpty(progRBD) )
        {
            flag2 = false;
        }else
        {
            try
            {
                Double d = new Double(progRBD);
                String temp = DecimalUtil.format(d.doubleValue(), 3);
                bidTabContract.setProgRBD(temp);
            }
            catch ( Throwable t )
            {
                logContext.debug(this, t.toString());
                flag2 = false;
            }
        }
        
        if ( StringUtils.isEmpty(incrRBD) )
        {
            flag3 = false;
        }
        else
        {
            try
            {
                Double d = new Double(incrRBD);
                String temp = DecimalUtil.format(d.doubleValue(), 3);
                bidTabContract.setIncrRBD(temp);
            }
            catch ( Throwable t )
            {
                logContext.debug(this, t.toString());
                flag3 = false;
            }
        }
        
        if ( !flag2 )
        {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_PROG_RBD);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.PROG_RBD);
            map.put(SpecialBidParamKeys.PROG_RBD, field);
            addToValidationDataMap(contract, map);
            flag = false;
        }
        
        if ( !flag3 )
        {
            FieldResult field = new FieldResult();
            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_INCR_RBD);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.INCR_RBD);
            map.put(SpecialBidParamKeys.INCR_RBD, field);
            addToValidationDataMap(contract, map);
            flag = false;
        }
        
        return flag;
    }
    
    public QuoteProcess getQuoteProcess() throws QuoteException{
        return QuoteProcessFactory.singleton().create();
    }
    public TransactionContextManager getTransactionContextManager(){
        return TransactionContextManager.singleton();
    }

}
