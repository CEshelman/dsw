package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.RowExistingException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.submittedquote.config.SubmittedDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ReviewerPersister</code> class is the persister for the Reviewer
 * domain object.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class ReviewerPersister extends Persister {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    Reviewer_jdbc reviewer_jdbc;

    public ReviewerPersister(Reviewer_jdbc reviewer_jdbc) {
        super();
        this.reviewer_jdbc = reviewer_jdbc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        //TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        int poGenStatus = -1;
        HashMap params = new HashMap();
        
        Integer iTxtId = reviewer_jdbc.getQuoteTxtId() == -1 ? null : new Integer(reviewer_jdbc.getQuoteTxtId());
        params.put("piWebQuoteNum", reviewer_jdbc.getWebQuoteNum());
        params.put("piUserEmail", reviewer_jdbc.getUserEmail());
        params.put("piReviewerEmail", reviewer_jdbc.getReviewEmail());
        params.put("piQuoteTxtId", iTxtId);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(SubmittedDBConstants.IU_QT_SB_REVIEWER, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement statement = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(statement, SubmittedDBConstants.IU_QT_SB_REVIEWER, params);
            
            statement.execute();
            poGenStatus = statement.getInt(1);
            
            //System.err.println("poGenStatus in ReviewerPersister : " + poGenStatus);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_ROW_EXISTING == poGenStatus) {
                throw new RowExistingException("Row existing: " + sqlQuery);
            } else if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != poGenStatus) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }

        }catch(RowExistingException ree){
            //System.err.println("RowExistingException " + ree.getMessage());
            throw ree;
        }catch (Exception e) {
            //e.printStackTrace();
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + SubmittedDBConstants.IU_QT_SB_REVIEWER, e);
        }
    }

}
