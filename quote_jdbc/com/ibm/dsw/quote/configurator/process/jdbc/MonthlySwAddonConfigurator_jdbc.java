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
import com.ibm.dsw.quote.configurator.domain.MonthlySwAddonTradeupConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlySwAddonConfigurator_jdbc
 * @author Frank
 * @Description: TODO
 * @date Jan 8, 2014 5:48:36 PM
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MonthlySwAddonConfigurator_jdbc extends MonthlySwConfiguratorProcess_jdbc {

    /**
     * get configuratorParts From ca
     */
    @Override
    public List<MonthlySwConfiguratorPart> getMonthlySwConfgiuratorParts(String webQuoteNum, String caNum, String configId,
            String userId) throws TopazException {
        TimeTracer tracer = TimeTracer.newInstance();
        // call sp
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piCaNum", caNum);
        params.put("piConfigId", configId);
        params.put("piUserId", userId);

        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rs = null;

        /**
         * just store main parts
         */
        List<MonthlySwConfiguratorPart> masterMonthlySwParts = new ArrayList<MonthlySwConfiguratorPart>();

        try {
            QueryContext context = QueryContext.getInstance();

            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_FRM_CA, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_FRM_CA, params);

            tracer.stmtTraceStart("call DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_FRM_CA");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call DB2_S_QT_GET_MONTHLY_SW_CONFGRTN_FRM_CA");
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
        MonthlySwAddonTradeupConfiguratorPart addonConfiguratorPart = new MonthlySwAddonTradeupConfiguratorPart(monthlyPart);
        super.setMonthlySwActionPartAttribute(rs, addonConfiguratorPart);
        setCAMonthlysSwActionPart(rs, addonConfiguratorPart);
        monthlyPart.setConfiguratorActionPart(addonConfiguratorPart);
    }

    private void setCAMonthlysSwActionPart(ResultSet rs, MonthlySwAddonTradeupConfiguratorPart addonConfiguratorPart)
            throws SQLException {
        addonConfiguratorPart.setMonthlySwSectionFlag(rs.getString("SECTION_FLAG"));
        addonConfiguratorPart.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
        addonConfiguratorPart.setEndDate(rs.getDate("END_DATE"));
        addonConfiguratorPart.setRenewalEndDate(rs.getDate("RENWL_END_DATE"));
        addonConfiguratorPart.setNextRenwlDate(rs.getDate("NEXT_RENWL_DATE"));
        addonConfiguratorPart.setRenwlMdlCode(StringUtils.trim(rs.getString("RENWL_MDL_CODE")));
        addonConfiguratorPart.setRenwlTermMths(rs.getInt("RENWL_TERM_MTHS"));
        addonConfiguratorPart.setPartQty(rs.getObject("ORIGINAL_PART_QTY") == null ? null : new Integer(rs
                .getInt("ORIGINAL_PART_QTY")));
        addonConfiguratorPart.setSapBillgFrqncyOptCode(rs.getString("BILLG_FRQNCY_CODE"));
        addonConfiguratorPart.setLocalExtndPrice(rs.getObject("LOCAL_EXTND_PRICE") == null ? null : rs.getDouble("LOCAL_EXTND_PRICE"));
        addonConfiguratorPart.setLocalOvrageAmt(rs.getObject("LOCAL_SAAS_OVRAGE_AMT") == null ? null : rs.getDouble("LOCAL_SAAS_OVRAGE_AMT"));
        addonConfiguratorPart.setTotCmmtmtVal(rs.getObject("SAAS_TOT_CMMTMT_VAL") == null ? null : rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
    }

}
