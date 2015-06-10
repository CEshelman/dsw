package com.ibm.dsw.quote.draftquote.util.date;

import is.domainx.User;

import java.sql.Date;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DateCalculator</code> class is to calculate the start/end date of
 * parts
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 28, 2007
 */

public abstract class DateCalculator {

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;

    protected boolean isQualifiedForDateCalculation = false;

    public DateCalculator(Quote q) {
        this.quote = q;
    }

    public static DateCalculator create(Quote quote) {

        if (quote.getQuoteHeader().isSalesQuote()) {
            return new SalesQuoteDateCalculator(quote);
        } else if (quote.getQuoteHeader().isRenewalQuote()) {
            return new RenewalQuoteDateCalculator(quote);
        }
        logContext.info(DateCalculator.class, "Unsupported Quote Type :type"
                + quote.getQuoteHeader().getQuoteTypeCode());

        return null;
    }

    public abstract void calculateDate();

    public abstract void setLineItemDates() throws TopazException;

    //public abstract List getDateResult();

    public boolean needCalculateDate(User user) {

        QuoteHeader header = quote.getQuoteHeader();
        
        if (!header.isSubmittedQuote()) {
            logContext.debug(this, "This is a draft quote , always need calcuate date");
            return true;
        }

        if (header.getDateOvrrdByApproverFlag()) {
            logContext.debug(this, "The first approver has manually changed the dates");
            return false;
        }
        if (header.getSpeclBidFlag()==1) {
            if (header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isOEMQuote()) {
                boolean spBidReturnedForChanges = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG);
                boolean spBidRejected = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED);
                boolean orderOnHold = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD);
                boolean orderedNotBilled = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED);
                boolean billedOrder = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER);
                boolean cancelledOrTerminated = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED);
                boolean expiredOrOthers = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS);
                logContext.debug(this,"spBidReturnedForChanges="+spBidReturnedForChanges);
                logContext.debug(this,"spBidRejected="+spBidRejected);
                logContext.debug(this,"orderOnHold="+orderOnHold);
                logContext.debug(this,"orderedNotBilled="+orderedNotBilled);
                logContext.debug(this,"billedOrder="+billedOrder);
                logContext.debug(this,"cancelledOrTerminated="+cancelledOrTerminated);
                logContext.debug(this,"expiredOrOthers="+expiredOrOthers);
                if(spBidReturnedForChanges||spBidRejected||orderOnHold||orderedNotBilled||billedOrder||cancelledOrTerminated||expiredOrOthers){
                    return false;
                } else{
                    return true;
                } 
            }
        }else{
            boolean onHold = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD);

            //Ready to order
            boolean readyToOrder = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER);
            logContext.debug(this,"onHold="+onHold);
            logContext.debug(this,"readyToOrder="+readyToOrder);
            
            return onHold || readyToOrder;
        }

        
        return false;

    }
    
    public Date calLicencePartEndDate(boolean isFTL, Date startDate){
        if (isFTL) {
        	return DateUtil.plusOneYearMinusOneDay(startDate);
        } else {
        	return DateUtil.getNonFTLEndDate(startDate);
        }
    }

    /*protected DateResult createDateResult(QuoteLineItem item, Date startDate, Date endDate) {

        DateResult dr = new DateResult(item.getPartNum(), item.getSeqNum());

        dr.stdStartDate = startDate;
        dr.stdEndDate = endDate;

        dr.ovrdStartDateFlag = item.getStartDtOvrrdFlg();
        dr.ovrdEndDateFlag = item.getEndDtOvrrdFlg();

        if (dr.ovrdStartDateFlag) {
            dr.ovrdStartDate = item.getMaintStartDate();
        }
        if (dr.ovrdEndDateFlag) {
            dr.ovrdEndDate = item.getMaintEndDate();
        }
        return dr;
    }*/

}
