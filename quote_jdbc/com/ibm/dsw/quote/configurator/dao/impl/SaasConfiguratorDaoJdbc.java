package com.ibm.dsw.quote.configurator.dao.impl;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.SaasConfiguration;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDao;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.helper.BillingOptionHelper;
import com.ibm.dsw.quote.configurator.process.jdbc.AbstractSaasConfiguratorProcess;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

public class SaasConfiguratorDaoJdbc implements SaasConfiguratorDao {
	private LogContext logContext = LogContextFactory.singleton().getLogContext();
	protected String getConfigIdForSetDefaultProvisngDays(String configId,
			String orgConfigrtnId) {
		return configId;
	}
	public  void setDefaultProvisngDays(String webQuoteNum,String configrtnIdParam,String orgConfigrtnIdParam)
			throws TopazException {		
			TransactionContextManager.singleton().begin();
			String configrtnId = getConfigIdForSetDefaultProvisngDays(
					configrtnIdParam, orgConfigrtnIdParam);
			// setDefaultProvisngDays(ct.getWebQuoteNum(), configrtnId);
			// TODO need to move
			// ConfiguratorPartProcess_Impl.setDefaultProvisngDays
			// to here
			HashMap params = new HashMap();

			params.put("piWebQuoteNum", webQuoteNum);
			params.put("piConfigrtnId", configrtnId);

			int retCode = -1;

			try {

				QueryContext context = QueryContext.getInstance();
				String sqlQuery = context.getCompletedQuery(
						CommonDBConstants.DB2_U_QT_ESTD_PROVISNG_DAYS, null);
				logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
				CallableStatement ps = TopazUtil.getConnection().prepareCall(
						sqlQuery);
				context.completeStatement(ps,
						CommonDBConstants.DB2_U_QT_ESTD_PROVISNG_DAYS, params);
				ps.execute();
				retCode = ps.getInt(1);
				if (retCode != 0) {
					throw new TopazException("SP call returns error code: "
							+ retCode);
				}
			} catch (Exception e) {
				logContext.error(
						"Failed to update or insert SaaS configuration!", e);
				throw new TopazException(e);
			}
			TransactionContextManager.singleton().commit();
		}
	

	public List findConfiguratorsByWebQuoteNum(String webQuoteNum)
			throws TopazException {
		List list = null;
		try {			
			list = PartsPricingConfigurationFactory.singleton()
					.findPartsPricingConfiguration(webQuoteNum);			
		} catch (TopazException e) {
			logContext.error(this, e);
			TransactionContextManager.singleton().rollback();
			throw e;
		}
		return list;
	}
	/**
	 * Retrieve all active subscription parts within a charge agreement
	 */
	public List<ConfiguratorPart> getSubPartsFromChrgAgrm(String chrgAgrmtNum,
			String configId) throws QuoteException {
		List<ConfiguratorPart> configrtrPartList = new ArrayList<ConfiguratorPart>();
		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", chrgAgrmtNum);

		if (StringUtils.isNotBlank(configId)) {
			params.put("piConfigId", configId);
		}
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(
					CommonDBConstants.DB2_S_QT_GET_CA_SUB_PARTS, null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_GET_CA_SUB_PARTS, params);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(1);

			logContext.debug(this, "the return code of calling " + sqlQuery
					+ " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			rs = ps.getResultSet();
			while (rs.next()) {
				ConfiguratorPart part = new ConfiguratorPart();
				configrtrPartList.add(part);
				part.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));
				part.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
				part.setEndDate(rs.getDate("END_DATE"));
				part.setRenewalEndDate(rs.getDate("RENWL_END_DATE"));
				part.setSapMatlTypeCode(StringUtils.trim(rs
						.getString("SAP_MATL_TYPE_CODE")));
				// 13.4kenexa
				part.setSubsumedSubscrptn("1".equals(StringUtils.trim(rs
						.getString("SUB_SUMED_SCRIPTION_FLAG"))));
				part.setConfigrtnId(StringUtils.trim(rs
						.getString("CONFIGRTN_ID")));
				part.setCotermEndDate(rs.getDate("COTERM_END_DATE"));
				part.setSapBillgFrqncyOptCode(StringUtils.trim(rs
						.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
				part.setNextRenwlDate(rs.getDate("NEXT_RENWL_DATE"));
				part.setRenwlTermMths(rs.getInt("RENWL_TERM_MTHS"));
				part.setRenwlMdlCode(StringUtils.trim(rs
						.getString("RENWL_MDL_CODE")));
			}
		} catch (SQLException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} finally {
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
		}

		return configrtrPartList;
	}

