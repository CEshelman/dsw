package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.customer.contract.AgrmntBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>AssignCreateAgrmntContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Oct 30, 2009
 */

public class AssignCreateAgrmntContract extends AgrmntBaseContract {
    
    protected String webCtrctId = null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        // Set lob as PA when assigning or creating new PA contract for existing customer.
        this.setLob(QuoteConstants.LOB_PA);
        
        verifyAgreementType();
        verifyAgreementNumber();
        verifyGovSiteType();
        verifyTransSVPLevel();
    }

    public String getWebCtrctId() {
        return webCtrctId;
    }
    
    public void setWebCtrctId(String webCtrctId) {
        this.webCtrctId = webCtrctId;
    }
    
    public int getWebCtrctIdAsInt() {
        int ctrctId = -1;
        
        try {
            ctrctId = Integer.parseInt(getWebCtrctId());
        } catch (NumberFormatException e) {
            ctrctId = -1;
        }
        return ctrctId;
    }
}
