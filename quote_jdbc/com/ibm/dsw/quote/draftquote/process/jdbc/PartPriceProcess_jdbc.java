package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.EquityCurveTotal;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess_Impl;
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
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: 2007-3-27
 */
public class PartPriceProcess_jdbc extends PartPriceProcess_Impl {
	
	@Override
	public void deleteYtyGrowth(String quoteNum,Integer seqNum) throws QuoteException {
		long start = System.currentTimeMillis();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", quoteNum);
		params.put("piQuoteLineItemSeqNum",seqNum);

		int retCode = -1;
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_LINE_ITEM_YTY, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			context.completeStatement(ps, CommonDBConstants.DB2_D_QT_LINE_ITEM_YTY,params);
			ps.execute();
			retCode = ps.getInt(1);
			if (retCode != 0) {
				throw new TopazException("SP call returns error code: " + retCode);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.error("Failed to log the quote line item yty to the database!", e);
			throw new QuoteException(e);
		}
		long end = System.currentTimeMillis();

		logger.debug(this, "delete single line item = " + (end - start));
	}

    public void updateQuoteHeader(QuoteHeader header, String userID) throws QuoteException {

        try {
            String spName = CommonDBConstants.DB2_U_QT_HDR_PP;
            HashMap parms = new HashMap();

            logContext.debug(this, "webquote number is " + header.getWebQuoteNum());
            parms.put("piWebQuoteNum", header.getWebQuoteNum());
            parms.put("piGsaPricingFlag", new Integer(header.getGsaPricngFlg()));
            parms.put("piRecalcPrcFlag", new Integer(header.getRecalcPrcFlag()));
            parms.put("piVolLevelDiscCode", header.getVolDiscLevelCode());
            parms.put("piTransPriceLevelCode", header.getTranPriceLevelCode());
            parms.put("piQuotePriceTot", new Double(header.getQuotePriceTot()));
            parms.put("piQuotePtsTot", new Double(header.getTotalPoints()));
            parms.put("piSysInitSpBidFlag", new Integer(header.getSpeclBidSystemInitFlg()));
            parms.put("piSpeclBidManualInitiatedFlag", new Integer(header.getSpeclBidManualInitFlg()));
            parms.put("piSapIdocNum", header.getSapIntrmdiatDocNum());
            parms.put("piOvrrdTranLevelCode", header.getOvrrdTranLevelCode());
            parms.put("piOfferPrice", header.getOfferPrice());
            parms.put("piDateOvrrdByAppvrFlg", header.getDateOvrrdByApproverFlag() ? new Integer(1) : new Integer(0));
            parms.put("piQuoteChnlPriceTot", null);
            parms.put("piApprvlRoutgFlg", new Integer(header.getApprovalRouteFlag()));
            int backDtgFlg = header.getBackDatingFlag() ? 1 : 0;
            parms.put("piBackDtgFlg", new Integer(backDtgFlg));
            parms.put("piPriceStartDate",  header.getPriceStartDate() == null?
                                           null: new Date(header.getPriceStartDate().getTime()));

            parms.put("piCmprssCvrageFlag", header.getCmprssCvrageFlag() ? new Integer(1) : new Integer(0));
            parms.put("piUserID", userID);
            parms.put("piLatamUpliftPct", new Double(header.getLatamUpliftPct()));
            parms.put("piSaasTotCmmtmtVal", header.getSaasTotCmmtmtVal());
            parms.put("piRefDocNum", header.getRefDocNum());
            parms.put("piChnlSAASTotCmmtmtVal", header.getSaasBpTCV());
            parms.put("piYtyGrwthPct", header.getYtyGrwthPct());
            parms.put("piImpldGrwthPct", header.getImpldGrwthPct());
            parms.put("piRecalcEcFlag", header.isECRecalculateFlag()? new Integer(1) : new Integer(0));
            EquityCurveTotal  equityCurveTotal = header.getEquityCurveTotal();
            if (null != equityCurveTotal){
            	parms.put("piEcWtdAvgMinPct", equityCurveTotal.getWeightAverageMin());
                parms.put("piEcWtdAvgMaxPct", equityCurveTotal.getWeightAverageMax());
            }else{
            	parms.put("piEcWtdAvgMinPct", null);
                parms.put("piEcWtdAvgMaxPct", null);
            }
            parms.put("piOlRecalcFlag", header.getOmittedLineRecalcFlag());
            if (header.isSaasRenewalFlag()==null){
            	parms.put("piSaasRenewalFlag", null);
            }else {
            	 parms.put("piSaasRenewalFlag", header.isSaasRenewalFlag()?"1":"0");
            }
            if (header.isSaasMigrationFlag()==null){
            	parms.put("piSaasMigrationFlag", null);
            }else {
            	parms.put("piSaasMigrationFlag", header.isSaasMigrationFlag()?"1":"0");
            }
            if (header.isMonthlyRenewalFlag()==null){
            	 parms.put("piMonthlyRenewalFlag", null);
            }else {
            	 parms.put("piMonthlyRenewalFlag", header.isMonthlyRenewalFlag()?"1":"0");
            }
            if (header.isMonthlyMigrationFlag()==null){
            	parms.put("piMonthlyMigrationFlag", null);
            }else {
            	parms.put("piMonthlyMigrationFlag", header.isMonthlyMigrationFlag()?"1":"0");
            }

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
        } catch (Exception e) {

            logContext.error(this, e.getMessage());
            throw new QuoteException(e);

        }

    }

