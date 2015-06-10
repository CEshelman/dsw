package com.ibm.dsw.quote.mail.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.mail.process.MailProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;

public class MailProcess_jdbc extends MailProcess_Impl {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();
    
	@Override
	public String createMessage(String quoteNumber, String content,
			String messageType, String receivers) {
		try {
			if (!CacheProcessFactory.singleton().create().isPersistMessages()) {
				return receivers;
			}
		} catch (QuoteException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		}
		logContext.info(this, "persist messages: " + receivers + ";" + content);
		
		String msgReceivers = null;
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
        try{ 
        	String spName = FindQuoteDBConstants.I_QTM_MESSAGE_DTL;
            this.beginTransaction(); 
            // retrieve the quote status
            QuoteHeader quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(quoteNumber);
            String statusStr = "";
            if(quoteHeader != null){
                List overallStatuses = quoteHeader.getQuoteOverallStatuses();
                if (overallStatuses != null && overallStatuses.size() > 0) {
                	StringBuffer sbOS = new StringBuffer();
                    Iterator iterator = overallStatuses.iterator();
                    while(iterator.hasNext()){
                    	CodeDescObj codeDescObj = (CodeDescObj)iterator.next();
                    	String codeDesc = codeDescObj.getCodeDesc();
                    	if (!iterator.hasNext()) {
                        	sbOS.append(codeDesc);
                        } else {
                        	sbOS.append(codeDesc + ", ");
                        }
                    }
                    statusStr = sbOS.toString();
                }
            }

            Date currDate = new Date();
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy",Locale.US);
            String dateStr = sdfDate.format(currDate);
            
            HashMap parms = new HashMap();
            parms.put("piMessage", content);
            parms.put("piWebQuoteNumber", quoteNumber);
            parms.put("piReadFlag", 0);
            parms.put("piMessageType", messageType);
            parms.put("piReceiverIds", receivers);
            
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            boolean psResult = ps.execute();
            
            if (psResult) {
            	rs = ps.getResultSet();
           	 	while (rs.next()) {
           	 		sb.append(",");
           	 		sb.append(StringUtils.trim(rs.getString("RECEIVER")));
           	 	}
           	 	msgReceivers = sb.toString().substring(1);
            }
            
            this.commitTransaction();
        }catch(Exception e){
            logContext.error(this, e.getMessage());
        }
        finally
        {
            this.rollbackTransaction();
        }
        return msgReceivers;
    }
}
