package com.ibm.dsw.quote.draftquote.viewbean;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.customer.viewbean.AgrmntTypeViewAdapter;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DisplayAssignCreateAgrmntViewBean<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Oct 29, 2009
 */

public class DisplayAssignCreateAgrmntViewBean extends BaseViewBean {
    
    AgrmntTypeViewAdapter agrmntTypeView = null;
    protected boolean isGovTypeDisplay = false;
    
    public void collectResults(Parameters param) throws ViewBeanException {
        
        super.collectResults(param);
        
        agrmntTypeView = new AgrmntTypeViewAdapter();
        agrmntTypeView.collectResultsForCtrctCreate(param);
        
        isGovTypeDisplay = !agrmntTypeView.getCntryCode().equals(CustomerConstants.COUNTRY_USA)
							&& agrmntTypeView.isGovTypeDisplay();
    }
    
    public AgrmntTypeViewAdapter getAgrmntTypeView() {
        return agrmntTypeView;
    }

    public boolean isGovTypeDisplay() {    	
        return isGovTypeDisplay;
    }
    
    public boolean isDisplaySignatureMsg() {
        return agrmntTypeView.isDisplaySignatureMsg();
    }

}
