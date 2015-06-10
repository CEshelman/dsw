package com.ibm.dsw.quote.dlgtn.process.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.RowExistingException;
import com.ibm.dsw.quote.dlgtn.process.QuoteDlgtnProcess_Impl;
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
 * The <code>QuoteDlgtnProcess_jbc</code> class implements the db operation
 * for two business methods
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 13, 2007
 */
public class QuoteDlgtnProcess_jdbc extends QuoteDlgtnProcess_Impl {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.QuoteDlgtnProcess#addQuoteDelegation(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addQuoteDelegation(String webQuoteNum, String creatorId, String dlgUserId) throws QuoteException {
        LogContext log = LogContextFactory.singleton().getLogContext();
        dlgUserId = StringUtils.lowerCase(dlgUserId);
        try {
            log.debug(this, "begin to add quote delegation - quote num=" + webQuoteNum + " creatorId=" + creatorId
                    + " userId=" + dlgUserId);
            this.beginTransaction();

            HashMap parms = new HashMap();

            parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
            parms.put("piCreatorId", creatorId == null ? "" : creatorId);
            parms.put("piUserId", dlgUserId);
            parms.put("piEmailAdr", dlgUserId);

            this.fillBluePageInfo(dlgUserId, parms);

            this.excuteSp(CommonDBConstants.DB2_I_QT_DLGTN, parms);

            this.commitTransaction();
            log.debug(this, "complete add quote delegation");
        } catch (RowExistingException re) {
            log.error(this, re.getMessage());
        } catch (TopazException e) {
            log.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    public void addQuoteDelegation(String webQuoteNum, String creatorId, String dlgUserId, String cntryCode,
            String notesId, String fullName, String firstName, String lastName, String intlPhoneNumFull,
            String intlFaxNumFull) throws QuoteException {
        
        LogContext log = LogContextFactory.singleton().getLogContext();
        dlgUserId = StringUtils.lowerCase(dlgUserId);
        
        try {
            log.debug(this, "begin to add quote delegation - quote num=" + webQuoteNum + " creatorId=" + creatorId
                    + " userId=" + dlgUserId);
            this.beginTransaction();
            
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
            parms.put("piCreatorId", creatorId == null ? "" : creatorId);
            parms.put("piUserId", dlgUserId);
            parms.put("piEmailAdr", dlgUserId);
            parms.put("piCntryCode", cntryCode == null ? "" : cntryCode);
            parms.put("piNotesId", notesId == null ? "" : notesId);
            parms.put("piFullName", fullName == null ? "" : fullName);
            parms.put("piFirstName", firstName == null ? "" : firstName);
            parms.put("piLastName ", lastName == null ? "" : lastName);
            parms.put("piIntlPhoneNumFull", intlPhoneNumFull == null ? "" : intlPhoneNumFull);
            parms.put("piIntlFaxNumFull", intlFaxNumFull == null ? "" : intlFaxNumFull);
            
            this.excuteSp(CommonDBConstants.DB2_I_QT_DLGTN, parms);
            
            this.commitTransaction();
            log.debug(this, "complete add quote delegation");
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    private void excuteSp(String spName, HashMap parms) throws TopazException {
        LogContext log = LogContextFactory.singleton().getLogContext();

        try {

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, spName, parms);
            log.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_ROW_EXISTING == retStatus) {
                throw new RowExistingException("Row existing: " + sqlQuery);
            } else if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed:" + sqlQuery);
            }

        } catch (SQLException e) {
            log.error(this, e.getMessage());
            throw new TopazException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.QuoteDlgtnProcess#removeQuoteDelegation(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void removeQuoteDelegation(String webQuoteNum, String creatorId, String dlgUserId) throws QuoteException {
        LogContext log = LogContextFactory.singleton().getLogContext();
        dlgUserId = StringUtils.lowerCase(dlgUserId);        
        try {
            log.debug(this, "begin to remove quote delegation - quote num=" + webQuoteNum + " creatorId=" + creatorId
                    + " userId=" + dlgUserId);
            this.beginTransaction();
            HashMap parms = new HashMap();

            parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
            parms.put("piCreatorId", creatorId == null ? "" : creatorId);
            parms.put("piUserId", dlgUserId);

            this.excuteSp(CommonDBConstants.DB2_D_QT_DLGTN, parms);

            this.commitTransaction();
            log.debug(this, "completed removing quote delegation");
        } catch (RowExistingException re) {
            log.error(this, re.getMessage());
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

}
