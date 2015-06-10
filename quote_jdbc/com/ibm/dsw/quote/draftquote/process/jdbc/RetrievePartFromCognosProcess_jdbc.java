package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.draftquote.contract.RetrievePartFromCognosContract;
import com.ibm.dsw.quote.draftquote.process.RetrievePartFromCognosProcess_Impl;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

public class RetrievePartFromCognosProcess_jdbc extends RetrievePartFromCognosProcess_Impl{
	LogContext logger = LogContextFactory.singleton().getLogContext();
    public List<String> getValidPartsByPartSearch(String partNumList, RetrievePartFromCognosContract ct)
    throws QuoteException {
    	List<String> validPartsList = new ArrayList<String>();
		    String sqlQuery = null;
		    HashMap params = new HashMap();
		    QuoteHeader quoteHeader = ct.getQuoteHeader();
		    params.put("piCreatorId", StringUtils.EMPTY);
		    params.put("piLineOfBusCode", quoteHeader.getLob().getCode());
		    params.put("piAcqrtnCode", quoteHeader.getAcqrtnCode());
		    params.put("piCountryCode", quoteHeader.getCountry().getCode3());
		    params.put("piAudience", quoteHeader.getAudCode());
		    params.put("piPartNumList", partNumList);
		    try {
		        logger.debug(this, "start to validate Cognos parts....");
		
		        QueryContext queryCtx = QueryContext.getInstance();
		        sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.S_QT_PART_BY_ID, null);
		        logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
		
		        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
		        queryCtx.completeStatement(ps, NewQuoteDBConstants.S_QT_PART_BY_ID, params);
		        ps.execute();
		        logger.debug(logger, "finish to validate Cognos parts");
		
		        ResultSet rs = ps.getResultSet();
		        if (rs != null) {
		            while (rs.next()) {
		                String partNum = StringUtils.trimToEmpty(rs.getString("PART_NUM").trim());
		                validPartsList.add(partNum);
		            }
		            rs.close();
		        }
		        return validPartsList;
		    } catch (Exception e) {
		        logger.error(this, LogHelper.logSPCall(sqlQuery, params));
		        throw new QuoteException(e);
		    } finally {
		        this.rollbackTransaction();
		    }
    }
}
