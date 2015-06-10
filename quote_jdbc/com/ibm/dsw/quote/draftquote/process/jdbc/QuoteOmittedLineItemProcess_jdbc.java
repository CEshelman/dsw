package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteOmittedLineItemVO;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.process.QuoteOmittedLineItemProcess_Impl;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class QuoteOmittedLineItemProcess_jdbc extends
		QuoteOmittedLineItemProcess_Impl {

	private LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	/**
	 * Get getOmittedLineItemList
	 */
	@Override
	public void getOmittedLineItemList(List quoteList, List renewalNumList, String webQuoteNum)
			throws QuoteException {
		// call sp
		HashMap params = new HashMap();
				params.put("piWebQuoteNum", webQuoteNum);
		ResultSet rs = null;
		try {

            this.beginTransaction();
		QueryContext queryCtx = QueryContext.getInstance();
		String sqlQuery = queryCtx.getCompletedQuery(
				CommonDBConstants.DB2_S_QT_GET_OMITTED_LINES, null);
		CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
		queryCtx.completeStatement(callStmt,CommonDBConstants.DB2_S_QT_GET_OMITTED_LINES, params);
		logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

		callStmt.execute();
		
		int poGenStatus = callStmt.getInt(1);

		if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
			throw new SPException(LogHelper.logSPCall(sqlQuery, params),
					poGenStatus);
		}

		rs = callStmt.getResultSet();
		
		QuoteOmittedLineItemVO quoteOmittedLineItemVO = null;
		BigDecimal lineQuotedPoints=null;
		BigDecimal lineQuotedPrice=null;
		
		
		while (rs != null && rs.next()) {
			 BigDecimal itemPoints= rs.getBigDecimal("cntribtn_unit_pts");
			 BigDecimal itemPrice=rs.getBigDecimal("local_unit_price");
			 quoteOmittedLineItemVO = new QuoteOmittedLineItemVO();
			 quoteOmittedLineItemVO.setQuoteNum(rs.getString("WEB_QUOTE_NUM"));
			 quoteOmittedLineItemVO.setRenewalNum(rs.getString("RENWL_QUOTE_NUM"));
			 quoteOmittedLineItemVO.setPartNum(rs.getString("PART_NUM"));
			 quoteOmittedLineItemVO.setItemPoints((itemPoints!=null)?itemPoints.setScale(2,BigDecimal.ROUND_HALF_UP).toString():null);
			 quoteOmittedLineItemVO.setItemPrice((itemPrice!=null)?itemPrice.setScale(2,BigDecimal.ROUND_HALF_UP).toString():null);
			 quoteOmittedLineItemVO.setLineItem(rs.getString("RENWL_QUOTE_LINE_ITEM_SEQ_NUM"));
			 quoteOmittedLineItemVO.setStartDate(rs.getDate("START_DATE"));
			 quoteOmittedLineItemVO.setEndDate(rs.getDate("END_DATE"));
			 quoteOmittedLineItemVO.setYtySrcCode(rs.getString("YTY_SRC_CODE"));
			 quoteOmittedLineItemVO.setPricingMethod(rs.getString("PRICE_METHOD"));
			 quoteOmittedLineItemVO.setRenwlQuoteLineItemQty(rs.getInt("RENWL_QUOTE_LINE_ITEM_QTY"));
			
			 if(rs.getObject("LOCAL_UNIT_PRICE_LPP") != null){
				 quoteOmittedLineItemVO.setLocalPriorPrice(rs.getDouble("LOCAL_UNIT_PRICE_LPP"));
			 }else{
				 quoteOmittedLineItemVO.setLocalPriorPrice(0);
			 }
			
			 Double priorPrice = null;
     	     if(rs.getObject("LOCAL_UNIT_PRICE_LPP") != null){
     			priorPrice = rs.getDouble("LOCAL_UNIT_PRICE_LPP");
     		 }
			
			 if(priorPrice == null){
				  priorPrice = rs.getDouble("prior_local_unit_price_12_mnth");
			 }
			 if(priorPrice == null || priorPrice == 0){
				 quoteOmittedLineItemVO.setPriorPrice("");
			 }else{
				 quoteOmittedLineItemVO.setPriorPrice(priorPrice.toString()); 
			 }
			 quoteOmittedLineItemVO.setSystemPriorPrice(rs.getDouble("prior_local_unit_price_12_mnth"));
			
			 int lineQuotedQty=0;
			 int lineOrderedQty=0;
			 int lineOpenQty=0; 
			 
			 lineQuotedQty = rs.getInt("quoted_qty");
			 quoteOmittedLineItemVO.setPartQty(lineQuotedQty);
			 lineOrderedQty = lineOrderedQty + rs.getInt("ordered_qty");
			 lineOpenQty = lineQuotedQty - lineOrderedQty;
			 if (lineOpenQty<0){
			     lineOpenQty = 0;
			 }
			 if(lineOrderedQty > 0 ){
				 quoteOmittedLineItemVO.setLineOpenQty(lineOpenQty);
			 }
			 
			  if(priorPrice != null && priorPrice != 0 ) {
				  // Calculate the YTY growth % and put the result to QuoteItemsLineItemValueObject
				  quoteOmittedLineItemVO.setYTYGrowth(calculateYTYGrowth(rs,priorPrice));
			  }
			 
			 
			 quoteOmittedLineItemVO.setRsvpPrice(rs.getString("RSVP_SRP_PRICE"));
			 quoteOmittedLineItemVO.setPartDscr(rs.getString("part_dscr_full"));
			 quoteOmittedLineItemVO.setRenewalSecNum(rs.getInt("RENWL_QUOTE_LINE_ITEM_SEQ_NUM"));
			 quoteOmittedLineItemVO.setCurrencyCodeParam(rs.getString("prior_yr_currncy_code"));
			 quoteOmittedLineItemVO.setCurrency(rs.getString("currncy_code"));
			 lineQuotedPoints =rs.getBigDecimal("quoted_tot_pts");
			 lineQuotedPrice =  rs.getBigDecimal("quoted_tot_prc");
			 quoteOmittedLineItemVO.setTotalPoints((lineQuotedPoints!=null)?lineQuotedPoints.setScale(2,BigDecimal.ROUND_HALF_UP).toString():null);
			 quoteOmittedLineItemVO.setTotalPrice((lineQuotedPrice!=null)?lineQuotedPrice.setScale(2,BigDecimal.ROUND_HALF_UP).toString():null);
			 quoteList.add(quoteOmittedLineItemVO);
			
	}
		 if (callStmt.getMoreResults()) {
			 rs = callStmt.getResultSet();
			 while (rs.next()) {
				 renewalNumList.add(rs.getString(1));
			 }
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
