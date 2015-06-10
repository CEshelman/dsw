package com.ibm.dsw.quote.base.servlet;

import is.domainx.User;
import is.servlet.AuthenticationHelper;
import is.servlet.AuthenticationHelperRegistration;
import is.servlet.UserNotEntitledException;
import is.servlet.UserNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.cache.CacheForceRefreshWork;
import com.ibm.dsw.quote.base.cache.CacheWork;
import com.ibm.dsw.quote.base.cache.QuoteCacheBootStrape;
import com.ibm.dsw.quote.base.cache.QuoteTopazCacheableFactoryHelper;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.NoUndoStateKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.UUIDHelper;
import com.ibm.ead4j.common.action.ActionHandler;
import com.ibm.ead4j.common.action.BaseActionHandlerAdapter;
import com.ibm.ead4j.common.bean.Message;
import com.ibm.ead4j.common.bean.MessageFactory;
import com.ibm.ead4j.common.bean.Result;
import com.ibm.ead4j.common.config.EAD4JBootstrapKeys;
import com.ibm.ead4j.common.util.Tracer;
import com.ibm.ead4j.jade.action.ActionHandlerNotFoundException;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.common.MessageUtil;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.HTMLKeys;
import com.ibm.ead4j.jade.config.MessageKeys;
import com.ibm.ead4j.jade.config.UploadKeys;
import com.ibm.ead4j.jade.servlet.JadeGatewayServlet;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.jade.util.UploadFile;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import commonj.timers.TimerManager;
import commonj.work.WorkManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>BaseServlet<code> class is the Gateway Servlet for the application.
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Apr 1, 2006
 */
public abstract class BaseServlet extends JadeGatewayServlet {

	protected transient AuthenticationHelper authenticationHelper = null;
	protected transient AuthenticationHelperRegistration authenticationHelperPGS = null;

	private String encoding = null;

	private static CacheWork cacheWork;
	LogContext logger = null;
	String actionKey = null;
	String secondActionKey = null ;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		logger = LogContextFactory.singleton().getLogContext();
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();

		actionKey = context.getConfigParameter(JADE_ACTION_KEY);
		secondActionKey = context.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
		
