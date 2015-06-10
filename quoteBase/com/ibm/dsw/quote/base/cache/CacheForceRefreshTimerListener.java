/*
 * Created on 2007-2-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.quartz.common.CacheContext;
import com.ibm.ead4j.quartz.common.CacheContextFactory;
import com.ibm.ead4j.quartz.exception.CacheException;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;
import commonj.timers.Timer;
import commonj.timers.TimerListener;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CacheForceRefreshTimerListener implements TimerListener {
    static String refreshedTimeStamp = null;
    private static LogContext logger = LogContextFactory.singleton().getLogContext();
    
    public CacheForceRefreshTimerListener()
    {
    	this.initTimeStamp();
    }

    /* (non-Javadoc)
     * @see commonj.timers.TimerListener#timerExpired(commonj.timers.Timer)
     */
    public void timerExpired(Timer timer) {
        try
        {
	        if ( !isNeedToDoRefresh() )
	        {
	        	logger.debug(this, "Check force refresh, no force refresh need");
	        	return;
	        }
        }
        catch ( TopazException t )
        {
        	logger.error(this, "check force refresh timestamp: " + t.toString());
        	return;
        }
       
//        if ( )
        logger.info(this, "begin do force refresh");
        TopazCacheableFactory[] factories = QuoteCacheBootStrape.getInstance().getCachedKeyFactories(); 
        CacheContext cc = CacheContextFactory.singleton().getCacheContext();
        //CacheAccess ca;
        try {
            logger.debug(this,"Refreshing Cache Group: QUOTE_CACHE_GROUP");
            QuoteCache cache = (QuoteCache) cc.getCache();
            cache.loadGroup("QUOTE_CACHE_GROUP",factories);
            logger.debug(this,"Cache Group: QUOTE_CACHE_GROUP has been refreshed.");
        } catch (CacheException e) {
            logger.error(this, e);
        } 
    }
    
    protected void initTimeStamp()
    {
    	if ( refreshedTimeStamp == null )
    	{
    		try {
				refreshedTimeStamp = getForceRefreshTimeStamp();
			} catch (TopazException e) {
				logger.debug(this, "init time stamp:" + e.toString());
			}
    	}
    }
    
    protected String getForceRefreshTimeStamp() throws TopazException
    {
        try {
        	QueryContext queryCtx = QueryContext.getInstance();
        	String sqlKey = "S_QT_GET_WEB_APP_CNSTNT";
            String sqlQuery = queryCtx.getCompletedQuery(sqlKey, null);
            Map map = new HashMap();
            map.put("piCnstntName", "CACHE_FORCE_REFRESH");
            
        	TransactionContextManager.singleton().begin();
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sqlKey, map);
            logger.info(this, LogHelper.logSPCall(sqlQuery, (HashMap)map));
            boolean retCode = ps.execute();
            ResultSet rs = ps.getResultSet();
            String temp = null;
            while ( rs.next() )
            {
            	String code = StringUtils.trim(rs.getString("CODE"));
            	if ( "SQO_APP_CACHE".equals(code) ) { 
            		temp = rs.getString("COL_NAME");
            		logger.info(this, "refresh timestamp: " + temp);
            		break;
            	}
            }
            TransactionContextManager.singleton().commit();
            return temp;
	    } catch (SQLException sqle) {
	        throw new TopazException(sqle);
	    }
	    finally
	    {
	    	TransactionContextManager.singleton().rollback();
	    }
    }
    
    protected boolean isNeedToDoRefresh() throws TopazException
    {
    	logger.debug(this, "begin to check refresh flag");
    	String temp = this.getForceRefreshTimeStamp();
    	if ( StringUtils.equals(refreshedTimeStamp, temp) )
    	{
    		return false;
    	}
    	refreshedTimeStamp = temp;
    	return true;
    }

}
