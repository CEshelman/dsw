package com.ibm.dsw.quote.findquote.viewbean;

import java.util.Date;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteActionKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByIBMerContract;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByIBMerViewBean</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindByIBMerViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the ownerRoles.
     */
    public String[] getOwnerRoles() {
        return ((DisplayFindQuoteByIBMerContract) findContract).getOwnerRoles();
    }

    public String[] getOwnerRolesChecked() {
        String[] ownerRoles = this.getOwnerRoles();
        String[] roles = new String[5];
        for (int i = 0; i < 5; i++) {
            roles[i] = "0";
        }
        if ((ownerRoles != null) && (!ownerRoles[0].equalsIgnoreCase(""))) {
            for (int i = 0; i < ownerRoles.length; i++) {
                if (ownerRoles[i].equalsIgnoreCase("Creator"))
                    roles[0] = "1";
                else if (ownerRoles[i].equalsIgnoreCase("Editor"))
                    roles[1] = "1";
                else if (ownerRoles[i].equalsIgnoreCase("Reviewer"))
                    roles[2] = "1";
                else if (ownerRoles[i].equalsIgnoreCase("Approver_named"))
                    roles[3] = "1";
                else if (ownerRoles[i].equalsIgnoreCase("Approver_pending"))
                    roles[4] = "1";
            }
        }
        return roles;
    }

    public String[] getOwnerRolesNames() {
        String[] ownerRolesNames = null;
        if (this.getOwnerRoles() != null) {
            ownerRolesNames = new String[this.getOwnerRoles().length];
            for (int i = 0; i < this.getOwnerRoles().length; i++) {
                if (this.getOwnerRoles()[i].equalsIgnoreCase("Creator"))
                    ownerRolesNames[i] = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                            FindQuoteParamKeys.ROLE_CREATOR);
                else if (this.getOwnerRoles()[i].equalsIgnoreCase("Editor"))
                    ownerRolesNames[i] = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                            FindQuoteParamKeys.ROLE_EDITOR);
                else if (this.getOwnerRoles()[i].equalsIgnoreCase("Approver_named"))
                    ownerRolesNames[i] = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                            FindQuoteParamKeys.ROLE_APPROVER_NAMED);
                else if (this.getOwnerRoles()[i].equalsIgnoreCase("Approver_pending"))
                    ownerRolesNames[i] = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                            FindQuoteParamKeys.ROLE_APPROVER_PENDING);
                else if (this.getOwnerRoles()[i].equalsIgnoreCase("Reviewer"))
                    ownerRolesNames[i] = getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                            FindQuoteParamKeys.ROLE_REVIEWER);
            }
        }
        return ownerRolesNames;
    }

    /**
     * @return Returns the ownerType.
     */
    public String getOwnerType() {
        return ((DisplayFindQuoteByIBMerContract) findContract).getOwnerType();

    }

    public String getOwnerTypeName() {
        String ownerTypeName = "";
        if (this.getOwnerType() != null) {
            ownerTypeName = this.getOwnerType();

            if (this.getOwnerType().equalsIgnoreCase("0"))
                ownerTypeName = FindQuoteParamKeys.ASSIGNED_TO_ME;
            else if (this.getOwnerType().equalsIgnoreCase("1"))
                ownerTypeName = FindQuoteParamKeys.ASSIGNED_TO;
        }
        return ownerTypeName;
    }

    public String getOwnerNameOrEmail() {
        return ((DisplayFindQuoteByIBMerContract) findContract).getOwnerNameOrEmail();
    }

    public String getStatusTrackerSettingsUrl() {
        return HtmlUtil.getURLForAction(FindQuoteActionKeys.STATUS_TRACKER_SETTINGS);
    }
    
    public int getReloadInterval(){
        int interval = 0;
        try{
           interval = CacheProcessFactory.singleton().create().getTrackerRefreshInterval();
	    } catch (QuoteException e) {
	        LogContextFactory.singleton().getLogContext().error(this, e.getMessage());
	    }
	    return interval;
    }
    
    public String getDateStr(){
        return DateHelper.getDateByFormat(new Date(), "dd MMM yyyy, HH:mm a",findContract.getLocale());
    }
    
    public String getIBMerPageURL(){
        String ibmerURL = "";
        
        ibmerURL += "&" + FindQuoteParamKeys.OWNER_TYPE + "=" + this.getOwnerType();
		String[] ownerRoles = this.getOwnerRoles();
		if (ownerRoles != null){
		    ibmerURL += "&" + FindQuoteParamKeys.OWNER_ROLES + "=";
			for (int i = 0; i < ownerRoles.length; i++){
			    ibmerURL += ownerRoles[i] + ":";
			}
		}
		ibmerURL += "&" + FindQuoteParamKeys.OWNER_NAME_OR_EMAIL + "=" + this.getOwnerNameOrEmail();
		
		return ibmerURL;
        
    }
    
    public String getPrePageURL(){
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_IBMER");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getIBMerPageURL();
        
        return prePageURL;
    }
    
    public String getNextPageURL(){
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_IBMER");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getIBMerPageURL();
        
        return nextPageURL;
    }
    
    public String getChangeCriteriaURL() {
        String criteriaURL = HtmlUtil.getURLForAction("DISPLAY_FIND_QUOTE_BY_IBMER_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getIBMerPageURL();
        return criteriaURL;
    }
    
    public String getStatusTrackerActionUrl(){
        return HtmlUtil.getURLForAction(FindQuoteActionKeys.STATUS_TRACKER);
    }
}
