package com.ibm.dsw.quote.ps.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.WwideProductCode;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * 
 * Copyright 2005 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CustomerList_Impl.java</code> Class implements CustomerList
 * Interface and realize the business functions
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class PartSearchResult_Impl implements PartSearchResult {

    private List parts;
    private int rowCount;

    /**
     * record search criteria, only 1 will be populated at a time
     */
    protected String searchString;
    protected List selectedPartNumbers;
    protected String description;
    protected String brand;
    protected String searchType = "";
    protected String creatorId;
    protected List searchProdCodes;
    protected int expandLevel;
    protected int exceedCode;
    protected String lob;
    protected String acqrtnCode;
    protected String countryCode;
    protected String audience;
    protected String dataRetrievalType;
    protected String chrgAgrmtNum;
    protected String configrtnActionCode;
    protected String orgConfigId;
    
    LogContext logger = LogContextFactory.singleton().getLogContext();
    
    public PartSearchResult_Impl() {
        parts = new ArrayList();
        rowCount = 0;
    }

    public int size() {
        return parts.size();
    }
    
    public int getPartCount() {
        return parts.size();
    }

    public void addPart(PartSearchPart part) {
        parts.add(part);
    }

    public List getParts() {
        return parts;
    }
    
    public void setParts(List parts) {
        this.parts = parts;
    }

    public void setPartsFromProdCodeList(String brandCode, List prodCodes) {
        PartSearchPart part;
        WwideProductCode prodCode;
        Iterator i = prodCodes.iterator();
        while(i.hasNext()) {
            part = new PartSearchPart("dummyID", "dummy desc");
            part.setBrandID(brandCode);
            part.setBrandDescription(brandCode + " description");
            prodCode = (WwideProductCode)i.next();
            part.setProductID(prodCode.getProductCode());
            part.setProductDescription(prodCode.getProductCodeDescription());
            part.setProdTypeID("dummytypeID");
            part.setProdTypeDescription("dummytype description");
            this.addPart(part);
        }
    }
    
    /**
     * go through the PartSearchResult just returned from db2
     * if the part is a controlled part, then lookup the portfolio description
     * from the cache against the portfolio code which came from db2
     * @param psResult
     */
    public void setPortfolioDescriptions() throws QuoteException {
        PartSearchPart part;
        CodeDescObj codeObj;
        String codeDescription = "";
        
        if(parts!=null) {
	        CacheProcess cp = CacheProcessFactory.singleton().create();
	        Iterator iterparts = parts.iterator();
	        while( iterparts.hasNext() ) {
	            part = (PartSearchPart)iterparts.next();
	            if(part.isPartControlled()) {
                    //Do not commit the following debug line uncommented because it will fill the log.  Use for local testing only.
		         	//logger.debug(this, "about to lookup by code: " + part.getPortfolioCode());
	                codeObj = cp.getProductPortfolioByCode(part.getPortfolioCode());
	                codeDescription = (codeObj!=null?codeObj.getCodeDesc():"");
                    //Do not commit the following debug line uncommented because it will fill the log.  Use for local testing only.
		         	//logger.debug(this, ", code description found:" + codeDescription + ":");
		            part.setPortfolioDescription(codeDescription);
	            }
	        }
        }
    }    

    /**
     * @return Returns the number used as criteria for search by num.
     */
    public String getSearchPartNumbers() {
        return searchString;
    }

    /**
     * @return Returns the number used as criteria for search by num.
     */
    public String getSearchDescription() {
        return searchString;
    }

    /**
     * @return Returns the number used as criteria for search by num.
     */
    public List getSelectedPartNumbers() {
        return selectedPartNumbers;
    }
    
    /**
     * @return Returns the searchBrand.
     */
    public String getSearchBrand() {
        return searchString;
    }
    /**
     * @param searchBrand The searchBrand to set.
     */
    public void setSearchBrand(String searchBrand) {
        this.searchString = searchBrand;
    }
    /**
     * @param searchDescription The searchDescription to set.
     */
    public void setSearchDescription(String searchDescription) {
        this.searchString = searchDescription;
    }
    /**
     * @param searchPartNumbers The searchPartNumbers to set.
     */
    public void setSearchPartNumbers(String searchPartNumbers) {
        this.searchString = searchPartNumbers;
    }
    
    /**
     * @return Returns the quoteNum.
     */
    public String getCreatorId() {
        return creatorId;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setCreatorId(String quoteNumber) {
        this.creatorId = quoteNumber;
    }
    /**
     * @param level - the level the tree is to be expanded
     * 100 = fully expanded
     * 1 = fully collapsed
     */
    public void setExpandLevel(int level) {
        this.expandLevel = level;
    }
    /**
     * sets expandLevel to 1, fully collapsed
     */
    public void setExpandLevelCollapsed() {
        this.expandLevel = TREE_FULLY_COLLAPSED;
    }
    /**
     * sets expandLevel to 100, fully expanded
     */
    public void setExpandLevelExpanded() {
        this.expandLevel = TREE_FULLY_EXPANDED;
    }
    /**
     * returns expandLevel
     */
    public int getExpandLevel() {
        return this.expandLevel;
    }
    public String getLob() {
        return this.lob;
    }
    
    
    public String getAcqrtnCode() {
        return acqrtnCode;
    }
    public String getAudience() {
        return audience;
    }
    public String getCountryCode() {
        return countryCode;
    }
    /**
     * @return Returns the exceedCode.
     */
    public int getExceedCode() {
        return exceedCode;
    }
    /**
     * @param exceedCode The exceedCode to set.
     */
    public void setExceedCode(int exceedCode) {
        this.exceedCode = exceedCode;
    }
    
    public String getSearchString() {
        return searchString;
    }
    
    public String getDataRetrievalType() {
        return dataRetrievalType;
    }
}
