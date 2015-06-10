package com.ibm.dsw.quote.findquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>LoadApprovalQueueContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-8-10
 */
public class LoadEvaluatorQueueContract extends DisplayEvaluatorQueueContract {
    
	public void load(Parameters parameters, JadeSession session) {
        this.loadFromRequest(parameters, session);
    }

}
