//newfolder
package com.ibm.dsw.quote.relatedbid.process.jdbc;

//import previous files
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.relatedbid.process.RelatedBidProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidProcess_jdbc<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */
 
 
public class RelatedBidProcess_jdbc extends RelatedBidProcess_Impl {

	private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public RelatedBidProcess_jdbc() {
        super();
    }

	public List findAllAgreementTypes(int agrmntTypeFlag) throws QuoteException {
	
		HashMap params = new HashMap();
		params.put("piAgrmntTypeFlag", agrmntTypeFlag);
        ArrayList agrmntTypes = new ArrayList();

        try {
            //begin topaz transaction
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_AGRMNT_TYPES, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_AGRMNT_TYPES, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            
            ResultSet rs = callStmt.getResultSet();
            while (rs.next()) {
                String code = StringUtils.trimToEmpty(rs.getString(1));
                String desc = StringUtils.trimToEmpty(rs.getString(2));
                CodeDescObj_jdbc codeObj = new CodeDescObj_jdbc(code, desc);
                agrmntTypes.add(codeObj);
            }
            rs.close();

            //end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
        
        return agrmntTypes;
	}
}


	
