/**
 * 
 */
package com.ibm.dsw.quote.configurator.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwNewCAConfiguratorPart;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlySwNewCAConfigurator_jdbc
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 4:06:48 PM
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MonthlySwNewCAConfigurator_jdbc extends MonthlySwConfiguratorProcess_jdbc {

    protected void setConfiguratorActionPart() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.configurator.process.MonthlySwConfiguratorJDBC#getMonthlySwConfgiuratorParts()
     */
    @Override
	public List<MonthlySwConfiguratorPart> getMonthlySwConfgiuratorParts(String webQuoteNum, String caNum, String configId,
			String userId)
            throws TopazException {

        TimeTracer tracer = TimeTracer.newInstance();
        // call sp
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);

        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rs = null;

        /**
         * just store main parts
         */
        List<MonthlySwConfiguratorPart> masterMonthlySwParts = new ArrayList<MonthlySwConfiguratorPart>();

        try {
            QueryContext context = QueryContext.getInstance();

            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_NEW_MOD, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_NEW_MOD, params);

            tracer.stmtTraceStart("call S_QT_GET_MONTHLY_SW_CONFGRTN_NEW_MOD");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call S_QT_GET_MONTHLY_SW_CONFGRTN_NEW_MOD");
            if (psResult) {
                rs = ps.getResultSet();

                /**
                 * key : subId value: MonthlySwConfiguratorPart store subscrption part \ ondemand part
                 */
                Map<String, MonthlySwConfiguratorPart> configuratorPartMap = new HashMap<String, MonthlySwConfiguratorPart>();

                while (rs.next()) {

                    MonthlySwConfiguratorPart monthlyPart = null;

                    String subId = StringUtils.trim(rs.getString("sw_sbscrptn_id"));

                    boolean isSubscrtnPart = "1".equals(rs.getString("MONTHLY_SW_SBSCRPTN_FLAG"));
                    boolean isOveragePart = "1".equals(rs.getString("MONTHLY_SW_SBSCRPTN_OVRAGE_FLAG"));
                    boolean isDailyPart = "1".equals(rs.getString("MONTHLY_SW_DLY_FLAG"));
                    boolean isOnDemand = "1".equals(rs.getString("MONTHLY_SW_ON_DMND_FLAG"));
                    boolean isRampUp = "1".equals(rs.getString("RAMP_UP_FLAG"));

                    processPartsBaseOnType(rs, masterMonthlySwParts, configuratorPartMap, subId, isSubscrtnPart, isOveragePart,
                            isDailyPart, isOnDemand, isRampUp, monthlyPart);

                }

            }
        } catch (SQLException sqle) {
            logger.error("Failed to get the configurator parts from the database!", sqle);
            throw new TopazException(sqle);
        } finally {
            try {
                if (null != rs && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                logger.error("Failed to close the resultset!", e);
            }

            tracer.dump();
        }

        return masterMonthlySwParts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.configurator.process.jdbc.MonthlySwConfiguratorProcess_jdbc#setMonthlySwActionPart(java.sql
     * .ResultSet, com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
     */
    @Override
    protected void setMonthlySwActionPart(ResultSet rs, MonthlySwConfiguratorPart monthlyPart) throws SQLException {
        MonthlySwNewCAConfiguratorPart newCAConfiguratorPart = new MonthlySwNewCAConfiguratorPart(monthlyPart);
        this.setMonthlySwActionPartAttribute(rs, newCAConfiguratorPart);
        monthlyPart.setConfiguratorActionPart(newCAConfiguratorPart);

    }

}
