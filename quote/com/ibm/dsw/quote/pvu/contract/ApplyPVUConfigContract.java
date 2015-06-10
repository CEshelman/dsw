package com.ibm.dsw.quote.pvu.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.pvu.config.VUParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ApplyPVUConfigContract</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public class ApplyPVUConfigContract extends QuoteBaseContract {
    
    private String configNum;
    private String lineItemNum;
    /* (non-Javadoc)
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters, com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        this.configNum = parameters.getParameterAsString(VUParamKeys.CONFIG_NUM);
        this.lineItemNum = parameters.getParameterAsString(VUParamKeys.LINE_ITEM);
        super.load(parameters, session);
    }

    /**
     * @return Returns the configNum.
     */
    public String getConfigNum() {
        return configNum;
    }
    /**
     * @return Returns the lineItemNum.
     */
    public String getLineItemNum() {
        return lineItemNum;
    }
}
