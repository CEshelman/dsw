package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory;
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
 * This <code>SpecialBidApprvr_jdbc<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SpecialBidApprvrFactory_jdbc extends SpecialBidApprvrFactory {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     *  
     */
    public SpecialBidApprvrFactory_jdbc() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory#createSpecialBidApprvr(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public SpecialBidApprvr createSpecialBidApprvrForSelectionUpdate() throws TopazException {
        SpecialBidApprvr_jdbc specialBidApprvr_jdbc = new SpecialBidApprvr_jdbc();
        specialBidApprvr_jdbc.isNew(true); //persist to DB2
        return specialBidApprvr_jdbc;
    }
    
    public SpecialBidApprvr createSpecialBidApprvrForActionUpdate() throws TopazException {
        SpecialBidApprvr_jdbc specialBidApprvr_jdbc = new SpecialBidApprvr_jdbc();
        specialBidApprvr_jdbc.isNew(false); //persist to DB2
        return specialBidApprvr_jdbc;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory#findApprvrsByQuoteNum(java.lang.String)
     */
    public List findApprvrsByQuoteNum(String webQuoteNum) throws TopazException {
        List apprvrList = new ArrayList();

        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum==null?"":webQuoteNum);
        ResultSet rs = null;
        try {
	        QueryContext queryCtx = QueryContext.getInstance();
	        String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_SB_APPRV, null);
	        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	        queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_SB_APPRV, parms);
	        logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
	        
            ps.execute();
            
            int resultCode = ps.getInt(1);
            if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS){
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }
            
            rs =  ps.getResultSet();
            while (rs.next()) {
                SpecialBidApprvr_jdbc apprvr = new SpecialBidApprvr_jdbc();
                apprvr.webQuoteNum = webQuoteNum;
                apprvr.apprvrAction = rs.getString("USER_ACTION");
                apprvr.apprvrEmail = rs.getString("APPRVR_EMAIL");
                apprvr.specialBidApprGrp = rs.getString("SPECL_BID_APPRVR_GRP_NAME");
                apprvr.specialBidApprLvl = rs.getInt("SPECL_BID_APPRVR_LVL");
                apprvr.rdyToOrder = rs.getInt("RDY_TO_ORD_FLAG");
                if ( rs.getString("SPECL_BID_SUPRSD_APPRVR_TYPE") != null )
                {
                    apprvr.setSupersedeApprvFlag(1);
                }
                else
                {
                    apprvr.setSupersedeApprvFlag(0);
                }
                apprvrList.add(apprvr);
            }
        } catch (SQLException sqle) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(sqle));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_SB_APPRV);
        }finally{
        	try {
				if (null != rs)
				{
					rs.close();
				}
			} catch (SQLException e) {
				logContext.error("Failed to close the resultset!", e);
			}
        }
        
        return apprvrList;
    }
}
