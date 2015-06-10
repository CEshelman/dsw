package com.ibm.dsw.quote.appcache.domain;

import java.io.Serializable;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CodeDescObj_Impl</code> class is the abstract implementation of
 * CodeDescObj.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-6
 */
public abstract class CodeDescObj_Impl implements CodeDescObj, Serializable {

    public String code;

	public String codeDesc;

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @return Returns the codeDesc.
     */
    public String getCodeDesc() {
        return codeDesc;
    }
    
    /**
     * @return Set the code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Set the codeDesc.
     */
    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    /**
     *  
     */

    public String toString() {
        return "Code=" + this.getCode() + " Description=" + this.getCodeDesc();
    }
    
    public int compareTo(Object o) {
        CodeDescObj_Impl s = null;
		if (null == o)
			return -1;
		if (!(o instanceof CodeDescObj_Impl))
			return -1;
		s = (CodeDescObj_Impl) o;
		
        return this.getCodeDesc().compareTo(s.getCodeDesc());
    }
    
    public boolean equals(Object o){
    	CodeDescObj_Impl s = null;
    	if (null == o)
			return false;
    	if (!(o instanceof CodeDescObj_Impl))
    		return false;
    	s = (CodeDescObj_Impl) o;
    	return this.getCodeDesc().equals(s.getCodeDesc()) && this.getCode().equals(s.getCode()) ;
    }
}
