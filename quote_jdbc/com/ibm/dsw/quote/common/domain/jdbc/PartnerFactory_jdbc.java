package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.ProductPortfolioFactory;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.partner.config.PartnerDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerFactory_jdbc</code> class is the jdbc implementation of
 * Partner factory.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public class PartnerFactory_jdbc extends PartnerFactory {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.PartnerFactory#findResellerByNum(java.lang.String,
     *      java.lang.String, java.lang.String, int, int)
     */
    public SearchResultList findResellersByNum(String lobCode, String custCnt, String custNum, int tierType,
            int pageIndex, String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws TopazException {
        return findPartnersByCustNum(lobCode, custCnt, custNum, PartnerDBConstants.PARTNER_TYPE_RESELLER, tierType,
                pageIndex, PartnerDBConstants.PARTNER_PAGE_ROWS, webQuoteNum, audCode, FCT2PAMigrtnFlag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.PartnerFactory#findResellerByAttr(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, int, int)
     */
    public SearchResultList findResellersByAttr(String lobCode, String custCnt, String custName, String country,
            String state, int tierType, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws TopazException {
        return findPartnersByCustAttr(lobCode, custCnt, custName, country, state,
                PartnerDBConstants.PARTNER_TYPE_RESELLER, tierType, pageIndex, PartnerDBConstants.PARTNER_PAGE_ROWS,
                webQuoteNum, FCT2PAMigrtnFlag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.PartnerFactory#findDistributorsByNum(java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    public SearchResultList findDistributorsByNum(String lobCode, String custCnt, String custNum, int pageIndex,
            String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws TopazException {
        return findPartnersByCustNum(lobCode, custCnt, custNum, PartnerDBConstants.PARTNER_TYPE_DISTRIBUTOR,
                PartnerDBConstants.PARTNER_TYPE_TIER1, pageIndex, PartnerDBConstants.PARTNER_PAGE_ROWS, webQuoteNum, audCode, FCT2PAMigrtnFlag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.PartnerFactory#findDistributorsByAttr(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, int)
     */
    public SearchResultList findDistributorsByAttr(String lobCode, String custCnt, String custName, String country,
            String state, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws TopazException {
        return findPartnersByCustAttr(lobCode, custCnt, custName, country, state,
                PartnerDBConstants.PARTNER_TYPE_DISTRIBUTOR, PartnerDBConstants.PARTNER_TYPE_TIER1, pageIndex,
                PartnerDBConstants.PARTNER_PAGE_ROWS, webQuoteNum, FCT2PAMigrtnFlag);
    }

    protected SearchResultList findPartnersByCustNum(String lobCode, String custCnt, String custNum, int partnerType,
            int tierType, int pageIndex, int offset, String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws TopazException {
        //convert page index to start index
        int startIndex = (pageIndex - 1) * offset;

        HashMap parms = new HashMap();
        parms.put("piLineOfBusCode", lobCode);
        parms.put("piCustCntry", custCnt);
        parms.put("piCustNum", custNum.trim());
        parms.put("piPrtnrType", "" + partnerType);
        parms.put("piTierType", "" + tierType);
        parms.put("piStartIndex", "" + startIndex);
        parms.put("piOffset", "" + offset);
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piAudCode", audCode);
        parms.put("piAudCode", audCode);
        parms.put("piFCT2PAMigrtnFlag", FCT2PAMigrtnFlag);
        
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            ProductPortfolioFactory portfolioFactory = ProductPortfolioFactory.singleton();
            Map mapPortfolio = portfolioFactory.getProductPorfolioMap();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(PartnerDBConstants.S_QT_PRTNR_BY_ID, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, PartnerDBConstants.S_QT_PRTNR_BY_ID, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int resultCode = ps.getInt(1);

            if (resultCode == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            
            SearchResultList resultList = new SearchResultList(ps.getInt(10), offset, pageIndex);
            
            rs = ps.getResultSet();
            while (rs.next()) {
                String custNumber = rs.getString("CUST_NUM");
                String custName = rs.getString("NAME");
                String custName2 = rs.getString("CUST_NAME_2");
                String address1 = rs.getString("ADR_1");
                String internalAddress = rs.getString("ADR_INTRNL");
                String countryDscr = rs.getString("CNTRY_DSCR");
                String city = rs.getString("CITY");
                String partnerState = rs.getString("SAP_RGN_CODE_DSCR");
                String postalCode = rs.getString("POSTAL_CODE");
                int tier = rs.getInt("TIER");
                String ibmCustNum = rs.getString("IBM_CUST_NUM");
                String rdcNum = rs.getString("RDC_CUST_NUM");
                String portfolios = rs.getString("PORTFOLIOLIST");
                Partner_jdbc partner_jdbc = new Partner_jdbc(
                        partnerType == PartnerDBConstants.PARTNER_TYPE_RESELLER);
                partner_jdbc.custNum = custNumber;
                partner_jdbc.custNameFull = custName;
                partner_jdbc.custName2 = custName2;
                partner_jdbc.address1 = address1;
                partner_jdbc.address2 = internalAddress;
                partner_jdbc.country = countryDscr;
                partner_jdbc.city = city;
                partner_jdbc.state = partnerState;
                partner_jdbc.postalCode = postalCode;                
                partner_jdbc.PATierType = tier == 1? 1 : 2;		// tier: 0 for PA tier two, 1 for PA tier one, 2 for SW VN tier one
                partner_jdbc.SWVNTierType = tier == 2? 1 : 2;	// tier: 0 for PA tier two, 1 for PA tier one, 2 for SW VN tier one
                partner_jdbc.ibmCustNum = ibmCustNum;
                partner_jdbc.rdcNumList = partner_jdbc.parseRDCNumList(rdcNum);
                partner_jdbc.authorizedPortfolioList = partner_jdbc.parsePortfolioList(mapPortfolio, portfolios);
                
                resultList.add(partner_jdbc);
            }
            rs.close();
            
            if (resultList.getResultCount() == 0 || resultList.getRealSize() == 0) {
                throw new NoDataException();
            }
            
            return resultList;
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(PartnerDBConstants.S_QT_PRTNR_BY_ID, parms));
            throw new TopazException("Exception when execute the SP " + PartnerDBConstants.S_QT_PRTNR_BY_ID, e);
        } finally{
        	this.close(ps, rs);
        }
    }

    protected SearchResultList findPartnersByCustAttr(String lobCode, String custCntCode, String name, String cntCode,
            String state, int partnerType, int tierType, int pageIndex, int offset, String webQuoteNum, int FCT2PAMigrtnFlag) throws TopazException {
        //convert page index to start index
        int startIndex = (pageIndex - 1) * offset;

        HashMap parms = new HashMap();
        parms.put("piPrtnrName", name.trim());
        parms.put("piLineOfBusCode", lobCode);
        parms.put("piCustCntry", custCntCode);
        parms.put("piPrtnrCntryCode", cntCode);
        parms.put("piState", state);
        parms.put("piPrtnrType", "" + partnerType);
        parms.put("piTierType", "" + tierType);
        parms.put("piStartIndex", "" + startIndex);
        parms.put("piOffset", "" + offset);
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piFCT2PAMigrtnFlag", FCT2PAMigrtnFlag);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            ProductPortfolioFactory portfolioFactory = ProductPortfolioFactory.singleton();
            Map mapPortfolio = portfolioFactory.getProductPorfolioMap();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(PartnerDBConstants.S_QT_PRTNR_BY_ATT, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, PartnerDBConstants.S_QT_PRTNR_BY_ATT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int resultCode = ps.getInt(1);

            if (resultCode == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            
            SearchResultList resultList = new SearchResultList(ps.getInt(12), offset, pageIndex);
            
            rs = ps.getResultSet();
            while (rs.next()) {
                String custNumber = rs.getString("CUST_NUM");
                String custName = rs.getString("CUST_NAME");
                String custName2 = rs.getString("CUST_NAME_2");
                String address1 = rs.getString("ADR_1");
                String internalAddress = rs.getString("ADR_INTRNL");
                String countryDscr = rs.getString("CNTRY_DSCR");
                String city = rs.getString("CITY");
                String partnerState = rs.getString("SAP_RGN_CODE_DSCR");
                String postalCode = rs.getString("POSTAL_CODE");
                int tier = rs.getInt("TIER");
                String ibmCustNum = rs.getString("IBM_CUST_NUM");
                String rdcNum = rs.getString("RDC_CUST_NUM");
                String portfolios = rs.getString("PORTFOLIOLIST");
                Partner_jdbc partner_jdbc = new Partner_jdbc(
                        partnerType == PartnerDBConstants.PARTNER_TYPE_RESELLER);
                partner_jdbc.custNum = custNumber;
                partner_jdbc.custNameFull = custName;
                partner_jdbc.custName2 = custName2;
                partner_jdbc.address1 = address1;
                partner_jdbc.address2 = internalAddress;
                partner_jdbc.country = countryDscr;
                partner_jdbc.city = city;
                partner_jdbc.state = partnerState;
                partner_jdbc.postalCode = postalCode;
                partner_jdbc.PATierType = tier == 1? 1 : 2;		// tier: 0 for PA tier two, 1 for PA tier one, 2 for SW VN tier one
                partner_jdbc.SWVNTierType = tier == 2? 1 : 2;	// tier: 0 for PA tier two, 1 for PA tier one, 2 for SW VN tier one
                partner_jdbc.ibmCustNum = ibmCustNum;
                partner_jdbc.rdcNumList = partner_jdbc.parseRDCNumList(rdcNum);
                partner_jdbc.authorizedPortfolioList = partner_jdbc.parsePortfolioList(mapPortfolio, portfolios);
                
                resultList.add(partner_jdbc);
            }
            rs.close();
            
            if (resultList.getResultCount() == 0 || resultList.getRealSize() == 0) {
                throw new NoDataException();
            }
            
            return resultList;
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(PartnerDBConstants.S_QT_PRTNR_BY_ATT, parms));
            throw new TopazException("Exception when execute the SP " + PartnerDBConstants.S_QT_PRTNR_BY_ATT, e);
        } finally{
        	this.close(ps, rs);
        }
    }
    
    public SearchResultList findResellersByPortfolio(String portfolios, String lob, String custCntry, int pageIndex, int multipleProdFlag)
            throws TopazException {
        
        // convert page index to start index
        int offset = PartnerDBConstants.PARTNER_PAGE_ROWS;
        int startIndex = (pageIndex - 1) * offset;

        HashMap parms = new HashMap();
        parms.put("piPortfolioList", portfolios);
        parms.put("piLineOfBusCode", lob);
        parms.put("piCustCntry", custCntry);
        parms.put("piStartIndex", new Integer(startIndex));
        parms.put("piOffset", new Integer(offset));
        parms.put("piMultipleProdFlag", new Integer(multipleProdFlag));
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            ProductPortfolioFactory portfolioFactory = ProductPortfolioFactory.singleton();
            Map mapPortfolio = portfolioFactory.getProductPorfolioMap();
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(PartnerDBConstants.S_QT_PRTNR_BY_PORT, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, PartnerDBConstants.S_QT_PRTNR_BY_PORT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            ps.execute();
            int resultCode = ps.getInt(1);

            if (resultCode == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            
            SearchResultList resultList = new SearchResultList(ps.getInt(8), offset, pageIndex);
            
            rs = ps.getResultSet();
            while (rs.next()) {
                String custNumber = rs.getString("CUST_NUM");
                String custName = rs.getString("NAME");
                String custName2 = rs.getString("CUST_NAME_2");
                String address1 = rs.getString("ADR_1");
                String internalAddress = rs.getString("ADR_INTRNL");
                String countryDscr = rs.getString("CNTRY_DSCR");
                String city = rs.getString("CITY");
                String partnerState = rs.getString("SAP_RGN_CODE_DSCR");
                String postalCode = rs.getString("POSTAL_CODE");
                int tier = rs.getInt("TIER");
                String ibmCustNum = rs.getString("IBM_CUST_NUM");
                String rdcNum = rs.getString("RDC_CUST_NUM");
                String outPortfolios = rs.getString("PORTFOLIOLIST");
                Partner_jdbc partner_jdbc = new Partner_jdbc(true);
                partner_jdbc.custNum = custNumber;
                partner_jdbc.custNameFull = custName;
                partner_jdbc.custName2 = custName2;
                partner_jdbc.address1 = address1;
                partner_jdbc.address2 = internalAddress;
                partner_jdbc.country = countryDscr;
                partner_jdbc.city = city;
                partner_jdbc.state = partnerState;
                partner_jdbc.postalCode = postalCode;
                partner_jdbc.PATierType = tier == 1? 1 : 2;		// tier: 0 for PA tier two, 1 for PA tier one, 2 for SW VN tier one
                partner_jdbc.SWVNTierType = tier == 2? 1 : 2;	// tier: 0 for PA tier two, 1 for PA tier one, 2 for SW VN tier one
                partner_jdbc.ibmCustNum = ibmCustNum;
                partner_jdbc.rdcNumList = partner_jdbc.parseRDCNumList(rdcNum);
                partner_jdbc.authorizedPortfolioList = partner_jdbc.parsePortfolioList(mapPortfolio, outPortfolios);
                
                resultList.add(partner_jdbc);
            }
            rs.close();
            
            if (resultList.getResultCount() == 0 || resultList.getRealSize() == 0) {
                throw new NoDataException();
            }
            
            return resultList;
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(PartnerDBConstants.S_QT_PRTNR_BY_PORT, parms));
            throw new TopazException("Exception when execute the SP " + PartnerDBConstants.S_QT_PRTNR_BY_PORT, e);
        } finally{
        	this.close(ps, rs);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.PartnerFactory#findPartnerByNum(java.lang.String)
     */
    public Partner findPartnerByNum(String partnerNum, String lob) throws TopazException {
        Partner partner = (Partner) this.getFromCache(partnerNum);
        if (partner == null) {
            Partner_jdbc partner_jdbc = new Partner_jdbc();
            partner_jdbc.custNum = partnerNum;
            partner_jdbc.lineOfBusiness = lob;
            partner_jdbc.hydrate(TopazUtil.getConnection());
            partner = partner_jdbc;
            this.putInCache(partnerNum, partner);
        }
        return partner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(PartnerFactory.class, objectId, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        // get instance from cache
        return TransactionContextManager.singleton().getTransactionContext().get(PartnerFactory.class, objectId);
    }
    
    /**
     * close JDBC resource
     * @param cstmt
     * @param rs
     */
    private void close(CallableStatement cstmt, ResultSet rs) {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				logContext.error(TopazUtil.class, LogThrowableUtil.getStackTraceContent(e));
			}
		}

		if (null != cstmt) {
			try {
				cstmt.close();
			} catch (SQLException e) {
				logContext.error(TopazUtil.class, LogThrowableUtil.getStackTraceContent(e));
			}
		}
	}

    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	@Override
	public boolean checkPartnerCEID(String custNum) throws TopazException {
		
		boolean isEnable = false;
		TimeTracer tracer = TimeTracer.newInstance();
        String spName = CommonDBConstants.DB2_S_QT_GET_PARTNER_CHECK_CEID;
        HashMap parms = new HashMap();
        parms.put("piCustNum", custNum);
        ResultSet rs = null;
        try {
            logger.debug(this, "customer number is " + custNum);
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            tracer.stmtTraceStart("call S_QT_GET_PARTNER_CHECK_CEID");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call S_QT_GET_PARTNER_CHECK_CEID");

            int retStatus = ps.getInt(1);
            int cEIDFlag = ps.getInt(3);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            if(cEIDFlag == 1){
            	isEnable = true;
            }
        } catch (Exception e) {
             logger.error("Failed to check partner CEID.",e);
            throw new TopazException(e);
        }

        return isEnable;
	}
}
