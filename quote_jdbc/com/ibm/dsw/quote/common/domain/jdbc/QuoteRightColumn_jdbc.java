package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn_Impl;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteRightColumn_jdbc<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 5, 2007
 */

public class QuoteRightColumn_jdbc extends QuoteRightColumn_Impl implements PersistentObject, Serializable {

    private transient LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteRightColumn_jdbc(String creatorId){
        super();
        this.creatorId = creatorId;
    }
    /**
     * Call SP to get the quote right column inforamtion
     */
    public void hydrate(Connection connection) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCreatorId", creatorId);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_SSNQT_INFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_SSNQT_INFO, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA || StringUtils.isBlank(ps.getString(3))) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            this.sWebQuoteNum = StringUtils.trimToEmpty(ps.getString(3));
            this.sCustName = StringUtils.trimToEmpty(ps.getString(4));
            this.iNumOfParts = Integer.parseInt(ps.getString(5));
            this.sQuoteTypeCode = StringUtils.trimToNull(ps.getString(6));
            this.setQtCopyType(ps.getInt(7));
            
            this.isNew(false);
            this.isDeleted(false);
        } catch (SQLException e) {
            throw new TopazException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        // TODO Auto-generated method stub

    }

}
