package com.ibm.dsw.quote.massdlgtn.process.jdbc;

import java.sql.CallableStatement;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcess_jdbc</code> class is jdbc implementation of
 * MassDlgtnProcess.
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public class MassDlgtnProcess_jdbc extends MassDlgtnProcess_Impl {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess#addDelegate(java.lang.String,
     *      java.lang.String)
     */
    public void addDelegate(String salesUserID, String dlgID,String managerId) throws QuoteException {

        try {
            this.beginTransaction();
            salesUserID = StringUtils.lowerCase(salesUserID);
            dlgID = StringUtils.lowerCase(dlgID);
            
            SalesRep delegate = SalesRepFactory.singleton().createSalesRep(dlgID);
            populateBluePageInfo(delegate);

            HashMap parms = new HashMap();

            parms.put("piUserID", salesUserID);
            parms.put("piDlgtnUserID", StringUtils.lowerCase(delegate.getEmailAddress()));
            parms.put("piManagerID", StringUtils.lowerCase(managerId));
            parms.put("piNotesID", delegate.getNotesId());
            parms.put("piEmpCntryCode", delegate.getCountryCode());
            parms.put("piEmpNameFull", delegate.getFullName());
            parms.put("piEmpFirstName", delegate.getFirstName());
            parms.put("piEmpLastName", delegate.getLastName());
            parms.put("piIntlPhnNumFull", delegate.getPhoneNumber());
            parms.put("piIntlFaxNumFull ", delegate.getFaxNumber());
            parms.put("piInactFlag", new Integer(0));
            insertOrUpdate(parms);

            this.commitTransaction();

        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess#removeDelegate(java.lang.String,
     *      java.lang.String)
     */
    public void removeDelegate(String salesUserID, String dlgID, String managerId) throws QuoteException {

        try {

            this.beginTransaction();

            HashMap parms = new HashMap();

            parms.put("piUserID", StringUtils.lowerCase(salesUserID));
            parms.put("piDlgtnUserID", StringUtils.lowerCase(dlgID));
            parms.put("piManagerID", StringUtils.lowerCase(managerId));
            parms.put("piNotesID", "");
            parms.put("piEmpCntryCode", "");
            parms.put("piEmpNameFull", "");
            parms.put("piEmpFirstName", "");
            parms.put("piEmpLastName", "");
            parms.put("piIntlPhnNumFull", "");
            parms.put("piIntlFaxNumFull ", "");
            parms.put("piInactFlag", new Integer(1));
            insertOrUpdate(parms);

            this.commitTransaction();

        } catch (TopazException e) {

            throw new QuoteException(e);

        } finally {

            this.rollbackTransaction();
        }
    }

    private void insertOrUpdate(HashMap parms) throws TopazException {
        LogContext log = LogContextFactory.singleton().getLogContext();

        try {

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_MASS_DLG, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_MASS_DLG, parms);

            log.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            
            ps.execute();

            int retStatus = ps.getInt(1);

            if (0 != retStatus) {
                throw new TopazException("exeute sp failed:" + sqlQuery);
            }

        } catch (Exception e) {
            log.error(this, e.getMessage());
            throw new TopazException(e);
        }
    }
    
	/**
      *if rep has owned quote, return full name.
     **/
    public String retriveFullRepName(String salesUserId)throws QuoteException{
    	LogContext log = LogContextFactory.singleton().getLogContext();
        try {
            this.beginTransaction();

            String salesFullName = SalesRepFactory.singleton().retriveFullRepName(salesUserId);

            this.commitTransaction();
            return salesFullName;

        } catch (TopazException e) {
            log.error(this, e.getMessage());
            throw new QuoteException(e);

        } finally {

            this.rollbackTransaction();
        }
    }
}
