package com.ibm.dsw.quote.pvu.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.pvu.config.VUDBConstants;
import com.ibm.dsw.quote.pvu.domain.VUConfig;
import com.ibm.dsw.quote.pvu.domain.VUConfigFactory;
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
 * This <code></code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-19
 */
public class VUConfigFactory_jdbc extends VUConfigFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfigFactory#create(java.lang.Integer, java.lang.Integer)
     */
    public SearchResultList findByConfigNum(String configNum, String noDescFlag) throws TopazException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        SearchResultList result = new SearchResultList();
        
        String sqlQuery = null ;
        int poGenStatus = -1 ;
        HashMap params = new HashMap();
        params.put("piVuConfigNum", configNum);
        params.put("piNoDscrFlag", noDescFlag);
        ResultSet rs = null;
        try{
            
            QueryContext queryCtx = QueryContext.getInstance();
            sqlQuery = queryCtx.getCompletedQuery(VUDBConstants.S_VU_CONFIG, null);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
            
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, VUDBConstants.S_VU_CONFIG, params);
            rs = ps.executeQuery();
            poGenStatus = ps.getInt(1);
            
            if(poGenStatus == 0) {
                while(rs.next()){
                    String procrCode = rs.getString("PROCR_CODE");
                    String procrVendCode = rs.getString("PROCR_VEND_CODE");
                    String procrBrandCode = rs.getString("PROCR_BRAND_CODE");
                    String procrTypeCode = rs.getString("PROCR_TYPE_CODE");
                    String procrValUnit = rs.getString("CORE_VAL_UNIT");
                    int procrTypeQTY = rs.getInt("PROCR_TYPE_QTY");
                    int procrTypeDVU = rs.getInt("EXTND_DVU");
                    VUConfig vcfg = new VUConfig_jdbc(procrCode, procrVendCode, procrBrandCode,
                            procrTypeCode, procrValUnit, procrTypeQTY, procrTypeDVU);
                    logContext.debug(logContext, "VUConfig " + vcfg);
                    result.add(vcfg);
                }
            } else {
                logContext.error(logContext, "SP [ " + sqlQuery + " ] return status: " + + poGenStatus + "\n" + LogHelper.logSPCall(sqlQuery, params));
                throw new TopazException("SP [ " + sqlQuery + " ] return status: " + + poGenStatus);
            }
        }catch (Exception e) {
            logContext.error(logContext, "Error in calling SP [ " + sqlQuery + " ]\n" + LogHelper.logSPCall(sqlQuery, params) + "\n" + e.getMessage());
            throw new TopazException("" + e);
        }finally{
        	try {
				if (null != rs && ! rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(logContext, "fail to close the resultset.");
			}
        }
        
        return result;
    }

}
