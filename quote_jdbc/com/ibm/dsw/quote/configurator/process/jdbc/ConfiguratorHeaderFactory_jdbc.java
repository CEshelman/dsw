/**
 * 
 */
package com.ibm.dsw.quote.configurator.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.CountryFactory;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.configurator.process.ConfiguratorHeaderFactory;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public class ConfiguratorHeaderFactory_jdbc extends ConfiguratorHeaderFactory {

	private LogContext logContext = LogContextFactory.singleton().getLogContext();

	public ConfiguratorHeaderFactory_jdbc() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.ConfiguratorHeaderFactory#getHdrInfoForMonthlyConfiguratorByWebQuoteNum(java.lang.String)
	 */
	@Override
	public ConfiguratorHeader getHdrInfoForMonthlyConfiguratorByWebQuoteNum(String webQuoteNum, String userId) throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", webQuoteNum);
		params.put("piUserId", userId);

		ResultSet rs = null;
		int poGenStatus = 0;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB_S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR,
					null);
			CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(callStmt, CommonDBConstants.DB_S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR, params);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

			tracer.stmtTraceStart("call S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR");
			boolean retCode = callStmt.execute();
			tracer.stmtTraceEnd("call S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR");
			poGenStatus = callStmt.getInt(1);

			if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
			}

			rs = callStmt.getResultSet();
			ConfiguratorHeader configHeader = new ConfiguratorHeader();
			configHeader.setWebQuoteNum(webQuoteNum);
			while (rs.next()) {
				String lobCode = StringUtils.trimToEmpty(rs.getString("prog_code"));
				configHeader.setLob(lobCode);
				Country country = CountryFactory.singleton().findByCode3(StringUtils.trimToEmpty(rs.getString("cntry_code")));
				configHeader.setCntryCode(country.getCode3());
				configHeader.setCntryCodeDscr(country.getDesc());
				String currencyCode = StringUtils.trimToEmpty(rs.getString("currncy_code"));
				configHeader.setCurrencyCode(currencyCode);
				configHeader.setCurrencyCodeDscr(getCurrencyDesc(country, currencyCode));

				configHeader.setCustomerNumber(StringUtils.trimToEmpty(rs.getString("sold_to_cust_num")));
				configHeader.setSapContractNum(StringUtils.trimToEmpty(rs.getString("sap_ctrct_num")));
				configHeader.setAudience(StringUtils.trimToEmpty(rs.getString("AUD_CODE")));
				configHeader.setAcqrtnCode(StringUtils.trimToEmpty(rs.getString("ACQRTN_CODE")));
				configHeader.setQuoteTypeCode(StringUtils.trimToEmpty(rs.getString("QUOTE_TYPE_CODE")));
				configHeader.setProgMigrationCode(StringUtils.trimToEmpty(rs.getString("PROG_MIGRTN_CODE")));
				configHeader.setConfigId(StringUtils.trimToEmpty(rs.getString("CONFIGRTN_ID")));
				configHeader.setWebQuoteNum(StringUtils.trimToEmpty(rs.getString("WEB_QUOTE_NUM")));
			}
			return configHeader;
		} catch (SQLException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new TopazException("Exception when execute the SP "
					+ CommonDBConstants.DB_S_QT_GET_HDRINFO_FOR_MONTHLY_BAISC_CONFIGURATOR, e);
		} finally {
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
		}

	}

	protected String getCurrencyDesc(Country cntry, String currencyCode) {
		String currencyDesc = null;
		if (cntry != null) {
			List currencyList = cntry.getCurrencyList();
			if (currencyList != null && currencyList.size() > 0) {
				for (int i = 0; i < currencyList.size(); i++) {
					CodeDescObj obj = (CodeDescObj) currencyList.get(i);
					if (obj != null) {
						String objKey = obj.getCode();
						if (objKey != null && objKey.equalsIgnoreCase(currencyCode))
							currencyDesc = obj.getCodeDesc();
					}
				}
			}
		}
		return StringUtils.isNotBlank(currencyDesc) ? currencyDesc : currencyCode;
	}

}
