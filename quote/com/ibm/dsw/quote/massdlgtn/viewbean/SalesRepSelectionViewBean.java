package com.ibm.dsw.quote.massdlgtn.viewbean;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.massdlgtn.config.MassDlgtnKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SalesRepSelectionViewBean</code> class is only for Sales rep
 * selection page
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class SalesRepSelectionViewBean extends BaseViewBean {

    boolean isSalesManager;
    
    private String selectSalesRepUrl;
    
    private String salesUserID;

    public void collectResults(Parameters params) throws ViewBeanException {
        isSalesManager = params.getParameterAsBoolean(MassDlgtnKeys.Params.IS_SALES_MANAGER);
        selectSalesRepUrl = HtmlUtil.getURLForAction(MassDlgtnKeys.Action.MASS_DLGTN_SELECT_SALES_REP);
        salesUserID = MassDlgtnKeys.Params.SALES_USER_ID;
    }

    /**
     * @return Returns the salesUserID.
     */
    public String getSalesUserID() {
        return salesUserID;
    }
    /**
     * @return Returns the selectSalesRepUrl.
     */
    public String getSelectSalesRepUrl() {
        return selectSalesRepUrl;
    }
    /**
     * @return Returns the isSalesManager.
     */
    public boolean isSalesManager() {
        return isSalesManager;
    }
}
