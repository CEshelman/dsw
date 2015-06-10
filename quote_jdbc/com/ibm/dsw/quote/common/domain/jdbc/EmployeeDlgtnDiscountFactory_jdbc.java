package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.EmployeeDlgtnDiscountFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EmployeeDlgtnDiscountFactory_jdbc</code> class.
 * 
 * @author: zhangdy@cn.ibm.com
 * 
 * Created on: Apr 10, 2008
 */
public class EmployeeDlgtnDiscountFactory_jdbc extends EmployeeDlgtnDiscountFactory {

    public int[] checkEmpDlgtnDisc(QuoteHeader header, String rsAndMaxDisc) throws TopazException{
        return this.checkEmpDlgtnDiscByCrrntUser(header, rsAndMaxDisc, header.getCreatorId());
    }
    
    public int[] checkEmpDlgtnDiscByCrrntUser(QuoteHeader header, String rsAndMaxDisc, String userId) throws TopazException{
        int[] result = null;
        HashMap params = new HashMap();
        //inputs : user email address, quote's LOB, quote's acquisition code, quote's program migrated code, quote-payer's country code, revenue-stream-and-max-discount-on-quote
        params.put("piUserEmail", userId);
        params.put("piQuoteLob",header.getLob().getCode());
        params.put("piAcqCode",header.getAcqrtnCode());
        params.put("piProgMirCode",header.getProgMigrationCode());
        params.put("piPricingCntryCode",header.getPriceCountry().getCode3());
        params.put("piRS_MDList",rsAndMaxDisc);
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_CHK_EMP_DLGTN, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_CHK_EMP_DLGTN, params);
            boolean psResult = ps.execute();
            int status = ps.getInt(1);
            int isSpecialBid = ps.getInt(8);
            result = new int[]{status,isSpecialBid};
        } catch (Exception e) {
            logger.error("Failed to get the employee delegation percentage from the database!", e);
            throw new TopazException(e);
        }
        return result;
    }
}
