package com.ibm.dsw.quote.configurator.process.jdbc;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PartPriceProcess_jdbc</code> class is jdbc implementation of
 * PartPriceProcess.
 *
 *
 * @author <a href="jhma@cn.ibm.com">Jun Hui Ma </a> <br/>
 *
 *         Creation date: 2011-6-17
 */
public class ConfiguratorPartProcess_jdbc extends ConfiguratorPartProcess_Impl {
	private static LogContext logger = LogContextFactory.singleton()
			.getLogContext();


	   //process QLI_TIERD_SCALE_QTY
	  private void processQliTierdScaleQty(ResultSet rs,List<ConfiguratorPart> result) throws TopazException{
	      LogContext logger = LogContextFactory.singleton().getLogContext();
	      try{

	      	Map<String, HashSet> tierdMap = new HashMap<String, HashSet>();
	      	int count = 1;
	      	do {
		            String partNum = rs.getString("PART_NUM").trim();
		            int tierdScaleQty = rs.getInt("TIERD_SCALE_QTY");
		            if(tierdScaleQty > 0){
			            //if ResultSet.size() == 1, add the part_num and qtySet to the map
			            if(count == 1){
			            	HashSet firstSet = new HashSet();
			            	firstSet.add(tierdScaleQty);
			            	tierdMap.put(partNum, firstSet);
			            }
			            HashSet tempQtySet = (HashSet)tierdMap.get(partNum);
			            //if exist this partnum's data, remove the old set then add the new set
			            if(tempQtySet != null){
			            	tierdMap.remove(partNum);
			            	tempQtySet.add(tierdScaleQty);
			            	tierdMap.put(partNum, tempQtySet);
			            //else add the new set directly
			            }else{
			            	HashSet newSet = new HashSet();
			            	newSet.add(tierdScaleQty);
			            	tierdMap.put(partNum, newSet);
			            }
			            count ++;
		            }
		        } while(rs.next());

	      	for(ConfiguratorPart cp:result){
	      		HashSet itemTierdScaleQtySet = (HashSet)tierdMap.get(cp.getPartNum());
	      		if(itemTierdScaleQtySet != null){
		        		List itemTierdScaleQtyList = new ArrayList();
		        		itemTierdScaleQtyList.addAll(itemTierdScaleQtySet);
		        		cp.setTierdScalQtyList(itemTierdScaleQtyList);
	      		}
	      	}

	      }catch(Exception e){
	          logger.error("Failed to get QliTierdScaleQty infomation",e);
	          throw new TopazException(e);
	      }
	  }


