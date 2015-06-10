package com.ibm.dsw.quote.findquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteActionKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayStatusTrackerContract;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
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
public class DisplayStatusTrackerSettingsViewBean extends DisplayFindViewBean {

    public Collection generateSortByOptions() {

        List sortByOptionList = new ArrayList();

        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.SELECT_FOLLOWING), "", this.getSortBy().equalsIgnoreCase("")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.DATE_SUBMITTED), "0", this.getSortBy().equalsIgnoreCase("0")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.CUSTOMER_NAME), "1", this.getSortBy().equalsIgnoreCase("1")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.RESELLER_NAME), "2", this.getSortBy().equalsIgnoreCase("2")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteParamKeys.OVERALL_STATUS), "3", this.getSortBy().equalsIgnoreCase("3")));
        sortByOptionList.add(new SelectOptionImpl(getI18NString(I18NBundleNames.FIND_QUOTE_BASE, locale,
                FindQuoteParamKeys.TOTAL_PRICE), "4", this.getSortBy().equalsIgnoreCase("4")));

        return sortByOptionList;
    }
    
    /**
     * @return Returns the ownerRoles.
     */
    public String[] getOwnerRoles() {
        return ((DisplayStatusTrackerContract) findContract).getOwnerRoles();
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
                else if (ownerRoles[i].equalsIgnoreCase("Approver"))
                    roles[2] = "1";
                else if (ownerRoles[i].equalsIgnoreCase("Reviewer"))
                    roles[3] = "1";
            }
        }
        return roles;
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
    
}
