package com.ibm.dsw.quote.draftquote.process.jdbc;


import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;
import com.ibm.dsw.quote.draftquote.process.PriorYearPriceProcess_Impl;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * 
 * @author jason
 *
 */
public class PriorYearPriceProcess_jdbc extends PriorYearPriceProcess_Impl {

	@Override
	public void AddPriorSSPrice(PriorSSPriceContract contract) {
		this.beginTransaction();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", contract.getQuoteNumber());
		params.put("piQuoteLineItemSeqNum",contract.getLineSeqNum());
		params.put("piYtySrcCode",contract.getYtySrcCode());
		params.put("piRenwlQliQty",contract.getRenwlQliQty());
		params.put("piLocalUnitPriceLpp",contract.getLocalUnitPriceLpp());
		params.put("piSoldToCustNum",contract.getSoldToCustNum());
		params.put("piSapSalesOrdNum",contract.getSapSalesOrdNum());
		params.put("piPriorQuoteNum",contract.getPriorQuoteNum());
		params.put("piPartNum",contract.getPartNum());
		params.put("piPartQty",StringUtils.isBlank(contract.getPartQty())?null:contract.getPartQty());
		params.put("piLppMissReas",contract.getLppMissReas());
		params.put("piJustTxt",contract.getJustTxt());
		params.put("piYtyGrowthRadio",contract.getGrowthRadio());
		int retCode = -1;
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_PYP, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_PYP,params);
			ps.execute();
			retCode = ps.getInt(1);
			this.commitTransaction();
			if (retCode != 0) {
				throw new TopazException("SP call returns error code: " + retCode);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.error("Failed to log the  yty to the database!", e);
		}finally {
			this.rollbackTransaction();
        }
	}

	@Override
	public void updatePriorSSPrice(PriorSSPriceContract contract) {
		this.AddPriorSSPrice(contract);
	}

	@Override
	public PriorSSPriceContract getPriorSSPrice(String quoteNum,
			String lineItemSeq) {
		this.beginTransaction();
		LogContext logger = LogContextFactory.singleton().getLogContext();
		PriorSSPriceContract contract=new PriorSSPriceContract();
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
            if (rs.next())
            {
            	contract.setQuoteNumber(StringUtils.trim(rs.getString("WEB_QUOTE_NUM")));
            	contract.setLineSeqNum(String.valueOf(rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM")));
            	
            	contract.setLocalUnitPriceLpp(String.valueOf(rs.getDouble("LOCAL_UNIT_PRICE_LPP")));
            	
            	contract.setYtySrcCode(StringUtils.trim(rs.getString("YTY_SRC_CODE")));
            	contract.setRenwlQliQty(rs.getInt("RENWL_QUOTE_LINE_ITEM_QTY"));
            	contract.setLppMissReas(StringUtils.trim(rs.getString("LPP_MISSG_REAS")));
            	contract.setJustTxt(StringUtils.trim(rs.getString("LPP_MISSG_REAS_DSCR")));
            	contract.setSapSalesOrdNum(StringUtils.trim(rs.getString("SAP_SALES_ORD_NUM")));
            	contract.setPriorQuoteNum(StringUtils.trim(rs.getString("PRIOR_QUOTE_NUM")));
            	if(rs.getObject("PART_QTY")!=null){
            		//fix the bug when PART_QTY is null then rs.getInt("PART_QTY")= 0
            		contract.setPartQty(String.valueOf(rs.getInt("PART_QTY")));
            	}else{
            		contract.setPartQty("");
            	}
            	
            	contract.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));
            	contract.setSoldToCustNum(StringUtils.trim(rs.getString("SOLD_TO_CUST_NUM")));
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
	public void addOmittedPriorSSPrice(PriorSSPriceContract contract) {
		this.beginTransaction();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", contract.getQuoteNumber());
		params.put("piRenewanNum",contract.getRenewalNum());
		params.put("piQuoteLineItemSeqNum",contract.getLineSeqNum());
		params.put("piYtySrcCode",contract.getYtySrcCode());
		params.put("piRenwlQliQty",contract.getRenwlQliQty());
		params.put("piLocalUnitPriceLpp",contract.getLocalUnitPriceLpp());
		params.put("piSoldToCustNum",contract.getSoldToCustNum());
		params.put("piSapSalesOrdNum",contract.getSapSalesOrdNum());
		params.put("piPriorQuoteNum",contract.getPriorQuoteNum());
		params.put("piPartNum",contract.getPartNum());
		params.put("piPartQty",StringUtils.isBlank(contract.getPartQty())?null:contract.getPartQty());
		params.put("piLppMissReas",contract.getLppMissReas());
		params.put("piJustTxt",contract.getJustTxt());
		
		int retCode = -1;
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_OMITTED_PSSP, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_OMITTED_PSSP,params);
			ps.execute();
			retCode = ps.getInt(1);
			this.commitTransaction();
			if (retCode != 0) {
				throw new TopazException("SP call returns error code: " + retCode);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.error("Failed to log the  omitted to the database!", e);
		}finally {
			this.rollbackTransaction();
        }
	}
	
	@Override
	public PriorSSPriceContract getOmittedPriorSSPrice(String quoteNum,
			String rewlQuoteLineSeqNum,String renewalNum) {
		this.beginTransaction();
		LogContext logger = LogContextFactory.singleton().getLogContext();
		PriorSSPriceContract contract=new PriorSSPriceContract();
		HashMap params = new HashMap();
        params.put("piWebQuoteNum", quoteNum);
        params.put("piRenwlQuoteLineSeqNum", rewlQuoteLineSeqNum);
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
            if (rs.next())
            {
            	contract.setQuoteNumber(StringUtils.trim(rs.getString("WEB_QUOTE_NUM")));
            	contract.setLineSeqNum(String.valueOf(rs.getInt("RENWL_QUOTE_LINE_ITEM_SEQ_NUM")));
            	contract.setLocalUnitPriceLpp(rs.getString("LOCAL_UNIT_PRICE_LPP"));
            	contract.setYtySrcCode(StringUtils.trim(rs.getString("YTY_SRC_CODE")));
            	contract.setRenwlQliQty(rs.getInt("RENWL_QUOTE_LINE_ITEM_QTY"));
            	contract.setLppMissReas(StringUtils.trim(rs.getString("LPP_MISSG_REAS")));
            	contract.setJustTxt(StringUtils.trim(rs.getString("LPP_MISSG_REAS_DSCR")));
            	contract.setSapSalesOrdNum(StringUtils.trim(rs.getString("SAP_SALES_ORD_NUM")));
            	contract.setPriorQuoteNum(StringUtils.trim(rs.getString("PRIOR_QUOTE_NUM")));
            	if(rs.getObject("PART_QTY")!=null){
            		//fix the bug when PART_QTY is null then rs.getInt("PART_QTY")= 0
            		contract.setPartQty(String.valueOf(rs.getInt("PART_QTY")));
            	}else{
            		contract.setPartQty("");
            	}
            	
            	contract.setPartNum(StringUtils.trim(rs.getString("PART_NUM")));
            	contract.setSoldToCustNum(StringUtils.trim(rs.getString("SOLD_TO_CUST_NUM")));
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
	public void updateOmittedPriorSSPrice(PriorSSPriceContract  contract) {
		addOmittedPriorSSPrice(contract);
	}
	
	
	public String getNewOmittedYtYGrowth (String quoteNum,String renewalNum,String renwlQuoteLineSeqNum) throws QuoteException {
		double ytygrwoth = 0;
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", quoteNum);
		params.put("piRenewalNum", renewalNum);
		params.put("piRenwlQuoteLineSeqNum", renwlQuoteLineSeqNum);
		ResultSet rs = null;
		try {
		
		    this.beginTransaction();
		QueryContext queryCtx = QueryContext.getInstance();
		String sqlQuery = queryCtx.getCompletedQuery(
				CommonDBConstants.DB2_S_QT_GET_OMITTED_YTYGROWTH, null);
		CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
		queryCtx.completeStatement(callStmt,CommonDBConstants.DB2_S_QT_GET_OMITTED_YTYGROWTH, params);
		logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
		callStmt.execute();
		int poGenStatus = callStmt.getInt(1);
		if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
			throw new SPException(LogHelper.logSPCall(sqlQuery, params),
					poGenStatus);
		}
		rs = callStmt.getResultSet();
		while (rs != null && rs.next()) {
			 Double priorPrice = rs.getDouble("LOCAL_UNIT_PRICE_LPP");
			 if(priorPrice == null){
				  priorPrice = rs.getDouble("prior_local_unit_price_12_mnth");
			 }
			 ytygrwoth =   calculateYTYGrowth(rs,priorPrice);
			 
		}
		 
		} catch (SQLException e) {
		    logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		    throw new QuoteException(e);
		} catch (TopazException te) {
		    logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
		    throw new QuoteException(te);
		}finally{
			if(rs!=null){
		    	try {
		    		rs.close();
				} catch (Exception e) {
					logContext.error(this, e);
				}
			}
		    rollbackTransaction();
		}
		return ytygrwoth+"";
		}
		/**
		* Calculate the YTY growth %
		* @return double result
		* @throws SQLException
		*/
		private double calculateYTYGrowth(ResultSet dbRslt,Double  priorSSPrice) throws SQLException {
		// Calculate the YTY growth %
		double quotedTotPrc = dbRslt.getBigDecimal("quoted_tot_prc").doubleValue();
		double YTYGrowth = 0;
		double priorPrice = calculatePriorPrice(dbRslt,priorSSPrice);
		if (priorPrice != 0) {
			YTYGrowth = (quotedTotPrc / priorPrice - 1) * 100;
		}
		
		return YTYGrowth;
		}
		
		/**
		* Calculate the prior price
		* @return double result
		* @throws SQLException
		*/
		private double calculatePriorPrice(ResultSet dbRslt,Double priorSSPrice) throws SQLException {
		// Calculate the prior price

		int quotedQty = dbRslt.getInt("quoted_qty");
		double month = DateUtil.calculateFullCalendarMonths(dbRslt.getDate("start_date"), dbRslt.getDate("end_date"));
		double priorPrice = priorSSPrice * quotedQty * (month / 12);
		return priorPrice;
		}
	}