    public void addDupLineItem(String partNum, String creatorId) throws QuoteException {
        try {
            this.beginTransaction();
            callAddLineItemSP(partNum, creatorId);
            this.commitTransaction();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    public int addRenewalPart(String rqNum, String partNum, String creatorId) throws QuoteException {
        int retCode = 0;
        try {
            this.beginTransaction();
            retCode = callAddRQPartSP(rqNum, partNum, creatorId);
            this.commitTransaction();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return retCode;
    }

	public void addRenwlModel(String userId, String renwlModelStr,String webQuoteNum , String configLevelRenwlModStr, String source)
			throws QuoteException {
		try {
			String spName = CommonDBConstants.DB2_IU_WEB_QUOTE_RENWL_MDL;
			HashMap parms = new HashMap();
			parms.put("piUserID", userId);
			parms.put("piRenewalModelConfigString", renwlModelStr);
			parms.put("piSource", source);
			parms.put("piWebQuoteNum", webQuoteNum);
			parms.put("piSapSalesOrderNum", "");
			parms.put("piConfigurationLevelRnewalModelString", configLevelRenwlModStr);
			parms.put("piSapIntrmdiatDocNum", "");

			QueryContext queryCtx = QueryContext.getInstance();

			String sqlQuery = queryCtx.getCompletedQuery(spName, null);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);

			queryCtx.completeStatement(ps, spName, parms);

			ps.execute();

			int retStatus = ps.getInt(1);

			if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
				throw new TopazException("exeute sp failed:" + sqlQuery);
			}
		} catch (Exception e) {

			logContext.error(this, e.getMessage());
			throw new QuoteException(e);

		}

	}

    /**
     * @param rqNum
     * @param partNum
     * @param creatorId
     * @throws TopazException
     * @throws SQLException
     */
private int callAddRQPartSP(String rqNum, String partNum, String creatorId) throws TopazException, SQLException {
        String spName = CommonDBConstants.DB2_I_QT_SESSN_RQ_PRTS;
        HashMap parms = new HashMap();

        parms.put("piCreatorId", creatorId);
        parms.put("piPartNum", partNum);
        parms.put("piRnwlQuoteNum", rqNum);
        parms.put("piPartLimit", new Integer(PartPriceConfigFactory.singleton().getElaLimits()));

        QueryContext queryCtx = QueryContext.getInstance();

        String sqlQuery = queryCtx.getCompletedQuery(spName, null);
        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

        queryCtx.completeStatement(ps, spName, parms);

        ps.execute();

        int retStatus = ps.getInt(1);
        if ((retStatus == DBConstants.DB2_SP_ALREADY_IS_MAX) || (retStatus == DBConstants.DB2_SP_EXCEED_MAX)){
            return retStatus;
        }else if (retStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
            throw new TopazException("Execute failed on " + sqlQuery + " with retCode " + retStatus);
        }

        return 0;


    }
    /**
     * @param partNum
     * @param creatorId
     * @param spName
     * @throws TopazException
     * @throws SQLException
     */
    private void callAddLineItemSP(String partNum, String creatorId) throws TopazException, SQLException {
        String spName = CommonDBConstants.DB2_I_QT_ADD_DUP_PRT;
        HashMap parms = new HashMap();

        parms.put("piCreatorId", creatorId);
        parms.put("piPartNum", partNum);

        QueryContext queryCtx = QueryContext.getInstance();

        String sqlQuery = queryCtx.getCompletedQuery(spName, null);
        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

        queryCtx.completeStatement(ps, spName, parms);

        ps.execute();

        int retStatus = ps.getInt(1);

        if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
        	if(CommonDBConstants.DB2_SP_RETURN_PART_INPUT_INVALID == retStatus){
        		logContext.info(this, "retStatus = " + retStatus+ ", Invalid part number:"+partNum);
        	}else{
        		throw new TopazException("exeute sp failed:" + sqlQuery);
        	}
        }
    }

