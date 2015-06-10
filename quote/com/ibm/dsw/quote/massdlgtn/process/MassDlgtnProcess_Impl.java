package com.ibm.dsw.quote.massdlgtn.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcess_Impl</code> class is the abstract implementation
 * of MassDlgtnProcess
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public abstract class MassDlgtnProcess_Impl extends TopazTransactionalProcess implements MassDlgtnProcess {
    LogContext log = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess#getDelegates(java.lang.String)
     */
    public List getDelegates(String salesUserID) throws QuoteException {
        List result = null;
        log.debug(this, "begin finding all delegates to user=" + salesUserID);
        try {
            this.beginTransaction();
            result = SalesRepFactory.singleton().findDelegatesBySalesRep(salesUserID);
            this.commitTransaction();
        } catch (TopazException e) {
            log.error(this, "Get delegates error :" + e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        log.debug(this, "completed finding all delegates");
        return result;
    }

    protected void populateBluePageInfo(SalesRep salesRep) throws TopazException {

        String intranetId = salesRep.getEmailAddress();
        BluePageUser user = null;
        try {
            user = BluePagesLookup.getBluePagesInfo(intranetId);
        } catch (Exception ex) {
            throw new TopazException("Could not get sales rep from BluePages!", ex);
        }

        if (null != user) {
            salesRep.setBluepageInformation(user.getCountryCode(), user.getFullName(), user.getLastName(), user
                    .getFirstName(), user.getPhoneNumber(), user.getFaxNumber(), user.getDirectReportsList(), user.getUp2LevelReportsList(), user
                    .getWIID(), user.getNotesId(), user.getBluePagesId());

        }

    }

}
