/*
 * Created on 2007-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteAttachmentProcess_jdbc extends QuoteAttachmentProcess_Impl{
    
    protected boolean saveOrUpdateWebAttechment(QuoteAttachment file) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        this.beginTransaction();
        boolean retFlag = true;
        try{
            HashMap parms = new HashMap();
            parms.put("piWebQuoteNum", file.getQuoteNumber());
            parms.put("piAttchmtSeqNum", file.getId());
            parms.put("piAttchmtFileName", file.getFileName());
            parms.put("piAttchmtFileSize", new Long(file.getFileSize()));
            parms.put("piAttchmtStageCode", file.getStageCode());
            parms.put("piUserID", file.getUploaderEmail());
            parms.put("piJstfctnSectnID", file.getSecId());
            parms.put("piAttchmtClassfctnCode", file.getClassfctnCode());
            parms.put("piCmprssdFileFlag", file.isCmprssdFileFlag() ? new Integer(1) : new Integer(0));
            parms.put("touURL", file.getTouURL());
            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_IU_QT_ATTCHMT, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            queryCtx.completeStatement(ps, CommonDBConstants.DB2_IU_QT_ATTCHMT, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            ps.execute();
            
            int retStatus = ps.getInt(1);
            logContext.debug(this, "saveOrUpdateWebAttechment: " + retStatus);
            this.commitTransaction();
            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                return false;
            }
        }catch(Exception e){
            logContext.error(this, e.getMessage());
            retFlag = false;
        }
        finally
        {
            this.rollbackTransaction();
        }
        return retFlag;
    }
}
