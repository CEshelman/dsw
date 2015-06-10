/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.submittedquote.domain.ExecSummary_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 *
 * Created At: 2009-2-5
 */

public class ExecSummary_jdbc extends ExecSummary_Impl implements PersistentObject, Serializable{
	
    transient ExecSummaryPersister persister = null;
	
	public ExecSummary_jdbc(){
		super();
		persister = new ExecSummaryPersister(this);
	}
	
	/* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
    }
    
    
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }
    
    public void setWebQuoteNum(String webQuoteNum){
    	this.webQuoteNum = webQuoteNum;
    }
    
    public void setUserId(String userId){
    	this.userId = userId;
    }

	public void setRecmdtFlag(Boolean recmdtFlag) throws TopazException{
		this.recmdtFlag = recmdtFlag;
		persister.setDirty();
	}

	public void setPeriodBookableRevenue(Double periodBookableRevenue) throws TopazException{
		this.periodBookableRevenue = periodBookableRevenue;
		persister.setDirty();
	}

	public void setRecmdtText(String recmdtText) throws TopazException{
		this.recmdtText = recmdtText;
		persister.setDirty();
	}

	public void setExecSupport(String execSupport) throws TopazException {
		this.execSupport = execSupport;
		persister.setDirty();
	}

	public void setTermCondText(String termCondText) throws TopazException{
		this.termCondText = termCondText;
		persister.setDirty();
	}

	public void setBriefOverviewText(String briefOverviewText) throws TopazException {
		this.briefOverviewText = briefOverviewText;
		persister.setDirty();
	}
	
	public void setServiceRevenue(Double serviceRevenue) throws TopazException{
		this.serviceRevenue = serviceRevenue;
		persister.setDirty();
	}

}
