package com.ibm.dsw.quote.base.util;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PeakHoursUtil<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2009-10-21
 */
public class PeakHoursUtil extends TopazTransactionalProcess {
    Map map = new HashMap();
    public static PeakHoursUtil getInstance()
    {
        return new PeakHoursUtil();
    }
    
    private PeakHoursUtil()
    {
        init();
    }
    
    protected void init()
    {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try
        {
            String peakHoursCode = "SQO_CACHE";
	        String peakHoursConfig = this.getConfigString(peakHoursCode);
	        logger.info(this, "peak hours config string from db: " + peakHoursConfig);
//            String peakHoursConfig = "1-12:1-31:9-13";
//            String peakHoursConfig = "   ";
	        if ( StringUtils.isBlank(peakHoursConfig) )
	        {
	            return;
	        }
	        String[] arr = StringUtils.split(peakHoursConfig.trim(), '|');
	        for ( int i = 0; i < arr.length; i++ )
	        {
	            if ( StringUtils.isEmpty(arr[i]) )
	            {
	                continue;
	            }
	            String[] arr2 = StringUtils.split(arr[i], ':');
	            //parse month
	            String[] month = parse(arr2[0]);
	            String[] day = parse(arr2[1]);
	            List hours = parse2(arr2[2]);
	            StringBuffer buff = new StringBuffer();
	            for ( int j = 0; j < hours.size(); j++ )
	            {
	                buff.append(",").append((String)hours.get(j));
	            }
	            buff.append(",");
	            for ( int m = 0; m < month.length; m++ )
	            {
	                for ( int d = 0; d < day.length; d++ )
	                {
	                    String key = month[m] + "-" + day[d];
	                    String temp = (String)map.get(key);
	                    if ( temp != null )
	                    {
	                        map.put(key, temp + buff.toString());
	                    }
	                    else
	                    {
	                        map.put(key, buff.toString());
	                    }
	                }
	            }
	        }
        }
        catch ( Throwable t )
        {
            
            logger.error(this, t);
        }
    }
    
    private String[] parse(String str)
    {
        String[] ret = null;
        if ( str.indexOf('-') == -1 )
        {
            ret = StringUtils.split(str, ',');
        }
        else
        {
            int begin = Integer.parseInt(str.substring(0, str.indexOf('-')));
            int end = Integer.parseInt(str.substring(str.indexOf('-') + 1));
            ret = new String[end - begin + 1];
            for ( int i = begin; i <= end; i++ )
            {
                ret[i - begin] = Integer.toString(i);
            }
        }
        return ret;
    }
    
    private List parse2(String str)
    {
        List list = new ArrayList();
        String[] temp = StringUtils.split(str, ',');
        int index = 0;
        for ( int i = 0; i < temp.length; i++ )
        {
            int begin = Integer.parseInt(temp[i].substring(0, temp[i].indexOf('-')));
            int end = Integer.parseInt(temp[i].substring(temp[i].indexOf('-') + 1));
            for ( int j = begin; j <= end; j++ )
            {
                list.add(Integer.toString(j));
            }
        }
        return list;
    }
    
    /**
     * check whether current time is peak hours
     * @return true skip
     */
    public boolean skipCheck()
    {
        Calendar cald = Calendar.getInstance();
        int month = cald.get(Calendar.MONTH) + 1;
        int day = cald.get(Calendar.DAY_OF_MONTH);
        int hour = cald.get(Calendar.HOUR_OF_DAY);
        String hourStr = "," + Integer.toString(hour) + ",";
        String peakHoursStr = (String)map.get(Integer.toString(month) + "-" + Integer.toString(day));
        if ( peakHoursStr != null && peakHoursStr.indexOf(hourStr) != -1 )
        {
            return true;
        }
        return false;
    }
    
    public String getPeakHoursString()
    {
        StringBuffer buff = new StringBuffer();
        Iterator iter = map.entrySet().iterator();
        while ( iter.hasNext() )
        {
            Map.Entry entry = (Map.Entry)iter.next();
            buff.append((String)entry.getKey()).append('=').append(entry.getValue()).append("\n");
        }
        return buff.toString();
    }
    
    protected String getConfigString(String code) throws QuoteException
    {
        String spName = "S_WEB_APP_CNSTNT";
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        String ret = null;
        ResultSet rs = null;
        this.beginTransaction();
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String spSql = queryCtx.getCompletedQuery(spName, null);
            HashMap params = new HashMap();
            params.put("piCnstntName", "PEAK_HOURS_CONFIG");
            params.put("piCode", code);
            params.put("piColName", "");
            CallableStatement stmt = TopazUtil.getConnection().prepareCall(spSql);
            logContext.debug(this, LogHelper.logSPCall(spSql, params));

            queryCtx.completeStatement(stmt, spName, params);
            stmt.execute();

            int outCode = stmt.getInt(1);
            if ( outCode != 0 )
            {
                throw new QuoteException();
            }
            
            rs = stmt.getResultSet();
            if ( rs != null ) {
                while ( rs.next() )
                {
                    ret = rs.getString("col_name");
                    break;
                }
            }

            commitTransaction();
        }catch (Exception e) {
            logContext.error(this, e);
            throw new QuoteException(e);
        } finally {
        	try {
				if (null != rs)
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
            rollbackTransaction();
        }
        return ret;
    }
}
