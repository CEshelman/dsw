package com.ibm.dsw.quote.common.process.jdbc;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.process.SpecialBidRselAuthRuleProcess_Impl;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>WebXmlCnstntProcess_jdbc<code> class.
 *    
 * @author: changwei@cn.ibm.com
 * 
 * Creation date: Apr 21, 2009
 */

public class SpecialBidRselAuthRuleProcess_jdbc extends SpecialBidRselAuthRuleProcess_Impl {

    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.process.WebXmlCnstntProcess#getXmlConfig(java.lang.String)
     */
    public boolean needUpdateRule(String cnstntName, Timestamp ts) throws QuoteException {
        HashMap parms = new HashMap();
        parms.put("piCnstntName", cnstntName);
        parms.put("piUpdatedTime", ts);
        boolean needUpdate = true;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(DraftQuoteDBConstants.S_QT_SPBID_RSELAUTH, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, DraftQuoteDBConstants.S_QT_SPBID_RSELAUTH, parms);

            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                needUpdate = false;
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            this.setXrule(ps.getString(4));
            this.setLastUpdateTime(ps.getTimestamp(5));
        } catch (Exception e) {
            logger.debug(this, e.getMessage());
            throw new QuoteException(e);
        } 
        
        return needUpdate;
    }
    
    private String clobToString(Clob clob) throws SQLException, IOException{
        Reader reader = clob.getCharacterStream();
        CharArrayWriter writer=new CharArrayWriter();
        
        if(reader != null){
            int i=-1;
            while((i=reader.read()) != -1){
            	writer.write(i);
            };
        }
        
        return writer.toString();
    } 
}