		if(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equals(this.getAudienceCode())){
			authenticationHelper = new AuthenticationHelper(servletConfig);
		}else if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(this.getAudienceCode())){
			authenticationHelperPGS = new AuthenticationHelperRegistration(servletConfig);
		}
		encoding = servletConfig.getInitParameter(ParamKeys.ENCODING);
		if(BaseServlet.isCacheAvailable() && "1".equals(servletConfig.getInitParameter("cache-init"))){
		    initializeCache();
		}
	}

	/**
     * @return
     */
    private static boolean isCacheAvailable() {
        ApplicationProperties properties = ApplicationProperties.getInstance();
        return properties.isCacheAvailable();
    }

    private void initializeCache(){
//		final LogContext logger = LogContextFactory.singleton().getLogContext();
		ApplicationProperties appProp = ApplicationProperties.getInstance();
		String timerManagerJNDI = appProp.getTimerManagerJNDI();
		String workManagerJNDI = appProp.getWorkManagerJNDI();
		try{
			
			QuoteCacheBootStrape.getInstance().initialize();
			
		    InitialContext ctx = new InitialContext();
			TimerManager tmgr = (TimerManager) ctx.lookup("java:comp/env/"+timerManagerJNDI);
			TimerManager tmgrForceRefresh = (TimerManager) ctx.lookup("java:comp/env/"+timerManagerJNDI);
			WorkManager wmgr = (WorkManager) ctx.lookup("java:comp/env/"+workManagerJNDI);
			cacheWork = new CacheWork(tmgr);
			wmgr.schedule(cacheWork);
			CacheForceRefreshWork forceRefreshWork = new CacheForceRefreshWork(tmgrForceRefresh);
			wmgr.schedule(forceRefreshWork);
			
		}catch(Exception e){
		    logger.error(this,"initializeCache \n" + LogThrowableUtil.getStackTraceContent(e));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ibm.ead4j.jade.servlet.JadeGatewayServlet#preService(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void preService(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

//		LogContext logger = LogContextFactory.singleton().getLogContext();
	    request.setCharacterEncoding(encoding);
		JadeSession session = createSession(request);
		
		if("SALESCON".equals(request.getParameter("channel"))){
			HashMap<String, String> dsjParameters = new HashMap<String, String>();
			dsjParameters.put("opptNum", request.getParameter("opptNum"));
			dsjParameters.put("opptOwnerMail", request.getParameter("opptOwnerMail"));
			dsjParameters.put("customerName", request.getParameter("customerName"));
			dsjParameters.put("resellerName", request.getParameter("resellerName"));
			dsjParameters.put("description", request.getParameter("description"));
			dsjParameters.put("customerID", request.getParameter("customerID"));
			dsjParameters.put("trialID", request.getParameter("trialID"));
			dsjParameters.put("cntryCode", request.getParameter("cntryCode"));
			session.setAttribute("dsjParameters", dsjParameters);
		}

		User user = (User)session.getAttribute(SessionKeys.SESSION_USER);
		if (user == null) {
			try {
				user = createWebAuthUser(request, response);
				session.setAttribute(SessionKeys.SESSION_USER, user);
				processLogin(request, response);
			} catch (UserNotFoundException notFoundException) {
				logger.error(this, "User is not found");
			} catch (UserNotEntitledException notEntitledException) {
				logger.error(this, notEntitledException);
				throw new ServletException("User is not entitled");
			}catch (Exception e){
				logger.error(this, e);
			    throw new ServletException("Login failed");
			}
		} else {
			try {
				if(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equals(this.getAudienceCode())){
					authenticationHelper.validateUser(request, response, user);
				}else if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(this.getAudienceCode())){
					authenticationHelperPGS.validateUser(request, response, user);
				}
			} catch (UserNotFoundException notFoundException) {
				session.invalidate();
				logger.error(this, "User is not found");
			} catch (UserNotEntitledException notEntitledException) {
				session.invalidate();
				logger.error(this, notEntitledException);
				throw new ServletException("User is not entitled");
			}catch (Exception e){
				logger.error(this, e);
			    throw new ServletException("Login failed");
			}
		}
		// Set userId to session
		String userId = (String)session.getAttribute(SessionKeys.SESSION_USER_ID);
		if(userId == null || "".equals(userId)){
			if(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equals(this.getAudienceCode())){
			    userId = user.getEmail().toLowerCase();
			}else if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(this.getAudienceCode())){
				userId = user.getUniqueID() + "-" + user.getSAPNumber();
			}
		    session.setAttribute(SessionKeys.SESSION_USER_ID, userId);
		    QuoteUserSession quoteUserSession = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
		    if(quoteUserSession!=null){
		    	quoteUserSession.setUserId(userId);
		    }
		}

		String timezoneOffset = request.getParameter(ParamKeys.PARAM_TIMEZONEOFFSET);
		if(timezoneOffset != null){
			session.setAttribute(ParamKeys.PARAM_TIMEZONEOFFSET,timezoneOffset);
		}

	}

	/**
	 * Pulls data from the launch page and creates a fake user
	 *
	 * @param request
	 * @param response
	 * @throws is.servlet.UserNotFoundException
	 * @throws is.servlet.UserNotEntitledException
	 * @throws java.io.IOException
	 */
	protected abstract User createWebAuthUser(HttpServletRequest request,
			HttpServletResponse response) throws UserNotFoundException,
			UserNotEntitledException, IOException ;


    public void destroy() {
        super.destroy();
//        final LogContext logger = LogContextFactory.singleton().getLogContext();
        if(BaseServlet.isCacheAvailable()){
            if(cacheWork != null){
            	//Stop the timer
                cacheWork.cancelTimer();
                //Close the cache and release memory
                QuoteTopazCacheableFactoryHelper.singleton().closeCache();
                }
        }
    }
    /**
     * Override the prepareUndo method to ignore those no undo states
     */
    protected void prepareUndo(HttpServletRequest req, HttpServletResponse res,
			String key, Result resultBean) {
        String state = resultBean.getState().getStateAsString();

        for (int i = 0; i < NoUndoStateKeys.noUndoStates.length; i++) {
            // use endWith to ignore 1_
            if (state.endsWith(NoUndoStateKeys.noUndoStates[i])) {
                return;
            }
        }
        super.prepareUndo(req, res, key, resultBean);
    }

    /**
     * Execute the SQO Login action handler
     */
    public void processLogin(HttpServletRequest req, HttpServletResponse res) throws ServletException,
            IOException, ActionHandlerNotFoundException {
//		LogContext logger = LogContextFactory.singleton().getLogContext();
		String actionCode = getLoginAction();
		ActionHandler aHandler = null;
		// create parameters object
		Parameters inParameters = this.createParameters(req, res);

		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();

		// retrieve ActionHandler from ApplicationContext
		aHandler = context.getActionHandler(actionCode);
		JadeSession jadeSession = this.getSession(req);

		if (aHandler != null) {
			Result resultBean = null;
			resultBean = ((BaseActionHandlerAdapter) aHandler)._execute(
			        inParameters,
			        jadeSession,
			        this.getClientInfo(req, res),
					this.getLocale(req));
			logger.debug(this, "Login Action Handler " + actionCode + " executed sucessfully!");

			//do not process the resultBean returned by login action handler
			//login action handler is only to do the Login Initialization
			//Jade will continue to execute the action

		} else {
			logger.info(this, "Login Action Handler  " + actionCode + " not found!");
		}
    }

    public abstract String getLoginAction();

    public abstract String getAudienceCode();

	/**
	 * This method handles all the requests. In the single servlet programming
	 * model, this servlet is used everywhere.
	 *
	 * @param req
	 *            javax.servlet.http.HttpServletRequest
	 * @param res
	 *            javax.servlet.http.HttpServletResponse
	 * @exception javax.servlet.ServletException
	 * @exception java.io.Exception
	 */
	public void processWithSession(HttpServletRequest req,
			HttpServletResponse res) throws ServletException,
			java.io.IOException, ActionHandlerNotFoundException {

//	    final LogContext logger = LogContextFactory.singleton().getLogContext();

	    boolean multipartRequest = ServletFileUpload.isMultipartContent(req);
		long timeStart = System.currentTimeMillis();

	    String uploadUUID = null;
	    if(multipartRequest){
	        uploadUUID = req.getParameter(ParamKeys.PARAM_UPLOAD_UUID);
	        logger.debug(this, "File upload UUID is ---------> "+uploadUUID);
	        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
	        String uploadDir = null;
	        String uploadDirConfig = context.getConfigParameter(UploadKeys.UPLOAD_FILE_DIR_KEY);
	        if (uploadDirConfig == null || uploadDirConfig.equals("")) {
	            uploadDir = context.getConfigParameter(EAD4JBootstrapKeys.EAD4J_CODEBASE_PATH_KEY);
	        } else {
	            uploadDir = uploadDirConfig;
	        }
	        File tmpDir = new File(uploadDir);

	        // Create a factory for disk-based file items
		    DiskFileItemFactory factory = new DiskFileItemFactory(0, tmpDir);

		    // Create a new file upload handler
		    ServletFileUpload upload = new ServletFileUpload(factory);

		    // Create a progress listener
    	    ProgressListener progressListener = null;
    	    if(uploadUUID != null && !"".equals(uploadUUID)){
    	        progressListener = new UploadProcessListener(uploadUUID, getServletContext());
    	        upload.setProgressListener(progressListener);
    	    }


                // Parse the request
            //List items = null;
            try {
                //items = upload.parseRequest(req);
                FileItemIterator iter = upload.getItemIterator(req);
                while(iter.hasNext()){
                    FileItemStream item = iter.next();
                    String fieldName = item.getFieldName();
                    InputStream stream = item.openStream();
                    if (item.isFormField()) {
        	            Object value = req.getAttribute(fieldName);
        	            if(value != null){
        	                String[] newvalue = null;
        	                if(value instanceof String[]){
        	                    String[] oldvalue = (String[])value;
        	                    newvalue = new String[oldvalue.length+1];
        	                    System.arraycopy(oldvalue, 0, newvalue, 0, oldvalue.length);
        	                    newvalue[oldvalue.length] = Streams.asString(stream);
        	                    value = null;
        	                }else{
        	                    newvalue = new String[2];
        	                    newvalue[0] = (String)value;
        	                    newvalue[1] = Streams.asString(stream);
        	                }
        	                req.setAttribute(fieldName, newvalue);
        	            }else{
        	                req.setAttribute(fieldName, Streams.asString(stream));
        	            }
        	        } else {
        	            if(item.getName()!=null && !"".equals(item.getName().trim())){
        	                String fileName = item.getName();
            	            int colonIndex = fileName.indexOf(":");
            	            int backslashIndex = fileName.lastIndexOf("\\");
            	            int slashIndex = fileName.lastIndexOf("/");
            	            if(colonIndex!=-1 && backslashIndex!=-1){
            	                fileName = fileName.substring(backslashIndex+1, fileName.length());
            	            }else if(slashIndex != -1){
            	                fileName = fileName.substring(slashIndex+1, fileName.length());
            	            }
            	            String contentType = item.getContentType();
            	            String tmpFileName = UUIDHelper.getUUID().toString();
            	            File localFile = new File(tmpDir, tmpFileName);
            	            FileOutputStream fos = new FileOutputStream(localFile);
            	            byte[] buffer = new byte[4096];
            	            int count = 0;
            	            while(-1 != (count = (stream.read(buffer)))){
            	                fos.write(buffer, 0, count);
            	            }
            	            fos.close();
            	            UploadFile uf = new UploadFile(fieldName, fileName, contentType, localFile);
            	            req.setAttribute(fieldName, uf);
        	            }
        	        }
                }
                if(uploadUUID!=null && !"".equals(uploadUUID)){
                    long[] progress = (long[])getServletContext().getAttribute(uploadUUID);
                    if(progress != null){
                        progress[0] = -2;
                        progress[1] = -2;
                    }
                }
            } catch (Exception e) {
                logger.error(this, e);
                if(uploadUUID!=null && !"".equals(uploadUUID)){
                    long[] progress = (long[])getServletContext().getAttribute(uploadUUID);
                    if(progress != null){
                        progress[0] = -2;
                        progress[1] = -2;
                    }
                }

            }
	    }


		String actionCode = null;
		String secondActionCode = null ;

		ActionHandler aHandler = null;
		ActionHandler secondActionHandler = null ;
		Result resultBean = null;
		// create parameters object
		Parameters inParameters = this.createParameters(req, res);
		Parameters inParamsForSecondAction = this.createParameters(req, res);

		ApplicationContext context = ApplicationContextFactory.singleton()
				.getApplicationContext();


		//retrieve action code using "jadeAction"
		actionCode = inParameters.getParameterAsString(actionKey);

		secondActionCode = inParamsForSecondAction.getParameterAsString(secondActionKey);

		if ((actionCode == null) || ("".equals(actionCode))) {
			//let's see if somone is using action mapping
			actionCode = processActionExtension(req, res);

			//if its still null, lets get the default
			if ((actionCode == null) || ("".equals(actionCode)))
				actionCode = this.getDefaultActionCode();
		}
		
		//instead by ActionTracer. 
		//this.logActionCodeAndQuoteNum(actionCode, secondActionCode, inParameters);

		// retrieve ActionHandler from ApplicationContext
		try {
		aHandler = context.getActionHandler(actionCode);
		} catch (ActionHandlerNotFoundException hnfe) {
//			LogContext logCtx = LogContextFactory.singleton().getLogContext();
			logger.error(this, "ActionHandlerNotFoundException |actionCode| " + actionCode);
			throw new ActionHandlerNotFoundException(hnfe);
		}
		JadeSession jadeSession = this.getSession(req);

		if (aHandler != null) {
			if (this.isDoubleClick(aHandler, jadeSession)) {
				this.includeJSP(req, res, context
						.getValueAsString(HTMLKeys.JADE_DOUBLECLICK_PAGE_KEY));
				return;
			}
			this.prepareDoubleClick(aHandler, jadeSession);
			
			//trace user action
			ActionTracer actionTracer = ActionTracer.createInstance(jadeSession, inParameters, actionCode, secondActionCode);
			actionTracer.traceStart();
			jadeSession.setAttribute("timeStart", timeStart);
			resultBean = ((BaseActionHandlerAdapter) aHandler)._execute(
					inParameters, jadeSession, this.getClientInfo(req, res),
					this.getLocale(req));
			//Check if we have second action
			Result actionOneResultBean = resultBean;
			if (secondActionCode != null && !secondActionCode.equals("")
			        && !resultBean.getState().getStateAsString().equals(
					context.getConfigParameter(JADE_UNDO_STATE_KEY))
					&& !resultBean.getState().getStateAsString().equals(StateKeys.STATE_GENERAL_ERROR)){
			    secondActionHandler = context.getActionHandler(secondActionCode);
				resultBean = ((BaseActionHandlerAdapter) secondActionHandler)._execute(
				        inParamsForSecondAction, jadeSession, this.getClientInfo(req, res),
						this.getLocale(req));
			    resultBean = mergeResultBean(actionOneResultBean,resultBean);
			}

			//Set Cookie to response
			Cookie quoteCookie = (Cookie)inParameters.getParameter(ParamKeys.PARAM_QUOTE_COOKIE);
			if(quoteCookie != null){
			    res.addCookie(quoteCookie);
			}
			Cookie statusTrackerCookie = (Cookie)inParameters.getParameter(ParamKeys.PARAM_STATUS_TRACKER_COOKIE);
			if(statusTrackerCookie != null){
			    res.addCookie(statusTrackerCookie);
			}

			if (resultBean == null) {
				// say this cannot be. sigh. normally only get here if the very
				// FIRST Jade action
				//	fails in some unexpected way. when that happens the UNDO is
				// not initialized
				//	correctly and can cause this. another reason that can cause
				// this is when the
				//	validation component validation without reason. the data map
				// sent to the validation
				//	component cannot be null.
				String message = MessageUtil
						.getString(MessageKeys.JADE_RESULT_BEAN_NULL_FROM_ACTIONHANDLER_KEY);
				Tracer.trace(this, message);
				Message messageBean = MessageFactory.singleton()
						.create(message);
				this.handleUndo(req, res, inParameters, messageBean);
			} else if (resultBean.getState().getStateAsString().equals(
					context.getConfigParameter(JADE_UNDO_STATE_KEY))) {
				if(null != secondActionCode && QuoteConstants.downloadActionsSet.contains(secondActionCode)){
					req.setAttribute("csrfUid", inParameters.getParameter("csrfUid"));
				}
				req.setAttribute("has_validate_errors", "true");
				this.handleUndo(req, res, inParameters, resultBean
								.getMessage());
			} else {
			    if (!res.isCommitted()) {
			        this.processResult(req, res, inParameters, (Result) resultBean);
			    }
			}
			//trace user action
			actionTracer.traceEnd();
		} else {
			String errorString = MessageUtil
					.getString(JADE_ACTION_NOT_FOUND_MESSAGE_KEY);
			this.logError(req, errorString);
			this.launchErrorPage(req, res, errorString);
		}
		if(uploadUUID!=null && !"".equals(uploadUUID)){
		    getServletContext().removeAttribute(uploadUUID);
		}
	}

	protected Parameters createParameters(HttpServletRequest req,
			HttpServletResponse res) {
		Parameters parameters = super.createParameters(req, res);
		parameters.addParameter(ParamKeys.PARAM_HTTP_REQUEST, req);
		return parameters;
	}
	
	/*//instead by ActionTracer.
	private void logActionCodeAndQuoteNum(String actionCode, String secondActionCode, Parameters inParameters)
	{
		try
		{
			String webQuoteNum = inParameters.getParameterAsString("webQuoteNum");
			if ( StringUtils.isEmpty(webQuoteNum) )
			{
				webQuoteNum = inParameters.getParameterAsString("quoteNum");
			}
			logger.error(this, "log action code: " + actionCode + ":" + secondActionCode + ":" + webQuoteNum);
		}
		catch ( Throwable t )
		{
			logger.error(this, "error in log action code:" + t.toString());
		}
	}
	*/
	
    /**
     * @return
     */
    private Result mergeResultBean(Result source, Result destination) {

//        final LogContext logger = LogContextFactory.singleton().getLogContext();

        if (source == null || source.getMessage() == null || source.getMessage().numberOfMessages() == 0
                || destination == null) {
            logger.debug(this, "Source message bean has no messages or destination result bean is null.");
            return destination;
        }

        if (destination.getMessage() == null || destination.getMessage().numberOfMessages() == 0) {
            logger.debug(this, "Destination message bean has no messages.");
            destination.setMessage(source.getMessage());
            return destination;
        }

        Message srcMsg = source.getMessage();
        Message dstMsg = destination.getMessage();

        if (dstMsg instanceof MessageBean) {

            logger.debug(this, "Destination message bean is instance of MessageBean.");
            MessageBean dstMsgBean = (MessageBean) dstMsg;

            Iterator sIt = srcMsg.getListIterator();
            if (sIt.hasNext()) {
                List msgList = new ArrayList();
                while (sIt.hasNext()) {
                    msgList.add(sIt.next());
                }
                dstMsgBean.addMessages(msgList);
            }

            Iterator sMIt = srcMsg.getMapIterator();
            while (sMIt.hasNext()) {
                Object msgObject = sMIt.next();
                if (msgObject instanceof com.ibm.ead4j.jade.bean.Message) {
                    com.ibm.ead4j.jade.bean.Message msg = (com.ibm.ead4j.jade.bean.Message) msgObject;
                    dstMsgBean.addKeyedMessage(msg);
                }
            }
        }

        return destination;
    }
}

