package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.Set;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.SubmittedQuoteBaseViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="zgsun@cn.ibm.com">Owen Sun </a> <br/>
 * 
 * Creation date: Apr 5, 2007
 */

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NotMeetBidIterationView extends SubmittedQuoteBaseViewBean {
	
	private Set<String> bidIteratnReasons;
	@SuppressWarnings("unchecked")
	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        bidIteratnReasons = (Set<String>) params.getParameter(ParamKeys.PARMA_BID_ITERATN_REASONS);
	}


	public Set<String> getBidIteratnReasons() {
		return bidIteratnReasons;
	}


	public void setBidIteratnReasons(Set<String> bidIteratnReasons) {
		this.bidIteratnReasons = bidIteratnReasons;
	}


	/* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        // TODO Auto-generated method stub
        return null;
    }

}
