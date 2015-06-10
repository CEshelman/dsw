package com.ibm.dsw.quote.customerlist.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.customerlist.domain.CustomerList;
import com.ibm.dsw.quote.customerlist.domain.CustomerListResult;
import com.ibm.dsw.quote.customerlist.process.CustomerListProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerListProcess_jdbc<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2008-6-24
 */

public class CustomerListProcess_jdbc extends CustomerListProcess_Impl {
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public static final String SP_NAME_KEY = "_sp_name_key_";

    public CustomerListResult retrieveCustomerByCustNum(String custNum, String programType, String countryCode,
            String currencyCode, int startingIndex, int endingIndex) throws QuoteException {
        Map params = getParamsMap(custNum, programType, countryCode, currencyCode, startingIndex, endingIndex);

        return retrievCustomer(params);
    }

    public CustomerListResult retrieveCustomerByCustAttr(String custAttr, String stateOrProvinceCode,
            String programType, String countryCode, String currencyCode, int startingIndex, int endingIndex)
            throws QuoteException {
        Map params = getParamsMap(custAttr, stateOrProvinceCode, programType, countryCode, currencyCode, startingIndex,
                endingIndex);

        return retrievCustomer(params);
    }

    private CustomerListResult retrievCustomer(Map params) throws QuoteException {
        String spName = (String) params.get(SP_NAME_KEY);
        params.remove(SP_NAME_KEY);
        CustomerListResult result = new CustomerListResult();

        this.beginTransaction();
        ResultSet resultSet = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String spSql = queryCtx.getCompletedQuery(spName, null);

            CallableStatement stmt = TopazUtil.getConnection().prepareCall(spSql);
            logContext.debug(this, LogHelper.logSPCall(spSql, (HashMap) params));

            queryCtx.completeStatement(stmt, spName, params);
            stmt.execute();

            result.setSqlErrorCode(stmt.getInt(1));
            result.setTotalCount(stmt.getInt(2));

            resultSet = stmt.getResultSet();
            if (resultSet != null) {
                while (resultSet.next()) {
                    result.addCustomer(mapProperties(resultSet));
                }
            }

            commitTransaction();
        } catch (Exception e) {
            logContext.error(this, e);
            throw new QuoteException(e);
        } finally {
        	try {
				if (null != resultSet && !resultSet.isClosed())
				{
					resultSet.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
            rollbackTransaction();
        }

        return result;
    }

    protected Map getParamsMap(String custNum, String programType, String countryCode, String currencyCode,
            int startingIndex, int endingIndex) {
        Map params = getCommonParamsMap(programType, countryCode, currencyCode, startingIndex, endingIndex);
        params.put("piCustNum", custNum);
        params.put(SP_NAME_KEY, CommonDBConstants.DB2_S_QT_CUSTLIST_ID);

        return params;
    }

    protected Map getParamsMap(String custAttr, String stateOrProvinceCode, String programType, String countryCode,
            String currencyCode, int startingIndex, int endingIndex) {
        Map params = getCommonParamsMap(programType, countryCode, currencyCode, startingIndex, endingIndex);
        params.put("piCustName", custAttr);
        if(stateOrProvinceCode == null){
            stateOrProvinceCode = "";
        }
        params.put("piStateCode", stateOrProvinceCode);
        params.put(SP_NAME_KEY, CommonDBConstants.DB2_S_QT_CUSTLIST_ATTR);

        return params;
    }

    protected Map getCommonParamsMap(String programType, String countryCode, String currencyCode, int startingIndex,
            int endingIndex) {
        Map map = new HashMap();

        if (countryCode == null || countryCode.equals("")) {
            countryCode = "%";
        }
        if (currencyCode == null || currencyCode.equals("")) {
            currencyCode = "%";
        }
        map.put("piLineOfBus", programType);
        map.put("piCntryCode", countryCode);
        map.put("piCurrncyCode", currencyCode);
        map.put("piStartPos", new Integer(startingIndex));
        map.put("piEndPos", new Integer(endingIndex));

        return map;
    }

    protected CustomerList mapProperties(ResultSet set) throws SQLException {
        CustomerList customer = new CustomerList();
        customer.setSiteNumber(set.getString("cust_num"));
        customer.setCustomerReferenceDataNumber(set.getString("rdc_number"));
        customer.setIBMCustomerNumber(set.getString("ibm_cust_num"));
        customer.setCustomerName(set.getString("cust_name"));
        customer.setAddressLine1(set.getString("adr_1"));
        customer.setAddressLine2(set.getString("adr_intrnl"));
        customer.setCity(set.getString("city"));

        customer.setStateOrProvinceCode(set.getString("sap_region_code"));
        customer.setStateOrProvinceDescription(set.getString("sap_rgn_code_dscr"));
        customer.setPostalCode(set.getString("postal_code"));
        customer.setCountryCode(set.getString("cntry_code"));
        customer.setCountryDescription(set.getString("cntry_dscr"));
        customer.setCurrencyCode(set.getString("CURRNCY_CODE"));
        customer.setCurrencyDescription(set.getString("currncy_dscr"));

        return customer;
    }
}
