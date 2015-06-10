package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerSearchResultList<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-11-1
 */

public class CustomerSearchResultList extends SearchResultList {
    
    protected String lob;

	/**
     * 
     */
    public CustomerSearchResultList() {
        super();
    }
    /**
     * @param resultCount
     * @param pageSize
     * @param pageNumber
     */
    public CustomerSearchResultList(int resultCount, int pageSize, int pageNumber) {
        super(resultCount, pageSize, pageNumber);
    }
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    /**
     * @param lob The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }
}
