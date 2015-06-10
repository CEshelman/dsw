package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author George
 */
public class SalesCommentAttachmentsContract extends AttachmentsContract {
    
    private String detailComments;
    
    private static LogContext logContext = LogContextFactory.singleton().getLogContext();

    public void load(Parameters parameters, JadeSession session) {
        try {
            super.load(parameters, session);
            detailComments = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_SI_DETAIL_COMMENTS);
        } catch (Exception e) {
        	logContext.error(this, e);            
        }
    }
    
    public String getDetailComments() {
        return detailComments;
    }
}