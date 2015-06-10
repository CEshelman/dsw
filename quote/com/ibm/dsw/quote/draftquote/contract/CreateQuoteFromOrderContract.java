package com.ibm.dsw.quote.draftquote.contract; 

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>CreateQuoteFromOrderContract<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: Oct 18, 2010
 */
public class CreateQuoteFromOrderContract extends DraftQuoteBaseContract {
    private String orderNum;
    private String backToOrdHistRptUrlParam;
    
    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getBackToOrdHistRptUrlParam() {
        return backToOrdHistRptUrlParam;
    }

    public void setBackToOrdHistRptUrlParam(String backToOrdHistRptUrlParam) {
        this.backToOrdHistRptUrlParam = backToOrdHistRptUrlParam;
    }
    

}
 