/**
 * 
 */
package com.ibm.dsw.quote.base.viewbean;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.ead4j.jade.bean.ViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>AjaxOperViewBean<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2011-2-9
 */
public class AjaxOperViewBean extends ViewBean
{

	private static final long serialVersionUID = 5073961066251769605L;
	
	protected String operStatus;
	protected String messKey;

	@Override
	public void collectResults(Parameters parms) throws ViewBeanException
	{
		this.operStatus = parms.getParameterAsString(ParamKeys.PARAM_AJAX_OPER_STATUS);
		this.messKey = parms.getParameterAsString(ParamKeys.PARAM_AJAX_OPER_MESS);
	}

	public String getOperStatus()
	{
		return operStatus;
	}

	public String getMessKey()
	{
		return messKey;
	}
}