	public List findPartsByWebQuoteNumPID(String webQuoteNum, String pid)
			throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		List<ConfiguratorPart> result = new ArrayList();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", webQuoteNum);
		params.put("piPID", pid);
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		ResultSet rs = null;
		ResultSet rsbo = null;
		try {
			TransactionContextManager.singleton().begin();
			BillingOptionFactory.singleton().getBillingOptionMap();
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					CommonDBConstants.DB2_S_QT_PART_BY_PID, null);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			context.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_PART_BY_PID, params);

			tracer.stmtTraceStart("call S_QT_PART_BY_PID");
			boolean psResult = ps.execute();
			tracer.stmtTraceEnd("call S_QT_PART_BY_PID");
			ConfiguratorPart cp = null;
			if (psResult) {
				rs = ps.getResultSet();
				while (rs.next()) {
					cp = new ConfiguratorPart();
					cp.setPartNum(StringUtils.trimToEmpty(rs
							.getString("PART_NUM")));
					cp.setSwProdBrandCode(StringUtils.trimToEmpty(rs
							.getString("sw_prod_brand_code")));
					cp.setPartDscr(rs.getString(StringUtils
							.trimToEmpty("PART_DSCR_LONG")));
					cp.setSapMatlTypeCode(StringUtils.trimToEmpty(rs
							.getString("SAP_MATL_TYPE_CODE")));
					cp.setSapMatlTypeCodeGroupCode(StringUtils.trimToEmpty(rs
							.getString("SAP_MATL_TYPE_CODE_GROUP_CODE")));
					cp.setSapMatlTypeCodeGroupDscr(StringUtils.trimToEmpty(rs
							.getString("SAP_MATL_TYPE_CODE_GROUP_DSCR")));
					cp.setPricingTierModel(StringUtils.trimToEmpty(rs
							.getString("PRICNG_TIER_MDL")));
					cp.setWwideProdCode(StringUtils.trimToEmpty(rs
							.getString("WWIDE_PROD_CODE")));
					cp.setWwideProdCodeDscr(StringUtils.trimToEmpty(rs
							.getString("WWIDE_PROD_CODE_DSCR")));
					cp.setPidDscr(StringUtils.trimToEmpty(rs
							.getString("IBM_PROD_ID_DSCR")));
					cp.setSubId(StringUtils.trimToEmpty(rs
							.getString("sw_sbscrptn_id")));
					cp.setSubIdDscr(StringUtils.trimToEmpty(rs
							.getString("SW_SBSCRPTN_ID_DSCR")));
					// get all price , then in action will get real price per
					// lob.
					cp.setSvpLevelD(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_A")));
					cp.setSvpLevelB(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_B")));
					cp.setSvpLevelA(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_D")));
					cp.setSvpLevelE(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_E")));
					cp.setSvpLevelF(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_F")));
					cp.setSvpLevelG(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_G")));
					cp.setSvpLevelH(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_H")));
					cp.setSvpLevelI(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_I")));
					cp.setSvpLevelJ(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_J")));
					cp.setSvpLevelED(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_ED")));
					cp.setSvpLevelGV(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_GV")));
					String ptqm = StringUtils.trimToEmpty(rs
							.getString("pricng_tier_qty_mesur"));
					if (StringUtils.isNotBlank(ptqm))
						cp.setTierQtyMeasre(StringUtils.isNumeric(ptqm) ? new Integer(
								ptqm) : null);
					else
						cp.setTierQtyMeasre(null);
					cp.setBillgAnlFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_ANL_FLAG")));
					cp.setBillgMthlyFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_MTHLY_FLAG")));
					cp.setBillgQtrlyFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_QTRLY_FLAG")));
					cp.setBillgUpfrntFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_UPFRNT_FLAG")));
					// kenexa
					cp.setBillgEvtFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_EVENT_FLAG")));
					cp.setSubsumedSubscrptn("1".equals(StringUtils
							.trimToEmpty(rs
									.getString("SUB_SUMED_SCRIPTION_FLAG"))));

					cp.setAvailableBillingOptions(BillingOptionHelper
							.singleton()
							.getAvaliableBillingFrequencyOptions(cp));
					cp.setPublshdPriceDurtnCode(StringUtils.trimToEmpty(rs
							.getString("PUBLSHD_PRICE_DURTN_CODE")));
					cp.setPublshdPriceDurtnCodeDscr(StringUtils.trimToEmpty(rs
							.getString("PUBLSHD_PRICE_DURTN_CODE_DSCR")));

					// 14.1 SPP
					cp.setDivestedPart(rs.getInt("DIVESTED_FLAG") == 1 ? true
							: false);
					cp.setActiveOnAgreement(false);
					cp.setChecked(false);
					
					// Set the restricted part flag
					int restrictedFlag = rs.getInt("PART_RSTRCT_FLAG");
					cp.setRestrictedPart(restrictedFlag == 0 ? false : true);
					// By default, if part is restricted, it will not show in configurator.
					// But this need show flag may override later, eg, the part's QTY is not blank, or it's subscription part's needShow flag is true.
					cp.setNeedShow(restrictedFlag == 0 ? true : false);
					
					// set the hybrid offering flag
					cp.setHybridOfferg((rs.getInt("ODS_HYBRID_OFFERG_FLAG") == 1 &&  rs.getInt("ODS_SAAS_FLAG") == 0) ? true : false );
					result.add(cp);
					
				}
				while (ps.getMoreResults()) {
					rsbo = ps.getResultSet();
					// we can't access result set again when all rows are
					// consumed
					if (rsbo.next()) {
						String cursorName = rsbo.getString(1);
						if ("QLI_BLG_OPTION".equalsIgnoreCase(cursorName)) {
							// processCfgrtrBillingOptionsBillingOption(rsbo,
							// result, billingOptionMap);
						} else if ("QLI_TIERD_SCALE_QTY"
								.equalsIgnoreCase(cursorName)) {
							processQliTierdScaleQty(rsbo, result); // directly move
						}
					}
				}
			}
			TransactionContextManager.singleton().commit();
		} catch (SQLException sqle) {
			logContext.error(
					"Failed to get the configurator parts from the database!",
					sqle);
			TransactionContextManager.singleton().rollback();
			throw new TopazException(sqle);
		} finally {
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rsbo && !rsbo.isClosed()) {
					rsbo.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
			tracer.dump();
		}

		return result;
	}

	public List findPartsByWebQuoteNumPID4Scw(String webQuoteNum, String pid)
			throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		List<ConfiguratorPart> result = new ArrayList();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", webQuoteNum);
		params.put("piPID", pid);
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		ResultSet rs = null;
		ResultSet rsbo = null;
		try {
			TransactionContextManager.singleton().begin();
			BillingOptionFactory.singleton().getBillingOptionMap();
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					CommonDBConstants.DB2_S_QT_PART_BY_PID_SCW, null);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			context.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_PART_BY_PID_SCW, params);

			tracer.stmtTraceStart("call S_QT_PART_BY_PID_SCW");
			boolean psResult = ps.execute();
			tracer.stmtTraceEnd("call S_QT_PART_BY_PID_SCW");
			ConfiguratorPart cp = null;
			if (psResult) {
				rs = ps.getResultSet();
				while (rs.next()) {
					cp = new ConfiguratorPart();
					cp.setPartNum(StringUtils.trimToEmpty(rs
							.getString("PART_NUM")));
					cp.setSwProdBrandCode(StringUtils.trimToEmpty(rs
							.getString("sw_prod_brand_code")));
					cp.setPartDscr(rs.getString(StringUtils
							.trimToEmpty("PART_DSCR_LONG")));
					cp.setSapMatlTypeCode(StringUtils.trimToEmpty(rs
							.getString("SAP_MATL_TYPE_CODE")));
					cp.setSapMatlTypeCodeGroupCode(StringUtils.trimToEmpty(rs
							.getString("SAP_MATL_TYPE_CODE_GROUP_CODE")));
					cp.setSapMatlTypeCodeGroupDscr(StringUtils.trimToEmpty(rs
							.getString("SAP_MATL_TYPE_CODE_GROUP_DSCR")));
					cp.setPricingTierModel(StringUtils.trimToEmpty(rs
							.getString("PRICNG_TIER_MDL")));
					cp.setWwideProdCode(StringUtils.trimToEmpty(rs
							.getString("WWIDE_PROD_CODE")));
					cp.setWwideProdCodeDscr(StringUtils.trimToEmpty(rs
							.getString("WWIDE_PROD_CODE_DSCR")));
					cp.setPidDscr(StringUtils.trimToEmpty(rs
							.getString("IBM_PROD_ID_DSCR")));
					cp.setSubId(StringUtils.trimToEmpty(rs
							.getString("sw_sbscrptn_id")));
					cp.setSubIdDscr(StringUtils.trimToEmpty(rs
							.getString("SW_SBSCRPTN_ID_DSCR")));
					// get all price , then in action will get real price per
					// lob.
					cp.setSvpLevelD(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_A")));
					cp.setSvpLevelB(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_B")));
					cp.setSvpLevelA(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_D")));
					cp.setSvpLevelE(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_E")));
					cp.setSvpLevelF(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_F")));
					cp.setSvpLevelG(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_G")));
					cp.setSvpLevelH(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_H")));
					cp.setSvpLevelI(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_I")));
					cp.setSvpLevelJ(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_J")));
					cp.setSvpLevelED(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_ED")));
					cp.setSvpLevelGV(StringUtils.trimToEmpty(rs
							.getString("SVP_LEVEL_GV")));
					String ptqm = StringUtils.trimToEmpty(rs
							.getString("pricng_tier_qty_mesur"));
					if (StringUtils.isNotBlank(ptqm))
						cp.setTierQtyMeasre(StringUtils.isNumeric(ptqm) ? new Integer(
								ptqm) : null);
					else
						cp.setTierQtyMeasre(null);
					cp.setBillgAnlFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_ANL_FLAG")));
					cp.setBillgMthlyFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_MTHLY_FLAG")));
					cp.setBillgQtrlyFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_QTRLY_FLAG")));
					cp.setBillgUpfrntFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_UPFRNT_FLAG")));
					// kenexa
					cp.setBillgEvtFlag(StringUtils.trimToEmpty(rs
							.getString("BILLG_EVENT_FLAG")));
					cp.setSubsumedSubscrptn("1".equals(StringUtils
							.trimToEmpty(rs
									.getString("SUB_SUMED_SCRIPTION_FLAG"))));

					cp.setAvailableBillingOptions(BillingOptionHelper
							.singleton()
							.getAvaliableBillingFrequencyOptions(cp));
					cp.setPublshdPriceDurtnCode(StringUtils.trimToEmpty(rs
							.getString("PUBLSHD_PRICE_DURTN_CODE")));
					cp.setPublshdPriceDurtnCodeDscr(StringUtils.trimToEmpty(rs
							.getString("PUBLSHD_PRICE_DURTN_CODE_DSCR")));

					// 14.1 SPP
					cp.setDivestedPart(rs.getInt("DIVESTED_FLAG") == 1 ? true
							: false);
					cp.setActiveOnAgreement(false);
					cp.setChecked(false);
					result.add(cp);
				}
				while (ps.getMoreResults()) {
					rsbo = ps.getResultSet();
					// we can't access result set again when all rows are
					// consumed
					if (rsbo.next()) {
						String cursorName = rsbo.getString(1);
						if ("QLI_BLG_OPTION".equalsIgnoreCase(cursorName)) {
							// processCfgrtrBillingOptionsBillingOption(rsbo,
							// result, billingOptionMap);
						} else if ("QLI_TIERD_SCALE_QTY"
								.equalsIgnoreCase(cursorName)) {
							processQliTierdScaleQty(rsbo, result); // ֱ��move
						}
					}
				}
			}
			TransactionContextManager.singleton().commit();
		} catch (SQLException sqle) {
			logContext.error(
					"Failed to get the configurator parts from the database!",
					sqle);
			TransactionContextManager.singleton().rollback();
			throw new TopazException(sqle);
		} finally {
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rsbo && !rsbo.isClosed()) {
					rsbo.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
			tracer.dump();
		}

		return result;
	}

	// process QLI_TIERD_SCALE_QTY
	private void processQliTierdScaleQty(ResultSet rs,
			List<ConfiguratorPart> result) throws TopazException {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		try {

			Map<String, HashSet> tierdMap = new HashMap<String, HashSet>();
			int count = 1;
			do {
				String partNum = rs.getString("PART_NUM").trim();
				int tierdScaleQty = rs.getInt("TIERD_SCALE_QTY");
				if (tierdScaleQty > 0) {
					// if ResultSet.size() == 1, add the part_num and qtySet to
					// the map
					if (count == 1) {
						HashSet firstSet = new HashSet();
						firstSet.add(tierdScaleQty);
						tierdMap.put(partNum, firstSet);
					}
					HashSet tempQtySet = (HashSet) tierdMap.get(partNum);
					// if exist this partnum's data, remove the old set then add
					// the new set
					if (tempQtySet != null) {
						tierdMap.remove(partNum);
						tempQtySet.add(tierdScaleQty);
						tierdMap.put(partNum, tempQtySet);
						// else add the new set directly
					} else {
						HashSet newSet = new HashSet();
						newSet.add(tierdScaleQty);
						tierdMap.put(partNum, newSet);
					}
					count++;
				}
			} while (rs.next());

			for (ConfiguratorPart cp : result) {
				HashSet itemTierdScaleQtySet = (HashSet) tierdMap.get(cp
						.getPartNum());
				if (itemTierdScaleQtySet != null) {
					List itemTierdScaleQtyList = new ArrayList();
					itemTierdScaleQtyList.addAll(itemTierdScaleQtySet);
					cp.setTierdScalQtyList(itemTierdScaleQtyList);
				}
			}

		} catch (Exception e) {
			logContext.error("Failed to get QliTierdScaleQty infomation", e);
			throw new TopazException(e);
		}
	}
	public int addOrUpdateConfigrtn(SaasConfiguration configrtn)
			throws TopazException {
		// TODO need move ConfiguratorPartProcess_jdbc.addOrUpdateConfigrtn()
		// method to here
		HashMap params = new HashMap();

		params.put("piWebQuoteNum", configrtn.getWebQuoteNum());
		params.put("piConfigrtnId", configrtn.getConfigrtnId());
		params.put("piConfigrtrConfigrtnId",
				configrtn.getConfigrtrConfigrtnId());
		params.put("piUserID", configrtn.getUserId());
		String refDocNum = configrtn.getRefDocNum();
		if (StringUtils.isNotBlank(refDocNum)) {
			params.put("piRefDocNum", refDocNum);
		}
		params.put("piEstdNumProvisngDays", null);
		params.put("piUpdateProvisngDaysFlag", new Integer(0));
		params.put("piConfigrtnErrCode", configrtn.getConfigrtnErrCode());
		params.put("piConfigrtnActionCode", configrtn.getConfigrtnActionCode());
		params.put("piEndDate", configrtn.getEndDate());
		if (StringUtils.isNotBlank(configrtn.getCotermConfigrtnId())) {
			params.put("piCotermConfigrtnId", configrtn.getCotermConfigrtnId());
		}
		params.put("piConfigrtnOvrrdnFlag", configrtn.getConfigrtnOvrrdn());
		params.put("piImportFlag", configrtn.getImportFlag());
		params.put("piTermExtensionFlag", configrtn.getTermExtFlag());
		params.put("piServiceDate", configrtn.getServiceDate());
		params.put("piServiceDateModType", configrtn.getServiceDateModType());
		params.put("piProvisngId", configrtn.getProvisioningId());
		params.put("piExtEntireConfigFlag", configrtn.getConfigEntireExtended());

		int retCode = -1;
		int maxDestObjSeqNum = -1;

		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					CommonDBConstants.DB2_IU_QT_CONFIGRTN, null);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			context.completeStatement(ps,
					CommonDBConstants.DB2_IU_QT_CONFIGRTN, params);
			ps.execute();
			retCode = ps.getInt(1);
			if (retCode != 0) {
				throw new TopazException("SP call returns error code: "
						+ retCode);
			}
			maxDestObjSeqNum = ps.getInt(2);
		} catch (Exception e) {
			logContext.error("Failed to update or insert SaaS configuration!",
					e);
			throw new TopazException(e);
		}

		return maxDestObjSeqNum;
	}

	public List<ConfiguratorPart> getPartsFromChrgAgrm(AddOrUpdateConfigurationContract ct, Map<String, ConfiguratorPart> map)
			throws QuoteException {
		List<ConfiguratorPart> configrtrPartList = new ArrayList<ConfiguratorPart>();

		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", ct.getChrgAgrmtNum());
		if (PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ct.getConfigrtnActionCode())) {// FCT
																										// TO
																										// PA
																										// Finalization
			String configrtnId = ct.getConfigId();
			String orgConfigrtnId = ct.getOrgConfigId();
			// if original configrtnId is null or configrtnId is equal to
			// orgConfigrtnId, it means first time to Finalization.
			if (StringUtils.isBlank(orgConfigrtnId) || configrtnId.equalsIgnoreCase(orgConfigrtnId)) {
				orgConfigrtnId = configrtnId;
			}
			params.put("piConfigId", orgConfigrtnId);
		} else
			params.put("piConfigId", ct.getConfigId());
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG, null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG, params);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(4);

			logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			int lineItemCount = ps.getInt(3);
			if (lineItemCount > 0) {

				rs = ps.getResultSet();
				String qtyStr = null;
				while (rs.next()) {
					String activeFlag = StringUtils.trim(rs.getString("ACTIVE_FLAG"));
					if (activeFlag.equals("0")) {
						continue;
					}
					String rampUpFlag = StringUtils.trim(rs.getString("RAMP_UP_FLAG"));// rtc
																						// #224650
					if ("1".equals(rampUpFlag)) {
						continue;
					}
					ConfiguratorPart part = new ConfiguratorPart();
					configrtrPartList.add(part);
					part.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));

					qtyStr = rs.getString("PART_QTY");
					if (StringUtils.isNotBlank(qtyStr)) {
						// if qtyStr is '1.000', convert it to '1'
						qtyStr = qtyStr.substring(0, qtyStr.indexOf(".") < 0 ? qtyStr.length() : qtyStr.indexOf("."));
						part.setPartQty(Integer.parseInt(qtyStr));
					}

					part.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
					part.setTerm(rs.getObject("CVRAGE_TERM") == null ? null : rs.getInt("CVRAGE_TERM"));
					part.setBillingFrequencyCode(StringUtils.trim(rs.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
					part.setLocalSaasOvrageAmt(rs.getObject("LOCAL_SAAS_OVRAGE_AMT") == null ? null : rs
							.getDouble("LOCAL_SAAS_OVRAGE_AMT"));
					part.setSaasTotCmmtmtVal(rs.getObject("SAAS_TOT_CMMTMT_VAL") == null ? null : rs
							.getDouble("SAAS_TOT_CMMTMT_VAL"));
					part.setLocalExtndPrice(rs.getObject("LOCAL_EXTND_PRICE") == null ? null : rs.getDouble("LOCAL_EXTND_PRICE"));
					part.setCotermEndDate(rs.getObject("COTERM_END_DATE") == null ? null : rs.getDate("COTERM_END_DATE"));
					part.setNextRenwlDate(rs.getObject("NEXT_RENWL_DATE") == null ? null : rs.getDate("NEXT_RENWL_DATE"));
					part.setOrignlSalesOrdRefNum(StringUtils.trim(rs.getString("ORIGNL_SALES_ORD_REF_NUM")));
					part.setOrignlConfigrtnId(StringUtils.trim(rs.getString("ORIGNL_CONFIGRTN_ID")));
					part.setRenwlCounter(rs.getInt("RENWL_COUNTER"));
					part.setRenwlMdlCode(StringUtils.trim(rs.getString("RENWL_MDL_CODE")));
				}
			}

		} catch (SQLException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} finally {
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
		}

		AbstractSaasConfiguratorProcess.setSaaSPartAttribute(configrtrPartList,
				map, ct.getPid(), ct.getTerm(), true);

		return configrtrPartList;
	}

}