class UploadProcessListener implements ProgressListener{

    private String id;

    private ServletContext context;

    private long[] progressData = new long[2];

    private long megaBytes = -1;

    public UploadProcessListener(String id, ServletContext context){
        this.id = id;
        this.context = context;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.fileupload.ProgressListener#update(long, long, int)
     */
    public void update(long pBytesRead, long pContentLength, int pItems) {

        long mBytes = pBytesRead / 1000;
        if (megaBytes == mBytes) {
            return;
        }
        megaBytes = mBytes;
        progressData[0] = pBytesRead;
        progressData[1] = pContentLength;
        if(context.getAttribute(id) == null){
            context.setAttribute(id, progressData);
        }
    }
}

/**
 * Trace user action to the log file.
 * @author lirui
 * @since  release 14.4
 */
class ActionTracer{
	private String action;
	private String secondAction;
	private String userId;
	private String quoteNum;

	private ActionTracer(){}

	/**
	 * create a ActionTracer instance.
	 * @param session
	 * @param inParameters
	 * @param action
	 * @param secondAction
	 * @return
	 */
	public static ActionTracer createInstance(JadeSession session, Parameters inParameters, String action, String secondAction){
		ActionTracer tracer = new ActionTracer();
		tracer.action = action;
		tracer.secondAction = (secondAction==null?"":secondAction);
		tracer.initUserId(session);
		tracer.initQuoteNum(inParameters);
		if(tracer.userId == null) tracer.userId = "";
		if(tracer.quoteNum == null) tracer.quoteNum = "";
		return tracer;
	}
	
	/**
	 * record action start log.
	 */
	public void traceStart(){
		trace("start");
	}
	/**
	 * record action end log.
	 */
	public void traceEnd(){
		trace("end");
	}
	private void trace(String indic){
		//userid|quoteNum|action1|action2|start[end]
		String msg = "#ActionTrace#|" + userId + "|" + quoteNum + "|" + action + "|" + secondAction + "|" + indic;
		LogContextFactory.singleton().getLogContext().error(this, msg);
	}
	
	//try to get web quote number from parameters
	private void initQuoteNum(Parameters inParameters) {
		try {
			quoteNum = inParameters.getParameterAsString("webQuoteNum");
			if (StringUtils.isEmpty(quoteNum)) {
				quoteNum = inParameters.getParameterAsString("quoteNum");
			}
		} catch (Throwable t) {	}
	}

	//try to get user id from session.
	private void initUserId(JadeSession session) {
		try {
			if (session != null)
				userId = (String) session
						.getAttribute(SessionKeys.SESSION_USER_ID);
		} catch (Exception e) {	}
	}
}
