package com.ibm.dsw.quote.rule;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RuleParameter.java</code>
 * 
 * @author: Jeff zhu
 * 
 * Creation date: 2009-4-9
 */
public class RuleParameter {
    private String paramName;
    private Object paramValue;
    
    public RuleParameter(String paramName, Object paramValue){
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
    
    /**
     * @return Returns the paramName.
     */
    public String getParamName() {
        return paramName;
    }
    /**
     * @param paramName The paramName to set.
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    /**
     * @return Returns the paramValue.
     */
    public Object getParamValue() {
        return paramValue;
    }
    /**
     * @param paramValue The paramValue to set.
     */
    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }
}
