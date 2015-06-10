package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.ContractFactory;
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
 * This <code>ContractFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Nov 2, 2009
 */

public class ContractFactory_jdbc extends ContractFactory {
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public ContractFactory_jdbc() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.ContractFactory#createWebContract(com.ibm.dsw.quote.common.domain.Contract)
     */
    public void createOrUpdateWebCtrct(Contract contract, String userID) throws TopazException {
        
        HashMap parms = new HashMap();
        Integer webCtrctId = contract.getWebCtrctId() > 0 ? new Integer(contract.getWebCtrctId()) : new Integer(0);
        
        parms.put("pioWebCtrctId", webCtrctId);
        parms.put("piSapCtrctVariantCode", contract.getSapContractVariantCode());
        parms.put("piAuthrztnGrp", contract.getAuthrztnGroup());
        parms.put("piVolDiscLevelCode", contract.getVolDiscLevelCode());
        parms.put("piUserID", userID);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_WEB_CTRCT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_WEB_CTRCT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != poGenStatus) {
                throw new TopazException("SP call returns error code: " + poGenStatus);
            }

            contract.setWebCtrctId(ps.getInt(2));
            
        } catch (SQLException e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }

    }
    
    public Contract getContractByNum(String sapCtrctNum, String lob) throws TopazException {
        
        HashMap parms = new HashMap();
        Contract contract = new Contract();
        String vldCtrctNum = StringUtils.isBlank(sapCtrctNum) ? "" : StringHelper.fillString(sapCtrctNum);
        
        parms.put("piSapCtrctNum", vldCtrctNum);
        parms.put("piLineOfBus", lob);

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_CTRCT, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_CTRCT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != poGenStatus) {
                throw new TopazException("SP call returns error code: " + poGenStatus);
            }

            contract.setSapContractNum(sapCtrctNum);
            contract.setSapContractVariantCode(StringUtils.trimToEmpty(ps.getString(4)));
            contract.setIsContractActiveFlag(ps.getInt(5));
            
        } catch (SQLException e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        }
        
        return contract;
    }

}
