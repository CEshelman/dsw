/*
 * @(#)TopazCacheableFactoryHelper.java		11/11/2003
 * 
 * (C) Copyright 2000-2002 by IBM Corporation
 *  All Rights Reserved.
 *  
 * This software is the confidential and proprietary information
 * of the IBM Corporation. ("Confidential Information"). Redistribution
 * of the source code or binary form is not permitted without prior authorization
 * from the IBM Corporation.
 */
package com.ibm.dsw.quote.base.cache;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.PrintSPTimeTrace;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.quartz.common.Attributes;
import com.ibm.ead4j.quartz.common.CacheContext;
import com.ibm.ead4j.quartz.common.CacheContextFactory;
import com.ibm.ead4j.quartz.exception.CacheException;
import com.ibm.ead4j.quartz.exception.InvalidHandleException;
import com.ibm.ead4j.quartz.exception.ObjectNotFoundException;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.persistence.jdbc.KeyedPersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.MessageKeys;
import com.ibm.ead4j.topaz.persistence.jdbc.MessageUtil;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;

/**
 * <p>
 * The <code>TopazCacheableFactoryHelper</code> is the helper class for TOPAZ
 * factories to use Quartz to cache objects.
 * </p>
 * 
 * @author O'Neil Palmer
 * 
 * @history 11/11/2003 12:21:27 PM - Created
 * @history 03/16/2006 - Removed initialization of Cache which is not required
 *          anymore
 * @history 03/02/2007 - defined cacheRegion,cacheGroup to feed the special
 *          needs of SQO project. By Nathan.
 * 
 * @since EAD4J 3.4
 */
public class QuoteTopazCacheableFactoryHelper {

    private static String cacheRegion = "QUOTE_CACHE_REGION";

    private static String cacheGroup = "QUOTE_CACHE_GROUP";

    /** unique instance of this helper class */
    private static QuoteTopazCacheableFactoryHelper singleton = new QuoteTopazCacheableFactoryHelper();

    LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * <p>
     * This method returns the single instance of this class.
     * </p>
     * 
     * @return QuoteTopazCacheableFactoryHelper
     */
    public static QuoteTopazCacheableFactoryHelper singleton() {
        return singleton;
    }

