/*
 * Created on 2007-7-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.fileupload.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.ead4j.jade.action.AbstractActionHandler;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * @author Nathan Wang  wnan@cn.ibm.com
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadStatusReportAction extends AbstractActionHandler{

    /* (non-Javadoc)
     * @see com.ibm.ead4j.jade.action.ActionHandler#execute(com.ibm.ead4j.jade.util.Parameters, com.ibm.ead4j.jade.session.JadeSession)
     */
    public ResultBean execute(Parameters parms, JadeSession jadeSession) {
        ResultHandler handler = new ResultHandler(parms);
        handler.addObject(ParamKeys.PARAM_UPLOAD_UUID,parms.getParameter(ParamKeys.PARAM_UPLOAD_UUID));
        handler.setState(StateKeys.STATE_DISPLAY_UPLOAD_PROGRESS_REPORT);
        ResultBean rb = null;
        try {
             rb = handler.getResultBean();
        } catch (ResultBeanException e) {
            // TODO Auto-generated catch block
            LogContextFactory.singleton().getLogContext().error(this, e, "Error when getResultBean");
        }
        return rb;
    }

}
