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

public class PartsPricingConfigurationPersister extends Persister {


	PartsPricingConfiguration_jdbc partsPricingConfiguration = null;

    /**
     *
     */
    public PartsPricingConfigurationPersister(PartsPricingConfiguration_jdbc partsPricingConfiguration_jdbc) {
        super();
        this.partsPricingConfiguration = partsPricingConfiguration_jdbc;
    }

    /**
     * @param connection
     * @throws TopazException
     */
    public void update(Connection connection) throws TopazException {

        long start = System.currentTimeMillis();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", partsPricingConfiguration.getWebQuoteNum());
        params.put("piConfigrtnId", partsPricingConfiguration.getConfigrtnId());
        params.put("piConfigrtrConfigrtnId", partsPricingConfiguration.getConfigrtrConfigrtnId());
        params.put("piUserID", partsPricingConfiguration.getUserID());
        params.put("piRefDocNum", null);
        params.put("piEstdNumProvisngDays", partsPricingConfiguration.getProvisngDays());
        params.put("piUpdateProvisngDaysFlag", new Integer(1));
        params.put("piConfigrtnErrCode", null);
        params.put("piConfigrtnActionCode", partsPricingConfiguration.getConfigrtnActionCode());
        params.put("piEndDate", partsPricingConfiguration.getEndDate());
        params.put("piCotermConfigrtnId", partsPricingConfiguration.getCotermConfigrtnId());
        params.put("piConfigrtnModDate", partsPricingConfiguration.getConfigrtnModDate());

        params.put("piTermExtensionFlag", partsPricingConfiguration.isTermExtension() == true ? "1" : "0");
        params.put("piServiceDate", partsPricingConfiguration.getServiceDate());
        params.put("piServiceDateModType", (partsPricingConfiguration.getServiceDateModType()==null ? null : partsPricingConfiguration.getServiceDateModType().toString()));
        params.put("piExtEntireConfigFlag", (partsPricingConfiguration.isConfigEntireExtended()? 1 : 0));

        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_CONFIGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_CONFIGRTN, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
        } catch (Exception e) {
            logger.error("Failed to log the configuration to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Update single configuration = "+ (end-start));
    }

    public void delete(Connection connection) throws TopazException {

        long start = System.currentTimeMillis();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", partsPricingConfiguration.getWebQuoteNum());
        params.put("piConfigrtnId", partsPricingConfiguration.getConfigrtnId());
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_WEB_CONFIGRTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_D_QT_WEB_CONFIGRTN, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isDeleted(true);
        } catch (Exception e) {
            logger.error("Failed to delete the configuration from the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Delete Single configuration = "+(end-start));
    }

    public void hydrate(Connection connection) throws TopazException {
        // may not need this, we always use SP to return all line items of
        // the part
        //implement this method only if we have SP to return one line item

    }

    public void insert(Connection connection) throws TopazException {

    }


}
