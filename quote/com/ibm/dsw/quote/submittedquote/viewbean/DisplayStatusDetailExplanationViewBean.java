/*
 * Created on Feb 13, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.domain.StatusExplanation;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author zhangln
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayStatusDetailExplanationViewBean extends BaseViewBean {
    private StatusExplanation explanation;
    
    public void collectResults(Parameters params) throws ViewBeanException {
    	super.collectResults(params);
    	explanation = (StatusExplanation)params.getParameter(SubmittedQuoteParamKeys.STATUS_DETAIL_EXPLANATION);
    }
    
    public StatusExplanation getStatusExplanation(){
        return explanation;
    }
}
