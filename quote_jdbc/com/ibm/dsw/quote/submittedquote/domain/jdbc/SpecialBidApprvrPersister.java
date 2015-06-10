package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
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
 * This <code>SpecialBidApprvrPersister<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SpecialBidApprvrPersister extends Persister {

    private SpecialBidApprvr_jdbc specialBidApprvr_jdbc;

    public SpecialBidApprvrPersister(SpecialBidApprvr_jdbc specialBidApprvr_jdbc) {
        super();
        this.specialBidApprvr_jdbc = specialBidApprvr_jdbc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {
        this.insertOrUpdate(connection, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        //do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        //do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        this.insertOrUpdate(connection, true);
    }

    /**
     * 
     * @param connection
     * @throws TopazException
     */
    private void insertOrUpdate(Connection connection, boolean isForSelection) throws TopazException {
        String sp_name = "";
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", specialBidApprvr_jdbc.getWebQuoteNum());
      
        if (isForSelection) {
            params.put("piGrpName", specialBidApprvr_jdbc.getSpecialBidApprGrp()==null?"":specialBidApprvr_jdbc.getSpecialBidApprGrp());
            params.put("piApprEmail", specialBidApprvr_jdbc.getApprvrEmail()==null?"":specialBidApprvr_jdbc.getApprvrEmail());
            params.put("piUserEmail", specialBidApprvr_jdbc.getApplierEmail()==null?"":specialBidApprvr_jdbc.getApplierEmail());
            params.put("piSpeclBidApprvrLvl", new Integer(specialBidApprvr_jdbc.getSpecialBidApprLvl()));
            params.put("piIsPGS", new Integer(specialBidApprvr_jdbc.getIsPGS()));
            sp_name = CommonDBConstants.DB2_IU_QT_SB_APPR;
        } else {
            params.put("piApprvrEmail", specialBidApprvr_jdbc.getApprvrEmail()==null?"":specialBidApprvr_jdbc.getApprvrEmail());
            params.put("piApprvrAction", specialBidApprvr_jdbc.getApprvrAction()==null?"":specialBidApprvr_jdbc.getApprvrAction());
            params.put("piSpeclBidApprvrLvl", new Integer(specialBidApprvr_jdbc.getSpecialBidApprLvl()));
            params.put("piIsSupersedeApprv", new Integer(specialBidApprvr_jdbc.getSupersedeApprvFlag()));
            params.put("piApprvrReturnReason", specialBidApprvr_jdbc.getReturnReason()==null?"":specialBidApprvr_jdbc.getReturnReason());
            sp_name = CommonDBConstants.DB2_IU_QT_SB_APPR_ACT;
        }

        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(sp_name, null);
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, sp_name, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            
            if (!isForSelection)
                specialBidApprvr_jdbc.lastActApprEmail = ps.getString(8);
        } catch (Exception e) {
            logger.error("Failed to insert/update the special bid approver to the database!", e);
            throw new TopazException(e);
        }
    }

}
