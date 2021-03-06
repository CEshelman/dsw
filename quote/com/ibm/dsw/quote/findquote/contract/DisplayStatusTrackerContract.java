package com.ibm.dsw.quote.findquote.contract;

import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayStatusTrackerContract</code> class is
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on May 10, 2007
 */
public class DisplayStatusTrackerContract extends FindQuoteContract {

    String[] ownerRoles;

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
        if (super.getFlag != null) {
            String ownerRolesString = parameters.getParameterAsString(FindQuoteParamKeys.OWNER_ROLES);
            if (StringUtils.isNotBlank(ownerRolesString)) {
                ownerRoles = ownerRolesString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN);
            }
        } else {
            this.setOwnerRoles(parameters.getParameterWithMultiValues(FindQuoteParamKeys.OWNER_ROLES));
        }
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        List ownerRolesList = QuoteCookie.getSubmittedOwnerRoles(sqoCookie);
        ownerRoles = new String[ownerRolesList.size()];
        for (int i = 0; i < ownerRolesList.size(); i++) {
            ownerRoles[i] = (String) ownerRolesList.get(i);
        }
        super.loadFromCookie(parameters, session);
    }

    /**
     * @return Returns the ownerRoles.
     */
    public String[] getOwnerRoles() {
        return ownerRoles;
    }

    /**
     * @param ownerRoles
     *            The ownerRoles to set.
     */
    public void setOwnerRoles(String[] ownerRoles) {
        this.ownerRoles = ownerRoles;
    }

    public void fillDefault() {
        if (StringUtils.isBlank(sortFilter)) {
            // TODO should replace by constants
            quoteTypeFilter = new String[] { "RNWLQUOTE", "SLSQUOTE" };
            LOBsFilter = new String[] { CustomerConstants.LOB_PPSS, CustomerConstants.LOB_PAE,
                    CustomerConstants.LOB_PA, CustomerConstants.LOB_FCT };
            ownerRoles = new String[] { "Creator" };
            statusFilter = new String[] { "QS001", "QS002", "QS003", "QS004", "QS006", "QS008" };
            timeFilter = "7";
            sortFilter = "0";
        }
    }
    
    protected Cookie getCookie(Parameters parameters) {
        return (Cookie) parameters.getParameter(ParamKeys.PARAM_STATUS_TRACKER_COOKIE);
    }

}
