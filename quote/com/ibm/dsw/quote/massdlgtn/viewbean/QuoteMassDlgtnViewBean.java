package com.ibm.dsw.quote.massdlgtn.viewbean;

import java.util.List;

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
 * The <code>QuoteMassDlgtnViewBean</code> class is the main view bean for
 * jsps, it contains following parts (1) sales user id (2) delegates : a list
 * composed of SalesRep object (3) isSalesManager: to identify if current user
 * is sales manager
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class QuoteMassDlgtnViewBean extends BaseViewBean {
    private String salesUserId;

    private transient List delegates;

    private boolean isSalesManager;

    private String salesFullName;
    
    //common keys 	
    private String Delegate_User_Key;
    
    private String SalesRep_User_Key;

    private String Sales_Full_Name_Key;

    //urls
    private String addDelegationUrl;
    
    private String removeDelegationUrl;
    
    private String showSalesRepSelectionUrl;
    

    public void collectResults(Parameters params) throws ViewBeanException {
        this.salesUserId = params.getParameterAsString(MassDlgtnKeys.Params.SALES_USER_ID);
        this.delegates = (List) params.getParameter(MassDlgtnKeys.Params.DELEGATES_LIST);
        this.isSalesManager = params.getParameterAsBoolean(MassDlgtnKeys.Params.IS_SALES_MANAGER);
        this.salesFullName = params.getParameterAsString(MassDlgtnKeys.Params.SALES_USER_FULL_NAME);
    	
        delegates = getDelegates();
        Delegate_User_Key = MassDlgtnKeys.Params.DELEGATE_USER_ID;
        SalesRep_User_Key = MassDlgtnKeys.Params.SALES_USER_ID;
        Sales_Full_Name_Key = MassDlgtnKeys.Params.SALES_USER_FULL_NAME;

        addDelegationUrl = HtmlUtil.getURLForAction(MassDlgtnKeys.Action.MASS_DLGTN_ADD_DELEGATE);
        removeDelegationUrl = HtmlUtil.getURLForAction(MassDlgtnKeys.Action.MASS_DLGTN_REMOVE_DELEGATE);
        showSalesRepSelectionUrl = HtmlUtil.getURLForAction(MassDlgtnKeys.Action.MASS_DLGTN_SHOW_SALES_SELECTION);
    }

    /**
     * @return Returns the addDelegationUrl.
     */
    public String getAddDelegationUrl() {
        return addDelegationUrl;
    }
    /**
     * @return Returns the delegate_User_Key.
     */
    public String getDelegate_User_Key() {
        return Delegate_User_Key;
    }
    /**
     * @return Returns the removeDelegationUrl.
     */
    public String getRemoveDelegationUrl() {
        return removeDelegationUrl;
    }
    /**
     * @return Returns the sales_Full_Name_Key.
     */
    public String getSales_Full_Name_Key() {
        return Sales_Full_Name_Key;
    }
    /**
     * @return Returns the salesRep_User_Key.
     */
    public String getSalesRep_User_Key() {
        return SalesRep_User_Key;
    }
    /**
     * @return Returns the showSalesRepSelectionUrl.
     */
    public String getShowSalesRepSelectionUrl() {
        return showSalesRepSelectionUrl;
    }
    /**
     * @return Returns the delegates.
     */
    public List getDelegates() {
        return delegates;
    }

    /**
     * @return Returns the salesUserId.
     */
    public String getSalesUserId() {
        return salesUserId;
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
}
