package com.ibm.dsw.quote.customer.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.common.domain.jdbc.Customer_jdbc;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.customer.process.CustomerProcess_Impl;
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
 * This <code>CustomerProcess_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class CustomerProcess_jdbc extends CustomerProcess_Impl {
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public CustomerProcess_jdbc() { super(); }
    
    public List<CodeDescObj_jdbc> findAllAgreementTypes(int agrmntTypeFlag) throws QuoteException {
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("piAgrmntTypeFlag", agrmntTypeFlag);
        List<CodeDescObj_jdbc> agrmntTypes = new ArrayList<CodeDescObj_jdbc>();

        CallableStatement ps = null; ResultSet rs = null;
        try {
            //begin topaz transaction
            this.beginTransaction();
            
            ps = executeQuery(CommonDBConstants.DB2_S_QT_AGRMNT_TYPES, params);       
            rs = ps.getResultSet();
            
            while (rs.next()) {
                String code = StringUtils.trimToEmpty(rs.getString(1));
                String desc = StringUtils.trimToEmpty(rs.getString(2));
                CodeDescObj_jdbc codeObj = new CodeDescObj_jdbc(code, desc);
                agrmntTypes.add(codeObj);
            }

            //end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
        	close(ps, rs);
            this.rollbackTransaction();
        }
        
        return agrmntTypes;
    }
    
    public List<CodeDescObj_jdbc> findAllAgreementOptions() throws QuoteException {
        HashMap<String,Object> params = new HashMap<String,Object>();
        List<CodeDescObj_jdbc> agrmntOptions = new ArrayList<CodeDescObj_jdbc>();

        CallableStatement ps = null; ResultSet rs = null;
        try {
            //begin topaz transaction
            this.beginTransaction();
            
            ps = executeQuery(CommonDBConstants.DB2_S_QT_AGRMNT_OPTIONS, params);       
            rs = ps.getResultSet();
            
            while (rs.next()) {
                String code = StringUtils.trimToEmpty(rs.getString(1));
                String desc = StringUtils.trimToEmpty(rs.getString(2));
                CodeDescObj_jdbc codeObj = new CodeDescObj_jdbc(code, desc);
                agrmntOptions.add(codeObj);
            }

            //end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
        	close(ps, rs);
            this.rollbackTransaction();
        }
        
        return agrmntOptions;
    }
    
    public List<CodeDescObj_jdbc> findPAUNAgreementOptions() throws QuoteException {
        HashMap<String,Object> params = new HashMap<String,Object>();
        List<CodeDescObj_jdbc> agrmntOptions = new ArrayList<CodeDescObj_jdbc>();

        CallableStatement ps = null; ResultSet rs = null;
        try {
            //begin topaz transaction
            this.beginTransaction();
            
            ps = executeQuery(CommonDBConstants.DB2_S_QT_PAUN_AGRMNT_OPTIONS, params);       
            rs = ps.getResultSet();
            
            while (rs.next()) {
                String code = StringUtils.trimToEmpty(rs.getString(1));
                String desc = StringUtils.trimToEmpty(rs.getString(2));
                CodeDescObj_jdbc codeObj = new CodeDescObj_jdbc(code, desc);
                agrmntOptions.add(codeObj);
            }

            //end topaz transaction
            this.commitTransaction();
            
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
        	close(ps, rs);
            this.rollbackTransaction();
        }
        
        return agrmntOptions;
    }

    public CustomerSearchResultList searchAddressByAttr(String webQuoteNum, 
    		String customerName, String stateCode, String siteNum, 
    		int shareAgreement, int startIndex, String userIDInSession) throws QuoteException 
    {
        HashMap<String, Object> parms = new HashMap<String,Object>();
        
        parms.put("piWebQuoteNum", StringUtils.trimToEmpty(webQuoteNum) );
        parms.put("piCustName", StringUtils.trimToEmpty(customerName) );
        parms.put("piStateCode", StringUtils.trimToEmpty(stateCode) );
        // blank site number if searching by agreement
        siteNum = (shareAgreement != 1) ? truncateSiteNum(siteNum) : "";
        parms.put("piSiteNum", siteNum);
        parms.put("piShareAgreement", shareAgreement);
        parms.put("piStartIndex", startIndex);
        parms.put("piRecordCount", CustomerConstants.PAGE_ROW_COUNT); 
        parms.put("piUserID", userIDInSession); 
        
        return searchAddressByAttr(parms);
    }
    
    /** siteNum parameter has a limit of 10; truncate to first 10 */
	private String truncateSiteNum(String siteNum) {
        final int SITENUM_MAX_LEN = 10;
        final String zero = "0";
		siteNum = StringUtils.trimToEmpty(siteNum);
        if (siteNum.length() > SITENUM_MAX_LEN) {
        	siteNum = siteNum.substring(0, SITENUM_MAX_LEN); 
        }else if(siteNum.length() < SITENUM_MAX_LEN){
        	siteNum = StringUtils.leftPad(siteNum, SITENUM_MAX_LEN, zero);
        }
		return siteNum;
	}
    
    /** Call SP to get customer List by customer's attribute */
    protected CustomerSearchResultList searchAddressByAttr(
    		HashMap<String,Object> parms) throws QuoteException 
    {
    	String queryName = CommonDBConstants.DB2_S_SHIP_INSTALL_CUST_BY_ATTR;
        CallableStatement ps = null; ResultSet rs = null;
        try {
        	beginTransaction();
        	ps = executeQuery(queryName, parms);
        	rs = ps.getResultSet();
        	
        	CustomerSearchResultList resultList = new CustomerSearchResultList();
        	resultList.setResultCount(ps.getInt(2) ); // col position on CustSPs.xml
        	
            HashMap<String, Contract> contractMap = new HashMap<String, Contract>();            
            while (rs.next() ) {
            	Customer_jdbc customer_jdbc = addCustomer(rs, resultList);
            	
            	String custNum = StringUtils.trimToEmpty(rs.getString("CUST_NUM") );
            	String sapContractNum = rs.getString("rdc_num");
            	if (StringUtils.isNotBlank(sapContractNum) ) {
            		String contractKey = custNum + '_' + sapContractNum;

            		if (!contractMap.containsKey(contractKey) ) {
            			Contract contract = new Contract();
            			contract.setSapContractNum(sapContractNum);
            			contractMap.put(contractKey, contract);
            		}
        			customer_jdbc.addContract(contractMap.get(contractKey) );
            	}
            }
            commitTransaction();
            return resultList;

        } catch (Exception e) {
        	logContext.error(this, LogHelper.logSPCall(queryName, parms) );
        	throw new QuoteException(e);
        } finally {
        	close(ps, rs);
        	this.rollbackTransaction();
        }
    }

	private Customer_jdbc addCustomer(ResultSet rs, 
			CustomerSearchResultList resultList) throws SQLException, TopazException 
	{
		Customer_jdbc customer_jdbc = new Customer_jdbc();
		customer_jdbc.custName = rs.getString("CUST_NAME");
		customer_jdbc.address1 = rs.getString("ADR_1");
		customer_jdbc.internalAddress = rs.getString("ADR_INTRNL");
		customer_jdbc.city = rs.getString("CITY");
		customer_jdbc.sapRegionCode = rs.getString("SAP_REGION_CODE");
		customer_jdbc.postalCode = rs.getString("POSTAL_CODE");
		customer_jdbc.countryCode = rs.getString("CNTRY_CODE");
		customer_jdbc.ibmCustNum = rs.getString("IBM_CUST_NUM");
		customer_jdbc.custNum = rs.getString("CUST_NUM");
		customer_jdbc.rdcNumList = StringHelper.parseStr2List(rs.getString("rdc_num"));
		customer_jdbc.isBPRelatedCust = (rs.getInt("bp_related_cust") == 1);
		resultList.add(customer_jdbc);
		
		return customer_jdbc;
	}

    public CallableStatement executeQuery(String queryName, HashMap<String, Object> params) 
    	throws TopazException, SQLException 
    {
    	QueryContext queryCtx = QueryContext.getInstance();
    	String sqlQuery = queryCtx.getCompletedQuery(queryName, null);
    	CallableStatement callStmt = TopazUtil.getConnection().prepareCall(sqlQuery);
    	queryCtx.completeStatement(callStmt, queryName, params);

    	callStmt.execute();

    	int poGenStatus = callStmt.getInt(1); // check return status (poGenStatus), then log
    	if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
    		throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
    	} else {
    		logContext.debug(this, LogHelper.logSPCall(sqlQuery, params) );
    	}
        
        return callStmt;
    }
    
    private void close(Statement stmt, ResultSet rs) {
    	if (rs != null) try { rs.close(); } catch (SQLException e) { logContext.error(this, LogThrowableUtil.getStackTraceContent(e));}
    	if (stmt != null) try { stmt.close(); } catch (SQLException e) { logContext.error(this, LogThrowableUtil.getStackTraceContent(e)); }
	}


}
