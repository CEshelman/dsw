package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustPrtnrTabContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-5
 */

public class CustPrtnrTabContract extends DraftQuoteBaseContract {
    
    private String uploadWarning;
    private String invalidPartsWarning;

    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }
    
    /**
     * @return Returns the uploadWarning.
     */
    public String getUploadWarning() {
        return uploadWarning;
    }
    
    /**
     * @param uploadWarning The uploadWarning to set.
     */
    public void setUploadWarning(String uploadWarning) {
        this.uploadWarning = uploadWarning;
    }
    
    
    public String getInvalidPartsWarning() {
        return invalidPartsWarning;
    }

    public void setInvalidPartsWarning(String invalidPartsWarning) {
        this.invalidPartsWarning = invalidPartsWarning;
    }
}
