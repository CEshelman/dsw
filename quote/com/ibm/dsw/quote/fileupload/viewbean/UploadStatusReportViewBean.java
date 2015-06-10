/*
 * Created on 2007-7-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.fileupload.viewbean;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author helenyu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadStatusReportViewBean extends BaseViewBean {
    
    private String uploadUUID;
    
    
    /* (non-Javadoc)
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        uploadUUID = params.getParameterAsString(ParamKeys.PARAM_UPLOAD_UUID);
    }
    
    
    /**
     * @return Returns the uploadUUID.
     */
    public String getUploadUUID() {
        return uploadUUID;
    }
}
