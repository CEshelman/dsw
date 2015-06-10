package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class MonthlySoftwarePersister extends QuoteLineItemPersister {
	
	
	public MonthlySoftwarePersister(MonthlySwLineItem_jdbc mothSwLineItem) {
		super(mothSwLineItem);
		this.monlySwItem = mothSwLineItem;
	}

	MonthlySwLineItem_jdbc monlySwItem = null;
	

	@Override
	public void update(Connection connection) throws TopazException {
		
		long start = System.currentTimeMillis();
		

        super.update(connection);
		
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", monlySwItem.getQuoteNum());
		params.put("piPartNum", monlySwItem.getPartNum());
		params.put("piQuoteLineItemSeqNum", monlySwItem.getSeqNum());
		params.put("piMonthlySWFlag", monlySwItem.isMonthlySwPart()? "1" : "0");
		params.put("piMonthlySWSubScrpFlag", monlySwItem.isMonthlySwSubscrptnPart()? "1" : "0");
		params.put("piMonthlySWSubScrpOVRGFlag", monlySwItem.isMonthlySwSubscrptnOvragePart()? "1" : "0");
		params.put("piMonthlySWDLYFlag", monlySwItem.isMonthlySwDailyPart()? "1" : "0");
		params.put("piMonthlySWOnDmndFlag", monlySwItem.isMonthlySwOnDemandPart()? "1" : "0");
		params.put("piHasRampUpFlag", monlySwItem.isHasRamupPart()? "1" : "0");
		params.put("piUpdateSectionFlag", monlySwItem.isUpdateSectionFlag()? "1" : "0");
		params.put("piAddtSectionFlag", monlySwItem.isAdditionSectionFalg()? "1" : "0");
		params.put("piMonthlySWTcvActFlag", monlySwItem.isSaasTcvAcv()? "1" : "0");

		int retCode = -1;
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	    try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MONTHLY_SW_LINE_ITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MONTHLY_SW_LINE_ITEM, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
        } catch (Exception e) {
            logger.error("Failed to log the monthly SW line item to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Update single monthly SW line item = "+ (end-start));
		
	}

	@Override
	public void delete(Connection connection) throws TopazException {
		long start = System.currentTimeMillis();
		
		super.delete(connection);
		
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", monlySwItem.getQuoteNum());
		params.put("piQuoteLineItemSeqNum", monlySwItem.getSeqNum());
		
		int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_MONTHLY_SW_LINE_ITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_D_QT_MONTHLY_SW_LINE_ITEM, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isDeleted(true);
        } catch (Exception e) {
            logger.error("Failed to delete the monthly SW line item from the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Delete Single monthly SW Line Item = "+(end-start));
	}

	@Override
	public void hydrate(Connection connection) throws TopazException {
		super.hydrate(connection);
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
	public void insert(Connection connection) throws TopazException {
		long start = System.currentTimeMillis();
		
        // need reload the line item to get iSeqNum.
		HashMap params = new HashMap();
		params.put("piWebQuoteNum", monlySwItem.getQuoteNum());
		params.put("piPartNum", monlySwItem.getPartNum());
		params.put("piQuoteLineItemSeqNum", monlySwItem.getSeqNum());
        params.put("piMonthlySWFlag", "1");
		params.put("piMonthlySWSubScrpFlag", monlySwItem.isMonthlySwSubscrptnPart()? "1" : "0");
		params.put("piMonthlySWSubScrpOVRGFlag", monlySwItem.isMonthlySwSubscrptnOvragePart()? "1" : "0");
		params.put("piMonthlySWDLYFlag", monlySwItem.isMonthlySwDailyPart()? "1" : "0");
		params.put("piMonthlySWOnDmndFlag", monlySwItem.isMonthlySwOnDemandPart()? "1" : "0");
		params.put("piHasRampUpFlag", monlySwItem.isHasRamupPart()? "1" : "0");
		params.put("piUpdateSectionFlag", monlySwItem.isUpdateSectionFlag()? "1" : "0");
		params.put("piAddtSectionFlag", monlySwItem.isAdditionSectionFalg()? "1" : "0");
		params.put("piMonthlySWTcvActFlag", monlySwItem.isSaasTcvAcv()? "1" : "0");

		int retCode = -1;
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	    try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MONTHLY_SW_LINE_ITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MONTHLY_SW_LINE_ITEM, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isNew(true);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to log the monthly SW line item to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Update single monthly SW line item = "+ (end-start));
	}


}
