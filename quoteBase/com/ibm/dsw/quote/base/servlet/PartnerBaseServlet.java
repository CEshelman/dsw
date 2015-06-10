package com.ibm.dsw.quote.base.servlet;

import is.domainx.User;
import is.servlet.UserNotEntitledException;
import is.servlet.UserNotFoundException;
import is.webauth.service.proxy.WebAuthIntegrationWrapper;
import is.webauth.service.proxy.WebAuthIntegrationWrapperImpl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SQOBaseServlet.java<code> class is the Gateway Servlet for the application.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Sept 16, 2011
 */
public class PartnerBaseServlet extends BaseServlet {
	
	/**
	 * Pulls data from the launch page and creates a fake user
	 * 
	 * @param request
	 * @param response
	 * @throws is.servlet.UserNotFoundException
	 * @throws is.servlet.UserNotEntitledException
	 * @throws java.io.IOException
	 */
	protected User createWebAuthUser(HttpServletRequest request,
			HttpServletResponse response) throws UserNotFoundException,
			UserNotEntitledException, IOException {
		WebAuthIntegrationWrapper wrapper = new WebAuthIntegrationWrapperImpl();
		return authenticationHelperPGS.getUser(request, response,wrapper);
	}
	

    public String getLoginAction(){
        return ActionKeys.PARTNERLOGIN;
    }


	@Override
	public String getAudienceCode() {
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS;
	}
	
}
