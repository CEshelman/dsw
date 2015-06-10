package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.ProductPortfolioFactory;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.Partner_Impl;
import com.ibm.dsw.quote.common.domain.PortfolioWithGovFlag;
import com.ibm.dsw.quote.common.domain.RselCtrldDistribtn;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Partner_jdbc</code> class is the jdbc implementation of Partner
 * domain object.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public class Partner_jdbc extends Partner_Impl implements PersistentObject, Serializable {

    public Partner_jdbc() {
        super();
    }

    public Partner_jdbc(boolean isReseller) {
        super(isReseller);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext(); 
    	HashMap parms = new HashMap();
        parms.put("piPartnerNum", custNum);
        parms.put("piLineOfBusiness", lineOfBusiness);
        
        ResultSet rs = null;
        try {
            ProductPortfolioFactory portfolioFactory = ProductPortfolioFactory.singleton();
            Map mapPortfolio = portfolioFactory.getProductPorfolioMap();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_PRTNRINFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_PRTNRINFO, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int returnCode = ps.getInt(1);
            
            if (returnCode == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), returnCode); 
            }

            this.custNameFull = ps.getString(4);
            this.custName = ps.getString(5);
            this.custName2 = ps.getString(6);
            this.custName3 = ps.getString(7);
            this.address1 = ps.getString(8);
            this.address2 = ps.getString(9);
            this.city = ps.getString(10);
            this.state = ps.getString(11);
            this.postalCode = ps.getString(12);
            this.country = ps.getString(13);
            this.phoneNum = ps.getString(14);
            this.faxNum = ps.getString(15);
            this.email = ps.getString(16);
            this.rdcNumList = parseRDCNumList(ps.getString(17));
            this.ibmCustNum = ps.getString(18);
            this.currencyCode = ps.getString(19);
            this.sapSalesOrgCode = ps.getString(20);
            this.PATierType = ps.getInt(21) == 1 ? 1 : 2;
            this.SWVNTierType = ps.getInt(22) == 1 ? 1 : 2;

            List list = new ArrayList();
            rs = ps.getResultSet();
            
            while (rs.next()) {
                String contactEmail = rs.getString(1);
                if (StringUtils.isNotBlank(contactEmail)) {
                    list.add(contactEmail);
                }
            }
            rs.close();
            
            if (ps.getMoreResults()) {
                rs = ps.getResultSet();
                this.authorizedPortfolioMap = new LinkedHashMap();
                
                while (rs.next()) {
	                String matlGrp5Code = StringUtils.trimToEmpty(rs.getString("SAP_MATL_GRP_5_COND_CODE"));
	                String tier1CustNum = StringUtils.trimToEmpty(rs.getString("TIER_1_CUST_NUM"));
	                String sapGovRselFlag = StringUtils.trimToEmpty(rs.getString("SAP_GOVT_RSEL_FLAG"));
	                
	                RselCtrldDistribtn rselCtrldDistribtn = new RselCtrldDistribtn();
	                
	                if (StringUtils.isNotBlank(matlGrp5Code)) {
		                List tier1List = (List) this.authorizedPortfolioMap.get(matlGrp5Code);
		                rselCtrldDistribtn.setSapMatlGrp5CondCode(matlGrp5Code);
		                
		                if (tier1List == null) {
		                    tier1List = new ArrayList();
		                    this.authorizedPortfolioMap.put(matlGrp5Code, tier1List);
		                }
		                
		                if (StringUtils.isNotBlank(tier1CustNum))
		                    rselCtrldDistribtn.setTier1CustNum(tier1CustNum);
		                
		                if (StringUtils.isNotBlank(sapGovRselFlag))
		                    rselCtrldDistribtn.setSapGovRselFlag(sapGovRselFlag);
		                
		                tier1List.add(rselCtrldDistribtn);
	                }
                }
                rs.close();
                
                Iterator iter = authorizedPortfolioMap.keySet().iterator();
                this.authorizedPortfolioList = new ArrayList();
                
                while (iter.hasNext()) {
                    String matlGrp5Code = (String) iter.next();
                    CodeDescObj portfolio = (CodeDescObj) mapPortfolio.get(matlGrp5Code);
                    
                    if (portfolio != null)
                        authorizedPortfolioList.add(portfolio);
                }
            }
            
            this.y9EmailList = list;
            
            this.isNew(false);
            this.isDeleted(false);
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when hydrate a Partner object", e);
        } finally{
        	try {
				if (null != rs )
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {

    }
    
    protected List parseRDCNumList(String rdcNums) {
        
        ArrayList rdcNumList = new ArrayList();
        if (StringUtils.isBlank(rdcNums))
            return rdcNumList;
        
        StringTokenizer st = new StringTokenizer(rdcNums, ",");
        while (st.hasMoreTokens()) {
            String rdc = st.nextToken();
            if (StringUtils.isNotBlank(rdc))
                rdcNumList.add(rdc.trim());
        }
        return rdcNumList;
    }
    
    protected List parsePortfolioList(Map map, String portfolios) throws TopazException {

        List list = new ArrayList();
        if (StringUtils.isBlank(portfolios))
            return list;

        StringTokenizer st = new StringTokenizer(portfolios, ",");
        while (st.hasMoreTokens()) {
            String codeWithGovFlag = st.nextToken();
            if (StringUtils.isNotBlank(codeWithGovFlag)) {
                String[] codeAndGovFlag = codeWithGovFlag.split("#");
                String code = codeAndGovFlag[0];
                String govFlag = codeAndGovFlag[1];
                if (StringUtils.isNotBlank(code)) {
                    CodeDescObj portfolio = (CodeDescObj) map.get(code.trim());
                    if (portfolio != null) {
                        PortfolioWithGovFlag portfolioWithGovFlag = new PortfolioWithGovFlag();
                        portfolioWithGovFlag.setPortfolio(portfolio);
                        portfolioWithGovFlag.setGovermentFlag(govFlag);
                        list.add(portfolioWithGovFlag);
                    }
                }
            }
        }
        return list;
    }
}
