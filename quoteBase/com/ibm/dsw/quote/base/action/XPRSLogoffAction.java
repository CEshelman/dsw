package com.ibm.dsw.quote.base.action;

import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.jade.session.JadeSession;

public class XPRSLogoffAction extends BaseContractActionHandler {
    
    public ResultBean execute( ProcessContract contract, JadeSession session, ResultHandler handler ) {
        session.removeValue(SessionKeys.SESSION_USER);
		session.removeValue(SessionKeys.SESSION_USER_ID);
        //session.invalidate();
        handler.setState("STATE_DONE");
        try{
            return handler.getResultBean(); 
        }catch( ResultBeanException rbe ){
            return handler.getUndoResultBean();
        }
    }
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
     ResultBeanException {
         return null;
    }
}
