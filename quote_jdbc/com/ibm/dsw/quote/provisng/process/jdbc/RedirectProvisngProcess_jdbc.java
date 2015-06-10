package com.ibm.dsw.quote.provisng.process.jdbc;

import java.sql.CallableStatement;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.provisng.process.RedirectProvisngProcess_Impl;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

public class RedirectProvisngProcess_jdbc extends RedirectProvisngProcess_Impl {

	@Override
	public String updateProvisngId(String webQuoteNum, String provisngIdForBrand,String saasBrandCode)
			throws TopazException {
		try{
			String spName = CommonDBConstants.DB2_U_QT_PROVISNG_ID;
	        HashMap parms = new HashMap();
	        
	        logContext.debug(this, "webquote number is " + webQuoteNum+"and the configuration id is "+provisngIdForBrand);
	        
	        parms.put("piWebQuoteNum", webQuoteNum);
	        parms.put("piProvisngIdForBrand", provisngIdForBrand);
	        parms.put("piSaasBrandCode", saasBrandCode);
	        
	        QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            
            String provisingId = StringUtils.trimToEmpty(ps.getString(5));
            
            return provisingId;
	        
		}catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);

        }
        
	}

}
