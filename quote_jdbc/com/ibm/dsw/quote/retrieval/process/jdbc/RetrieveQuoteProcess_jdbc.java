package com.ibm.dsw.quote.retrieval.process.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.retrieval.RetrieveQuoteResultCodes;
import com.ibm.dsw.quote.retrieval.exception.RetrieveQuoteException;
import com.ibm.dsw.quote.retrieval.process.RetrieveQuoteProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteProcess_jdbc.java</code>
 * 
 * @author: tom_boulet@us.ibm.com
 * 
 * Created on: 2007-05-08
 */

public class RetrieveQuoteProcess_jdbc extends RetrieveQuoteProcess_Impl {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected Map validateSalesQuote(String sapQuoteNum, String sapQuoteIDoc, String webQuoteNum, String fulfillment, String userID, boolean external) throws QuoteException {
        int result = 0;
        int poGenStatus = 9999;
        String returnedWebQuoteNum = "";
        Map returnValues = new HashMap();
    
        try {
            this.beginTransaction();
    
            HashMap parms = new HashMap();
            parms.put("piSapQuoteNum", sapQuoteNum);
            parms.put("piSapIdocNum", sapQuoteIDoc);
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piFulfllSrcCode", fulfillment);
            parms.put("piUserId", userID.toLowerCase());
            parms.put("piExternalFlag", Boolean.valueOf(external));
    
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery("VALIDATE_QUOTE_FOR_RETRIEVAL", null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
    
            CallableStatement callStmt = TransactionContextManager.singleton().getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, "VALIDATE_QUOTE_FOR_RETRIEVAL", parms);
    
            boolean retCode = callStmt.execute();
            poGenStatus = callStmt.getInt(1);
            logContext.debug(this, "return code from " + sqlQuery + ": " + poGenStatus);
            returnedWebQuoteNum = callStmt.getString(8);
            logContext.debug(this, "webQuoteNum " + returnedWebQuoteNum + " returned from SP");
            returnValues.put("poGenStatus", new Integer(poGenStatus));
            returnValues.put("webQuoteNum", returnedWebQuoteNum);
            
            //eligFirmOrdExcptn
            int eligFirmOrdExcptn = callStmt.getInt(9);
            logContext.debug(this, "eligFirmOrdExcptn " + eligFirmOrdExcptn + " returned from SP");
            returnValues.put("eligFirmOrdExcptn", eligFirmOrdExcptn);
    
            this.commitTransaction();
        } catch (SQLException se) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(se));
            throw new RetrieveQuoteException("Exception when execute the SP " + "VALIDATE_QUOTE_FOR_RETRIEVAL",
                    RetrieveQuoteResultCodes.UNKNOWN_ERROR_CODE);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    
        return returnValues;
    }

}