	protected List<ConfiguratorPart> getPartsFromChrgAgrm(
			AddOrUpdateConfigurationContract ct,
			Map<String, ConfiguratorPart> map) throws QuoteException {
		List<ConfiguratorPart> configrtrPartList = new ArrayList<ConfiguratorPart>();

		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", ct.getChrgAgrmtNum());
		if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ct.getConfigrtnActionCode())){//FCT TO PA Finalization
			String configrtnId = ct.getConfigId();
			String orgConfigrtnId = ct.getOrgConfigId();
			//if original configrtnId is null or configrtnId is equal to orgConfigrtnId, it means first time to Finalization.
			if(StringUtils.isBlank(orgConfigrtnId) || configrtnId.equalsIgnoreCase(orgConfigrtnId)){
				orgConfigrtnId = configrtnId;
			}
			params.put("piConfigId", orgConfigrtnId);
		}else
			params.put("piConfigId", ct.getConfigId());
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx
					.getCompletedQuery(
							CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG,
							null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG,
					params);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(4);

			logger.debug(this, "the return code of calling " + sqlQuery
					+ " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			int lineItemCount = ps.getInt(3);
			if (lineItemCount > 0) {

				rs = ps.getResultSet();
				String qtyStr = null;
				while (rs.next()) {
					String activeFlag = StringUtils.trim(rs.getString("ACTIVE_FLAG"));
					if(activeFlag.equals("0")){
						continue;
					}
					String rampUpFlag = StringUtils.trim(rs.getString("RAMP_UP_FLAG"));//rtc #224650
					if("1".equals(rampUpFlag)){
						continue;
					}
					ConfiguratorPart part = new ConfiguratorPart();
					configrtrPartList.add(part);
					part.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));

					qtyStr = rs.getString("PART_QTY");
					if (StringUtils.isNotBlank(qtyStr)) {
						// if qtyStr is '1.000', convert it to '1'
						qtyStr = qtyStr.substring(0,
								qtyStr.indexOf(".") < 0 ? qtyStr.length()
										: qtyStr.indexOf("."));
						part.setPartQty(Integer.parseInt(qtyStr));
					}

					part.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
					part.setTerm(rs.getObject("CVRAGE_TERM") == null ? null : rs.getInt("CVRAGE_TERM"));
					part.setBillingFrequencyCode(StringUtils.trim(rs.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
					part.setLocalSaasOvrageAmt(rs.getObject("LOCAL_SAAS_OVRAGE_AMT") == null ? null : rs.getDouble("LOCAL_SAAS_OVRAGE_AMT"));
					part.setSaasTotCmmtmtVal(rs.getObject("SAAS_TOT_CMMTMT_VAL") == null ? null : rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
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
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		} catch (TopazException e) {
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		}finally{
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
		}

		setSaaSPartAttribute(configrtrPartList, map, ct.getPid());

		return configrtrPartList;
	}

	public List<ConfiguratorPart> getSubPartsFromChrgAgrm(String chrgAgrmtNum, String configId)
                                                                 throws QuoteException{
		List<ConfiguratorPart> configrtrPartList = new ArrayList<ConfiguratorPart>();
		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", chrgAgrmtNum);

		if(StringUtils.isNotBlank(configId)){
			params.put("piConfigId", configId);
		}
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_CA_SUB_PARTS, null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_CA_SUB_PARTS, params);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(1);

			logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
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
				part.setSapMatlTypeCode(StringUtils.trim(rs.getString("SAP_MATL_TYPE_CODE")));
				//13.4kenexa
				part.setSubsumedSubscrptn("1".equals(StringUtils.trim(rs.getString("SUB_SUMED_SCRIPTION_FLAG"))));
				part.setConfigrtnId(StringUtils.trim(rs.getString("CONFIGRTN_ID")));
				part.setCotermEndDate(rs.getDate("COTERM_END_DATE"));
				part.setSapBillgFrqncyOptCode(StringUtils.trim(rs.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
				part.setNextRenwlDate(rs.getDate("NEXT_RENWL_DATE"));
				part.setRenwlTermMths(rs.getInt("RENWL_TERM_MTHS"));
				part.setRenwlMdlCode(StringUtils.trim(rs.getString("RENWL_MDL_CODE")));
			}
		} catch (SQLException e) {
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		} catch (TopazException e){
			logger.error(this, e.getMessage());
			throw new QuoteException(e);
		}finally{
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
		}

		return configrtrPartList;
	}

	public void setDefaultProvisngDays(String webQuoteNum, String configrtnId)
			throws TopazException {
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
			logContext.error("Failed to update or insert SaaS configuration!",
					e);
			throw new TopazException(e);
		}
	}

	public int addOrUpdateConfigrtn(String webQuoteNum, String configrtnId,
			String configrtrConfigrtnId, String userId, String refDocNum,
			String errorCode, String configrtnAction, Date endDate, String coTermToConfigrtnId, String overrideFlag,
			String importFlag,String termExtensionFlag ,Date serviceDate,String serviceDateModType,
			String provisioningId,int extEntireConfigFlag) throws TopazException {
		HashMap params = new HashMap();

		params.put("piWebQuoteNum", webQuoteNum);
		params.put("piConfigrtnId", configrtnId);
		params.put("piConfigrtrConfigrtnId", configrtrConfigrtnId);
		params.put("piUserID", userId);
		if (StringUtils.isNotBlank(refDocNum)) {
			params.put("piRefDocNum", refDocNum);
		}
		params.put("piEstdNumProvisngDays", null);
		params.put("piUpdateProvisngDaysFlag", new Integer(0));
		params.put("piConfigrtnErrCode", errorCode);
		params.put("piConfigrtnActionCode", configrtnAction);
		params.put("piEndDate", endDate);
		if(StringUtils.isNotBlank(coTermToConfigrtnId)){
			params.put("piCotermConfigrtnId", coTermToConfigrtnId);
		}
		params.put("piConfigrtnOvrrdnFlag", new Integer(overrideFlag));
		params.put("piImportFlag", new Integer(importFlag));

        params.put("piTermExtensionFlag", termExtensionFlag);
        params.put("piServiceDate", serviceDate);
        params.put("piServiceDateModType", serviceDateModType);
        params.put("piProvisngId", provisioningId);
        params.put("piExtEntireConfigFlag", extEntireConfigFlag);

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
	public Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm(String chrgAgrmtNum, String configId)  throws TopazException {
		Map<String, ConfiguratorPart> configrtrPartMap = new HashMap<String, ConfiguratorPart>();

		HashMap params = new HashMap();
		params.put("piChrgAgrmtNum", chrgAgrmtNum);
		params.put("piConfigId", configId);
		ResultSet rs = null;
		try {
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx
					.getCompletedQuery(
							CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG,
							null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					CommonDBConstants.DB2_S_QT_HTSRV_LINE_ITMES_BY_CONFIG,
					params);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

			ps.execute();
			int returnCode = ps.getInt(4);

			logger.debug(this, "the return code of calling " + sqlQuery
					+ " is: " + returnCode);
			if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				throw new SPException(returnCode);
			}

			int lineItemCount = ps.getInt(3);
			if (lineItemCount > 0) {

				rs = ps.getResultSet();
				String qtyStr = null;
				while (rs.next()) {
					String activeFlag = StringUtils.trim(rs.getString("ACTIVE_FLAG"));
					if(activeFlag.equals("0")){
						continue;
					}
					String rampUpFlag = StringUtils.trim(rs.getString("RAMP_UP_FLAG"));//rtc #224650
					if("1".equals(rampUpFlag)){
						continue;
					}
					ConfiguratorPart part = new ConfiguratorPart();

					part.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));

					qtyStr = rs.getString("PART_QTY");
					if (StringUtils.isNotBlank(qtyStr)) {
						// if qtyStr is '1.000', convert it to '1'
						qtyStr = qtyStr.substring(0,
								qtyStr.indexOf(".") < 0 ? qtyStr.length()
										: qtyStr.indexOf("."));
						part.setPartQty(Integer.parseInt(qtyStr));
					}

					part.setRefDocLineNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
					part.setTerm(rs.getObject("CVRAGE_TERM") == null ? null : rs.getInt("CVRAGE_TERM"));
					part.setBillingFrequencyCode(StringUtils.trim(rs.getString("SAP_BILLG_FRQNCY_OPT_CODE")));
					part.setLocalSaasOvrageAmt(rs.getObject("LOCAL_SAAS_OVRAGE_AMT") == null ? null : rs.getDouble("LOCAL_SAAS_OVRAGE_AMT"));
					part.setSaasTotCmmtmtVal(rs.getObject("SAAS_TOT_CMMTMT_VAL") == null ? null : rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
					part.setLocalExtndPrice(rs.getObject("LOCAL_EXTND_PRICE") == null ? null : rs.getDouble("LOCAL_EXTND_PRICE"));
					part.setCotermEndDate(rs.getObject("COTERM_END_DATE") == null ? null : rs.getDate("COTERM_END_DATE"));
					part.setNextRenwlDate(rs.getObject("NEXT_RENWL_DATE") == null ? null : rs.getDate("NEXT_RENWL_DATE"));
					part.setOrignlSalesOrdRefNum(StringUtils.trim(rs.getString("ORIGNL_SALES_ORD_REF_NUM")));
					part.setOrignlConfigrtnId(StringUtils.trim(rs.getString("ORIGNL_CONFIGRTN_ID")));
					part.setRenwlCounter(rs.getInt("RENWL_COUNTER"));
					part.setRenwlMdlCode(StringUtils.trim(rs.getString("RENWL_MDL_CODE")));
					part.setEndDate(rs.getDate("END_DATE"));
					part.setRenewalEndDate(rs.getDate("RENWL_END_DATE"));
					configrtrPartMap.put(part.getPartNum(), part);
				}
			}

		} catch (SQLException e) {
			logger.error(this, e.getMessage());
			throw new TopazException(e);
		} catch (TopazException e) {
			logger.error(this, e.getMessage());
			throw new TopazException(e);
		}finally{
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
		}

		return configrtrPartMap;
	}

}
