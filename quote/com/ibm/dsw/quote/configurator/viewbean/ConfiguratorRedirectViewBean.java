package com.ibm.dsw.quote.configurator.viewbean;

import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.draftquote.viewbean.RedirectViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class ConfiguratorRedirectViewBean extends RedirectViewBean {
	private boolean cancelConfigrtn;

	public void collectResults(Parameters param) throws ViewBeanException {
		super.collectResults(param);
		cancelConfigrtn = ((Boolean)param
				.getParameter(ConfiguratorParamKeys.CANCEL_CONFIGRTN)).booleanValue();
	}
	
	public boolean isCancelConfigrtn(){
		return cancelConfigrtn;
	}
}
