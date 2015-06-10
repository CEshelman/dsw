package com.ibm.dsw.quote.appcache.domain;

import java.io.Serializable;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CodeDescObj</code> class is POJO object holding Code/Desc style
 * data.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-6
 */
public interface CodeDescObj extends Comparable, Serializable{
    /**
     * @return Returns the code.
     */
    public abstract String getCode();

    /**
     * @return Returns the codeDesc.
     */
    public abstract String getCodeDesc();
    
    /**
     * @return Set the code.
     */
    public abstract void setCode(String code);

    /**
     * @return Set the codeDesc.
     */
    public abstract void setCodeDesc(String codeDesc);
}