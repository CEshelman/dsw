package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerFactory;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.common.domain.MigratePart_Impl;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrateRequest_Impl;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MigrationRequestCustProcess_jdbc</code> class is jdbc implementation of
 * MigrationRequestCustProcess_Impl.
 * 
 * 
 * @author <a href="mmzhou@cn.ibm.com">Tyler Zhou </a> <br/>
 * 
 * Creation date: 2012-05-24
 */
public class MigrationRequestProcess_jdbc extends MigrationRequestProcess_Impl {

    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

    public MigrateRequest getMigrtnReqByReqNum(String requestNum) throws QuoteException{
    	MigrateRequest request = new MigrateRequest_Impl();
    	HashMap params = new HashMap();
        params.put("piRequestNum", requestNum);
        
        ResultSet rs = null;
        try {
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_MIGRATE_REQUEST, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_MIGRATE_REQUEST, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new SPException(returnCode);
            }
            
            rs = ps.getResultSet();
            if ( rs.next() )
            {
            	request.setRequestNum(rs.getString("MIGRTN_REQST_NUM"));
            	request.setBillingFreq(rs.getString("BILLG_FRQNCY_CODE"));
            	request.setCoverageTerm(rs.getInt("CVRAGE_TERM"));
            	
            	request.setOrginalCANum(rs.getString("SAP_SALES_ORD_NUM"));
            	request.setSoldToCustNum(rs.getString("SOLD_TO_CUST_NUM"));
            	request.setSapCtrctNum(rs.getString("SAP_CTRCT_NUM"));
            	request.setReslCustNum(rs.getString("RSEL_CUST_NUM"));
            	request.setPayerCustNum(rs.getString("PAYER_CUST_NUM"));
            	request.setSapIDocNum(rs.getString("SAP_INTRMDIAT_DOC_NUM"));
            	request.setSapDistChnl(rs.getString("SAP_DISTRIBTN_CHNL_CODE"));
            	request.setMigrtnStageCode(rs.getString("MIGRTN_STAGE_CODE"));
            	request.setLob(rs.getString("PROG_CODE"));
            	request.setCountryCode(rs.getString("CNTRY_CODE"));
            	request.setCurrencyCode(rs.getString("CURRNCY_CODE"));
            	request.setFulfillmentSrc(rs.getString("FULFILLMENT_SRC"));
            	request.setOrignalSapDistChnl(rs.getString("ORIGINAL_SAP_DISTRIBTN_CHNL_CODE"));
            }
            List<MigratePart> list = new ArrayList<MigratePart>();
            if ( ps.getMoreResults() )
            {
            	rs = ps.getResultSet();
            	while ( rs.next() )
            	{
            		MigratePart part = new MigratePart_Impl();
                	part.setPartNum(rs.getString("PART_NUM"));
                	part.setSeqNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
                	part.setMigration(true);
                	part.setPartDesc(rs.getString("PART_DESC"));
                	list.add(part);
            	}
            }
            request.setParts(list);
          
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);           
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
        	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
        
    	return request;
    }

    public Customer getCustomerInfoByRequest(MigrateRequest request) throws QuoteException{
    	if(request == null || StringUtils.isBlank(request.getSoldToCustNum())){
    		return null;
    	}
    	Customer cust = null;
    	try{
    		cust = CustomerFactory.singleton().findByQuoteNum(request.getLob(), 0, request.getSoldToCustNum(), request.getSapCtrctNum(), null, 0);
	    } catch (TopazException e){
	        logger.error(this, e.getMessage());
	        throw new QuoteException(e);
	    }
    	return cust;
    }

    public Partner getResellerInfoByRequest(MigrateRequest request) throws QuoteException{
    	if(request == null || StringUtils.isBlank(request.getReslCustNum())){
    		return null;
    	}
    	return this.getPartnerById(request.getReslCustNum(), request.getLob());
    }
    
    public Partner getPayerInfoByRequest(MigrateRequest request) throws QuoteException{
    	if(request == null || StringUtils.isBlank(request.getPayerCustNum())){
    		return null;
    	}
    	return this.getPartnerById(request.getPayerCustNum(), request.getLob());
    }
    
	private Partner getPartnerById(String custNum, String lineOfBusiness)throws QuoteException{

		 try{
				Partner partner = PartnerFactory.singleton().findPartnerByNum(custNum, lineOfBusiness);
				return partner;
		  } catch (TopazException e){
		        logger.error(this, e.getMessage());
		        throw new QuoteException(e);
		 }
		
	}
	
    public  void updateMigrateInfByRequestNum(MigrateRequest request,String userId) throws QuoteException{
    	HashMap params = new HashMap();
        params.put("piRequestNum", request.getRequestNum());
        params.put("piCustNum", request.getSoldToCustNum());
        params.put("piReselNum", request.getReslCustNum());
        params.put("piPayerNum", request.getPayerCustNum());
        params.put("piFufillmentSrc", request.getFulfillmentSrc());
        params.put("piUserId", userId);
        params.put("piLob", request.getLob());
        params.put("piSapCtrctNum", request.getSapCtrctNum());
    
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MIGRATE_REQUST_INF_UPT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MIGRATE_REQUST_INF_UPT, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            ps.execute();
            retCode = ps.getInt(1);
            
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            
        } catch (SQLException e) {
            logger.error("Failed to insert the customer to the database!", e);
            throw new QuoteException(e);
        } catch (TopazException e) {
	        logger.error(this, e.getMessage());
	        throw new QuoteException(e);
		}
    	
    }
	
	public void updateMigrateRequestSubmission(int piSuccessFlag,String piSapIDocNum,String piRequestNum,String piCreatorID,String piPartError,String piHeadError) throws QuoteException{
	    	HashMap params = new HashMap();
	        params.put("piSuccessFlag", piSuccessFlag);
	        params.put("piSapIDocNum", piSapIDocNum);
	        params.put("piRequestNum", piRequestNum);
	        params.put("piCreatorID", piCreatorID);
	        params.put("piPartError",piPartError);
	        params.put("piHeadError",piHeadError);	        
	        
	        try {
	            this.beginTransaction();
	            
	            QueryContext queryCtx = QueryContext.getInstance();
	            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_MIGRATE_REQSTD_SUBMISSION, null);
	            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_MIGRATE_REQSTD_SUBMISSION, params);
	            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

	            ps.execute();
	            int returnCode = ps.getInt(1);

	            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
	            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
	            	logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
	            	throw new SPException(returnCode);
	            }
	            
	            this.commitTransaction();
	          
	        } catch (SQLException e) {
	            logger.error(this, e.getMessage());
	            throw new QuoteException(e);           
	        } catch (TopazException e){
	            logger.error(this, e.getMessage());
	            throw new QuoteException(e);
	        } finally {
	            this.rollbackTransaction();
	        }
	    }

    public void updateNewPAWebQuoteImpl(String userId, String webQuoteNum, String fctToPaMigrationFlag)throws QuoteException{
    	HashMap params = new HashMap();
        params.put("piCreatorID", userId);
        params.put("piWebQuoteNum", webQuoteNum);
        params.put("piFctToPaMigrtnflag", fctToPaMigrationFlag);
        
        
        try {
            this.beginTransaction();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_FCT_PA_MIGRATE_INFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_FCT_PA_MIGRATE_INFO, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            int returnCode = ps.getInt(1);

            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
            	logger.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            	throw new SPException(returnCode);
            }
            
            this.commitTransaction();
          
        } catch (SQLException e) {
            logger.error(this, e.getMessage());
            throw new QuoteException(e);           
        } catch (TopazException e){
            logger.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
}
