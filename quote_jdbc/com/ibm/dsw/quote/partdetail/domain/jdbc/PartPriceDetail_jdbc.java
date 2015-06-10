/*
 * Created on 2007-4-5
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.Domain;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartPriceDetail_jdbc extends PartPriceDetail_Impl implements PersistentObject, Serializable {
    
    transient private Persister persister;
    
    private boolean loadCoPrerequsite;
    
    public PartPriceDetail_jdbc(){
        this(false);
    }
    
    public PartPriceDetail_jdbc(boolean loadCoPrerequsite){
        super();
        this.loadCoPrerequsite = loadCoPrerequsite;
        persister = new PartPriceDetailPersister(this);
    }

    
    public boolean isLoadCoPrerequsite() {
        return loadCoPrerequsite;
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
        // nothing needs to be persisted. 
        persister.persist(connection);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        // nothing will be deleted for this domain.
        persister.isDeleted(deleteState);
    }

    /* (non-Javadoc)
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        // will not be invoked anyway.
        persister.isNew(newState);
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
    
    public void setCountryCode(String countryCode)  throws Exception{
        super.setCountryCode(countryCode);
        markAsModified();
    }
    public void setCurrencyCode(String currencyCode) throws Exception{
        super.setCurrencyCode(currencyCode);
        markAsModified();
        
    }
    public void setLob(String lob)throws Exception {
        super.setLob(lob);
        markAsModified();
    }
    public void setBrandName(String brandName) throws Exception{
        super.setBrandName(brandName);
        markAsModified();
    }
    public void setChargeUnit(List chargeUnit) throws Exception{
        super.setChargeUnits(chargeUnit);
        markAsModified();
    }
    public void setCountry(String country) throws Exception{
        super.setCountry(country);
        markAsModified();
    }
    public void setCurrency(String currency) throws Exception{
        super.setCurrency(currency);
        markAsModified();
    }
    public void setEncryptionLevelCode(String encryptionLevelCode) throws Exception{
        super.setEncryptionLevelCode(encryptionLevelCode);
        markAsModified();
    }
    public void setMediaTypes(List mediaTypes) throws Exception{
        super.setMediaTypes(mediaTypes);
        markAsModified();
    }
    public void setPartDescription(String partDescription) throws Exception{
        super.setPartDescription(partDescription);
        markAsModified();
    }
    public void setPartGroups(List partGroups) throws Exception{
        super.setPartGroups(partGroups);
        markAsModified();
    }
    public void setPartLanguages(List partLanguages) throws Exception{
        super.setPartLanguages(partLanguages);
        markAsModified();
    }
    public void setPartNumber(String partNumber) throws Exception{
        super.setPartNumber(partNumber);
        markAsModified();
    }
    public void setPartType(String partType) throws Exception{
        super.setPartType(partType);
        markAsModified();
    }
    public void setPartVersion(String partVersion) throws Exception{
        super.setPartVersion(partVersion);
        markAsModified();
    }
    public void setPids(Map pids) throws Exception{
        super.setPids(pids);
        markAsModified();
    }
    public void setPoints(double points) throws Exception{
        super.setPoints(points);
        markAsModified();
    }
    public void setPriceEffectiveDateFrom(Date priceEffectiveDateFrom) throws Exception{
        super.setPriceEffectiveDateFrom(priceEffectiveDateFrom);
        markAsModified();
    }
    public void setPriceEffectiveDateTo(Date priceEffectiveDateTo) throws Exception{
        super.setPriceEffectiveDateTo(priceEffectiveDateTo);
        markAsModified();
    }
    public void setPriceLvlA(double priceLvlA) throws Exception{
        super.setPriceLvlA(priceLvlA);
        markAsModified();
    }
    public void setPriceLvlBL(double priceLvlBL) throws Exception{
        super.setPriceLvlBL(priceLvlBL);
        markAsModified();
    }
    public void setPriceLvlD(double priceLvlD) throws Exception{
        super.setPriceLvlD(priceLvlD);
        markAsModified();
    }
    public void setPriceLvlE(double priceLvlE) throws Exception{
       super.setPriceLvlE(priceLvlE);
        markAsModified();
    }
    public void setPriceLvlED(double priceLvlED) throws Exception{
        super.setPriceLvlED(priceLvlED);
        markAsModified();
    }
    public void setPriceLvlF(double priceLvlF) throws Exception{
        super.setPriceLvlF(priceLvlF);
        markAsModified();
    }
    public void setPriceLvlG(double priceLvlG) throws Exception{
        super.setPriceLvlG(priceLvlG);
        markAsModified();
    }
    public void setPriceLvlGV(double priceLvlGV) throws Exception{
        super.setPriceLvlGV(priceLvlGV);
        markAsModified();
    }
    public void setPriceLvlH(double priceLvlH) throws Exception{
        super.setPriceLvlH(priceLvlH);
        markAsModified();
    }
    public void setPriceLvlI(double priceLvlI) throws Exception{
        super.setPriceLvlI(priceLvlI);
        markAsModified();
    }
    public void setPriceLvlJ(double priceLvlJ) throws Exception{
        super.setPriceLvlJ(priceLvlJ);
        markAsModified();
    }
    public void setProductDistributionChannel(String productDistributionChannel) throws Exception{
        super.setProductDistributionChannel(productDistributionChannel);
        markAsModified();
    }
    public void setProductGroup(String productGroup) throws Exception{
        super.setProductGroup(productGroup);
        markAsModified();
    }
    public void setProductName(String productName) throws Exception{
        super.setProductName(productName);
        markAsModified();
    }
    public void setProductPlatforms(List productPlatforms) throws Exception{
        super.setProductPlatforms(productPlatforms);
        markAsModified();
    }
    public void setProductTradeName(String productTradeName) throws Exception{
        super.setProductTradeName(productTradeName);
        markAsModified();
    }
    public void setCoprerequisites(Map coprerequisites) throws Exception{
        super.setCoprerequisites(coprerequisites);
        markAsModified();
    }
    public void setPartCntrlDistDesc(String partCntrlDistDesc) throws Exception {
		super.setPartCntrlDistDesc(partCntrlDistDesc);
		markAsModified();
	}
    public void setProdSubGroupDesc(String prodSubGroupDesc) throws Exception {
		super.setProdSubGroupDesc(prodSubGroupDesc);
		markAsModified();
	}
    
    public void setPriceType(String priceType) throws Exception {
		super.setPriceType(priceType);
		markAsModified();
	}
}
