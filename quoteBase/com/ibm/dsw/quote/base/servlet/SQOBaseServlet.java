package com.ibm.dsw.quote.base.servlet;

import is.domainx.User;
import is.security.guard.WebGuard;
import is.servlet.UserNotEntitledException;
import is.servlet.UserNotFoundException;
import is.webauth.service.proxy.WebAuthIntegrationInternalWrapperImpl;
import is.webauth.service.proxy.WebAuthIntegrationWrapper;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.login.history.process.QuoteLoginHistoryProcess;
import com.ibm.dsw.quote.login.history.process.QuoteLoginHistoryProcessFactory;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SQOBaseServlet<code> class is the Gateway Servlet for the application.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Sept 16, 2011
 */
public class SQOBaseServlet extends BaseServlet {

	/**
	 * Pulls data from the launch page and creates a fake user
	 * 
	 * @param request
	 * @param response
	 * @throws is.servlet.UserNotFoundException
	 * @throws is.servlet.UserNotEntitledException
	 * @throws java.io.IOException
	 * @throws  
	 */
	protected User createWebAuthUser(HttpServletRequest request,
			HttpServletResponse response) throws UserNotFoundException,
			UserNotEntitledException, IOException {
		WebAuthIntegrationWrapper wrapper = new WebAuthIntegrationInternalWrapperImpl();
		User user = authenticationHelper.getUser(request, response,wrapper);
		// Get CSRFID from DB2 if exist or else generate new token and persist	
		String CSRFID = getCSRFID(user);
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute(WebCSRFGuard.USER_TOKEN_KEY, CSRFID);	
		}
		return user;
		
	}
	
	public String getCSRFID(User user){		
		// Get CSRFID from DB2 with the key
		QuoteLoginHistoryProcess loginHistory = null;
		try {
			logger.debug(this, "QuoteLoginHistoryProcessFactory before create");
			loginHistory = QuoteLoginHistoryProcessFactory.singleton().create();
			logger.debug(this, "QuoteLoginHistoryProcessFactory after create");
		} catch (QuoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		logger.debug(this, "mod date from user "+user.getModDate());
		logger.debug(this, "cookie date time value  "+user.getCookieDTime());
		
		Timestamp time = new Timestamp(Long.parseLong(user.getCookieDTime()+"000"));				
		String CSRFID = null;
		try {
			logger.debug(this, "time "+time);
			CSRFID = loginHistory.getCSRFID(user.getEmail().toLowerCase(), time);
			logger.debug(this, "existing CSRFID from DB "+CSRFID);
			
		} catch (QuoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( CSRFID == null || StringUtils.isBlank(CSRFID) ) {
			// Create a new CSRFID and store it into DB2
			CSRFID = WebCSRFGuard.generateUserToken(WebGuard.PARAM_APP_NAME, user.getEmail().toLowerCase());
			logger.debug(this, "CSRFID generate user token "+CSRFID);
			try {
				logger.debug(this, "persistCSRFID before");
				loginHistory.persistCSRFID(user.getEmail().toLowerCase(), CSRFID, time);
				logger.debug(this, "persistCSRFID after");
			} catch (QuoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return CSRFID;
	}
  
    public String getLoginAction(){
        return ActionKeys.SQOLOGIN;
    }


	@Override
	public String getAudienceCode() {
		return QuoteConstants.QUOTE_AUDIENCE_CODE_SQO;
	}
}