	@Override
	public void updateEquityCurvePart(String webQuoteNum) throws QuoteException {
		String spName = CommonDBConstants.DB2_IU_QT_EQUITY_CURVE;
		try {
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", webQuoteNum);
            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
	}


	@Override
	public int addEntirParts(String webQuoteNum, String caNum, String configrtnNum, String isFromReporting)
			throws QuoteException {
		int retCode = 0;
        try {
            this.beginTransaction();
            retCode = callAddEntirPartSP(webQuoteNum, caNum, configrtnNum, isFromReporting);
            this.commitTransaction();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return retCode;
	}
	
	/**
     * @param rqNum
     * @param partNum
     * @param creatorId
     * @throws TopazException
     * @throws SQLException
     */
private int callAddEntirPartSP(String webQuoteNum, String caNum, String configtnNum, String isFromReporting) throws TopazException, SQLException {
        String spName = CommonDBConstants.DB2_I_QT_EXT_ENTIRE_CONFIGRTN;
        HashMap parms = new HashMap();

        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piCaNum", caNum);
        parms.put("piConfigrtnNum", configtnNum);
        parms.put("piIsFromReporting", isFromReporting);

        QueryContext queryCtx = QueryContext.getInstance();

        String sqlQuery = queryCtx.getCompletedQuery(spName, null);
        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

        queryCtx.completeStatement(ps, spName, parms);

        ps.execute();

        int retStatus = ps.getInt(1);
        if ((retStatus == DBConstants.DB2_SP_ALREADY_IS_MAX) || (retStatus == DBConstants.DB2_SP_EXCEED_MAX)){
            return retStatus;
        }else if (retStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
            throw new TopazException("Execute failed on " + sqlQuery + " with retCode " + retStatus);
        }

        return 0;


    }


	@Override
	public void reviewUpdateOmittedRenewalLine(String webQuoteNum) throws QuoteException {
		String spName = CommonDBConstants.DB2_IU_QT_OMIT_LINES;
		try {
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", webQuoteNum);
            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
		
	}
	
	@Override
	public Map<String, Double> getLicensePartSplit(String webQuoteNum) throws QuoteException{
		
		ResultSet rs = null;
		Map<String,Double> splitMap = new HashMap<String,Double>();
		try {
            String spName = CommonDBConstants.DB2_S_QT_GET_SPLIT_PCT;
            HashMap params = new HashMap();
            params.put("piWebQuoteNum",webQuoteNum);
            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            queryCtx.completeStatement(ps, spName, params);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                logContext.error(this, "retStatus = " + retStatus);
                throw new TopazException("exeute sp failed:" + sqlQuery);
            }
            rs =  ps.getResultSet();
            
            if( rs != null){
            	while (rs.next()) {
    				splitMap.put(rs.getString("PART_NUM").trim(), rs.getDouble("SPLIT_PCT") / 100);
    			}
    			
            }
            return splitMap;
            
            
	} catch (Exception e) {
        logContext.debug(this, e.getMessage());
        throw new QuoteException(e);

	}finally {
		try {
			if (null != rs && !rs.isClosed())
			{
				rs.close();
			}
			} catch (SQLException e) {
			logContext.error("Failed to close the resultset!", e);
		}
	}
		
}

    @Override
    public Map<String,String> validateOrCreateDeploymentId(String webQuoteNum, int quoteLineItemSeqNum,
            int deployModelOption, String deployModelId) throws QuoteException {
    	Map<String,String> validateMessageResult = new HashMap<String,String>();
        // if no quote num or deployment association is not in ('DEPLOYMENT_NOT_ON_QUOTE', 'DEPLOYMENT_NEW_ID')
        if(PartPriceConstants.DEPLOYMENT_NOT_ON_QUOTE != deployModelOption && PartPriceConstants.DEPLOYMENT_NEW_ID != deployModelOption ){
            return validateMessageResult;
        }
        // input parameter
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piQuoteLineItemSeqNum",quoteLineItemSeqNum);
        params.put("piDeployOption",deployModelOption);
        params.put("piDeployId",deployModelId);

        try {
        	this.beginTransaction();
            QueryContext context = QueryContext.getInstance();

            String sqlQuery = context.getCompletedQuery(CommonDBConstants.S_QT_GET_VALIDATE_DEPL_ID, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.S_QT_GET_VALIDATE_DEPL_ID,params);

            ps.execute();
            int retStatus = ps.getInt(1);
            deployModelId = StringUtils.trimToEmpty(ps.getString(5));
            String deployIdInvalidation = StringUtils.trimToEmpty(ps.getString(6));

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            this.commitTransaction();
            validateMessageResult.put("DEPLOYMT_ID", deployModelId);
            validateMessageResult.put("DEPLOYMT_ID_INVD", deployIdInvalidation);
        } catch (Exception e) {
            logContext.error("Failed to validate the deployment id to the database!", e);
            throw new QuoteException(e);
        }finally{
        	this.rollbackTransaction();
        }

        return validateMessageResult;
    }


	@Override
	public void addOrUpdateDeploymentId(QuoteLineItem item, String userId) throws QuoteException {
		 // input parameter
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("piWebQuoteNum", item.getQuoteNum());
        params.put("piQuoteLineItemSeqNum",item.getQuoteSectnSeqNum());
        params.put("piDeployOption",item.getDeployModel().getDeployModelOption());
        params.put("piDeployId",item.getDeployModel().getDeployModelId());
        params.put("piDeployIdInvalid",item.getDeployModel().isDeployModelInvalid() ? "1" : "0");
        params.put("piSerialNumWarningFlag",item.getDeployModel().getSerialNumWarningFlag());
        params.put("piModByUserID",userId);

        try {
            QueryContext context = QueryContext.getInstance();

            String sqlQuery = context.getCompletedQuery(CommonDBConstants.IU_QT_LINE_ITEM_APPLNC, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.IU_QT_LINE_ITEM_APPLNC,params);

            ps.execute();
            int retStatus = ps.getInt(1);
            
            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
        } catch (Exception e) {
            logContext.error("Failed to validate the deployment id to the database!", e);
            throw new QuoteException(e);
        }finally{
        	this.rollbackTransaction();
        }

		
	}

	@Override
	public String getApplianceDeploymentIdByMTM(String applncMachineType, String applncMachineModel, String applncSerialNumber)
			throws QuoteException {
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("piApplncMachineType", applncMachineType);
        params.put("piApplncMachineModel", applncMachineModel);
        params.put("piApplncSerialNumber", applncSerialNumber);        
        String result = "";
        try {
        	this.beginTransaction();
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_DEPLOY_ID_BY_MTM, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_DEPLOY_ID_BY_MTM, params);
            ps.execute();
            int retStatus = ps.getInt(1);
            result = StringUtils.trimToEmpty(ps.getString(5));
            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            this.commitTransaction();
        } catch (Exception e) {
            logContext.error("Failed to get appliance deployment id!", e);
            throw new QuoteException(e);
        }finally{
        	this.rollbackTransaction();
        }
        return result;
	}

	public void updateIncreaseBidTCV(String webQuoteNum, String configrtnIds, String increaseBidTCVs, String unUsedTCVs) throws QuoteException {
		 // input parameter
       HashMap<String,Object> params = new HashMap<String,Object>();
       params.put("piWebQuoteNum", webQuoteNum);
       params.put("piConfigrtnIds",configrtnIds);
       params.put("piIncreaseBidTCVs",increaseBidTCVs);
       params.put("piUnusedTCVs",unUsedTCVs);

       try {
           QueryContext context = QueryContext.getInstance();

           String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_U_QT_TCV_NET_INC, null);
           logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

           CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
           context.completeStatement(ps, CommonDBConstants.DB2_U_QT_TCV_NET_INC,params);

           ps.execute();
           int retStatus = ps.getInt(1);
           
           if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
               throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
           }
           
       } catch (Exception e) {
           logContext.error("Failed to store increaseBidTCV to the database!", e);
           throw new QuoteException(e);
       }
	
	}

    public void deleteRenwlModel(String webQuoteNum, String configrtnId, String chargeAgreementNum) throws QuoteException
    {
        try
        {
            String spName = CommonDBConstants.DB2_D_QT_RENWL_MDL;
            HashMap<String, String> parms = new HashMap<String, String>();
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piConfigrtnId", null == configrtnId ? "" : configrtnId);
            parms.put("piSapSalesOrderNum", null == chargeAgreementNum ? "" : chargeAgreementNum);
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            ps.execute();
            int retStatus = ps.getInt(1);
            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus)
            {
                throw new TopazException("exeute sp failed:" + sqlQuery);
            }
        }
        catch (Exception e)
        {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
}
