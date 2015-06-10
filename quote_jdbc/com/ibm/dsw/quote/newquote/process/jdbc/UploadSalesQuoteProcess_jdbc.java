package com.ibm.dsw.quote.newquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidAcquisitionException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCustCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidLOBException;
import com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcess_Impl;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetPart;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UploadSalesQuoteProcess_jdbc<code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-16
 */
public class UploadSalesQuoteProcess_jdbc extends UploadSalesQuoteProcess_Impl {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcess_Impl#importSpreadSheetQuote(com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote, com.ibm.dsw.quote.appcache.domain.CodeDescObj, com.ibm.dsw.quote.appcache.domain.Country, java.lang.String)
     */
    public void importSpreadSheetQuote(SpreadSheetQuote squote, String userId) 
    				throws NewQuoteInvalidCntryCurrencyException, NewQuoteInvalidCustCurrencyException,NewQuoteInvalidLOBException, NewQuoteInvalidAcquisitionException, NewQuoteInvalidCntryException, QuoteException  {
        
        /* validate lobcode on spreadsheet */
        validateLOB(squote);

        /* validate country on spreadsheet */
        validateCountry(squote);

        /* validate acquisition for FCT */
        validateAcquisition(squote);
        
        //Validate spreadsheet data, init lob
        validateCustAndCtrct(squote, userId);
        
        /* validate currency on spreadsheet */
        validateCurrency(squote);
        
        validateStartEndDates(squote);
        
        //validate term and billing frequency for SaaS parts
        validateFrequenciesAndTerms(squote);
        
        //validate if including both SW and SaaS parts for PGS
        if (squote.isPGSFlag()) {
        	validateBothSWSaaSParts(squote);
        }

        //import lineItem imfor
        boolean newQuote = validateImportOption(squote, userId);        
      
        if (newQuote) {
        	if (squote.isPGSFlag()) {
        		processAsNewSessionQuote(squote, userId, QuoteConstants.QUOTE_AUDIENCE_CODE_PGS, newQuote);
        	} else {
        		processAsNewSessionQuote(squote, userId, QuoteConstants.QUOTE_AUDIENCE_CODE_SQO, newQuote);
        	}
        } else {
            processAsExportedQuote(squote, userId, newQuote);
        }
        
        /* update special bid indicators, we just determine sysInitSpBidFlag
         	speclBidManualInitiatedFlag is coming from part and pricing tab.
        */
        updateSpecialBidFlag(userId, StringUtils.trimToEmpty(squote.getQuoteSVPLevel()));
    }
 
    /**
     * @param creatorId
     * @throws TopazException
     */
    public void updateValidRenewalLineItem(SpreadSheetQuote squote, String webQuoteNum)
            throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        String sqlQuery = null;
        HashMap params = new HashMap();
        params.put("piCreatorID", "");
        params.put("piWebQuoteNum", webQuoteNum);
        try {
            this.beginTransaction();
            logger.debug(this, "validate renewal line item....  webQuoteNum: " + webQuoteNum);

            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.SP_VALIDATE_RENEWAL_LINEITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, NewQuoteDBConstants.SP_VALIDATE_RENEWAL_LINEITEM, params);
            ps.execute();
            logger.debug(logger, "finished to update line item sourced from  renewal quote");

