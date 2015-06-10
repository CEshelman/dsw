package com.ibm.dsw.quote.massdlgtn.contract;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.massdlgtn.config.MassDlgtnKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DlgtnSalesRepContract</code> class is a contract which hold
 * parameters for all Actions except DlgtnEntryAction
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class DlgtnSalesRepContract extends QuoteBaseContract {

    private String currentUserId;

    private String salesUserId;

    private String delegateUserId;

    private boolean isSalesManager;

    private String salesFullName;

    private transient List reportingSalesReps;

    public void load(Parameters parameters, JadeSession session) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();        
    	super.load(parameters, session);

        this.loadDataFromSession(session);

        this.delegateUserId = parameters.getParameterAsString(MassDlgtnKeys.Params.DELEGATE_USER_ID);
        this.salesUserId = parameters.getParameterAsString(MassDlgtnKeys.Params.SALES_USER_ID);
        this.salesFullName = parameters.getParameterAsString(MassDlgtnKeys.Params.SALES_USER_FULL_NAME);
        
        logContext.debug(this, "Current Sales is :" + this.currentUserId + "(" + this.isSalesManager + ")");
    }

    private void loadDataFromSession(JadeSession session) {
        // firstly get current sales rep
        LogContext logContext = LogContextFactory.singleton().getLogContext();	
        this.currentUserId = this.getUserId();

        // check if current sales rep is a manager
        QuoteUserSession quoteUserSession = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
        if (null != quoteUserSession) {
            this.reportingSalesReps = quoteUserSession.getReportingHierarchy();
            this.isSalesManager = (null != reportingSalesReps);
            logContext.debug(this,"is salesManager = "+this.isSalesManager);
        } else {
            // should be a error
            logContext.info(this, "quoteUserSession is null");
            this.isSalesManager = false;
        }

    }

    public boolean isValid() {
        
        return true;
    }

    /**
     * @return Returns the currentUserId.
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * @return Returns the delegateUserId.
     */
    public String getDelegateUserId() {
        return delegateUserId;
    }

    /**
     * @return Returns the salesUserId.
     */
    public String getSalesUserId() {
        return StringUtils.trim(salesUserId);
    }

    /**
     * @return Returns the isSalesManager.
     */
    public boolean isSalesManager() {
        return isSalesManager;
    }

    /**
     * @return Returns the salesFullName.
     */
    public String getSalesFullName() {
        return salesFullName;
    }

    /**
     * @return Returns the reportingSalesReps.
     */
    public List getReportingSalesReps() {
        return reportingSalesReps;
    }
}
