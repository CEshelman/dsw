/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Dec 9, 2013
 */
package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.util.CheckPersisterUtil;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * DOC class global comment. Detailled comment
 */
public class MonthlySoftwareConfiguration_jdbc extends MonthlySoftwareConfiguration_Impl implements PersistentObject,
        Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3205375736945327750L;

    transient Persister persister;

    private transient String userID;
    
    public MonthlySoftwareConfiguration_jdbc() {
		this.persister = new MonthlySoftwareConfigurationPersister(this);
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    @Override
    public void isDeleted(boolean deleteState) throws TopazException {
        persister.isDeleted(deleteState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    @Override
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    @Override
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    @Override
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /**
     * Getter for userID.
     * 
     * @return the userID
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * Sets the userID.
     * 
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Sets the persister.
     * 
     * @param persister the persister to set
     */
    public void setPersister(Persister persister) {
        this.persister = persister;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setServiceDateModType(com.ibm.dsw.quote.common
     * .domain.ServiceDateModType)
     */
    @Override
    public void setServiceDateModType(ServiceDateModType serviceDateModType) throws TopazException {
        CheckPersisterUtil.checkPersisterDirty(getServiceDateModType(), serviceDateModType, persister);
        this.serviceDateModType = serviceDateModType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setServiceDate(java.sql.Date)
     */
    @Override
    public void setServiceDate(Date serviceDate) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.serviceDate, serviceDate, persister);
        this.serviceDate = serviceDate;
    }

	public void setConfigrtnIdFromCa(String configrtnIdFromCa) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.configrtnIdFromCa, configrtnIdFromCa, persister);
		this.configrtnIdFromCa = configrtnIdFromCa;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setTermExtension(boolean)
     */
    @Override
    public void setTermExtension(boolean termExtension) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.termExtension, termExtension, persister);
    	this.termExtension = termExtension;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setConfigEntireExtended(boolean)
     */
    @Override
    public void setConfigEntireExtended(boolean configEntireExtended) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.configEntireExtended, configEntireExtended, persister);
    	this.configEntireExtended = configEntireExtended;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setEndDate(java.sql.Date)
     */
    @Override
    public void setEndDate(Date endDate) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.endDate, endDate, persister);
    	this.endDate = endDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setGlobalTerm(java.lang.Integer)
     */
    @Override
    public void setGlobalTerm(Integer globalTerm) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.globalTerm, globalTerm, persister);
    	this.globalTerm = globalTerm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setNeedRecalculate(boolean)
     */
    @Override
    public void setNeedReconfigFlag(boolean needReconfigFlag) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.needReconfigFlag, needReconfigFlag, persister);
    	this.needReconfigFlag = needReconfigFlag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration_Impl#setGlobalBillingFrequencyCode(java.lang.String)
     */
    @Override
    public void setGlobalBillingFrequencyCode(String globalBillingFrequencyCode) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.globalBillingFrequencyCode, globalBillingFrequencyCode, persister);
    	this.globalBillingFrequencyCode = globalBillingFrequencyCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.BasicConfiguration_Impl#setWebQuoteNum(java.lang.String)
     */
    @Override
    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.webQuoteNum, webQuoteNum, persister);
    	this.webQuoteNum = webQuoteNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.BasicConfiguration_Impl#setConfigrtnId(java.lang.String)
     */
    @Override
    public void setConfigrtnId(String configrtnId) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.configrtnId, configrtnId, persister);
    	this.configrtnId = configrtnId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.BasicConfiguration_Impl#setConfigrtnActionCode(java.lang.String)
     */
    @Override
    public void setConfigrtnActionCode(String configrtnActionCode) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.configrtnActionCode, configrtnActionCode, persister);
    	this.configrtnActionCode = configrtnActionCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.BasicConfiguration_Impl#setCotermConfigrtnId(java.lang.String)
     */
    @Override
    public void setCotermConfigrtnId(String cotermConfigrtnId) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.cotermConfigrtnId, cotermConfigrtnId, persister);
    	this.cotermConfigrtnId = cotermConfigrtnId;
    }
    
    @Override
	public int hashCode(){
    	int hash = 0;
    	if (this.webQuoteNum != null){
    		hash += webQuoteNum.hashCode()*1;
    	}
    	if (this.configrtnId != null){
    		hash += configrtnId.hashCode()*2;
    	}
    	return hash;
    }
    
    @Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof MonthlySoftwareConfiguration)) {
			return false;
		}

		final MonthlySoftwareConfiguration ppc = (MonthlySoftwareConfiguration) other;
        if (StringUtils.equals(this.getWebQuoteNum(), ppc.getWebQuoteNum())
                && (StringUtils.equals(this.getConfigrtnId(), ppc.getConfigrtnId()))) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration#setAddNewMonthlySWFlag(boolean)
	 */
	@Override
	public void setAddNewMonthlySWFlag(boolean addNewMonthlySWFlag) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.addNewMonthlySWFlag, addNewMonthlySWFlag, persister);	
		this.addNewMonthlySWFlag = addNewMonthlySWFlag;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration#delete()
	 */
	@Override
	public void delete() throws TopazException {
		isDeleted(true);
	}

	@Override
	public void setDirty() throws TopazException {
		this.persister.setDirty();
		
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration#setChrgAgrmtNum(java.lang.String)
     */
    @Override
    public void setChrgAgrmtNum(String chrgAgrmtNum) throws TopazException {
        CheckPersisterUtil.checkPersisterDirty(this.chrgAgrmtNum, chrgAgrmtNum, persister);
        this.chrgAgrmtNum = chrgAgrmtNum;
    }

}
