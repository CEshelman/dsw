package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import com.ibm.dsw.quote.common.domain.QuoteTxt_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteTxt_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 16, 2008
 */

public class QuoteTxt_jdbc extends QuoteTxt_Impl implements PersistentObject, Serializable {
    
    transient QuoteTxtPersister persister;

    public QuoteTxt_jdbc() {
        super();
        persister = new QuoteTxtPersister(this);
    }

    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    public void isDeleted(boolean deleteState) throws TopazException {
        persister.isDeleted(deleteState);
    }

    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }
    
    public void setAddDate(Date addDate) throws TopazException {
        this.addDate = addDate;
        persister.setDirty();
    }
    
    public void setJustificationSectionId(String justificationSectionId) throws TopazException {
        this.justificationSectionId = justificationSectionId;
        persister.setDirty();
    }
    
    public void setModDate(Date modDate) throws TopazException {
        this.modDate = modDate;
        persister.setDirty();
    }
    
    public void setQuoteText(String quoteText) throws TopazException {
        this.quoteText = quoteText;
        persister.setDirty();
    }
    
    public void setQuoteTextId(int quoteTextId) throws TopazException {
        this.quoteTextId = quoteTextId;
        persister.setDirty();
    }
    
    public void setQuoteTextTypeCode(String quoteTextTypeCode) throws TopazException {
        this.quoteTextTypeCode = quoteTextTypeCode;
        persister.setDirty();
    }
    
    public void setUserEmail(String userEmail) throws TopazException {
        this.userEmail = userEmail;
        persister.setDirty();
    }
    
    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        persister.setDirty();
    }

	@Override
	public void setAddByUserName(String addByUserName) throws TopazException {
		this.addByUserName = addByUserName;
		persister.setDirty();
	}

	@Override
	public void setModByUserName(String modByUserName) throws TopazException {
		this.modByUserName = modByUserName;
		persister.setDirty();
	}
    
}
