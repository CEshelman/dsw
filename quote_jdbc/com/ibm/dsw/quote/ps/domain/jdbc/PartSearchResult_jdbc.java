package com.ibm.dsw.quote.ps.domain.jdbc;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.domain.PartSearchResult_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CustomerList_jdbc.java</code>
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class PartSearchResult_jdbc extends PartSearchResult_Impl implements PersistentObject {
    
    PartSearchResultPersister p;
    
    public PartSearchResult_jdbc() {
        
    }

    //for searching by num, desc
    public PartSearchResult_jdbc(String searchType, String searchString, String creatorId, String lob, String acqrtnCode, String countryCode, String audience) {
        p = new PartSearchResultPersister(this);
        this.searchType = searchType;
        this.searchString = searchString;
        this.creatorId = creatorId;
        this.lob = lob;
        this.acqrtnCode = acqrtnCode;
        this.countryCode = countryCode;
        this.audience = audience;
    }
    
    //for searching by saas num
    public PartSearchResult_jdbc(String searchType, String searchString, String lob, String audience) {
        p = new PartSearchResultPersister(this);
        this.searchType = searchType;
        this.searchString = searchString;
        this.lob = lob;
        this.audience = audience;
    }
    
    //for searching by prodCodes
    public PartSearchResult_jdbc(List prodCodes, String creatorId, String lob) {
        p = new PartSearchResultPersister(this);
        this.searchType = PartSearchParamKeys.SEARCH_PARTS_BY_PROD_CODE;
        this.searchProdCodes = prodCodes;
        this.creatorId = creatorId;
        this.lob = lob;
    }
    
    //for searching by LOB
    public PartSearchResult_jdbc(String creatorId, String lob) {
        p = new PartSearchResultPersister(this);
        this.searchType = PartSearchParamKeys.SEARCH_PARTS_BY_LOB;
        this.creatorId = creatorId;
        this.lob = lob;
    }
    
    //for saving selected parts
    public PartSearchResult_jdbc(List selectedPartNumbers, String creatorId, String searchString, String dataRetrievalType, String chrgAgrmtNum, String configrtnActionCode, String orgConfigId) {
        p = new PartSearchResultPersister(this);
        this.selectedPartNumbers = selectedPartNumbers;
        this.searchType = PartSearchParamKeys.SAVE_SELECTED_PARTS;
        this.creatorId = creatorId;
        this.searchString = searchString;
        this.dataRetrievalType = dataRetrievalType;
        this.chrgAgrmtNum = chrgAgrmtNum;
        this.orgConfigId = orgConfigId;
        this.configrtnActionCode = configrtnActionCode;
    }
    
    public String getSelectedPartsString() {
        StringBuffer sb = new StringBuffer();
        boolean flag = false;
        Iterator i = selectedPartNumbers.iterator();
        while(i.hasNext()) {
            if(flag) {
                sb.append(',' + ((String)i.next()).trim());
            } else {
                sb.append(((String)i.next()).trim());
                flag = true;
            }
        }
        
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection con) throws TopazException {
        p.hydrate(con);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection con) throws TopazException {
        p.isNew(true);
        p.isDeleted(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean arg0) throws TopazException {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean arg0) throws TopazException {
        // do nothing

    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.ps.domain.PartSearchResult#getSearchType()
     */
    public String getSearchType() {
        return searchType;
    }
    
    public List getSearchProdCodes() {
        return searchProdCodes;
    }
    
    public String getChrgAgrmtNum(){
    	return chrgAgrmtNum;
    }
    
    public String getOrgConfigId(){
    	return orgConfigId;
    }
    
    public String getConfigrtnActionCode(){
    	return configrtnActionCode;
    }
}
