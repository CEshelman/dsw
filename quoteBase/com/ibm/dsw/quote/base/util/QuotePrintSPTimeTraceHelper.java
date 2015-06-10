package com.ibm.dsw.quote.base.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.PrintSPTimeTraceFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuotePrintSPTimeTraceHelper<code> class is print specific procedure executeTime .
 *    
 * @author: <a href="jiewbj@cn.ibm.com">Crespo </a>
 * 
 * Creation date: October 1, 2013
 */
public class QuotePrintSPTimeTraceHelper {

	 private static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.printsptime.process.QuotePrintSPTimeTraceProcess#PrintSPTimeTrace(java.lang.String, java.lang.Long)
	 */
	public static void printSpExecuteTime(String sql, Long executiontime)throws QuoteException 
	{
		try 
		{
			 Map spMap=PrintSPTimeTraceFactory.singleton().findAllSPinCache();
			 String spName=getSpNameFromJdbcSql(sql);
			  if(StringUtils.isNotBlank(spName)&&null!=spMap&&null!=spMap.get(spName))
			  {
				  Long timethreshold=((Integer)spMap.get(spName)).longValue();
			      if(executiontime>timethreshold)
			        {
			    	  logSPCall(sql,executiontime,timethreshold);
			         }
			   }
		} 
		catch (TopazException e) 
		{
			//logContext.error(QuotePrintSPTimeTraceHelper.class, e.toString());
		}
	}
	
	/**
	 * Through standard jdbc sql parsing sp name
	 * eg:  change "CALL EBIZ1.S_QT_LOB(?)" to "EBIZ1.S_QT_LOB"
	 * @param sql
	 * @return String
	 */
	private static String getSpNameFromJdbcSql(String sql)
	{
		String spName=null;
		if(StringUtils.isNotBlank(sql))
		{
			if(sql.indexOf(ApplicationProperties.PRINT_SP_TIMETRACE_EBIZ1)!=-1&&sql.indexOf(ApplicationProperties.PRINT_SP_TIMETRACE_SIGN)!=-1)
			{
				spName=sql.substring(sql.indexOf(ApplicationProperties.PRINT_SP_TIMETRACE_EBIZ1), sql.indexOf(ApplicationProperties.PRINT_SP_TIMETRACE_SIGN));
				spName.trim();
			}
		}
		return spName;
	}
	
	/**
	 * Print sp log.  
	 * @param sqlQuery
	 * @param parms
	 */
    private static void logSPCall(String sqlQuery, Long executiontime,Long timeshreshold){
        StringBuffer sp = new StringBuffer();
        sp.append(sqlQuery).append("-->");
        sp.append("(").append(executiontime).append(",");
        sp.append(timeshreshold).append(")");
		logContext.error(QuotePrintSPTimeTraceHelper.class, sp.toString());
    }
}
