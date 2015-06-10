package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerFactory;
import com.ibm.dsw.quote.common.domain.CustomerGroup;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.common.domain.DefaultCustAddress;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.customer.contract.CustomerCreateContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
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
 * This <code>CustomerFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class CustomerFactory_jdbc extends CustomerFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public CustomerFactory_jdbc() {
        super();
    }


    /**
     * Call SP to get customer List by customer's attribute
     */
    public CustomerSearchResultList findByAttribute(String customerName, String cntryCode, String contractOption,
            String anniversary, String lineOfBus, int findActiveFlag, int startPos, String stateCode,
            int searchType, String audCode, String userSiteNum) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCustName", customerName == null ? "" : customerName.trim());
        parms.put("piCntryCode", cntryCode == null ? "" : cntryCode);
        parms.put("piSapCtrctVarCode", StringUtils.isBlank(contractOption) ? "%" : contractOption.trim());
        parms.put("piCtrctMonth", new Integer((anniversary == null || StringUtils.isBlank(anniversary))? "0" : anniversary));
        parms.put("piLineOfBus", lineOfBus == null ? "" : lineOfBus);
        parms.put("piChkLOB", new Integer(findActiveFlag));
        parms.put("piStartPos", new Integer(startPos));
        parms.put("piRecordCount", new Integer(CustomerConstants.PAGE_ROW_COUNT));
        parms.put("piStateCode", stateCode == null ? "" : stateCode);
        parms.put("piSearchType", new Integer(searchType));
        parms.put("piAudCode", audCode == null ? "" : audCode);
        parms.put("piUserSiteNum", userSiteNum == null ? "" : userSiteNum);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            String sprocName = CommonDBConstants.DB2_S_QT_CUST_BY_ATTR;
            CustomerSearchResultList resultList = new CustomerSearchResultList();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            if (!retCode)
                return null;
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                rs = ps.getResultSet();
                Contract contract = null;
                Customer_jdbc customer_jdbc = null;
                String rsLineOfBus = lineOfBus;
                HashMap mapCtrct = new HashMap();
                HashMap mapRDC = new HashMap();

                while (rs.next()) {
                    String sKey = StringUtils.trimToEmpty(rs.getString("CUST_NUM"));
                    customer_jdbc = (Customer_jdbc) this.getFromCache(sKey);

                    if (customer_jdbc == null) {
                        customer_jdbc = new Customer_jdbc();
                        customer_jdbc.address1 = rs.getString("ADR_1");
                        customer_jdbc.internalAddress = rs.getString("ADR_INTRNL");
                        customer_jdbc.city = rs.getString("CITY");
                        customer_jdbc.countryCode = rs.getString("CNTRY_CODE");
                        customer_jdbc.custName = rs.getString("CUST_NAME");
                        customer_jdbc.custNum = rs.getString("CUST_NUM");
                        customer_jdbc.ibmCustNum = rs.getString("IBM_CUST_NUM");
                        customer_jdbc.postalCode = rs.getString("POSTAL_CODE");
                        customer_jdbc.sapRegionCodeDscr = rs.getString("SAP_RGN_CODE_DSCR");
                        customer_jdbc.currencyCode = rs.getString("CURRNCY_CODE");
                        customer_jdbc.gsaStatusFlag = (rs.getInt("GSA_FLAG") == 1);
                        customer_jdbc.setRelatedwithBPFlag(true);// according to requirement, all customers searched out by attr must have relationship with current BP.
                        resultList.add(customer_jdbc);
                        this.putInCache(sKey, customer_jdbc);
                    }

                    if (StringUtils.isNotBlank(rs.getString("RDC_NUM"))) {
                        String rdcKey = StringUtils.trimToEmpty(rs.getString("CUST_NUM")) + "_"
                                + StringUtils.trimToEmpty(rs.getString("RDC_NUM"));

                        if (!mapRDC.containsKey(rdcKey)) {
                            customer_jdbc.addRdcNumber(rs.getString("RDC_NUM"));
                            mapRDC.put(rdcKey, rdcKey);
                        }
                    }

                    if (StringUtils.isNotBlank(rs.getString("SAP_CTRCT_NUM"))) {
                        String ctrctKey = StringUtils.trimToEmpty(rs.getString("CUST_NUM")) + "_"
                                + StringUtils.trimToEmpty(rs.getString("SAP_CTRCT_NUM"));

                        if (!mapCtrct.containsKey(ctrctKey)) {
                            contract = new Contract();
                            contract.setSapContractNum(rs.getString("SAP_CTRCT_NUM"));
                            contract.setSapContractVariantCode(rs.getString("SAP_CTRCT_VARIANT_CODE"));
                            contract.setVolDiscLevelCode(rs.getString("VOL_DISC_LEVEL_CODE"));
                            customer_jdbc.addContract(contract);

                            if (QuoteConstants.LOB_PAUN.equalsIgnoreCase(rsLineOfBus))
                                rsLineOfBus = QuoteConstants.LOB_PA;

                            mapCtrct.put(ctrctKey, contract);
                        }
                    }
                }
                int resultCount = ps.getInt(11);
                resultList.setResultCount(resultCount);
                resultList.setLob(rsLineOfBus);
                rs.close();
                return resultList;
            } else
                return null;
        } catch (Exception e) {
        	logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_CUST_BY_ATTR, parms));
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        }
    }

    /**
     * Call SP to get the duplicate customer list
     */
    public SearchResultList findDuplCustomers(String customerName, String address, String piCity, String regionCode,
            String piPostalCode, String country) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        HashMap parms = new HashMap();
        SearchResultList resultList = new SearchResultList();

        parms.put("piCustname", customerName == null ? "" : customerName);
        parms.put("piAdr1", address == null ? "" : address);
        parms.put("piCity", piCity == null ? "" : piCity);
        parms.put("piSAPRegionCode", regionCode == null ? "" : regionCode);
        parms.put("piPostalCode", piPostalCode == null ? "" : piPostalCode);
        parms.put("piCntryCode", country == null ? "" : country);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_DUPL_CUSTOMR, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_DUPL_CUSTOMR, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {

                if (!retCode)
                    return resultList;

                rs = ps.getResultSet();
                Contract contract = null;
                Customer_jdbc customer_jdbc = null;

                while (rs.next()) {
                    String sKey = rs.getString("CUST_NUM");
                    customer_jdbc = (Customer_jdbc) this.getFromCache(sKey);

                    if (customer_jdbc == null) {
                        customer_jdbc = new Customer_jdbc();
                        customer_jdbc.address1 = rs.getString("ADR_1");
                        customer_jdbc.internalAddress = rs.getString("ADR_INTRNL");
                        customer_jdbc.city = rs.getString("CITY");
                        customer_jdbc.countryCode = rs.getString("CNTRY_CODE");
                        customer_jdbc.custName = rs.getString("CUST_NAME");
                        customer_jdbc.custNum = rs.getString("CUST_NUM");
                        customer_jdbc.ibmCustNum = rs.getString("IBM_CUST_NUM");
                        customer_jdbc.postalCode = rs.getString("POSTAL_CODE");
                        customer_jdbc.sapRegionCodeDscr = rs.getString("SAP_RGN_CODE_DSCR");

                        resultList.add(customer_jdbc);
                        this.putInCache(customer_jdbc.getCustNum(), customer_jdbc);
                    }

                    if (StringUtils.isNotBlank(rs.getString("SAP_CTRCT_NUM"))) {
                        contract = new Contract();
                        contract.setSapContractNum(rs.getString("SAP_CTRCT_NUM"));
                        contract.setSapContractVariantCode(rs.getString("SAP_CTRCT_VARIANT_CODE"));
                        contract.setVolDiscLevelCode(rs.getString("VOL_DISC_LEVEL_CODE"));
                        customer_jdbc.addContract(contract);
                    }
                }
                rs.close();
            }
        } catch (SQLException e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        }

        return resultList;
    }

    /**
     * Get a customer by customer number.
     */
    public Customer findCustomerByNum(String customerNum) throws TopazException {
        Customer customer = (Customer) this.getFromCache(customerNum);
        if (customer == null) {
            Customer_jdbc customer_jdbc = new Customer_jdbc(customerNum);
            customer_jdbc.hydrate(TopazUtil.getConnection());
            customer = customer_jdbc;
            this.putInCache(customerNum, customer);
        }
        return customer;
    }
    
    
	@Override
	public Customer createCustomer() throws Exception {
		return new Customer_jdbc();
	}

	/**
     * Create a customer.
     */
   /* public Customer createCustomer(String customerNum , String userID) throws TopazException {
        //Customer customer = (Customer) this.getFromCache(customerNum);
      if (customer == null) {
            Customer_jdbc customer_jdbc = new Customer_jdbc(customerNum);
            //customer_jdbc.isNew(true); //persist to DB2
            insertOrUpdate(customer_jdbc, userID);
            customer = customer_jdbc;
            this.putInCache(customerNum, customer);
        } 
        
        
        return customer;
    } */
    
    public Customer createCustomer(CustomerCreateContract custCreateContract , String userID) throws TopazException {
        
        Customer_jdbc customer_jdbc  = new Customer_jdbc(custCreateContract.getCustomerNumber());
        customer_jdbc.setWebCustId(Integer.parseInt(custCreateContract.getWebCustId().trim()));
        customer_jdbc.setCustNum(custCreateContract.getCustomerNumber());
        customer_jdbc.setSapContractNum(custCreateContract.getAgreementNumber());
        customer_jdbc.setTempAccessNum(custCreateContract.getTempAccessNum());
        customer_jdbc.setSapCntId(Integer.parseInt(custCreateContract.getSapContactId().trim()));  
        customer_jdbc.setWebCustTypeCode(custCreateContract.getWebCustTypeCode());
        customer_jdbc.setCustName(custCreateContract.getCompanyName());
        customer_jdbc.setAddress1(custCreateContract.getAddress1());
        customer_jdbc.setInternalAddress(custCreateContract.getAddress2());
        customer_jdbc.setCity(custCreateContract.getCity());
        customer_jdbc.setSapRegionCode(custCreateContract.getState());
        customer_jdbc.setPostalCode(custCreateContract.getPostalCode());
        
        if (QuoteConstants.LOB_SSP.equalsIgnoreCase(custCreateContract.getLob()) && StringUtils.equals("1", custCreateContract.getEndUserFlag()) && !StringUtils.isBlank(custCreateContract.getCusCountry())) {
        	customer_jdbc.setCountryCode(custCreateContract.getCusCountry());
        }else{
        	customer_jdbc.setCountryCode(custCreateContract.getCountry());
        }
        
        customer_jdbc.setCurrencyCode(custCreateContract.getCurrency());
        customer_jdbc.setSapIntlPhoneNumFull(custCreateContract.getSapIntlPhoneNum());
        customer_jdbc.setCustVatNum(custCreateContract.getVatNum());
        customer_jdbc.setSapWwIsuCode(custCreateContract.getIndustryIndicator());
        customer_jdbc.setEmpTot(Integer.parseInt(custCreateContract.getCompanySize().trim()));
        customer_jdbc.setIbmCustNum("");
        customer_jdbc.setIsoLangCode(custCreateContract.getCommLanguage());
        customer_jdbc.setUpgrLangCode(custCreateContract.getMediaLanguage());
        customer_jdbc.setCntFirstName(custCreateContract.getCntFirstName());
        customer_jdbc.setCntLastName(custCreateContract.getCntLastName());
        customer_jdbc.setCntPhoneNumFull(custCreateContract.getCntPhoneNumFull());
        customer_jdbc.setCntFaxNumFull(custCreateContract.getCntFaxNumFull());
        customer_jdbc.setCntEmailAdr(custCreateContract.getCntEmailAdr());
        customer_jdbc.setMktgEmailFlag(custCreateContract.getMktgEmailFlag()); 
        customer_jdbc.setWebCustStatCode(custCreateContract.getWebCustStatCode());
        customer_jdbc.setSapCntPrtnrFuncCode(custCreateContract.getSapCntPrtnrFuncCode());
        customer_jdbc.setAuthrztnGroup(custCreateContract.getAuthrztnGroup());
        customer_jdbc.setAgreementType(custCreateContract.getAgreementType());
        customer_jdbc.setTransSVPLevel(custCreateContract.getTransSVPLevel());
        if( !StringUtils.isBlank(custCreateContract.getCustomerType())){
        	customer_jdbc.setCustomerType(custCreateContract.getCustomerType());
        } else{
        	customer_jdbc.setCustomerType("0");
        }
        insertOrUpdate(customer_jdbc, userID);
        Customer customer = customer_jdbc;

        return customer;
    }
    
    private void insertOrUpdate(Customer_jdbc customer_jdbc, String userID) throws TopazException{
        HashMap params = new HashMap();
        String sapCtrctNum = customer_jdbc.getSapContractNum();
        if (StringUtils.isNotBlank(sapCtrctNum))
            sapCtrctNum = StringHelper.fillString(sapCtrctNum);
        
        params.put("pioWebCustId", new Integer(customer_jdbc.getWebCustId()));
        params.put("piCustNum", customer_jdbc.getCustNum());
        params.put("piSapCtrctNum", sapCtrctNum);
        params.put("piTempAccessNum", customer_jdbc.getTempAccessNum());
        params.put("piSapCntId", new Integer(customer_jdbc.getSapCntId()));
        params.put("piWebCustTypeCode", customer_jdbc.getWebCustTypeCode());
        params.put("piCustName", customer_jdbc.getCustName());
        params.put("piAdr1", customer_jdbc.getAddress1());
        params.put("piAdrIntrnl", customer_jdbc.getInternalAddress());
        params.put("piCity", customer_jdbc.getCity());
        params.put("piSapRegionCode", customer_jdbc.getSapRegionCode());
        params.put("piPostalCode", customer_jdbc.getPostalCode());
        params.put("piCntryCode", customer_jdbc.getCountryCode());
        params.put("piCurrncyCode", customer_jdbc.getCurrencyCode());
        params.put("piSapIntlPhoneNumFull", customer_jdbc.getSapIntlPhoneNumFull());
        params.put("piCustVatNum", customer_jdbc.getCustVatNum());
        params.put("piSapWwIsuCode", customer_jdbc.getSapWwIsuCode());
        params.put("piEmpTot", new Integer(customer_jdbc.getEmpTot()));
        params.put("piIbmCustNum", customer_jdbc.getIbmCustNum());
        params.put("piIsoLangCode", customer_jdbc.getIsoLangCode());
        params.put("piUpgrLangCode", customer_jdbc.getUpgrLangCode());
        params.put("piCntFirstName", customer_jdbc.getCntFirstName());
        params.put("piCntLastName", customer_jdbc.getCntLastName());
        params.put("piCntPhoneNumFull", customer_jdbc.getCntPhoneNumFull());
        params.put("piCntFaxNumFull", customer_jdbc.getCntFaxNumFull());
        params.put("piCntEmailAdr", customer_jdbc.getCntEmailAdr());
        params.put("piMktgEmailFlag", customer_jdbc.getMktgEmailFlag());
        params.put("piWebCustStatCode", customer_jdbc.getWebCustStatCode());
        params.put("piSapCntPrtnrFuncCode", customer_jdbc.getSapCntPrtnrFuncCode());
        params.put("piSapCtrctVariantCode", customer_jdbc.getAgreementType());
        params.put("piAuthrztnGroup", customer_jdbc.getAuthrztnGroup());
        params.put("piVolDiscLevelCode", customer_jdbc.getTransSVPLevel());
        params.put("piUserID", userID );
        params.put("piCustomerType", customer_jdbc.getCustomerType());
        
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        CallableStatement ps = null;
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_WEB_CUSTMER, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_WEB_CUSTMER, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            ps.execute();
            retCode = ps.getInt(1);
            
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            
            customer_jdbc.setWebCustId(ps.getInt(2));
        } catch (SQLException e) {
            logger.error("Failed to insert the customer to the database!", e);
            throw new TopazException(e);
        } finally{
        	this.close(ps, null);
        }
    }  


    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.CustomerFactory#findByQuoteNum(java.lang.String,
     *      int, java.lang.String, java.lang.String, java.lang.String)
     */
    public Customer findByQuoteNum(String lob, int newCustFlag, String custNum, String sapCtrctNum, String webQuoteNum,
            int webCustId) throws TopazException {

        Customer_jdbc customer_jdbc = null;
        HashMap parms = new HashMap();
        parms.put("piLineOfBusiness", lob == null ? "" : lob);
        parms.put("piNewCustFlag", new Integer(newCustFlag));
        parms.put("piCustNum", custNum == null ? "" : StringHelper.fillString(custNum));
        parms.put("piSapCtrctNum", sapCtrctNum == null ? "" : sapCtrctNum);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        parms.put("piWebCustId", webCustId);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_CUSTINFO, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_CUSTINFO, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            Contract contract = new Contract();
            customer_jdbc = new Customer_jdbc(custNum);

            customer_jdbc.custNum = custNum;
            customer_jdbc.sapContractNum = sapCtrctNum;
            customer_jdbc.webCustId = webCustId;

            customer_jdbc.custName = StringUtils.trimToEmpty(ps.getString(8));
            customer_jdbc.city = StringUtils.trimToEmpty(ps.getString(9));
            customer_jdbc.sapRegionCode = StringUtils.trimToEmpty(ps.getString(10));
            customer_jdbc.postalCode = StringUtils.trimToEmpty(ps.getString(11));
            customer_jdbc.ibmCustNum = StringUtils.trimToEmpty(ps.getString(13));
            customer_jdbc.cntFirstName = StringUtils.trimToEmpty(ps.getString(17));
            customer_jdbc.cntLastName = StringUtils.trimToEmpty(ps.getString(18));
            customer_jdbc.cntEmailAdr = StringUtils.trimToEmpty(ps.getString(19));
            customer_jdbc.cntPhoneNumFull = StringUtils.trimToEmpty(ps.getString(20));
            customer_jdbc.cntFaxNumFull = StringUtils.trimToEmpty(ps.getString(21));
            customer_jdbc.gsaStatusFlag = (ps.getInt(22) == 1) ? true : false;
            customer_jdbc.ctrctHasPreApprPrcLvlFlg = ps.getInt(25);
            customer_jdbc.purchOrdReqrdFlag = StringUtils.trimToEmpty(ps.getString(26));
            customer_jdbc.countryCode = StringUtils.trimToEmpty(ps.getString(28));
            customer_jdbc.custName2 = StringUtils.trimToEmpty(ps.getString(29));
            customer_jdbc.custName3 = StringUtils.trimToEmpty(ps.getString(30));
            customer_jdbc.address1 = StringUtils.trimToEmpty(ps.getString(31));
            customer_jdbc.salesOrg = StringUtils.trimToEmpty(ps.getString(32));
            customer_jdbc.internalAddress = StringUtils.trimToEmpty(ps.getString(33));
            customer_jdbc.currencyCode = StringUtils.trimToEmpty(ps.getString(34));
            customer_jdbc.custVatNum = StringUtils.trimToEmpty(ps.getString(35));
            customer_jdbc.sapWwIsuCode = StringUtils.trimToEmpty(ps.getString(36));
            customer_jdbc.empTot = ps.getInt(37);
            customer_jdbc.isoLangCode = StringUtils.trimToEmpty(ps.getString(38));
            customer_jdbc.upgrLangCode = StringUtils.trimToEmpty(ps.getString(39));
            customer_jdbc.mktgEmailFlag = StringUtils.trimToEmpty(ps.getString(40));
            customer_jdbc.sapCntId = ps.getInt(41);
            customer_jdbc.rdcNumList = StringHelper.parseStr2List(ps.getString(42));
            customer_jdbc.webCustTypeCode = StringUtils.trimToEmpty(ps.getString(43));
            customer_jdbc.custDesignation = StringUtils.trimToEmpty(ps.getString(44));
            customer_jdbc.agreementType = StringUtils.trimToEmpty(ps.getString(45));
            customer_jdbc.authrztnGroup = StringUtils.trimToEmpty(ps.getString(46));
            customer_jdbc.transSVPLevel = StringUtils.trimToEmpty(ps.getString(47));
            customer_jdbc.PAIndCode = ps.getInt(48);
            customer_jdbc.govEntityIndCode = ps.getInt(49);
            customer_jdbc.govEntityIndCodeDesc = StringUtils.trimToEmpty(ps.getString(50));
            customer_jdbc.supprsPARegstrnEmailFlag = ps.getInt(51);
            customer_jdbc.custSetDesCode = ps.getString(52);
            customer_jdbc.custSetDesCode2 = ps.getString(53);
            customer_jdbc.tempAccessNum = StringUtils.trimToEmpty(ps.getString(55));
            customer_jdbc.setCustOrdBlcked(CustomerConstants.CUST_ORD_BLCKED.equalsIgnoreCase(ps.getString(56)));
			customer_jdbc.isMthlySwTermsAccptd = (ps.getInt(57) == 1) ? true : false;
            
            if (StringUtils.isNotBlank(sapCtrctNum)) {
                contract.setSapContractNum(StringUtils.trimToEmpty(sapCtrctNum));
                contract.setSapContractVariantCode(StringUtils.trimToEmpty(ps.getString(14)));
                contract.setAnniversaryDate(ps.getDate(15));
                contract.setVolDiscLevelCode(StringUtils.trimToEmpty(ps.getString(16)));
                contract.setCtrctStartDate(ps.getDate(23));
                contract.setCtrctEndDate(ps.getDate(24));
                contract.setIsContractActiveFlag(ps.getInt(27));
                contract.setNimOffergCode(StringUtils.trimToEmpty(ps.getString(54)));

                customer_jdbc.addContract(contract);
            }

            if (retCode) {
                rs = ps.getResultSet();
                StringBuffer sb = new StringBuffer();
                while (rs.next()) {
                    String dscr = StringUtils.trimToEmpty(rs.getString("CVRAGE_GRP_CODE_DSCR"));
                    if (StringUtils.isNotBlank(dscr)) {
                        sb.append(dscr).append(", ");
                    }
                }
                rs.close();
                sb.reverse().delete(0,2).reverse();
                customer_jdbc.setCvrageGrpCodeDesc(sb.toString());
                
                if(ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                    	customer_jdbc.setCustCvrageDesc(StringUtils.trimToEmpty(rs.getString("CUST_CVRAGE_DSCR")));
                    	customer_jdbc.setInitWebAppBusDscr(StringUtils.trimToEmpty(rs.getString("INI_WEB_APPL_BUS_DSCR")));
                    	customer_jdbc.setCurrWebAppBusDscr(StringUtils.trimToEmpty(rs.getString("CURR_WEB_APPL_BUS_DSCR")));
                    }
                    rs.close();
                }

                if(ps.getMoreResults()) {
                    List customerGroupList = new LinkedList();
                    customer_jdbc.setCustomerGroupList(customerGroupList);
                    CustomerGroup custGrp = null;
                    rs = ps.getResultSet();
                    while (rs.next()) {
                    	custGrp = new CustomerGroup();
                    	custGrp.setCustomerGroupName(StringUtils.trimToEmpty(rs.getString("SPECL_BID_CUST_GRP_NAME")));
                        customerGroupList.add(custGrp);
                    }
                    rs.close();
                }

                if(ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                    	customer_jdbc.setCustOnlySSP(true);
                    }
                    rs.close();
                }
                
                if(ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                    	customer_jdbc.isGOECust = rs.getInt("GOVT_OWNED_ENTITY_CODE") == 1? true : false;
                    }
                    rs.close();
                }
                
                if(ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        contract = new Contract();
                        contract.setSapContractNum(StringUtils.trimToEmpty(rs.getString("SAP_CTRCT_NUM")));
                        contract.setSapContractVariantCode(StringUtils.trimToEmpty(rs.getString("SAP_CTRCT_VARIANT_CODE")));
                        contract.setVolDiscLevelCode(StringUtils.trimToEmpty(rs.getString("VOL_DISC_LEVEL_CODE")));
                        contract.setTranPriceLevelCode(StringUtils.trimToEmpty(rs.getString("TRAN_PRICE_LEVEL_CODE")));
                        contract.setAnniversaryDate(rs.getDate("DISMANTLING_DATE"));
                        contract.setCtrctStartDate(rs.getDate("CTRCT_START_DATE"));
                        contract.setCtrctEndDate(rs.getDate("CTRCT_END_DATE"));
                        contract.setIsContractActiveFlag(rs.getInt("CTRCT_ACTIVE_FLAG"));

                        customer_jdbc.addEnrolledCtrct(contract);
                    }
                    rs.close();
                }
                

