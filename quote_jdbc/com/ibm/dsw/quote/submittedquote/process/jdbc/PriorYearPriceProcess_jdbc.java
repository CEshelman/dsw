package com.ibm.dsw.quote.submittedquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.viewbean.MissingReasonType;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPriorSSPriceContract;
import com.ibm.dsw.quote.submittedquote.process.PriorYearPriceProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * 
 * @author J Zhang
 *
 */
public class PriorYearPriceProcess_jdbc extends PriorYearPriceProcess_Impl {

	@Override
	public SubmittedPriorSSPriceContract getPriorSSPrice(String quoteNum,
			String lineItemSeq) {
		this.beginTransaction();
		LogContext logger = LogContextFactory.singleton().getLogContext();
		SubmittedPriorSSPriceContract contract = new SubmittedPriorSSPriceContract();
		HashMap params = new HashMap();
        params.put("piWebQuoteNum", quoteNum);
        params.put("piQuoteLineItemSeqNum", lineItemSeq);
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_LINE_ITEM_YTY, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_LINE_ITEM_YTY, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new SPException(returnCode);
            }
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
            	contract.setQuoteNumber(rs.getString("WEB_QUOTE_NUM").trim());
            	contract.setLineSeqNum(String.valueOf(rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM")));
            	contract.setLocalUnitPriceLpp(String.valueOf(rs.getDouble("LOCAL_UNIT_PRICE_LPP")));
            	contract.setJustificationText(rs.getString("LPP_MISSG_REAS_DSCR").trim());
            	contract.setSapSalesOrdNum(rs.getString("SAP_SALES_ORD_NUM").trim());
            	
            	int partQty = rs.getInt("PART_QTY");
            	contract.setPartQuality(String.valueOf(partQty != 0 ? partQty : ""));
            	contract.setPriorQuoteNum(rs.getString("PRIOR_QUOTE_NUM").trim());
            	contract.setSoldToCustNum(rs.getString("SOLD_TO_CUST_NUM").trim());
            	contract.setPartNum(StringUtils.trimToEmpty(rs.getString("PART_NUM")));
            	
            	String missingReason = rs.getString("LPP_MISSG_REAS").trim();
            	if("10".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_SITE_MIGRATION_10;
            	} else if("20".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_COMPLEX_PART_EVOLUTION_20;
            	} else if("30".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_COMPRESSION_30;
            	} else if("40".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_ZERO_VALUE_ORDE_40;
            	} else if("50".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_MULTIPLE_CURRENCIES_50;
            	} else if("60".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_CURRENCY_CONVERSION_60;
            	} else if("70".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_INCORRECT_SYSTEM_PRICE_70;
            	} else if("100".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_OTHER_100;
            	} else {
            		missingReason = MissingReasonType.TYPE_OTHER_100;
            	}
            	
            	contract.setLppMissReason(missingReason);
            	contract.setType(rs.getString("YTY_SRC_CODE").trim());
            }
            this.commitTransaction();
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            System.err.println(e.getMessage());
        }finally {
			this.rollbackTransaction();
        }
        
    	return contract;
	}
	
	@Override
	public SubmittedPriorSSPriceContract getSubmittedOmittedPriorSSPrice(String quoteNum,
			String lineItemSeq,String renewalNum) {
		this.beginTransaction();
		LogContext logger = LogContextFactory.singleton().getLogContext();
		SubmittedPriorSSPriceContract contract = new SubmittedPriorSSPriceContract();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", quoteNum);
        params.put("piRenwlQuoteLineSeqNum", lineItemSeq);
        params.put("piRenewalNum", renewalNum);
        try {
        	QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.S_QT_OMITTED_LINES_BY_QT_NUM, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.S_QT_OMITTED_LINES_BY_QT_NUM, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            ps.execute();
            int returnCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new SPException(returnCode);
            }
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
            	contract.setQuoteNumber(rs.getString("WEB_QUOTE_NUM").trim());
            	contract.setLineSeqNum(String.valueOf(rs.getInt("RENWL_QUOTE_LINE_ITEM_SEQ_NUM")));
            	contract.setLocalUnitPriceLpp(String.valueOf(rs.getDouble("LOCAL_UNIT_PRICE_LPP")));
            	if(rs.getString("LPP_MISSG_REAS_DSCR") != null){
            		contract.setJustificationText(rs.getString("LPP_MISSG_REAS_DSCR").trim());
            	}
            	if(rs.getString("SAP_SALES_ORD_NUM") != null){
            		contract.setSapSalesOrdNum(rs.getString("SAP_SALES_ORD_NUM").trim());
            	}
            	
            	int partQty = rs.getInt("PART_QTY");
            	contract.setPartQuality(String.valueOf(partQty != 0 ? partQty : ""));
            	if(rs.getString("PRIOR_QUOTE_NUM") != null){
            		contract.setPriorQuoteNum(rs.getString("PRIOR_QUOTE_NUM").trim());
            	}
            	if(rs.getString("SOLD_TO_CUST_NUM") != null){
            		contract.setSoldToCustNum(rs.getString("SOLD_TO_CUST_NUM").trim());
            	}
            	Double systemLppPrice = null;
            	if(rs.getObject("prior_local_unit_price_12_mnth") != null){
            		systemLppPrice = rs.getDouble("prior_local_unit_price_12_mnth");
            	}
            	if(systemLppPrice != null && systemLppPrice > 0){
            		contract.setLpp(systemLppPrice+"");
            	}
            	contract.setPartNum(StringUtils.trimToEmpty(rs.getString("PART_NUM")));
            	String missingReason =  rs.getString("LPP_MISSG_REAS");
            	if(rs.getString("LPP_MISSG_REAS") != null){
            		missingReason = missingReason.trim();
            	}
            	if("10".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_SITE_MIGRATION_10;
            	} else if("20".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_COMPLEX_PART_EVOLUTION_20;
            	} else if("30".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_COMPRESSION_30;
            	} else if("40".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_ZERO_VALUE_ORDE_40;
            	} else if("50".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_MULTIPLE_CURRENCIES_50;
            	} else if("60".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_CURRENCY_CONVERSION_60;
            	} else if("70".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_INCORRECT_SYSTEM_PRICE_70;
            	} else if("100".equals(missingReason)) {
            		missingReason = MissingReasonType.TYPE_OTHER_100;
            	} else {
            		missingReason = MissingReasonType.TYPE_OTHER_100;
            	}
            	
            	contract.setLppMissReason(missingReason);
            	if(rs.getString("YTY_SRC_CODE") != null){
            		contract.setType(rs.getString("YTY_SRC_CODE").trim());
            	}
            	
            }
            this.commitTransaction();
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            System.err.println(e.getMessage());
        }finally {
			this.rollbackTransaction();
        }
        
    	return contract;
	}
}
