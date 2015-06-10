/*
 * Created on 2007-9-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostSpecialBidTabContract;
import com.ibm.dsw.quote.draftquote.contract.SpBidRemoveAttchmtContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * @author helenyu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SpBidRemoveAttachmntAction extends PostDraftQuoteBaseAction {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    public void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        try {
            PostSpecialBidTabContract bidTabContract = (PostSpecialBidTabContract) contract;
            if ( bidTabContract.isHttpGETRequest() )
            {
                return;
            }
            Quote quote = null;
            TransactionContextManager.singleton().begin();
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
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
            bidInfo.setSpBidJustText(bidTabContract.getSpBidJustText());
            bidInfo.setSpBidRgn(bidTabContract.getSpBidRgn());
            bidInfo.setSpBidType(bidTabContract.getSpBidType());            
            bidInfo.setQuestions(bidTabContract.getSpBidQuestions());
            
            bidInfo.setCompetitorName(bidTabContract.getCompetitorName());
            bidInfo.setCompetitorPrice(bidTabContract.getCompetitorPrice());
            bidInfo.setCompetitorProduct(bidTabContract.getCompetitorProduct());
            bidInfo.setCompetitorTC(bidTabContract.getCompetitorTC());
            bidInfo.setCompetitive(bidTabContract.isCompetitive());
            bidInfo.setUserMail(bidTabContract.getUserId());
            bidInfo.setRateBuyDown(bidTabContract.getRateBuyDown());
            //bidInfo.setSWGIncur(bidTabContract.getSWGIncur());
            if (bidTabContract.getRateBuyDown() != 0)
            {	
            	if(StringUtils.isNotEmpty(bidTabContract.getProgRBD())){
            		bidInfo.setProgRBD(Double.valueOf(bidTabContract.getProgRBD()));
            	}
            	
            	if(StringUtils.isNotEmpty(bidTabContract.getIncrRBD())){
            		bidInfo.setIncrRBD(Double.valueOf(bidTabContract.getIncrRBD()));
            	}
            }
            bidInfo.setOrgnlSalesOrdNum(StringHelper.fillString(StringUtils.trim(bidTabContract.getOrgnlSalesOrdNum())));
            bidInfo.setOrgnlQuoteNum(StringHelper.fillString(StringUtils.trim(bidTabContract.getOrgnlQuoteNum())));
            bidInfo.setSplitBidFlag(bidTabContract.isSplitBid());
            setSection(bidInfo, bidTabContract);
            
            TransactionContextManager.singleton().commit();

            logContext.debug(this, "remove attachment file method.");
            SpBidRemoveAttchmtContract rmAttchmtContract = (SpBidRemoveAttchmtContract) contract;

            String webQuoteNum = rmAttchmtContract.getWebQuoteNum();
            String attchmtSeqNum = rmAttchmtContract.getAttchmtSeqNum();
            if (webQuoteNum == null || "".equals(webQuoteNum)) {
                logContext.error(this, "Error! Must provide web quote number for deleting.");
                throw new QuoteException("Must provide web quote number for deleting.");
            }
            if (attchmtSeqNum == null || "".equals(attchmtSeqNum)) {
                logContext.error(this, "Error! Must provide an attachment sequence number for deleting.");
                throw new QuoteException("Must provide an attachment sequence number for deleting.");
            }
            logContext.debug(this, "Remove special bid attachment. webQuoteNum=" + webQuoteNum + "; attchmtSeqNum="
                    + attchmtSeqNum);
            
            QuoteAttachmentProcess process = QuoteAttachmentProcessFactory.singleton().create();
            process.removeQuoteAttachment(webQuoteNum, attchmtSeqNum);

        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException e1) {
            	 logContext.error(this, "Error when rollback transaction of posting draft quote datas");
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
        HashMap map = new HashMap();
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
}
