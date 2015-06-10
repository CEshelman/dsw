package com.ibm.dsw.quote.common.domain; 

import java.io.Serializable;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>PortfolioWithGovFlag<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: Nov 23, 2010
 */
public class PortfolioWithGovFlag implements Serializable {
    
    private transient CodeDescObj portfolio;
  
    private String govermentFlag;

    public CodeDescObj getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(CodeDescObj portfolio) {
        this.portfolio = portfolio;
    }

    public String getGovermentFlag() {
        return govermentFlag;
    }

    public void setGovermentFlag(String govermentFlag) {
        this.govermentFlag = govermentFlag;
    }
    
   

}
 