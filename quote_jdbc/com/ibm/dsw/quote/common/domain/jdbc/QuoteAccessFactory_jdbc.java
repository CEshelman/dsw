package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteAccessFactory;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAccessFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Jun 12, 2009
 */

public class QuoteAccessFactory_jdbc extends QuoteAccessFactory {
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteAccessFactory_jdbc() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteAccessFactory#getRenwlQuoteStatus(java.lang.String, java.lang.String, int, int, int, boolean)
     */
    public Map getRenwlQuoteStatus(String quoteNum, String userIdList, int qaNeeded, int steNeeded, int duration,
            boolean checkSalesRep) throws TopazException {
        
        HashMap parms = new HashMap();
        HashMap results = new HashMap();
        parms.put("piQuoteNum", quoteNum == null ? "" : quoteNum);
        parms.put("piUserIDList", userIdList == null ? "" : userIdList);
        parms.put("piQANeeded", new Integer(qaNeeded));
        parms.put("piSTENeeded", new Integer(steNeeded));
        parms.put("piDuration", new Integer(duration));
        
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.S_QT_QDTL_STAT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.S_QT_QDTL_STAT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int returnCode = ps.getInt(24);

            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logContext.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new SPException(returnCode);
            }
            
            boolean quoteEditable = (ps.getInt(6) == 1);
            boolean quoteOrderable = (ps.getInt(7) == 1);
            boolean canBeAddedToSQ = (ps.getInt(9) == 1);
            boolean salesTrackingEditable = (ps.getInt(10) == 1);
            Date quoteModDate = ps.getTimestamp(16);
            Date quoteStatusModDate = ps.getTimestamp(17);
            String sapDocNum = ps.getString(15);
            
            // for editRQAction, editRQSalesTrackAction, editRQStatusAction, check sales rep
            // for draft quote tabs user access validation, don't check sales rep
            boolean isSalesRep = (ps.getInt(11) == 1 || !checkSalesRep);
            boolean isQuotingRep = (ps.getInt(12) == 1);
            String rnwlStatus = StringUtils.trimToEmpty(ps.getString(14));
            boolean isFCT = ("1".equals(ps.getString(21)));
            boolean isSpecialBidCreatable = (ps.getInt(22) == 1);
            boolean isGPEEditable = (ps.getInt(23) == 1);
            
            // change quoteEditable & salesTrackingEditable if FCT renewal quote
            boolean isUserValid = isUserValid(isFCT, rnwlStatus, isSalesRep, isQuotingRep);
            quoteEditable = quoteEditable && isUserValid;
            salesTrackingEditable = salesTrackingEditable && isUserValid;
            canBeAddedToSQ = canBeAddedToSQ || canBeAddedToSQ(isFCT, rnwlStatus);

