package com.ibm.dsw.quote.common.domain.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

public class YTYGrowthPersister extends Persister {
    private LogContext logger = LogContextFactory.singleton().getLogContext();

    private YTYGrowth_jdbc ytyGrowth;
    
	public YTYGrowthPersister(YTYGrowth_jdbc ytyGrowth) {
		super();
		this.ytyGrowth = ytyGrowth;
	}

	@Override
	public void update(Connection connection) throws TopazException {
		insertOrUpdate(connection);
	}

	@Override
	public void delete(Connection connection) throws TopazException {
		
		long start = System.currentTimeMillis();
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", ytyGrowth.quoteNum);
		params.put("piQuoteLineItemSeqNum",ytyGrowth.lineItemSeqNum);

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
			this.isDeleted(true);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.error("Failed to log the quote line item yty to the database!", e);
			throw new TopazException(e);
		}
		long end = System.currentTimeMillis();

		logger.debug(this, "delete single line item = " + (end - start));
	}

	@Override
	public void hydrate(Connection connection) throws TopazException {

	}

	@Override
	public void insert(Connection connection) throws TopazException {
		insertOrUpdate(connection);
	}
	
	private void insertOrUpdate(Connection connection) throws TopazException {
		long start = System.currentTimeMillis();
		
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", ytyGrowth.getQuoteNum());
		params.put("piQuoteLineItemSeqNum",ytyGrowth.getLineItemSeqNum());
		params.put("piYtySrcCode",ytyGrowth.getYtySourceCode());
		
		final Double  ytyGrowthVal = ytyGrowth.getYTYGrowthPct() != null ? new BigDecimal(ytyGrowth.getYTYGrowthPct()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() : 0.00;
		
		params.put("piYtyGrwthPct",ytyGrowthVal);
		params.put("piYtyInclddFlag",ytyGrowth.getIncludedInOverallYTYGrowthFlag());
		params.put("piImpldInclddFlag",ytyGrowth.getIncludedInImpliedYTYGrowthFlag());
		params.put("piRenwlQliQty",ytyGrowth.getRenwlQliQty());
		params.put("piLocalUnitPriceLpp",ytyGrowth.getManualLPP());
		params.put("piSoldToCustNum",ytyGrowth.getManualLPPCustNum());
		params.put("piSapSalesOrdNum",ytyGrowth.getSapSalesOrdNum());
		params.put("piPriorQuoteNum",ytyGrowth.getPriorQuoteNum());
		params.put("piPartNum",ytyGrowth.getManualLPPPartNum());
		params.put("piPartQty",ytyGrowth.getPartQty());
		params.put("piLppMissReas",ytyGrowth.getLppMissReas());
		params.put("piJustTxt",ytyGrowth.getJustTxt());
		params.put("piYtyGrowthRadio",ytyGrowth.getYtyGrwothRadio());
		params.put("piLocalUnitPrice",ytyGrowth.getSysComputedPriorPrice());
		
		int retCode = -1;
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_LINE_ITEM_YTY, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_LINE_ITEM_YTY,params);
			ps.execute();
			retCode = ps.getInt(1);
			if (retCode != 0) {
				throw new TopazException("SP call returns error code: " + retCode);
			}
			this.isNew(true);
			this.isDeleted(false);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.error("Failed to log the quote line item yty to the database!", e);
			throw new TopazException(e);
		}
		long end = System.currentTimeMillis();
		
		logger.debug(this, "Insert or update single line item ytygrowth= " + (end - start));
		
	}

}
