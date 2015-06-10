package com.ibm.dsw.quote.home.contract;

import is.domainx.User;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class LoginContract extends QuoteBaseContract {
    
    /** the logging framework */
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    /** the user object from the session */
    private User user;
    
    /**
     * Constructor
     */
    public LoginContract() {
        super();
    }
    /**
     *  Gets the user
     * @return Returns the user.
     */
    public User getUser() {
        return user;
    }
    /**
     * Sets the user
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("=== Login Process Contract (debug) ====");
        buffer.append( (user!=null ? user.toString() : " user object is null!!!" ) );
        if (user != null){
            buffer.append( " Access Level : " + user.getAccessLevel(QuoteConstants.APP_CODE_SQO));
        }
        buffer.append("==========================");
        return buffer.toString();
    }
    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters, com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(com.ibm.ead4j.jade.util.Parameters parameters, JadeSession session) {
        super.load(parameters, session);       
        
        this.setUser(  (User) session.getAttribute( SessionKeys.SESSION_USER ) );
    }
    
    
}
