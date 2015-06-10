package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>TacticCode<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 12, 2007
 */

public class TacticCode implements Serializable{
    
    private String tacticCode;

    /**
     * @return Returns the tacticCode.
     */
    public String getTacticCode() {
        return tacticCode;
    }
    /**
     * @param tacticCode The tacticCode to set.
     */
    public void setTacticCode(String tacticCode) {
        this.tacticCode = tacticCode;
    }
}
