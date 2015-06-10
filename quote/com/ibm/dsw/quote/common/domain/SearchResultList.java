package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SearchResultList<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class SearchResultList implements Serializable {

    private static final int DEFAULT_PAGE_NUMBER = 1;

    private static final int DEFAULT_PAGE_SIZE = 100;

    protected transient List resultList;

    protected int resultCount;
    
    protected int availableCount;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int pageNumber = DEFAULT_PAGE_NUMBER;

    /**
     *  
     */
    public SearchResultList() {
        resultList = new ArrayList();
    }

    public SearchResultList(int resultCount, int pageSize, int pageNumber) {
        if (pageSize < 1 || resultCount < 0) {
            throw new IllegalArgumentException("pageSize must great than 1 and resultCount must great than 0");
        }
        this.pageSize = pageSize;
        this.resultCount = resultCount;
        this.resultList = new ArrayList();

        int lastPage = getLastPage();
        if (lastPage < pageNumber) {
            this.pageNumber = lastPage;
        } else {
            this.pageNumber = pageNumber;
        }
    }
    
    public SearchResultList(int resultCount, int pageSize, int pageNumber, int availableCount) {
        if (pageSize < 1 || resultCount < 0) {
            throw new IllegalArgumentException("pageSize must great than 1 and resultCount must great than 0");
        }
        this.pageSize = pageSize;
        this.resultCount = resultCount;
        this.resultList = new ArrayList();
        this.availableCount = availableCount;

        int lastPage = getLastPage();
        if (lastPage < pageNumber) {
            this.pageNumber = lastPage;
        } else {
            this.pageNumber = pageNumber;
        }
    }

    public void add(Object obj) {
        this.resultList.add(obj);
    }

    public void add(int index, Object obj) {
        this.resultList.add(index, obj);
    }

    public List getResultList() {
        return this.resultList;
    }

    public int getResultCount() {
        return this.resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(int availableCount) {
		this.availableCount = availableCount;
	}

	/**
     * @return Returns the pageSize.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return Returns the pageNumber.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    public int getRealSize() {
        return resultList.size();
    }

    /**
     * 
     * @return the first record number in this page
     */
    public int getPageFirstNumber() {
        return (this.getPageNumber() - 1) * this.getPageSize() + 1;
    }

    /**
     * 
     * @return the last record number in this page
     */
    public int getPageLastNumber() {
        return (pageNumber - 1) * this.getPageSize() + this.getRealSize();
    }

    public boolean isHasNext() {
        if (getResultCount() > getPageSize()) {
            return (getResultCount() - pageNumber * getPageSize()) > 0;
        } else {
            return false;
        }
    }

    public boolean isHasPre() {
        if (getResultCount() > getPageSize()) {
            return pageNumber > 1;
        } else {
            return false;
        }
    }

    public int getPre() {
        int p = pageNumber;
        if (p > 1) {
            return p - 1;
        } else {
            return 1;
        }

    }

    public int getNext() {
        int p = getPageNumber();
        return p + 1;
    }

    private int getLastPage() {
        return resultCount % pageSize == 0 ? resultCount / pageSize : resultCount / pageSize + 1;
    }
    
}
