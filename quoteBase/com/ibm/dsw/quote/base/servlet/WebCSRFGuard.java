/*
 * @(#)WebCSRFGuard.java		03/27/2013
 * 
 * (C) Copyright by IBM Corporation
 * All Rights Reserved.
 *  
 * This software is the confidential and proprietary information
 * of the IBM Corporation. ("Confidential Information"). Redistribution
 * of the source code or binary form is not permitted without prior authorization
 * from the IBM Corporation.
 */
package com.ibm.dsw.quote.base.servlet;

import is.security.guard.ResponseWrapper;
import is.security.guard.WebGuard;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;


/**
 * WebCSRFGuard is to protect web applications against CSRF attack
 * 
 * Defense CSRF (Cross-site Request Forgery)
 * 1. Generate and cache a hash token for the authenticated user
 * 2. Inject the hash token into POST forms, side-effect URLs etc.
 * 3. Determine whether request is secure by checking request token
 * 
 * 
 * @author Aaron
 * @Author Thomas
 * 
 * Thomas	03-27-2013	Update to use StringBuffer with default capacity to avoid it expend automatically to solve OOM issue.
 */
public abstract class WebCSRFGuard extends WebGuard {
	public static final String USER_TOKEN_KEY = "csrfUid";

	public static final String PARAM_ACTIONS_DELIMITER = "|";
	
	public static final String PARAM_ACTION_KEY = "actionKey";
	
	public static final String PARAM_GUARDED_ACTIONS = "guardedActions";

	public static final String PARAM_UNGUARDED_ACTIONS = "unguardedActions";

	public static final String PARAM_REWRITE_IGNORED_ACTIONS = "rewriteIgnoredActions";
	
	public static final String PARAM_WEB_FORM_TAG_PATTERN = "webFormTagPattern";
	
	public static final String PARAM_WEB_FORM_ACTION_PATTERN = "webFormActionPattern";
	
	public static final String PARAM_SIDE_EFFECT_URL_PATTERN = "sideEffectUrlPattern";
	
	private String actionKey = "";
	
	private String webFormTagPattern = "";
	
	private String webFormActionPattern = "";
	
	private String sideEffectUrlPattern = "";
	
	private Map<String, Boolean> ACTIONS_GUARDED_OPTIONS = new HashMap<String, Boolean>();

