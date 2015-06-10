package com.ibm.dsw.quote.home.action;

import org.apache.commons.lang.StringUtils;

import is.domainx.User;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.home.contract.LoginContract;
import com.ibm.dsw.quote.home.process.LoginProcess;
import com.ibm.dsw.quote.home.process.LoginProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.jade.session.JadeSession;


/**
 * 
 * <p>Handles the login processing for this application</p>
 * 
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class LoginAction extends BaseContractActionHandler implements SessionKeys, StateKeys {

    private static final String 	TLS_PORTAL = "TLS";
        
    public LoginAction(){
        super();
    }
    
    public ResultBean execute( ProcessContract contract, JadeSession session, ResultHandler handler )
    {
        LoginContract loginContract = (LoginContract)contract;
        User webAuthUser = loginContract.getUser();
        logContext.debug( this, webAuthUser.toString() );
        SalesRep salesRep = null;
        try{
            LoginProcess process = LoginProcessFactory.singleton().create();
            //logUser method will populate salesRep with bluepage information, and persist the information to DB2
            if (webAuthUser.getEmail()== null) {
                logContext.info( this, "Can't get email address from webAuthUser!") ;
                
            }
            salesRep = process.logUser(webAuthUser.getEmail(),webAuthUser.getAccessLevel( QuoteConstants.APP_CODE_SQO));
            //create the QuoteUserSession object that we will save to the session
            QuoteUserSession quoteUserSession = new QuoteUserSession();
            quoteUserSession.setWebAuthUser(webAuthUser);
            quoteUserSession.setCountryCode( salesRep.getCountryCode() );
            quoteUserSession.setEmailAddress( StringUtils.lowerCase(salesRep.getEmailAddress()) );
            quoteUserSession.setFullName( salesRep.getFullName() );
            quoteUserSession.setPhoneNumber( salesRep.getPhoneNumber() );
            quoteUserSession.setReportingHierarchy( salesRep.getReportingReps() );
            quoteUserSession.setUp2ReportingHierarchy( salesRep.getUp2ReportingReps() );
            quoteUserSession.setSerialNumber( salesRep.getSerialNumber() );
            quoteUserSession.setCountryCode3( salesRep.getCountryCode3() );
            quoteUserSession.setFirstName( salesRep.getFirstName() );
            quoteUserSession.setLastName( salesRep.getLastName() );
            quoteUserSession.setNotesId( salesRep.getNotesId() );
            quoteUserSession.setFaxNumber( salesRep.getFaxNumber() );
            quoteUserSession.setAudienceCode(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO);
            
            boolean evaluatorFlag = process.evaluatorUser(webAuthUser.getEmail()).isEvaluator();
            if(evaluatorFlag){
            	session.setAttribute(SESSION_EVAL_USERROLE, QuoteConstants.EVAL_USER_FLAG);
            }
            
            logContext.debug(this, "LoginAction-" + quoteUserSession.toString());
            session.setAttribute( SESSION_QUOTE_USER, quoteUserSession );
        }catch( Exception ex ){
            logContext.error( this,  "The SalesRep was not successfully obtained from the Blue Pages API! SalesRep is " + webAuthUser.getEmail());
            
        }

        // redirect to landing page
        String redirectURL = HtmlUtil.getURLForAction("DISPLAY_QUOTE_HOME");
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        
        try{
            return handler.getResultBean(); 
        }catch( ResultBeanException rbe ){
            return handler.getUndoResultBean();
        }
    }

    /**
     * @param contract
     * @param handler
     * @return
     * @throws QuoteException
     * @throws ResultBeanException
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        return null;
    }
}
