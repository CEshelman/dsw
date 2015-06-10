package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.CacheableFactory;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * 
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney </a> <br/>
 *  
 */
public class SalesRepFactory_jdbc extends SalesRepFactory implements CacheableFactory {

    private static final String SEARCH_QT_MASS_DLG = "S_QT_MASS_DLG";

    /**
     * 
     * @param internetId
     *            the internet id
     * @param telesalesAccessLevel
     *            the telesales access level
     * @return the <code>SalesRep</code>
     * @throws TopazException
     * @see com.ibm.dsw.quote.common.domain.SalesRepFactory#create(java.lang.String,
     *      int)
     */
    public SalesRep create(String internetId, int telesalesAccessLevel) throws TopazException {
        SalesRep salesRep = (SalesRep) this.getFromCache(internetId);
        if (salesRep == null) {
            SalesRep_jdbc salesRep_jdbc = new SalesRep_jdbc(internetId, telesalesAccessLevel);
            salesRep_jdbc.isNew(true); //persist to DB2
            salesRep = salesRep_jdbc;
            this.putInCache(internetId, salesRep_jdbc);
        }
        return salesRep;
    }

    /**
     * 
     * @param objectId
     * @param object
     * @throws TopazException
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(SalesRepFactory.class, objectId, object);
    }

    /**
     * 
     * @param objectId
     * @return
     * @throws TopazException
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        return TransactionContextManager.singleton().getTransactionContext().get(SalesRepFactory.class, objectId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.SalesRepFactory#findDelegatesByQuote(java.lang.String)
     */
    public List findDelegatesByQuote(String quoteNum) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        List delegates = new ArrayList();
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", quoteNum);
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_DLGTS, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_DLGTS, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            rs = ps.getResultSet();

            while (rs.next()) {
                SalesRep_jdbc delegate = new SalesRep_jdbc();
                delegate.setEmailAddress(StringUtils.trimToEmpty(rs.getString(1)));
                delegate.setFullName(StringUtils.trimToEmpty(rs.getString(2)));
                delegates.add(delegate);
            }
            rs.close();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
	    	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this,"Failed to close the resultset!");
			}
        }
        return delegates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.SalesRepFactory#findDelegatesBySalesRep(java.lang.String)
     */
    public List findDelegatesBySalesRep(String internetId) throws TopazException {
        LogContext log = LogContextFactory.singleton().getLogContext();

        List delegates = new ArrayList();
        HashMap parms = new HashMap();
        parms.put("piUserID", internetId);
        parms.put("poGenStatus", "");
        
        ResultSet rs = null;
        try {

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(SEARCH_QT_MASS_DLG, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, SEARCH_QT_MASS_DLG, parms);
            log.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();

            rs = ps.getResultSet();

            while (rs.next()) {

                SalesRep_jdbc delegate = new SalesRep_jdbc();
                delegate.setEmailAddress(rs.getString(1));
                delegate.setCountryCode(rs.getString(2));
                delegate.setFullName(rs.getString(3));
                delegate.setFirstName(rs.getString(4));
                delegate.setLastName(rs.getString(5));
                delegate.setPhoneNumber(rs.getString(6));
                delegate.setFaxNumber(rs.getString(7));
                delegates.add(delegate);
            }
            rs.close();
        } catch (Exception e) {
            log.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
	    	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				log.error(this,"Failed to close the resultset!");
			}
        }
        return delegates;
    }

    public SalesRep createSalesRep(String internetId) throws TopazException {
        SalesRep salesRep = (SalesRep) this.getFromCache(internetId);

        if (salesRep == null) {

            SalesRep_jdbc salesRep_jdbc = new SalesRep_jdbc(internetId);

            salesRep_jdbc.isNew(true); //persist to DB2
            salesRep = salesRep_jdbc;
            //don't put it to cache
            //this.putInCache(internetId, salesRep_jdbc);

        }
        return salesRep;

    }

    public SalesRep findDelegateByID(String userId) throws TopazException {
        SalesRep salesRep = (SalesRep) this.getFromCache(userId);
        if (salesRep == null) {
            SalesRep_jdbc salesRep_jdbc = new SalesRep_jdbc(userId);
            try {
                salesRep_jdbc.hydrate(TopazUtil.getConnection());
                salesRep = salesRep_jdbc;
                this.putInCache(userId, salesRep_jdbc);
            } catch (Exception e) {
                throw new TopazException(e);
            }
        }
        return salesRep;
    }

    public String retriveFullRepName(String salesUserId)throws TopazException{
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        HashMap parms = new HashMap();
        parms.put("piSalesUserId", salesUserId);
        String salesFullName = null;
        
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_HAS_OWNED_QUOTE, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_HAS_OWNED_QUOTE, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            rs = ps.getResultSet();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            int hasOwnedQuote = ps.getInt(3);
            if(hasOwnedQuote > 0){
            	salesFullName = ps.getString(4);
            }
            
            ps.close();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
	    	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this,"Failed to close the resultset!");
			}
        }
        return salesFullName;
    }
    
}
