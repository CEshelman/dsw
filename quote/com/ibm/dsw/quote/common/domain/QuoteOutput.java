package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteOutput<code> class.
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Apr 22, 2009
 */
public class QuoteOutput implements Serializable {
    private String outputType;
    
    private String outputName;
    
    private transient List distributedTo;
    
    private transient List docIds = new ArrayList();
    
    public List getDocIds() {
		return docIds;
	}
	public void addDocId(String docId) {
		this.docIds.add(docId);
	}
	/**
     * @return Returns the distributedTo.
     */
    public List getDistributedTo() {
        return distributedTo;
    }
    /**
     * @param distributedTo The distributedTo to set.
     */
    public void setDistributedTo(List distributedTo) {
        this.distributedTo = distributedTo;
    }
   
    /**
     * @return Returns the outputName.
     */
    public String getOutputName() {
        return outputName;
    }
    /**
     * @param outputName The outputName to set.
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
    /**
     * @return Returns the outputType.
     */
    public String getOutputType() {
        return outputType;
    }
    /**
     * @param outputType The outputType to set.
     */
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }
}