//              List customerGroupList = new LinkedList();
//              customer_jdbc.setCustomerGroupList(customerGroupList);
//              CustomerGroup custGrp = new CustomerGroup();
//          	  custGrp.setCustomerGroupName("test1 Group");
//              customerGroupList.add(custGrp);
//              custGrp = new CustomerGroup();
//          	  custGrp.setCustomerGroupName("test2 Group");
//              customerGroupList.add(custGrp);
//              custGrp = new CustomerGroup();
//          	  custGrp.setCustomerGroupName("test4 Group");
//              customerGroupList.add(custGrp);
//              custGrp = new CustomerGroup();
//          	  custGrp.setCustomerGroupName("test5 Group");
//              customerGroupList.add(custGrp);
              
                
            }
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_GET_CUSTINFO, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_CUSTINFO, e);
        } finally{
        	this.close(ps, rs);
        }

        return customer_jdbc;
    }

    /**
     * Call SP to get customer list by DSW Customer number or ICN(IBM Cust #) or
     * RCD
     */
    public CustomerSearchResultList findByDswIcnRcdID(String sapCtrctNum, String dswIcnRcdID, String cntryCode,
            String lineOfBus, int startPos, boolean searchPayer, String progMigrtnCode, String audCode, String userSiteNum) throws TopazException {

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        HashMap parms = new HashMap();
        parms.put("piSapCtrctNum", sapCtrctNum == null ? "" : sapCtrctNum);
        parms.put("piCustNum", dswIcnRcdID == null ? "" : dswIcnRcdID);
        parms.put("piCntryCode", cntryCode == null ? "" : cntryCode);
        parms.put("piLineOfBus", lineOfBus == null ? "" : lineOfBus);
        parms.put("piStartPos", new Integer(startPos));
        parms.put("piRecordCount", new Integer(CustomerConstants.PAGE_ROW_COUNT));
        if (!searchPayer) {
            parms.put("piProgMigrtnCode", progMigrtnCode == null ? "" : progMigrtnCode);
        }
        parms.put("piAudCode", audCode == null ? "" : audCode);
        parms.put("piUserSiteNum", userSiteNum == null ? "" : userSiteNum);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            String sprocName = searchPayer ? CommonDBConstants.DB2_S_QT_PAYER_BY_ID
                    : CommonDBConstants.DB2_S_QT_CUST_BY_ID;
            CustomerSearchResultList resultList = new CustomerSearchResultList();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            if (!retCode)
                return resultList;
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                rs = ps.getResultSet();
                Contract contract = null;
                Customer_jdbc customer_jdbc = null;
                String rsLineOfBus = lineOfBus;

                while (rs.next()) {
                    String sKey = StringUtils.trimToEmpty(rs.getString("CUST_NUM"));
                    customer_jdbc = (Customer_jdbc) this.getFromCache(sKey);

                    if (customer_jdbc == null) {
                        customer_jdbc = new Customer_jdbc();
                        customer_jdbc.address1 = rs.getString("ADR_1");
                        customer_jdbc.internalAddress = rs.getString("ADR_INTRNL");
                        customer_jdbc.city = rs.getString("CITY");
                        customer_jdbc.countryCode = rs.getString("CNTRY_CODE");
                        customer_jdbc.custName = rs.getString("CUST_NAME");
                        customer_jdbc.custNum = rs.getString("CUST_NUM");
                        customer_jdbc.ibmCustNum = rs.getString("IBM_CUST_NUM");
                        customer_jdbc.postalCode = rs.getString("POSTAL_CODE");
                        customer_jdbc.gsaStatusFlag = (rs.getInt("GSA_FLAG") == 1);
                        customer_jdbc.sapRegionCodeDscr = rs.getString("SAP_RGN_CODE_DSCR");
                        customer_jdbc.currencyCode = rs.getString("CURRNCY_CODE");
                        customer_jdbc.rdcNumList = StringHelper.parseStr2List(rs.getString("RDC_NUM"));
                        //customer_jdbc. = rs.getString("LINE_OF_BUS_CODE");
                        if(!searchPayer){
                        	String relatedwithBPFlagStr = rs.getString("RELATED_WITH_BP_FLAG");
                        	boolean relatedwithBPFlag = "1".equals(relatedwithBPFlagStr);
                        	customer_jdbc.setRelatedwithBPFlag(relatedwithBPFlag);
                        }
                        resultList.add(customer_jdbc);
                        this.putInCache(sKey, customer_jdbc);
                    }

                    if (StringUtils.isNotBlank(rs.getString("SAP_CTRCT_NUM"))) {
                        contract = new Contract();
                        contract.setSapContractNum(rs.getString("SAP_CTRCT_NUM"));
                        contract.setSapContractVariantCode(rs.getString("SAP_CTRCT_VARIANT_CODE"));
                        contract.setVolDiscLevelCode(rs.getString("VOL_DISC_LEVEL_CODE"));
                        customer_jdbc.addContract(contract);

                        if (QuoteConstants.LOB_PAUN.equalsIgnoreCase(rsLineOfBus))
                            rsLineOfBus = QuoteConstants.LOB_PA;
                    }
                }
                int resultCount = searchPayer ? ps.getInt(8) : ps.getInt(9);
                resultList.setResultCount(resultCount);
                resultList.setLob(rsLineOfBus);
                rs.close();
                return resultList;
            } else
                return resultList;
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#putInCache(java.lang.Object,
     *      java.lang.Object)
     */
    public void putInCache(Object objectId, Object object) throws TopazException {
        // register new instance in the cache
        TransactionContextManager.singleton().getTransactionContext().put(CustomerFactory.class, objectId, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.CacheableFactory#getFromCache(java.lang.Object)
     */
    public Object getFromCache(Object objectId) throws TopazException {
        // get instance from cache
        return TransactionContextManager.singleton().getTransactionContext().get(CustomerFactory.class, objectId);
    }

    /**
     * Call SP to get customer List by customer's attribute
     */
    public CustomerSearchResultList findEndCustByAttr(String customerName, String cntryCode, String lineOfBus,
            String stateCode, int startPos) throws TopazException {
        HashMap parms = new HashMap();
        parms.put("piCustName", customerName == null ? "" : customerName.trim());
        parms.put("piCntryCode", cntryCode == null ? "" : cntryCode);
        parms.put("piLineOfBus", lineOfBus == null ? "" : lineOfBus);
        parms.put("piStateCode", stateCode == null ? "" : stateCode);
        parms.put("piStartPos", new Integer(startPos));
        parms.put("piRecordCount", new Integer(CustomerConstants.PAGE_ROW_COUNT));
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            String sprocName = CommonDBConstants.DB2_S_QT_END_USER_BY_ATTR;
            CustomerSearchResultList resultList = new CustomerSearchResultList();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            if (!retCode)
                return null;
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                rs = ps.getResultSet();
                Customer_jdbc customer_jdbc = null;
                String rsLineOfBus = lineOfBus;

                while (rs.next()) {
                    String sKey = StringUtils.trimToEmpty(rs.getString("CUST_NUM"));
                    customer_jdbc = (Customer_jdbc) this.getFromCache(sKey);

                    if (customer_jdbc == null) {
                        customer_jdbc = new Customer_jdbc();
                        customer_jdbc.custNum = rs.getString("CUST_NUM");
                        customer_jdbc.custName = rs.getString("CUST_NAME");
                        customer_jdbc.address1 = rs.getString("ADR_1");
                        customer_jdbc.internalAddress = rs.getString("ADR_INTRNL");
                        customer_jdbc.city = rs.getString("CITY");
                        customer_jdbc.sapRegionCodeDscr = rs.getString("SAP_RGN_CODE_DSCR");
                        customer_jdbc.postalCode = rs.getString("POSTAL_CODE");
                        //customer_jdbc.countryCode = rs.getString("CNTRY_CODE");
                        resultList.add(customer_jdbc);
                        this.putInCache(sKey, customer_jdbc);
                    }
                }
                int resultCount = ps.getInt(8);
                resultList.setResultCount(resultCount);
                resultList.setLob(rsLineOfBus);
                rs.close();
                return resultList;
            } else
                return null;
        } catch (Exception e) {
        	logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_END_USER_BY_ATTR, parms));
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        }
    }

    /**
     * Call SP to get customer list by DSW Customer number
     */
    public CustomerSearchResultList findEndCusByDswID(String dswID, String lineOfBus,String country, int startPos)
            throws TopazException {

        HashMap parms = new HashMap();
        parms.put("piCustNum", dswID == null ? "" : dswID.trim());
        parms.put("piLineOfBus", lineOfBus == null ? "" : lineOfBus);
        parms.put("piCntryCode", country == null ? "" : country);
        parms.put("piStartPos", new Integer(startPos));
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            String sprocName = CommonDBConstants.DB2_S_QT_END_USER_BY_ID;
            CustomerSearchResultList resultList = new CustomerSearchResultList();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            if (!retCode)
                return resultList;
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == 0) {
                rs = ps.getResultSet();
                Customer_jdbc customer_jdbc = null;
                String rsLineOfBus = lineOfBus;

                while (rs.next()) {
                    String sKey = StringUtils.trimToEmpty(rs.getString("CUST_NUM"));
                    customer_jdbc = (Customer_jdbc) this.getFromCache(sKey);

                    if (customer_jdbc == null) {
                        customer_jdbc = new Customer_jdbc();
                        customer_jdbc.custNum = rs.getString("CUST_NUM");
                        customer_jdbc.custName = rs.getString("CUST_NAME");
                        customer_jdbc.address1 = rs.getString("ADR_1");
                        customer_jdbc.internalAddress = rs.getString("ADR_INTRNL");
                        customer_jdbc.city = rs.getString("CITY");
                        customer_jdbc.sapRegionCodeDscr = rs.getString("SAP_RGN_CODE_DSCR");
                        customer_jdbc.postalCode = rs.getString("POSTAL_CODE");
                        customer_jdbc.countryCode = rs.getString("CNTRY_CODE");
                        resultList.add(customer_jdbc);
                        this.putInCache(sKey, customer_jdbc);
                    }
                }
                int resultCount = ps.getInt(5);
                resultList.setResultCount(resultCount);
                resultList.setLob(rsLineOfBus);
                rs.close();
                return resultList;
            } else
                return resultList;
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        }
    }
    
    public void updateChannelInfo(String webQuoteNum, String chargeArgNum) throws TopazException
    {
    	HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piChargeArgNum", chargeArgNum);
        logContext.debug(this, "webQuoteNum=" + webQuoteNum + ";chargeArgNum=" + chargeArgNum);
        CallableStatement ps = null;
        try {
            String sprocName = CommonDBConstants.DB2_U_QT_CHANNEL_INFO;
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            logContext.debug(this,sqlQuery);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if ( poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS )
            {
            	return;
            }
            logContext.error(this, "update channel info throw no data: " + webQuoteNum + "; " + chargeArgNum);
            if ( poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA )
            {
            	return;
            }
            else 
            {
            	throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
        	this.close(ps, null);
        }
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

    // whether this customer is a NEW PGS customer that current user created
    private boolean isPGSNewCust(String jspBpSiteNum, String jdbcAddByUserName)
    {
    	if(StringUtils.isBlank(jspBpSiteNum) || StringUtils.isBlank(jdbcAddByUserName)){
    		return false;
    	}else{
    		if(jdbcAddByUserName.contains("-")){
    			if(jdbcAddByUserName.split("-")[1].trim().equals(jspBpSiteNum))
    				return true;
    			else
    				return false;
    		}else
    			return false;
    	}
    }
    
	@Override
	public Map searchApplianceAddress(String webQuoteNum,
			String addressType,String lob,String custNum,String bpSiteNum)throws TopazException {
		
		Map returnMap = new HashMap();
		
		HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piAddressType", Integer.valueOf(addressType));//ship-to address
        parms.put("piCustNum", custNum);
        parms.put("piBPSiteNum", StringUtils.isBlank(bpSiteNum)?"":bpSiteNum);
        logContext.debug(this, "webQuoteNum=" + webQuoteNum + ";addressType=" + 0 +";custNum="+ custNum+";bpSiteNum="+ bpSiteNum);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            String sprocName = CommonDBConstants.DB2_S_QT_CUST_ADDRESS;
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            if (!retCode)
                return returnMap;
            int poGenStatus = ps.getInt(1);
            int poOption = ps.getInt(6);
            // poOption  -- for ship to: 0(sold to for all addresses), 1(many addresses); install at: 0,1,2
            returnMap.put(DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION, poOption);
            if (poGenStatus == 0) {
                rs = ps.getResultSet();
                Map lineItemMap = new HashMap();
                ApplianceLineItem lineItem = null;
                while (rs.next()) {
                    lineItem = new ApplianceLineItem();
                    
                    lineItem.setCcDscr(rs.getString("CC_DSCR"));
                    lineItem.setConfigrtnId(rs.getString("CONFIGRTN_ID"));
                    lineItem.setPartQty(rs.getInt("PART_QTY"));
                    lineItem.setQuoteLineItemSeqNum(rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM"));
                    lineItem.setQuoteSectnSeqNum(rs.getInt("QUOTE_SECTN_SEQ_NUM")== -1 ? rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM") : rs.getInt("QUOTE_SECTN_SEQ_NUM"));
                    lineItem.setTransceiver(rs.getString("TRANSCEIVER"));
                    
                    lineItem.setAppliance(rs.getString("APPLIANCE"));
                    
                    lineItemMap.put(String.valueOf(lineItem.getQuoteLineItemSeqNum()), lineItem);
                }
                returnMap.put(DraftQuoteConstants.LINE_ITEM_MAP, lineItemMap);
                boolean nextFlag = ps.getMoreResults();
                if(nextFlag)
                {
                	rs = ps.getResultSet();
                	List<ApplianceAddress> applianceAddressList = new ArrayList<ApplianceAddress>();
                	ApplianceAddress applianceAddress = null;
                	while (rs.next()) {
                        applianceAddress = new ApplianceAddress();
                        
                        applianceAddress.setCustName(rs.getString("CUST_NAME"));
                        applianceAddress.setCustAddress(rs.getString("ADR_1"));
                        applianceAddress.setAddressIntrnl(rs.getString("ADR_INTRNL"));
                        applianceAddress.setCustCity(rs.getString("CITY"));
                        applianceAddress.setSapRegionCode(rs.getString("SAP_REGION_CODE"));
                        applianceAddress.setPostalCode(rs.getString("POSTAL_CODE"));
                        applianceAddress.setCntryCode(rs.getString("CNTRY_CODE"));
                        applianceAddress.setCustNum(StringUtils.isBlank(rs.getString("CUST_NUM"))?"":rs.getString("CUST_NUM"));
                        applianceAddress.setWebCustId(rs.getInt("WEB_CUST_ID"));
                        applianceAddress.setQuoteLineItemSeqNum(rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM"));
                        applianceAddress.setCntFirstName(rs.getString("CNT_FIRST_NAME"));
                        applianceAddress.setCntLastName(rs.getString("CNT_LAST_NAME"));
                        applianceAddress.setSapIntlPhoneNumFull(rs.getString("SAP_INTL_PHONE_NUM_FULL"));
                        applianceAddress.setCntId(rs.getInt("CNT_ID"));
                        applianceAddress.setSecId(rs.getInt("SEC_ID"));
                        applianceAddress.setSapCntId(rs.getString("SAP_CNT_ID"));
                        applianceAddress.setAddByUserName(rs.getString("ADD_BY_USER_NAME"));
                        applianceAddress.setShowPGSInfor((rs.getInt("BP_RELATED_CUST")==1 || isPGSNewCust(bpSiteNum,applianceAddress.getAddByUserName()))?true:false);
                        applianceAddressList.add(applianceAddress);
                    }
                	returnMap.put(DraftQuoteConstants.APPLIANCE_ADDRESS_LIST, applianceAddressList);
                }
                nextFlag = ps.getMoreResults();
                logContext.debug(this, "sold to address ps.getMoreResults():" + nextFlag);
                if(nextFlag)
                {
                	logContext.debug(this, "sold to address result:" + "----------");
                	rs = ps.getResultSet();
                	ApplianceAddress soldToCust = null;
                	while (rs.next()) {
                        if (soldToCust == null) {
                        	soldToCust = new ApplianceAddress();
                        	soldToCust.setCustName(rs.getString("CUST_NAME"));
                        	soldToCust.setCustAddress(rs.getString("ADR_1"));
                        	soldToCust.setAddressIntrnl(rs.getString("ADR_INTRNL"));
                        	soldToCust.setCustCity(rs.getString("CITY"));
                        	soldToCust.setSapRegionCode(rs.getString("SAP_REGION_CODE"));
                        	soldToCust.setPostalCode(rs.getString("POSTAL_CODE"));
                        	soldToCust.setCntryCode(rs.getString("CNTRY_CODE"));
                        	soldToCust.setCustNum(StringUtils.isBlank(rs.getString("CUST_NUM"))?"":rs.getString("CUST_NUM"));
                        	soldToCust.setCntFirstName(rs.getString("CNT_FIRST_NAME"));
                        	soldToCust.setCntLastName(rs.getString("CNT_LAST_NAME"));
                        	soldToCust.setSapIntlPhoneNumFull(rs.getString("SAP_INTL_PHONE_NUM_FULL"));
                        	soldToCust.setWebCustId(rs.getInt("WEB_CUST_ID"));
                        	soldToCust.setAddByUserName(rs.getString("ADD_BY_USER_NAME"));
                        	soldToCust.setSapCntId(rs.getString("SAP_CNT_ID"));
                        	soldToCust.setShowPGSInfor((rs.getInt("BP_RELATED_CUST")==1 || isPGSNewCust(bpSiteNum,soldToCust.getAddByUserName()))?true:false);
                        	returnMap.put(DraftQuoteConstants.SOLD_TO_ADDRESS, soldToCust);
                        	logContext.debug(this, "sold to address:" + soldToCust.getCntFirstName());
                        }
                    }
                }
                nextFlag = ps.getMoreResults();
                logContext.debug(this, "selected customer ps.getMoreResults():" + nextFlag);
                if(nextFlag)
                {
                	logContext.debug(this, "selected customer result:" + "----------");
                	rs = ps.getResultSet();
                	ApplianceAddress customer = null;
                	while (rs.next()) {
                        if (customer == null) {
                        	customer = new ApplianceAddress();
                        	customer.setCustName(rs.getString("CUST_NAME"));
                        	customer.setCustAddress(rs.getString("ADR_1"));
                        	customer.setAddressIntrnl(rs.getString("ADR_INTRNL"));
                        	customer.setCustCity(rs.getString("CITY"));
                        	customer.setSapRegionCode(rs.getString("SAP_REGION_CODE"));
                        	customer.setPostalCode(rs.getString("POSTAL_CODE"));
                        	customer.setCntryCode(rs.getString("CNTRY_CODE"));
                        	customer.setCustNum(StringUtils.isBlank(rs.getString("CUST_NUM"))?"":rs.getString("CUST_NUM"));
                        	customer.setCntFirstName(rs.getString("CNT_FIRST_NAME"));
                        	customer.setCntLastName(rs.getString("CNT_LAST_NAME"));
                        	customer.setSapIntlPhoneNumFull(rs.getString("SAP_INTL_PHONE_NUM_FULL"));
                        	customer.setSapCntId(rs.getString("SAP_CNT_ID"));
                        	customer.setWebCustId(rs.getInt("WEB_CUST_ID"));
                        	customer.setAddByUserName(rs.getString("ADD_BY_USER_NAME"));
                        	customer.setShowPGSInfor((rs.getInt("BP_RELATED_CUST")==1 || isPGSNewCust(bpSiteNum,customer.getAddByUserName()))?true:false);
                        	returnMap.put(DraftQuoteConstants.SELECTED_CUSTOMER, customer);
                        	logContext.debug(this, "selected customer:" + customer.getCntFirstName());
                        }
                    }
                	
                }
                rs.close();
                return returnMap;
            } else
                return returnMap;
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
        	this.close(ps, rs);
        
        }
	}
	
	/**
	 * Search customer default address for ship to and install at
	 */
	public DefaultCustAddress findCustomerDefaulAddress(String webQuoteNum , String bpSiteNum) throws TopazException {
		DefaultCustAddress defaultCustAddr = new DefaultCustAddress();
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piBpSiteNum", bpSiteNum);
        CallableStatement ps = null;
        ResultSet rs = null;
        boolean addressFlag = true;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_CUST_DEFAULT_ADDRESS, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_CUST_DEFAULT_ADDRESS, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            
            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                addressFlag = false;
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            /**
             * Get customer default address radio button checked status.
             */
            defaultCustAddr.setShipToOption(ps.getInt(4));
            defaultCustAddr.setInstallAtOption(ps.getInt(5));
            
            /**
             * Get customer default address content
             */
            if (retCode && addressFlag) {
            	rs = ps.getResultSet();
            	List<ApplianceAddress> addrList = new ArrayList<ApplianceAddress>();
            	while(rs.next()){
            		// Mapping resultset
            		ApplianceAddress addr = new ApplianceAddress();
            		addr.setCustName(rs.getString("CUST_NAME"));
            		addr.setCustAddress(rs.getString("ADR_1"));
            		addr.setCustCity(rs.getString("CITY"));
            		addr.setPostalCode(rs.getString("POSTAL_CODE"));
            		addr.setCntFirstName(rs.getString("CNT_FIRST_NAME"));
            		addr.setCntLastName(rs.getString("CNT_LAST_NAME"));
            		addr.setCntryCode(rs.getString("CNTRY_CODE"));
            		addr.setSapIntlPhoneNumFull(rs.getString("SAP_INTL_PHONE_NUM_FULL"));
            		addr.setAddressType(rs.getInt("ADDRESS_TYPE"));
            		addr.setShowPGSInfor((rs.getInt("BP_RELATED_CUST")==1 || isPGSNewCust(bpSiteNum,addr.getAddByUserName()))?true:false);
            		addr.setSapRegionCode(rs.getString("SAP_REGION_CODE"));
            		addrList.add(addr);
            	}
            	defaultCustAddr.setCustDefaulAddreList(addrList);
            	rs.close();
            }
            ps.close();            
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_CUST_DEFAULT_ADDRESS, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_CUST_DEFAULT_ADDRESS, e);
        } finally{
        	this.close(ps, rs);
        }
        return defaultCustAddr;
        
    }
	
	
	/**
	 * Update customer ship to / install at address option status.
	 */
	public boolean updateInstallAtAddr(String webQuoteNum, int installAtOption, int shipToOption) throws TopazException{
    	HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piInstallAtOption", installAtOption);
        parms.put("piShipToOption", shipToOption);
        logContext.debug(this, "piWebQuoteNum=" + webQuoteNum + ";piInstallAtOption=" + installAtOption+ ";piShipToOption=" + shipToOption);
        CallableStatement ps = null;
        try {
            String sprocName = CommonDBConstants.DB2_U_QT_CUST_INSTALL_AT_OPT;
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            logContext.debug(this,sqlQuery);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            ps.close();
            
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				return true;
			} else if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
				return false;
			} else {
				throw new SPException(LogHelper.logSPCall(sqlQuery, parms),poGenStatus);
			}
			
        } catch (Exception e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_U_QT_CUST_INSTALL_AT_OPT, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_U_QT_CUST_INSTALL_AT_OPT, e);
        } finally{
        	this.close(ps, null);
        }	
	}
	
	public boolean updateApplianceAddress(String userId, String webQuoteNum, String addressType,ApplianceAddress address) throws TopazException{
    	HashMap parms = new HashMap();
    	parms.put("piUserId", userId);
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piAddressType", addressType);
        parms.put("piCustNum", address.getCustNum());
        parms.put("piFirstName", address.getCntFirstName());
        parms.put("piLastName", address.getCntLastName());
        parms.put("piSapIntlPhoneNumFull", address.getSapIntlPhoneNumFull());
        parms.put("piQuoteLineSeqNum", null==address.getQuoteLineItemSeqNumStr()?"":address.getQuoteLineItemSeqNumStr());
        parms.put("piWebCustId", address.getWebCustId());
        parms.put("piSecId", address.getSecId());
        parms.put("piCntId", address.getCntId());
        logContext.debug(this, "webQuoteNum=" + webQuoteNum + ";piAddressType=" + addressType);
        CallableStatement ps = null;
        try {
            String sprocName = CommonDBConstants.DB2_IU_QT_CUST_ADDRESS;
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            logContext.debug(this,sqlQuery);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            ps.close();
            
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				return true;
			} else if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
				return false;
			} else {
				throw new SPException(LogHelper.logSPCall(sqlQuery, parms),poGenStatus);
			}
			
        } catch (Exception e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_IU_QT_CUST_ADDRESS, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_IU_QT_CUST_ADDRESS, e);
        } finally{
        	this.close(ps, null);
        }	
	}


	@Override
	public List findLineItemAddr(String webQuoteNum,String bpSiteNum) throws TopazException {
		
		List returnList = new ArrayList();
		
//		Map lineItemDetailMap = findLineItemByQuoteNum(webQuoteNum);
		
		Map shipToMap = searchApplianceAddress(webQuoteNum, DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE, "", "",bpSiteNum);
		returnList.add(shipToMap);
		
		Map installAtMap = searchApplianceAddress(webQuoteNum, DraftQuoteConstants.APPLIANCE_INSTALLAT_ADDTYPE, "", "",bpSiteNum);
		returnList.add(installAtMap);
		
//		returnList.add(lineItemDetailMap);
		
		return returnList;
	}
	