	private Map<String, Boolean> ACTIONS_REWRITE_IGNORED = new HashMap<String, Boolean>();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		registerActions(getConfigParam(PARAM_GUARDED_ACTIONS), true);
		registerActions(getConfigParam(PARAM_UNGUARDED_ACTIONS), false);
		addRewriteIgnoredActions(getConfigParam(PARAM_REWRITE_IGNORED_ACTIONS));
		actionKey = getConfigParam(PARAM_ACTION_KEY);
		webFormTagPattern = getConfigParam(PARAM_WEB_FORM_TAG_PATTERN);
		webFormActionPattern = getConfigParam(PARAM_WEB_FORM_ACTION_PATTERN);
		sideEffectUrlPattern = getConfigParam(PARAM_SIDE_EFFECT_URL_PATTERN);
	}
	
	protected void registerActions(String actionsList, boolean guarded) {
		if (actionsList == null || actionsList.trim().length() == 0) {
			return;
		}
		StringTokenizer st = new StringTokenizer(actionsList, PARAM_ACTIONS_DELIMITER);
		while (st.hasMoreTokens()) {
			String actionCode = st.nextToken();
			actionCode = (actionCode == null) ? "" : actionCode.trim();
			if (actionCode.length() > 0) {
				ACTIONS_GUARDED_OPTIONS.put(actionCode, guarded);
				getFilterConfig().getServletContext().log(
						"Registered action [" + actionCode + "] with guarding option: " + guarded);
			}
		}
	}

	protected void addRewriteIgnoredActions(String actionList) {
		if (actionList == null || actionList.trim().length() == 0) {
			return;
		}
		StringTokenizer st = new StringTokenizer(actionList, PARAM_ACTIONS_DELIMITER);
		while (st.hasMoreTokens()) {
			String actionCode = st.nextToken();
			actionCode = (actionCode == null) ? "" : actionCode.trim();
			if (actionCode.length() > 0) {
				ACTIONS_REWRITE_IGNORED.put(actionCode, true);
				getFilterConfig().getServletContext().log("Registered response rewritten ignored action: " + actionCode);
			}
		}
	}
	
	/**
	 * Assumption: Servlet default action configured in web.xml is for reading only
	 * @param actionCode The action code to be executed
	 * @return true if the action is being guarded
	 */
	protected boolean isActionGuarded(String actionCode) {
		if (actionCode == null || actionCode.length() == 0) {
			return false;
		}
		Boolean guardedOption = ACTIONS_GUARDED_OPTIONS.get(actionCode);
		return guardedOption == null ? false : guardedOption.booleanValue();
	}

	/**
	 * For the action execution response, the filter will pass response rewrite
	 * @param actionCode The action code to be executed
	 * @return true if the action response is to be ignored
	 */
	protected boolean isActionRewriteIgnored(String actionCode) {
		if (actionCode == null || actionCode.length() == 0) {
			return false;
		}
		Boolean rewriteIgnored = ACTIONS_REWRITE_IGNORED.get(actionCode);
		return rewriteIgnored == null ? false : rewriteIgnored.booleanValue();
	}

	protected String getUserTokenKey() {
		return USER_TOKEN_KEY;
	}
	
	protected String getActionKey() {
		return actionKey;
	}
	
	protected String getWebFormTagPattern() {
		return webFormTagPattern;
	}

	protected String getWebFormActionPattern() {
		return webFormActionPattern;
	}

	protected String getSideEffectUrlPattern() {
		return sideEffectUrlPattern;
	}
	
	protected void refreshCachedUserToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String userToken) {
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			if (userToken == null) {
				httpSession.removeAttribute(getUserTokenKey());
			} else {
				httpSession.setAttribute(getUserTokenKey(), userToken);
			}
		}
	}
	
	protected String readCachedUserToken(HttpServletRequest httpRequest) {
		String sessionUserToken = null;
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			sessionUserToken = (String)httpSession.getAttribute(getUserTokenKey());
		}
		return sessionUserToken;
	}
	
	protected void clearCachedUserToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			httpSession.removeAttribute(getUserTokenKey());
		}
	}
	
	protected String getRequestAction(ServletRequest request) throws ServletException {
		return request.getParameter(getActionKey());
	}
	
	@Override
	protected boolean isRequestGuarded(ServletRequest request) throws ServletException {
		return isActionGuarded(getRequestAction(request));
	}

	@Override
	protected boolean isSecureToServe(ServletRequest request) throws ServletException {
		boolean secureRequest = false;
		String cachedUserToken = readCachedUserToken((HttpServletRequest)request);
		if (cachedUserToken != null && cachedUserToken.length() > 0) {
			String requestUserToken = request.getParameter(getUserTokenKey());
			secureRequest = cachedUserToken.equals(requestUserToken);
			if (!secureRequest) {
				getFilterConfig().getServletContext().log("Insecure request with token [" 
						+ requestUserToken + "] different from cached token [" 
						+ cachedUserToken + "] for user: " + getAuthenticatedUserId((HttpServletRequest)request));
			}
		} else {
			secureRequest = true;
		}
		return secureRequest;
	}

	protected abstract String getAuthenticatedUserId(HttpServletRequest httpRequest);

	protected String getRequestFinalAction(ServletRequest request) throws ServletException {
		return getRequestAction(request);
	}

	protected boolean isRewriteResponseRequired(ServletRequest request) throws ServletException {
		return !isActionRewriteIgnored(getRequestFinalAction(request));
	}
	
	protected void serveRequest(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		String authedUserId = getAuthenticatedUserId(httpRequest);
		authedUserId = (authedUserId == null) ? "" : authedUserId;
		String prevSessionId = getHttpSessionId(httpRequest);
		
		ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);
		responseWrapper.setCharacterEncoding(getEncoding());

		boolean rewriteResponse = isRewriteResponseRequired(request);
		if (rewriteResponse) {
			filterChain.doFilter(request, responseWrapper);
		} else {
			filterChain.doFilter(request, response);
		}
		
		ServletContext servletContext = getFilterConfig().getServletContext();
		String userId = getAuthenticatedUserId(httpRequest);
		userId = (userId == null) ? "" : userId;
		String userToken = readCachedUserToken(httpRequest);
		
		if (!userId.equals(authedUserId) || userToken == null) {
		 	 String currSessionId = getHttpSessionId(httpRequest);
		     servletContext.log(getAppName()+"   Cached token [" + userToken + "] for user: " + authedUserId + "; session: " + prevSessionId);
//		     userToken = generateUserToken(getAppName(), userId);
		     userToken = getUserToken(getAppName(),userId,authedUserId,userToken);
		     servletContext.log("Refresh token [" + userToken + "] for user: " + userId + "; session: " + currSessionId);
		     refreshCachedUserToken(httpRequest, httpResponse, userToken);
		}

		if (rewriteResponse) {
			String injectedContent = rewriteResponseContent(request, responseWrapper.getContent(), userToken);
			response.setContentLength(-1);
			PrintWriter out = response.getWriter();
			out.write(injectedContent);
			out.flush();
			out.close();
		}
	}
	
	protected String rewriteResponseContent(ServletRequest request, String responseContent, String userToken) {
		String injectedContent = responseContent;

		if (isWebFormTagEnabled() || isWebFormActionEnabled()) {
			injectedContent = injectWebFormTag(injectedContent, userToken);
		}
		
		if (isSideEffectUrlEnabled()) {
			injectedContent = injectSideEffectUrl(injectedContent, userToken);
		}
		
		return injectedContent;
	}
	
	protected String injectWebFormTag(String responseContent, String userToken) {
		if (userToken != null && userToken.length() > 0) {
			String hiddenInput = "<input type=\"hidden\" name=\"" + getUserTokenKey() 
							+ "\" value=\"" + userToken	+ "\" />";
			String queryString = getUserTokenKey() + "=" + userToken;
			StringBuffer sb = new StringBuffer(responseContent.length() + 1024);
			sb.append(responseContent);
			int formStart = getFormStartIndex(sb, 0);
			int formEnd = formStart;
			while (formStart > 0) {
				formEnd = getFormEndIndex(sb, formStart) + 1;
				String formDef = sb.substring(formStart, formEnd);
				boolean formWithAction = (formDef.indexOf(getActionStartingChars()) >= 0);
				if (shouldInjectAsActionQS(formDef) && formWithAction) {
					int actionStart = formDef.indexOf(getActionStartingChars()) + getActionStartingChars().length();
					int actionEnd = formDef.indexOf(getActionEndingChars(), actionStart);
					String actionUrl = formDef.substring(actionStart, actionEnd);
					sb.insert(formStart + actionEnd, formatQueryString(actionUrl, queryString));
				} else if (isWebFormTag(formDef)){
					sb.insert(formEnd, hiddenInput);
				}
				formStart = getFormStartIndex(sb, formEnd);
			}
			String result = sb.toString();
			sb = null;
			responseContent = null;
			return result;
		}
		else{
			return responseContent;
		}
	}
	
	protected String getFormStartingChars() {
		return "<form";
	}
	
	protected String getFormEndingChars() {
		return ">";
	}
		
	protected String getActionStartingChars() {
		return "action=\"";
	}
	
	protected String getActionEndingChars() {
		return "\"";
	}
	
	protected String formatQueryString(String uri, String queryString) {
		String formattedQueryString = queryString;
		if (uri.contains("?")) {
			formattedQueryString = "&amp;" + queryString;
		} else {
			formattedQueryString = "?" + queryString;
		}
		return formattedQueryString;
	}
	
	protected int getFormStartIndex(StringBuffer content, int start) {
		return content == null ? -1 : content.indexOf(getFormStartingChars(), start);
	}

	protected int getFormEndIndex(StringBuffer content, int start) {
		return content == null ? -1 : content.indexOf(getFormEndingChars(), start);
	}
	
	protected boolean isWebFormTag(String formDef) {
		return isWebFormTagEnabled() && formDef != null && formDef.matches(getWebFormTagPattern());
	}
	
	protected boolean isWebFormTagEnabled() {
		return getWebFormTagPattern() != null && getWebFormTagPattern().length() > 0;
	}
	
	protected boolean isWebFormActionEnabled() {
		return getWebFormActionPattern() != null && getWebFormActionPattern().length() > 0;
	}
	
	protected boolean shouldInjectAsActionQS(String formDef) {
		if (!isWebFormActionEnabled()) {
			return false;
		}
		return formDef != null && formDef.contains(getActionStartingChars()) && formDef.matches(getWebFormActionPattern());
	}
	
	protected String injectSideEffectUrl(String responseContent, String userToken) {
		if (userToken != null && userToken.length() > 0) {
			String queryString = getUserTokenKey() + "=" + userToken;
			StringBuffer sb = new StringBuffer(responseContent.length() + 512);
			sb.append(responseContent);
			int hrefStart = getHrefStartIndex(sb, 0);
			int hrefEnd = hrefStart;
			while (hrefStart > 0) {
				hrefEnd = getHrefEndIndex(sb, hrefStart);
				String hrefDef = sb.substring(hrefStart, hrefEnd);
				if (isSideEffectUrl(hrefDef)) {
					sb.insert(hrefEnd, formatQueryString(hrefDef, queryString));
				}
				hrefStart = getHrefStartIndex(sb, hrefEnd);
			}
			String result = sb.toString();
			sb = null;
			responseContent = null;
			
			return result;
		}
		else{
			return responseContent;
		}
	}

	protected String getHrefStartingChars() {
		return "href=\"";
	}
	
	protected String getHrefEndingChars() {
		return "\"";
	}
	
	protected String getHrefJSStartingChars() {
		return "href='";
	}
	
	protected String getHrefJSEndingChars() {
		return "'";
	}
	
	protected int getHrefStartIndex(StringBuffer content, int start) {
		if (content == null) {
			return -1;
		}
		int startIndex1 = content.indexOf(getHrefStartingChars(), start);
		int startIndex2 = content.indexOf(getHrefJSStartingChars(), start);
		int startIndex = -1;
		if (startIndex1 < 0) {
			startIndex = startIndex2;
		} else if (startIndex2 < 0) {
			startIndex = startIndex1;
		} else {
			startIndex = (startIndex1 < startIndex2) ? startIndex1 : startIndex2;
		}
		return startIndex;
	}

	protected int getHrefEndIndex(StringBuffer content, int start) {
		if (content == null) {
			return -1;
		}
		int endIndex = -1;
		if (stringBufferStartsWith(content, getHrefStartingChars(), start)) {
			endIndex = content.indexOf(getHrefEndingChars(), start + getHrefStartingChars().length());
		} else if (stringBufferStartsWith(content, getHrefJSStartingChars(), start)) {
			endIndex = content.indexOf(getHrefJSEndingChars(), start + getHrefJSStartingChars().length());
		}
		return endIndex;
	}
	protected boolean stringBufferStartsWith(StringBuffer buff, String str, int start)
	{
		return buff.indexOf(str, start) == start;
	} 
	protected boolean isSideEffectUrl(String urlDef) {
		return isSideEffectUrlEnabled() && urlDef != null && urlDef.matches(getSideEffectUrlPattern());
	}
	
	protected boolean isSideEffectUrlEnabled() {
		return getSideEffectUrlPattern() != null && getSideEffectUrlPattern().length() > 0;
	}
	
	public static String getHttpSessionId(HttpServletRequest httpRequest) {
		HttpSession httpSession = httpRequest.getSession(false);
		return (httpSession == null) ? null : httpSession.getId();
	}
	
	/**
	 * Generate a unique hash token for the application user
	 * @param appName Any string specific to a web application
	 * @param userId User id that has been authenticated
	 * @return the generated unique hash token
	 */
	public static String generateUserToken(String appName, String userId) {
		StringBuffer sb = new StringBuffer("ut4crsf");
		UUID uid = UUID.randomUUID();
		sb = sb.append(String.valueOf(uid)).append(appName == null ? "" : appName)
				.append(userId == null ? "" : userId).append(System.currentTimeMillis());
		return shaToHex(sb.toString());
	}
	
	
	public static String getUserToken(String appName,String userId,String authedUserId,String userToken){
		String usrToken = userToken;
		// If auth User Id is not blank and userToken is null then only generate a new token or else use the existing token
    	if (!StringUtils.isBlank(authedUserId) || usrToken == null){    		
		       usrToken = generateUserToken(appName,userId);
	    }
		return usrToken;		
	}
	
	
}