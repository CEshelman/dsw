package com.ibm.dsw.quote.common.domain.jdbc;

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

public class MonthlySoftwareConfigurationPersister extends Persister {
	
	public MonthlySoftwareConfigurationPersister(MonthlySoftwareConfiguration_jdbc monthlySwConfigrtn) {
		super();
		this.monthlySwConfigrtn = monthlySwConfigrtn;
	}

	MonthlySoftwareConfiguration_jdbc monthlySwConfigrtn = null;
	
	@Override
	public void update(Connection connection) throws TopazException {
		
		long start = System.currentTimeMillis();
		HashMap params = new HashMap();
		
		params.put("piWebQuoteNum", monthlySwConfigrtn.getWebQuoteNum());
		params.put("piConfigrtnId", monthlySwConfigrtn.getConfigrtnId());
		params.put("piConfigrtnActionCode", monthlySwConfigrtn.getConfigrtnActionCode());
		params.put("piCotermConfigrtnId", monthlySwConfigrtn.getCotermConfigrtnId());
		params.put("piUserID", monthlySwConfigrtn.getUserID());
		params.put("piServiceDateModType", monthlySwConfigrtn.getServiceDateModType());
		params.put("piServiceDate", monthlySwConfigrtn.getServiceDate());
		params.put("piTermExtensionFlag", monthlySwConfigrtn.isTermExtension()? "1" : "0");
		params.put("piEndDate", monthlySwConfigrtn.getEndDate());
		params.put("piGlobalTerm", monthlySwConfigrtn.getGlobalTerm());
		params.put("piNeedReconfigFlag", monthlySwConfigrtn.isNeedReconfigFlag()? "1" : "0");
		params.put("piGlobalBillgFrqncyCode",monthlySwConfigrtn.getGlobalBillingFrequencyCode());
		params.put("piExtEntireConfigrtnFlag", monthlySwConfigrtn.isConfigEntireExtended()? "1" : "0");
		params.put("piIsAddNewMonthlySWFlag",monthlySwConfigrtn.isAddNewMonthlySWFlag()? "1" : "0");
		params.put("PiCANum",monthlySwConfigrtn.getChrgAgrmtNum());
		params.put("piConfigrtnIdFrmCA",monthlySwConfigrtn.getConfigrtnIdFromCa());
		int retCode = -1;
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	    try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MONTHLY_SW_CONFGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MONTHLY_SW_CONFGRTN, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
        } catch (Exception e) {
            logger.error("Failed to log the monthly SW configurator to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Update single monthly SW configurator = "+ (end-start));
	}

	@Override
	public void delete(Connection connection) throws TopazException {
		long start = System.currentTimeMillis();
		HashMap params = new HashMap();
		
		params.put("piWebQuoteNum", monthlySwConfigrtn.getWebQuoteNum());
		params.put("piConfigrtnId", monthlySwConfigrtn.getConfigrtnId());
		
		int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_MONTHLY_SW_CONFIGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_D_QT_MONTHLY_SW_CONFIGRTN, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isDeleted(true);
        } catch (Exception e) {
            logger.error("Failed to delete the monthly SW Configurator from the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Delete Single monthly SW Configurator = "+(end-start));
		
	}

	@Override
	public void hydrate(Connection connection) throws TopazException {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(Connection connection) throws TopazException {
		
		long start = System.currentTimeMillis();
		HashMap params = new HashMap();
		
		params.put("piWebQuoteNum", monthlySwConfigrtn.getWebQuoteNum());
		params.put("piConfigrtnId", monthlySwConfigrtn.getConfigrtnId());
		params.put("piConfigrtnActionCode", monthlySwConfigrtn.getConfigrtnActionCode());
		params.put("piCotermConfigrtnId", monthlySwConfigrtn.getCotermConfigrtnId());
		params.put("piUserID", monthlySwConfigrtn.getUserID());
		params.put("piServiceDateModType", monthlySwConfigrtn.getServiceDateModType());
		params.put("piServiceDate", monthlySwConfigrtn.getServiceDate());
		params.put("piTermExtensionFlag", monthlySwConfigrtn.isTermExtension()? "1" : "0");
		params.put("piEndDate", monthlySwConfigrtn.getEndDate());
		params.put("piGlobalTerm", monthlySwConfigrtn.getGlobalTerm());
		params.put("piNeedReconfigFlag", monthlySwConfigrtn.isNeedReconfigFlag()? "1" : "0");
		params.put("piGlobalBillgFrqncyCode",monthlySwConfigrtn.getGlobalBillingFrequencyCode());
		params.put("piExtEntireConfigrtnFlag", monthlySwConfigrtn.isConfigEntireExtended()? "1" : "0");
		params.put("piIsAddNewMonthlySWFlag",monthlySwConfigrtn.isAddNewMonthlySWFlag()? "1" : "0");
		params.put("PiCANum",monthlySwConfigrtn.getChrgAgrmtNum());
		params.put("piConfigrtnIdFrmCA",monthlySwConfigrtn.getConfigrtnIdFromCa());
		
		int retCode = -1;
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	    try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MONTHLY_SW_CONFGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MONTHLY_SW_CONFGRTN, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isNew(true);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to log the monthly SW configurator to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Update single monthly SW configurator = "+ (end-start));

	}

}
