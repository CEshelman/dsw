package com.ibm.dsw.quote.ps.domain;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2005 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CustomerList.java</code> is the interface for the customer list
 * domain object
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 *  
 */
public interface PartSearchResult {
    
    public static final int TREE_FULLY_EXPANDED = 100;
    public static final int TREE_FULLY_COLLAPSED = 1;

    public List getParts();
    
    public void setParts(List parts);
    public void setPartsFromProdCodeList(String brandCode, List prodCodes);
    
    public int getPartCount();

    public String getSearchPartNumbers();
    
    public List getSelectedPartNumbers();
    
    public String getCreatorId();
    
    public String getSearchType();

    public String getSearchBrand();
    
    public String getSearchDescription();
    
    public void addPart(PartSearchPart part);
    public void setPortfolioDescriptions() throws QuoteException;
    
    public List getSearchProdCodes();
    
    public void setExpandLevel(int level);
    public void setExpandLevelCollapsed();
    public void setExpandLevelExpanded();
    public int getExpandLevel();
    
    public int getExceedCode() ;
    
    public void setExceedCode(int exceedCode) ;
}
