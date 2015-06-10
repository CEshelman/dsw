package com.ibm.dsw.quote.opportunity.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.util.OpptNumServiceLock;
import com.ibm.dsw.quote.common.util.OpptNumServiceLockAcquireFailedException;
import com.ibm.dsw.quote.opportunity.domain.Opportunity;
import com.ibm.dsw.quote.opportunity.exception.OpportunityDSException;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OpportunityCommonProcess_jdbc<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2012-02-16
 */
public class OpportunityCommonProcess_jdbc extends OpportunityCommonProcess_Impl {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

	@Override
	public List<Opportunity> getValidOpportunityListByWebQuote(String webQuoteNum)
			throws OpportunityDSException {
		List<Opportunity> opptList = new LinkedList<Opportunity>();

        // introducing SCODS conn management
        try{
        	OpptNumServiceLock.lock();
        }catch(OpptNumServiceLockAcquireFailedException e){
        	throw new OpportunityDSException(e);
        }
        
        try {
            //begin topaz transaction
            this.beginTransaction();
            
            HashMap params = new HashMap();
            params.put("piWebQuoteNum", webQuoteNum);
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_OPPT_LIST_BY_QT_NUM, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_OPPT_LIST_BY_QT_NUM, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            
            int poOpenCur = callStmt.getInt(3);
            if(poOpenCur>0){
            	String opptNum = null;
            	String opptName = null;
                Opportunity oppt = null;
                ResultSet rs = callStmt.getResultSet();
                while (rs.next()) {
                    opptNum = StringUtils.trimToEmpty(rs.getString(1));
                    opptName = StringUtils.trimToEmpty(rs.getString(2));
                    oppt = new Opportunity(opptNum, opptName);
                    opptList.add(oppt);
                }
                rs.close();
            }

            //end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new OpportunityDSException(e);
        } catch (SPException se) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(se));
            throw new OpportunityDSException(se);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new OpportunityDSException(te);
        } finally {
            this.rollbackTransaction();
        	OpptNumServiceLock.unLock();
        }
        
        return opptList;
     
	}

	@Override
	public boolean isValidOpptNum(String opptNum) throws OpportunityDSException {
		
		boolean isValid = false;

        // introducing SCODS conn management
        try{
        	OpptNumServiceLock.lock();
        }catch(OpptNumServiceLockAcquireFailedException e){
        	throw new OpportunityDSException(e);
        }
        
        try {
            //begin topaz transaction
            this.beginTransaction();
            
            HashMap params = new HashMap();
            params.put("piOpptNum", opptNum);
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_IS_OPPT_VLD, null);
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, CommonDBConstants.DB2_S_QT_IS_OPPT_VLD, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);
            
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
            }
            
            int poIsValid = callStmt.getInt(3);
            if(poIsValid == 1){
            	isValid = true;
            }
            //end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new OpportunityDSException(e);
        } catch (SPException se) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(se));
            throw new OpportunityDSException(se);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new OpportunityDSException(te);
        } finally {
            this.rollbackTransaction();
        	OpptNumServiceLock.unLock();
        }
        
        return isValid;
     
	}
	
	
}
