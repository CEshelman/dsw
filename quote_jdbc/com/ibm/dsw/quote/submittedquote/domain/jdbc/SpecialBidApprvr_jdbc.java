package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.Domain;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

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
public class SpecialBidApprvr_jdbc extends SpecialBidApprvr_Impl implements PersistentObject {

    transient SpecialBidApprvrPersister persister;

    /**
     *  
     */
    public SpecialBidApprvr_jdbc() {
        super();
        persister = new SpecialBidApprvrPersister(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        //do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        //do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr#setWebQuoteNum(java.lang.String)
     */
    public void setWebQuoteNum(String webQuoteNum) throws Exception {
        super.setWebQuoteNum(webQuoteNum);
        markAsModified();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr#setSpecialBidApprGrp(java.lang.String)
     */
    public void setSpecialBidApprGrp(String specialBidApprGrp) throws Exception {
        super.setSpecialBidApprGrp(specialBidApprGrp);
        markAsModified();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr#setApprvrEmail(java.lang.String)
     */
    public void setApprvrEmail(String apprvrEmail) throws Exception {
        super.setApprvrEmail(apprvrEmail);
        markAsModified();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr#setApprvrAction(java.lang.String)
     */
    public void setApprvrAction(String apprvrAction) throws Exception {
        super.setApprvrAction(apprvrAction);
        markAsModified();
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr#setSpecialBidApprLvl(int)
     */
    public void setSpecialBidApprLvl(int specialBidApprLvl) throws Exception {
        super.setSpecialBidApprLvl(specialBidApprLvl);
        markAsModified();
    }
    
    public void markAsModified() throws Exception{
        super.markAsModified();
        if(getMode() == Domain.DOMAIN_MODE_PO){
            persister.setDirty();
        }
    }
    
    public void setMode(int mode) throws Exception{
        super.setMode(mode);
        if(mode == Domain.DOMAIN_MODE_PO && isModified()){
            persister.setDirty();
        }
    }

	public void setReturnReason(String returnReason) throws Exception {
		// TODO Auto-generated method stub
		super.setReturnReason(returnReason);
		markAsModified();
	}
}