    /**
     * <p>
     * This method initializes the global cache for the given factory.
     * </p>
     * 
     * @param factory
     *            TopazCacheableFactory
     * @return HashMap
     */
    public Map initializeCache(TopazCacheableFactory factory) throws TopazException {
        if (factory == null) {
            return null;
        }
        if (factory instanceof QuoteCacheableFactory) {
            return ((QuoteCacheableFactory) factory).initializeCache(TopazUtil.getConnection());
        }
        String sqlKey = factory.getSelectSqlQueryStringKey();
        HashMap parameters = factory.getSelectSqlParms();

        HashMap objectMap = new LinkedHashMap();

        int rowCount = 0;

        QueryContext queryCtx = QueryContext.getInstance();

        String sqlQuery = queryCtx.getCompletedQuery(sqlKey, null);
        ResultSet rs = null;
        try {
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sqlKey, parameters);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, new HashMap()));

            boolean retCode = ps.execute();
            if (!retCode)
                throw new TopazException("execute callable statement fails!!!");
            rs = ps.getResultSet();

            KeyedPersistentObject object = null;
            while (rs.next()) {
                object = factory.hydrate(rs, false);

                //add object to local cache
                if (object != null) {
                    putInLocalCache(factory, object.getKey(), object);

                    objectMap.put(object.getKey(), object);
                    rowCount++;
                }
            }
            
            TransactionContextManager.singleton().commit();
        } catch (SQLException sqle) {
            throw new TopazException(sqle);
        }finally{
        	try {
				rs.close();
			} catch (SQLException e) {
				logContext.error(this, e.getMessage());
			}
        }

        return objectMap;
    }

    /**
     * This method return all cached objects populated by the corresponding
     * factory
     * 
     * @param objectFactory
     * @return
     * @throws TopazException
     */
    public List findObjectList(TopazCacheableFactory objectFactory) throws TopazException {
        Map map = null;
        Object obj = null;

        CacheContext context = CacheContextFactory.singleton().getCacheContext();

        try {
            map = (HashMap) context.getCacheAccess(cacheRegion).get(objectFactory.getClass().getName());
        } catch (ObjectNotFoundException onfe) {
        	logContext.error(this, onfe);
        } catch (InvalidHandleException ihe) {
            try {
                //destroy object when expired
                context.getCacheAccess(cacheRegion).destroy(objectFactory.getClass().getName());
            } catch (CacheException e) {
                throw new TopazException(e);
            }
        } catch (CacheException ce) {
            throw new TopazException(ce);
        }
        if (map == null) {
            map = initializeCache(objectFactory);
            try {
                context.getCacheAccess(cacheRegion).put(objectFactory.getClass().getName(), cacheGroup,
                        getAttributes(), map);
            } catch (CacheException ce) {
                throw new TopazException(ce);
            }
        }
        Iterator iterator = map.keySet().iterator();
        List result = new ArrayList();
        while (iterator.hasNext()) {
            Object object = map.get(iterator.next());
            result.add(object);
        }
        return result;
    }

    /**
     * This method return all cached objects populated by the corresponding
     * factory
     * 
     * @param objectFactory
     * @return
     * @throws TopazException
     */
    public Map<String, Integer> findAllSPinCache(TopazCacheableFactory objectFactory) throws TopazException {
        Map map=null;
        Map<String, Integer> spMap = null;
        CacheContext context = CacheContextFactory.singleton().getCacheContext();
		try {
            map = (HashMap) context.getCacheAccess(cacheRegion).get(objectFactory.getClass().getName());
        } catch (InvalidHandleException ihe) {
            try {
                //destroy object when expired
                context.getCacheAccess(cacheRegion).destroy(objectFactory.getClass().getName());
            } catch (CacheException e) {
                throw new TopazException(e);
            }
        } catch (CacheException ce) {
            throw new TopazException(ce);
        }
		if (map != null) {
        	Iterator iterator = map.keySet().iterator();
        	spMap = new HashMap<String, Integer>();
            while (iterator.hasNext()) {
                Object object = map.get(iterator.next());
                if(object instanceof PrintSPTimeTrace){
                	PrintSPTimeTrace printSPTimeTrace = (PrintSPTimeTrace)object;
                	spMap.put(printSPTimeTrace.getSPName(), printSPTimeTrace.getTimeThreshold());
                }
           }
         }
        return spMap;
    }
    
    /**
     * This method return all cached sortabel objects populated by the
     * corresponding factory
     * 
     * @param objectFactory
     * @return
     * @throws TopazException
     */
    public List findSortObjectList(TopazCacheableFactory objectFactory) throws TopazException {
        List result = this.findObjectList(objectFactory);
        if (result != null && result.size() > 0) {
            try {
                Collections.sort(result);
            } catch (Exception e) {
                return result;
            }
        }
        return result;
    }

    /**
     * <p>
     * This method puts the <b>object </b> identified by the supplied
     * <b>objectId </b> in the cache.
     * </p>
     * 
     * @param objectId
     *            object id of the object to place in the cache
     * @param object
     *            object to place in the cache
     * @throws TopazException
     *             if the object cannot be placed in the cache
     */
    public void putInGlobalCache(TopazCacheableFactory factory, Object objectId, Object object) throws TopazException {
        Map map = null;
        CacheContext context = null;
        try {
            context = CacheContextFactory.singleton().getCacheContext();
            map = (HashMap) context.getCacheAccess(cacheRegion).get(factory.getClass().getName(), cacheGroup, null);
        } catch (ObjectNotFoundException e) {
            map = initializeCache(factory);
        } catch (CacheException ce) {
            throw new TopazException(ce);
        }

        if (map != null) {
            map.put(objectId, object);
            try {
                context.getCacheAccess(cacheRegion).replace(factory.getClass().getName(), cacheGroup, map);
            } catch (Exception e) {
                throw new TopazException(e);
            }
        } else
            throw new TopazException(MessageUtil
                    .getString(MessageKeys.EAD4J_TOPAZ_QUARTZ_EXT_CACHEABLE_FACTORY_INIT_ERROR_MSG) //$NON-NLS-1$
                    + factory.getClass());
    }

    /**
     * <p>
     * This method retrieves the an <code>Object</code> from the cache if the
     * object identified by <b>objectId </b> is in the cache. Otherwise, null is
     * returned.
     * </p>
     * 
     * @param objectId
     *            object id of the object to place in the cache
     * @throws TopazException
     *             if the object cannot be retrieved from the cache
     */
    public Object getFromGlobalCache(TopazCacheableFactory factory, Object objectId) throws TopazException {
        Map map = null;
        Object obj = null;

        CacheContext context = CacheContextFactory.singleton().getCacheContext();

        try {
            map = (HashMap) context.getCacheAccess(cacheRegion).get(factory.getClass().getName());
        } catch (ObjectNotFoundException onfe) {
        	logContext.error(this, onfe);
        } catch (InvalidHandleException ihe) {
            try {
                //destroy object when expired
                context.getCacheAccess(cacheRegion).destroy(factory.getClass().getName());
            } catch (CacheException e) {
                throw new TopazException(e);
            }
        } catch (CacheException ce) {
            throw new TopazException(ce);
        }
        if (map != null) {
            obj = map.get(objectId);
            putInLocalCache(factory, objectId, obj);
        } else {
            map = initializeCache(factory);
            try {
                context.getCacheAccess(cacheRegion).put(factory.getClass().getName(), cacheGroup, getAttributes(), map);
            } catch (CacheException ce) {
                throw new TopazException(ce);
            }
            obj = map.get(objectId);
        }
        return obj;

    }

    /**
     * Adds the given object to the local cache.
     * 
     * @param factory
     *            TopazCacheableFactory
     * @param objectId
     *            Object
     * @param object
     *            Object
     */
    private void putInLocalCache(TopazCacheableFactory factory, Object objectId, Object object) throws TopazException {
        TransactionContextManager.singleton().getTransactionContext().put(factory.getClass(), objectId, object);
    }

    private Attributes getAttributes() {
        return getAttributes(cacheRegion, cacheGroup);
    }

    private Attributes getAttributes(String region, String group) {
        CacheContext context = CacheContextFactory.singleton().getCacheContext();
        Attributes attributes = null;
        try {
            attributes = context.getCacheAccess(region).getAttributes(group);
        } catch (Exception e) {
            this.logContext.error(this, e);
        }
        return attributes;
    }

    /**
     * Close the cache in case of application shutting down.
     */
    public void closeCache(){
        CacheContext cc = CacheContextFactory.singleton().getCacheContext();
       	logContext.debug(this,"Closing Cache Group: QUOTE_CACHE_GROUP");
        QuoteCache cache = (QuoteCache) cc.getCache();
        cache.close(cacheRegion);
       	logContext.debug(this,"Cache Group: QUOTE_CACHE_GROUP closed.");
    	
    }
}