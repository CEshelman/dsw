package com.ibm.dsw.quote.export.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcess_Impl;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetPart;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadsheetPricing;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ExportQuoteProcess_jdbc</code> class is
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 11, 2007
 */
public class ExportQuoteProcess_jdbc extends ExportQuoteProcess_Impl {
	
	protected SpreadSheetQuote populateSupportingData(SpreadSheetQuote epQuote, String creatorId) throws ExportQuoteException
	{
		return populateSupportingData(epQuote, creatorId, null);
	}

	/**
	 * @param epQuote
	 * @param creatorId
	 * @throws QuoteException
	 */
	protected SpreadSheetQuote populateSupportingData(SpreadSheetQuote epQuote,
			String creatorId, String webQuoteNum) throws ExportQuoteException {
		LogContext logger = LogContextFactory.singleton().getLogContext();

		List pricingList = epQuote.getPricingList();

		List fixedPriceParts = new ArrayList();

		for (Iterator it = epQuote.getEqPartList().iterator(); it.hasNext();) {
			SpreadSheetPart ssp = (SpreadSheetPart) it.next();
			if (ssp.isNonContractPart()) {
				fixedPriceParts.add(ssp.getEpPartNumber());
			}
		}

		String partNums = epQuote.getPartNums();
		String sqlQuery = null;
		HashMap params = new HashMap();
		params.put("piCreatorId", StringUtils.trimToEmpty(creatorId));
		params.put("piLineOfBusCode", StringUtils.EMPTY);
		params.put("piAcqrtnCode", StringUtils.EMPTY);
		params.put("piCountryCode", epQuote.getCntryCode());
		params.put("piAudience", StringUtils.EMPTY);
		params.put("piPartNumList", StringUtils.trimToEmpty(partNums));
		params.put("piWebQuoteNum", StringUtils.trimToEmpty(webQuoteNum));
		try {
			this.beginTransaction();
			logger.debug(this, "retrieving part and price for : " + partNums);

			QueryContext queryCtx = QueryContext.getInstance();
			sqlQuery = queryCtx.getCompletedQuery(
					PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			queryCtx.completeStatement(ps,
					PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS, params);
			ps.executeQuery();

			ResultSet rs = ps.getResultSet();
			List partNumList = new ArrayList();
			while (rs.next()) {
				String partNum = StringUtils.trimToEmpty(rs
						.getString("PART_NUM"));

                if(partNumList.contains(partNum)){
                    continue;
                }
				
				String revenueStreamCode = StringUtils.trim(rs
						.getString("REV_STRM_OR_PROD_PACK"));
				String SVP_LEVEL_A = rs.getString("SVP_LEVEL_A");
				String SVP_LEVEL_B; 
				String SVP_LEVEL_D; 
				String SVP_LEVEL_E; 
				String SVP_LEVEL_F; 
				String SVP_LEVEL_G; 
				String SVP_LEVEL_H; 
				String SVP_LEVEL_I; 
				String SVP_LEVEL_J; 
				String SVP_LEVEL_ED; 
				String SVP_LEVEL_GV;

				logger.debug(this, "fixedPriceParts : " + fixedPriceParts);

				if (fixedPriceParts.contains(partNum)) {
					SVP_LEVEL_B = SVP_LEVEL_A;
					SVP_LEVEL_D = SVP_LEVEL_A;
					SVP_LEVEL_E = SVP_LEVEL_A;
					SVP_LEVEL_F = SVP_LEVEL_A;
					SVP_LEVEL_G = SVP_LEVEL_A;
					SVP_LEVEL_H = SVP_LEVEL_A;
					SVP_LEVEL_I = SVP_LEVEL_A;
					SVP_LEVEL_J = SVP_LEVEL_A;
					SVP_LEVEL_ED = SVP_LEVEL_A;
					SVP_LEVEL_GV = SVP_LEVEL_A;
				} else {
					SVP_LEVEL_B = rs.getString("SVP_LEVEL_B");
					SVP_LEVEL_D = rs.getString("SVP_LEVEL_D");
					SVP_LEVEL_E = rs.getString("SVP_LEVEL_E");
					SVP_LEVEL_F = rs.getString("SVP_LEVEL_F");
					SVP_LEVEL_G = rs.getString("SVP_LEVEL_G");
					SVP_LEVEL_H = rs.getString("SVP_LEVEL_H");
					SVP_LEVEL_I = rs.getString("SVP_LEVEL_I");
					SVP_LEVEL_J = rs.getString("SVP_LEVEL_J");
					SVP_LEVEL_ED = rs.getString("SVP_LEVEL_ED");
					SVP_LEVEL_GV = rs.getString("SVP_LEVEL_GV");
				}
				SpreadsheetPricing pricingData = new SpreadsheetPricing();
				pricingData.setEpPartNumber(partNum);
				pricingData.setRevenueStream(revenueStreamCode);
				pricingData
						.setSvpLevelB(SVP_LEVEL_B == null ? "" : SVP_LEVEL_B);
				pricingData
						.setSvpLevelA(SVP_LEVEL_A == null ? "" : SVP_LEVEL_A);
				pricingData
						.setSvpLevelD(SVP_LEVEL_D == null ? "" : SVP_LEVEL_D);
				pricingData
						.setSvpLevelE(SVP_LEVEL_E == null ? "" : SVP_LEVEL_E);
				pricingData
						.setSvpLevelF(SVP_LEVEL_F == null ? "" : SVP_LEVEL_F);
				pricingData
						.setSvpLevelG(SVP_LEVEL_G == null ? "" : SVP_LEVEL_G);
				pricingData
						.setSvpLevelH(SVP_LEVEL_H == null ? "" : SVP_LEVEL_H);
				pricingData
						.setSvpLevelI(SVP_LEVEL_I == null ? "" : SVP_LEVEL_I);
				pricingData
						.setSvpLevelJ(SVP_LEVEL_J == null ? "" : SVP_LEVEL_J);
				pricingData.setSvpLevelED(SVP_LEVEL_ED == null ? ""
						: SVP_LEVEL_ED);
				pricingData.setSvpLevelGV(SVP_LEVEL_GV == null ? ""
						: SVP_LEVEL_GV);
				pricingList.add(pricingData);
                partNumList.add(partNum);
			}
			this.commitTransaction();
			Collections.sort(pricingList);
		} catch (Exception e) {
			logger.error(this, LogHelper.logSPCall(sqlQuery, params));
			throw new ExportQuoteException(e);
		} finally {
			this.rollbackTransaction();
		}

		return epQuote;
	}
}
