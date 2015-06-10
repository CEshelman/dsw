package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteCustomerRelatedDocument<code> class.
 *    
 * @author: jfwei@cn.ibm.com
 * 
 * Creation date: May 11, 2010
 */
public class QuoteCustomerRelatedDocument implements Serializable {
    
    private String documentName;
    
    private String distributedTo;
    
    private boolean pending;
    
    /**
     * @return Returns the distributedTo.
     */
    public String getDistributedTo() {
        return distributedTo;
    }
    /**
     * @param distributedTo The distributedTo to set.
     */
    public void setDistributedTo(String distributedTo) {
        this.distributedTo = distributedTo;
    }
    
    /**
     * @return Returns the documentName.
     */
    public String getDocumentName() {
        return documentName;
    }
    /**
     * @param documentName The documentName to set.
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
   
    /**
     * @return Returns the pending.
     */
    public boolean isPending() {
        return pending;
    }
    /**
     * @param pending The pending to set.
     */
    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
