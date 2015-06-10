/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExecSummaryPersister extends Persister {
	public static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	private ExecSummary_jdbc execSummary_jdbc = null;
	
	public ExecSummaryPersister(ExecSummary_jdbc execSummary_jdbc){
		super();
		this.execSummary_jdbc = execSummary_jdbc;
	}
	

	/* (non-Javadoc)
	 * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
	 */
	public void update(Connection connection) throws TopazException {
		HashMap params = new HashMap();
		
		params.put("piWebQuoteNum", execSummary_jdbc.getWebQuoteNum());
		params.put("piUserID", execSummary_jdbc.getUserId());
		
		if(execSummary_jdbc.getRecmdtFlag() == null){
			params.put("piRecmdtFlag", null);
		} else {
			params.put("piRecmdtFlag", execSummary_jdbc.getRecmdtFlag().booleanValue()? new Integer(1) : new Integer(0));
		}
		params.put("piBookbleRevenue", execSummary_jdbc.getPeriodBookableRevenue());
		params.put("piRecmdtnText", execSummary_jdbc.getRecmdtText());
		params.put("piExecSupport", execSummary_jdbc.getExecSupport());
		params.put("piBriefOverviewText", execSummary_jdbc.getBriefOverviewText());
		params.put("piServiceRevenue", execSummary_jdbc.getServiceRevenue());

		try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_EXEC_SUMRY, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_EXEC_SUMRY, params);
            ps.execute();
            int retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            
        } catch (Exception e) {
            logger.error("Failed to update executive summary info to database!", e);
            throw new TopazException(e);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
	 */
	public void delete(Connection connection) throws TopazException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
	 */
	public void hydrate(Connection connection) throws TopazException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
	 */
	public void insert(Connection connection) throws TopazException {
		update(connection);
	}

}
