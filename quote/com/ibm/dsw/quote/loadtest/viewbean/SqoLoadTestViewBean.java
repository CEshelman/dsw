/**
 * 
 */
package com.ibm.dsw.quote.loadtest.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author julia.liu
 *
 */
public class SqoLoadTestViewBean extends BaseViewBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5121885109990040298L;
	private String content;
	
	 public void collectResults(Parameters parms) throws ViewBeanException {
        super.collectResults(parms);
        content = (String)parms.getParameter("SqoLoadTest");
    }
	 
	public String getJSONString() {
		 return content;
	}

}
