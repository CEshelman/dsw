package com.ibm.dsw.quote.findquote.contract;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindQuoteByIBMerContract</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindQuoteByIBMerContract extends FindQuoteContract {
    String ownerType = "";

    String ownerNameOrEmail = "";

    String email = "";

    String firstName = "";

    String lastName = "";

    String[] ownerRoles;

    String markIBMerDefault = "";

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
        this.setOwnerType(parameters.getParameterAsString(FindQuoteParamKeys.OWNER_TYPE));
        this.setOwnerNameOrEmail(parameters.getParameterAsString(FindQuoteParamKeys.OWNER_NAME_OR_EMAIL));
        this.setMarkIBMerDefault(parameters.getParameterAsString(FindQuoteParamKeys.MARK_IBMER_DEFAULT));
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
        if (sqoCookie == null)
            return;// Normally it never goes here.
        this.setOwnerType(QuoteCookie.getSubmittedOwner(sqoCookie));        
        List ownerRolesList = QuoteCookie.getSubmittedOwnerRoles(sqoCookie);
        ownerRoles = new String[ownerRolesList.size()];
        for (int i = 0; i < ownerRolesList.size(); i++) {
            ownerRoles[i] = (String) ownerRolesList.get(i);
        }
    }

    /**
     * @return Returns the markIBMerDefault.
     */
    public String getMarkIBMerDefault() {
        return markIBMerDefault;
    }

    /**
     * @param markIBMerDefault
     *            The markIBMerDefault to set.
     */
    public void setMarkIBMerDefault(String markIBMerDefault) {
        this.markIBMerDefault = markIBMerDefault;
    }

    /**
     * @return Returns the ownerNameOrEmail.
     */
    public String getOwnerNameOrEmail() {
        return notNullString(ownerNameOrEmail);
    }

    /**
     * @param ownerNameOrEmail
     *            The ownerNameOrEmail to set.
     */
    public void setOwnerNameOrEmail(String ownerNameOrEmail) {
        this.ownerNameOrEmail = ownerNameOrEmail;
        if (ownerNameOrEmail == null || ownerNameOrEmail.equals(""))
            return;
        else if (ownerNameOrEmail.indexOf("@") > 0)
            this.setEmail(notNullString(ownerNameOrEmail));
        else if (ownerNameOrEmail.indexOf(",") >= 0) {
            this.setLastName(notNullString(ownerNameOrEmail.substring(0, ownerNameOrEmail.indexOf(","))));
            this.setFirstName(notNullString(ownerNameOrEmail.substring(ownerNameOrEmail.indexOf(",") + 1,
                    ownerNameOrEmail.length())));
        } else
            this.setLastName(notNullString(ownerNameOrEmail));

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

    /**
     * @return Returns the ownerType.
     */
    public String getOwnerType() {
        return notNullString(ownerType);
    }

    /**
     * @param ownerType
     *            The ownerType to set.
     */
    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return notNullString(email);
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return notNullString(firstName);
    }

    /**
     * @param firstName
     *            The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return notNullString(lastName);
    }

    /**
     * @param lastName
     *            The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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

}
