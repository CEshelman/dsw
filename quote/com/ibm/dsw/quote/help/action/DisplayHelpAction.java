package com.ibm.dsw.quote.help.action;

import com.ibm.dsw.quote.base.action.SimpleParameterAction;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayHelpAction</code> class is to present help & tutorial
 * screen.
 * 
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: Apr 23, 2007
 */
public class DisplayHelpAction extends SimpleParameterAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.SimpleParameterAction#getEObject(com.ibm.ead4j.jade.util.Parameters)
     */
    protected Object getEObject(Parameters params) throws QuoteException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.SimpleParameterAction#getState(com.ibm.ead4j.jade.util.Parameters)
     */
    protected String getState(Parameters params) {
        return StateKeys.STATE_DISPLAY_HELP;
    }

}
