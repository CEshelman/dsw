package com.ibm.dsw.quote.submittedquote.domain;

import java.io.Serializable;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteAccess<code> class.
 *    
 * @author: zhangln@cn.ibm.com
 * 
 * Creation date: 2009-2-13
 */

public class StatusExplanation implements Serializable {
    
    private String statusCode; 
    private String statusDescription;
    private String explanation;
    private String resolutionAction;
    private String actionOwner;
    private String durtnIndctr;
    
    public StatusExplanation(){
    }
    
    /**
     * @return Returns the actionOwner.
     */
    public String getActionOwner() {
        return actionOwner;
    }
    /**
     * @param actionOwner The actionOwner to set.
     */
    public void setActionOwner(String actionOwner) {
        this.actionOwner = actionOwner;
    }
    /**
     * @return Returns the explanation.
     */
    public String getExplanation() {
        return explanation;
    }
    /**
     * @param explanation The explanation to set.
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    /**
     * @return Returns the resolutionAction.
     */
    public String getResolutionAction() {
        return resolutionAction;
    }
    /**
     * @param resolutionAction The resolutionAction to set.
     */
    public void setResolutionAction(String resolutionAction) {
        this.resolutionAction = resolutionAction;
    }
    /**
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }
    /**
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    /**
     * @return Returns the statusDescription.
     */
    public String getStatusDescription() {
        return statusDescription;
    }
    /**
     * @param statusDescription The statusDescription to set.
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
    
    /**
     * @return Returns the durtnIndctr.
     */
    public String getDurtnIndctr() {
        return durtnIndctr;
    }
    /**
     * @param durtnIndctr The durtnIndctr to set.
     */
    public void setDurtnIndctr(String durtnIndctr) {
        this.durtnIndctr = durtnIndctr;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("statusCode = ").append(statusCode).append("\n");  
        buffer.append("statusDescription = ").append(statusDescription).append("\n");  
        buffer.append("explanation = ").append(explanation).append("\n");  
        buffer.append("resolutionAction = ").append(resolutionAction).append("\n"); 
        buffer.append("actionOwner = ").append(actionOwner).append("\n");  
        buffer.append("durtnIndctr = ").append(durtnIndctr).append("\n");  
        return buffer.toString();
    }
}