            results.put(QuoteCapabilityProcess.EDIT_RQ, Boolean.valueOf(quoteEditable));
            results.put(QuoteCapabilityProcess.EDIT_RQ_STATUS, Boolean.FALSE);
            results.put(QuoteCapabilityProcess.EDIT_RQ_ST, Boolean.valueOf(salesTrackingEditable));
            results.put(QuoteCapabilityProcess.ORDER_RQ, Boolean.valueOf(quoteOrderable));
            results.put(QuoteCapabilityProcess.ADD_RQ_TO_SQ, Boolean.valueOf(canBeAddedToSQ));
            results.put(QuoteCapabilityProcess.CREATE_RQ_SPECL_BID, Boolean.valueOf(isSpecialBidCreatable));
            results.put(QuoteCapabilityProcess.RQ_MOD_DATE, quoteModDate);
            results.put(QuoteCapabilityProcess.RQ_STATUS_MOD_DATE, quoteStatusModDate);
            results.put(QuoteCapabilityProcess.RQ_IDOC_NUM, sapDocNum);
            results.put(QuoteCapabilityProcess.IS_SALES_REP, Boolean.valueOf(isSalesRep));
            results.put(QuoteCapabilityProcess.IS_QUOTING_REP, Boolean.valueOf(isQuotingRep));
            results.put(QuoteCapabilityProcess.EDIT_GPE_RQ, Boolean.valueOf(isGPEEditable));
            results.put(QuoteCapabilityProcess.IS_USER_VALID, Boolean.valueOf(isUserValid));
            
        } catch (SQLException e) {
            logContext.debug(this, e.getMessage());
            throw new TopazException(e);
        }
        
        return results;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteAccessFactory#getRenwlQuoteAccess(java.lang.String, java.lang.String, int, int, int, boolean)
     */
    public QuoteAccess getRenwlQuoteAccess(String quoteNum, String userIdList, int qaNeeded, int steNeeded,
            int duration, boolean checkSalesRep) throws TopazException {
        
        Map statusMap = null;
        QuoteAccess access = new QuoteAccess();
        
        statusMap = getRenwlQuoteStatus(quoteNum, userIdList, qaNeeded, steNeeded, QuoteConstants.RNWL_QUOTE_DURATION,
                checkSalesRep);
        
        boolean quoteEditable = ((Boolean) statusMap.get(QuoteCapabilityProcess.EDIT_RQ)).booleanValue();
        boolean quoteStatusEditable = ((Boolean) statusMap.get(QuoteCapabilityProcess.EDIT_RQ_STATUS)).booleanValue();
        boolean salesTrackingEditable = ((Boolean) statusMap.get(QuoteCapabilityProcess.EDIT_RQ_ST)).booleanValue();
        boolean quoteOrderable = ((Boolean) statusMap.get(QuoteCapabilityProcess.ORDER_RQ)).booleanValue();
        boolean canBeAddedToSQ = ((Boolean) statusMap.get(QuoteCapabilityProcess.ADD_RQ_TO_SQ)).booleanValue();
        boolean canCreateRQSpeclBid = ((Boolean) statusMap.get(QuoteCapabilityProcess.CREATE_RQ_SPECL_BID))
                .booleanValue();
        boolean isSalesRep = ((Boolean) statusMap.get(QuoteCapabilityProcess.IS_SALES_REP)).booleanValue();
        boolean isQuotingRep = ((Boolean) statusMap.get(QuoteCapabilityProcess.IS_QUOTING_REP)).booleanValue();
        boolean isGPEEditable = ((Boolean) statusMap.get(QuoteCapabilityProcess.EDIT_GPE_RQ)).booleanValue();
        
        access.setCanEditRQ(quoteEditable);
        access.setCanUpdateRQStatus(quoteStatusEditable);
        access.setCanEditRQSalesInfo(salesTrackingEditable);
        access.setCanOrderRQ(quoteOrderable);
        access.setCanBeAddedToSQ(canBeAddedToSQ);
        access.setCanCreateRQSpeclBid(canCreateRQSpeclBid);
        access.setSalesRep(isSalesRep);
        access.setQuotingRep(isQuotingRep);
        access.setCanEditGPExprdRQ(isGPEEditable);
        
        return access;
    }
    
    protected boolean canBeAddedToSQ(boolean isFCT, String rnwlStatus) {
        return (isFCT && (QuoteConstants.RENEWAL_QUOTE_STATUS_READY_ON_HOLD.equalsIgnoreCase(rnwlStatus)
                || QuoteConstants.RENEWAL_QUOTE_STATUS_WITH_SALES_FOR_REVIEW.equalsIgnoreCase(rnwlStatus)));
    }
    
    protected boolean isUserValid(boolean isFCT, String rnwlStatus, boolean isSalesRep, boolean isQuotingRep) {
        if (isFCT) {
            if (QuoteConstants.RENEWAL_QUOTE_STATUS_READY_ON_HOLD.equalsIgnoreCase(rnwlStatus))
                return isQuotingRep;
            else
                return (isSalesRep || isQuotingRep);
        }
        else {
            return (isSalesRep || isQuotingRep);
        }
    }

}
