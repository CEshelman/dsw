/**
 * SPBRuleTestingServiceExport1_SPBRuleTestingServiceHttpBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf190715.04 v41807231423
 */

package com.ibm.dsw.spbid.ruletesting;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.util.ApprovalGroupHelper;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class SPBRuleTestingServiceImpl implements com.ibm.dsw.spbid.ruletesting.SPBRuleTestingService{
    public static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    public com.ibm.dsw.spbid.common.ApprovalGroup[] testRuleWithWebQuoteNum(java.lang.String webQuoteNum) throws java.rmi.RemoteException {
        logContext.info(this, "execute rule test service here: " + webQuoteNum);
        ApprovalGroup[] ret = null;
        try
        {
            logContext.debug(this, "begin to get quote data");
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            boolean isPGSEnv=false;
            Quote quote = process.getQuoteByNumForTest(webQuoteNum,isPGSEnv);
            logContext.debug(this, "call approval group helper to test rule");
            ApprovalGroupHelper helper = new ApprovalGroupHelper();
            List list = helper.getListOfApprover(quote);
            if ( list != null )
            {
                ret = new ApprovalGroup[list.size()];
                for ( int i = 0; i < list.size(); i++ )
                {
                    ret[i] = (ApprovalGroup)list.get(i);
                }
            }
            logContext.debug(this, "end test rule");
        }
        catch ( QuoteException qe)
        {
           logContext.error(this, qe);
        }
        if ( ret == null )
        {
            ret = new ApprovalGroup[0];
        }
        return ret;
    }
}
