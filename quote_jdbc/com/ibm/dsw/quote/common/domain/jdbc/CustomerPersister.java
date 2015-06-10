package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerPersister<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 9, 2007
 */

public class CustomerPersister extends Persister {
    
    private Customer_jdbc customer_jdbc;
    
    public CustomerPersister(Customer_jdbc customer_jdbc){
        super();
        this.customer_jdbc = customer_jdbc;
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
   public void update(Connection connection) throws TopazException {
        //this.insertOrUpdate(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        // don't need to delete a customer

    }

	/**
	 * Call SP to get a customer by customer number.
	 */
    public void hydrate(Connection connection) throws TopazException {
        //TODO implement this method to call SP here.
        this.isNew(false);
        this.isDeleted(false);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        //this.insertOrUpdate(connection);
    } 
    
    private void insertOrUpdate(Connection connection) throws TopazException{
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
        
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_WEB_CUSTMER, null);
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_WEB_CUSTMER, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            ps.execute();
            retCode = ps.getInt(1);
            
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            
            customer_jdbc.setWebCustId(ps.getInt(2));
            this.isNew(true);
            this.isDeleted(false);
        } catch (SQLException e) {
            logger.error("Failed to insert the customer to the database!", e);
            throw new TopazException(e);
        }
    }  

}
