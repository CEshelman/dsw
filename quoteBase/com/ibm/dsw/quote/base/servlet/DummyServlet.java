package com.ibm.dsw.quote.base.servlet;

import is.domainx.User;
import is.domainx.WebAuthUser;
import is.servlet.UserFactory;
import is.servlet.UserNotEntitledException;
import is.servlet.UserNotFoundException;
import is.webauth.service.proxy.WebAuthIntegrationInternalWrapperImpl;
import is.webauth.service.proxy.WebAuthIntegrationWrapper;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>DummyServlet</code> uses a dummy user to pass the authorization
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class DummyServlet extends SQOBaseServlet {

    private static final String SQO = "SQO";

    private static final String TLSORDER = "UTLS";

    private static final String PA_EORDER = "PACORDD";

    private static final String TLS_SAPNUMBER = "TLS0000000";

    private static final java.lang.String HTML_COUNTRY_CODE = "cntryCode";

    private static final java.lang.String HTML_ACCESS_LEVEL = "tsAccessLevel";

    private static final java.lang.String HTML_CONTRACT_NUMBER = "ctrctNumber";

    private static final java.lang.String HTML_CUSTOMER_NUMBER = "custNumber";

    private static final java.lang.String HTML_PORTAL_ID = "portal";

    private static final java.lang.String HTML_EMAIL_ADDRESS = "user";

    protected User createWebAuthUser(HttpServletRequest request, HttpServletResponse response)
            throws UserNotFoundException, UserNotEntitledException, IOException {
        DummyUser dummyuser = new DummyUser();
        WebAuthUser user = null;
        String userEmail = null;
        if(StringUtils.isNotBlank(getParam(request, HTML_COUNTRY_CODE)))
            dummyuser.setCntryCode(getParam(request, HTML_COUNTRY_CODE));
        if(StringUtils.isNotBlank(getParam(request, HTML_CONTRACT_NUMBER)))
            dummyuser.setContractNumber(getParam(request, HTML_CONTRACT_NUMBER));
        if(StringUtils.isNotBlank(getParam(request, HTML_EMAIL_ADDRESS)))
            userEmail = getParam(request, HTML_EMAIL_ADDRESS).toLowerCase();
            dummyuser.setEmail(userEmail);
        if(StringUtils.isNotBlank(getParam(request, HTML_PORTAL_ID)))
            dummyuser.setPortal(getParam(request, HTML_PORTAL_ID));
        if(StringUtils.isNotBlank(getParam(request, HTML_CUSTOMER_NUMBER)))
            dummyuser.setSAPNumber(getParam(request, HTML_CUSTOMER_NUMBER));

        if (userEmail == null || "".equals(userEmail)) {
            WebAuthIntegrationWrapper wrapper = new WebAuthIntegrationInternalWrapperImpl();
            return authenticationHelper.getUser(request, response, wrapper);
        } else {
            try {
                int level = Integer.parseInt(getParam(request, HTML_ACCESS_LEVEL));
                dummyuser.getUIApplicationAccess().put(SQO, new Integer(level));
                user = (WebAuthUser) UserFactory.createLoginUser(dummyuser.getValidUniqueID(userEmail), userEmail, dummyuser.getPortal());
                UserFactory.setApplicationAccessLevel(user, SQO, level);
                UserFactory.setApplicationAccessLevel(user, TLSORDER, level);
                UserFactory.setApplicationAccessLevel(user, PA_EORDER, 7);
                UserFactory.setCntryCode(user, dummyuser.getCntryCode());
                UserFactory.setEmail(user, dummyuser.getEmail());
                UserFactory.setSAPNumber(user, TLS_SAPNUMBER);
                UserFactory.setRole(user, "P");
                UserFactory.setEntitlementInd(user, "F");
                UserFactory.addEntitledPortal(user, "TLS");

                Cookie[] theCookies = UserFactory.getSecureCookie(user);
                if (theCookies != null){
    				for (int c= 0; c < theCookies.length; c++)
    				{
    					if (theCookies[c] != null) {
    					    response.addCookie(theCookies[c]);
    					}
    				}
                }
            } catch (Exception e) {
                LogContextFactory.singleton().getLogContext().error(this, e);
            }
            return user;
        }

    }

    /**
     * Gets a parameter from the <code>HttpServletRequest</code>
     *
     * @param request
     *            the active <code>HttpServletRequest</code>
     * @param paramName
     *            the parameter name
     * @return the parameter value as a string
     */
    private String getParam(HttpServletRequest request, String paramName) {
        Object object = request.getParameter(paramName);
        return (String) object;
    }
}