            List invalidPartList = squote.getInvalidPartList();
            ResultSet rs = ps.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    String invalidPartNum = StringUtils.trimToEmpty(rs.getString(1));
                    if (invalidPartList.contains(invalidPartNum)) {
                        continue;
                    } else {
                        invalidPartList.add(invalidPartNum);
                    }
                }
                rs.close();
            }
            this.commitTransaction();
        } catch (Exception e) {
            logger.error(this, LogHelper.logSPCall(sqlQuery, params));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }
    
    /**
     * @param creatorId
     * @throws TopazException
     */
    public void stripOutSaaSPartsFromInvalidParts(SpreadSheetQuote squote)
            throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        List invalidPartNumList = squote.getInvalidPartList();
        if(null != invalidPartNumList && !invalidPartNumList.isEmpty()){
	        String sqlQuery = null;
	        HashMap params = new HashMap();
	        params.put("piPartNumList", squote.getInvalidPartNums());
	        params.put("piCreatorId", StringUtils.EMPTY);
		    params.put("piLineOfBusCode", squote.getLobCode());
		    params.put("piAcqrtnCode", squote.getAcquisition());
		    params.put("piCountryCode", squote.getCntryCode());
		    params.put("piAudience", NewQuoteDBConstants.AUDIENCE);
	        try {
	            this.beginTransaction();
	            logger.debug(this, "validate imported SaaS line item....");
	
	            QueryContext queryCtx = QueryContext.getInstance();
	            sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.S_QT_SAAS_PART_BY_ID, null);
	            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
	
	            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	            queryCtx.completeStatement(ps, NewQuoteDBConstants.S_QT_SAAS_PART_BY_ID, params);
	            ps.execute();
	            logger.debug(logger, "finished to validate imported SaaS line item");
	
		        List saasPartNumList = squote.getSaaSPartNumList();

	            ResultSet rs = ps.getResultSet();
	            if (rs != null) {
	                while (rs.next()) {
	                    String saasPartNum = StringUtils.trimToEmpty(rs.getString(1));
						
	                    if (invalidPartNumList.contains(saasPartNum)){
                    		invalidPartNumList.remove(saasPartNum);
	                    }
	                    
	                    if (saasPartNumList.contains(saasPartNum)) {
	                        continue;
	                    } else {
                    		saasPartNumList.add(saasPartNum);
	                    }
	                }
	                squote.setSaaSPartNumList(saasPartNumList);
	                rs.close();
	            }
	            this.commitTransaction();
	        } catch (Exception e) {
	            logger.error(this, LogHelper.logSPCall(sqlQuery, params));
	            throw new QuoteException(e);
	        } finally {
	            this.rollbackTransaction();
	        }
        }
    }
    /**
     * @param PartNumList
     * @throws TopazException
     */
    public List searchSaaSPartsByNumber(SpreadSheetQuote squote)
    throws QuoteException {
    	LogContext logger = LogContextFactory.singleton().getLogContext();
    	String sqlQuery = null;
    	HashMap params = new HashMap();
	    params.put("piCreatorId", StringUtils.EMPTY);
	    params.put("piLineOfBusCode", squote.getLobCode());
	    params.put("piAcqrtnCode", squote.getAcquisition());
	    params.put("piCountryCode", squote.getCntryCode());
	    params.put("piAudience", NewQuoteDBConstants.AUDIENCE);
	    params.put("piPartNumList", squote.getSaaSPartNums());
		List validPartList = null;
		try {
			this.beginTransaction();
			logger.debug(this, "validate imported SaaS line item....");
			
			QueryContext queryCtx = QueryContext.getInstance();
			sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.S_QT_SAAS_PART_BY_ID, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(ps, NewQuoteDBConstants.S_QT_SAAS_PART_BY_ID, params);
			ps.execute();
			logger.debug(logger, "finished to validate imported SaaS line item");
			
			ResultSet rs = ps.getResultSet();
			if (rs != null) {
				validPartList = new ArrayList();
				while (rs.next()) {
					String saasPartNum = StringUtils.trimToEmpty(rs.getString(1));
					String restrictFlag = StringUtils.trimToEmpty(rs.getString(2));
					String obsoluteFlag = StringUtils.trimToEmpty(rs.getString(3));
					String certifiedFlag = StringUtils.trimToEmpty(rs.getString(4));
					if(!certifiedFlag.equals("1")){
						squote.getUncertifiedSaaSPartNumList().add(saasPartNum);
					}
					if(!obsoluteFlag.equals("0")){
						squote.getInactiveSaaSPartNumList().add(saasPartNum);
					}
					if(!restrictFlag.equals("0")){
						continue;
					}
					validPartList.add(StringUtils.trimToEmpty(saasPartNum));				
				}
				rs.close();
			}
			this.commitTransaction();
		} catch (Exception e) {
			logger.error(this, LogHelper.logSPCall(sqlQuery, params));
			throw new QuoteException(e);
		} finally {
			this.rollbackTransaction();
		}
		return validPartList;
    }
    
	public void stripOutSWPartsFromInvalidParts(SpreadSheetQuote squote)
    throws QuoteException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		
		List invalidSaaSPartNumList = squote.getInvalidSaaSPartList();
		if(null != invalidSaaSPartNumList && !invalidSaaSPartNumList.isEmpty()){
		    String sqlQuery = null;
		    HashMap params = new HashMap();
		    params.put("piCreatorId", StringUtils.EMPTY);
		    params.put("piLineOfBusCode", squote.getLobCode());
		    params.put("piAcqrtnCode", squote.getAcquisition());
		    params.put("piCountryCode", squote.getCntryCode());
		    params.put("piAudience", NewQuoteDBConstants.AUDIENCE);
		    params.put("piPartNumList", squote.getInvalidSaaSPartNums());
		    try {
		        this.beginTransaction();
		        logger.debug(this, "validate imported Software line item....");
		
		        QueryContext queryCtx = QueryContext.getInstance();
		        sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.S_QT_PART_BY_ID, null);
		        logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
		
		        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
		        queryCtx.completeStatement(ps, NewQuoteDBConstants.S_QT_PART_BY_ID, params);
		        ps.execute();
		        logger.debug(logger, "finished to validate imported Software line item");
		
		        List partNumList = squote.getPartNumList();
		
		        ResultSet rs = ps.getResultSet();
		        if (rs != null) {
		            while (rs.next()) {
		                String partNum = StringUtils.trimToEmpty(rs.getString("PART_NUM").trim());
		                if (invalidSaaSPartNumList.contains(partNum)){
		                	invalidSaaSPartNumList.remove(partNum);
		                }
		                
		                if (partNumList.contains(partNum)) {
		                    continue;
		                } else {
		                	partNumList.add(partNum);
		                }
		            }
		            squote.setPartNumList(partNumList);
		            rs.close();
		        }
		        this.commitTransaction();
		    } catch (Exception e) {
		        logger.error(this, LogHelper.logSPCall(sqlQuery, params));
		        throw new QuoteException(e);
		    } finally {
		        this.rollbackTransaction();
		    }
		}
    }
	
	
	 public void updateApplianceLineItem(String webQuoteNum, List appliancePartList)
     throws QuoteException {
		 LogContext logger = LogContextFactory.singleton().getLogContext();
	        
	        String sqlQuery = null;
	        HashMap params = new HashMap();
	        StringBuffer partNumList =new StringBuffer("");
	        
	        for(Iterator it = appliancePartList.iterator(); it.hasNext();){
	        	SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
	        	partNumList.append(ssPart.getEpPartNumber()+",");
	        	
	        }
	        
	        
	        
	        params.put("piWebQuoteNum", webQuoteNum);
	        params.put("piPartNumList", partNumList.toString());
	        try {
	            this.beginTransaction();
	            logger.debug(this, "update appliance line item....  webQuoteNum: " + webQuoteNum);

	            QueryContext queryCtx = QueryContext.getInstance();
	            sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.IU_QT_APPLIANCE, null);
	            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

	            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	            queryCtx.completeStatement(ps, NewQuoteDBConstants.IU_QT_APPLIANCE, params);
	            ps.execute();
	            logger.debug(logger, "finished to update applance line item ");
	           
	            this.commitTransaction();
	        } catch (Exception e) {
	            logger.error(this, LogHelper.logSPCall(sqlQuery, params));
	            throw new QuoteException(e);
	        } finally {
	            this.rollbackTransaction();
	        }

		 
		 
	 }
	
	
}