//	private Map findLineItemByQuoteNum(String webQuoteNum) throws TopazException {
//		Map returnMap = new HashMap();
//		
//		HashMap parms = new HashMap();
//        parms.put("piWebQuoteNum", webQuoteNum);
//        logContext.debug(this, "webQuoteNum=" + webQuoteNum );
//        CallableStatement ps = null;
//        ResultSet rs = null;
//        try {
//            String sprocName = CommonDBConstants.DB2_S_QT_GET_SHIP_INSTALL_ITEMS;
//            QueryContext queryCtx = QueryContext.getInstance();
//            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
//            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
//            queryCtx.completeStatement(ps, sprocName, parms);
//            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
//
//            boolean retCode = ps.execute();
//            if (!retCode)
//                return returnMap;
//            int poGenStatus = ps.getInt(1);
//            
//            if (poGenStatus == 0) {
//                rs = ps.getResultSet();
//                ApplianceLineItem lineItem = null;
//                while (rs.next()) {
//                    lineItem = new ApplianceLineItem();
//                    lineItem.setQuoteLineItemSeqNum(rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM"));
//                    lineItem.setDestSeqNum(rs.getInt("DEST_OBJCT_LINE_ITM_SEQ_NUM"));
//                    lineItem.setConfigrtnId(rs.getString("CONFIGRTN_ID"));
//                    lineItem.setPartNum(rs.getString("PART_NUM"));
//                    lineItem.setQuoteSectnSeqNum(rs.getInt("QUOTE_SECTN_SEQ_NUM"));
//                    lineItem.setPartDscrLong(rs.getString("PART_DSCR_LONG"));
//                    lineItem.setCustReqstdArrivlDate(rs.getDate("CUST_REQSTD_ARRIVL_DATE"));
//                    boolean isAppl = rs.getInt("IS_APPLIANCE_PART")==1?true:false;
//                    lineItem.setAppliancePart(isAppl);
//                    returnMap.put(String.valueOf(lineItem.getQuoteLineItemSeqNum()),  lineItem);
//                }
//                
//                rs.close();
//                return returnMap;
//            } else
//                return returnMap;
//        } catch (Exception e) {
//            logContext.error(this, e.getMessage());
//            throw new TopazException(e);
//        } finally{
//        	this.close(ps, rs);
//        
//        }
//	}


	@Override
	public boolean updateLineItemAddr(String webQuoteNum, String userId)
			throws TopazException {
		HashMap parms = new HashMap();
    	parms.put("piUserId", userId);
        parms.put("piWebQuoteNum", webQuoteNum);
        
        logContext.debug(this, "webQuoteNum=" + webQuoteNum + ";userId=" + userId);
        CallableStatement ps = null;
        try {
            String sprocName = CommonDBConstants.DB2_IU_QT_LINE_ITEM_ADDRESS;
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(sprocName, null);
            logContext.debug(this,sqlQuery);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, sprocName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            ps.close();
            
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				return true;
			} else if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
				return false;
			} else {
				throw new SPException(LogHelper.logSPCall(sqlQuery, parms),poGenStatus);
			}
			
        } catch (Exception e) {
        	logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_IU_QT_LINE_ITEM_ADDRESS, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_IU_QT_LINE_ITEM_ADDRESS, e);
        } finally{
        	this.close(ps, null);
        }
	}
    /*
     * (non-Javadoc)
     * @Abstract:  CSA - provide an interface to Overlay to search customer existing contracts
     * @see com.ibm.dsw.quote.common.domain.CustomerFactory#findByQuoteNum(java.lang.String,
     *      java.lang.String)
     */
    public Customer findCustForExistCtrctCust(String webQuoteNum,String custNum) throws TopazException {

        Customer_jdbc customer_jdbc = null;
        HashMap parms = new HashMap();
        parms.put("piCustNum", custNum == null ? "" : StringHelper.fillString(custNum));
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_CUSTINFO_EXISTCTRCT, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_CUSTINFO_EXISTCTRCT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            if (retCode) {
            	    customer_jdbc=new Customer_jdbc();
            	    Contract contract = new Contract();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        contract = new Contract();
                        contract.setSapContractNum(StringUtils.trimToEmpty(rs.getString("SAP_CTRCT_NUM")));
                        contract.setSapContractVariantCode(StringUtils.trimToEmpty(rs.getString("SAP_CTRCT_VARIANT_CODE")));
                        contract.setVolDiscLevelCode(StringUtils.trimToEmpty(rs.getString("VOL_DISC_LEVEL_CODE")));
                        contract.setTranPriceLevelCode(StringUtils.trimToEmpty(rs.getString("TRAN_PRICE_LEVEL_CODE")));
                        contract.setAnniversaryDate(rs.getDate("DISMANTLING_DATE"));
                        contract.setCtrctStartDate(rs.getDate("CTRCT_START_DATE"));
                        contract.setCtrctEndDate(rs.getDate("CTRCT_END_DATE"));
                        String agrmtFlag=StringUtils.trimToEmpty(rs.getString("AGRMT_FLAG"));//1.CSA,2.OTHER
                        if(QuoteConstants.LOB_CSA.equalsIgnoreCase(agrmtFlag))
                        {
                        	customer_jdbc.addCsaContractList(contract);
                        }else
                        {
                        	customer_jdbc.addNoCsaContractList(contract);
                        }
                    }
                    rs.close();
                        }
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_GET_CUSTINFO_EXISTCTRCT, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_CUSTINFO_EXISTCTRCT, e);
        } finally{
        	this.close(ps, rs);
        }

        return customer_jdbc;
    }
    
    public ApplianceAddress getInstallAtByMTM(String type, String mode, String serialNum, String renewlQuoteNum, int renewalLineItemSeq) throws TopazException
    {
    	ApplianceAddress addr = null;
        Customer_jdbc customer_jdbc = null;
        HashMap parms = new HashMap();
        parms.put("piType", StringUtils.trimToEmpty(type));
        parms.put("piMode", StringUtils.trimToEmpty(mode));
        parms.put("piSerialNum", StringUtils.trimToEmpty(serialNum));
        parms.put("piRenewalQuoteNum", StringUtils.trimToEmpty(renewlQuoteNum));
        parms.put("piRenewalLineItemSeq", renewalLineItemSeq);
        CallableStatement ps = null;
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_INSTALL_AT_BY_MTM, null);
            ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_INSTALL_AT_BY_MTM, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            rs = ps.getResultSet();
            while ( rs.next() )
            {
            	addr = new ApplianceAddress();
            	addr.setCustNum(rs.getString("INSTALL_AT_CUST_NUM"));
            	addr.setCustName(rs.getString("INSTALL_AT_CUST_NAME"));
            	addr.setPostalCode(rs.getString("INSTALL_AT_POSTAL_CODE"));
            	addr.setCustCity(rs.getString("INSTALL_AT_CITY"));
            	addr.setSapRegionCode(rs.getString("INSTALL_AT_SAP_REGION_CODE"));
            	addr.setCntryCode(rs.getString("INSTALL_AT_CNTRY_CODE"));
            	addr.setSapIntlPhoneNumFull(rs.getString("SAP_INTL_PHONE_NUM_FULL"));
            	addr.setAddressIntrnl(rs.getString("INSTALL_AT_ADDRESS"));
            	addr.setCntId(0);
            	addr.setAddrBaseMTM(true);
            }
            rs.close();
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_GET_CUSTINFO_EXISTCTRCT, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_CUSTINFO_EXISTCTRCT, e);
        } finally{
        	this.close(ps, rs);
        }

        return addr;
    	
    }

}
