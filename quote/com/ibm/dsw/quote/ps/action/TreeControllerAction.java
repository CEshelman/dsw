
package com.ibm.dsw.quote.ps.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseParameterActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.config.PartSearchStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.reuse.tree.controller.ITreeController;
import com.ibm.reuse.tree.util.StringEncoderAjaxTree;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>TreeControllerAction.java</code> 
 * 
 * @author: jinfahua@cn.ibm.com
 * 
 * Created on: Mar 7, 2007
 */

public class TreeControllerAction extends BaseParameterActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseParameterActionHandler#executeBiz(com.ibm.ead4j.jade.util.Parameters)
     */
    public ResultBean executeBiz(Parameters params) throws QuoteException, ResultBeanException {
        
        //used to inspect customer browse pattern
        logContext.debug(this,"PartSearchTraceLog:TreeControllerAction:"
                +params.getParameterAsString(TreeControllerAdapter.DEFAULT_TREE_PARA_NAME)+":"
                +params.getParameterAsString(TreeControllerAdapter.DEFAULT_NODE_ID_PARA_NAME));
        params.getParameter(ParamKeys.PARAM_QUOTE_NUM);
        encoderParamsForAppScan(params);
        
        ITreeController controller = new TreeControllerAdapter(params);
        String html = controller.execute();
        ResultHandler handler = new ResultHandler(params);
        handler.addObject(PartSearchParamKeys.PARAM_HTML_SOURCE_CODE, html);
        handler.setState(PartSearchStateKeys.STATE_TREE_CONTROLLER);
        return handler.getResultBean();
    }
    
    protected void retrieveBizObjectsFromSession(Parameters params, JadeSession session) {
        Object creatorId = session.getAttribute(SessionKeys.SESSION_USER_ID);
        if (creatorId != null) {
            params.addParameter(SessionKeys.SESSION_USER_ID, creatorId);
        }
    }
    
    private void encoderParamsForAppScan(Parameters params){
    	String currency = params.getParameterAsString("currency");
    	if(StringUtils.isNotBlank(currency)){
    		currency = StringEncoderAjaxTree.textToHTML(currency);
    		params.addParameter("currency", currency);
    	}
    	String quoteNum = params.getParameterAsString("quoteNum");
    	if(StringUtils.isNotBlank(quoteNum)){
    		quoteNum = StringEncoderAjaxTree.textToHTML(quoteNum);
    		params.addParameter("quoteNum", quoteNum);
    	}
    	String country = params.getParameterAsString("country");
    	if(StringUtils.isNotBlank(country)){
    		if(country.length()>3){
    			country = null;
    		}
    		else{
    			country = StringEncoderAjaxTree.textToHTML(country);
        		params.addParameter("country", country);
    		}
    	}
    	String quoteFlag =params.getParameterAsString("quoteFlag");
    	if(StringUtils.isNotBlank(quoteFlag)){
    		quoteFlag = StringEncoderAjaxTree.textToHTML(quoteFlag);
    		params.addParameter("quoteFlag", quoteFlag);
    	}
    }
}
