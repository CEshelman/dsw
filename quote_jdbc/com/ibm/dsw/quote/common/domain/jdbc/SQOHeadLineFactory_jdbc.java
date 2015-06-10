package com.ibm.dsw.quote.common.domain.jdbc; 

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.SQOHeadLineFactory;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>SQOHeadLineFactory_jdbc<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-7-13
 */
public class SQOHeadLineFactory_jdbc extends SQOHeadLineFactory{

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.SQOHeadLineFactory#getSQOHeadLineMsg(java.lang.String)
     */
    public List getSQOHeadLineMsg(String applCode) throws TopazException {
    	ResultSet rs = null;
        try {
    		QueryContext queryCtx = QueryContext.getInstance();
            String spName = CommonDBConstants.DB2_S_WEB_DSW_HEADLINE;
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            HashMap parms = new HashMap();
            parms.put("piApplCode", applCode);
            
            logContext.debug(this,"Get SQO head line msg, appl code is " + applCode);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, spName, parms);    
            
            ps.execute();
            
            int resultCode = ps.getInt(1);
            if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            List resultList = new ArrayList();
            resultList.add(StringUtils.trimToEmpty(ps.getString(2)));
            logContext.debug(this,"SQO head line message is:  " + ps.getString(2));
            
            rs = ps.getResultSet();
            if (rs == null) {
                logContext.debug(this, "There is no cursor definition in " + spName);
            } else {
                while (rs.next()) {
                    resultList.add(StringUtils.trimToEmpty(rs.getString(1)));
                    logContext.debug(this, "SQO head line message is:  " + rs.getString(1));
                }
                rs.close();
            }
            return resultList;
            	
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new TopazException(e);
        } finally{
	    	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error(this,"Failed to close the resultset!");
			}
        }
    }

}
 