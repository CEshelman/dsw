package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration_Impl;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.util.CheckPersisterUtil;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

public class PartsPricingConfiguration_jdbc extends PartsPricingConfiguration_Impl implements PersistentObject, Serializable{


    /**
	 * 
	 */
	private static final long serialVersionUID = -1464935964324860450L;
	
	transient Persister persister;
	

	public PartsPricingConfiguration_jdbc(){
		persister = new PartsPricingConfigurationPersister(this);
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#delete()
     */
    @Override
	public void delete() throws TopazException {
        persister.isDeleted(true);
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

    

	

    
    @Override
	public int hashCode(){
    	int hash = 0;
    	if (this.ibmProdId != null){
    		hash += ibmProdId.hashCode()*1;
    	}
    	if (this.ibmProdIdDscr != null){
    		hash += ibmProdIdDscr.hashCode()*2;
    	}
    	if (this.configrtnId != null){
    		hash += configrtnId.hashCode()*3;
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
		if (!(other instanceof PartsPricingConfiguration)) {
			return false;
		}

		final PartsPricingConfiguration ppc = (PartsPricingConfiguration) other;
        if (StringUtils.equals(this.getIbmProdId(), ppc.getIbmProdId())
                && (StringUtils.equals(this.getIbmProdIdDscr(), ppc.getIbmProdIdDscr()))
                && (StringUtils.equals(this.getConfigrtnId(), ppc.getConfigrtnId()))) {
			return true;
		}
		return false;
	}
    
	@Override
	public void setWebQuoteNum(String webQuoteNum)  throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.webQuoteNum, webQuoteNum, persister);	
		this.webQuoteNum = webQuoteNum;
	}
	@Override
	public void setConfigrtnId(String configrtnId) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.configrtnId, configrtnId, persister);	
		this.configrtnId = configrtnId;
	}
	@Override
	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId)throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.configrtrConfigrtnId, configrtrConfigrtnId, persister);	
		this.configrtrConfigrtnId = configrtrConfigrtnId;
	}
	@Override
	public void setProvisngDays(Integer provisngDays) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.provisngDays, provisngDays, persister);	
		this.provisngDays = provisngDays;
	}
	@Override
	public void setUserID(String userID) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.userID, userID, persister);	
		this.userID = userID;
	}
	
	@Override
	public void setConfigrtnActionCode(String configrtnActionCode) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.configrtnActionCode, configrtnActionCode, persister);	
		this.configrtnActionCode = configrtnActionCode;
	}
	@Override
	public void setEndDate(Date endDate) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.endDate, endDate, persister);	
		this.endDate = endDate;
	}
	@Override
	public void setCotermConfigrtnId(String cotermConfigrtnId) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.cotermConfigrtnId, cotermConfigrtnId, persister);	
		this.cotermConfigrtnId = cotermConfigrtnId;
	}
	@Override
	public void setConfigrtnModDate(Date configrtnModDate) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.configrtnModDate, configrtnModDate, persister);	
		this.configrtnModDate = configrtnModDate;
	}

	@Override
	public void setServiceDateModType(ServiceDateModType serviceDateModType) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.serviceDateModType, serviceDateModType, persister);	
		this.serviceDateModType = serviceDateModType;
	}

	@Override
	public void setServiceDate(Date serviceDate) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.serviceDate, serviceDate, persister);	
		this.serviceDate = serviceDate;
	}

	@Override
	public void setTermExtension(boolean termExtension) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.termExtension, termExtension, persister);	
		this.termExtension = termExtension;
	}
	
	public void setProvisioningId(String provisioningId) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.provisioningId, provisioningId, persister);	
		this.provisioningId = provisioningId;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.PartsPricingConfiguration#setConfigEntireExtended(boolean)
     */
    @Override
    public void setConfigEntireExtended(boolean configEntireExtended) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.configEntireExtended, configEntireExtended, persister);	
    	this.configEntireExtended = configEntireExtended;
    }

}
